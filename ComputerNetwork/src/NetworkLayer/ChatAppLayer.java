package NetworkLayer;

import java.util.ArrayList;

import Application.ApplicationController;
import EventHandlers.ChatEventHandler;

public class ChatAppLayer implements BaseLayer{
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	private class _CAPP_HEADER{
		byte[] capp_totlen;
		byte capp_type;
		byte capp_unused;
		byte[] capp_data;
		
		public _CAPP_HEADER(){
			this.capp_totlen = new byte[2];
			this.capp_type = 0x00;
			this.capp_unused = 0x00;
			this.capp_data = null;
		}
	}
	
	_CAPP_HEADER m_sHeader = new _CAPP_HEADER();
	
	public ChatAppLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
		ResetHeader();
	}
	
	public void ResetHeader(){
		for(int i=0; i<2; i++){
			m_sHeader.capp_totlen[i] = (byte) 0x00;
		}
		m_sHeader.capp_type = (byte) 0x00;	
		m_sHeader.capp_unused = (byte) 0x00;	
		m_sHeader.capp_data = null;	
	}
	
	public byte[] ObjToByte(_CAPP_HEADER Header, byte[] input, int length){
	
		return null;		
	}
	
	public boolean Send(byte[] input, int length) {     	 
    	if(length == 0) return false;
    	System.out.println("Send Msg");
		return p_UnderLayer.Send(input,length);
	}

           
	public boolean Receive(byte[] input){
		
		ChatEventHandler.receiveMsg(new String(input));
		return true;
	}	 

	@Override
	public void SetUnderLayer(BaseLayer pUnderLayer) {
		// TODO Auto-generated method stub
		if (pUnderLayer == null)
			return;
		this.p_UnderLayer = pUnderLayer;
	}

	@Override
	public void SetUpperLayer(BaseLayer pUpperLayer) {
		// TODO Auto-generated method stub
		if (pUpperLayer == null)
			return;
		this.p_aUpperLayer.add(nUpperLayerCount++, pUpperLayer);
		// nUpperLayerCount++;
	}

	@Override
	public String GetLayerName() {
		// TODO Auto-generated method stub
		return pLayerName;
	}

	@Override
	public BaseLayer GetUnderLayer() {
		// TODO Auto-generated method stub
		if (p_UnderLayer == null)
			return null;
		return p_UnderLayer;
	}

	@Override
	public BaseLayer GetUpperLayer(int nindex) {
		// TODO Auto-generated method stub
		if (nindex < 0 || nindex > nUpperLayerCount || nUpperLayerCount < 0)
			return null;
		return p_aUpperLayer.get(nindex);
	}

	@Override
	public void SetUpperUnderLayer(BaseLayer pUULayer) {
		this.SetUpperLayer(pUULayer);
		pUULayer.SetUnderLayer(this);

	}

}
