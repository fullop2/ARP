package View;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class AddressPanel extends JPanel {

	public static JLabel lblSelectNic;
	public static JLabel lblsrcMAC;
	public static JLabel lblsrcIP;
	
	public static JComboBox comboBox;
	public static JTextArea srcMacAddress;
	public static JTextField srcIPAddress;
	
	public static JButton btnSettingSrcAddress;
	
	public static JLabel lbldstIP;
	public static JTextField dstIPAddress;
	public static JButton btnSettingDstAddress;
	
	
	
	public AddressPanel(int x, int y) {

		setBounds(x,y,400,230);
		setLayout(null);
		
		JPanel srcPanel = new JPanel(null);
		srcPanel.setBounds(0,0,400,135);
		srcPanel.setBorder(BorderFactory.createTitledBorder("Src Address"));
		srcPanel.setLayout(null);
		add(srcPanel);
		
		
		lblSelectNic = new JLabel("NIC List");
		lblSelectNic.setBounds(15,15,70,20);
		srcPanel.add(lblSelectNic);

		comboBox = new JComboBox();
		comboBox.setBounds(100,15,280,20);
		srcPanel.add(comboBox);
		
		lblsrcMAC = new JLabel("Mac Address");
		lblsrcMAC.setBounds(15,45,70,20);
		srcPanel.add(lblsrcMAC);

		srcMacAddress = new JTextArea();
		srcMacAddress.setBounds(100,45,280,20);
		srcPanel.add(srcMacAddress);

		lblsrcIP = new JLabel("IP Address");
		lblsrcIP.setBounds(15,75,70,20);
		srcPanel.add(lblsrcIP);
		
		srcIPAddress = new JTextField();
		srcIPAddress.setBounds(100,75,280,20);
		srcPanel.add(srcIPAddress);


		btnSettingSrcAddress = new JButton("Setting");// setting
		btnSettingSrcAddress.setBounds(160,105,80,20);
		srcPanel.add(btnSettingSrcAddress);
		
		JPanel dstPanel = new JPanel(null);
		dstPanel.setBounds(0,135,400,95);
		dstPanel.setBorder(BorderFactory.createTitledBorder("Dst Address"));
		add(dstPanel);

		lbldstIP = new JLabel("IP Address");
		lbldstIP.setBounds(15,25,70,20);
		dstPanel.add(lbldstIP);
		
		dstIPAddress = new JTextField();
		dstIPAddress.setBounds(100,25,280,20);
		dstPanel.add(dstIPAddress);
		
		btnSettingDstAddress = new JButton("Setting");// setting
		btnSettingDstAddress.setBounds(160,55,80,20);
		dstPanel.add(btnSettingDstAddress);
		
		setCombobox();
	}
	
	private void setCombobox() {
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
}
