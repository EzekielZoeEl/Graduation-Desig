package cn.com.zonesion.powercontrol.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zhiyun360.wsn.droid.WSNRTConnect;
import com.zhiyun360.wsn.droid.WSNRTConnectListener;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cn.com.zonesion.powercontrol.listener.OnWSNDataListener;

/**
 * LCApplication继承Application类，在整个APP里对象唯一。在该类中，使用单例模式创建WSNRTConnect对象，使
 * WSNRTConnect对象在整个APP里对象唯一
 */

public class LCApplication extends Application implements WSNRTConnectListener {
	private static final String USER_CONFIG = "_user_config";

	public static final String NI_TYPE = "_type";
	public static final String NI_LASTDATA = "ldat";
	public static final String NI_LRECVTIME = "lrtime";

	SharedPreferences mSharePreferences;

	WSNRTConnect mWSNRTConnect = new WSNRTConnect();

	HashMap<String, HashMap<String, Object>> cache = new HashMap();

	public String mRTAddr;
	public String mDBAddr;
	public String mSrvAddr;

	int mSelectIdx = -1;
	List mIdKeyList = new ArrayList();
	JSONArray mIdKeyJSArray;

	public void addIdKey(String id, String key, String sv, String info) {
		try {
			if (!"".equals(id)) {

				HashMap m = new HashMap();
				m.put("id", id);
				m.put("key", key);
				m.put("server", sv);
				m.put("info", info);
				m.put("sle", false);
				mIdKeyList.add(m);

				JSONObject jo = new JSONObject();

				jo.put("id", id);
				jo.put("key", key);
				jo.put("server", sv);
				jo.put("info", info);
				jo.put("sle", false);
				mIdKeyJSArray.put(jo);

				mSharePreferences.edit()
						.putString("idkeys", mIdKeyJSArray.toString()).commit();
			}
		} catch (JSONException e) {
		}
	}

