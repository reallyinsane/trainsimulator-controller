package de.mathan.trainsimulator.server;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

public class TrayApp {
  
  private static String TOOLTIP = "TrainSimulator Controller Server (%s)";
  private static String NOT_RUNNING ="not running";
  private static String RUNNING ="running";
  
  private TrayIcon icon;
  
  private BufferedImage icoDefault = ImageIO.read(getClass().getResource("/railway-station-blue.png"));
  private BufferedImage icoActive = ImageIO.read(getClass().getResource("/railway-station-green.png"));
  
  private MenuItem itemStart = new MenuItem("Start");
  private MenuItem itemStop = new MenuItem("Stop");
  private MenuItem itemLocation = new MenuItem("Dll Location");
  private MenuItem itemExit = new MenuItem("Exit");
  
  private ActionListener actionStart = new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      start();
    }
  };
  private ActionListener actionStop = new ActionListener() {
    
    public void actionPerformed(ActionEvent e) {
      try {
        setState(icoDefault, NOT_RUNNING);
        TrainSimulatorServer.stop();
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
    }
  };
  private ActionListener actionLocation = new ActionListener() {
    
    public void actionPerformed(ActionEvent e) {
      changeLocation();
    }
  };
  private ActionListener actionExit = new ActionListener() {
    
    public void actionPerformed(ActionEvent e) {
      SystemTray.getSystemTray().remove(icon);
      try {
        TrainSimulatorServer.stop();
      } catch (Exception e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      System.exit(0);
    }
  };

  private int trayIconWidth;
  
  private String getLocation() {
    String railworksPath = Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\WOW6432Node\\railsimulator.com\\railworks","install_path");
    if(railworksPath!=null&&!railworksPath.trim().isEmpty()) {
      railworksPath+="\\plugins\\RailDriver.dll";
      if(new File(railworksPath).exists()) {
        return railworksPath;
      }
    }
    return Preferences.userNodeForPackage(TrainSimulatorServer.class).get("location", "");
  }
  
  protected void setState(BufferedImage ico, String message) {
    icon.setImage(ico.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
    icon.setToolTip(tooltip(message));
  }

  protected void changeLocation() {
    String location = JOptionPane.showInputDialog("Specify path to Railworks.dll", getLocation());
    setLocation(location);
  }

  public void setLocation(String location) {
    Preferences.userNodeForPackage(TrainSimulatorServer.class).put("location", location);
  }
  
  private String tooltip(String message) {
    return String.format(TOOLTIP, message);
  }
  
  
  private void start() {
    try {
      String location = getLocation();
      if(!location.isEmpty()&&TrainSimulatorServer.start(location)) {
        setState(icoActive, RUNNING);
      }
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }

  public TrayApp() throws IOException {
    trayIconWidth = new TrayIcon(icoDefault).getSize().width;
    icon= new TrayIcon(icoDefault.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH), tooltip(NOT_RUNNING));
    PopupMenu menu = new PopupMenu();
    menu.add(itemStart);
    menu.add(itemStop);
    menu.add(itemLocation);
    menu.add(itemExit);
    itemStart.addActionListener(actionStart);
    itemStop.addActionListener(actionStop);
    itemLocation.addActionListener(actionLocation);
    itemExit.addActionListener(actionExit);
    icon.setPopupMenu(menu);
    try {
      SystemTray.getSystemTray().add(icon);
      start();
    } catch (AWTException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    new TrayApp();
  }
}
