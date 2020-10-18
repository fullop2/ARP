package EventHandlers;

import javax.swing.JOptionPane;

public class Address {
	public static byte[] mac(String stringMacAddress) {
		stringMacAddress.trim();
		if(!stringMacAddress.matches("([0-9A-F]{2}[:-]){5}([0-9A-F]{2})")) {
			JOptionPane.showMessageDialog(null, "[ERR] MAC을 제대로 설정해주세요");
			return null;
		}
		String[] macSplit = stringMacAddress.split("-");
		byte[] address = new byte[6];
		for(int i = 0; i < 6; i++) {
			address[i] = (byte) (Integer.parseInt(macSplit[i],16) & 0xff);
		}
		return address;
	}
	
	public static byte[] ip(String stringIpAddress) {
		stringIpAddress.trim();
		if(!stringIpAddress.matches("((2[0-5]|1[0-9]|[0-9])?[0-9]\\.){3}((2[0-5]|1[0-9]|[0-9])?[0-9])")) {
			JOptionPane.showMessageDialog(null, "[ERR] IP를 제대로 설정해주세요");
			return null;
		}
		
		String[] ipSplit = stringIpAddress.split("\\.");
		byte[] address = new byte[6];
		for(int i = 0; i < 6; i++) {
			address[i] = (byte) (Integer.parseInt(ipSplit[i]) & 0xff);
		}
		return address;
	}
}
