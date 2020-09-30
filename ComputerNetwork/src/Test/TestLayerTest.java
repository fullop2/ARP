package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;

/*
 * TestLayer Test
 * 
 *  테스트 레이어를 통해 어떻게 값을 검증하는지 간단한 예시입니다
 *  테스트 레이어에서 Send한 값과 Receive한 값이 잘 전달되는지 확인합니다
 *  
 *  실제 테스트에서는 test 부분에 검증을 위한 레이어를 넣고, 해당 레이어의 헤더 정보도 필요합니다
 */

class TestLayerTest {

	
	LayerManager layerManager;
	
	@BeforeEach
	void init(){
		
		// 레이어 계층을 레이어 매니저로 생성한다
		
		layerManager = new LayerManager();
		
		layerManager.AddLayer(new TestLayer("upper"));
		layerManager.AddLayer(new TestLayer("test"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *test ( *upper ) ) ");
	}
	
	/*
	 * Send의 경우, 하위 계층에 내려간 데이터의 값을 확인한다
	 * 테스트할 레이어에서 값을 잘 처리해서 헤더를 붙인 뒤 제대로 데이터를 생성해 
	 * 보냈는지 확인을 하위에서 할 수 있기 때문이다
	 * 
	 * Send로 보낸 데이터는 동일 컴퓨터 상에서 
	 * Send를 호출해 데이터를 보내므로 getSendMessage로 확인한다
	 */
	@Test
	void testSend() {
		String msg = "Hello World";
		byte[] byteMsg = msg.getBytes();
		layerManager.GetLayer("upper").Send(byteMsg,byteMsg.length);
		
		assertArrayEquals(byteMsg, ((TestLayer)layerManager.GetLayer("under")).getSendMessage());
		
	}
	
	/*
	 * Receive의 경우, 상위 계층으로 올라간 데이터의 값을 확인한다
	 * 테스트할 레이어에서 값을 잘 처리해서 헤더를 제거한 뒤 
	 * 보냈는지 확인을 상위에서 할 수 있기 때문이다
	 * 
	 * Receive로 보낸 데이터는 동일 컴퓨터 상에서 
	 * Receive를 호출해 데이터를 보내므로 getReceiveMessage로 확인한다
	 */
	@Test
	void testReceive() {
		String msg = "Hello World";
		byte[] byteMsg = msg.getBytes();
		layerManager.GetLayer("under").Receive(byteMsg);
		
		assertArrayEquals(byteMsg, ((TestLayer)layerManager.GetLayer("upper")).getReceiveMessage());
	}

}
