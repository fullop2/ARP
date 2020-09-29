package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import Application.AppView;
import NetworkLayer.LayerManager;

public class AddressEventHandlers implements EventHandlers{
	
	LayerManager layerManager;
	@Override
	public void setEventHandlers(LayerManager layerManager) {
		/*
		 * btnSetting
		 * author : Taehyun
		 * Just Alert TEST text
		 */
		AppView.btnSetting.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showConfirmDialog(null, "TEST","YES",JOptionPane.YES_OPTION);				
			}
			
		});
	}
	

	
}
