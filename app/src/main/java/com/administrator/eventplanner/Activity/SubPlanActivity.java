package com.administrator.eventplanner.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.administrator.eventplanner.BaseActivity;
import com.administrator.eventplanner.R;
import com.administrator.eventplanner.adapter.SubPlanAdapter;
import com.administrator.eventplanner.db.SubPlanDb;
import com.administrator.eventplanner.model.SubPlan;
import com.administrator.eventplanner.util.AppUtil;
import com.administrator.eventplanner.util.DatabaseHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SubPlanActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView subPlanList;
    private SubPlanAdapter subPlanAdapter;
    private FloatingActionButton addPlanFB;
    private LinearLayout popManagerLayout;
    private Animation bottomIn,bottomOut;
    private View transView;

    private TextInputLayout editInputLayout;
    private EditText subPlanEdit;
    private TextView subStartDate,subEndDate,seekPro;
    private SeekBar subPlanSeekBar;
    private LinearLayout addLayout,deleteLayout;

    private String startDate,endDate;
    private long startMilliTime,endMilliTime,startMilliTimePick,endMilliTimePick;
    private SQLiteDatabase db;
    private int year,month,day,hour,min;
    private int planProgress = 0;
    List<SubPlan> subPlans = new ArrayList<>();

    private CardView cardView;
    private int parentId = 0;
    private String parentPlan;
    private long parentStartMilliDate,parentEndMilliDate;
    private TextView parentPlanContent,parentPlanStartTime,parentPlanEndTime,parentPlanArrowIc,parentPlanTotalTime;
    private String addPlanStartTime;
    private boolean hasSetTime = false;
    private int planProgressTotal = 0;
    private int delayProgress = 100;

    private SubPlanDb subPlanDao;
    private int subPlanID = 0;
    private boolean isEdit = false;
    private RelativeLayout showSubPlanTimeLayout;
    private LinearLayout addSubPlanTimeLayout,addSubPlanWeightLayout;
    private TextView titleView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subplan);

        setBar();
        setPlanInfo();
        initSubPlan();
        addSubPlan();
    }
    private void setPlanInfo(){
        cardView = (CardView)findViewById(R.id.parent_plan_cardview);
        parentPlanContent = (TextView)findViewById(R.id.parent_plan_content);
        parentPlanStartTime = (TextView)findViewById(R.id.parentplan_startdate);
        parentPlanEndTime = (TextView)findViewById(R.id.parentplan_enddate);
        parentPlanArrowIc = (TextView)findViewById(R.id.parentplan_arrowdate_icon);
        parentPlanTotalTime = (TextView)findViewById(R.id.parentPlanTotalTime);

        showSubPlanTimeLayout = (RelativeLayout)findViewById(R.id.show_subplan_time_layout);
        addSubPlanTimeLayout = (LinearLayout)findViewById(R.id.add_subplan_timeshow);
        addSubPlanWeightLayout = (LinearLayout)findViewById(R.id.show_time_weightlayout);
        titleView = (TextView)findViewById(R.id.title_bottom);

        parentPlanArrowIc.setTypeface(getIconFont());
        parentPlanEndTime.setTypeface(getFZXiYuanFont());
        parentPlanStartTime.setTypeface(getFZXiYuanFont());

        try{
            Intent intent = this.getIntent();
            parentId = intent.getIntExtra("planId", 0);
            parentPlan = intent.getStringExtra("planContent");
            parentStartMilliDate = intent.getLongExtra("planStartTime", 0l);
            parentEndMilliDate = intent.getLongExtra("planEndTime", 0l);
            hasSetTime = intent.getBooleanExtra("hasSetTimeType",false);

            parentPlanContent.setText(parentPlan);
            if (hasSetTime){
                showSubPlanTimeLayout.setVisibility(View.GONE);
                parentPlanTotalTime.setText("시간/진행도 설정");
            }else {
                startMilliTime = parentStartMilliDate;
                endMilliTime = parentEndMilliDate;
                addPlanStartTime = formater.format(parentStartMilliDate);
                parentPlanStartTime.setText(formater.format(parentStartMilliDate));
                parentPlanEndTime.setText(formater.format(parentEndMilliDate));
                parentPlanTotalTime.setText("시간:" + AppUtil.getTotalDays(parentStartMilliDate, parentEndMilliDate) +
                        "/"+ AppUtil.getTotalDays(parentStartMilliDate, parentEndMilliDate)+"  진행도:" + (100-planProgressTotal)+"%");
            }

        }catch (Exception e){
            toast("일정이 비어있습니다");
        }

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }
        });

        DatabaseHelper helper = new DatabaseHelper(this);
        db = helper.getWritableDatabase();
        subPlanDao = new SubPlanDb(db);
        if (subPlanDao.existParentID(parentId)){
            subPlans = subPlanDao.findAllByParentID(parentId);
        }
        if (subPlans.size() > 0){
            parentPlanTotalTime.setText("시간:" + AppUtil.getTotalDays(parentStartMilliDate, parentEndMilliDate) +
                    "/"+ AppUtil.getTotalDays(endMilliTime, parentEndMilliDate)+" 진행도:" +"0%");
        }

    }


    private void initSubPlan() {
        subPlanList = (RecyclerView)findViewById(R.id.sub_plan_list);
        subPlanList.setLayoutManager(new LinearLayoutManager(this));
        subPlanAdapter = new SubPlanAdapter(this, subPlans, new SubPlanAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showPop(subPlans.get(position));
            }
        },hasSetTime);
        subPlanList.setAdapter(subPlanAdapter);

        bottomIn = AnimationUtils.loadAnimation(this,R.anim.popup_bottom_in);
        bottomOut = AnimationUtils.loadAnimation(this,R.anim.popup_bottom_out);
        popManagerLayout = (LinearLayout)findViewById(R.id.popup_manager_layout);
        popManagerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        transView = findViewById(R.id.pop_trans_view);
        transView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePop(false);
            }
        });

        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        min = calendar.get(Calendar.MINUTE);

    }

    private void addSubPlan() {

        editInputLayout = (TextInputLayout)findViewById(R.id.subplan_content_layout);
        subPlanEdit = (EditText)findViewById(R.id.subplan_content);

        subStartDate = (TextView)findViewById(R.id.subplan_starttime);
        subEndDate= (TextView)findViewById(R.id.subplan_endtime);
        subStartDate.setText(formater.format(parentStartMilliDate));
        subEndDate.setText(formater.format(parentEndMilliDate));

        subStartDate.setOnClickListener(this);
        subEndDate.setOnClickListener(this);

        subPlanSeekBar = (SeekBar)findViewById(R.id.subplan_progress_seekbar);
        seekPro = (TextView)findViewById(R.id.subplan_detail_progress);
        seekPro.setTypeface(getFZXiYuanFont());
        subPlanSeekBar.setProgress(100);
        seekPro.setText(100 + "");
        subPlanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekPro.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                planProgress = seekBar.getProgress();
            }
        });


        addLayout = (LinearLayout)findViewById(R.id.add_sub_plan_done);
        deleteLayout = (LinearLayout)findViewById(R.id.delete_sub_plan);
        if (isEdit){
            deleteLayout.setVisibility(View.VISIBLE);
        }else {
            deleteLayout.setVisibility(View.GONE);
        }
        addLayout.setOnClickListener(this);
        deleteLayout.setOnClickListener(this);
    }
    private void setBar() {

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if (id == android.R.id.home){
            this.finish();
            return true;
        }
      /*  if (id == R.id.action_compelete){
            try{

            }catch (Exception e){
                toast("저장 실패");
            }
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.subplan_starttime:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        startDate = year + "/"+(monthOfYear+1)+"/"+dayOfMonth;
                        try{
                            subStartDate.setText(formater.format(formater.parse(startDate)));
                            startMilliTimePick = formater.parse(startDate).getTime();
                        }catch (ParseException e){

                        }

                        new TimePickerDialog(SubPlanActivity.this, new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int min) {
                                startDate = startDate+" "+ hour+":"+min;
                                try{

                                    startMilliTimePick = formater.parse(startDate).getTime();
                                    if (startMilliTimePick < parentStartMilliDate){
                                        toast("세부 일정이 본 일정보다 늦습니다");
                                        return;
                                    }
                                    if (startMilliTimePick > parentEndMilliDate){
                                        toast("세부 일정이 본 일정보다 빠릅니다");
                                        return;
                                    }
                                    if (startMilliTimePick < startMilliTime){
                                        toast("시작 시간보다 일찍 시작할 수 없습니다");
                                        return;
                                    }
                                    startMilliTime = startMilliTimePick;
                                    subStartDate.setText(formater.format(formater.parse(startDate)));
                                }catch (ParseException e){

                                }
                            }
                        },hour,min,true).show();
                    }
                },year,month,day).show();
                break;
            case R.id.subplan_endtime:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate = year + "/"+(monthOfYear+1)+"/"+dayOfMonth;

                        try{
                            subEndDate.setText(formater.format(formater.parse(endDate)));
                            endMilliTimePick = formater.parse(endDate).getTime();
                        }catch (ParseException e){

                        }

                        new TimePickerDialog(SubPlanActivity.this, new TimePickerDialog.OnTimeSetListener(){
                            @Override
                            public void onTimeSet(TimePicker view, int hour, int min) {
                                endDate = endDate+" "+ hour+":"+min;
                                try{
                                    endMilliTimePick = formater.parse(endDate).getTime();
                                    if (endMilliTimePick < parentStartMilliDate){
                                        toast("세부 일정이 본 일정보다 빨리 끝납니다");
                                        return;
                                    }
                                    if (endMilliTimePick > parentEndMilliDate){
                                        toast("세부 일정이 본 일정보다 늦게 끝납니다");
                                        return;
                                    }
                                    endMilliTime = endMilliTimePick;
                                    subEndDate.setText(formater.format(formater.parse(endDate)));

                                }catch (ParseException e){

                                }
                            }
                        },hour,min,true).show();

                    }
                },year,month,day).show();
                break;

            case R.id.add_sub_plan_done:

                if (!AppUtil.isEntityString(subPlanEdit.getText().toString())){
                    editInputLayout.setError("일정을 입력하십시오");
                    Snackbar.make(v, "일정을 입력하십시오", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (!hasSetTime){
                    if (planProgress == 0){
                        toast("진행도를 설정해주세요");
                        Snackbar.make(v, "진행도를 설정해주세요", Snackbar.LENGTH_LONG).show();
                        return;
                    }

                    if (planProgressTotal > 100){
                        Snackbar.make(v, "이미 총 진행도를 지났습니다", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (startMilliTime == endMilliTime){
                        Snackbar.make(v, "시작 시간과 종료 시간이 같습니다", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (startMilliTime > endMilliTime){
                        Snackbar.make(v, "시작 시간은 종료 시간 이전이어야 합니다", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if(planProgress > delayProgress ){
                        Snackbar.make(v, "50%이상 진행되었습니다", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    planProgressTotal += planProgress;

                    if (endMilliTime == parentEndMilliDate){
                        if (planProgressTotal < 100){
                            Snackbar.make(v, "총 진행도가 100%보다 작습니다", Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                SubPlan subPlan = new SubPlan();
                if (!isEdit){
                    subPlanID = subPlans.size()+1;

                }
                Log.e("parentId",parentId+"<>"+subPlanID);
                subPlan.setId(subPlanID);
                subPlan.setParentId(parentId);
                subPlan.setContent(subPlanEdit.getText().toString());
                if (!hasSetTime){
                    subPlan.setWeight(planProgress);
                    subPlan.setStartDateMilli(startMilliTime);
                    subPlan.setEndDateMilli(endMilliTime);
                }

                try{
                    subPlanDao.save(subPlan);
                    toast("저장 완료");

                    subPlans.add(subPlan);
                    subPlanAdapter.notifyDataSetChanged();
                    if (!hasSetTime){
                        delayProgress = 100-planProgressTotal;
                        parentPlanTotalTime.setText("시간:" + AppUtil.getTotalDays(parentStartMilliDate, parentEndMilliDate) + "/" +
                                AppUtil.getTotalDays(endMilliTime, parentEndMilliDate) + "  진행도:" + delayProgress + "%");
                    }
                    hidePop(true);
                }catch (Exception e){
                    toast("저장 실패");
                    Log.e("EXP",e.getMessage());
                }

                break;
            case R.id.delete_sub_plan:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("도움말");
                alert.setMessage("삭제하시겠습니까?");
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            subPlanDao.deleteBySubPlanID(subPlanID);
                            subPlans.clear();
                            subPlans = subPlanDao.findAllByParentID(parentId);
                            subPlanAdapter.notifyDataSetChanged();
                            toast("삭제 완료");
                        } catch (Exception e){
                            toast("삭제 실패");
                        }
                    }
                });
                alert.setNegativeButton("취소", null);
                alert.setCancelable(true);
                alert.create().show();
                break;
        }
    }

    private void showPop(){
        popManagerLayout.startAnimation(bottomIn);
        popManagerLayout.setVisibility(View.VISIBLE);
        transView.setVisibility(View.VISIBLE);

        if (hasSetTime){
            titleView.setVisibility(View.VISIBLE);
            addSubPlanTimeLayout.setVisibility(View.GONE);
            addSubPlanWeightLayout.setVisibility(View.GONE);
        }else {
            addSubPlanTimeLayout.setVisibility(View.VISIBLE);
            addSubPlanWeightLayout.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);
        }
        subPlanEdit.setText("");

    }

    private void showPop(SubPlan subPlan){
        isEdit = true;
        popManagerLayout.startAnimation(bottomIn);
        popManagerLayout.setVisibility(View.VISIBLE);
        transView.setVisibility(View.VISIBLE);

        subPlanEdit.setText(subPlan.getContent());
        subStartDate.setText(formater.format(subPlan.getStartDateMilli()));
        subEndDate.setText(formater.format(subPlan.getEndDateMilli()));
        subPlanSeekBar.setProgress(subPlan.getWeight());

        planProgress = subPlan.getProgress();
        subPlanID = subPlan.getId();
        parentId = subPlan.getParentId();
        startMilliTime = subPlan.getStartDateMilli();
        endMilliTime = subPlan.getEndDateMilli();
    }
    private void hidePop(boolean isNeededToClear){
        popManagerLayout.startAnimation(bottomOut);
        popManagerLayout.setVisibility(View.GONE);
        transView.setVisibility(View.GONE);

        isEdit = false;
        if (isNeededToClear){
            popClear();
        }

    }

    private void popClear() {
        subPlanEdit.setText("");
        if (endMilliTime> parentStartMilliDate && endMilliTime <= parentEndMilliDate){
            startMilliTime = endMilliTime;
            subStartDate.setText(formater.format(endMilliTime));
            subEndDate.setText(formater.format(parentEndMilliDate));
            endMilliTime = parentEndMilliDate;
            planProgress = 100-planProgressTotal;
            subPlanSeekBar.setProgress(planProgress);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
