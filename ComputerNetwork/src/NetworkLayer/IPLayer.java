package NetworkLayer;

import java.util.ArrayList;

public class IPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	private class _IP_ADDR {
		private byte[] addr = new byte[4];

		public _IP_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
		}
	}
	
	@SuppressWarnings("unused")
	private class _IP_HEADER {
		byte[] VER;
		byte[] HLEN;
		byte[] service;
		byte[] totalLength;
		
		byte[] identification;
		byte[] flag;
		byte[] fragmentOffset;
		
		byte[] timeToLive;
		byte[] protocol;
		byte[] headerChecksum;
		
		_IP_ADDR ipDstAddr;
		_IP_ADDR ipSrcAddr;

		public _IP_HEADER() {
			VER = new byte[4];
			HLEN = new byte[4];
			service= new byte[8];
			totalLength = new byte[16];
			
			identification = new byte[16];
			flag = new byte[3];
			fragmentOffset = new byte[13];
			
			timeToLive = new byte[8];
			protocol = new byte[8];
			headerChecksum = new byte[16];
			
			VER[3] = 0x04; // IPv4
			HLEN[3] = 0x05; // 20byte / 4 = 5 block
			timeToLive[7] = 0x7F; // 127 hop
			protocol[3] = 0x06; // TCP protocol = 6
			
			
			this.ipDstAddr = new _IP_ADDR();
			this.ipSrcAddr = new _IP_ADDR();
		}
		
	}
	
	private _IP_HEADER ipHeader = new _IP_HEADER();
	
	public IPLayer(String string) {
		pLayerName = string;
	}

	public void setIPDstAddr(byte[] addr) {
		for(int i = 0; i < 4; i++)
			ipHeader.ipDstAddr.addr[i] = addr[i];
	}
	
	public void setIPSrcAddr(byte[] addr) {
		for(int i = 0; i < 4; i++)
			ipHeader.ipSrcAddr.addr[i] = addr[i];
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
