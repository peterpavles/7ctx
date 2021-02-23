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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RestartPCPacket implements Packet
{
    @Override
    public int getOpcode() 
    {
        return Packets.RESTART_PC_PACKET.getCode();
    }

    @Override
    public String getDescription() 
    {
        return Packets.RESTART_PC_PACKET.getDescription();
    }
    
    @Override
    public PacketType getPacketType() 
    {
        return Packets.RESTART_PC_PACKET.getPacketType();
    }

    @Override
    public void read(ChannelHandlerContext chc, ByteBuf in) 
    {
         try 
        {
            Runtime cmd = Runtime.getRuntime();

            ChannelHandler.writeMessage("Restarting computer..");

            if (System.getProperty("os.name").toLowerCase().equalsIgnoreCase("windows")) 
            {
                cmd.exec("shutdown -r");
            } 
            else 
            {
                cmd.exec("sudo reboot");
            }
        } 
        catch (IOException ex) 
        {
            ChannelHandler.writeException(ex);
            Logger.getLogger(RestartPCPacket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void write(ChannelHandlerContext chc, ByteBuf out) 
    {
    }
    
}
