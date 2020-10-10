package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import NetworkLayer.EthernetLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;

class EthernetLayerTest {
public LayerManager layerManager;
	
	byte[] eth1 = {(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA,(byte)0xAA};
	byte[] eth2 = {(byte)0xBB,(byte)0xBB,(byte)0xBB,(byte)0xBB,(byte)0xBB,(byte)0xBB};
	byte[] eth3 = {(byte)0xCC,(byte)0xCC,(byte)0xCC,(byte)0xCC,(byte)0xCC,(byte)0xCC};
	byte[] ethNull = {0x00,0x00,0x00,0x00,0x00,0x00};
	byte[] ethBroadCast = {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
	
	byte[] typeIPv4 = {0x08,0x00};
	byte[] typeARP = {0x08,0x06};
	
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("IP"));
		layerManager.AddLayer(new TestLayer("ARP"));
		layerManager.AddLayer(new EthernetLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *IP *ARP ) ) ");
		
	}
	
	void setEthernet(byte[] eth, byte addr) {
		eth[0] = addr; eth[1] = addr; eth[2] = addr; 
		eth[3] = addr; eth[4] = addr; eth[5] = addr;
	}
	
	public byte[] makeHeader(byte[] destEth, byte[] sourceEth, byte[] ethType) {
		byte[] header = new byte[14];
		System.arraycopy(destEth, 0, header, 0, 6);
		System.arraycopy(sourceEth, 0, header, 6, 6);
		header[12] = ethType[0];
		header[13] = ethType[1];
		
		return header;
	}
	
	@Test
	public void testSendARP() {

		TestLayer arpLayer = (TestLayer)layerManager.GetLayer("ARP");
		byte[] msg = "Hello".getBytes();
		
		EthernetLayer ethernetLayer = (EthernetLayer)layerManager.GetLayer("test");
		ethernetLayer.setSrcEthernetAddress(eth1);
		ethernetLayer.setDstEthernetAddress(ethBroadCast);
		ethernetLayer.setEthernetType(typeARP);
		
		arpLayer.Send(msg, msg.length);
		
		TestLayer underLayer = (TestLayer)layerManager.GetLayer("under");
		byte[] sendMessage = underLayer.getSendMessage();
		
		byte[] frame = new byte[14+msg.length];
		byte[] header = makeHeader(ethBroadCast,eth1,typeARP);
		
		System.arraycopy(header, 0, frame, 0, 14);
		System.arraycopy(msg, 0, frame, 14, msg.length);
		
		assertArrayEquals(frame, sendMessage);
	}
	
	@Test
	public void testSendIP() {

		TestLayer arpLayer = (TestLayer)layerManager.GetLayer("IP");
		byte[] msg = "Hello".getBytes();
		
		EthernetLayer ethernetLayer = (EthernetLayer)layerManager.GetLayer("test");
		ethernetLayer.setSrcEthernetAddress(eth1);
		ethernetLayer.setDstEthernetAddress(eth2);
		ethernetLayer.setEthernetType(typeIPv4);
		
		arpLayer.Send(msg, msg.length);
		
		TestLayer underLayer = (TestLayer)layerManager.GetLayer("under");
		byte[] sendMessage = underLayer.getSendMessage();
		
		byte[] frame = new byte[14+msg.length];
		byte[] header = makeHeader(eth2,eth1,typeIPv4);
		
		System.arraycopy(header, 0, frame, 0, 14);
		System.arraycopy(msg, 0, frame, 14, msg.length);
		
		assertArrayEquals(frame, sendMessage);
	}
	
	@Test
	public void testReceiveARP() {

		byte[] msg = "Hello".getBytes();
		byte[] frame = new byte[14+msg.length];
		byte[] header = makeHeader(ethBroadCast,eth1,typeARP);
		
		EthernetLayer ethernetLayer = (EthernetLayer)layerManager.GetLayer("test");
		ethernetLayer.setSrcEthernetAddress(eth1);
		
		System.arraycopy(header, 0, frame, 0, 14);
		System.arraycopy(msg, 0, frame, 14, msg.length);

		TestLayer underLayer = (TestLayer)layerManager.GetLayer("under");
		underLayer.Receive(frame);
		
		TestLayer arpLayer = (TestLayer)layerManager.GetLayer("ARP");
		
		byte[] receiveMessage = arpLayer.getReceiveMessage();
		
		assertArrayEquals(msg, receiveMessage);
	}
	
	@Test
	public void testReceiveIP() {

		byte[] msg = "Hello".getBytes();
		byte[] frame = new byte[14+msg.length];
		byte[] header = makeHeader(eth1,eth2,typeIPv4);
		
		EthernetLayer ethernetLayer = (EthernetLayer)layerManager.GetLayer("test");
		ethernetLayer.setSrcEthernetAddress(eth1);
		
		System.arraycopy(header, 0, frame, 0, 14);
		System.arraycopy(msg, 0, frame, 14, msg.length);

		TestLayer underLayer = (TestLayer)layerManager.GetLayer("under");
		underLayer.Receive(frame);
		
		TestLayer arpLayer = (TestLayer)layerManager.GetLayer("IP");
		
		byte[] receiveMessage = arpLayer.getReceiveMessage();
		
		assertArrayEquals(msg, receiveMessage);
	}
}
