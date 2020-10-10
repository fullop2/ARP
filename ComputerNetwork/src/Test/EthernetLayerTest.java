package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import NetworkLayer.EthernetLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;

class EthernetLayerTest {
public LayerManager layerManager;
	
	public byte[] eth1,eth2,eth3,ethNull,ethBroadCast;
	
	public byte[] typeIPv4, typeARP;
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("IP"));
		layerManager.AddLayer(new TestLayer("ARP"));
		layerManager.AddLayer(new EthernetLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *IP *ARP ) ) ");
		
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
		
		typeIPv4 = new byte[2];
		typeIPv4[0] = (byte)0x08;
		typeIPv4[1] = (byte)0x00;
		
		typeARP = new byte[2];
		typeARP[0] = (byte)0x08;
		typeARP[1] = (byte)0x06;
	}
	
	void setEthernet(byte[] eth, byte addr) {
		eth[0] = addr; eth[1] = addr; eth[2] = addr; 
		eth[3] = addr; eth[4] = addr; eth[5] = addr;
	}
	
	public byte[] makeHeader(byte[] destEth, byte[] sourceEth, byte[] ethType) {
		byte[] header = new byte[14];

		for(int i=0; i < 6; i++) {
			header[i] = destEth[i]; 
			header[6+i] = sourceEth[i];
		}
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
}
