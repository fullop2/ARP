package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import NetworkLayer.ARPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;
import View.AppView;

class ARPLayerTest {

	public LayerManager layerManager;
	
	public byte[] ip1,ip2,ip3;
	public byte[] eth1,eth2,eth3,ethNull,ethBroadCast;
	
	AppView view = new AppView(false);
	
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("upper"));
		layerManager.AddLayer(new ARPLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *upper ) ) ");
		
		ip1=new byte[4];
		ip1[0] = (byte)192; ip1[1] = (byte)168; ip1[2] = 0; ip1[3] = 1;
		
		ip2=new byte[4];
		ip2[0] = (byte)192; ip2[1] = (byte)168; ip2[2] = 0; ip2[3] = 2;
		
		ip3=new byte[4];
		ip3[0] = (byte)192; ip3[1] = (byte)168; ip3[2] = 0; ip3[3] = 3;
		
		eth1 = new byte[6];
		setEthernet(eth1,(byte)0xAA);
		
		eth2 = new byte[6];
		setEthernet(eth2,(byte)0xBB);
		
		eth3 = new byte[6];
		setEthernet(eth3,(byte)0xCC);
		
		ethNull = new byte[6];
		setEthernet(ethNull,(byte)0);
		
		ethBroadCast = new byte[6];
		setEthernet(ethBroadCast, (byte)0xff);
	}
	
	void setEthernet(byte[] eth, byte addr) {
		eth[0] = addr; eth[1] = addr; eth[2] = addr; 
		eth[3] = addr; eth[4] = addr; eth[5] = addr;
	}
	
	public byte[] makeHeader(byte[] senderIP, byte[] targetIP, byte[] senderEth, byte[] targetEth, byte opcode1) {
		byte[] header = new byte[28];
		header[0] = 0x00; 
		header[1] = 0x01;
		header[2] = 0x08;
		header[3] = 0x00;
		header[4] = 0x06;
		header[5] = 0x04;
		header[6] = 0x00; 
		header[7] = opcode1; // request
		for(int i=0; i <6; i++) {
			header[8+i] =  senderEth[i]; // sender
			header[18+i] = targetEth[i]; // target
		}
		for(int i=0; i <4; i++) {
			header[14+i] = senderIP[i]; // sender
			header[24+i] = targetIP[i]; // target
		}
		return header;
	}
	
	// ARP Table Function 동작 확인
	@Test
	void testARPTableBasicFlow() {
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		layer.addARPCache(ip1, null);		
		assertArrayEquals(ethNull, layer.getEthernet(ip1));
		layer.addARPCache(ip1, eth1);
		assertArrayEquals(eth1, layer.getEthernet(ip1));
		layer.deleteARPCache(ip1);
		assertArrayEquals(null, layer.getEthernet(ip1));
	}
	
	// ARP Request 송신 확인
	@Test
	void testSend() {

		byte[] header = makeHeader(ip1,ip2,eth1,ethNull,(byte) 0x1);
		
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip2);
		layer.Send();
		
		
		byte[] data = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();	
		assertArrayEquals(header,data);
		
	}
	
	// ARP Request 수신 확인
	@Test
	void testJustReceive() {
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip2);
		
		byte[] header = makeHeader(ip2, ip1, eth2, eth1, (byte)0x01); 
					
		layer.Receive(header);
		
		byte[] eth = layer.getEthernet(ip2);	
		assertArrayEquals(eth2,eth);			
	}

	// ARP Request 송신 후 ARP Reply 수신 확인
	@Test
	void testSettingAndReceive() {
		
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip2);
		
		byte[] header = makeHeader(ip1, ip2, eth1, ethNull, (byte)0x1);
		layer.Send();
		
		byte[] sendData = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();	
		assertArrayEquals(header,sendData);
		
		header = makeHeader(ip2, ip1, eth2, eth1, (byte)0x2); // reply
		layer.Receive(header);
		
		byte[] eth = layer.getEthernet(ip2);	
		assertArrayEquals(eth2,eth);	
	}
	
	
	
	// ARP Request 수신 및 ARP Reply 확인
	@Test
	void testReceiveRequest() {
		
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip2);
		
		byte[] header = makeHeader(ip2, ip1, eth2, ethNull, (byte)0x1); // request
		layer.Receive(header);
		
		byte[] eth = layer.getEthernet(ip2);	
		assertArrayEquals(eth2,eth);	
		
		header = makeHeader(ip1, ip2, eth1, eth2, (byte)0x2); // reply
		
		TestLayer under = ((TestLayer)layerManager.GetLayer("under"));	
		byte[] sendData = under.getSendMessage();
		
		assertArrayEquals(header, sendData); // reply is valid
	}

	@Test
	void testReceiveARPinProxy() throws InterruptedException {
		
		view.setVisible(true);
		
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip2);
		
		layer.setProxyTable("0", ip3, eth3);
		
		byte[] header = makeHeader(ip2, ip3, eth2, ethBroadCast, (byte)0x1); // request
		layer.Receive(header);
		
		byte[] eth = layer.getEthernet(ip2);	
		assertArrayEquals(eth2,eth);	
		
		header = makeHeader(ip3, ip2, eth1, eth2, (byte)0x2); // reply
		
		TestLayer under = ((TestLayer)layerManager.GetLayer("under"));	
		byte[] sendData = under.getSendMessage();
		
		assertArrayEquals(header, sendData); // reply is valid
		
		Thread.sleep(3000);
	}
}
