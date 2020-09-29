package View;

import java.awt.Container;
import java.awt.FileDialog;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class AppView extends JFrame{

	/* Global */
	private static AppView appView;
	static Container contentPane;
	
	/* Chat */
	public static JTextField chattingWrite;
	public static JTextArea chattingArea;
	public static JButton chatSendButton;
	
	
	public AppView() {
		
		appView = this;

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

	public JPanel chatPanel(int x, int y) {
		
		JPanel pane = new JPanel();
		pane.setBounds(x,y,400,300);
		pane.setBorder(BorderFactory.createTitledBorder("Chat"));
		pane.setLayout(null);
		
		chattingArea = new JTextArea();
		chattingArea.setEditable(false);
		chattingArea.setBounds(15,15,370,250);
		pane.add(chattingArea);
		
		
		chattingWrite = new JTextField();
		chattingWrite.setBounds(15,270,295,20);
		pane.add(chattingWrite);
		chattingWrite.setColumns(10);
		
		chatSendButton = new JButton("Send");		
		chatSendButton.setBounds(315,270,70,20);
		pane.add(chatSendButton);
		
		return pane;
	}	
	
	
}
