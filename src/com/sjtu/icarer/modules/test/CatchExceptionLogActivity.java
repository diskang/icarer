package com.sjtu.icarer.modules.test;

import com.sjtu.icarer.R;

import android.app.Activity;
import android.os.Bundle;

public class CatchExceptionLogActivity extends Activity {
    /** Called when the activity is first created. */
	private String s;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        System.out.println(s.equals("hello"));  // s没有进行赋值，所以会出现NullPointException异常
    }
}