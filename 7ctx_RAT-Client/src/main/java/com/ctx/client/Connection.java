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

import com.ctx.client.configuration.JsonConfiguration;
import com.protocol.packets.codec.PacketCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import java.net.InetSocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Connection 
{
    public static Connection instance;

    /**
     * Connection instance
     *
     * @return instance
     */
    public static Connection getInstance() 
    {
        if (instance == null) 
        {
            instance = new Connection();
        }
        return instance;
    }

    void bootstrap() 
    {
        EventLoopGroup group = new NioEventLoopGroup();
        try 
        {
            Bootstrap clientBootstrap = new Bootstrap();
            clientBootstrap.group(group);
            clientBootstrap.channel(NioSocketChannel.class);
            clientBootstrap.remoteAddress(new InetSocketAddress(JsonConfiguration.getConfiguration().getServerHost(), JsonConfiguration.getConfiguration().getServerPort())); // Define remote connection
            clientBootstrap.handler(new ChannelInitializer<SocketChannel>() 
            {
                /**
                 * initialize SocketChannel
                 *
                 * @param socketChannel
                 * @throws Exception
                 */
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception 
                {
                    socketChannel.pipeline().addLast("lengthFieldEncoder", new LengthFieldPrepender(4));
                    socketChannel.pipeline().addLast("lengthFieldDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                    socketChannel.pipeline().addLast("packetCodec", new PacketCodec());
                    socketChannel.pipeline().addLast(new ChannelHandler());
                }
            }).option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            ChannelFuture channelFuture = clientBootstrap.connect().sync();
             if (channelFuture.isSuccess()) 
            {
                log.info("Client is connected to: {}", channelFuture.channel().remoteAddress());
            }
            channelFuture.channel().closeFuture().sync();
        } 
        catch (InterruptedException ex) 
        {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
