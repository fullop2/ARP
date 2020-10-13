package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import NetworkLayer.LayerManager;
import View.AppView;

public class AppViewEventHandlers implements EventHandlers {

	@Override
	public void setEventHandlers(LayerManager layerManager) {
		AppView.btnOpenNetwork.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AppView.networkView.setVisible(!AppView.networkView.isVisible());						
			}
		});

	}

}
