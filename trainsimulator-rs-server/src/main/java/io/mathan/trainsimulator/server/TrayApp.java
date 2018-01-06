/*
 * Copyright 2017 Matthias Hanisch (reallyinsane)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mathan.trainsimulator.server;

import io.mathan.trainsimulator.server.internal.NativeLibraryFactory;
import io.mathan.trainsimulator.server.internal.UdpMulticastServer;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class TrayApp {

  private static String TOOLTIP = "TrainSimulator Controller Server (%s)";
  private static String NOT_RUNNING = "not running";
  private static String RUNNING = "running";

  private final TrayIcon icon;

  private final BufferedImage icoDefault =
      ImageIO.read(getClass().getResource("/railway-station-blue.png"));
  private final BufferedImage icoActive =
      ImageIO.read(getClass().getResource("/railway-station-green.png"));

  private final MenuItem itemStart = new MenuItem("Start");
  private final MenuItem itemStop = new MenuItem("Stop");
  private final MenuItem itemLocation = new MenuItem("Dll Location");
  private final MenuItem itemExit = new MenuItem("Exit");

  private final int trayIconWidth;
  private final Configuration configuration;

  protected void setState(BufferedImage ico, String message) {
    this.icon.setImage(ico.getScaledInstance(this.trayIconWidth, -1, Image.SCALE_SMOOTH));
    this.icon.setToolTip(tooltip(message));
  }

  protected void changeLocation() {
    String location =
        JOptionPane.showInputDialog(
            "Specify path to Railworks.dll", NativeLibraryFactory.getDllLocation());
    NativeLibraryFactory.setDllLocation(location);
  }

  private String tooltip(String message) {
    return String.format(TOOLTIP, message);
  }

  private void start() {
    try {
      String location = NativeLibraryFactory.getDllLocation();
      if (!location.isEmpty() && TrainSimulatorServer.start(this.configuration)) {
        setState(this.icoActive, RUNNING);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  public TrayApp(Configuration configuration) throws IOException, AWTException {
    this.configuration = configuration;
    this.trayIconWidth = new TrayIcon(this.icoDefault).getSize().width;
    this.icon =
        new TrayIcon(
            this.icoDefault.getScaledInstance(this.trayIconWidth, -1, Image.SCALE_SMOOTH),
            tooltip(NOT_RUNNING));
    PopupMenu menu = new PopupMenu();
    menu.add(this.itemStart);
    menu.add(this.itemStop);
    menu.add(this.itemLocation);
    menu.add(this.itemExit);
    this.itemStart.addActionListener((e) -> start());
    this.itemStop.addActionListener((e) -> stop());
    this.itemLocation.addActionListener((e) -> changeLocation());
    this.itemExit.addActionListener((e) -> exit());
    this.icon.setPopupMenu(menu);
    SystemTray.getSystemTray().add(this.icon);
    start();
    new UdpMulticastServer(configuration).start();
  }

  private void exit() {
    SystemTray.getSystemTray().remove(TrayApp.this.icon);
    try {
      TrainSimulatorServer.stop();
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    System.exit(0);
  }

  private void stop() {
    try {
      setState(TrayApp.this.icoDefault, NOT_RUNNING);
      TrainSimulatorServer.stop();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException, AWTException {
    Configuration configuration = new Configuration();
    new TrayApp(configuration);
  }
}
