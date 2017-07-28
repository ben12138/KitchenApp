package com.jlkj.kitchen.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.AllCourseActivity;
import com.jlkj.kitchen.activity.ChangeInfActivity1;
import com.jlkj.kitchen.activity.RankActivity;
import com.jlkj.kitchen.global.Global;

public class MyFragment extends Fragment {

    private LinearLayout personLinearLayout;
    private LinearLayout collectLinearLayout;
    private LinearLayout downLinearLayout;
    private LinearLayout productionLinearLayout;
    private LinearLayout menuLinearLayout;

    private ImageView headimageImageView;
    private TextView usernameTextView;

    public static int CHANGESTATE = 0;
    public static int CHANGEUSER = 1;

    public static Handler handler;

    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,null);
        init(view);
        return view;
    }

    private void init(View view){
        personLinearLayout = view.findViewById(R.id.personal_inf);
        collectLinearLayout = view.findViewById(R.id.collect);
        downLinearLayout = view.findViewById(R.id.down);
        productionLinearLayout = view.findViewById(R.id.my_production);
        menuLinearLayout = view.findViewById(R.id.my_menu);
        headimageImageView = view.findViewById(R.id.headimage);
        usernameTextView = view.findViewById(R.id.username);

        Bitmap bitmap = BitmapFactory.decodeFile(Global.user.getImgurl());
        headimageImageView.setImageBitmap(bitmap);
        if(Global.user.getNickname() == null || Global.user.getNickname().equals("")){
            usernameTextView.setText(Global.user.getUsername());
        }else{
            usernameTextView.setText(Global.user.getNickname());
        }

        personLinearLayout.setOnClickListener(personalOnClickListener);
        collectLinearLayout.setOnClickListener(collectonClickLikstener);
        downLinearLayout.setOnClickListener(myOnClickListener);
        productionLinearLayout.setOnClickListener(productionOnClickListener);
        menuLinearLayout.setOnClickListener(menuOnClickListener);

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == CHANGESTATE){
                    Bitmap bitmap = (Bitmap) msg.obj;
                    headimageImageView.setImageBitmap(bitmap);
                }else if(msg.what == CHANGEUSER){
                    if(Global.user.getNickname() == null || Global.user.getNickname().equals("")){
                        usernameTextView.setText(Global.user.getUsername());
                    }else{
                        usernameTextView.setText(Global.user.getNickname());
                    }
                }
            }
        };

    }

    private View.OnClickListener personalOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ChangeInfActivity1.class);
            context.startActivity(intent);
        }
    };

    private View.OnClickListener collectonClickLikstener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,RankActivity.class);
            intent.putExtra("type","collect");
            context.startActivity(intent);
        }
    };

    private View.OnClickListener myOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, AllCourseActivity.class);
            intent.putExtra("type","mycourse");
            context.startActivity(intent);
        }
    };

    private View.OnClickListener productionOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, RankActivity.class);
            intent.putExtra("name","production");
            intent.putExtra("type","my");
            intent.putExtra("senderid",Global.user.getId());
            context.startActivity(intent);
        }
    };

    private View.OnClickListener menuOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, RankActivity.class);
            intent.putExtra("name","menu");
            intent.putExtra("type","my");
            intent.putExtra("senderid",Global.user.getId());
            context.startActivity(intent);
        }
    };

}
