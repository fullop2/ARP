package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import NetworkLayer.ARPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;

class ARPLayerTest {

	LayerManager layerManager;
	
	byte[] ip1,ip2,ip3;
	byte[] eth1,eth2,eth3;
	
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
	}
	
	void setEthernet(byte[] eth, byte addr) {
		eth = new byte[6];
		eth[0] = addr; eth[1] = addr; eth[2] = addr; 
		eth[3] = addr; eth[4] = addr; eth[5] = addr;
	}
	
	@Test
	void testARPTable() {
		ARPLayer layer = ((ARPLayer)layerManager.GetLayer("test"));
		layer.addARPCache(ip1, null);
		
		assertEquals(null, layer.getEthernet(ip1));
	}
	
	
	@Disabled
	@Test
	void testSend() {
		String msg = "Hello World";
		byte[] byteMsg = msg.getBytes();
		layerManager.GetLayer("upper").Send(byteMsg,byteMsg.length);
		
		assertArrayEquals(byteMsg, ((TestLayer)layerManager.GetLayer("under")).getSendMessage());
		
	}
	
	@Disabled
	@Test
	void testReceive() {
		String msg = "Hello World";
		byte[] byteMsg = msg.getBytes();
		layerManager.GetLayer("under").Receive(byteMsg);
		
		assertArrayEquals(byteMsg, ((TestLayer)layerManager.GetLayer("upper")).getReceiveMessage());
	}

}
