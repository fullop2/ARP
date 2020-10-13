package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

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
				String msg = new String(ChatPanel.chattingWrite.getText());
				byte[] byteMsg = msg.getBytes();
				ChatPanel.chattingWrite.setText("");
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				ethernetLayer.setEthernetType(ipType);	
				
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				IPLayer ipLayer = ((IPLayer)layerManager.GetLayer("IP"));
				
				byte[] ethernetAddr = arpLayer.getEthernet(ipLayer.getIPDstAddr());
				if(ethernetAddr == null || Arrays.equals(ethNIL,ethernetAddr)) {
					System.out.println("[ERR] 현재 IP에 매칭되는 MAC이 없습니다 다시 확인하세요");
					AddressPanel.btnSettingDstAddress.getActionListeners()[0].actionPerformed(arg0);
				}
				else {
					ChatPanel.chattingArea.append("[SEND] : "+msg+"\n");
					TCPLayer tcpLayer =  ((TCPLayer)layerManager.GetLayer("TCP"));
					tcpLayer.setPort(port);				
					ChatAppLayer chatAppLayer = ((ChatAppLayer)layerManager.GetLayer("Chat"));
					chatAppLayer.Send(byteMsg,byteMsg.length);
				}
			}
		});
	}

}
