package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

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
				String mac = GARPPanel.GARPMacAddr.getText();
				byte[] macAddress = new byte[6];				
				for(int i = 0; i < 6; i++)
					macAddress[i] = (byte) (Integer.parseInt(mac.substring(i*2, i*2+2),16) & 0xff);
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				byte[] nil = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
				byte[] broadcast = { (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
				byte[] ethType = { (byte) 0x08, (byte)0x06 };
				
				ethernetLayer.setDstEthernetAddress(broadcast);
				ethernetLayer.setEthernetType(ethType);
				
				/*
				 * ARP
				 * 요청할 ip를 ARP Table에 추가
				 * 타겟으로 해당 ip 설정
				 */
				
				String ip = AddressPanel.srcIPAddress.getText();
				StringTokenizer ipSplit = new StringTokenizer(ip, ".");
				byte[] ipAddress = new byte[4];
				for (int i = 0; i < 4; i++)
					ipAddress[i] = (byte)Integer.parseInt(ipSplit.nextToken());
				
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.setIPTargetAddress(ipAddress);
				arpLayer.setEthernetTargetAddress(nil);
				// no info for tcp,ip
				
				// app
				ARPAppLayer arpAppLayer = ((ARPAppLayer)layerManager.GetLayer("ARPA"));			
				arpAppLayer.Send(null,0);
			}
		});
	}

}
