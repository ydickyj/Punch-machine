package com.pemt.pda.punchmachine.punch_machine.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pemt.pda.punchmachine.punch_machine.R;


public class FragmentOne extends Fragment {
    View v;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_one, container,false);
        context = getActivity();
        return v;
    }
}
