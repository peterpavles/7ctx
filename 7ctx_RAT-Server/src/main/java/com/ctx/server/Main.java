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

import com.ctx.server.configuration.Configuration;
import com.ctx.server.ui.UI;
import com.ctx.server.misc.Uptime;
import lombok.extern.slf4j.Slf4j;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.border.FlatBorderPainter;
import org.jvnet.substance.button.ClassicButtonShaper;
import org.jvnet.substance.painter.MatteGradientPainter;
import org.jvnet.substance.skin.RavenSkin;
import org.jvnet.substance.theme.SubstanceEbonyTheme;
import org.jvnet.substance.title.FlatTitlePainter;

@Slf4j
public class Main 
{
    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) 
    {
        java.awt.EventQueue.invokeLater(() -> 
        {
            SubstanceLookAndFeel.setSkin(new RavenSkin());
            SubstanceLookAndFeel.setCurrentTheme(new SubstanceEbonyTheme());
            SubstanceLookAndFeel.setCurrentBorderPainter(new FlatBorderPainter());
            SubstanceLookAndFeel.setCurrentGradientPainter(new MatteGradientPainter());
            SubstanceLookAndFeel.setCurrentTitlePainter(new FlatTitlePainter());
            SubstanceLookAndFeel.setCurrentButtonShaper(new ClassicButtonShaper());
            
            UI ui = new UI();
            ui.setVisible(true);
            ui.toFront();
            ui.requestFocus();
        });
        
        if (!Constants.DATA_DIR.exists()) 
        {
            Constants.DATA_DIR.mkdir();
            log.debug("Created Directory: {}", Constants.DATA_DIR.getAbsolutePath());
        }
        
        Configuration configuration = new Configuration();

        if (System.getProperty("os.name").toLowerCase().contains("linux")) 
        {
            Constants.executor.submit(new ServerLinux(configuration));
        } 
        else 
        {
            Constants.executor.submit(new Server(configuration));
        }
        Constants.executor.submit(new Uptime(configuration));
        log.info(Constants.executor.toString());
    }
}
