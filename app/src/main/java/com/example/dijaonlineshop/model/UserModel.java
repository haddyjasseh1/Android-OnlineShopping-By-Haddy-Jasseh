package com.example.dijaonlineshop.model;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

public class UserModel  extends SugarRecord {

    public String first_name="";
    public String last_name="";
    public String email="";
    public String passWord="";
    public String contact="";
    public String address="";
    public String gender="";
    public String prof_photo="";
    public String user_type="";
    public String reg_date="";

    @Unique
    public String user_id="";

}
