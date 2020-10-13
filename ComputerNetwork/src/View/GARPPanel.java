package View;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GARPPanel extends JPanel {
	
	public static JLabel lblGARPHWAddr;
	public static JTextField GARPMacAddr;
	public static JButton btnGARPSend;	
	
	public GARPPanel(int x, int y) {
		setBounds(x,y,400,150);
		setBorder(BorderFactory.createTitledBorder("Gratuitous ARP"));
		setLayout(null);
		
		lblGARPHWAddr = new JLabel("HW Addr");
		lblGARPHWAddr.setBounds(15,45,70,30);
		add(lblGARPHWAddr);

		GARPMacAddr = new JTextField();
		GARPMacAddr.setBounds(100,45,280,30);
		add(GARPMacAddr);
		
		btnGARPSend = new JButton("Send");
		btnGARPSend.setBounds(140,90,120,40);
		add(btnGARPSend);
	}
}
