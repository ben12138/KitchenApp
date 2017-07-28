package com.jlkj.kitchen.global;

import com.jlkj.kitchen.bean.Course;
import com.jlkj.kitchen.bean.UserInf;

/**
 * Created by benrui on 2017/7/5.
 */

public class Global {
    public static UserInf user = new UserInf();
    public static Course course = new Course();
    public static void copy(UserInf user){
        Global.user.setId(user.getId());
        Global.user.setNickname(user.getNickname());
        Global.user.setIntroduction(user.getIntroduction());
        Global.user.setCompany(user.getCompany());
        Global.user.setSex(user.getSex());
        Global.user.setBirthday(user.getBirthday());
        Global.user.setEmail(user.getEmail());
        Global.user.setImgurl(user.getImgurl());
        Global.user.setPassword(user.getPassword());
        Global.user.setUsername(user.getUsername());
    }
}
