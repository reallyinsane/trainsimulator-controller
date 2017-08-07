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
package de.mathan.trainsimulator.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import de.mathan.trainsimulator.client.internal.TrainSimulatorRSClient;

public class TrainSimulatorClientFactory {

  public static TrainSimulatorClient getInstance() throws IOException {
    return getInstance(new Configuration());
  }

  public static TrainSimulatorClient getInstance(Configuration configuration) throws IOException {
    System.out.println("waiting for trainsimulator-controller-server");
    MulticastSocket socket = new MulticastSocket(configuration.getMulticastPort());
    InetAddress group = InetAddress.getByName(configuration.getMulticastHost());
    socket.joinGroup(group);
    byte[] data = new byte["TrainSimulator".length()];
    DatagramPacket packet = new DatagramPacket(data, data.length);
    socket.receive(packet);
    String serverIp = packet.getAddress().getHostAddress();
    configuration.setRestHost(serverIp);
    System.out.println("trainsimulator-controller-server found on " + serverIp);
    socket.close();
    return new DefaultTrainSimulatorClient(new TrainSimulatorRSClient(configuration));
  }
}
