package Application;

import NetworkLayer.ARPLayer;
import NetworkLayer.ChatAppLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.FileAppLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import View.AppView;
import View.ChatPanel;

import java.util.ArrayList;
import java.util.List;

import EventHandlers.*;


public class ApplicationController {
	
	
	public void init() {
		new AppView();
		
		LayerManager layerManager = new LayerManager();
		layerManager.AddLayer(new NILayer("NI"));
		layerManager.AddLayer(new EthernetLayer("Ethernet"));
		layerManager.AddLayer(new ARPLayer("ARP"));
		layerManager.AddLayer(new IPLayer("IP"));
		layerManager.AddLayer(new ChatAppLayer("Chat"));
		layerManager.AddLayer(new FileAppLayer("File"));
		layerManager.ConnectLayers("NI ( *Ethernet ( *IP ( *Chat *File ) *ARP ( +IP ) ) )" );
		
		
		// initialization event handler in here
		List<EventHandlers> listEventHandlers = new ArrayList<EventHandlers>();
		listEventHandlers.add(new AddressEventHandlers());
		listEventHandlers.add(new ARPTableEventHandlers());
		listEventHandlers.add(new GARPEventHandlers());
		listEventHandlers.add(new ProxyARPEventHandlers());
		
		for(EventHandlers eventHandlers : listEventHandlers) {
			eventHandlers.setEventHandlers(layerManager);
		}
	}
		

	public static void receiveMsg(String msg) {
		ChatPanel.chattingArea.append("[RECV] : " + msg + "\n");
	}
}

