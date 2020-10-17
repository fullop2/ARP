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
			
			byte[] broadcast = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
			byte[] NIL = {0,0,0,0,0,0};
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String ip = ARPCachePanel.ArpIP.getText();
				// ip 앞 뒤 공백 제거 
				ip = ip.trim();
				StringTokenizer ipSplit = new StringTokenizer(ip, ".");
				byte[] ipAddress = new byte[4];
				for (int i = 0; i < 4; i++) {
					ipAddress[i] = (byte)Integer.parseInt(Integer.toHexString(Integer.parseInt(ipSplit.nextToken())),16);
				}
				
				/*
				 * 이더넷
				 * 목적지 브로드캐스팅
				 * 타입 ARP로 설정
				 */
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				
				ethernetLayer.setDstEthernetAddress(broadcast);
				byte[] ethType = { (byte) 0x08, (byte)0x06 };
				ethernetLayer.setEthernetType(ethType);
				
				/*
				 * ARP
				 * 요청할 ip를 ARP Table에 추가
				 * 타겟으로 해당 ip 설정
				 */
			
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.setIPTargetAddress(ipAddress);
				arpLayer.setEthernetTargetAddress(NIL);
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
				String ip = data.split(" ")[0];
				StringTokenizer ipSplit = new StringTokenizer(ip, ".");
				byte[] ipAddress = new byte[4];
				for (int i = 0; i < 4; i++) {
					ipAddress[i] = (byte)Integer.parseInt(Integer.toHexString(Integer.parseInt(ipSplit.nextToken())),16);
				}
				
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
					String ip = cache.split(" ")[0];
					StringTokenizer ipSplit = new StringTokenizer(ip, ".");
					byte[] ipAddress = new byte[4];
					for (int i = 0; i < 4; i++) {
						ipAddress[i] = (byte)Integer.parseInt(Integer.toHexString(Integer.parseInt(ipSplit.nextToken())),16);
					}
					
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
