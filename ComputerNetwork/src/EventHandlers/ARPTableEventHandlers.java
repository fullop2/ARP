package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import NetworkLayer.ARPAppLayer;
import NetworkLayer.ARPLayer;
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
				
				// app
				ARPAppLayer arpAppLayer = ((ARPAppLayer)layerManager.GetLayer("ARPA"));
				
				// no info for tcp,ip
				
				// arp
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				arpLayer.addARPCache(ipAddress, null);
				arpLayer.setIPTargetAddress(ipAddress);
	
				arpAppLayer.Send();
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
				ARPCachePanel.ArpTable.remove(data);
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
					ARPCachePanel.ArpTable.remove(cache);
				}
			}
		});
	}


}
