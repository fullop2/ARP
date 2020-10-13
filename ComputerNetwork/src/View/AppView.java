package View;

import javax.swing.*;

public class AppView extends JFrame{
	public AppView() {
		this(true);
	}	
	
	public AppView(boolean visible) {

		setTitle("ARP");

		setBounds(250, 250, 1220, 345);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);

		add(new ARPCachePanel(0,0));
		add(new ProxyARPPanel(400, 0));
		add(new GARPPanel(800,0));
		add(new AddressPanel(800,70));
		
		setVisible(visible);
	}
}
