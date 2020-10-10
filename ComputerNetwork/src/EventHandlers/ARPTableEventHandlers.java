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
				String ip = ARPCachePanel.ArpIP.getText();
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
				byte[] broadcast = { (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF};
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
				
				// no info for tcp,ip
				
				// app
				ARPAppLayer arpAppLayer = ((ARPAppLayer)layerManager.GetLayer("ARPA"));			
				arpLayer.Send(null,0);
			}
		});
		
		ARPCachePanel.btnArpDeleteOne.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String data = ARPCachePanel.ArpTable.getSelectedItem();
				if(data == null) return;
				String ip = ARPCachePanel.ArpIP.getText();
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
					String ip = ARPCachePanel.ArpIP.getText();
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


}
