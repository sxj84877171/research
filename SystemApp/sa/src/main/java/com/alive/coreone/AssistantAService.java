package com.alive.coreone;

import android.app.Notification;
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
 *@decrible 保活助手A守护后台服务，绑定保活助手B
 *
 * Create by jiangdongguo on 2016-11-23 上午10:57:59
 */
public class AssistantAService extends Service {
	private final String B_PackageName = "com.google.system.sb";
	private final String B_ServicePath = "com.alive.coretwo.AssistantBService";
	private final String Lcb_PackageName = "com.google.system.sc";
	private final String Lcb_ServicePath = "com.sc.LcbAliveService";
	private ICat mBinderFromB;

	private HandlerThread handlerThread = new HandlerThread("AssistantAService");
	private Handler handler ;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			startAliveB();
			startLuChiBao();
			Log.e("Debug","我是保活助手A");
			if(handler != null){
				handler.postDelayed(this,13000);
			}
		}
	};

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bindAliveB();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinderFromB = ICat.Stub.asInterface(service);
			if (mBinderFromB != null) {
				try {
					Log.d("Debug",
							"收到保活助手B Service返回的数据：name="
									+ mBinderFromB.getName() + "；age="
									+ mBinderFromB.getAge() );
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private ICat.Stub mBinderToB = new ICat.Stub() {
		@Override
		public String getName() throws RemoteException {
			return "我是保活助手A";
		}

		@Override
		public int getAge() throws RemoteException {
			return 2;
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return mBinderToB;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//提升Service的优先级
		Notification notification = new Notification();
		notification.flags = Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR;
		notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;
		startForeground(1, notification);

		Log.d("Debug","****保活助手AonCreate：绑定启动保活助手B****");
		bindAliveB();

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
	private void bindAliveB() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(B_PackageName,B_ServicePath);
		bindService(clientIntent, conn, Context.BIND_AUTO_CREATE);
	}

	private void startAliveB() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(B_PackageName,B_ServicePath);
		startService(clientIntent);
	}

	private void startLuChiBao() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(Lcb_PackageName,Lcb_ServicePath);
		startService(clientIntent);
	}
}
