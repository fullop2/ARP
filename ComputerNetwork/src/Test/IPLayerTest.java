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
		byte[] byteMsg = msg.getBytes();
		
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
		
		byte[] packet = makePacket(src,dst,byteMsg);

		((IPLayer)layerManager.GetLayer("test")).setIPDstAddr(dst);
		((IPLayer)layerManager.GetLayer("test")).setIPSrcAddr(src);
			
		layerManager.GetLayer("upper").Send(byteMsg,byteMsg.length);
				
		byte[] send = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();
		assertArrayEquals(packet, send);
		
	}	
	
	@Test
	void testReceive() {
		String msg = "Bye World";
		byte[] byteMsg = msg.getBytes();
		
		byte[] src = new byte[4];
		byte[] dst = new byte[4];
		
		src[0] = (byte)192;
		src[1] = (byte)168;
		src[2] = (byte)0;
		src[3] = (byte)4;
		
		dst[0] = (byte)192;
		dst[1] = (byte)168;
		dst[2] = (byte)0;
		dst[3] = (byte)5;
		
		byte[] packet = makePacket(src,dst,byteMsg);
		
		// 수신자 테스트이므로 src와 dst가 반대
		((IPLayer)layerManager.GetLayer("test")).setIPDstAddr(src);
		((IPLayer)layerManager.GetLayer("test")).setIPSrcAddr(dst); 

		
		layerManager.GetLayer("under").Receive(packet);
		
		byte[] send = ((TestLayer)layerManager.GetLayer("upper")).getReceiveMessage();
		assertArrayEquals(byteMsg, send);
	}
	
	byte[] makePacket(byte[] src, byte[] dst, byte[] byteMsg) {		
		int length = byteMsg.length + 20;
		byte[] header = new byte[20];
		header[0] = 0x45;
		header[2] = (byte)((length >> 8) & 0xff);
		header[3] = (byte)(length & 0xff);
		header[6] = (byte) (((0x02 << 5) & 0xe0) | (0x00 & 0x5)); 
		header[10] = 0x7F;
		header[11] = 0x06;
		
		for(int i = 0; i < 4; i++) {
			header[12+i] = dst[i];
			header[16+i] = src[i];
		}
		
		byte[] packet = new byte[length];
		System.arraycopy(header, 0, packet, 0, 20);
		System.arraycopy(byteMsg, 0, packet, 20, byteMsg.length);
		
		return packet;
	}
	
}
