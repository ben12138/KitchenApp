package com.jlkj.kitchen;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jlkj.kitchen.activity.LoginActivity;
import com.jlkj.kitchen.fragment.KitchenFragment;
import com.jlkj.kitchen.fragment.MainFragment;
import com.jlkj.kitchen.fragment.MyFragment;
import com.jlkj.kitchen.fragment.RankFragment;
import com.jlkj.kitchen.global.Global;

public class MainActivity extends Activity {

    private static final int PRESSED = 0;
    private static final int NORMAL = 1;

    private FragmentManager fragmentManager = null;
    private FragmentTransaction transaction = null;

    //fragment对象的创建
    private KitchenFragment kitchenFragment;
    private RankFragment rankFragment;
    private MainFragment mainFragment;
    private MyFragment myFragment;

    //下部的LinearLayout
    private LinearLayout homeLinearLayout;
    private LinearLayout kitchenLinearLayout;
    private LinearLayout rankingLinearLayout;
    private LinearLayout myLinearLayout;

    //drawer部分的组件
    private ImageView drawerHeadImageImageView;
    private TextView drawerNicknameTextView;
    private TextView drawerintroductionTextView;
    private Button drawerexitButton;
    private Button drawerchangeAccountButton;

    //LinearLayout内部的组件
    private ImageView homeImageView;
    private TextView homeTextView;
    private ImageView kitchenImageView;
    private TextView kitchenTextView;
    private ImageView rankingImageView;
    private TextView rankingTextView;
    private ImageView myImageView;
    private TextView myTextView;

    private Context context;

