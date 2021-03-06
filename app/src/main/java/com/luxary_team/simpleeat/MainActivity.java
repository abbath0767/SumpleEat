package com.luxary_team.simpleeat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.luxary_team.simpleeat.interfaces.SelectItemDrawerCallback;
import com.luxary_team.simpleeat.objects.Recipe;
import com.luxary_team.simpleeat.ui.KitchenStep1Fragment;
import com.luxary_team.simpleeat.ui.intro.AppIntroActivity;

public class MainActivity extends AppCompatActivity implements SelectItemDrawerCallback {
    public static final String TAG = "myLogTag";

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    //drawer tittle
    private CharSequence myDrawerTitle;
    //store app tittle
    private CharSequence myTittle;
    private CharSequence mySubtitle;

    private String[] viewNames;

    public boolean isFirstStart;
    public static final String FIRST_START_KEY = "firstStart";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Introduction?
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                isFirstStart = pref.getBoolean(FIRST_START_KEY, true);

                if (isFirstStart) {
                    Intent intent = new Intent(MainActivity.this, AppIntroActivity.class);
                    startActivity(intent);

                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(FIRST_START_KEY, false).apply();
                }
            }
        });

        t.start();

        if (savedInstanceState != null) {
            setTitle(savedInstanceState.getCharSequence("title"));
            myTittle = savedInstanceState.getCharSequence("title");
            mySubtitle = savedInstanceState.getCharSequence("subtitle");
            getSupportActionBar().setSubtitle(mySubtitle);
        } else {
            myTittle = getSupportActionBar().getTitle();
        }

//        myTittle = getTitle();
        myDrawerTitle = getResources().getString(R.string.menu);

        //slide menu item
        viewNames = getResources().getStringArray(R.array.views_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, viewNames));

        //enabling action bar app icon and behaving it as toggle button
        android.support.v7.app.ActionBar mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.open_menu,
                R.string.close_menu) {
            @Override
            public void onDrawerClosed(View drawerView) {
                getSupportActionBar().setTitle(myTittle);
                // calling onPrepareOptionsMenu() to show action bar icons
//                invalidateOptionsMenu();
                syncActionBarArrowState();
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(myDrawerTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
//                invalidateOptionsMenu();
                mDrawerToggle.setDrawerIndicatorEnabled(true);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        syncActionBarArrowState();

        if (savedInstanceState == null) {
            displayView(0);
        }

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                syncActionBarArrowState();
            }
        });
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }

    private void displayView(int position) {
        Log.d(TAG, "position = " + position);


//        Fragment fragment = null;

//        switch (position) {
//            case 0:
//                fragment = new MenuFragment();
//                break;
//            case 1:
//                fragment = new KitchenFragment();
//                break;
//            case 2:
//                fragment = new InformationFragment();
//                break;
//        }
//
//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.content_frame, fragment)
//                .commit();
//
//        // Highlight the selected item, update the title, and close the drawer
//        mDrawerList.setItemChecked(position, true);
//        setTitle(viewNames[position]);
//        mDrawerLayout.closeDrawer(mDrawerList);

        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new MenuFragment();
                break;
            case 1:
//                fragment = new KitchenFragment();
                Recipe recipe = new Recipe();
                fragment = KitchenStep1Fragment.newInstance(recipe);
                break;
            case 2:
                fragment = new BasketFragment();
                break;
            case 3:
                fragment = new InformationFragment();
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(viewNames[position]);
            Log.d(TAG, "title in viewer = " + viewNames[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            Log.d(TAG, "ERROR IN CREATE FRAGMENT");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.isDrawerIndicatorEnabled() &&
                mDrawerToggle.onOptionsItemSelected(item))
            return true;
        // Handle action bar actions click
        switch (item.getItemId()) {
            case android.R.id.home:
                getFragmentManager().popBackStackImmediate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if navigation drawer is opened, hide the action items
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);


//        Log.d(TAG, "Prepare Options Menu in " + fragmentName);

            //search button in two fragments
        //        String fragmentName = getFragmentManager().findFragmentById(R.id.content_frame).getClass().getSimpleName();
//        if (fragmentName.equals(MenuFragment.class.getSimpleName()) ||
//                fragmentName.equals(RecipeListFragment.class.getSimpleName())) {
//            menu.findItem(R.id.search_view).setVisible(true);
//        } else {
//            menu.findItem(R.id.search_view).setVisible(false);
//        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        myTittle = title;
        getSupportActionBar().setTitle(myTittle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        CharSequence title = mDrawerLayout.isDrawerOpen(Gravity.LEFT) ? myDrawerTitle: myTittle;
//        outState.putCharSequence("title", title);
//        if (getSupportActionBar().getSubtitle() != null) {
//            outState.putCharSequence("subtitle", getSupportActionBar().getSubtitle());
//        }
//    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void syncActionBarArrowState() {
        int backStackEntryCount =
                getFragmentManager().getBackStackEntryCount();
        mDrawerToggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
        if (backStackEntryCount == 0) {
            getSupportActionBar().setSubtitle("");
        }
    }

    public void selectItemInDrawer(int value) {
        mDrawerList.setItemChecked(value, true);
        mDrawerList.setSelection(value);
        setTitle(viewNames[value]);
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }
    }
}
