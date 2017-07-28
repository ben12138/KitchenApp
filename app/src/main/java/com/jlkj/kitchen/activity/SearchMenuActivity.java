package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.adapter.MenuAdapter;
import com.jlkj.kitchen.bean.Menu;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class SearchMenuActivity extends Activity {

    private ImageView backImageView;
    private EditText searchEditText;
    private ListView menusListView;
    private List<Menu> menus;

    private MenuAdapter menuAdapter;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_menu);
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
        searchEditText = findViewById(R.id.search);
        backImageView = findViewById(R.id.back);
        menusListView = findViewById(R.id.menus);
        searchEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                    if(searchEditText.getText().toString().trim().equals("")){
                        Toast.makeText(context, "关键字不能为空", Toast.LENGTH_SHORT).show();
                    }else{
                        getMenuDate(searchEditText.getText().toString());
                    }
                }
                return false;
            }
        });
        backImageView.setOnClickListener(backOnClickListener);
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SearchMenuActivity.this.finish();
        }
    };

    private void getMenuDate(String key){
        OkGo.post(Net.SEARCHMENUACTION_IP)
                .params("key",key)
                .params("type",1)
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
                            menusListView.setAdapter(menuAdapter);
                            menusListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(context,MenuInfActivity.class);
                                    intent.putExtra("menu",menus.get(i));
                                    startActivity(intent);
                                }
                            });
                        }else{
                            Toast.makeText(context,"暂无结果",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
