package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import NetworkLayer.IPLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;

class IPLayerTest {

	LayerManager layerManager;
	
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("upper"));
		layerManager.AddLayer(new IPLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *upper ) ) ");
	}

	@Test
	void testSend() {
		String msg = "Hello World";
		
		byte[] src = new byte[4];
		byte[] dst = new byte[4];
		
		src[0] = (byte)192;
		src[1] = (byte)168;
		src[2] = (byte)0;
		src[3] = (byte)1;
		
		dst[0] = (byte)192;
		dst[1] = (byte)168;
		dst[2] = (byte)0;
		dst[3] = (byte)2;
		
		byte[] byteMsg = msg.getBytes();
		
		int length = byteMsg.length + 20;
		byte[] header = new byte[20];
		header[0] = 0x45;
		header[2] = (byte)((length >> 8) & 0xff);
		header[3] = (byte)(length & 0xff);
		
		header[10] = 0x7F;
		header[11] = 0x06;
		
		for(int i = 0; i < 4; i++) {
			header[12+i] = dst[i];
			header[16+i] = src[i];
		}
		
		((IPLayer)layerManager.GetLayer("test")).setIPDstAddr(src);
		((IPLayer)layerManager.GetLayer("test")).setIPSrcAddr(dst);
		
		
		layerManager.GetLayer("upper").Send(byteMsg,byteMsg.length);
		
		byte[] packet = new byte[length];
		System.arraycopy(header, 0, packet, 0, 20);
		System.arraycopy(byteMsg, 0, packet, 20, byteMsg.length);
		
		assertArrayEquals(packet, ((TestLayer)layerManager.GetLayer("under")).getSendMessage());
		
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
