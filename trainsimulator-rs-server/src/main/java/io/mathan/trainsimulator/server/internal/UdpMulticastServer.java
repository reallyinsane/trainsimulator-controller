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
package io.mathan.trainsimulator.server.internal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import io.mathan.trainsimulator.server.Configuration;

public class UdpMulticastServer extends Thread {
  private DatagramSocket socket = null;
  private final InetAddress group;
  private final Configuration configuration;

  public UdpMulticastServer(Configuration configuration) throws IOException {
    this.configuration = configuration;
    this.group = InetAddress.getByName(configuration.getMulticastHost());
    this.socket = new DatagramSocket(configuration.getMulticastPort());
  }

  @Override
  public void run() {
    for (; ; ) {
      try {
        byte[] data = "TrainSimulator".getBytes();
        DatagramPacket packet =
            new DatagramPacket(
                data, data.length, this.group, this.configuration.getMulticastPort());
        this.socket.send(packet);
      } catch (UnknownHostException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        Thread.sleep(5000L);
      } catch (InterruptedException e) {
        closeSocket();
      }
    }
  }

  @Override
  public void interrupt() {
    super.interrupt();
    closeSocket();
  }

  private void closeSocket() {
    if (this.socket != null) {
      this.socket.close();
      this.socket = null;
    }
  }
}
