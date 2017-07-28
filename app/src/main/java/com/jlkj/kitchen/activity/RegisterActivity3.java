package com.jlkj.kitchen.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jlkj.kitchen.MainActivity;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.database.DatabaseHelper;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.jlkj.kitchen.util.Util;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

import static android.os.Environment.*;

public class RegisterActivity3 extends Activity {

    private Context context;
    private LinearLayout cameraLinearLayout;
    private LinearLayout xiangceLinearLayout;
    private ImageView headImageImageView;
    private EditText passwordEditText;
    private EditText confirmPasswordEdutEditText;
    private EditText nicknameEditText;
    private TextView birthdayTextView;
    private String birthday;
    private TextView sexTextView;
    private int sex = 0;
    private EditText schoolEditText;
    private EditText personalIntroductionEditText;
    private Button submitButton;
    private Button backButton;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private Uri imageUri;
    private byte[] image = null;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private Bitmap headImage;
    private File headImageFile;
    private String email;
    private boolean uploadImageSuccess = false;
    private boolean uploadInfSuccess = false;
    private Handler handler = null;
    private int submitSuccess = 0;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);
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
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        cameraLinearLayout = (LinearLayout) findViewById(R.id.camera);
        cameraLinearLayout.setOnClickListener(cameraOnClikListener);
        xiangceLinearLayout = (LinearLayout) findViewById(R.id.xiangce);
        xiangceLinearLayout.setOnClickListener(xiangceOnClickListener);
        headImageImageView = (ImageView) findViewById(R.id.head_image);
        passwordEditText = (EditText) findViewById(R.id.password);
        confirmPasswordEdutEditText = (EditText) findViewById(R.id.confirm_password);
        nicknameEditText = (EditText) findViewById(R.id.nickname);
        birthdayTextView = (TextView) findViewById(R.id.birthday);
        birthdayTextView.setOnClickListener(birthdayTextViewOnClickListener);
        sexTextView = (TextView) findViewById(R.id.sex);
        sexTextView.setOnClickListener(sexTextViewOnCLickListener);
        schoolEditText = (EditText) findViewById(R.id.school);
        personalIntroductionEditText = (EditText) findViewById(R.id.personal_introduction);
        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(submitButtonOnClickListener);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if(msg.what == submitSuccess){
                    Intent intent = new Intent(context,MainActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        this.finish();
    }

    private OnClickListener backButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            RegisterActivity3.this.finish();
        }
    };

    private OnClickListener submitButtonOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            if(isRight()){
                submitInf();
            }else{
                Toast.makeText(context, "请输入正确信息", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private OnClickListener birthdayTextViewOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            SetDateDialog s = new SetDateDialog();
            s.show(getFragmentManager(), "选择日期");
        }
    };

    private OnClickListener sexTextViewOnCLickListener = new OnClickListener() {

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

    private OnClickListener xiangceOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
            openAlbumIntent.setType("image/*");
            startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
        }
    };

    private OnClickListener cameraOnClikListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent openCameraIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            headImageFile = new File(
                    getExternalStorageDirectory(), email+".jpg");
            if(headImageFile.exists()){
                headImageFile.delete();
            }
            try {
                headImageFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            imageUri = Uri.fromFile(headImageFile);

            openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(openCameraIntent, TAKE_PICTURE);
        }
    };

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PICTURE:
                startPhotoZoom(imageUri);
                break;
            case CHOOSE_PICTURE:
                if(data!=null){
                    startPhotoZoom(data.getData());
                }
                break;
            case CROP_SMALL_PICTURE:
                if (data != null) {
                    setImageToView(data);
                }
                break;
            default:
                break;

        }
    };

    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        imageUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            headImage = extras.getParcelable("data");
            headImage = Util.toRoundBitmap(headImage, imageUri);
            headImageImageView.setImageBitmap(headImage);
            headImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
            image = baos.toByteArray();
            headImageFile = new File(Util.savePhoto(headImage, getExternalStorageDirectory()+ "/Lesson/headImage/", email));
        }
    }

    class SetDateDialog extends DialogFragment implements OnDateSetListener {

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

    public boolean isRight(){
        if(headImage == null ||
                passwordEditText.getText().toString() == null ||
                passwordEditText.getText().toString().equals("")||
                !passwordEditText.getText().toString().equals(confirmPasswordEdutEditText.getText().toString()) ||
                birthday == null || sex == -1){
            return false;
        }
        return true;

    }

    private void submitInf(){
        OkGo.post(Net.REGISTER_URL)
                .params("step",3)
                .params("upload",headImageFile)
                .params("user.username",email)
                .params("user.password",passwordEditText.getText().toString().trim())
                .params("user.email",email)
                .params("user.nickname",nicknameEditText.getText().toString().trim())
                .params("user.birthday",birthdayTextView.getText().toString().trim())
                .params("user.sex",sex)
                .params("user.company",schoolEditText.getText().toString().trim())
                .params("user.introduction",personalIntroductionEditText.getText().toString().trim())
                .params("user.imgurl",Net.IP+"/headimage/"+email+".png")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject json = JSONObject.fromString(s);
                        if(json.getInt("code") == 200){
                            insertUser();
                            Intent intent = new Intent(context,LoginActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(context,"初始化失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toast.makeText(context,"初始化失败",Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void insertUser(){
        Util.savePhoto(headImage, getExternalStorageDirectory()+ "/kitchen/headImage/", email);
    }

    private void uploadFile(){


    }
}

