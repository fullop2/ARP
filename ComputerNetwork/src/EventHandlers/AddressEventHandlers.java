package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

import NetworkLayer.ARPLayer;
import NetworkLayer.EthernetLayer;
import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.NILayer;
import View.ARPCachePanel;
import View.AddressPanel;
import View.ChatPanel;
import View.GARPPanel;

public class AddressEventHandlers implements EventHandlers{
	
	@Override
	public void setEventHandlers(LayerManager layerManager) {
		/*
		 * btnSetting
		 * author : Taehyun
		 * IP와 MAC을 Layer Model에 등록
		 */
		AddressPanel.btnSettingSrcAddress.addActionListener(new ActionListener() {
			
			boolean isSetting = true;
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(isSetting) {
									
					byte[] macAddress = Address.mac(AddressPanel.srcMacAddress.getText());
					if(macAddress == null) return;
					byte[] ipAddress = Address.ip(AddressPanel.srcIPAddress.getText());
					if(ipAddress == null) return;
					
					int index = AddressPanel.comboBox.getSelectedIndex();
					
					NILayer niLayer = ((NILayer)layerManager.GetLayer("NI"));
					niLayer.SetAdapterNumber(index);
					
					EthernetLayer ethernet = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
					ethernet.setSrcEthernetAddress(macAddress);
					
					ARPLayer arp = ((ARPLayer)layerManager.GetLayer("ARP"));
					arp.setEthernetSenderAddress(macAddress);
					arp.setIPSenderAddress(ipAddress);
					
					IPLayer ip = ((IPLayer)layerManager.GetLayer("IP"));
					ip.setIPSrcAddr(ipAddress);
					
					AddressPanel.srcMacAddress.setEditable(false);
					AddressPanel.srcIPAddress.setEditable(false);
					AddressPanel.comboBox.setEnabled(false);
					
					GARPPanel.btnGARPSend.setEnabled(true);		
					ARPCachePanel.btnArpSend.setEnabled(true);
					
					AddressPanel.btnSettingSrcAddress.setText("Reset");
					isSetting = false;
				}
				else {
					NILayer niLayer = ((NILayer)layerManager.GetLayer("NI"));
					niLayer.stopReceive();					
					
					AddressPanel.btnSettingSrcAddress.setText("Setting");
					
					AddressPanel.srcMacAddress.setEditable(true);
					AddressPanel.srcIPAddress.setEditable(true);
					AddressPanel.comboBox.setEnabled(true);
					
					GARPPanel.btnGARPSend.setEnabled(false);	
					ARPCachePanel.btnArpSend.setEnabled(false);
					
					isSetting = true;
				}
			}			
		});
		
		/*
		 * comboBox
		 * author : Taehyun
		 * NIC 콤보박스에서 아이템 선택시
		 * 해당 NIC의 MAC을 불러와 srcMacAddress에 표시
		 */
		AddressPanel.comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JComboBox cb = (JComboBox)e.getSource();
				
				int index = cb.getSelectedIndex();
				try {
					byte[] hardwareAddress = ((NILayer)layerManager.GetLayer("NI")).GetAdapterObject(index).getHardwareAddress();	
					StringBuffer stringBuffer = new StringBuffer();
					
					for(int i = 0; i < hardwareAddress.length-1; i++) {
						stringBuffer.append(String.format("%02X-", (hardwareAddress[i] & 0xff)));
					}
					stringBuffer.append(String.format("%02X", (hardwareAddress[5] & 0xff)));
					AddressPanel.srcMacAddress.setText(stringBuffer.toString());

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
	}
}
