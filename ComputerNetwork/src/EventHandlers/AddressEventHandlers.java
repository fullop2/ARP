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
					String macAddress = AddressPanel.srcMacAddress.getText();			
					byte[] hardwareAddress = new byte[6];
					
					for(int i = 0; i < 6; i++)
						hardwareAddress[i] = (byte) (Integer.parseInt(macAddress.substring(i*2, i*2+2),16) & 0xff);
					
					String ipStringAddress = AddressPanel.srcIPAddress.getText();
					String[] ipSplit = ipStringAddress.split("\\.");
					
					byte[] ipAddress = new byte[4];
					for(int i = 0; i < 4; i++)
						ipAddress[i] = (byte) (Integer.parseInt(ipSplit[i]) & 0xff);
				
					int index = AddressPanel.comboBox.getSelectedIndex();
					
					NILayer niLayer = ((NILayer)layerManager.GetLayer("NI"));
					niLayer.SetAdapterNumber(index);
					
					EthernetLayer ethernet = ((EthernetLayer)layerManager.GetLayer("Ethernet"));
					ethernet.setSrcEthernetAddress(hardwareAddress);
					
					ARPLayer arp = ((ARPLayer)layerManager.GetLayer("ARP"));
					arp.setEthernetSenderAddress(hardwareAddress);
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
					
					for(int i = 0; i < hardwareAddress.length; i++) {
						stringBuffer.append(String.format("%02X", (hardwareAddress[i] & 0xff)));
					}
					AddressPanel.srcMacAddress.setText(stringBuffer.toString());

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		});
		
		AddressPanel.btnSettingDstAddress.addActionListener(new ActionListener() {
			
			byte[] ethNIL = { 0,0,0,0,0,0};
			boolean isSetting = true;
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if(isSetting) {
					
					String ipStringAddress = AddressPanel.dstIPAddress.getText();
					String[] ipSplit = ipStringAddress.split("\\.");
					
					byte[] ipAddress = new byte[4];
					for(int i = 0; i < 4; i++)
						ipAddress[i] = (byte) (Integer.parseInt(ipSplit[i]) & 0xff);
				
	
					
					ARPLayer arp = ((ARPLayer)layerManager.GetLayer("ARP"));
					
					byte[] ethernetAddress = arp.getEthernet(ipAddress);
					if(ethernetAddress == null || Arrays.equals(ethNIL, ethernetAddress)) {
						System.out.println("IP에 대응하는 MAC을 찾지 못했습니다");
						return;
					}
					
					arp.setEthernetTargetAddress(ethernetAddress);
					arp.setIPTargetAddress(ipAddress);
					
					IPLayer ip = ((IPLayer)layerManager.GetLayer("IP"));
					ip.setIPDstAddr(ipAddress);
					
					AddressPanel.btnSettingSrcAddress.setText("Reset");
					AddressPanel.dstIPAddress.setEditable(false);
					ChatPanel.chatSendButton.setEnabled(true);					
					
					isSetting = false;
				}
				else {		
					AddressPanel.btnSettingSrcAddress.setText("Setting");
					AddressPanel.dstIPAddress.setEditable(true);
					ChatPanel.chatSendButton.setEnabled(false);
					isSetting = true;
				}
			}
			
		});
	}
}
