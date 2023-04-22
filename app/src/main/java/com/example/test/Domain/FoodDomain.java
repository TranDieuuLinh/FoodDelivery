package com.example.test.Domain;

import java.io.Serializable;

public class FoodDomain implements Serializable {
    private int pic;
    private String title;
    private Double fee;

    public FoodDomain(int pic, String title, Double fee) {
        this.pic = pic;
        this.title = title;
        this.fee = fee;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
}
