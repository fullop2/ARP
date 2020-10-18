package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import NetworkLayer.ARPAppLayer;
import NetworkLayer.ARPLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.LayerManager;
import View.ARPCachePanel;
import View.AddressPanel;
import View.GARPPanel;

public class GARPEventHandlers implements EventHandlers {

	@Override
	public void setEventHandlers(LayerManager layerManager) {
		GARPPanel.btnGARPSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {	
				/*
				 * 이더넷
				 * 목적지 브로드캐스팅
				 * 타입 ARP로 설정
				 */
				byte[] macAddress = Address.mac(GARPPanel.GARPMacAddr.getText());
				if(macAddress == null) return;
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				
				ethernetLayer.setDstEthernetAddress(Address.ETH_BROADCAST);
				ethernetLayer.setEthernetType(Address.ETH_TYPE_ARP);
				
				byte[] ipAddress = Address.ip( AddressPanel.srcIPAddress.getText());
				if(ipAddress == null) return;
				
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.setIPTargetAddress(ipAddress);
				arpLayer.setEthernetSenderAddress(macAddress);
				arpLayer.setEthernetTargetAddress(Address.ETH_NIL);
				// no info for tcp,ip
				
				// app
				ARPAppLayer arpAppLayer = ((ARPAppLayer)layerManager.GetLayer("ARPA"));			
				arpAppLayer.Send(null,0);
			}
		});
	}

}
