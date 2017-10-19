package com.administrator.eventplanner.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.administrator.eventplanner.R;
import com.administrator.eventplanner.adapter.PlanAdapter;
import com.administrator.eventplanner.db.PlanDb;
import com.administrator.eventplanner.model.Plan;
import com.administrator.eventplanner.util.DatabaseHelper;

import java.util.List;

public class MainPlanFragment extends BaseFragment{
    RecyclerView planList;
    PlanAdapter planAdapter;
    PlanDb planDao;

    List<Plan> plans;
    View view;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_main_plan,container,false);

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();

        init();
       // setListAnim();
    }
    private void init() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        planDao = new PlanDb(db);

        plans = planDao.findAll();
        plans.add(new Plan());

        planList = (RecyclerView)view.findViewById(R.id.plan_list);
        planList.setLayoutManager(new LinearLayoutManager(getContext()));
        planAdapter = new PlanAdapter(getContext(),plans,planDao);
        planList.setAdapter(planAdapter);
    }
    private void setListAnim(){
        Animation animation = (Animation) AnimationUtils.loadAnimation(
                getContext(), R.anim.list_anim_y);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setDelay(0.5f);
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL);
        planList.setLayoutAnimation(lac);
    }
    public void onPause(){
        super.onPause();
    }
}
