package View;

import javax.swing.JFrame;

public class ARPView extends JFrame {
	public ARPView() {
		setTitle("Network View");
		setResizable(false);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLayout(null);
		
		setBounds(250, 250, 815, 415);
		add(new ARPCachePanel(0,0));
		add(new ProxyARPPanel(400, 0));
		add(new GARPPanel(400,300));
	}
}
