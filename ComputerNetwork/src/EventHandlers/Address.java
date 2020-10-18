package EventHandlers;

import java.util.Arrays;

import javax.swing.JOptionPane;

public class Address {
	
	public static final byte[] ETH_NIL = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00};
	public static final byte[] ETH_BROADCAST = { (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
	public static final byte[] ETH_TYPE_ARP = { (byte) 0x08, (byte)0x06 };
	public static final byte[] ETH_TYPE_IP = { (byte) 0x08, (byte)0x06 };
	public static final byte[] APP_PORT_CHAT = { (byte) 0x20, (byte)0x10 };
	
	public static boolean isNIL(byte[] eth) {
		return Arrays.equals(ETH_NIL, eth);
	}
	
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
		for(int i = 0; i < 4; i++) {
			address[i] = (byte) (Integer.parseInt(ipSplit[i]) & 0xff);
		}
		return address;
	}

	
}
