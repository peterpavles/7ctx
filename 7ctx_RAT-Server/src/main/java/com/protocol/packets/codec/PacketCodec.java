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
package com.protocol.packets.codec;

import com.google.common.collect.ImmutableSet;
import com.protocol.packets.Packet;
import com.protocol.packets.PacketType;
import com.protocol.packets.Packets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PacketCodec extends ByteToMessageCodec<Packet>
{
    Set<Integer> BLOCKED_OPCODES = ImmutableSet.of(3, 4, 5, 6 ,7, 8, 10, 12, 14);
    
    @Override
    public void encode(ChannelHandlerContext chc, Packet packet, ByteBuf out) throws Exception 
    {
        // Start time
        Instant startTime = Instant.now();
        
        int opcode = packet.getOpcode();
        out.writeInt(opcode);
        packet.write(chc, out);
        
        Instant endTime = Instant.now();
        long totalTime = Duration.between(startTime, endTime).toMillis();
        log.debug("Packet encode completed: {} in {} ms", new Object[]{packet, totalTime});
    }

    @Override
    public void decode(ChannelHandlerContext chc, ByteBuf in, List<Object> out) throws Exception 
    {
        if (in.readableBytes() < 1) 
        {
            return;
        }
        
        log.debug("Bytes read: " + in.readableBytes());
        
        Instant startTime = Instant.now();
        
        int opcode = in.readInt();

        if (Packets.valueOf(opcode) == null) 
        {
            log.error("Invalid opcode received: {}", opcode);
            chc.close();
            return;
        }
        
        // Blocked opcodes to prevent arbitage code execution on the server
        if (BLOCKED_OPCODES.contains(opcode)) 
        {
            log.warn("Blocked opcode received: {}", opcode);
            if (Packets.valueOf(opcode) != null) 
            {
               Packets p = Packets.valueOf(opcode);
               log.warn("Packet {} was blocked!", p.toString());
            }
            chc.close();
            return;
        }

        Packets p = Packets.valueOf(opcode);
        Packet packet = p.getPacket();
        
        String description = packet.getDescription();
        PacketType packetType = packet.getPacketType();
        
        log.debug("Packet received: name = {} | opcode = {} | description = {} | packetType.{} | packet = {}", p, opcode, description, packetType, packet);
        
        packet.read(chc, in);
        
        // Log time read took
        Instant endTime = Instant.now();
        long totalTime = Duration.between(startTime, endTime).toMillis();
        log.debug("Packet decode completed: {} in {} ms", new Object[]{packet, totalTime});
    }

}
