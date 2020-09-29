package View;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatPanel extends JPanel {

	public static JTextField chattingWrite;
	public static JTextArea chattingArea;
	public static JButton chatSendButton;
	
	public ChatPanel(int x, int y) {
		setBounds(x,y,400,300);
		setBorder(BorderFactory.createTitledBorder("Chat"));
		setLayout(null);
		
		chattingArea = new JTextArea();
		chattingArea.setEditable(false);
		chattingArea.setBounds(15,15,370,250);
		add(chattingArea);
		
		
		chattingWrite = new JTextField();
		chattingWrite.setBounds(15,270,295,20);
		add(chattingWrite);
		chattingWrite.setColumns(10);
		
		chatSendButton = new JButton("Send");		
		chatSendButton.setBounds(315,270,70,20);
		add(chatSendButton);
	}
}
