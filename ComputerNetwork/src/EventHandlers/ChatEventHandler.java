package EventHandlers;

import java.awt.desktop.SystemSleepEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.JOptionPane;

import NetworkLayer.ARPLayer;
import NetworkLayer.ChatAppLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TCPLayer;
import View.AddressPanel;
import View.ChatPanel;

public class ChatEventHandler implements EventHandlers {

	@Override
	public void setEventHandlers(LayerManager layerManager) {
		ChatPanel.btnChatSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/* Setting Msg */
				String msg = new String(ChatPanel.chattingWrite.getText());
				if(msg.length() == 0) return;
				byte[] byteMsg = msg.getBytes();
				ChatPanel.chattingWrite.setText("");	
				
				/* Setting Network Options */
				String btnState = AddressPanel.btnSettingSrcAddress.getText();
				
				if(btnState.equals("Setting")){
					JOptionPane.showMessageDialog(null, "[ERR] 네트워크를 설정해주세요");
					return;
				}
				
				byte[] ipAddress = Address.ip(AddressPanel.dstIPAddress.getText());
				if(ipAddress == null) return;
				for(byte b : ipAddress)
					System.out.print((int)(b & 0x000000ff)+".");
				System.out.println();
				ARPLayer arp = ((ARPLayer)layerManager.GetLayer("ARP"));
				
				byte[] ethernetAddress = arp.getEthernet(ipAddress);
				
				
				if(ethernetAddress == null || Address.isNIL(ethernetAddress)) {
					System.out.println("IP에 대응하는 MAC을 찾지 못했습니다");
					JOptionPane.showMessageDialog(null, "[ERR] IP에 대응하는 MAC을 찾지 못했습니다");
					return;
				}
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				ethernetLayer.setEthernetType(Address.ETH_TYPE_IP);
				ethernetLayer.setDstEthernetAddress(ethernetAddress);
				
				IPLayer ip = ((IPLayer)layerManager.GetLayer("IP"));
				ip.setIPDstAddr(ipAddress);
				
				TCPLayer tcpLayer =  ((TCPLayer)layerManager.GetLayer("TCP"));
				tcpLayer.setPort(Address.APP_PORT_CHAT);	
				
				/* Send Msg */		
				ChatAppLayer chatAppLayer = ((ChatAppLayer)layerManager.GetLayer("Chat"));
				
				byte[] myIP = Address.ip(AddressPanel.srcIPAddress.getText());
				chatAppLayer.setIP(myIP);
				
				byte[] nickname = ChatPanel.nickName.getText().getBytes();
				if(nickname.length == 0)
					nickname = "Anonymous".getBytes();
				
				chatAppLayer.setNickname(nickname);
				
				chatAppLayer.Send(byteMsg,byteMsg.length);
			}
		});
	}

	public static void printMsg(String msgType,byte nickNameLen, byte[] nickname, byte[] ip, byte[] msg) {
		StringBuffer str = new StringBuffer();
		str.append("["+msgType+" ");
		byte[] nickName = new byte[nickNameLen];
		System.arraycopy(nickname, 0, nickName, 0, nickNameLen);
		str.append(new String(nickName).trim()+" ");
		for(int i = 0; i < 3; i++)
			str.append((int)(ip[i] & 0xff)+".");
		str.append((int)(ip[3] & 0xff)+"] : ");
		str.append(new String(msg)+"\n");
		
		ChatPanel.chattingArea.append(str.toString());
	}
}
