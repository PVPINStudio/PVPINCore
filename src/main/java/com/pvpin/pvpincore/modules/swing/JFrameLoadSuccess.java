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

import com.pvpin.pvpincore.modules.PVPINCore;
import com.pvpin.pvpincore.modules.logging.PVPINLogManager;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * @author William_Shi
 */
public class JFrameLoadSuccess extends JFrame {

    // Variables declaration - do not modify                     
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JLabel jLabel1;
    private JLabel jLabel2;
    // End of variables declaration                  

    /**
     * Creates new form JFrameLoadSuccess
     */
    public JFrameLoadSuccess() {
        // Created by NetBeansIDE

        try {
            initComponents();

            super.setTitle("PVPINCore Load Success!");
            super.setIconImage(ImageIO.read(PVPINCore.class.getResourceAsStream("/swing/favicon.png")));
            super.setSize(750, 464);// 0.618
            super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     *
     * @throws IOException default code generated by NetBeansIDE
     */
    @SuppressWarnings("unchecked")
    private void initComponents() throws IOException {

        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 3, 48)); // NOI18N
        jLabel1.setText("PVPINCore Load Success !");

        jButton1.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
        jButton1.setText("PVPIN Core Releases");
        jButton1.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton1ActionPerformed(evt);
        });

        jButton2.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
        jButton2.setText("PVPIN Core APIDOCS");
        jButton2.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton2ActionPerformed(evt);
        });

        jButton3.setFont(new java.awt.Font("Times New Roman", 3, 24)); // NOI18N
        jButton3.setText("PVPINCore Source Code");
        jButton3.addActionListener((java.awt.event.ActionEvent evt) -> {
            jButton3ActionPerformed(evt);
        });

        jLabel2.setIcon(new ImageIcon(ImageIO.read(PVPINCore.class.getResourceAsStream("/swing/favicon.png"))));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(60, 60, 60)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jButton3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jButton2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jButton1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .addGap(92, 92, 92)
                                                .addComponent(jLabel2))
                                        .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 558, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(132, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(70, 70, 70)
                                .addComponent(jLabel1)
                                .addGap(70, 70, 70)
                                .addComponent(jButton3)
                                .addGap(30, 30, 30)
                                .addComponent(jButton2)
                                .addGap(30, 30, 30)
                                .addComponent(jButton1)
                                .addContainerGap(77, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(180, 180, 180)
                                .addComponent(jLabel2))
        );

        pack();
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // TODO add your handling code here:
            URI uri = URI.create("https://github.com/PVPINStudio/PVPINCore/releases");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // TODO add your handling code here:
            URI uri = URI.create("https://github.com/PVPINStudio/PVPINCore/Wiki");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
    }

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // TODO add your handling code here:
            URI uri = URI.create("https://github.com/PVPINStudio/PVPINCore");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (IOException ex) {
            PVPINLogManager.log(ex);
        }
    }

}
