package com.freeplc.editor.tools;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Enumeration;

import com.freeplc.editor.info.PLCConfiguration;

public class PLCDiscover extends Thread {

	PLCDiscoverWindow parentWindow;
	public PLCDiscover(PLCDiscoverWindow parentWindow) {
		this.parentWindow = parentWindow;
	}
	
	public void run() {		
		try {
			startDiscovery();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateInterface(String status) {
		parentWindow.updateStatus(status);
	}
	
	public void startDiscovery() throws Exception {
        DatagramSocket c = new DatagramSocket();
        c.setBroadcast(true);
        c.setSoTimeout(5000);

        byte[] sendData = "FREEPLC_DISCOVER_REQUEST".getBytes();

        //Try the 255.255.255.255 first
        try {
          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
          c.send(sendPacket);
          System.out.println(">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
        } catch (Exception e) {}
        
        updateInterface("Enviando requests");

        // Broadcast the message over all the network interfaces
        Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
          NetworkInterface networkInterface = (NetworkInterface)interfaces.nextElement();

          if (networkInterface.isLoopback() || !networkInterface.isUp()) {
            continue; // Don't want to broadcast to the loopback interface
          }

          for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress broadcast = interfaceAddress.getBroadcast();
            if (broadcast == null) {
              continue;
            }

            // Send the broadcast package!
            try {
              DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
              c.send(sendPacket);
            } catch (Exception e) {
            }

            System.out.println(">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
          }
        }

        System.out.println(">>> Done looping over all network interfaces. Now waiting for a reply!");

        byte[] recvBuf = new byte[256];

        updateInterface("Aguardando PLCs");
        while (true) {
	        //Wait for a response
	        Arrays.fill(recvBuf, (byte)0);
	        DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
	        
	        try {
	        	c.receive(receivePacket);
	        } catch (SocketTimeoutException se) {
	        	c.close();
	        	break;
	        }
	
	        //We have a response
	        System.out.println(">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());
	
	        //Check if the message is correct
	        String message = new String(receivePacket.getData()).trim();
	        System.out.println("Received: " + message);
	        if (message.startsWith("FREEPLC_DISCOVER_RESPONSE")) {
	        	
	        	String[] tokens = message.split(",");
	        	PLCConfiguration config = new PLCConfiguration();
	        	config.setType(tokens[1]);
	        	config.setOs(tokens[2]);
	        	config.setVersion(tokens[3]);
	        	config.setAddress(receivePacket.getAddress().getHostAddress());
	        	config.setPort(Integer.parseInt(tokens[4]));
	        	config.setNumberOfDigitalInputPorts(Integer.parseInt(tokens[5]));
	        	config.setNumberOfDigitalOutputPorts(Integer.parseInt(tokens[6]));
	        	config.setNumberOfAnalogInputPorts(Integer.parseInt(tokens[7]));
	        	config.setNumberOfAnalogOutputPorts(Integer.parseInt(tokens[8]));
	        	parentWindow.addPLCToTable(config);
	        	System.out.println("Achou um PLC : " + message);
	        }
        }
        updateInterface("Discovery Terminado");
        
        parentWindow.setDiscoveryFinished();

	}
}
