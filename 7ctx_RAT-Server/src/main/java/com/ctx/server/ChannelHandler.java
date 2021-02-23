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
package com.ctx.server;

import com.protocol.packets.beans.Client;
import com.protocol.packets.beans.Message;
import com.ctx.server.ui.UI;
import com.protocol.packets.Packet;
import com.protocol.packets.impl.MessagePacket;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelHandler extends ChannelInboundHandlerAdapter
{
    @Getter
    public static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Getter
    protected static final AtomicInteger connectionCount = new AtomicInteger();

    /**
     * Write message to client
     *
     * @param channelId
     * @param msg
     */
    public static void writeMessage(ChannelId channelId, String msg) 
    {
        Message message = Message.builder()
                .message(msg)
                .build();
        
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.setMessage(message);
        
        for (Channel channel : channelGroup) 
        {
            if (channel.id() == null ? channelId == null : channel.id().equals(channelId)) 
            {
              channel.writeAndFlush(messagePacket);
              return;
            }
        }
        log.warn("Invalid client: {}", channelId);
    }
    
    /**
     * Write packet to client
     *
     * @param channelId
     * @param packet
     */
    public static void writePacket(ChannelId channelId, Packet packet) 
    {

        for (Channel channel : channelGroup) 
        {
            if (channel.id() == null ? channelId == null : channel.id().equals(channelId)) 
            {
                channel.writeAndFlush(packet);
                return;
            }
        }
        log.warn("Invalid client: {}", channelId);
    }

    @Override
    public void channelActive(ChannelHandlerContext chc) throws java.lang.Exception
    {
        super.channelActive(chc);
        channelGroup.add(chc.channel());
        log.info("Client: {} Connected", chc.channel().remoteAddress());
        connectionCount.incrementAndGet();
        log.info("Active connections: {}", connectionCount.get());
    }

    @Override
    public void channelInactive(ChannelHandlerContext chc) throws java.lang.Exception
    {
        super.channelInactive(chc);
        log.info("Client: {} Disconnected", chc.channel().remoteAddress());
        Client.getClients().remove(chc.channel().id());
        connectionCount.decrementAndGet();
        log.info("Active connections: {}", connectionCount.get());
        UI.removeClient(chc.channel().id());
    }

    /**
     * Read from channel, We just read from packets
     *
     * @param chc
     * @param object
     * @throws java.io.IOException
     */
    @Override
    public void channelRead(ChannelHandlerContext chc, Object object) throws IOException
    {
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext chc) throws java.lang.Exception
    {
        super.channelReadComplete(chc);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) 
    {
        if (cause.getMessage().contains("java.io.EOFException")) 
        {
            log.warn(cause.getMessage());
        } 
        else 
        {
            cause.printStackTrace();
            log.error(cause.getLocalizedMessage());
        }
        channelHandlerContext.close();
    }
}
