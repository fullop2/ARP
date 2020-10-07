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
	
	byte[] ip1 = new byte[4];
	byte[] ip2 = new byte[4];
	byte[] ip3 = new byte[4];
	
	
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("upper"));
		layerManager.AddLayer(new IPLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *upper ) ) ");
		
		ip1[0] = (byte)192;
		ip1[1] = (byte)168;
		ip1[2] = (byte)0;
		ip1[3] = (byte)1;
		
		ip2[0] = (byte)192;
		ip2[1] = (byte)168;
		ip2[2] = (byte)0;
		ip2[3] = (byte)2;
		
		ip3[0] = (byte)192;
		ip3[1] = (byte)168;
		ip3[2] = (byte)0;
		ip3[3] = (byte)3;
	}
	
	@Test
	void testSend() {
		String msg = "Hello World";
		byte[] byteMsg = msg.getBytes();
		

		/*
		 * sender : ip1 -> ip2
		 * 
		 * normal send
		 */
		byte[] packet = makePacket(ip1,ip2,byteMsg);
		((IPLayer)layerManager.GetLayer("test")).setIPDstAddr(ip2);
		((IPLayer)layerManager.GetLayer("test")).setIPSrcAddr(ip1);
			
		layerManager.GetLayer("upper").Send(byteMsg,byteMsg.length);
				
		byte[] send = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();
		assertArrayEquals(packet, send);
		
	}	
	
	@Test
	void testReceive() {
		String msg = "Bye World";
		byte[] byteMsg = msg.getBytes();
				
		/*
		 * sender : ip1 -> ip2
		 * receiver : ip2
		 * 
		 * normal receive
		 */	
		byte[] packet = makePacket(ip1,ip2,byteMsg);
		((IPLayer)layerManager.GetLayer("test")).setIPSrcAddr(ip2); 
		
		layerManager.GetLayer("under").Receive(packet);
		
		byte[] receive = ((TestLayer)layerManager.GetLayer("upper")).getReceiveMessage();
		assertArrayEquals(byteMsg, receive);
	}
	
	@Test
	void testReceiveSelf() {
		String msg = "Bye World";
		byte[] byteMsg = msg.getBytes();
		
		/*
		 * sender : ip1 -> ip2
		 * receiver : ip1
		 * 
		 * self receive
		 */
		byte[] packet = makePacket(ip1,ip2,byteMsg);
		((IPLayer)layerManager.GetLayer("test")).setIPSrcAddr(ip1); 

		layerManager.GetLayer("under").Receive(packet);
		
		byte[] receive = ((TestLayer)layerManager.GetLayer("upper")).getReceiveMessage();
		assertArrayEquals(null, receive);
	}
	
	@Test 
	void testReceiveOtherHost(){
		String msg = "Bye World";
		byte[] byteMsg = msg.getBytes();
		/*
		 * sender : ip1 -> ip2
		 * receiver : ip3
		 * 
		 * other host receive
		 */
		byte[] packet = makePacket(ip1,ip2,byteMsg);
		((IPLayer)layerManager.GetLayer("test")).setIPSrcAddr(ip3); 

		layerManager.GetLayer("under").Receive(packet);
		
		byte[] receive = ((TestLayer)layerManager.GetLayer("upper")).getReceiveMessage();
		assertArrayEquals(null, receive);
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
