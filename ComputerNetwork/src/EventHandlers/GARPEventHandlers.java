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
				
				String mac = GARPPanel.GARPMacAddr.getText();	
				if(!macValidation(mac)) {
					JOptionPane.showMessageDialog(null, "[ERR] MAC을 제대로 설정해주세요");
					return;
				}
				
				String[] macSplit = mac.split("-");
				byte[] macAddress = new byte[6];				
				for(int i = 0; i < 6; i++)
					macAddress[i] = (byte) (Integer.parseInt(macSplit[i],16) & 0xff);
				
				EthernetLayer ethernetLayer = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
				
				ethernetLayer.setDstEthernetAddress(Address.ETH_BROADCAST);
				ethernetLayer.setEthernetType(Address.ETH_TYPE_ARP);
				
				String ip = AddressPanel.srcIPAddress.getText();
				StringTokenizer ipSplit = new StringTokenizer(ip, ".");
				byte[] ipAddress = new byte[4];
				for (int i = 0; i < 4; i++)
					ipAddress[i] = (byte)Integer.parseInt(ipSplit.nextToken());
				
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.setIPTargetAddress(ipAddress);
				arpLayer.setEthernetSenderAddress(macAddress);
				arpLayer.setEthernetTargetAddress(Address.ETH_NIL);
				// no info for tcp,ip
				
				// app
				ARPAppLayer arpAppLayer = ((ARPAppLayer)layerManager.GetLayer("ARPA"));			
				arpAppLayer.Send(null,0);
			}
			
			boolean macValidation(String macAddress) {
				
				return macAddress.matches("([0-9A-F]{2}[:-]){5}([0-9A-F]{2})");
			}
		});
	}

}
