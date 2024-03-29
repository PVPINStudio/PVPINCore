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
package com.pvpin.pvpincore.modules.swing;

import com.pvpin.pvpincore.modules.logging.PVPINLogManager;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;

/**
 * @author William_Shi
 */
public class JFrameManager {

    /**
     * Display a window
     */
    public static void startShow() {
        if (!Desktop.isDesktopSupported()) {
            return;
            // Do nothing
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
                // Location of Windows look and feel is changed in JDK9
                // So we use Nimbus look and feel
            } catch (ClassNotFoundException
                    | InstantiationException
                    | IllegalAccessException
                    | UnsupportedLookAndFeelException ex) {
                PVPINLogManager.log(ex);
            }
            JFrame successFrame = new JFrameLoadSuccess();
            successFrame.setLocationByPlatform(true);
            successFrame.setVisible(true);
            SwingUtilities.updateComponentTreeUI(successFrame);
        });
    }

}
