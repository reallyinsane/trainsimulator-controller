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
