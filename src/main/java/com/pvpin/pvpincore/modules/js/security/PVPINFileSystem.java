/*
 * The MIT License
 * Copyright © 2020-2021 PVPINStudio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pvpin.pvpincore.modules.js.security;

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import org.graalvm.polyglot.io.FileSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.*;

/**
 * @author William_Shi
 */
public class PVPINFileSystem implements FileSystem {
    private static final File FOLDER_PERMITTED_JS =
            new File(PVPINCore.getCoreInstance().getDataFolder(), "js");
    private static final File FOLDER_PERMITTED_DATA =
            new File(PVPINCore.getCoreInstance().getDataFolder(), "data");
    private final Path dummyPath = Paths.get(new File(FOLDER_PERMITTED_JS, "api.js").getPath()).toAbsolutePath();
    private final String moduleBody = new String(PVPINCore.class.getResource("/api.js").openStream().readAllBytes());
    private final String expectedPath = "PVPINScriptManager";

    protected final Set<String> uriSpecifiers;
    protected final Set<String> stringSpecifiers;
    private final List<String> paths;

    static {
        try {
            PVPINCore.getCoreInstance().saveResource("api.js", false);
            Files.move(
                    Paths.get(new File(PVPINCore.getCoreInstance().getDataFolder(), "api.js").toURI()),
                    Paths.get(new File(FOLDER_PERMITTED_JS, "api.js").toURI()),
                    StandardCopyOption.ATOMIC_MOVE
            );
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
    }

    public PVPINFileSystem() throws Exception {
        this.uriSpecifiers = new HashSet<>();
        this.stringSpecifiers = new HashSet<>();
        this.paths = new LinkedList<>();
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) {
        Map<String, Object> attr = new HashMap<>();
        // for testing purposes, we consider all files non-regular. In this way, we force the
        // module loader to try all possible file names before throwing module not found
        attr.put("isRegularFile", false);
        return attr;
    }

    @Override
    public Path parsePath(URI uri) {
        System.out.println(uri);
        paths.add(uri.toString());
        uriSpecifiers.add(uri.toString());
        if (expectedPath.equals(uri.toString())) {
            return dummyPath;
        } else {
            return Paths.get(uri);
        }
    }

    @Override
    public Path parsePath(String path) {
        paths.add(path);
        stringSpecifiers.add(path);
        if (expectedPath.equals(path)) {
            return dummyPath;
        } else {
            if (Paths.get(path).toFile().exists()) {
                return Paths.get(path);
            } else {
                return FileSystems.getDefault().getPath(FOLDER_PERMITTED_JS.getAbsolutePath(), path);
            }
        }
    }

    @Override
    public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) {
        File current = path.toFile();
        String absolute = current.getAbsolutePath();
        if (absolute.startsWith(FOLDER_PERMITTED_JS.getAbsolutePath())
                || absolute.startsWith(FOLDER_PERMITTED_DATA.getAbsolutePath())) {
            return;
        }
        throw new RuntimeException(I18N.translateByDefault("js.access"));
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) {
        throw new AssertionError();
    }

    @Override
    public void delete(Path path) {
        throw new AssertionError();
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        if (dummyPath.equals(path)) {
            return new ReadOnlySeekableByteArrayChannel(moduleBody.getBytes(StandardCharsets.UTF_8));
        } else {
            return Files.newByteChannel(path, options, attrs);
        }
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) {
        throw new AssertionError();
    }

    @Override
    public Path toAbsolutePath(Path path) {
        return path.toAbsolutePath();
    }

    @Override
    public Path toRealPath(Path path, LinkOption... linkOptions) {
        return path;
    }

}

class ReadOnlySeekableByteArrayChannel implements SeekableByteChannel {
    private byte[] data;
    private int position;
    private boolean closed;

    public ReadOnlySeekableByteArrayChannel(byte[] data) {
        this.data = data;
    }

    @Override
    public long position() {
        return position;
    }

    @Override
    public SeekableByteChannel position(long newPosition) throws IOException {
        ensureOpen();
        position = (int) Math.max(0, Math.min(newPosition, size()));
        return this;
    }

    @Override
    public long size() {
        return data.length;
    }

    @Override
    public int read(ByteBuffer buf) throws IOException {
        ensureOpen();
        int remaining = (int) size() - position;
        if (remaining <= 0) {
            return -1;
        }
        int readBytes = buf.remaining();
        if (readBytes > remaining) {
            readBytes = remaining;
        }
        buf.put(data, position, readBytes);
        position += readBytes;
        return readBytes;
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean isOpen() {
        return !closed;
    }

    @Override
    public int write(ByteBuffer b) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public SeekableByteChannel truncate(long newSize) {
        throw new UnsupportedOperationException();
    }

    private void ensureOpen() throws ClosedChannelException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }
    }
}