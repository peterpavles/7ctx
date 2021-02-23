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
package com.ctx.server.misc;

import com.protocol.packets.beans.Client;
import com.ctx.server.configuration.Configuration;
import java.time.Duration;
import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Monitor Server uptime
 */
@Slf4j
public class Uptime implements Runnable 
{
    private final Configuration config;

    public Uptime(Configuration config) 
    {
        this.config = config;
    }

    Instant start = Instant.now();
    Timer timer = new Timer();

    @Override
    public void run() 
    {
        timer.scheduleAtFixedRate(new TimerTask() 
        {
            @Override
            public void run() 
            {
                Instant finish = Instant.now();
                long timeElapsed = Duration.between(start, finish).getSeconds();
                int day = (int) TimeUnit.SECONDS.toDays(timeElapsed);
                long hour = TimeUnit.SECONDS.toHours(timeElapsed) - (day * 24);
                long minute = TimeUnit.SECONDS.toMinutes(timeElapsed) - (TimeUnit.SECONDS.toHours(timeElapsed) * 60);
                long second = TimeUnit.SECONDS.toSeconds(timeElapsed) - (TimeUnit.SECONDS.toMinutes(timeElapsed) * 60);
                log.info("Server uptime: {} Days {} Hours {} Minutes {} Seconds", new Object[]{day, hour, minute, second});
                log.info("Connected clients: {}", Client.getClients().size());
            }
        }, config.getUptimeCycle() * 60 * 1000, config.getUptimeCycle() * 60 * 1000); // Every x minutes
    }
}
