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
				for(int i = 0; i < 4; i++)
					stringBuffer.append(ip.addr[i]);
				stringBuffer.append("completed\n");
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
				Thread.sleep(3000);
				if(Arrays.equals(NIL_ETHERNET,getEthernet(ip))) {
					deleteARPCache(ip);
				}
				else {
					System.out.println("wait for 17");
					Thread.sleep(17000);
					if(getEthernet(ip) != null) {
						deleteARPCache(ip);
					}
				}
				
			} catch (InterruptedException e) {
				System.out.println("interrupted");
			}
		}
	}
	
	public boolean Send() {	
		byte[] header = arpHeader.makeHeader();
		
		p_UnderLayer.Send(header,header.length);
		
		ARPTimer arpTimer = new ARPTimer(arpHeader.ipTargetAddr.addr);
		Iterator<ARPTimer> iter = listArpTimer.iterator();
		while(iter.hasNext()) {
			ARPTimer currentTimer = iter.next();
			if(Arrays.equals(currentTimer.ip, arpHeader.ipTargetAddr.addr)){
					currentTimer.interrupt();
					iter.remove();
			}
		}
		arpTimer.start();
		listArpTimer.add(arpTimer);
		
		return false;
	}
	
	private boolean isRequest(byte[] opcode) {
		return (opcode[0] == (byte)0x00) && (opcode[1] == (byte)0x01);
	}
	
	private boolean isMine(byte[] ipAddr) {
		for(int i = 0; i < 4; i++)
			if(ipAddr[i] != arpHeader.ipSenderAddr.addr[i])
				return false;
		return true;
	}
	
	@Override
	public boolean Receive(byte[] input) {
		
		_ARP_HEADER receivedHeader = new _ARP_HEADER(input);
		if(isMine(receivedHeader.ipSenderAddr.addr)) {// 메세지 전송자가 나인 경우
			return true;
		}
		else {
			byte[] ip = new byte[4];
			for(int i = 0; i < 4; i++)
				ip[i] = receivedHeader.ipSenderAddr.addr[i];
			
			byte[] eth = new byte[6];
			for(int i = 0; i < 6; i++)
				eth[i] = receivedHeader.enetSenderAddr.addr[i];
			
			addARPCache(ip,eth);

		}
		
		// 내게 온 요청인 경우
		if(isRequest(receivedHeader.opcode) && isMine(receivedHeader.ipTargetAddr.addr)) {
			
			receivedHeader.opcode[1] = 0x02; // make reply
			
			// swap sender and target
			receivedHeader.enetTargetAddr = arpHeader.enetSenderAddr;
			
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
