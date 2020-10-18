package NetworkLayer;

import java.util.ArrayList;

public class TestEthernetLayer extends EthernetLayer {

	public TestEthernetLayer(String pName) {
		super(pName);
		// TODO Auto-generated constructor stub
	}

	private byte[] sendMessage;
	private byte[] receiveMessage;

	
	public byte[] getSendMessage() {
		return sendMessage;
	}
	
	public byte[] getReceiveMessage() {
		return receiveMessage;
	}
	
	public boolean Send(byte[] input, int length) {
		sendMessage = input;
		if(p_UnderLayer != null) {
			p_UnderLayer.Send(input, length);
		}
		return true;
	}

	public boolean Receive(byte[] msg) {
		receiveMessage = msg;
		if(nUpperLayerCount > 0) {
			p_aUpperLayer.get(0).Receive(msg);
		}
		return false;
	}
	
	public boolean Receive(byte[] msg, int upperIndex) {
		receiveMessage = msg;
		if(nUpperLayerCount >= upperIndex) {
			p_aUpperLayer.get(upperIndex).Receive(msg);
		}
		return false;
	}

}
