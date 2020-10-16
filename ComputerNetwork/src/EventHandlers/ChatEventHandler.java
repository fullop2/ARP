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
			
			byte[] port = {0x20,0x10};
			byte[] ipType= {0x08,0x00};
			
			byte[] ethNIL = {0,0,0,0,0,0};
			@Override
			public void actionPerformed(ActionEvent arg0) {
				/* Setting Msg */
				String msg = new String(ChatPanel.chattingWrite.getText());
				byte[] byteMsg = msg.getBytes();
				ChatPanel.chattingWrite.setText("");	
				
				/* Setting Network Options */
				String btnState = AddressPanel.btnSettingSrcAddress.getText();
				
				if(btnState.equals("Setting")){
					JOptionPane.showMessageDialog(null, "[ERR] 네트워크를 설정해주세요");
					return;
				}
				
				String ipStringAddress = AddressPanel.dstIPAddress.getText();
				
				if(ipStringAddress.length() == 0){
					JOptionPane.showMessageDialog(null, "[ERR] IP를 입력해주세요");
					return;
				}
					
				String[] ipSplit = ipStringAddress.split("\\.");
				
				byte[] ipAddress = new byte[4];
				for(int i = 0; i < 4; i++)
					ipAddress[i] = (byte) (Integer.parseInt(ipSplit[i]) & 0xff);
				
				ARPLayer arp = ((ARPLayer)layerManager.GetLayer("ARP"));
				
				byte[] ethernetAddress = arp.getEthernet(ipAddress);
				if(ethernetAddress == null || Arrays.equals(ethNIL, ethernetAddress)) {
					System.out.println("IP에 대응하는 MAC을 찾지 못했습니다");
					JOptionPane.showMessageDialog(null, "[ERR] IP에 대응하는 MAC을 찾지 못했습니다");
					return;
				}
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				ethernetLayer.setEthernetType(ipType);
				
				IPLayer ip = ((IPLayer)layerManager.GetLayer("IP"));
				ip.setIPDstAddr(ipAddress);
				
				TCPLayer tcpLayer =  ((TCPLayer)layerManager.GetLayer("TCP"));
				tcpLayer.setPort(port);	
				
				/* Send Msg */
				ChatPanel.chattingArea.append("[SEND] : "+msg+"\n");			
				ChatAppLayer chatAppLayer = ((ChatAppLayer)layerManager.GetLayer("Chat"));
				chatAppLayer.Send(byteMsg,byteMsg.length);
			}
		});
	}

}
