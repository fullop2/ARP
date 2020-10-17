package View;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatPanel extends JPanel {

	public static JTextField chattingWrite;
	public static JTextArea chattingArea;
	public static JButton btnChatSend;
	
	public ChatPanel(int x, int y) {
		setBounds(x,y,400,300);
		setBorder(BorderFactory.createTitledBorder("Chat"));
		setLayout(null);
		
		chattingArea = new JTextArea();
		chattingArea.setEditable(false);
				
		add(chattingArea);
		
		JScrollPane scrollPane = new JScrollPane(chattingArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(15,15,370,250);
		scrollPane.setVisible(true);
		add(scrollPane);
		
		chattingWrite = new JTextField();
		chattingWrite.setBounds(15,270,295,20);
		add(chattingWrite);
		chattingWrite.setColumns(10);
		
		btnChatSend = new JButton("Send");		
		btnChatSend.setBounds(315,270,70,20);
		add(btnChatSend);
	}
}
