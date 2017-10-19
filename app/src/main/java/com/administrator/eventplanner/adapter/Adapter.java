package com.administrator.eventplanner.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.administrator.eventplanner.db.BaseDb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public Typeface iconFont, romanFont,newIcFont;
    public LayoutInflater inflater = null;
    Context context;
    ArrayList<HashMap<String, String>> data;
    public DateFormat formater = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public Adapter(Context context){
        this.context = context;
        init();
    }
    public Adapter(Context context,List<?> data,BaseDb daoObj){
        this.context = context;
        init();
    }
    public Adapter(Context context, ArrayList<HashMap<String, String>> data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        init();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void init(){
        inflater    = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(iconFont == null){
            iconFont=Typeface.createFromAsset(context.getAssets(), "iconfont.ttf") ;
        }
        if(romanFont ==null){
            romanFont =Typeface.createFromAsset(context.getAssets(), "typenewroman.TTF") ;
        }
        if(newIcFont == null){
            newIcFont = Typeface.createFromAsset(context.getAssets(), "newiconfont.ttf") ;
        }
    }

}
