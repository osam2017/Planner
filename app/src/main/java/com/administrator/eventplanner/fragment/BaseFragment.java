package com.administrator.eventplanner.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {


    public Context getFraContext() {
        return getActivity();
    }
}
