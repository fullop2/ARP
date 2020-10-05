package EventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextField;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;

import NetworkLayer.ARPLayer;
import NetworkLayer.LayerManager;
import View.AppView;
import View.ProxyARPPanel;

public class ProxyARPEventHandlers implements EventHandlers {
	int start;
	int end;

	@Override
	public void setEventHandlers(LayerManager layerManager) {
		// TODO Auto-generated method stub
		/*
		 * btnProxyAdd 
		 * author : Hyoin 
		 * Device와 IP와 MAC을 Layer Model에 등록
		 * 예외 : IP와 MAC주소가 비어있을 때 추가 안함/ IP와 MAC주소의 길이 형식이 맞지 않을 때 추가안함
		 */
		ProxyARPPanel.btnProxyAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String deviceAddress = ProxyARPPanel.proxyDevice.getText();
				String macAddress = ProxyARPPanel.proxyOutMAC.getText();
				String ipStringAddress = ProxyARPPanel.proxyInIP.getText();
				String[] ipSplit = ipStringAddress.split("\\.");

				if (deviceAddress.trim().length()!=0 && macAddress .trim().length()!=0&&ipStringAddress.trim().length()!=0&&macAddress.trim().length()==12&&ipSplit.length==4) {
					byte[] hardwareAddress = new byte[6];
					for (int i = 0; i < 6; i++)
						hardwareAddress[i] = (byte) (Integer.parseInt(
								macAddress.substring(i * 2, i * 2 + 2), 16) & 0xff);

					byte[] ipAddress = new byte[4];
					for (int i = 0; i < 4; i++)
						ipAddress[i] = (byte) (Integer.parseInt(ipSplit[i], 16) & 0xff);

					ARPLayer arp = ((ARPLayer) layerManager.GetLayer("ARP"));
					arp.setProxyTable(deviceAddress, ipAddress, hardwareAddress);

					ProxyARPPanel.proxyArpEntry.append(deviceAddress + "    "
							+ ipStringAddress + "    " + macAddress + "\n");
				}
			}

		});

		/*
		 * Proxy Arp Entry click
		 *  author : Hyoin
		 *  proxy arp entry에 있는 arp를 클릭하면
		 * 해당하는 ARP의 Device, IP, Mac주소 field에 표시
		 */
		ProxyARPPanel.proxyArpEntry.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int offset = ProxyARPPanel.proxyArpEntry.viewToModel(e
						.getPoint());

				String end_line = null;
				try {
					start = Utilities.getRowStart(ProxyARPPanel.proxyArpEntry,
							offset);
					end = Utilities.getRowEnd(ProxyARPPanel.proxyArpEntry,
							offset);
					end_line = ProxyARPPanel.proxyArpEntry.getText(start, end
							- start);
					
					if(end_line.trim().length()!=0){
						String[] addr = end_line.split("    ");
						ProxyARPPanel.proxyDevice.setText(addr[0]);
						ProxyARPPanel.proxyInIP.setText(addr[1]);
						ProxyARPPanel.proxyOutMAC.setText(addr[2]);
					}
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
		});

		/*
		 * btnProxyDelete
		 *  author : Hyoin 
		 * 내가 선택한 proxy arp 삭제(Device 이름으로 삭세)
		 */
		ProxyARPPanel.btnProxyDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				ProxyARPPanel.proxyArpEntry.replaceRange("", start, end + 1);

				ARPLayer arp = ((ARPLayer) layerManager.GetLayer("ARP"));
				arp.removeProxyTable(ProxyARPPanel.proxyDevice.getText());
				ProxyARPPanel.proxyDevice.setText("");
				ProxyARPPanel.proxyInIP.setText("");
				ProxyARPPanel.proxyOutMAC.setText("");
			}

		});
	}

}
