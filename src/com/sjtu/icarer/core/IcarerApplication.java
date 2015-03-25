package com.sjtu.icarer.core;


import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sjtu.icarer.BuildConfig;
import com.sjtu.icarer.Injector;
import com.sjtu.icarer.RootModule;
import com.sjtu.icarer.thread.CrashHandler;

import butterknife.ButterKnife;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

public class IcarerApplication extends Application{
	private static IcarerApplication instance;
	/**
     * Create main application
     */
    public IcarerApplication() {
    }
    
    /**
     * Create main application
     *
     * @param context
     */
    public IcarerApplication(final Context context) {
        this();
        attachBaseContext(context);
    }
    
    /**
     * Create main application
     *
     * @param instrumentation
     */
    public IcarerApplication(final Instrumentation instrumentation) {
        this();
        attachBaseContext(instrumentation.getTargetContext());
    }
    
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		Injector.init(getRootModule(), this);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
		}
		CrashHandler catchHandler = CrashHandler.getInstance();  
        catchHandler.init(this);
		
		
		
        ButterKnife.setDebug(BuildConfig.DEBUG);
		initImageLoader(this);
	}
	
	private Object getRootModule() {
        return new RootModule();
    }
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheSize(50 * 1024 * 1024) // 50 Mb
				.tasksProcessingOrder(QueueProcessingType.FIFO)
				//.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	public static IcarerApplication getInstance() {
        return instance;
    }
	
}
