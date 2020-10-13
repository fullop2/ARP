package View;

import javax.swing.*;

public class AppView extends JFrame{
	
	public static JButton btnOpenNetwork;
	public static NetworkView networkView;
	
	public AppView() {
		this(true);
	}	
	
	public AppView(boolean visible) {

		setTitle("Chatting App");

		setBounds(250, 250, 420, 380);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);
		
		add(new ChatPanel(0, 0));
		
		btnOpenNetwork = new JButton("Networks");
		btnOpenNetwork.setBounds(15, 305, 370, 20);
		add(btnOpenNetwork);
		
		
		
		networkView = new NetworkView();
		
		
		setVisible(visible);
	}
}
