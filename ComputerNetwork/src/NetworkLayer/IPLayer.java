package NetworkLayer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class IPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public ArrayList<BaseLayer> p_aUnderLayer = new ArrayList<BaseLayer>();
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
		byte VER;
		byte HLEN;
		byte service;
		byte[] totalLength;
		
		byte[] identification;
		byte flag;
		byte[] fragmentOffset;
		
		byte timeToLive;
		byte protocol;
		byte[] headerChecksum;
		
		_IP_ADDR ipDstAddr;
		_IP_ADDR ipSrcAddr;

		public _IP_HEADER() {
			VER = 0x04; 						// IPv4 : 		0000 0100			USE LSB 4
			HLEN = 0x05; 						// 5block : 	0000 0101			USE LSB 4
			service = 0x00;						// service : 	0000 0000
			totalLength = new byte[2];			
			
			identification = new byte[2];
			flag = 0x02;						// flag : 		0000 0010			USE LSB 3
			fragmentOffset = new byte[2];		// fragOff: 	0000 0000 0000 0000 USE LSB 13
			
			headerChecksum = new byte[2];		
			
			timeToLive = 0x7F; 					// timeToLive :	0111 1111			127 hop
			protocol = 0x06; 					// protocol	:	0000 0110			TCP 6
					
			this.ipDstAddr = new _IP_ADDR();
			this.ipSrcAddr = new _IP_ADDR();
		}
		
		public _IP_HEADER(byte[] header) {
			this();
			VER = (byte) ((header[0] >> 4 ) & 0x0f);
			HLEN = (byte) (header[0] & 0x0f);
			
			service = header[1];
			totalLength[0] = header[2]; 
			totalLength[1] = header[3];
			
			identification[0] = header[4];
			identification[1] = header[5];
			flag = (byte) ((header[6] >> 5 ) & 0x7);
			fragmentOffset[0] = (byte) (header[6] & 0x1f);
			fragmentOffset[1] = header[7];
			
			headerChecksum[0] = header[8];
			headerChecksum[1] = header[9];
			timeToLive = header[10];
			protocol = header[11];
			
			System.arraycopy(header, 12, ipDstAddr.addr, 0, 4);
			System.arraycopy(header, 16, ipSrcAddr.addr, 0, 4);
		}
		
		public void setTotalLength(int length) {
			assert(length <= 65535);
			totalLength[0] = (byte) ((length >> 8) & 0xFF);				
			totalLength[1] = (byte) (length & 0xFF);	
		}
		
		public int getTotalLength() {
			return (int)(totalLength[0] << 8) + (int)(totalLength[1]);
		}
		
		// 데이터 전송을 위한 헤더를 만드는 함수. 
		public byte[] makeHeader() {
			byte[] header = new byte[20];
			
			header[0] = (byte) (((VER << 4) & 0xf0) | ((HLEN) & 0x0f));
			header[1] = service;
			header[2] = totalLength[0]; 
			header[3] = totalLength[1];
			
			header[4] = identification[0];
			header[5] = identification[1];
			header[6] = (byte) (((flag << 5) & 0xe0) | ((fragmentOffset[0]) & 0x1f)); 
			header[7] = fragmentOffset[1];
			
			header[8] = headerChecksum[0];
			header[9] = headerChecksum[1];
			header[10] = timeToLive;
			header[11] = protocol;
			
			for(int i = 0; i < 4; i++) {
				header[12+i] = ipDstAddr.addr[i];
				header[16+i] = ipSrcAddr.addr[i];
			}
			return header;
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
	public boolean Send(byte[] input, int length) {
		
		if(input == null && length == 0) // ARP req
			return p_aUnderLayer.get(1).Send(null, 0);
		
		byte[] packet = new byte[20+length];
		ipHeader.setTotalLength(20+length);
		System.arraycopy(ipHeader.makeHeader(), 0, packet, 0, 20);
		System.arraycopy(input, 0, packet, 20, length);
		
		return p_aUnderLayer.get(0).Send(packet, 20+length);
	}
	
	// 해당 ip가 내 ip인지 확인
	private boolean isMine(byte[] ip) {
		return Arrays.equals(ipHeader.ipSrcAddr.addr, ip);
	}
	
	private boolean versionValid(byte versionLength) {
		return versionLength == 0x04;
	}
	private boolean lengthValid(byte versionLength) {
		return versionLength == 0x05;
	}
	
	@Override
	public boolean Receive(byte[] input) {
		if(input.length < 20)
			return false;
		
		_IP_HEADER receiveHeader = new _IP_HEADER(input);
		if(versionValid(receiveHeader.VER) && lengthValid(receiveHeader.HLEN)) {
			if(isMine(receiveHeader.ipSrcAddr.addr))
				return false;
			else if(isMine(receiveHeader.ipDstAddr.addr)) {
				byte[] data = new byte[input.length-20];
				System.arraycopy(input, 20, data, 0, input.length-20);
				p_aUpperLayer.get(0).Receive(data);
				return true;
			}
				
		}
		return false;
		
	}
	
	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_aUnderLayer.add(pUnderLayer);
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
		if (p_aUnderLayer.size() == 0)
			return null;
		return p_aUnderLayer.get(0);
	}
	
	@Override
	public BaseLayer GetUnderLayer(int index) {
		// TODO Auto-generated method stub
		if (p_aUnderLayer.size() == 0)
			return null;
		return p_aUnderLayer.get(index);
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
