package View;

import java.awt.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ARPCachePanel extends JPanel {
	
	public static List ArpTable;
	public static JTextField ArpIP;
	
	public static JButton btnArpDeleteOne;
	public static JButton btnArpDeleteAll;
	public static JButton btnArpSend;
	
	public static JLabel lblArpIP;
	
	public ARPCachePanel(int x, int y) {
		setBounds(x,y,400,370);
		setBorder(BorderFactory.createTitledBorder("ARP Cache"));
		setLayout(null);
		
		ArpTable = new List();
		ArpTable.setBounds(15,15,370,250);
		add(ArpTable);
		
		btnArpDeleteOne = new JButton("Item Delete");
		btnArpDeleteOne.setBounds(65,270,120,30);
		add(btnArpDeleteOne);
		
		btnArpDeleteAll = new JButton("All Delete");
		btnArpDeleteAll.setBounds(215,270,120,30);
		add(btnArpDeleteAll);
		
		lblArpIP = new JLabel("IP Addr");
		lblArpIP.setBounds(15,330,65,25);
		add(lblArpIP);
		
		ArpIP = new JTextField();
		ArpIP.setBounds(85,330,210,25);
		add(ArpIP);
		
		btnArpSend = new JButton("Send");
		btnArpSend.setBounds(310,330,65,25);
		btnArpSend.setEnabled(false);
		add(btnArpSend);
	}
}
