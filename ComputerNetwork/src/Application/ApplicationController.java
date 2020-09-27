package Application;

import NetworkLayer.ApplicationLayer;
import NetworkLayer.ChatAppLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.FileAppLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;

import java.util.ArrayList;
import java.util.List;

import EventHandlers.*;


public class ApplicationController {
	
	
	public void init() {
		new AppView();
		
		LayerManager layerManager = new LayerManager();
		layerManager.AddLayer(new NILayer("NI"));
		layerManager.AddLayer(new EthernetLayer("Ethernet"));
		layerManager.AddLayer(new IPLayer("IP"));
		layerManager.AddLayer(new ChatAppLayer("Chat"));
		layerManager.AddLayer(new FileAppLayer("File"));
		layerManager.AddLayer(new ApplicationLayer("App"));
		layerManager.ConnectLayers("NI ( *Ethernet ( *IP ( *Chat ( *App ) *File ( *App ) ) )" );
		
		
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
		AppView.chattingArea.append("[RECV] : " + msg + "\n");
	}
}

