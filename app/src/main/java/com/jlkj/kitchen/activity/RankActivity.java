package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.adapter.MenuAdapter;
import com.jlkj.kitchen.adapter.ProductionAdapter;
import com.jlkj.kitchen.bean.Menu;
import com.jlkj.kitchen.bean.Production;
import com.jlkj.kitchen.bean.Question;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class RankActivity extends Activity {

    private Context context;
    private ImageView backImageView;
    private ListView rankListView;
    private List<Production> productions;
    private List<Menu> menus;
    private TextView titleTextView;

    private ProductionAdapter productionAdapter;
    private MenuAdapter menuAdapter;

    public static Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        init();
    }

    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        context = this;
        backImageView = findViewById(R.id.back);
        rankListView = findViewById(R.id.rank);
        backImageView.setOnClickListener(backOnClickListener);
        titleTextView = findViewById(R.id.title);
        Intent intent = getIntent();
        if("category".equals(intent.getStringExtra("type"))){
            int category1 = intent.getIntExtra("category1",0);
            int category2 = intent.getIntExtra("category2",0);
            int category3 = intent.getIntExtra("category3",0);
            String name = intent.getStringExtra("name");
            titleTextView.setText(name);
            getData(category1,category2,category3);
        }else if(intent.getStringExtra("type").equals("collect")){
            titleTextView.setText("我的收藏");
            getCollectMenuDate(Global.user.getId());
        }else if("my".equals(intent.getStringExtra("type"))){
            int senderid = intent.getIntExtra("senderid",0);
            if (intent.getStringExtra("name").equals("menu")) {
                titleTextView.setText("我的菜谱");
                getMenuDate(senderid);
            } else if (intent.getStringExtra("name").equals("production")) {
                titleTextView.setText("我的作品");
                getProductionData(senderid);
            }
        }else {
            if (intent.getStringExtra("name").equals("menu")) {
                getMenuDate();
            } else if (intent.getStringExtra("name").equals("production")) {
                getProductionData();
            }
        }
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                productions.get(msg.what).setPraise(productions.get(msg.what).getPraise()+1);
            }
        };
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RankActivity.this.finish();
        }
    };

    private void getMenuDate(){
        OkGo.post(Net.GETMENUACTION_IP)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            menus = new ArrayList<Menu>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                menus.add(gson.fromJson(array.getJSONObject(i).toString(),Menu.class));
                            }
                            menuAdapter = new MenuAdapter(context,R.layout.menu_item,menus);
                            rankListView.setAdapter(menuAdapter);
                            rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,MenuInfActivity.class);
                                    intent.putExtra("menu",menus.get(i));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context,"暂无排行",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getProductionData(){
        OkGo.post(Net.GETPRODUCTIONACTION_IP)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            productions = new ArrayList<Production>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                productions.add(gson.fromJson(array.getJSONObject(i).toString(),Production.class));
                            }
                            productionAdapter = new ProductionAdapter(context,R.layout.production_menu,productions);
                            rankListView.setAdapter(productionAdapter);
                            rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,ProductionInfActivity.class);
                                    intent.putExtra("production",productions.get(i));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context,"暂无排行",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getMenuDate(int senderid){
        OkGo.post(Net.GETMYMENU_IP)
                .params("senderid",senderid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            menus = new ArrayList<Menu>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                menus.add(gson.fromJson(array.getJSONObject(i).toString(),Menu.class));
                            }
                            menuAdapter = new MenuAdapter(context,R.layout.menu_item,menus);
                            rankListView.setAdapter(menuAdapter);
                            rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,MenuInfActivity.class);
                                    intent.putExtra("menu",menus.get(i));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context,"暂无排行",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getCollectMenuDate(int id){
        OkGo.post(Net.GETCOLLECTIONACTION_IP)
                .params("userid", Global.user.getId())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            menus = new ArrayList<Menu>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                menus.add(gson.fromJson(array.getJSONObject(i).toString(),Menu.class));
                            }
                            menuAdapter = new MenuAdapter(context,R.layout.menu_item,menus);
                            rankListView.setAdapter(menuAdapter);
                            rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,MenuInfActivity.class);
                                    intent.putExtra("menu",menus.get(i));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context,"暂无收藏",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getProductionData(int senderid){
        OkGo.post(Net.GETMYPRODUCTION_IP)
                .params("senderid",senderid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            productions = new ArrayList<Production>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                productions.add(gson.fromJson(array.getJSONObject(i).toString(),Production.class));
                            }
                            productionAdapter = new ProductionAdapter(context,R.layout.production_menu,productions);
                            rankListView.setAdapter(productionAdapter);
                            rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,ProductionInfActivity.class);
                                    intent.putExtra("production",productions.get(i));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context,"暂无排行",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getData(int category1,int category2,int category3){
        OkGo.post(Net.GETCATEGORYENUACTION_IP)
                .params("category1",category1)
                .params("category2",category2)
                .params("category3",category3)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            menus = new ArrayList<Menu>();
                            JSONArray array = result.getJSONArray("data");
                            Gson gson = new Gson();
                            for(int i=0;i<array.length();i++){
                                menus.add(gson.fromJson(array.getJSONObject(i).toString(),Menu.class));
                            }
                            menuAdapter = new MenuAdapter(context,R.layout.menu_item,menus);
                            rankListView.setAdapter(menuAdapter);
                            rankListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,MenuInfActivity.class);
                                    intent.putExtra("menu",menus.get(i));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context,"暂无搜索结果",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
