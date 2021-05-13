package cn.com.zonesion.powercontrol.listener;

public interface OnWSNDataListener {
	void onMessageArrive(String mac, String dat);
}
