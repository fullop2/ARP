package NetworkLayer;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.jnetpcap.Pcap;

public class TestLayer implements BaseLayer {

	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();

	private byte[] sendMessage;
	private byte[] receiveMessage;
	
	
	public TestLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
	}
	
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
	
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}

}
