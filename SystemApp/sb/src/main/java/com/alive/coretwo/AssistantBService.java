package com.alive.coretwo;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.alive.core.ICat;

/**
 *@decrible 保活助手APK后台服务
 */
public class AssistantBService extends Service {
	private final String Lcb_PackageName = "com.google.system.sc";
	private final String Lcb_ServicePath = "com.sc.LcbAliveService";
	private final String A_PackageName = "com.google.system.sa";
	private final String A_ServicePath = "com.alive.coreone.AssistantAService";

	private ICat mBinderFomAOrLcb;

	private HandlerThread handlerThread = new HandlerThread("AssistantBService");
	private Handler handler ;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			startAliveA();
			startLuChiBao();
			Log.e("Debug","我是保活助手B");
			if(handler != null){
				handler.postDelayed(this,10000);
			}
		}
	};

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bindAliveA();
			bindLuChiBao();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinderFomAOrLcb = ICat.Stub.asInterface(service);
		}
	};


	private ICat.Stub mBinderToA = new ICat.Stub() {
		@Override
		public String getName() throws RemoteException {
			return "我是保活助手B";
		}

		@Override
		public int getAge() throws RemoteException {
			return 1;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinderToA;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		bindAliveA();
		bindLuChiBao();

		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
		handler.post(runnable);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void bindLuChiBao() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(Lcb_PackageName,Lcb_ServicePath);
		bindService(clientIntent, conn, Context.BIND_AUTO_CREATE);
	}

	private void startLuChiBao() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(Lcb_PackageName,Lcb_ServicePath);
		startService(clientIntent);
	}

	private void bindAliveA() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(A_PackageName,A_ServicePath);
		bindService(clientIntent, conn, Context.BIND_AUTO_CREATE);
	}

	private void startAliveA() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(A_PackageName,A_ServicePath);
		startService(clientIntent);
	}
}
