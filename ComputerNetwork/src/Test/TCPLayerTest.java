package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import NetworkLayer.LayerManager;
import NetworkLayer.TCPLayer;
import NetworkLayer.TestLayer;
import View.AppView;

class TCPLayerTest {

public LayerManager layerManager;
	
	byte[] chatPort = { 0x20, 0x10};
	byte[] filePort = { 0x20, 0x20};
	byte[] nilPort = { 0x00, 0x00};
	
	AppView view = new AppView();
	
	@BeforeEach
	void init(){
		view.setVisible(false);
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("Chat"));
		layerManager.AddLayer(new TestLayer("File"));
		layerManager.AddLayer(new TCPLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *Chat *File ) ) ");

	}

	
	public byte[] makeHeader(byte[] srcPort, byte[] dstPort) {
		byte[] header = new byte[20];
		header[0] = srcPort[0];
		header[1] = srcPort[1];
		header[2] = dstPort[0];
		header[3] = dstPort[1];
		header[12] = (byte)(((5<< 4) & 0xf0));
		
		return header;
	}
	
	
	@Test
	void testSendARP() {

		byte[] header = makeHeader(nilPort,nilPort);
		
		TCPLayer layer = ((TCPLayer)layerManager.GetLayer("test"));
		layer.setPort(nilPort);
		layer.Send(null,0);
		
		byte[] data = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();	
		assertArrayEquals(null,data);
		
	}

	@Test
	void testSendChat() {

		byte[] header = makeHeader(chatPort,chatPort);
		byte[] msg = "Hello".getBytes();
		byte[] tcpMsg = new byte[header.length+msg.length];
		System.arraycopy(header, 0, tcpMsg, 0, header.length);
		System.arraycopy(msg, 0, tcpMsg, 20, msg.length);
		
		TCPLayer layer = ((TCPLayer)layerManager.GetLayer("test"));
		layer.setPort(chatPort);
		
		layer.Send(msg,msg.length);
		
		byte[] data = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();	
		assertArrayEquals(tcpMsg,data);
		
	}
	
	@Test
	void testSendFile() {

		byte[] header = makeHeader(filePort,filePort);
		byte[] msg = "Hello".getBytes();
		byte[] tcpMsg = new byte[header.length+msg.length];
		System.arraycopy(header, 0, tcpMsg, 0, header.length);
		System.arraycopy(msg, 0, tcpMsg, 20, msg.length);
		
		TCPLayer layer = ((TCPLayer)layerManager.GetLayer("test"));
		layer.setPort(filePort);
		
		layer.Send(msg,msg.length);
		
		byte[] data = ((TestLayer)layerManager.GetLayer("under")).getSendMessage();	
		assertArrayEquals(tcpMsg,data);
		
	}
}
