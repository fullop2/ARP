package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import NetworkLayer.ChatAppLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TCPLayer;
import View.ChatPanel;

public class ChatEventHandler implements EventHandlers {

	@Override
	public void setEventHandlers(LayerManager layerManager) {
		ChatPanel.btnChatSend.addActionListener(new ActionListener() {
			
			byte[] port = {0x20,0x10};
			byte[] ipType= {0x08,0x00};
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String msg = ChatPanel.chattingWrite.getText();
				byte[] byteMsg = msg.getBytes();
 				ChatPanel.chattingArea.append("[SEND] : "+msg+"\n");
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				ethernetLayer.setEthernetType(ipType);
				
				TCPLayer tcpLayer =  ((TCPLayer)layerManager.GetLayer("TCP"));
				tcpLayer.setPort(port);
				ChatAppLayer chatAppLayer = ((ChatAppLayer)layerManager.GetLayer("Chat"));
				chatAppLayer.Send(byteMsg,byteMsg.length);
				ChatPanel.chattingWrite.setText("");
			}
		});
	}

}
