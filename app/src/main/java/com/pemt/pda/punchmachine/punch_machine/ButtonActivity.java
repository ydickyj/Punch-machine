package com.pemt.pda.punchmachine.punch_machine;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.pemt.pda.punchmachine.punch_machine.fragment.FragmentOne;
import com.pemt.pda.punchmachine.punch_machine.fragment.FragmentTwo;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;

@EActivity(R.layout.activity_button)
public class ButtonActivity extends FragmentActivity {
    @ViewById(R.id.viewPager)
    ViewPager viewPager;
    @ViewById(R.id.myCoordinator)
    CoordinatorLayout myCoordinator;

    private BottomBar mBottomBar;
    private List<Fragment> fragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        initViewPager();
        createBottomBar(savedInstanceState);
    }

    private void createBottomBar(Bundle savedInstanceState) {
        mBottomBar = BottomBar.attachShy(myCoordinator,viewPager, savedInstanceState);
        mBottomBar.setItemsFromMenu(R.menu.bottombar_menu, new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.bb_menu_recents:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.bb_menu_favorites:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.bb_menu_nearby:

                        break;
                    case R.id.bb_menu_friends:

                        break;
                    case R.id.bb_menu_food:

                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });

        // Setting colors for different tabs when there's more than three of them.
        // You can set colors for tabs in three different ways as shown below.
        mBottomBar.mapColorForTab(0, ContextCompat.getColor(this, R.color.colorAccent));
        mBottomBar.mapColorForTab(1, 0xFF5D4037);
        mBottomBar.mapColorForTab(2, "#7B1FA2");
        mBottomBar.mapColorForTab(3, "#FF5252");
        mBottomBar.mapColorForTab(4, "#FF9800");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        mBottomBar.onSaveInstanceState(outState);
    }

    private void initViewPager() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new FragmentOne());
        fragmentList.add(new FragmentTwo());
        viewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomBar.selectTabAtPosition(position, true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
