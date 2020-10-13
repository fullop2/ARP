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
	
	byte[] ip1 = {(byte)192,(byte)168,(byte)0,(byte)1};
	byte[] ip2 = {(byte)192,(byte)168,(byte)0,(byte)2};
	byte[] ip3 = {(byte)192,(byte)168,(byte)0,(byte)3};
	byte[] eth1 = {(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA};
	byte[] eth2 = {(byte)0xBB,(byte)0xBB,(byte)0xBB,(byte)0xBB,(byte)0xBB,(byte)0xBB};
	byte[] eth3 = {(byte)0xCC,(byte)0xCC,(byte)0xCC,(byte)0xCC,(byte)0xCC,(byte)0xCC};
	byte[] ethNull = {0x00,0x00,0x00,0x00,0x00,0x00};
	byte[] ethBroadCast = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
	
	AppView view = new AppView(false);
	
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("upper"));
		layerManager.AddLayer(new ARPLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *upper ) ) ");
		
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
	
	
	// ARP Request 송신 확인
	@Test
	void testSend() {

		byte[] header = makeHeader(ip1,ip2,eth1,ethNull,(byte) 0x1);
		
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip2);
		layer.Send(null,0);
		
		
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

	
	// GARP Request 송신 확인
	@Test
	void testSendGARP() {

		byte[] header = makeHeader(ip1,ip1,eth1,ethNull,(byte) 0x1);
		
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip1);
		layer.Send(null,0);
		
		
		byte[] data = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();	
		assertArrayEquals(header,data);
		
	}
	
	// GARP Request 수신 확인
	@Test
	void testReceiveGARP() {
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		
		layer.setEthernetSenderAddress(eth2);
		layer.setIPSenderAddress(ip2);
		
		byte[] header = makeHeader(ip1, ip1, eth1, ethNull, (byte)0x01); 
					
		layer.Receive(header);
		
		byte[] eth = layer.getEthernet(ip1);	
		assertArrayEquals(eth1,eth);	
		
		byte[] sendData = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();	
		
		byte[] replyHeader = makeHeader(ip2, ip1, eth2, eth1, (byte)0x02); 
		
		assertArrayEquals(replyHeader,sendData);
	}
	
	// ARP Request 송신 후 ARP Reply 수신 확인
	@Test
	void testSettingAndReceive() {
		
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		layer.setEthernetSenderAddress(eth1);
		layer.setIPSenderAddress(ip1);
		layer.setIPTargetAddress(ip2);
		
		byte[] header = makeHeader(ip1, ip2, eth1, ethNull, (byte)0x1);
		layer.Send(null,0);
		
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


}
