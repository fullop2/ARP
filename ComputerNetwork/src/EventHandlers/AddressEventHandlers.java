package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import View.AppView;

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
		
		AppView.comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				
				int index = cb.getSelectedIndex();
				try {
					byte[] hardwareAddress = ((NILayer)layerManager.GetLayer("NI")).GetAdapterObject(index).getHardwareAddress();	
					StringBuffer stringBuffer = new StringBuffer();
					
					for(int i = 0; i < hardwareAddress.length; i++) {
						stringBuffer.append(String.format("%02X", (hardwareAddress[i] & 0xff)));
					}
					AppView.srcMacAddress.setText(stringBuffer.toString());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
	}
}
