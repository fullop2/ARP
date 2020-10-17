package View;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

public class FilePanel extends JPanel {
	

	public JTextArea fileText;
	
	public JButton File_Button;
	public JButton File_Send_Button;
	
	public JProgressBar progressBar;
	
	public FilePanel(int x, int y) {
		setBorder(BorderFactory.createTitledBorder("File"));
		setBounds(x,y,400,150);
		setLayout(null);
		
		fileText = new JTextArea();
		fileText.setBounds(20, 20, 249, 20);
		fileText.setEditable(false);
		add(fileText);
		
		File_Button = new JButton();
		File_Button.setText("파일...");
		File_Button.setBounds(270, 20, 80, 20);
		add(File_Button);
		
		File_Button.addActionListener(new ActionListener() {
			JFileChooser fileChooser = new JFileChooser();
			@Override
			public void actionPerformed(ActionEvent e) {
				int isOK = fileChooser.showOpenDialog(null);
				if(isOK == JFileChooser.APPROVE_OPTION) {
					fileText.setText(fileChooser.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		progressBar = new JProgressBar();
		progressBar.setBounds(20, 50, 249, 20);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		add(progressBar);
		
		File_Send_Button = new JButton();
		File_Send_Button.setText("전송");
		File_Send_Button.setBounds(270, 50, 80, 20);
		add(File_Send_Button);
		
		File_Send_Button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
//				if (Setting_Button.getText() == "Reset") {
//					if (!fileText.getText().equals("")) { 
//						new Thread() {
//							@Override
//							public void run() {
//								setEnableFileButton(false);
//								((FileAppLayer)m_LayerMgr.GetLayer("FileApp")).setAndStartSendFile();
//								fileText.setText(""); 
//								progressBar.setValue(0);
//								setEnableFileButton(true);
//							}
//						}.start();
//						
//					} else {
//						JOptionPane.showMessageDialog(null, "파일이 지정되지 않았습니다");
//					}
//				}
//				else {
//					JOptionPane.showMessageDialog(null, "Address Setting Error!.");//주소설정 에러
//				}
			}
			
		});
	}
}
