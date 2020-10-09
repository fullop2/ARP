package NetworkLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import View.ARPCachePanel;


public class ARPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	// proxy arp table(key : device, value: IP Addr + MAC Addr)
	public HashMap<String,byte[]> proxyTable=new HashMap<String, byte[]>();
	
	// arp cache table
	private List<ARPCache> arpCacheTable = new ArrayList<ARPCache>();
	private List<ARPTimer> listArpTimer = new ArrayList<ARPTimer>();
	
	private final byte[] NIL_ETHERNET = new byte[6];
	
	public class ARPCache{
		_IP_ADDR ip = new _IP_ADDR();
		_ETHERNET_ADDR ethernet = new _ETHERNET_ADDR();
		
		public ARPCache(byte[] ip, byte[] ethernet) {
			setEthernet(ethernet);
			setIp(ip);
		}
		
		public void setEthernet(byte[] ethernet) {
			if(ethernet== null)
				return;
			assert(ethernet.length==6);
			for(int i = 0; i < 6; i++)
				this.ethernet.addr[i] = ethernet[i];
		}
		
		public void setIp(byte[] ip) {
			assert(ip.length == 4);
			for(int i = 0; i < 4; i++)
				this.ip.addr[i] = ip[i];
		}
		
		public String toString() {
			
			StringBuffer stringBuffer = new StringBuffer();
			
			for(int i = 0; i < 3; i++)
				stringBuffer.append((int)(ip.addr[i] & 0xff)+".");
			stringBuffer.append((int)(ip.addr[3] & 0xff)+" ");
			if(Arrays.equals(NIL_ETHERNET, ethernet.addr)) {
				stringBuffer.append("???????????? incompleted\n");
			}
			else {
				for(int i = 0; i < 6; i++)
					stringBuffer.append(Integer.toHexString((int)(ethernet.addr[3] & 0xff)).toUpperCase());
				stringBuffer.append(" completed\n");
			}
			
			return stringBuffer.toString();
		}
	}
	
	private class _ETHERNET_ADDR {
		private byte[] addr = new byte[6];

		public _ETHERNET_ADDR() {
			this.addr[0] = (byte) 0x00;
			this.addr[1] = (byte) 0x00;
			this.addr[2] = (byte) 0x00;
			this.addr[3] = (byte) 0x00;
			this.addr[4] = (byte) 0x00;
			this.addr[5] = (byte) 0x00;
		}
	}
	
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
	private class _ARP_HEADER {
		byte[] hardwareType;
		byte[] protocolType;		
		byte hardwareSize;
		byte protocolSize;
		byte[] opcode;
		_ETHERNET_ADDR enetSenderAddr;
		_IP_ADDR ipSenderAddr;
		_ETHERNET_ADDR enetTargetAddr;
		_IP_ADDR ipTargetAddr;
		
		final int headerSize = 28;
		
		public _ARP_HEADER() {
			hardwareType = new byte[2];
			hardwareType[0] = 0x00; hardwareType[1] = 0x01;
			protocolType = new byte[2];
			protocolType[0] = 0x08; protocolType[1] = 0x00;
			hardwareSize = 0x06;
			protocolSize = 0x04;
			opcode = new byte[2];
			opcode[0] = 0x00; opcode[1] = 0x01;
			enetSenderAddr = new _ETHERNET_ADDR();
			ipSenderAddr = new _IP_ADDR();
			enetTargetAddr = new _ETHERNET_ADDR();
			ipTargetAddr = new _IP_ADDR();
		}
		
		public _ARP_HEADER(byte[] header) {
			hardwareType = new byte[2];
			hardwareType[0] = header[0]; hardwareType[1] = header[1];
			protocolType = new byte[2];
			protocolType[0] = header[2]; protocolType[1] = header[3];
			hardwareSize = header[4];
			protocolSize = header[5];
			opcode = new byte[2];
			opcode[0] = header[6]; opcode[1] = header[7];
			enetSenderAddr = new _ETHERNET_ADDR();
			ipSenderAddr = new _IP_ADDR();
			enetTargetAddr = new _ETHERNET_ADDR();
			ipTargetAddr = new _IP_ADDR();
			
			for(int i=0; i <6; i++) {
				enetSenderAddr.addr[i] = header[8+i];
				enetTargetAddr.addr[i] = header[18+i];
			}
			for(int i=0; i <4; i++) {
				ipSenderAddr.addr[i] = header[14+i];
				ipTargetAddr.addr[i] = header[24+i];
			}
		}
		
		public byte[] makeHeader() {
			byte[] header = new byte[headerSize];
			header[0] = hardwareType[0]; 
			header[1] = hardwareType[1];
			header[2] = protocolType[0];
			header[3] = protocolType[1];
			header[4] = hardwareSize;
			header[5] = protocolSize;
			header[6] = opcode[0];
			header[7] = opcode[1];
			for(int i=0; i <6; i++) {
				header[8+i] =  enetSenderAddr.addr[i];
				header[18+i] = enetTargetAddr.addr[i];
			}
			for(int i=0; i <4; i++) {
				header[14+i] = ipSenderAddr.addr[i];
				header[24+i] = ipTargetAddr.addr[i];
			}
			return header;
		}
	}

	_ARP_HEADER arpHeader = new _ARP_HEADER();
	
	
	public ARPLayer(String string) {
		pLayerName = string;
		
		for(int i = 0; i < 6; i++)
			NIL_ETHERNET[i] = 0x00;
	}
  
	
	private class ARPTimer extends Thread{
		
		byte[] ip;
		ARPTimer(byte[] ip){
			this.ip = new byte[4];
			for(int i = 0; i < 4; i++)
				this.ip[i] = ip[i]; 
		}
		
		@Override
		public void run() {
			try {
				System.out.println("wait for 3");
				Thread.sleep(5000);
				if(Arrays.equals(NIL_ETHERNET,getEthernet(ip))) {
					deleteARPCache(ip);
				}				
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			}
		}
	}

	private class ARPReceiveTimer extends ARPTimer{
		ARPReceiveTimer(byte[] ip){
			super(ip);
		}
		
		@Override
		public void run() {
			try {
				if(getEthernet(ip) == null) return;
				else {
					System.out.println("wait for 20");
					Thread.sleep(20000);
					if(getEthernet(ip) != null) {
						deleteARPCache(ip);
					}
				}
				
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			}
		}
	}
	
	private void setTimer(_ARP_HEADER header) {
		
		Iterator<ARPTimer> iter = listArpTimer.iterator();
		boolean exist = false;
		while(iter.hasNext()) {
			ARPTimer currentTimer = iter.next();
			if(Arrays.equals(currentTimer.ip, header.ipTargetAddr.addr)){
					currentTimer.interrupt();
					iter.remove();
					exist = true;
			}
		}
		ARPTimer arpTimer = null;
		if(exist)
			arpTimer = new ARPTimer(header.ipTargetAddr.addr);
		else
			arpTimer = new ARPReceiveTimer(header.ipTargetAddr.addr);
		
		arpTimer.start();
		listArpTimer.add(arpTimer);
	}
	
	public boolean Send() {	
		byte[] header = arpHeader.makeHeader();
		
		addARPCache(arpHeader.ipTargetAddr.addr, NIL_ETHERNET);
		p_UnderLayer.Send(header,header.length);
		
		setTimer(arpHeader);
		
		return false;
	}
	
	private boolean isRequest(byte[] opcode) {
		return (opcode[0] == (byte)0x00) && (opcode[1] == (byte)0x01);
	}
	
	private boolean hasIPInProxyTable(byte[] receiveIP) {
		for(byte[] address : proxyTable.values()) {
			byte[] ip = new byte[4];
			for(int i = 0; i < 4; i++)
				ip[i] = address[i];
			// Proxy ARP에 수신한 메세지의 목적지 IP 정보가 있을 경우
			// 내 정보로 이더넷을 갱신하여 전송
			if(Arrays.equals(ip, receiveIP)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isMine(byte[] ipAddr) {
		for(int i = 0; i < 4; i++)
			if(ipAddr[i] != arpHeader.ipSenderAddr.addr[i])
				return false;
		return true;
	}
	
	private boolean isBroadCast(byte[] ethernetAddr) {
		for(int i = 0; i < 6; i++)
			if(ethernetAddr[i] != (byte)0xff)
				return false;
		return true;
	}
	
	@Override
	public boolean Receive(byte[] input) {
		
		_ARP_HEADER receivedHeader = new _ARP_HEADER(input);
		
		if(isMine(receivedHeader.ipSenderAddr.addr)) {// 메세지 전송자가 나인 경우 아무것도 하지 않음
			return true;
		}
		
		// 내가 보낸 ARP가 아니라면 ARP Table에 추가
		byte[] ip = new byte[4];
		for(int i = 0; i < 4; i++)
			ip[i] = receivedHeader.ipSenderAddr.addr[i];
		
		byte[] eth = new byte[6];
		for(int i = 0; i < 6; i++)
			eth[i] = receivedHeader.enetSenderAddr.addr[i];
		
		addARPCache(ip,eth);
		setTimer(receivedHeader);
		

		/*
		 * 1. ARP 요청이고
		 * 
		 * 1) 나에게 온 요청일 경우
		 * 2) 브로드캐스트이고 목적지가 내 프록시 테이블에 있을 경우 
		 * 
		 * 내 맥을 넣어서 답장
		 */
		if(isRequest(receivedHeader.opcode) && 
			(isMine(receivedHeader.ipTargetAddr.addr) || 
			   (isBroadCast(receivedHeader.enetTargetAddr.addr) && 
			    hasIPInProxyTable(receivedHeader.ipTargetAddr.addr)))) 
		{	
			receivedHeader.opcode[1] = 0x02; // make reply
			receivedHeader.enetTargetAddr = arpHeader.enetSenderAddr;
			
			// swap sender and target
			_IP_ADDR ipSender = receivedHeader.ipSenderAddr;
			_ETHERNET_ADDR ethSender = receivedHeader.enetSenderAddr;
			
			receivedHeader.ipSenderAddr = receivedHeader.ipTargetAddr;
			receivedHeader.enetSenderAddr = receivedHeader.enetTargetAddr;
			
			receivedHeader.ipTargetAddr = ipSender;
			receivedHeader.enetTargetAddr = ethSender;
			
			byte[] header = receivedHeader.makeHeader();
			p_UnderLayer.Send(header,header.length);	
		}
		
		return true;
	}
	
	private void updateARPCachePanel() {
		String[] stringData = new String[arpCacheTable.size()];
		for(int i = 0; i < stringData.length; i++)
			stringData[i] = arpCacheTable.get(i).toString();
		
		ARPCachePanel.ArpTable.removeAll();
		for(String str : stringData) {
			ARPCachePanel.ArpTable.add(str);
		}
	}
	
	
	public void setOpcode(byte[] opcode) {
		assert(opcode.length == 2);
		arpHeader.opcode[0] = opcode[0];
		arpHeader.opcode[1] = opcode[1];
	}
	
	public void setEthernetSenderAddress(byte[] ethernetAddress) {
		assert(ethernetAddress.length == 6);
		for(int i = 0; i < 6; i++)
			arpHeader.enetSenderAddr.addr[i] = ethernetAddress[i];
	}
	
	public void setIPSenderAddress(byte[] ipAddress) {
		assert(ipAddress.length == 4);
		for(int i = 0; i < 4; i++)
			arpHeader.ipSenderAddr.addr[i] = ipAddress[i];
	}
	
	public void setEthernetTargetAddress(byte[] ethernetAddress) {
		assert(ethernetAddress.length == 6);
		for(int i = 0; i < 6; i++)
			arpHeader.enetTargetAddr.addr[i] = ethernetAddress[i];
	}
	
	public void setIPTargetAddress(byte[] ipAddress) {
		assert(ipAddress.length == 4);
		for(int i = 0; i < 4; i++)
			arpHeader.ipTargetAddr.addr[i] = ipAddress[i];
	}
	
	
	/*
	 * ARP Cache Table Functions
	 * author : 박태현
	 */
	
	public boolean addARPCache(byte[] ip, byte[] ethernet) {
		
		Iterator<ARPCache> iter = arpCacheTable.iterator();
		
		while(iter.hasNext()) {
			ARPCache cache = iter.next();
			if(Arrays.equals(cache.ip.addr,ip)) {
				cache.setEthernet(ethernet);
				updateARPCachePanel();
				return false;
			}
		}
		
		arpCacheTable.add(new ARPCache(ip,ethernet));
		updateARPCachePanel();
		return true;
	}

	public void deleteARPCache(byte[] ip) {
		
		Iterator<ARPCache> iter = arpCacheTable.iterator();
		while(iter.hasNext()) {
			ARPCache arpCache = iter.next();
			if(Arrays.equals(arpCache.ip.addr,ip)) {
				arpCacheTable.remove(arpCache);
				updateARPCachePanel();
				return;
			}
		}
	}

	
	public byte[] getEthernet(byte[] ip) {
		if(ip != null && ip.length == 4)
			for(ARPCache arpCache : arpCacheTable) {
				if(Arrays.equals(arpCache.ip.addr,ip)) {
					return arpCache.ethernet.addr;
				}
			}
		return null; // not exist
	}
	
	
	/*
	 * proxy ARP 저장
	 * author : Hyoin
	 * key : device, value: IP Addr + MAC Addr
	 */
	public void setProxyTable(String device,byte[] ipAddress, byte[] ethernetAddress){
		assert(ipAddress.length == 4);
		assert(ethernetAddress.length == 6);
		
		byte [] proxy=new byte[10];
		for(int i=0;i<4;i++){
			proxy[i]=ipAddress[i];
		}
		for(int i=4;i<10;i++){
			proxy[i]=ethernetAddress[i-4];
		}
		proxyTable.put(device,proxy);		
	}
	
	/*
	 * proxy ARP 삭제
	 * author : Hyoin
	 * key : device, value: IP Addr + MAC Addr
	 */
	public void removeProxyTable(String deviceaddr){
		proxyTable.remove(deviceaddr);
		System.out.println(proxyTable.size());
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
