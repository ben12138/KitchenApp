package com.jlkj.kitchen;

import android.app.Application;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by benrui on 2017/7/5.
 */

public class MyApplication extends Application{

    public static List<String> category1;
    public static List<String> category2;
    public static List<String> category3;

    @Override
    public void onCreate() {
        super.onCreate();
        OkGo.init(this);
        OkGo.getInstance()

                // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                .debug("OkGo", Level.INFO, true)
                .setRetryCount(3);
        init();
    }

    public void init(){
        category1 = new ArrayList<>();
        category1.add("鲁菜");
        category1.add("浙菜");
        category1.add("川菜");
        category1.add("粤菜");
        category1.add("苏菜");
        category1.add("闽菜");
        category1.add("湘菜");
        category1.add("徽菜");
        category1.add("家常菜");
        category2 = new ArrayList<>();
        category2.add("凉菜");
        category2.add("热菜");
        category2.add("甜品");
        category2.add("汤");
        category2.add("主食");
        category2.add("自制");
        category2.add("饮品");
        category3 = new ArrayList<>();
        category3.add("水果");
        category3.add("蛋类");
        category3.add("肉类");
        category3.add("奶类");
        category3.add("腌肉腌菜");
        category3.add("粮油干果");
        category3.add("调味辅料");
    }

}
