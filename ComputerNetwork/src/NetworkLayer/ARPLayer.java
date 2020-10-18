package NetworkLayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import EventHandlers.ARPTableEventHandlers;

public class ARPLayer implements BaseLayer {
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	// proxy arp table(key : device, value: IP Addr + MAC Addr)
	public HashMap<String,byte[]> proxyTable=new HashMap<String, byte[]>();
	
	// arp cache table
	private List<ARPCache> arpCacheTable = Collections.synchronizedList(new ArrayList<ARPCache>());
	
	private final byte[] NIL_ETHERNET = new byte[6];
	private final byte[] BROADCAST_ETHERNET = new byte[6];
	private final byte[] HW_TYPE_ETH = {0x00, 0x01};
	
	public class ARPCache{
		_IP_ADDR ip = new _IP_ADDR();
		_ETHERNET_ADDR ethernet = new _ETHERNET_ADDR();
		int timeToLive;
		
		public ARPCache(byte[] ip, byte[] ethernet) {
			setEthernet(ethernet);
			setIp(ip);
			
			if(ethernet == null || isNIL(ethernet))
				setTimeToLive(180000);
			else
				setTimeToLive(1200000);
		}
		
		public void setTimeToLive(int milliSecond) {
			timeToLive = milliSecond;
		}
		public void setEthernet(byte[] ethernet) {
			if(ethernet== null)
				return;
			assert(ethernet.length==6);
			System.arraycopy(ethernet, 0, this.ethernet.addr, 0, 6);
		}
		
		public void setIp(byte[] ip) {
			assert(ip.length == 4);
			System.arraycopy(ip, 0, this.ip.addr, 0, 4);
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
				for(int i = 0; i < 5; i++) {
					stringBuffer.append(String.format("%02X-", (ethernet.addr[i] & 0xff)).toUpperCase());
				}
				stringBuffer.append(String.format("%02X", (ethernet.addr[5] & 0xff)).toUpperCase());
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
			
			System.arraycopy(header, 8, enetSenderAddr.addr, 0, 6);
			System.arraycopy(header, 18, enetTargetAddr.addr, 0, 6);

			System.arraycopy(header, 14, ipSenderAddr.addr, 0, 4);
			System.arraycopy(header, 24, ipTargetAddr.addr, 0, 4);

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
			
			System.arraycopy(enetSenderAddr.addr, 0, header, 8, 6);
			System.arraycopy(enetTargetAddr.addr, 0, header, 18, 6);

			System.arraycopy(ipSenderAddr.addr, 0, header, 14, 4);
			System.arraycopy(ipTargetAddr.addr, 0, header, 24, 4);
			
			return header;
		}
	}

	_ARP_HEADER arpHeader = new _ARP_HEADER();
	
	
	public ARPLayer(String string) {
		pLayerName = string;
		
		for(int i = 0; i < 6; i++) {
			NIL_ETHERNET[i] = 0x00;
			BROADCAST_ETHERNET[i] = (byte)0xff;
		}
		
		new ARPTimer().start();
	}
  
	
	private boolean isRequest(byte[] opcode) {
		return (opcode[0] == (byte)0x00) && (opcode[1] == (byte)0x01);
	}
	
	/*
	 * Proxy ARP에 수신한 메세지의 목적지 IP 정보가 있을 경우
	 * 내 정보로 이더넷을 갱신하여 전송
	 */
	private boolean hasIPInProxyTable(byte[] receiveIP) {
		for(byte[] address : proxyTable.values()) {
			byte[] ip = new byte[4];
			System.arraycopy(address, 0, ip, 0, 4);
			if(Arrays.equals(ip, receiveIP)) {
				System.out.println("proxy Table에 해당 IP 존재");
				return true;
			}
		}
		return false;
	}
	
	private boolean isMine(byte[] ipAddr) {
		return Arrays.equals(arpHeader.ipSenderAddr.addr, ipAddr);
	}
	
	private boolean isNIL(byte[] ethernetAddr){
		return Arrays.equals(NIL_ETHERNET, ethernetAddr);
	}
	
	private boolean isGARP(_ARP_HEADER header){
		return Arrays.equals(header.ipSenderAddr.addr, header.ipTargetAddr.addr) && isNIL(header.enetTargetAddr.addr);
	}
	
	private boolean needProxy(_ARP_HEADER header){
		return hasIPInProxyTable(header.ipTargetAddr.addr) && isNIL(header.enetTargetAddr.addr);
	}
	
	class ARPTimer extends Thread {
		
		private long beforeTime;
		