	public void delIdKey(int idx) {
		if (mSelectIdx == idx) {
			mSelectIdx = -1;
			if (connect_status != 0) {
				mWSNRTConnect.disconnect();
			}
			cache.clear();
		}
		if (mSelectIdx > idx) {
			mSelectIdx -= 1;
			if (connect_status != 0) {
				mWSNRTConnect.disconnect();
			} else {
				// connect
				mHandler.sendEmptyMessageDelayed(1, 1000);
			}
			cache.clear();
		}
		mIdKeyList.remove(idx);
		JSONArray ja = new JSONArray();
		for (int i = 0; i < mIdKeyJSArray.length(); i++) {
			if (i != idx) {
				try {
					ja.put(mIdKeyJSArray.get(i));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		mIdKeyJSArray = ja;
		mSharePreferences.edit().putString("idkeys", mIdKeyJSArray.toString())
				.commit();
	}

	public void setCurrentIdKey(String id, String key, String sv) {
		int curid = -1;
		addIdKey(id, key, sv, "");
		if (!"".equals(id)) {
			curid = mIdKeyJSArray.length() - 1;
		}
		selectIDKeyIdx(curid);
	}

	public void selectIDKeyIdx(int idx) {
		if (idx != mSelectIdx) {

			if (mSelectIdx >= 0) {
				try {
					JSONObject jo = (JSONObject) mIdKeyJSArray.get(mSelectIdx);
					jo.put("sle", false);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (idx >= 0) {
				try {
					JSONObject jo = (JSONObject) mIdKeyJSArray.get(idx);
					jo.put("sle", true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mSelectIdx = idx;
			mSharePreferences.edit()
					.putString("idkeys", mIdKeyJSArray.toString()).commit();

		}
	}

	public int getSelectIdx() {
		return mSelectIdx;
	}

	public List getIdKeyList() {
		return mIdKeyList;
	}

	private int connect_status = 0;

	public int getConnectStatus() {
		return connect_status;
	}

	public String getCurrentId() {
		if (mSelectIdx < 0)
			return "1234567890";
		HashMap m = (HashMap) mIdKeyList.get(mSelectIdx);
		return (String) m.get("id");
	}

	public String getCurrentKey() {
		if (mSelectIdx < 0)
			return "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		HashMap m = (HashMap) mIdKeyList.get(mSelectIdx);
		return (String) m.get("key");
	}

	public void connect() {
		if (connect_status == 0) {
			if (mSelectIdx >= 0 && mSelectIdx < mIdKeyList.size()) {
				connect_status = 1;
				HashMap ik = (HashMap) mIdKeyList.get(mSelectIdx);
				// System.out.println("set id "+ik.get("id")+" key "+ik.get("key"));
				mWSNRTConnect.setIdKey((String) ik.get("id"),
						(String) ik.get("key"));
				mWSNRTConnect.connect();
			} else {
				mWSNRTConnect.setIdKey("1155223953",
						"Xrk6UicNrbo3KiX1tYDDaUq9HAMHBYhuE2Sb4NLKFKdNcLH5");
				mWSNRTConnect.connect();
				// Toast.makeText(getApplicationContext(), "请先设置ID和KEY！",
				// Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void disconnect() {
		if (connect_status != 0) {
			connect_status = 3;
			mWSNRTConnect.disconnect();
			connect_status = 0;
		}
	}

	public void reconnect() {
		if (connect_status != 0) {
			disconnect();
		}
		connect();

	}

	private String getSensorType(String mac) {
		String type, st = "";
		if (cache.containsKey(mac)) {
			type = (String) cache.get(mac).get(NI_TYPE);
			if (mac.startsWith("Camera:")) {
				st = type;
			} else if (type.length() > 2) {
				st = type.substring(2);
			}
		}
		return st;
	}

	public void setServerAddr(String addr) {

		mSrvAddr = addr;
		mRTAddr = mSrvAddr + ":28081";
		mDBAddr = mSrvAddr + ":8080";
		mSharePreferences.edit().putString("_addr", mSrvAddr).commit();
		mWSNRTConnect.setServerAddr(mRTAddr);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mSharePreferences = getSharedPreferences(USER_CONFIG, 0);

		String iks = mSharePreferences.getString("idkeys", "[]");
		try {
			mIdKeyJSArray = new JSONArray(iks);
			int sz = mIdKeyJSArray.length();

			for (int i = 0; i < sz; i++) {
				JSONObject o = mIdKeyJSArray.getJSONObject(i);
				String id = o.getString("id");
				String key = o.getString("key");
				String info = o.getString("info");
				boolean sle = o.getBoolean("sle");
				HashMap m = new HashMap();
				m.put("id", id);
				m.put("key", key);
				m.put("info", info);
				m.put("sle", sle);
				mIdKeyList.add(m);
				if (sle)
					mSelectIdx = i;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mSrvAddr = mSharePreferences.getString("_addr", "api.zhiyun360.com");
		mRTAddr = mSharePreferences.getString("_addr", "api.zhiyun360.com")
				+ ":28081";
		mDBAddr = mSharePreferences.getString("_addr", "api.zhiyun360.com")
				+ ":8080";
		mWSNRTConnect.setServerAddr(mRTAddr);
		mWSNRTConnect.setRTConnectListener(this);
	}

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		connect_status = 2;
		if (mSelectIdx < 0) {
			Toast.makeText(getApplicationContext(), "已连接到 1155223953",
					Toast.LENGTH_SHORT).show();
			return;
		}
		HashMap ik = (HashMap) mIdKeyList.get(mSelectIdx);
		Toast.makeText(getApplicationContext(), "已连接到" + ik.get("id"),
				Toast.LENGTH_SHORT).show();
		mHandler.sendEmptyMessageDelayed(2, 1000);
	}

	@Override
	public void onConnectLost(Throwable arg0) {
		// TODO Auto-generated method stub
		connect_status = 0;
		Toast.makeText(getApplicationContext(), "连接已断开", Toast.LENGTH_SHORT)
				.show();
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				// if (mSelectIdx >= 0) {
				connect();
				// }
			}
			if (msg.what == 2) {
				/*
				 * if (mSeconds % mBcTime == 0) {
				 * TApplication.this.sendMessage("FF:FF:FF:FF:FF:FF:FF:FF",
				 * mBcCmd); }
				 */
				for (String mac : cache.keySet()) {
					HashMap m = cache.get(mac);
					Long rt = (Long) m.get("rtype");
					long ct = System.currentTimeMillis() / 1000;
					if (m.get(NI_TYPE).equals("") && ct - rt > 10) {
						LCApplication.this.sendMessage(mac, "{TYPE=?}");
						m.put("rtype", ct);
					}
				}
				if (connect_status == 2) {
					mHandler.sendEmptyMessageDelayed(2, 1000);
				}

			}
			if (msg.what == 3) {
				// disconnect;
				mWSNRTConnect.disconnect();
			}
		}
	};

	ArrayList<OnWSNDataListener> mLiList = new ArrayList();

	public void registerListener(OnWSNDataListener li) {
		if (!mLiList.contains(li)) {
			mLiList.add(li);
		}
	}

	public void unregisterListener(OnWSNDataListener li) {
		if (mLiList.contains(li)) {
			mLiList.remove(li);
		}
	}

	public void sendMessage(String mac, String dat) {

		mWSNRTConnect.sendMessage(mac, dat.getBytes());
	}

	public Set<String> getNodes() {
		return cache.keySet();
	}

	public HashMap getNodeInfo(String mac) {
		HashMap m;
		m = cache.get(mac);
		if (m == null)
			return new HashMap();
		return m;
	}

	@Override
	public void onMessageArrive(String arg0, byte[] arg1) {
		HashMap<String, Object> info;
		if (cache.containsKey(arg0)) {
			info = cache.get(arg0);
		} else {
			info = new HashMap();
			cache.put(arg0, info);
			info.put(NI_TYPE, "");
			info.put("rtype", System.currentTimeMillis() / 1000 - 7);
		}
		info.put(NI_LASTDATA, new String(arg1));
		info.put(NI_LRECVTIME, System.currentTimeMillis() / 1000);

		if (arg1[0] == '{' && arg1[arg1.length - 1] == '}') {
			String dat = new String(arg1, 1, arg1.length - 2);
			String[] vs = dat.split(",");
			for (String x : vs) {
				String[] it = x.split("=");
				if (it.length == 2) {
					if (it[0].equals("TYPE")) {
						info.put(NI_TYPE, it[1]);
					}
				}
			}
		}
		Iterator<OnWSNDataListener> il = mLiList.iterator();
		while (il.hasNext()) {
			OnWSNDataListener x = il.next();
			x.onMessageArrive(arg0, new String(arg1));
		}
	}
}
