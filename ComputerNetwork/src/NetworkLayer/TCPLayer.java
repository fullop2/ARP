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
		
		public _TCP_HEADER(byte[] header) {
			this();
			srcPortAddress[0] = header[0];
			srcPortAddress[1] = header[1];
			dstPortAddress[0] = header[2];
			dstPortAddress[1] = header[3];
			sequenceNumber[0] = header[4];
			sequenceNumber[1] = header[5];
			sequenceNumber[2] = header[6];
			sequenceNumber[3] = header[7];
			acknowledgementNumber[0] = header[8];
			acknowledgementNumber[1] = header[9];
			acknowledgementNumber[2] = header[10];
			acknowledgementNumber[3] = header[11];
			headerLength = (byte)((header[12] >> 4) & 0x0f);
			reserved = (byte)(((header[12] << 4) & 0xf0) | ((header[13] >> 6) & 0x03));
			FLAG = (byte)((header[13] >> 6) & 0x3f);
			windowSize[0] = header[14];
			windowSize[1] = header[15];
			checksum[0] = header[16];
			checksum[1] = header[17];
			urgentPointer[0] = header[18];
			urgentPointer[1] = header[19];
		}
		
		public byte[] makeHeader() {
			byte[] header = new byte[20];
			header[0] = srcPortAddress[0];
			header[1] = srcPortAddress[1];
			header[2] = dstPortAddress[0];
			header[3] = dstPortAddress[1];
			header[4] = sequenceNumber[0];
			header[5] = sequenceNumber[1];
			header[5] = sequenceNumber[2];
			header[6] = sequenceNumber[3];
			header[7] = acknowledgementNumber[0];
			header[8] = acknowledgementNumber[1];
			header[10] = acknowledgementNumber[2];
			header[11] = acknowledgementNumber[3];
			header[12] = (byte)(((headerLength<< 4) & 0xf0) | ((reserved >> 4) & 0x0f));
			header[13] = (byte)(((reserved  << 6) & 0xc0) | (FLAG & 0x3f));
			header[14] = windowSize[0];
			header[15] = windowSize[1];
			header[16] = checksum[0];
			header[17] = checksum[1];
			header[18] = urgentPointer[0];
			header[19] = urgentPointer[1];
			
			return header;
		}
	}
	
	private _TCP_HEADER tcpHeader = new _TCP_HEADER();	
	
	public TCPLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
	}

	public void setPort(byte[] port) {
		assert(port.length == 2);
		tcpHeader.srcPortAddress[0] = port[0];
		tcpHeader.srcPortAddress[1] = port[1];
	}
	
	public boolean isChatApp(byte[] port) {
		return port[0] == 0x20 && port[1] == 0x10;
	}
	
	public boolean isFileApp(byte[] port) {
		return port[0] == 0x20 && port[1] == 0x20;
	}
	
	@Override
	public boolean Send(byte[] input, int length) {
		if(input == null && length == 0) { // ARP Req is null
			p_UnderLayer.Send(null, 0);
		}
		else {
			tcpHeader.dstPortAddress[0] = tcpHeader.srcPortAddress[0];
			tcpHeader.dstPortAddress[1] = tcpHeader.srcPortAddress[1];
			
			byte[] msg = new byte[20+length];
			byte[] header = tcpHeader.makeHeader();
			System.arraycopy(header, 0, msg, 0, 20);
			System.arraycopy(input, 0, msg, 20, length);
			
			p_UnderLayer.Send(msg, msg.length);
		}
		return true;
	}
	
	
	@Override
	public boolean Receive(byte[] input) {
		if(input.length < 20) 
			return false;
		
		_TCP_HEADER receiveHeader = new _TCP_HEADER(input);
		byte[] data = new byte[input.length-20];
		System.arraycopy(input, 20, data, 0, input.length-20);
		
		if(isChatApp(receiveHeader.dstPortAddress)) {
			p_aUpperLayer.get(0).Receive(data);
		}
		else if(isFileApp(receiveHeader.dstPortAddress)) {
			p_aUpperLayer.get(1).Receive(data);
		}
		return true;
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
