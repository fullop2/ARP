package NetworkLayer;

import java.util.ArrayList;

public class TCPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	@SuppressWarnings("unused")
	private class _TCP_HEADER {
		byte[] srcPortAddress;
		byte[] dstPortAddress;
		byte[] sequenceNumber;
		byte[] acknowledgementNumber;
		byte headerLength;
		byte reserved;
		byte FLAG;
		byte[] windowSize;
		byte[] checksum;
		byte[] urgentPointer;
		
		public _TCP_HEADER() {
			srcPortAddress = new byte[2];
			dstPortAddress = new byte[2];
			sequenceNumber = new byte[4];
			acknowledgementNumber = new byte[4];
			windowSize = new byte[2];
			checksum = new byte[2];
			urgentPointer = new byte[2];
			
			headerLength = 0x05;
			
		}
	}
	
	private _TCP_HEADER tcpHeader = new _TCP_HEADER();	
	
	public TCPLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
	}

	
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
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