		ARPTimer(){
			beforeTime = System.currentTimeMillis();
		}
		@Override
		public void run() {
			while(true) {
				try {
					Thread.sleep(1000);
					
					long timeElipse = System.currentTimeMillis() - beforeTime;
					beforeTime =  System.currentTimeMillis();
					
					Iterator<ARPCache> iter = arpCacheTable.iterator();
					while(iter.hasNext()) {
						ARPCache cache = iter.next();
						cache.timeToLive -= timeElipse;
						if(cache.timeToLive <= 0) {
							iter.remove();
							updateARPCachePanel();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public boolean Send(byte[] input, int length) {	
		
		if(!isMine(arpHeader.ipTargetAddr.addr)) {// 메세지 수신자가 나인 경우 : GARP
			addARPCache(arpHeader.ipTargetAddr.addr, NIL_ETHERNET);
		}
		
		byte[] header = arpHeader.makeHeader();
		p_UnderLayer.Send(header,header.length);
		
		System.out.println("Send ARP request");
		printARPInfo("Sender", arpHeader.ipSenderAddr.addr, arpHeader.enetSenderAddr.addr);
		printARPInfo("Target", arpHeader.ipTargetAddr.addr, arpHeader.enetTargetAddr.addr);
		System.out.println();
		return false;
	}

	
	@Override
	public synchronized boolean Receive(byte[] input) {
		
		
		
		_ARP_HEADER receivedHeader = new _ARP_HEADER(input);
		
		if(isMine(receivedHeader.ipSenderAddr.addr)) {// 메세지 전송자가 나인 경우 아무것도 하지 않음
			return true;
		}
		
		// 내가 보낸 ARP가 아니라면 ARP Table에 추가
		
		byte[] ip = new byte[4];
		System.arraycopy(receivedHeader.ipSenderAddr.addr, 0, ip, 0, 4);		
		byte[] eth = new byte[6];
		System.arraycopy(receivedHeader.enetSenderAddr.addr, 0, eth, 0, 6);
		
		
		addARPCache(ip,eth);
		
		System.out.println("Receive ARP Request");
		printARPInfo("Sender", receivedHeader.ipSenderAddr.addr, receivedHeader.enetSenderAddr.addr);
		printARPInfo("Target", receivedHeader.ipTargetAddr.addr, receivedHeader.enetTargetAddr.addr);
		
		/*
		 * 1. ARP 요청이고
		 * 
		 * 1) 나에게 온 요청일 경우
		 * 2) 프록싱 가능할 경우
		 * 3) GARP인 경우
		 * 
		 * 내 맥을 넣어서 답장
		 */
		if(isRequest(receivedHeader.opcode) && 
				(isMine(receivedHeader.ipTargetAddr.addr) || needProxy(receivedHeader)|| isGARP(receivedHeader))
		   )
		{	
			
			
			receivedHeader.opcode[1] = 0x02; // make reply
			receivedHeader.enetTargetAddr = arpHeader.enetSenderAddr;
			/*
			 * 일반 ARP 요청은 목적지가 수신한 ARP 메세지에 존재하므로 건드리지 않아도 됨
			 * 하지만 GARP의 경우 Target과 Sender가 동일함. 따라서 현재 자신의 IP 정보를 넣어서 답장을 해줘야 한다
			 */
			if(Arrays.equals(receivedHeader.ipTargetAddr.addr,receivedHeader.ipSenderAddr.addr)) {
				System.out.println("[ TYPE : GARP Request]\n");
				receivedHeader.ipTargetAddr = arpHeader.ipSenderAddr;
			}
			else {
				System.out.println("[ TYPE : ARP Request]\n");
			}

			// swap sender and target
			_IP_ADDR ipSender = receivedHeader.ipSenderAddr;
			_ETHERNET_ADDR ethSender = receivedHeader.enetSenderAddr;
			
			receivedHeader.ipSenderAddr = receivedHeader.ipTargetAddr;
			receivedHeader.enetSenderAddr = receivedHeader.enetTargetAddr;
			
			receivedHeader.ipTargetAddr = ipSender;
			receivedHeader.enetTargetAddr = ethSender;
			
			/* HW Type을 ARP 프로토콜 정보에서 알 수 있다 
			 * 따라서 ARP Layer에서 하위 레이어의 정보에 접근 가능 
			 */
			if(Arrays.equals(HW_TYPE_ETH,receivedHeader.hardwareType))  
				((EthernetLayer)p_UnderLayer).setDstEthernetAddress(receivedHeader.enetTargetAddr.addr);
			
			byte[] header = receivedHeader.makeHeader();
			p_UnderLayer.Send(header,header.length);	
			
			System.out.println("Send ARP Reply");
		}
		
		return true;
	}
	
	private void printARPInfo(String who, byte[] ip, byte[] eth) {
		System.out.print(who + " : [ ETH : ");
		for(int i = 0; i < 5; i++)
			System.out.print(String.format("%02X ", eth[i] & 0xff));
		System.out.print(String.format("%02X", eth[5] & 0xff));
		System.out.print(", IP : ");
		for(int i = 0; i < 3; i++)
			System.out.print(String.format("%d.", (int)(ip[i] & 0xff)));
		System.out.print(String.format("%d", (int)(ip[3] & 0xff)));
		System.out.println("]");
	}
	/*
	 * View Update
	 */
	private void updateARPCachePanel() {
		String[] stringData = new String[arpCacheTable.size()];
		for(int i = 0; i < stringData.length; i++)
			stringData[i] = arpCacheTable.get(i).toString();
		ARPTableEventHandlers.updateARPTable(stringData);
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
	
	private boolean addARPCache(byte[] ipAddr, byte[] ethe) {
		
		byte[] ip = new byte[4];
		System.arraycopy(ipAddr, 0, ip, 0, 4);
		
		byte[] ethernet = new byte[6];
		System.arraycopy(ethe, 0, ethernet, 0, 6);
		Iterator<ARPCache> iter = arpCacheTable.iterator();
		
		byte[] deletedEthernet = null;
		while(iter.hasNext()) {
			ARPCache cache = iter.next();
			if(Arrays.equals(cache.ip.addr,ip)) {
				printARPInfo("Remove Cache", cache.ip.addr, cache.ethernet.addr);
				deletedEthernet = new byte[6];
				System.arraycopy(cache.ethernet.addr, 0, deletedEthernet, 0, 6);
				iter.remove();
				break;
			}
		}
		if(isNIL(ethernet) && !isNIL(deletedEthernet))
			ethernet = deletedEthernet;
		arpCacheTable.add(new ARPCache(ip,ethernet));
		
		iter = arpCacheTable.iterator();
		while(iter.hasNext()) {
			ARPCache cache = iter.next();
			byte[] eth = cache.ethernet.addr;
			byte[] ipp = cache.ip.addr;
			if(eth == null) eth = new byte[6];
			printARPInfo("ARP Cache", ipp, eth);
		}
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
