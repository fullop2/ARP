package View;

import javax.swing.JFrame;

public class NetworkView extends JFrame {
	public NetworkView() {
		setTitle("Network View");
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLayout(null);
		
		setBounds(250, 250, 1220, 345);
		add(new ARPCachePanel(0,0));
		add(new ProxyARPPanel(400, 0));
		add(new AddressPanel(800,70));
		add(new GARPPanel(800,00));
		
		
		
		
	}
}
