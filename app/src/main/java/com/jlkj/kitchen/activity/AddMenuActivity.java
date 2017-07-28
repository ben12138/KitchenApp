package com.jlkj.kitchen.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.global.Global;
import com.jlkj.kitchen.net.Net;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;

import net.sf.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Response;

public class AddMenuActivity extends Activity {

    private static final int IMAGE = 1;

    private Context context;

    private Button backButton;
    private ImageView coverImageView;
    private EditText titleEditText;
    private EditText stroyEditText;
    private EditText foodEditText;
    private EditText step1EditText;
    private EditText step2EditText;
    private EditText step3EditText;
    private ImageView pic1ImageView;
    private ImageView pic2ImageView;
    private ImageView pic3ImageView;
    private Button sendButton;
    private Spinner category1;
    private Spinner category2;
    private Spinner category3;

    private int category1value=0;
    private int category2value=0;
    private int category3value=0;

    private ArrayAdapter<CharSequence> adapter1;
    private ArrayAdapter<CharSequence> adapter2;
    private ArrayAdapter<CharSequence> adapter3;

    private String coverImagePath;
    private String pic1ImagePath;
    private String pic2ImagePath;
    private String pic3ImagePath;

    private String[] imagePath = new String[4];
    int picNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu);
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
        coverImageView = findViewById(R.id.cover);
        titleEditText = findViewById(R.id.title);
        stroyEditText = findViewById(R.id.stroy);
        foodEditText = findViewById(R.id.food);
        step1EditText = findViewById(R.id.step1);
        step2EditText = findViewById(R.id.step2);
        step3EditText = findViewById(R.id.step3);
        pic1ImageView = findViewById(R.id.pic1);
        pic2ImageView = findViewById(R.id.pic2);
        pic3ImageView = findViewById(R.id.pic3);
        sendButton = findViewById(R.id.send);
        category1 = findViewById(R.id.category1);
        category2 = findViewById(R.id.category2);
        category3 = findViewById(R.id.category3);

        adapter1 = ArrayAdapter.createFromResource(this,R.array.category1,android.R.layout.simple_spinner_item);
        adapter2 = ArrayAdapter.createFromResource(this,R.array.category2,android.R.layout.simple_spinner_item);
        adapter3 = ArrayAdapter.createFromResource(this,R.array.category3,android.R.layout.simple_spinner_item);

        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        category1.setAdapter(adapter1);
        category2.setAdapter(adapter2);
        category3.setAdapter(adapter3);

        category1.setOnItemSelectedListener(category1OnItemSelectedListener);
        category2.setOnItemSelectedListener(category2OnItemSelectedListener);
        category3.setOnItemSelectedListener(category3OnItemSelectedListener);

        coverImageView.setOnClickListener(coverOnClickListener);
        pic1ImageView.setOnClickListener(pic1OnClickListener);
        pic2ImageView.setOnClickListener(pic2OnClickListener);
        pic3ImageView.setOnClickListener(pic3OnClickListener);

        sendButton.setOnClickListener(sendOnClickListener);
        backButton.setOnClickListener(backButtonOnClickListener);

    }

    private View.OnClickListener backButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AddMenuActivity.this.finish();
        }
    };

    private View.OnClickListener sendOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(titleEditText.getText().toString().trim().equals("")||
                    stroyEditText.getText().toString().trim().equals("")||
                    foodEditText.getText().toString().trim().equals("")||
                    step1EditText.getText().toString().trim().equals("")||
                    step2EditText.getText().toString().trim().equals("")||
                    step3EditText.getText().toString().trim().equals("")){
                Toast.makeText(context,"请输入完整信息",Toast.LENGTH_SHORT).show();
            }else{
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
                String key = UUID.randomUUID().toString();
                OkGo.post(Net.ADDMENUACTION_IP)
                        .params("menu.senderid", Global.user.getId())
                        .params("menu.title",titleEditText.getText().toString().trim())
                        .params("menu.cover",Net.IP+"/cover/"+Global.user.getId()+"_"+key+".png")
                        .params("menu.story",stroyEditText.getText().toString().trim())
                        .params("menu.food",foodEditText.getText().toString().trim())
                        .params("menu.category1",category1value)
                        .params("menu.category2",category2value)
                        .params("menu.category3",category3value)
                        .params("menu.step1",step1EditText.getText().toString().trim())
                        .params("menu.step2",step2EditText.getText().toString().trim())
                        .params("menu.step3",step3EditText.getText().toString().trim())
                        .params("menu.time",sdf.format(date))
                        .params("menu.pic1",Net.IP+"step/"+Global.user.getId()+"_step1_"+key+".png")
                        .params("menu.pic2",Net.IP+"step/"+Global.user.getId()+"_step2_"+key+".png")
                        .params("menu.pic3",Net.IP+"step/"+Global.user.getId()+"_step3_"+key+".png")
                        .params("key",key)
                        .params("cover",new File(imagePath[0]))
                        .params("pic1",new File(imagePath[1]))
                        .params("pic2",new File(imagePath[2]))
                        .params("pic3",new File(imagePath[3]))
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                JSONObject result = JSONObject.fromString(s);
                                if(result.getInt("code") == 200){
                                    Toast.makeText(context,"发布成功",Toast.LENGTH_SHORT);
                                    AddMenuActivity.this.finish();
                                }else{
                                    Toast.makeText(context,"发布失败",Toast.LENGTH_SHORT);
                                }
                            }
                        });
            }
        }
    };

    private View.OnClickListener coverOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picNum = 0;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE);
        }
    };

    private View.OnClickListener pic1OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picNum = 1;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE);
        }
    };

    private View.OnClickListener pic2OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picNum = 2;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE);
        }
    };

    private View.OnClickListener pic3OnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            picNum = 3;
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, IMAGE);
        }
    };

    private AdapterView.OnItemSelectedListener category1OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            category1value = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    };

    private AdapterView.OnItemSelectedListener category2OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            category2value = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    private AdapterView.OnItemSelectedListener category3OnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            category3value = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

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
            imagePath[picNum] = c.getString(columnIndex);
            Bitmap bm = BitmapFactory.decodeFile(imagePath[picNum]);
            addPics(bm);
            c.close();
        }
    }

    public void addPics(Bitmap bm) {
        if(picNum == 0){
            coverImageView.setBackground(new BitmapDrawable(bm));
        }else if(picNum == 1){
            pic1ImageView.setBackground(new BitmapDrawable(bm));
        }else if(picNum == 2){
            pic2ImageView.setBackground(new BitmapDrawable(bm));
        }else if(picNum == 3){
            pic3ImageView.setBackground(new BitmapDrawable(bm));
        }
    }

}
