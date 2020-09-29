package View;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ProxyARPPanel extends JPanel {
	public static JTextArea proxyArpEntry;
	
	public static JTextField proxyDevice;
	public static JTextField proxyInIP;
	public static JTextField proxyOutMAC;
	
	public static JLabel lblProxyDevice;
	public static JLabel lblProxyInIP;
	public static JLabel lblProxyOutMAC;
	
	public static JButton btnProxyAdd;
	public static JButton btnProxyDelete;
	
	public ProxyARPPanel(int x, int y ) {
		setBounds(x,y,400,300);
		setBorder(BorderFactory.createTitledBorder("Proxy ARP Entry"));
		setLayout(null);
		
		proxyArpEntry = new JTextArea();
		proxyArpEntry.setBounds(15,15,370,150);
		proxyArpEntry.setEditable(false);
		add(proxyArpEntry);
		
		lblProxyDevice = new JLabel("Device");
		lblProxyDevice.setBounds(15,180,70,20);
		add(lblProxyDevice);
		
		proxyDevice = new JTextField();
		proxyDevice.setBounds(100,180,280,20);
		add(proxyDevice);
	
		
		lblProxyInIP = new JLabel("IP Addr");
		lblProxyInIP.setBounds(15,210,70,20);
		add(lblProxyInIP);
		
		proxyInIP = new JTextField();
		proxyInIP.setBounds(100,210,280,20);
		add(proxyInIP);
		
		
		lblProxyOutMAC = new JLabel("MAC Addr");
		lblProxyOutMAC.setBounds(15,240,70,20);
		add(lblProxyOutMAC);
		
		proxyOutMAC = new JTextField();
		proxyOutMAC.setBounds(100,240,280,20);
		add(proxyOutMAC);
				
		btnProxyAdd = new JButton("Item Delete");
		btnProxyAdd.setBounds(65,270,120,20);
		add(btnProxyAdd);
		
		btnProxyDelete = new JButton("All Delete");
		btnProxyDelete.setBounds(215,270,120,20);
		add(btnProxyDelete);
	}
}
