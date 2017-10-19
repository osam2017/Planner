package com.administrator.eventplanner.Activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.administrator.eventplanner.BaseActivity;
import com.administrator.eventplanner.R;
import com.administrator.eventplanner.db.PlanDb;
import com.administrator.eventplanner.db.SubPlanDb;
import com.administrator.eventplanner.model.Plan;
import com.administrator.eventplanner.model.SubPlan;
import com.administrator.eventplanner.util.DatabaseHelper;

import java.util.List;

public class PlanDetailActivity extends BaseActivity {

    private TextView subplanTextView,planProgressDisplay;
    private AppCompatSeekBar planSeekBar;
    private Plan plan;
    private SQLiteDatabase db;
    private PlanDb planDao;
    private SubPlanDb subPlanDao;
    private SubPlan mSubPlan;
    private List<SubPlan> subPlans;
    private int parentID = 0;
    private LinearLayout totalProgressLayout;
    private TextView totalProgressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);

        getDB();
        setBar();
        changePlanProgress();
    }
    private void getDB(){
        plan = (Plan)this.getIntent().getSerializableExtra("plan");
        try{
            mSubPlan = (SubPlan)this.getIntent().getSerializableExtra("subplan");
        }catch (Exception e){

        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();
        planDao = new PlanDb(db);
        subPlanDao = new SubPlanDb(db);
    }

    private void changePlanProgress() {
        parentID = plan.getProjectID();
        planProgressDisplay = (TextView)findViewById(R.id.plan_detail_progress);
        subplanTextView = (TextView)findViewById(R.id.plan_detail_subplan);
        planSeekBar = (AppCompatSeekBar)findViewById(R.id.plan_progress_seekbar);
        planProgressDisplay.setTypeface(getFZXiYuanFont());

        totalProgressLayout = (LinearLayout)findViewById(R.id.parent_progress_layout);
        totalProgressTextView = (TextView)findViewById(R.id.plan_detail_parentProgress);
        totalProgressTextView.setTypeface(getFZXiYuanFont());

        if (planDao.exists(parentID)){
            planProgressDisplay.setText(plan.getProgress() + "");
            Log.e("plan.getProgress",plan.getProgress()+"");
            planSeekBar.setProgress(plan.getProgress());
        }
        if (mSubPlan != null){
            totalProgressLayout.setVisibility(View.VISIBLE);
            totalProgressTextView.setText(plan.getProgress() + "");

            subplanTextView.setText(mSubPlan.getId()+"."+mSubPlan.getContent());
            planProgressDisplay.setText(mSubPlan.getProgress() + "");
            planSeekBar.setProgress(mSubPlan.getProgress());
        }

        planSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                planProgressDisplay.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                 saveProgress(seekBar.getProgress());
            }
        });
    }
    private void saveProgress(int progress){
        try {
            if (mSubPlan != null) {
                mSubPlan.setProgress(progress);
                if (subPlanDao.save(mSubPlan) < 0) {
                    throw new Exception("진행도 저장 실패");
                }
                List<SubPlan> newSubPlans = subPlanDao.findAllByParentID(parentID);
                int parentProgress = 0;
                for (int i=0;i<newSubPlans.size(); i++){
                    SubPlan newSubPlan = newSubPlans.get(i);
                    int pro = newSubPlan.getProgress();
                    if (pro == 100){
                        parentProgress += newSubPlan.getWeight();
                    }
                    if (pro>0 && pro < 100){
                        parentProgress += Math.floor((double)(newSubPlan.getWeight()*pro/100));
                    }
                }
                Log.e("parentProgress",parentProgress+"<>");
                plan.setProgress(parentProgress);
                if (planDao.save(plan) < 0) {
                    throw new Exception("진행도 저장됨");
                }
                totalProgressTextView.setText(parentProgress + "");
            } else {
                plan.setProgress(progress);
                if (planDao.save(plan) < 0) {
                    throw new Exception("진행도 저장 실패");
                }
            }

        } catch (Exception e) {
            toast("진행도 저장 실패");
        }
    }
    private void setBar() {
       ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Toolbar toolbar = (Toolbar)this.findViewById(R.id.plan_detail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout)findViewById(R.id.plan_detail_collapsing_toolbar);
        collapsingToolbar.setTitle(plan.getName());

    }

}
