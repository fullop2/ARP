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
				arpLayer.addARPCache(ipAddress, null);
				
				ARPCachePanel.ArpTable.append(ip+"\t????????????\tincompleted\n");
				
			}
		});
	}


}
