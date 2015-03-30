package com.sjtu.icarer.ui;

import javax.inject.Inject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.sjtu.icarer.Injector;
import com.sjtu.icarer.R;
import com.sjtu.icarer.common.constant.Constants;
import com.squareup.otto.Bus;

/**
 * Base class for all  icarer Activities that need ActionBar.
 */
public class IcarerFragmentActivity extends ActionBarActivity {
	@InjectView(R.id.toolbar) protected Toolbar toolbar;
    @Inject protected Bus eventBus;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public void setContentView(final int layoutResId) {
        super.setContentView(layoutResId);

        ButterKnife.inject(this);
        toolbar.setLogo(R.drawable.ic_launcher);
		toolbar.setTitle("—HOUSECARE— ");
		toolbar.setSubtitle(" 沪上养老专业品牌");
		setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventBus.unregister(this);
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_option, menu);
		return true;
    }
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    	int id = item.getItemId();
        
        switch(id) {
        	
        	case R.id.goto_prefer:
        		Intent intent_setting = new Intent(this, SetupActivity.class);
        		startActivity(intent_setting);
	        	break;
        	case R.id.goto_refresh:
        		Intent intent = new Intent(Constants.ACTION_UPDATE_INFO);
        		sendBroadcast(intent);
	        	break;
        	case R.id.goto_logout:
        		break;//TODO
        	case R.id.goto_quit:
        		System.exit(0);
        		break;//TODO
            default:
            	break;
            }
        		
        return super.onOptionsItemSelected(item);
        
    }
	
}
