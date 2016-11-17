package com.pemt.pda.punchmachine.punch_machine;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.roughike.bottombar.BottomBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_button)
public class ButtonActivity extends FragmentActivity {
    @ViewById(R.id.viewPager)
    ViewPager viewPager;
    @ViewById(R.id.myCoordinator)
    CoordinatorLayout myCoordinator;
    private BottomBar mBottomBar;
    private List<Fragment> fragmentList;


    @AfterViews
    void afterView() {

    }

}
