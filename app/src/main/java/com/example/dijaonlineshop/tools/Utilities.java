package com.example.dijaonlineshop.tools;

import android.util.Log;

import com.example.dijaonlineshop.model.UserModel;

import java.util.List;

public class Utilities {
    public static final UserModel get_logged_in_user(){
        try {
            List<UserModel> users = UserModel.listAll(UserModel.class);
            if (users==null){
                return null;
            }
            if (users.isEmpty()){
                return null;
            }
            return users.get(0);
        }catch (Exception e){
            return null;

        }
    }

}
