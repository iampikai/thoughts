package com.suvankar.thoughts;

public class ThoughtModel implements Cloneable{

    String text;
    String time;

    public ThoughtModel(String text, String time) {
        this.text = text;
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return "ThoughtModel{" +
                "text='" + text + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
