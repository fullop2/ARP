package chat_file;

import java.awt.Container;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.*;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

public class ChatFileDlg extends JFrame implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	String path;

	private static LayerManager m_LayerMgr = new LayerManager();
	int selected_index;
	private JTextField ChattingWrite;
	private JTextField FileDir_path;

	Container contentPane;

	JTextArea ChattingArea;
	JTextArea srcMacAddress;
	JTextArea dstMacAddress;

	JLabel lblSelectNic;
	JLabel lblsrc;
	JLabel lbldst;

	JButton Setting_Button;
	JButton File_select_Button;
	JButton Chat_send_Button;
	JButton NIC_select_Button;
	JButton File_send_Button;

	JComboBox comboBox;

	FileDialog fd;

	public static void main(String[] args) {
		m_LayerMgr.AddLayer(new NILayer("NI"));
		
		m_LayerMgr.AddLayer(new ChatFileDlg("GUI"));

		//m_LayerMgr.ConnectLayers( );
	}

	public ChatFileDlg(String pName) {
		
		pLayerName = pName;
		setTitle("Chatting & File Transfer");

		setBounds(250, 250, 580, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = this.getContentPane();
		JPanel pane = new JPanel();

		pane.setLayout(null);
		contentPane.add(pane);

		ChattingArea = new JTextArea();
		ChattingArea.setEditable(false);
		ChattingArea.setBounds(12, 13, 359, 226);
		pane.add(ChattingArea);// 채팅

		srcMacAddress = new JTextArea();
		srcMacAddress.setEditable(false);
		srcMacAddress.setBounds(383, 123, 170, 24);
		pane.add(srcMacAddress);// 보내는 주소

		dstMacAddress = new JTextArea();
		dstMacAddress.setBounds(383, 182, 170, 24);
		pane.add(dstMacAddress);// 받는 사람 주소

		ChattingWrite = new JTextField();
		ChattingWrite.setBounds(12, 249, 359, 20);// 249
		pane.add(ChattingWrite);
		ChattingWrite.setColumns(10);// 채팅 쓰는 곳

		FileDir_path = new JTextField();
		FileDir_path.setEditable(false);
		FileDir_path.setBounds(12, 280, 532, 20); // 280
		pane.add(FileDir_path);
		FileDir_path.setColumns(10);// file 경로

		lblSelectNic = new JLabel("NIC List");
		lblSelectNic.setBounds(383, 13, 170, 20);
		pane.add(lblSelectNic);// 글자

		lblsrc = new JLabel("Source Mac Address");
		lblsrc.setBounds(383, 98, 170, 20);
		pane.add(lblsrc);

		lbldst = new JLabel("Destination Mac Address");
		lbldst.setBounds(383, 157, 170, 20);
		pane.add(lbldst);

		Setting_Button = new JButton("Setting");// setting
		Setting_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (Setting_Button.getText() == "Reset") {
					srcMacAddress.setText("");
					dstMacAddress.setText("");
					Setting_Button.setText("Setting");
					dstMacAddress.setEditable(true);
				} else {
					byte[] srcAddress = new byte[6];
					byte[] dstAddress = new byte[6];

					String src = srcMacAddress.getText();
					String dst = dstMacAddress.getText();

					String[] byte_src = src.split("-");
					for (int i = 0; i < 6; i++) {
						srcAddress[i] = (byte) Integer.parseInt(byte_src[i], 16);
					}

					String[] byte_dst = dst.split("-");
					for (int i = 0; i < 6; i++) {
						dstAddress[i] = (byte) Integer.parseInt(byte_dst[i], 16);
					}

//					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetSrcAddress(srcAddress);
//					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetDstAddress(dstAddress);

					((NILayer) m_LayerMgr.GetLayer("NI")).SetAdapterNumber(selected_index);

					Setting_Button.setText("Reset");
					dstMacAddress.setEditable(false);
				}

			}
		});
		Setting_Button.setBounds(418, 218, 87, 20);
		pane.add(Setting_Button);// setting

		File_select_Button = new JButton("File select");// 파일 선택
		File_select_Button.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (Setting_Button.getText() == "Reset") {

					fd = new FileDialog(ChatFileDlg.this, "파일선택", FileDialog.LOAD);
					fd.setVisible(true);

					if (fd.getFile() != null) {
						path = fd.getDirectory() + fd.getFile();
						FileDir_path.setText("" + path);
					}
				} else {
					JOptionPane.showMessageDialog(null, "주소 설정 오류", "WARNING_MESSAGE", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		File_select_Button.setBounds(75, 311, 161, 21);// 파일 선택위치 280
		pane.add(File_select_Button);

		Chat_send_Button = new JButton("Send");
		Chat_send_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Setting_Button.getText() == "Reset") {
					String input = ChattingWrite.getText();

					ChattingArea.append("[SEND] : " + input + "\n");

					byte[] type = new byte[2];
					type[0] = 0x08;
					type[1] = 0x20;
					//((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetType(type);

					byte[] bytes = input.getBytes();
					m_LayerMgr.GetLayer("Chat").Send(bytes, bytes.length);
					// p_UnderLayer.Send(bytes, bytes.length);
				} else {
					JOptionPane.showMessageDialog(null, "주소 설정 오류");
				}
			}
		});
		Chat_send_Button.setBounds(383, 249, 161, 21);
		pane.add(Chat_send_Button);

		NIC_select_Button = new JButton("Select");
		NIC_select_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selected = comboBox.getSelectedItem().toString();
				selected_index = comboBox.getSelectedIndex();
				srcMacAddress.setText("");
				try {
					byte[] MacAddress = ((NILayer) m_LayerMgr.GetLayer("NI")).GetAdapterObject(selected_index)
							.getHardwareAddress();
					String hexNumber;
					for (int i = 0; i < 6; i++) {
						hexNumber = Integer.toHexString(0xff & MacAddress[i]);
						srcMacAddress.append(hexNumber.toUpperCase());
						if (i != 5)
							srcMacAddress.append("-");
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		NIC_select_Button.setBounds(418, 69, 87, 23);
		pane.add(NIC_select_Button);

		File_send_Button = new JButton("File Send");
		File_send_Button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (Setting_Button.getText() == "Reset") {
					byte[] type = new byte[2];
					type[0] = 0x08;
					type[1] = 0x30;
//					((EthernetLayer) m_LayerMgr.GetLayer("Ethernet")).SetEnetType(type);

					String filepath = FileDir_path.getText();
					System.out.println(filepath);
					m_LayerMgr.GetLayer("File").Send(filepath);
					// p_UnderLayer.Send(filename);
				}

				else {
					JOptionPane.showMessageDialog(null, "주소 설정 오류");
				}
			}
		});
		File_send_Button.setBounds(322, 311, 161, 23);
		pane.add(File_send_Button);

		comboBox = new JComboBox();

		comboBox.setBounds(380, 38, 170, 24);
		pane.add(comboBox);

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
	
	
	public boolean Receive(byte[] input) {
		byte[] data = input;
		String Text = new String(data);
		ChattingArea.append("[RECV] : " + Text + "\n");
		return false;
	}

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}
}
