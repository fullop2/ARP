package EventHandlers;

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
				
				IPLayer ip = ((IPLayer)layerManager.GetLayer("IP"));
				ip.setIPDstAddr(ipAddress);
				
				TCPLayer tcpLayer =  ((TCPLayer)layerManager.GetLayer("TCP"));
				tcpLayer.setPort(Address.APP_PORT_CHAT);	
				
				/* Send Msg */
				ChatPanel.chattingArea.append("[SEND] : "+msg+"\n");			
				ChatAppLayer chatAppLayer = ((ChatAppLayer)layerManager.GetLayer("Chat"));
				chatAppLayer.Send(byteMsg,byteMsg.length);
			}
		});
	}

}
