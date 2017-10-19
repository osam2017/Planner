package com.administrator.eventplanner.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.administrator.eventplanner.R;
import com.administrator.eventplanner.model.SubPlan;
import com.administrator.eventplanner.util.AppUtil;

import java.util.List;

public class SubPlanAdapter extends Adapter{

    Context mContext;
    List<SubPlan> subPlans;
    View.OnClickListener listener;
    OnItemClickListener onItemClickListener;
    boolean hasSetTime;
    public SubPlanAdapter(Context context, List<SubPlan> subPlans,OnItemClickListener onItemClickListener,boolean hasSetTime){
        super(context);
        this.mContext = context;
        this.subPlans = subPlans;
        this.onItemClickListener = onItemClickListener;
        this.hasSetTime = hasSetTime;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_plan_add,parent,false);
        RecyclerView.ViewHolder holder = new SubPlanHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        SubPlan subPlan = subPlans.get(position);
        SubPlanHolder subPlanHolder = (SubPlanHolder)holder;
        subPlanHolder.subPlanNo.setText(position + 1 + "");
        subPlanHolder.subPlanContent.setText(subPlan.getContent());
        if (!hasSetTime){
            subPlanHolder.subPlanWeight.setText(subPlan.getWeight()+"");
            subPlanHolder.subPlanStartDate.setText(formater.format(subPlan.getStartDateMilli()));
            subPlanHolder.subPlanEndDate.setText(formater.format(subPlan.getEndDateMilli()));
            subPlanHolder.subPlanTotalTime.setText("TOTAL:" + AppUtil.getTotalDays(subPlan.getStartDateMilli(), subPlan.getEndDateMilli()));
        }
        subPlanHolder.subPlanCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subPlans.size();
    }

    class SubPlanHolder extends RecyclerView.ViewHolder {
        TextView subPlanNo,subPlanContent,subPlanStartDate,subPlanEndDate,subPlanArrowIc,subPlanWeight,subPlanTotalTime;
        CardView subPlanCardView;
        OnItemClickListener onItemClickListener;
        RelativeLayout subPlanTimeLayout;
        TextView symbol;
        public SubPlanHolder(View itemView) {
            super(itemView);
            subPlanNo = (TextView)itemView.findViewById(R.id.sub_plan_No);
            subPlanContent = (TextView)itemView.findViewById(R.id.subplan_title);
            subPlanStartDate = (TextView)itemView.findViewById(R.id.subproject_startdate);
            subPlanStartDate.setTypeface(romanFont);
            subPlanEndDate = (TextView)itemView.findViewById(R.id.subproject_enddate);
            subPlanEndDate.setTypeface(romanFont);
            subPlanArrowIc = (TextView)itemView.findViewById(R.id.subproject_arrowdate_icon);
            subPlanArrowIc.setTypeface(iconFont);
            subPlanWeight = (TextView)itemView.findViewById(R.id.subplan_percent);
            subPlanWeight.setTypeface(romanFont);

            subPlanTotalTime = (TextView)itemView.findViewById(R.id.sub_plan_totaltime);
            subPlanCardView = (CardView)itemView.findViewById(R.id.sub_plan_edit_cardview);
            symbol = (TextView)itemView.findViewById(R.id.subplan_percent_symbol);
            subPlanTimeLayout = (RelativeLayout)itemView.findViewById(R.id.subplan_time);

            if (hasSetTime){
                subPlanContent.setGravity(Gravity.CENTER_VERTICAL);
                subPlanTimeLayout.setVisibility(View.GONE);
                subPlanWeight.setVisibility(View.GONE);
                symbol.setVisibility(View.GONE);
                subPlanTotalTime.setVisibility(View.GONE);
            }

        }

    }

    public interface OnItemClickListener{
        public void onItemClick(int position);
    }

}
