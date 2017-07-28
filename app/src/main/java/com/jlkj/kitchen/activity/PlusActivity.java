package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.global.Global;

public class PlusActivity extends Activity {

    private ImageView addMenuImageView;
    private ImageView addProductionImageView;
    private ImageView headImageImageView;
    private TextView usernameTextView;
    private Button backButton;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus);
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
        backButton = findViewById(R.id.back);
        headImageImageView = findViewById(R.id.headimage);
        usernameTextView = findViewById(R.id.username);
        addMenuImageView = findViewById(R.id.add_menu);
        addProductionImageView = findViewById(R.id.add_production);
        if(Global.user.getNickname() == null || Global.user.getNickname().toString().trim().equals("")){
            usernameTextView.setText(Global.user.getUsername());
        }else{
            usernameTextView.setText(Global.user.getNickname());
        }
        Bitmap bitmap = BitmapFactory.decodeFile(Global.user.getImgurl());
        headImageImageView.setImageBitmap(bitmap);
        backButton.setOnClickListener(backOnClickListener);
        addProductionImageView.setOnClickListener(addProductionOnClickListener);
        addMenuImageView.setOnClickListener(addMenuOnClickListener);
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PlusActivity.this.finish();
        }
    };

    private View.OnClickListener addMenuOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,AddMenuActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener addProductionOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,AddProductionActivity.class);
            startActivity(intent);
        }
    };

}
