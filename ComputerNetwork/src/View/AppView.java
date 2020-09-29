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
	
	/* Address */
	public static JLabel lblSelectNic;
	public static JLabel lblsrcMAC;
	public static JLabel lblsrcIP;
	
	public static JComboBox comboBox;
	public static JTextArea srcMacAddress;
	public static JTextField srcIPAddress;
	
	public static JButton btnSetting;
	
	/* ARP Table */
	public static JTextArea ArpTable;
	public static JTextField ArpIP;
	
	public static JButton btnArpDeleteOne;
	public static JButton btnArpDeleteAll;
	public static JButton btnArpSend;
	
	public static JLabel lblArpIP;
	
	
	/* Proxy ARP */
	public static JTextArea proxyArpEntry;
	
	public static JTextField proxyDevice;
	public static JTextField proxyInIP;
	public static JTextField proxyOutMAC;
	
	public static JLabel lblProxyDevice;
	public static JLabel lblProxyInIP;
	public static JLabel lblProxyOutMAC;
	
	public static JButton btnProxyAdd;
	public static JButton btnProxyDelete;
	
	
	/* GARP */ 
	static JLabel lblGARPHWAddr;
	static JTextField GARPMacAddr;
	static JButton btnGARPSend;	
	
	
	public AppView() {
		
		appView = this;

		setTitle("ARP");

		setBounds(250, 250, 1220, 340);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setLayout(null);

		add(arpCachePanel(0,0));
		add(proxyArpPanel(400, 0));
		add(garpPanel(800,0));
		add(addrPanel(800,150));
		setVisible(true);

		SetCombobox();
	}

	private void SetCombobox() {
		List<PcapIf> m_pAdapterList = new ArrayList<PcapIf>();
		StringBuilder errbuf = new StringBuilder();

		int r = Pcap.findAllDevs(m_pAdapterList, errbuf);
		if (r == Pcap.NOT_OK || m_pAdapterList.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf.toString());
			return;
		}
		for (int i = 0; i < m_pAdapterList.size(); i++)
			this.comboBox.addItem(m_pAdapterList.get(i).getDescription());
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
	
	public JPanel addrPanel(int x, int y) {
		JPanel pane = new JPanel();
		pane.setBounds(x,y,400,150);
		pane.setBorder(BorderFactory.createTitledBorder("Address"));
		pane.setLayout(null);
		
		lblSelectNic = new JLabel("NIC List");
		lblSelectNic.setBounds(15,15,70,20);
		pane.add(lblSelectNic);

		comboBox = new JComboBox();
		comboBox.setBounds(100,15,280,20);
		pane.add(comboBox);
		
		lblsrcMAC = new JLabel("Mac Address");
		lblsrcMAC.setBounds(15,45,70,20);
		pane.add(lblsrcMAC);

		srcMacAddress = new JTextArea();
		srcMacAddress.setEditable(false);
		srcMacAddress.setBounds(100,45,280,20);
		pane.add(srcMacAddress);

		lblsrcIP = new JLabel("IP Address");
		lblsrcIP.setBounds(15,75,70,20);
		pane.add(lblsrcIP);
		
		srcIPAddress = new JTextField();
		srcIPAddress.setBounds(100,75,280,20);
		pane.add(srcIPAddress);


		btnSetting = new JButton("Setting");// setting
		btnSetting.setBounds(160,105,80,20);
		pane.add(btnSetting);
		
		return pane;
	}

	public JPanel arpCachePanel(int x, int y) {
		JPanel pane = new JPanel();
		pane.setBounds(x,y,400,300);
		pane.setBorder(BorderFactory.createTitledBorder("ARP Cache"));
		pane.setLayout(null);
		
		ArpTable = new JTextArea();
		ArpTable.setBounds(15,15,370,200);
		ArpTable.setEditable(false);
		pane.add(ArpTable);
		
		btnArpDeleteOne = new JButton("Item Delete");
		btnArpDeleteOne.setBounds(65,220,120,30);
		pane.add(btnArpDeleteOne);
		
		btnArpDeleteAll = new JButton("All Delete");
		btnArpDeleteAll.setBounds(215,220,120,30);
		pane.add(btnArpDeleteAll);
		
		lblArpIP = new JLabel("IP Addr");
		lblArpIP.setBounds(15,260,65,25);
		pane.add(lblArpIP);
		
		ArpIP = new JTextField();
		ArpIP.setBounds(85,260,210,25);
		pane.add(ArpIP);
		
		btnArpSend = new JButton("Send");
		btnArpSend.setBounds(310,260,65,25);
		pane.add(btnArpSend);
		
		return pane;
	}
	
	public JPanel proxyArpPanel(int x, int y ) {
		JPanel pane = new JPanel();
		pane.setBounds(x,y,400,300);
		pane.setBorder(BorderFactory.createTitledBorder("Proxy ARP Entry"));
		pane.setLayout(null);
		
		proxyArpEntry = new JTextArea();
		proxyArpEntry.setBounds(15,15,370,150);
		proxyArpEntry.setEditable(false);
		pane.add(proxyArpEntry);
		
		lblProxyDevice = new JLabel("Device");
		lblProxyDevice.setBounds(15,180,70,20);
		pane.add(lblProxyDevice);
		
		proxyDevice = new JTextField();
		proxyDevice.setBounds(100,180,280,20);
		pane.add(proxyDevice);
	
		
		lblProxyInIP = new JLabel("IP Addr");
		lblProxyInIP.setBounds(15,210,70,20);
		pane.add(lblProxyInIP);
		
		proxyInIP = new JTextField();
		proxyInIP.setBounds(100,210,280,20);
		pane.add(proxyInIP);
		
		
		lblProxyOutMAC = new JLabel("MAC Addr");
		lblProxyOutMAC.setBounds(15,240,70,20);
		pane.add(lblProxyOutMAC);
		
		proxyOutMAC = new JTextField();
		proxyOutMAC.setBounds(100,240,280,20);
		pane.add(proxyOutMAC);
				
		btnProxyAdd = new JButton("Item Delete");
		btnProxyAdd.setBounds(65,270,120,20);
		pane.add(btnProxyAdd);
		
		btnProxyDelete = new JButton("All Delete");
		btnProxyDelete.setBounds(215,270,120,20);
		pane.add(btnProxyDelete);
		
		return pane;
	}
	
	public JPanel garpPanel(int x, int y) {
		JPanel pane = new JPanel();
		pane.setBounds(x,y,400,150);
		pane.setBorder(BorderFactory.createTitledBorder("Gratuitous ARP"));
		pane.setLayout(null);
		
		lblGARPHWAddr = new JLabel("HW Addr");
		lblGARPHWAddr.setBounds(15,45,70,30);
		pane.add(lblGARPHWAddr);

		GARPMacAddr = new JTextField();
		GARPMacAddr.setBounds(100,45,280,30);
		pane.add(GARPMacAddr);
		
		btnGARPSend = new JButton("Send");
		btnGARPSend.setBounds(140,90,120,40);
		pane.add(btnGARPSend);
		
		return pane;
	}
	
}
