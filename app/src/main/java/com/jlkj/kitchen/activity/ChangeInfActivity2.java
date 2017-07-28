package com.jlkj.kitchen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Message;
import android.support.constraint.solver.Goal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.jlkj.kitchen.MainActivity;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.database.DatabaseHelper;
import com.jlkj.kitchen.fragment.MyFragment;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

import static android.os.Environment.getExternalStorageDirectory;

public class ChangeInfActivity2 extends Activity {

    private Button backButton;
    private TextView cancelTextView;
    private TextView editTextView;
    private TextView okTextView;
    private EditText nicknameEditText;
    private TextView sexTextView;
    private TextView birthdayTextView;
    private EditText schoolEditText;
    private TextView emailTextView;
    private EditText introductionEditText;

    private String nickname;
    private String birthday;
    private int sex;
    private String school;
    private String introduction;
    //有没有正在修改的状态，如果正在修改，则back键效果等于取消，否则等于back，0表示被修改中，1表示没有修改
    private static boolean change = false;

    private Context context;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_inf2);
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
        dbHelper = new DatabaseHelper(context,"user.db3",null,1);
        backButton = (Button) findViewById(R.id.back);
        backButton.setOnClickListener(backButtononClickListener);
        cancelTextView = (TextView) findViewById(R.id.cancel);
        cancelTextView.setOnClickListener(cancelButtonOnClickListener);
        cancelTextView.setVisibility(View.GONE);
        editTextView = (TextView) findViewById(R.id.edit);
        editTextView.setOnClickListener(editButtonOnClickListener);
        okTextView = (TextView) findViewById(R.id.ok);
        okTextView.setOnClickListener(okButtonOnClickListener);
        okTextView.setVisibility(View.GONE);
        nicknameEditText = (EditText) findViewById(R.id.nickname);
        nicknameEditText.setEnabled(false);
        nicknameEditText.setText(Global.user.getNickname());
        sexTextView = (TextView) findViewById(R.id.sex);
        sexTextView.setOnClickListener(sexTextViewOnClickListener);
        sexTextView.setClickable(false);
        if(Global.user.getSex() == 0){
            sexTextView.setText("男");
        }else if(Global.user.getSex() == 1){
            sexTextView.setText("女");
        }
        sex = Global.user.getSex();
        birthdayTextView = (TextView) findViewById(R.id.birthday);
        birthdayTextView.setOnClickListener(birthdayTextViewOnClickListener);
        birthdayTextView.setClickable(false);
//		System.out.println(GOLBALVALUE.user.getBirthday());
        birthdayTextView.setText(Global.user.getBirthday());
