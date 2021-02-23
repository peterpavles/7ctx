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
package com.ctx.client.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils 
{
    public static String getCurrentDateString() 
    {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    public static String getCurrentTimeString() 
    {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")); // : character isn't allowed for files in windows
    }

    public static String getCurrentDateTimeString() 
    {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")); // : character isn't allowed for files in windows
    }
    
    /**
     * Get epoch day of a date
     * 
     * @param date
     * @return (Long) epoch day
     */
  public static long getEpochDayOfLocalDate(LocalDate date) 
  {
        //long myEpochDay = getEpochDayOfDate(LocalDate.of(1996, Month.OCTOBER, 1));
        long epochDay = date.toEpochDay();
        return epochDay;
  }
  
    /**
     * Get epoch second of a date
     * 
     * @param date
     * @return (Long) epoch second
     */
    public static long getEpochSecondOfLocalDate(LocalDate date) 
    {
        //long myEpochSecond = getEpochSecondOfDate(LocalDate.of(1996, Month.OCTOBER, 1));
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epochSecond = date.atStartOfDay(zoneId).toEpochSecond();
        return epochSecond;
    }
    
    /**
     * Get local date of epoch second
     * 
     * @param epochSecond
     * @return local date of epoch second
     */
    public static LocalDate getLocalDateOfEpochSecond(long epochSecond) 
    {
        //return LocalDate.ofEpochDay(epochSecond / 86400000L);
        //return new Timestamp(epochSecond).toLocalDateTime().toLocalDate();
        ZoneId zoneId = ZoneId.systemDefault();
        return Instant.ofEpochSecond(epochSecond).atZone(zoneId).toLocalDateTime().toLocalDate();
    }
  
    /**
     * set expiration date
     *
     * @return expired
     */
    /* public static boolean Expired() 
            {
             LocalDate expirationDate = LocalDate.of(2020, Month.APRIL, 12);
             LocalDate currentDate = LocalDate.now();
             System.err.println("expirationDate: " + expirationDate);
             System.out.println("currentDate: " + currentDate);
             return currentDate.equals(expirationDate) || currentDate.isAfter(expirationDate);
      }*/
}
