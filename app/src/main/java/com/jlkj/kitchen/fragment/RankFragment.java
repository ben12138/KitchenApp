package com.jlkj.kitchen.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jlkj.kitchen.R;
import com.jlkj.kitchen.activity.RankActivity;

public class RankFragment extends Fragment {

    private LinearLayout menuLinearLayout;
    private LinearLayout productionLinearLayout;

    private Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank,null);
        init(view);
        return view;
    }

    private void init(View view){
        menuLinearLayout = view.findViewById(R.id.menu);
        productionLinearLayout = view.findViewById(R.id.production);
        menuLinearLayout.setOnClickListener(menuOnClickListener);
        productionLinearLayout.setOnClickListener(productionClickListener);
    }

    private View.OnClickListener menuOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, RankActivity.class);
            intent.putExtra("name","menu");
            intent.putExtra("type","all");
            context.startActivity(intent);
        }
    };

    private View.OnClickListener productionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, RankActivity.class);
            intent.putExtra("name","production");
            intent.putExtra("type","all");
            context.startActivity(intent);
        }
    };

}
