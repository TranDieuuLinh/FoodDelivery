package com.example.test.Domain;


public class CartDomain   {
    public String title;
    public String quantity;
    public String each_fee;
    public int pic;
    public double eachFee;

    public CartDomain() {
        // Required empty constructor for Firebase
    }

    public double getEachFee() {
        return eachFee;
    }

    public void setEachFee(double eachFee) {
        this.eachFee = eachFee;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getEach_fee() {
        return each_fee;
    }

    public void setEach_fee(String each_fee) {
        this.each_fee = each_fee;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public CartDomain(String title, String quantity, String each_fee, int pic, double eachFee) {
        this.title = title;
        this.quantity = quantity;
        this.each_fee = each_fee;
        this.pic = pic;
        this.eachFee = eachFee;
    }


}
