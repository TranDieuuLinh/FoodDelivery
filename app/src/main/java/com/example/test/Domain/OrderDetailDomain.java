package com.example.test.Domain;

public class OrderDetailDomain {

    private String foodNAme, foodQUantity, eachFoodTOtal, foodIMage, roundTotalEachVAlue, DELIVERYVALUE,taxVAlue,totalVAlue;

    public OrderDetailDomain(String foodNAme, String foodQUantity, String eachFoodTOtal, String foodIMage, String roundTotalEachVAlue, String DELIVERYVALUE, String taxVAlue, String totalVAlue) {
        this.foodNAme = foodNAme;
        this.foodQUantity = foodQUantity;
        this.eachFoodTOtal = eachFoodTOtal;
        this.foodIMage = foodIMage;
        this.roundTotalEachVAlue = roundTotalEachVAlue;
        this.DELIVERYVALUE = DELIVERYVALUE;
        this.taxVAlue = taxVAlue;
        this.totalVAlue = totalVAlue;
    }

    public String getFoodNAme() {
        return foodNAme;
    }

    public void setFoodNAme(String foodNAme) {
        this.foodNAme = foodNAme;
    }

    public String getFoodQUantity() {
        return foodQUantity;
    }

    public void setFoodQUantity(String foodQUantity) {
        this.foodQUantity = foodQUantity;
    }

    public String getEachFoodTOtal() {
        return eachFoodTOtal;
    }

    public void setEachFoodTOtal(String eachFoodTOtal) {
        this.eachFoodTOtal = eachFoodTOtal;
    }

    public String getFoodIMage() {
        return foodIMage;
    }

    public void setFoodIMage(String foodIMage) {
        this.foodIMage = foodIMage;
    }

    public String getRoundTotalEachVAlue() {
        return roundTotalEachVAlue;
    }

    public void setRoundTotalEachVAlue(String roundTotalEachVAlue) {
        this.roundTotalEachVAlue = roundTotalEachVAlue;
    }

    public String getDELIVERYVALUE() {
        return DELIVERYVALUE;
    }

    public void setDELIVERYVALUE(String DELIVERYVALUE) {
        this.DELIVERYVALUE = DELIVERYVALUE;
    }

    public String getTaxVAlue() {
        return taxVAlue;
    }

    public void setTaxVAlue(String taxVAlue) {
        this.taxVAlue = taxVAlue;
    }

    public String getTotalVAlue() {
        return totalVAlue;
    }

    public void setTotalVAlue(String totalVAlue) {
        this.totalVAlue = totalVAlue;
    }


}
