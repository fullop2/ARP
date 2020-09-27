package Application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;

import NetworkLayer.ApplicationLayer;
import NetworkLayer.ChatAppLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.FileAppLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;

public class ApplicationController {

	private LayerManager layerManager = new LayerManager();
	private ApplicationLayer applicationLayer = new ApplicationLayer("App");
	private int selected_index;

	
	public void init() {
		new AppView();
		
		layerManager.AddLayer(new NILayer("NI"));
		layerManager.AddLayer(new EthernetLayer("Ethernet"));
		layerManager.AddLayer(new ChatAppLayer("Chat"));
		layerManager.AddLayer(new FileAppLayer("File"));
		layerManager.AddLayer(applicationLayer);

		layerManager.ConnectLayers("NI ( *Ethernet ( *Chat ( *App ) *File ( *App ) ) )" );
		
		// initialization event handler in here
	}
		

	public static void receiveMsg(String msg) {
		AppView.chattingArea.append("[RECV] : " + msg + "\n");
	}
}

