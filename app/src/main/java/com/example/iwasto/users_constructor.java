package com.example.iwasto;
public class users_constructor {

    private String user_id;
    private String major_area;
    private String barangay;
    private String full_name;

    public users_constructor(String user_id, String major_area,
                             String barangay, String full_name)
    {
        this.user_id = user_id;
        this.major_area  = major_area;
        this.barangay  = barangay;
        this.full_name  = full_name;

    }

    public String user_id() {
        return user_id;
    }
    public String major_area() {return major_area;}
    public String barangay() {return barangay;}
    public String full_name() {return full_name;}


}
