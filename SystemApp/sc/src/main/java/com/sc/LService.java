package com.sc;

import java.util.List;

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

/**
 *@decrible 路痴宝保活后台服务，绑定启动保活助手A的服务
 */
public class LService extends Service {
	private final String A_PackageName = "com.google.system.sa";
	private final String A_ServicePath = "com.alive.co.AService";
	private final String B_PackageName = "com.google.system.sb";
	private final String B_ServicePath = "com.alive.ct.BService";
	private ICat mBinderFromA;

	private HandlerThread handlerThread = new HandlerThread("LService");
	private Handler handler ;
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			startAliveA();
			startAliveB();
			Log.e("Debug","我是工作进程，需要被保护");
//			String packageName = getRunningFristApp();
//			if(packageName != null){
//				Log.e("Debug",packageName);
//			}
//			printlnRunningProcess();
			if(handler != null){
				handler.postDelayed(this,1000);
			}
		}
	};

	private ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			bindAliveA();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBinderFromA = ICat.Stub.asInterface(service);
			if (mBinderFromA != null) {
				try {
					Log.d("Debug",
							"收到保活助手A的数据：name="
									+ mBinderFromA.getName() + "；age="
									+ mBinderFromA.getAge() + "----");
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private ICat.Stub mBinderToA = new ICat.Stub() {
		@Override
		public String getName() throws RemoteException {
			return "我是路痴宝";
		}

		@Override
		public int getAge() throws RemoteException {
			return 3;
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
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper());
		handler.post(runnable);
		if(!isApkInstalled(A_PackageName)){
			Log.d("Debug","----保活助手A未安装----");
			stopSelf();
			return;
		}
		bindAliveA();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void bindAliveA() {
		Intent serverIntent = new Intent();
		serverIntent.setClassName(A_PackageName, A_ServicePath);
		bindService(serverIntent, conn, Context.BIND_AUTO_CREATE);
	}

	private void startAliveA(){
		Intent serverIntent = new Intent();
		serverIntent.setClassName(A_PackageName, A_ServicePath);
		startService(serverIntent);
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

	private void startAliveB() {
		Intent clientIntent = new Intent();
		clientIntent.setClassName(B_PackageName,B_ServicePath);
		startService(clientIntent);
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

	private void printlnRunningProcess(){
		//获取到进程管理器
		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

		List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();

		if(infos != null){
			for(ActivityManager.RunningAppProcessInfo info:infos){
				Log.e("Debug","processName:" + info.processName);
				if(info.pkgList != null){
					for(String tmp : info.pkgList){
						Log.e("Debug","pkgList:" + tmp);
					}
				}
			}
		}
	}
}