    public static Handler handler;
    public static int CHANGESTATE = 0;
    public static int CHANGEUSER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
    }

    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//             透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        context = this;

        kitchenFragment = new KitchenFragment();
        rankFragment = new RankFragment();
        myFragment = new MyFragment();
        mainFragment = new MainFragment();

        fragmentManager = getFragmentManager();
        transaction = fragmentManager.beginTransaction();

        if(!mainFragment.isAdded()){
            transaction.add(R.id.body_fragment,mainFragment);
        }else{
            transaction.hide(myFragment);
            transaction.hide(kitchenFragment);
            transaction.hide(rankFragment);
            transaction.show(mainFragment);
        }
        transaction.commit();

        homeLinearLayout = findViewById(R.id.home);
        homeImageView = findViewById(R.id.home_imageview);
        homeTextView = findViewById(R.id.home_textview);
        kitchenLinearLayout = findViewById(R.id.kitchen);
        kitchenImageView = findViewById(R.id.kitchen_imageview);
        kitchenTextView = findViewById(R.id.kitchen_textview);
        rankingLinearLayout = findViewById(R.id.ranking);
        rankingTextView = findViewById(R.id.ranking_textview);
        rankingImageView = findViewById(R.id.ranking_imageview);
        myLinearLayout = findViewById(R.id.my);
        myImageView = findViewById(R.id.my_imageview);
        myTextView = findViewById(R.id.my_textview);

        drawerHeadImageImageView = findViewById(R.id.drawer_menu_head_image);
        drawerNicknameTextView = findViewById(R.id.drawer_menu_nick_name);
        drawerintroductionTextView = findViewById(R.id.drawer_menu_introduction);
        drawerchangeAccountButton = findViewById(R.id.change_account);
        drawerexitButton = findViewById(R.id.exit);
        Bitmap bitmap = BitmapFactory.decodeFile(Global.user.getImgurl());
        drawerHeadImageImageView.setImageBitmap(bitmap);
        if(Global.user.getNickname() == null || Global.user.getNickname().equals("")){
            drawerNicknameTextView.setText(Global.user.getUsername());
        }else{
            drawerNicknameTextView.setText(Global.user.getNickname());
        }
        drawerintroductionTextView.setText(Global.user.getIntroduction());

        homeLinearLayout.setOnClickListener(homeLinearLayoutOnClickListener);
        kitchenLinearLayout.setOnClickListener(kitchenLinearLayoutOnClickListener);
        rankingLinearLayout.setOnClickListener(rankingLinearLayoutOnClickListener);
        myLinearLayout.setOnClickListener(myLinearLayoutOnClickListener);

        drawerchangeAccountButton.setOnClickListener(changeAccountOnClickListener);
        drawerexitButton.setOnClickListener(exitOnClickListener);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == CHANGESTATE){
                    Bitmap bitmap = (Bitmap) msg.obj;
                    drawerHeadImageImageView.setImageBitmap(bitmap);
                }else if(msg.what == CHANGEUSER){
                    if(Global.user.getNickname() == null || Global.user.getNickname().equals("")){
                        drawerNicknameTextView.setText(Global.user.getUsername());
                    }else{
                        drawerNicknameTextView.setText(Global.user.getNickname());
                    }
                    drawerintroductionTextView.setText(Global.user.getIntroduction());
                }
            }
        };

    }

    private View.OnClickListener exitOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };

    private View.OnClickListener changeAccountOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            MainActivity.this.finish();
        }
    };

    private View.OnClickListener homeLinearLayoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeBottomState(PRESSED,NORMAL,NORMAL,NORMAL);
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if(!mainFragment.isAdded()){
                transaction.add(R.id.body_fragment,mainFragment);
            }else{
                transaction.hide(myFragment);
                transaction.hide(kitchenFragment);
                transaction.hide(rankFragment);
                transaction.show(mainFragment);
            }
            transaction.commit();
        }
    };

    private View.OnClickListener kitchenLinearLayoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeBottomState(NORMAL,PRESSED,NORMAL,NORMAL);
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if(!kitchenFragment.isAdded()){
                transaction.add(R.id.body_fragment,kitchenFragment);
            }else{
                transaction.hide(myFragment);
                transaction.hide(mainFragment);
                transaction.hide(rankFragment);
                transaction.show(kitchenFragment);
            }
            transaction.commit();
        }
    };

    private View.OnClickListener rankingLinearLayoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeBottomState(NORMAL,NORMAL,PRESSED,NORMAL);
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if(!rankFragment.isAdded()){
                transaction.add(R.id.body_fragment,rankFragment);
            }else{
                transaction.hide(myFragment);
                transaction.hide(kitchenFragment);
                transaction.hide(mainFragment);
                transaction.show(rankFragment);
            }
            transaction.commit();
        }
    };

    private View.OnClickListener myLinearLayoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            changeBottomState(NORMAL,NORMAL,NORMAL,PRESSED);
            fragmentManager = getFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if(!myFragment.isAdded()){
                transaction.add(R.id.body_fragment,myFragment);
            }else{
                transaction.hide(mainFragment);
                transaction.hide(kitchenFragment);
                transaction.hide(rankFragment);
                transaction.show(myFragment);
            }
            transaction.commit();
        }
    };

    private void changeBottomState(int home,int kitchen,int ranking,int myinf){
        if(home == NORMAL){
            homeImageView.setImageResource(R.drawable.home_normal);
            homeTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }else if(home == PRESSED){
            homeImageView.setImageResource(R.drawable.home_pressed);
            homeTextView.setTextColor(context.getResources().getColor(R.color.title_background));
        }
        if(kitchen == NORMAL){
            kitchenImageView.setImageResource(R.drawable.kitchen_normal);
            kitchenTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }else if(kitchen == PRESSED){
            kitchenImageView.setImageResource(R.drawable.kitchen_pressed);
            kitchenTextView.setTextColor(context.getResources().getColor(R.color.title_background));
        }
        if(ranking == NORMAL){
            rankingImageView.setImageResource(R.drawable.ranking_normal);
            rankingTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }else if(ranking == PRESSED){
            rankingImageView.setImageResource(R.drawable.ranking_pressed);
            rankingTextView.setTextColor(context.getResources().getColor(R.color.title_background));
        }
        if(myinf == NORMAL){
            myImageView.setImageResource(R.drawable.my_normal);
            myTextView.setTextColor(context.getResources().getColor(R.color.gray));
        }else if(myinf == PRESSED){
            myImageView.setImageResource(R.drawable.my_pressed);
            myTextView.setTextColor(context.getResources().getColor(R.color.title_background));
        }
    }

}
