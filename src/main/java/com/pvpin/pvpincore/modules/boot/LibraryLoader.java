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
package com.pvpin.pvpincore.modules.boot;

import com.pvpin.pvpincore.modules.i18n.I18N;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;
import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.logging.PVPINLoggerFactory;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import sun.misc.Unsafe;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author William_Shi
 */
public class LibraryLoader {
    private static RepositorySystem repository;
    private static DefaultRepositorySystemSession session;
    private static List<RemoteRepository> repositories;

    public static void loadLibraries() {

        try {
            DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
            locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
            locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
            repository = (RepositorySystem) locator.getService(RepositorySystem.class);
            session = MavenRepositorySystemUtils.newSession();
            session.setChecksumPolicy("fail");
            session.setLocalRepositoryManager(repository.newLocalRepositoryManager((RepositorySystemSession) session, new LocalRepository("plugins/PVPINCore/libraries")));
            session.setTransferListener((TransferListener) new AbstractTransferListener() {
                public void transferStarted(TransferEvent event) throws TransferCancelledException {
                    PVPINLoggerFactory.getCoreLogger().info(I18N.translateByDefault("init.dependency.downloading"), event.getResource().getResourceName());
                }
            });
            session.setReadOnly();
            repositories = repository.newResolutionRepositories((RepositorySystemSession) session, Arrays.asList(new RemoteRepository[]{(new RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2")).build()}));

            DependencyResult result;
            List<Dependency> dependencies = new ArrayList<>();
            for (String library : List.of("org.graalvm.sdk:graal-sdk:21.3.1", "org.graalvm.js:js:21.3.1", "org.graalvm.js:js-scriptengine:21.3.1")) {
                DefaultArtifact defaultArtifact = new DefaultArtifact(library);
                Dependency dependency = new Dependency((Artifact) defaultArtifact, null);
                dependencies.add(dependency);
            }
            result = repository.resolveDependencies((RepositorySystemSession) session, new DependencyRequest(new CollectRequest((Dependency) null, dependencies, repositories), null));

            for (ArtifactResult artifact : result.getArtifactResults()) {
                URL url;
                File file = artifact.getArtifact().getFile();
                try {
                    url = file.toURI().toURL();
                } catch (Exception exception) {
                    throw new AssertionError(exception);
                }
                Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafe.setAccessible(true);
                Unsafe unsafe = (Unsafe) theUnsafe.get(null);
                Field module = Class.class.getDeclaredField("module");
                long offset = unsafe.objectFieldOffset(module);
                unsafe.putObject(LibraryLoader.class, offset, Object.class.getModule());

                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(PVPINCore.class.getClassLoader(), url);
            }
        } catch (Exception ex) {
            PVPINLogManager.log(ex);
        }
    }
}