//		System.out.println(GOLBALVALUE.user.getBirthday());
        emailTextView = (TextView) findViewById(R.id.email);
        emailTextView.setEnabled(false);
        emailTextView.setText(Global.user.getEmail());
        introductionEditText = (EditText) findViewById(R.id.introduction);
        introductionEditText.setEnabled(false);
        introductionEditText.setText(Global.user.getIntroduction());
        schoolEditText = (EditText) findViewById(R.id.school);
        schoolEditText.setEnabled(false);
        schoolEditText.setText(Global.user.getCompany());
    }

    private OnClickListener backButtononClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            ChangeInfActivity2.this.finish();
        }
    };
    private OnClickListener cancelButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            backButton.setVisibility(View.VISIBLE);
            cancelTextView.setVisibility(View.GONE);
            editTextView.setVisibility(View.VISIBLE);
            okTextView.setVisibility(View.GONE);
            if(Global.user.getSex() == 0){
                sexTextView.setText("男");
            }else if(Global.user.getSex() == 1){
                sexTextView.setText("女");
            }
            sexTextView.setClickable(false);
            nicknameEditText.setText(Global.user.getNickname());
            nicknameEditText.setEnabled(false);
            birthdayTextView.setText(Global.user.getBirthday());
            birthdayTextView.setClickable(false);
            introductionEditText.setText(Global.user.getIntroduction());
            introductionEditText.setEnabled(false);
            schoolEditText.setText(Global.user.getCompany());
            schoolEditText.setEnabled(false);
        }
    };
    private OnClickListener okButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            cancelTextView.setVisibility(View.GONE);
            backButton.setVisibility(View.VISIBLE);
            okTextView.setVisibility(View.GONE);
            editTextView.setVisibility(View.VISIBLE);
            schoolEditText.setEnabled(false);
            introductionEditText.setEnabled(false);
            birthdayTextView.setClickable(false);
            sexTextView.setClickable(false);
            nicknameEditText.setEnabled(false);
            change = false;
            submit();
        }
    };
    private OnClickListener editButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            backButton.setVisibility(View.GONE);
            cancelTextView.setVisibility(View.VISIBLE);
            editTextView.setVisibility(View.GONE);
            okTextView.setVisibility(View.VISIBLE);
            nicknameEditText.setEnabled(true);
            sexTextView.setClickable(true);
            birthdayTextView.setClickable(true);
            schoolEditText.setEnabled(true);
            introductionEditText.setEnabled(true);
            change = true;
        }
    };
    private OnClickListener sexTextViewOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("性别");
            final String str[] = { "男", "女" };
            builder.setItems(str, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    switch (which) {
                        case 0:
                            sex = 0;
                            sexTextView.setText("男");
                            break;
                        case 1:
                            sex = 1;
                            sexTextView.setText("女");
                            break;
                    }
                }
            });
            builder.show();
        }
    };
    private OnClickListener birthdayTextViewOnClickListener = new OnClickListener() {

        @SuppressLint("NewApi")
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            SetDateDialog s = new SetDateDialog();
            s.show(getFragmentManager(), "选择日期");
        }
    };
    @SuppressLint("NewApi")
    class SetDateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @SuppressLint("NewApi")
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this,
                    year, month, day);
            return dpd;
        }

        @Override
        public void onDateSet(DatePicker v, int year, int month, int day) {
            // TODO Auto-generated method stub
            birthdayTextView
                    .setText(year + "年" + (month + 1) + "月" + day + "日");
            birthday = year + "年" + (month + 1) + "月" + day + "日";
        }

    }

    private void upDateUset(){
        nickname = nicknameEditText.getText().toString().trim();
        school = schoolEditText.getText().toString().trim();
        introduction = introductionEditText.getText().toString().trim();
        Global.user.setNickname(nickname);
        Global.user.setSex(sex);
        Global.user.setBirthday(birthday);
        Global.user.setCompany(school);
        Global.user.setIntroduction(introduction);
        Message msg = new Message();
        msg.what = MainActivity.CHANGEUSER;
        MainActivity.handler.sendMessage(msg);
        Message msg1 = new Message();
        msg1.what = MyFragment.CHANGEUSER;
        MyFragment.handler.sendMessage(msg1);
        Message msg3 = new Message();
        ChangeInfActivity1.handler.sendMessage(msg3);
        saveUser();
    }
    public void onBackPressed() {
        if(change){
            backButton.setVisibility(View.VISIBLE);
            cancelTextView.setVisibility(View.GONE);
            editTextView.setVisibility(View.VISIBLE);
            okTextView.setVisibility(View.GONE);
            if(Global.user.getSex() == 0){
                sexTextView.setText("男");
            }else if(Global.user.getSex() == 1){
                sexTextView.setText("女");
            }
            sexTextView.setClickable(false);
            nicknameEditText.setText(Global.user.getNickname());
            nicknameEditText.setEnabled(false);
            birthdayTextView.setText(Global.user.getBirthday());
            birthdayTextView.setClickable(false);
            introductionEditText.setText(Global.user.getIntroduction());
            introductionEditText.setEnabled(false);
            schoolEditText.setText(Global.user.getCompany());
            schoolEditText.setEnabled(false);
            change = false;
        }else{

            ChangeInfActivity2.this.finish();
        }
    };

    private void saveUser(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL("insert into user values(null,?,?,?,?,?,?,?,?,?,?)", new String[]{
                Global.user.getId()+"",
                Global.user.getUsername(),
                Global.user.getPassword(),
                Global.user.getNickname(),
                getExternalStorageDirectory()+ "/kitchen/headImage/"+Global.user.getEmail()+".png",
                Global.user.getEmail(),
                Global.user.getBirthday(),
                Global.user.getSex()+"",
                Global.user.getCompany(),
                Global.user.getIntroduction()
        });
        db.close();
        dbHelper.close();
    }

    private void submit(){
        OkGo.post(Net.UPDATEINF_URL)
                .params("step",2)
                .params("user.id",Global.user.getId())
                .params("user.username",Global.user.getUsername())
                .params("user.password",Global.user.getPassword())
                .params("user.nickname",nicknameEditText.getText().toString().trim())
                .params("user.imgurl",Net.IP+"headimage/"+Global.user.getEmail()+".png")
                .params("user.email",Global.user.getEmail())
                .params("user.birthday",birthdayTextView.getText().toString().trim())
                .params("user.introduction",introductionEditText.getText().toString().trim())
                .params("user.sex",sex)
                .params("user.company",schoolEditText.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = JSONObject.fromString(s);
                        if(json.getInt("code") == 200){
                            upDateUset();
                        }
                    }
                });
    }

}
