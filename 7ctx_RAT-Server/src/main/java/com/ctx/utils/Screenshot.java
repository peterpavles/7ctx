package com.ctx.utils;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Timed screenshot
 */
public class Screenshot implements Runnable
{
    Rectangle rectangle;
    public Screenshot(Rectangle rectangle) 
    {
        this.rectangle = rectangle;
    }
    
    public void takeScreenshot() 
    {
        long startTime = System.currentTimeMillis();
        
        while (elapsedTime(startTime) < 5) 
        {
            System.out.println("waiting to take screenshot");
        }
        
        try 
        {
            Robot robot = new Robot();
            //Rectangle screenRectangle = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage image = robot.createScreenCapture(rectangle);
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            ImageIO.write(image, "jpg", baos);
            byte[] fileContent = baos.toByteArray();
            Path savePath = new File("/home/t7emon/Desktop/screenshot123.jpg").toPath();
            Files.write(savePath, fileContent);
            System.out.println("Saved Screenshot in: " + savePath.toString());
        } 
        catch (AWTException | IOException ex) 
        {
            Logger.getLogger(Screenshot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Elapsed time in seconds
     *
     * @param start
     * @return elapsed time in seconds
     */
    public static long elapsedTime(long start) 
    {
        //long startTime = System.currentTimeMillis();
        long now = System.currentTimeMillis();
        return (now - start) / 1000;
    }

    @Override
    public void run() 
    {
        takeScreenshot();
    }
}
