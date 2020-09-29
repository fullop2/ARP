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
	
	public static JButton btnSetting;
	
	public AddressPanel(int x, int y) {

		setBounds(x,y,400,150);
		setBorder(BorderFactory.createTitledBorder("Address"));
		setLayout(null);
		
		lblSelectNic = new JLabel("NIC List");
		lblSelectNic.setBounds(15,15,70,20);
		add(lblSelectNic);

		comboBox = new JComboBox();
		comboBox.setBounds(100,15,280,20);
		add(comboBox);
		
		lblsrcMAC = new JLabel("Mac Address");
		lblsrcMAC.setBounds(15,45,70,20);
		add(lblsrcMAC);

		srcMacAddress = new JTextArea();
		srcMacAddress.setEditable(false);
		srcMacAddress.setBounds(100,45,280,20);
		add(srcMacAddress);

		lblsrcIP = new JLabel("IP Address");
		lblsrcIP.setBounds(15,75,70,20);
		add(lblsrcIP);
		
		srcIPAddress = new JTextField();
		srcIPAddress.setBounds(100,75,280,20);
		add(srcIPAddress);


		btnSetting = new JButton("Setting");// setting
		btnSetting.setBounds(160,105,80,20);
		add(btnSetting);
		
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
