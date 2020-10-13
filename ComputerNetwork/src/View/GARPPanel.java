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
		setBounds(x,y,400,70);
		setBorder(BorderFactory.createTitledBorder("Gratuitous ARP"));
		setLayout(null);
		
		lblGARPHWAddr = new JLabel("HW Addr");
		lblGARPHWAddr.setBounds(15,15,70,20);
		add(lblGARPHWAddr);

		GARPMacAddr = new JTextField();
		GARPMacAddr.setBounds(100,15,280,20);
		add(GARPMacAddr);
		
		btnGARPSend = new JButton("Send");
		btnGARPSend.setBounds(140,40,120,20);
		btnGARPSend.setEnabled(false);
		add(btnGARPSend);
	}
}
