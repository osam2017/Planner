package com.administrator.eventplanner.model;

public class SubPlan extends BaseModel{

    int id;
    int parentId;
    String content;
    long startDateMilli;
    long endDateMilli;
    int progress;
    int weight;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getStartDateMilli() {
        return startDateMilli;
    }

    public void setStartDateMilli(long startDateMilli) {
        this.startDateMilli = startDateMilli;
    }

    public long getEndDateMilli() {
        return endDateMilli;
    }

    public void setEndDateMilli(long endDateMilli) {
        this.endDateMilli = endDateMilli;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    public boolean validate(){

        if (startDateMilli <= 0){return false;}
        if (endDateMilli <= 0){return  false;}
        if (startDateMilli > endDateMilli){return false;}

        return true;
    }
}
