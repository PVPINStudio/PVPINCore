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
package com.pvpin.pvpincore.modules.js;

import org.bukkit.inventory.meta.BookMeta;

import java.util.UUID;

/**
 * @author William_Shi
 */
public class BookJSPlugin extends StringJSPlugin {
    private BookMeta meta;

    public BookJSPlugin(UUID player, BookMeta src) {
        super(player, getContents(src));
        this.meta = src;
    }

    private static String getContents(BookMeta meta) {
        StringBuilder sb = new StringBuilder();
        meta.getPages().forEach(sb::append);
        String str =  sb.toString();
        if(str.isBlank()||str.isEmpty()){
            throw new RuntimeException("Invalid JavaScript Code.");
        }
        return str;
    }
}
