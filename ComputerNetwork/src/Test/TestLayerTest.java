package Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import NetworkLayer.EthernetLayer;
import NetworkLayer.LayerManager;
import NetworkLayer.TestLayer;

class TestLayerTest {

	
	LayerManager layerManager = new LayerManager();
	
	@BeforeAll
	void init(){
		layerManager.AddLayer(new TestLayer("upper"));
		layerManager.AddLayer(new EthernetLayer("ethernet"));
		layerManager.AddLayer(new TestLayer("under"));
		
		layerManager.ConnectLayers("under ( *ethernet ( *upper ) ) ");
	}
	
	@Test
	void test() {
		fail("Not yet implemented");
	}

}
