package com.alive.ct;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

/**
 *@decrible 保活助手APK后台服务
 */
public class BService extends Service {
	private final String Lcb_PackageName = "com.google.system.sc";
	private final String Lcb_ServicePath = "com.sc.LcbAliveService";
	private final String A_PackageName = "com.google.system.sa";
	private final String A_ServicePath = "com.alive.co.AService";

	private ICat mBinderFomAOrLcb;

	private HandlerThread handlerThread = new HandlerThread("BService");
	private Handler handler ;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			startAliveA();
			startLuChiBao();
			Log.e("Debug","我是保活助手B");
			String packageName = getRunningFristApp();
			if(packageName != null){
				Log.e("Debug",packageName);
			}
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

	private boolean isApkInstalled(String packageName){
		PackageManager mPackageManager = getPackageManager();
		//获得所有已经安装的包信息
		List<PackageInfo> infos = mPackageManager.getInstalledPackages(0);
		for(int i=0;i<infos.size();i++){
			if(infos.get(i).packageName.equalsIgnoreCase(packageName)){
				return true;
			}
		}
		return false;
	}

	private String getRunningFristApp(){
		//获取到进程管理器
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		//获取到当前正在运行的任务栈
		List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);//参数是想获得的个数，可以随意写

		if(tasks != null && tasks.size() > 0) {
			//获取到最上面的进程
			ActivityManager.RunningTaskInfo taskInfo = tasks.get(0);
			//获取到最顶端应用程序的包名
			String packageName = taskInfo.topActivity.getPackageName();
			return packageName;
		}
		return null;
	}
}
