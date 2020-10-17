package View;

import javax.swing.*;

public class AppView extends JFrame{
	
	public static JButton btnOpenNetwork;
	public static ARPView networkView;
	
	public AppView() {
		this(true);
	}	
	
	public AppView(boolean visible) {

		setTitle("Chatting App");

		setBounds(250, 250, 820, 350);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);
		
		add(new ChatPanel(0, 0));
		add(new AddressPanel(400,0));
		
		btnOpenNetwork = new JButton("ARP Settings");
		btnOpenNetwork.setBounds(405, 235, 390, 60);
		add(btnOpenNetwork);
		
		networkView = new ARPView();

		setVisible(visible);
	}
}
