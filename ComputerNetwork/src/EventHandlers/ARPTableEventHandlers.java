package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import NetworkLayer.ARPAppLayer;
import NetworkLayer.ARPLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.LayerManager;
import View.ARPCachePanel;

public class ARPTableEventHandlers implements EventHandlers {

	@Override
	public void setEventHandlers(LayerManager layerManager) {
		ARPCachePanel.btnArpSend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				byte[] ipAddress = Address.ip(ARPCachePanel.ArpIP.getText());
				
				if(ipAddress == null) return;
				
				/*
				 * 이더넷
				 * 목적지 브로드캐스팅
				 * 타입 ARP로 설정
				 */
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				
				ethernetLayer.setDstEthernetAddress(Address.ETH_BROADCAST);
				ethernetLayer.setEthernetType(Address.ETH_TYPE_ARP);
				
				/*
				 * ARP
				 * 요청할 ip를 ARP Table에 추가
				 * 타겟으로 해당 ip 설정
				 */
			
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.setIPTargetAddress(ipAddress);
				arpLayer.setEthernetTargetAddress(Address.ETH_NIL);
				// no info for tcp,ip
				
				// app
				ARPAppLayer arpAppLayer = ((ARPAppLayer)layerManager.GetLayer("ARPA"));			
				arpAppLayer.Send(null,0);
			}
		});
		
		ARPCachePanel.btnArpDeleteOne.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String data = ARPCachePanel.ArpTable.getSelectedItem();
				if(data == null) return;
				
				byte[] ipAddress = Address.ip(data.split(" ")[0]);
				
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.deleteARPCache(ipAddress);
			}
		});
		
		ARPCachePanel.btnArpDeleteAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String[] data = ARPCachePanel.ArpTable.getItems();
				if(data.length == 0) return;
				
				for(String cache : data) {
					
					byte[] ipAddress = Address.ip(cache.split(" ")[0]);
					
					ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
					arpLayer.deleteARPCache(ipAddress);
				}
			}
		});
	}

	public static void updateARPTable(String[] stringData) {
		ARPCachePanel.ArpTable.removeAll();
		for(String str : stringData) {
			ARPCachePanel.ArpTable.add(str);
		}
	}
}
