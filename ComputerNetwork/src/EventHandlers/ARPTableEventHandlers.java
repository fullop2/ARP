package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
				String[] ipSplit = ip.split("\\.");
				byte[] ipAddress = new byte[4];
				for (int i = 0; i < 4; i++)
					ipAddress[i] = (byte) (Integer.parseInt(ipSplit[i], 16) & 0xff);
				
				ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
				boolean newItemAdded = arpLayer.addARPCache(ipAddress, null);
				
				if(newItemAdded)
					ARPCachePanel.ArpTable.add(ip+" ???????????? incompleted\n");
				
				arpLayer.Send();
			}
		});
		
		ARPCachePanel.btnArpDeleteOne.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String data = ARPCachePanel.ArpTable.getSelectedItem();
				if(data == null) return;
				String ip = data.split(" ")[0];
				String[] ipSplit = ip.split("\\.");
				byte[] ipAddress = new byte[4];
				for (int i = 0; i < 4; i++)
					ipAddress[i] = (byte) (Integer.parseInt(ipSplit[i], 16) & 0xff);
				
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
					String ip = cache.split(" ")[0];
					String[] ipSplit = ip.split("\\.");
					byte[] ipAddress = new byte[4];
					for (int i = 0; i < 4; i++)
						ipAddress[i] = (byte) (Integer.parseInt(ipSplit[i], 16) & 0xff);
					
					ARPLayer arpLayer = ((ARPLayer)layerManager.GetLayer("ARP"));
					arpLayer.deleteARPCache(ipAddress);
					ARPCachePanel.ArpTable.remove(cache);
				}
			}
		});
	}


}
