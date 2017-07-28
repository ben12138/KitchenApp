package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.jlkj.kitchen.MainActivity;
import com.jlkj.kitchen.R;
import com.jlkj.kitchen.fragment.MyFragment;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.jlkj.kitchen.util.Util;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class ChangeInfActivity1 extends Activity {

    private ImageView headImageImageView;
    private LinearLayout myInfLinearLayout;
    private TextView nicknameTextView;
    private Button backButton;

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;

    private Uri imageUri;
    private File headImageFile;

    private Context context;

    public static Handler handler;

    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_inf1);
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
        headImageImageView = findViewById(R.id.headimage);
        myInfLinearLayout = findViewById(R.id.myInf);
        nicknameTextView = findViewById(R.id.nickname);
        backButton = findViewById(R.id.back);
        if(Global.user.getNickname() == null || Global.user.getNickname().equals("")){
            nicknameTextView.setText(Global.user.getUsername());
        }else{
            nicknameTextView.setText(Global.user.getNickname());
        }
        Bitmap bitmap = BitmapFactory.decodeFile(Global.user.getImgurl());
        headImageImageView.setImageBitmap(bitmap);
        headImageImageView.setOnClickListener(headImageOnClickListener);
        myInfLinearLayout.setOnClickListener(myInfOnClickListener);
        backButton.setOnClickListener(backOnClickListener);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                nicknameTextView.setText(Global.user.getNickname());
            }
        };
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ChangeInfActivity1.this.finish();
        }
    };

    private View.OnClickListener headImageOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View myview = inflater.inflate(R.layout.choose_photo_pop_window,null,false);
            Button cameraButton = (Button) myview.findViewById(R.id.camera);
            Button xiangceButton = (Button) myview.findViewById(R.id.xiangce);
            Button cancelButton = (Button) myview.findViewById(R.id.cancel);
            final PopupWindow popup = new PopupWindow(myview, WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
            ColorDrawable dw = new ColorDrawable(0xb0000000);
            popup.setBackgroundDrawable(dw);
            popup.setFocusable(true);
            popup.showAtLocation(findViewById(R.id.myInf), Gravity.BOTTOM, 0, 0);
            popup.setAnimationStyle(R.style.mypopwindow_anim_style);
            popup.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

                @Override
                public void onDismiss() {
                    popup.dismiss();
                }
            });
            cancelButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    popup.dismiss();
                }
            });
            cameraButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Intent openCameraIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    headImageFile = new File(Environment
                            .getExternalStorageDirectory(), Global.user.getEmail()+".png");
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
                    popup.dismiss();
                }
            });
            xiangceButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
                    openAlbumIntent.setType("image/*");
                    startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                    popup.dismiss();
                }
            });
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
            Bitmap headImage = extras.getParcelable("data");
            headImage = Util.toRoundBitmap(headImage, imageUri);
            headImageImageView.setImageBitmap(headImage);
            headImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] image = baos.toByteArray();
            headImageFile = new File(Util.savePhoto(headImage, Environment.getExternalStorageDirectory()+ "/kitchen/headImage/", Global.user.getEmail()));
            Message msg = new Message();
            msg.obj = headImage;
            msg.what = MainActivity.CHANGESTATE;
            MainActivity.handler.sendMessage(msg);
            Message msg1 = new Message();
            msg1.obj = headImage;
            msg1.what = MyFragment.CHANGESTATE;
            MyFragment.handler.sendMessage(msg1);
            if(image != null){
                uploadFile();
            }
        }
    }

    private View.OnClickListener myInfOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context,ChangeInfActivity2.class);
            startActivity(intent);
        }
    };

    private void uploadFile(){
        OkGo.post(Net.UPDATEINF_URL)
                .params("step",1)
                .params("headimage",headImageFile)
                .params("user.email",Global.user.getEmail())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            Toast.makeText(context,"上传成功",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
