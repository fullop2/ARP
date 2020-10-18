package NetworkLayer;

import java.util.ArrayList;

import Application.ApplicationController;
import EventHandlers.ChatEventHandler;
import View.ChatPanel;

public class ChatAppLayer implements BaseLayer{
	public int nUpperLayerCount = 0;
	public String pLayerName = null;
	public BaseLayer p_UnderLayer = null;
	public ArrayList<BaseLayer> p_aUpperLayer = new ArrayList<BaseLayer>();
	
	private class _CAPP_HEADER{
		byte[] ip;			// 0-3
		byte nicknameLen;	// 4
		byte[] nickname;	// 5-14
		byte[] msgLen;		// 15-16
		byte[] msg;			// 17-
		
		public _CAPP_HEADER(){
			ip = new byte[4];
			nickname = new byte[10];
			msgLen = new byte[2];
		}
		
		public _CAPP_HEADER(byte[] header) {
			this();
			int msgLength = (int)(header[15] << 8) + (int)(header[16]);
			
			System.arraycopy(header, 0, ip, 0, 4);
			nicknameLen = header[4];
			System.arraycopy(header, 5, nickname, 0, nicknameLen);
			msgLen[0] = header[15];
			msgLen[1] = header[16];
			msg = new byte[msgLength];
			System.arraycopy(header, 17, msg, 0, msgLength);
			
		}
		
		byte[] makeHeader() {
			int msgLength = (int)((msgLen[0] << 8) | (msgLen[1]));
			System.out.println(msgLength);
			
			byte[] header = new byte[17+msgLength];
			System.arraycopy(ip, 0, header, 0, 4);
			header[4] = nicknameLen;
			System.arraycopy(nickname, 0, header, 5, 10);
			header[15] = msgLen[0];
			header[16] = msgLen[1];
			System.arraycopy(msg, 0, header, 17, msgLength);
			return header;
		}
	}
	
	_CAPP_HEADER chatHeader = new _CAPP_HEADER();
	
	public ChatAppLayer(String pName) {
		//super(pName);
		// TODO Auto-generated constructor stub
		pLayerName = pName;
	}
	
	public boolean Send(byte[] input, int length) {     	 
    	if(length == 0) return false;
    	
    	setMsg(input);
    	byte[] msg = chatHeader.makeHeader();
    	
    	System.out.println("Send Msg");
    	ChatEventHandler.printMsg("SEND",chatHeader.nickname, chatHeader.ip, chatHeader.msg);
		return p_UnderLayer.Send(msg, msg.length);
	}

           
	public boolean Receive(byte[] input){
		_CAPP_HEADER receiveHeader = new _CAPP_HEADER(input);
		ChatEventHandler.printMsg("RECV", receiveHeader.nickname, receiveHeader.ip, receiveHeader.msg);
		return true;
	}	 

	public void setIP(byte[] ip) {
		System.arraycopy(ip, 0, chatHeader.ip, 0, 4);
	}
	
	public void setNickname(byte[] nickname) {
		chatHeader.nicknameLen = (byte) nickname.length;
		System.arraycopy(nickname, 0, chatHeader.nickname, 0, chatHeader.nicknameLen);
	}
	
	private void setMsg(byte[] msg) {
		chatHeader.msgLen[0] = (byte) ((msg.length >> 8) & 0xff);
		chatHeader.msgLen[1] = (byte) (msg.length & 0xff);
		chatHeader.msg = msg;
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
