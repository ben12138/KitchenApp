package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.File;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

public class AddProductionActivity extends Activity {

    private static final int IMAGE = 1;

    private Context context;

    private ImageView backImageView;
    private TextView sendTextView;
    private EditText descriptionEditText;
    private ImageView imgImageView;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_production);
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
        imgImageView = findViewById(R.id.img);
        descriptionEditText = findViewById(R.id.description);
        backImageView.setOnClickListener(backOnClickListener);
        imgImageView.setOnClickListener(imgOnClickListener);
        sendTextView = findViewById(R.id.send);
        sendTextView.setOnClickListener(sendOnClickListener);
    }

    private View.OnClickListener backOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AddProductionActivity.this.finish();
        }
    };

    private View.OnClickListener imgOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE);
        }
    };

    private View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(descriptionEditText.getText().toString().trim().equals("")||path == null){
                Toast.makeText(context,"请输入完整信息",Toast.LENGTH_SHORT).show();
            }else{
                send();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            path = c.getString(columnIndex);
            Bitmap bm = BitmapFactory.decodeFile(path);
            addPics(bm);
            c.close();
        }
    }

    public void addPics(Bitmap bm) {
        imgImageView.setBackground(new BitmapDrawable(bm));
    }

    private void send(){
        String key = UUID.randomUUID().toString();
        OkGo.post(Net.ADDPRODUCTIONACTION_IP)
                .params("production.senderid", Global.user.getId())
                .params("production.description",descriptionEditText.getText().toString())
                .params("production.imgurl",Net.IP+"production/"+Global.user.getId()+"_"+key+".png")
                .params("production.praise",0)
                .params("key",key)
                .params("img",new File(path))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        JSONObject result = JSONObject.fromString(s);
                        if(result.getInt("code") == 200){
                            Toast.makeText(context,"发表成功",Toast.LENGTH_SHORT).show();
                            AddProductionActivity.this.finish();
                        }else{
                            Toast.makeText(context,"发表失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
