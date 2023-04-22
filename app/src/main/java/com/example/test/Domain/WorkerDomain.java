package com.example.test.Domain;

public class WorkerDomain {
    private String email;
    private String userAddress;

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public WorkerDomain(String email, String userAddress) {
        this.email = email;
        this.userAddress = userAddress;
    }
}
