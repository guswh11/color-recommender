package edu.skku.color_recommender;

import java.util.ArrayList;

public class Color {

    private String name;
    private RGB rgb;
    private ArrayList<String> tag;

    public Color() {
    }

    public Color(String name, RGB rgb) {
        this.name = name;
        this.rgb = rgb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RGB getRgb() {
        return rgb;
    }

    public void setRgb(RGB rgb) {
        this.rgb = rgb;
    }

    public ArrayList<String> getTag() {
        return tag;
    }

    public void setTag(ArrayList<String> tag) {
        this.tag = tag;
    }
}
