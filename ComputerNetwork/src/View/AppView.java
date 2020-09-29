package View;

import java.awt.Container;
import java.awt.FileDialog;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class AppView extends JFrame{
	public AppView() {

		setTitle("ARP");

		setBounds(250, 250, 1220, 340);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);

		add(new ARPCachePanel(0,0));
		add(new ProxyARPPanel(400, 0));
		add(new GARPPanel(800,0));
		add(new AddressPanel(800,150));
		
		setVisible(true);

	}	
}
