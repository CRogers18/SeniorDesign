package com.group10.backupbuddy;

import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.Notification;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;


public class navigationFun extends AppCompatActivity {

    private BottomNavigationView MainNav;
    private FrameLayout MainFrame;
    private HomeFragment homeFragment;
    private DebugFragment debugFragment;
    private VideoFragment videoFragment;
    private SettingsFragment settingsFragment;
//    private SettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_fun);
        getSupportActionBar().hide();
        MainFrame = (FrameLayout) findViewById(R.id.main_frame);
        MainNav = (BottomNavigationView) findViewById(R.id.main_nav);

        homeFragment = new HomeFragment();
        debugFragment = new DebugFragment();
        videoFragment = new VideoFragment();
//        settingsFragment = new SettingsFragment();
//        int i = 0;
//        i = getIntent().getExtras().getInt("frgToLoad");
//        if(i == 1)
//        {
//            setFragment(videoFragment);
//        }
//        else
         setFragment(homeFragment);

        MainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.nav_home:
//                        MainNav.setItemBackgroundResource(R.color.colorPrimary);
                        setFragment(homeFragment);
                        return true;

                    case R.id.nav_debug :
//                        MainNav.setItemBackgroundResource(R.color.colorAccent);
                        setFragment(debugFragment);
                        return true;


//                    case R.id.nav_settings:
//                        MainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
//                        setFragment(settingsFragment);
//                        return true;

                    case R.id.nav_videos:
                        setFragment(videoFragment);
//                        MainNav.setItemBackgroundResource(R.color.colorPrimaryDark);
                        return true;


                    default:
                        return false;
                }

                //return false;
            }
        });
    }

    private void setFragment(Fragment fragment)
    {
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }
}
