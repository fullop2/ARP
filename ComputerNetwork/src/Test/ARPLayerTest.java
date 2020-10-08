package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import NetworkLayer.ARPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;

class ARPLayerTest {

LayerManager layerManager;
	
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("upper"));
		layerManager.AddLayer(new ARPLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *upper ) ) ");
	}
	
	@Test
	void testSend() {
		String msg = "Hello World";
		byte[] byteMsg = msg.getBytes();
		layerManager.GetLayer("upper").Send(byteMsg,byteMsg.length);
		
		assertArrayEquals(byteMsg, ((TestLayer)layerManager.GetLayer("under")).getSendMessage());
		
	}
	
	@Test
	void testReceive() {
		String msg = "Hello World";
		byte[] byteMsg = msg.getBytes();
		layerManager.GetLayer("under").Receive(byteMsg);
		
		assertArrayEquals(byteMsg, ((TestLayer)layerManager.GetLayer("upper")).getReceiveMessage());
	}

}
