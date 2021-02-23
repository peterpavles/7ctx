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
package com.ctx.server.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Handle server configuration
 */
@Slf4j
public final class Configuration 
{
    @Getter
    private final File configurationFile = new File("./config.properties");

    @Getter
    private int serverPort;

    @Getter
    private int uptimeCycle;

    // Configuration constructor
    public Configuration() 
    {
        if (!configurationFile.exists()) 
        {
            this.createDefaultPropertiesFile();
            log.debug("Created configuration file: {}", configurationFile.getAbsolutePath());
        }
        this.loadProperties();
    }

    /**
     * Creates default properties file
     */
    private void createDefaultPropertiesFile() 
    {
        try (OutputStream output = new FileOutputStream(configurationFile)) 
        {
            Properties properties = new Properties();

            // set the default values
            properties.setProperty("server.port", "1166");
            properties.setProperty("server.uptime_cycle", "20");
            properties.store(output, null);

            log.info("Default properties: {}", properties.toString());
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Load properties
     */
    private void loadProperties() 
    {
        try (InputStream input = new FileInputStream(configurationFile)) 
        {
            Properties properties = new Properties();

            // load properties file
            properties.load(input);

            this.serverPort = Integer.parseInt(properties.getProperty("server.port"));
            this.uptimeCycle = Integer.parseInt(properties.getProperty("server.uptime_cycle"));
            log.info("Loaded properties: {}", properties.entrySet());
            input.close();
        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
