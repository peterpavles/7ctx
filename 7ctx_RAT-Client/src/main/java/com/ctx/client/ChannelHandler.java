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
package com.ctx.client;

import com.protocol.packets.beans.Client;
import com.protocol.packets.beans.Message;
import com.ctx.client.configuration.JsonConfiguration;
import com.protocol.packets.Packet;
import com.protocol.packets.impl.ClientPacket;
import com.protocol.packets.impl.ExceptionPacket;
import com.protocol.packets.impl.MessagePacket;
import com.protocol.packets.impl.OSRSPropertiesFilePacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelHandler extends ChannelInboundHandlerAdapter 
{
    public static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelReadComplete(ChannelHandlerContext chc) throws java.lang.Exception
    {
        super.channelReadComplete(chc);
    }

    @Override
    public void channelRead(ChannelHandlerContext chc, Object object) throws java.lang.Exception
    {
    }
    
    /**
     * Write message to server
     *
     * @param msg
     */
    public static void writeMessage(String msg) 
    {
        Message message = Message.builder()
                .message(msg)
                .build();
        
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.setMessage(message);
        channelGroup.writeAndFlush(messagePacket);
    }
    
    /**
     * Write packet to server
     *
     * @param packet
     */
    public static void writePacket(Packet packet) 
    {

      channelGroup.writeAndFlush(packet);
    }
    
    /**
     * Write exceptions to server
     *
     * @param cause
     */
    public static void writeException(Throwable cause) 
    {
        com.protocol.packets.beans.Exception exception = com.protocol.packets.beans.Exception.builder()
                .cause(cause)
                .build();
        
        ExceptionPacket exceptionPacket = new ExceptionPacket();
        exceptionPacket.setException(exception);
        channelGroup.writeAndFlush(exceptionPacket);
    }

    @Override
    public void channelActive(ChannelHandlerContext chc) throws java.lang.Exception 
    {
        super.channelActive(chc);
        log.info("Channel active: {}", chc.channel());
        channelGroup.add(chc.channel());
        
        Client client = Client.builder()
                .username(Constants.username)
                .osName(Constants.osName)
                .osType(Constants.osType)
                .osVersion(Constants.osVersion)
                .javaVersion(Constants.javaVersion)
                .country(Constants.country)
                .language(Constants.language)
                .availableProcessors(Constants.availableProcessors)
                .totalPsychicalMemory(Constants.totalPsychicalMemory)
                .diskSpace(Constants.diskSpace)
                .build();
        
        ClientPacket clientPacket = new ClientPacket();
        clientPacket.setClient(client);
        
        chc.writeAndFlush(clientPacket);
        writeMessage("Connect");
        
        // Transfer osrs properties files to server
        OSRSPropertiesFilePacket osrsPropsPacket = new OSRSPropertiesFilePacket();
        writePacket(osrsPropsPacket);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws java.lang.Exception
    {
        super.channelInactive(ctx);
        log.info("Disconnected!");
    }

    @Override
    public void channelUnregistered(final ChannelHandlerContext chc) throws java.lang.Exception
    {
        log.info("Sleeping for: {}{}", JsonConfiguration.getConfiguration().getReconnectDelay(), 's');

        chc.channel().eventLoop().schedule(() -> 
        {
            log.info("Reconnecting to: {}{}{}", JsonConfiguration.getConfiguration().getServerHost(), ':', JsonConfiguration.getConfiguration().getServerPort());
            Connection.getInstance().bootstrap();
        }, JsonConfiguration.getConfiguration().getReconnectDelay(), TimeUnit.SECONDS);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) 
    {
        log.error("Exception: {}", cause.getMessage(), cause.getCause());
        writeException(cause);
        cause.printStackTrace();
        channelHandlerContext.close();
    }
}
