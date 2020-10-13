package View;

import javax.swing.*;

public class AppView extends JFrame{
	public AppView() {
		this(true);
	}	
	
	public AppView(boolean visible) {

		setTitle("ARP");

		setBounds(250, 250, 820, 645);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);

		add(new ARPCachePanel(0,0));
		add(new ProxyARPPanel(400, 0));
		add(new ChatPanel(0, 300));
		add(new GARPPanel(400,300));
		add(new AddressPanel(400,370));
		
		setVisible(visible);
	}
}
