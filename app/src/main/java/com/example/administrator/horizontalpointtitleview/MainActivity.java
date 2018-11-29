package com.example.administrator.horizontalpointtitleview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;

    private Button btn;
//    private HorizontalPointTitleView hptv;
    private ViewPager viewPager;

    private MyAdapter adapter;
    //模拟数据
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        initView();
        initData();
        initAdapter();
        initListener();

//        hptv.setCurrentItem(9);
    }

    private void initView() {
        btn = findViewById(R.id.btn);
//        hptv = findViewById(R.id.hptv);
        viewPager = findViewById(R.id.viewPager);
    }

    private void initData() {
        for (int i = 1; i <= 20; i++) {
            list.add("第" + i + "页");
        }
//        hptv.setData(list);
    }

    private void initAdapter() {
        adapter = new MyAdapter(context, list);
        viewPager.setAdapter(adapter);
    }

    private void initListener() {
//        viewPager.addOnPageChangeListener(vpListener);
//        hptv.bindViewPager(viewPager);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                viewPager.scrollTo(500, 0);
            }
        });
//        hptv.setItemChangerListener(new HorizontalPointTitleView.ItemChangerListener() {
//
//            @Override
//            public void onItemScroll(int position) {
//                Log.i("测试", "onItemScroll = " + position);
//                viewPager.setCurrentItem(position);
//            }
//
//            @Override
//            public void onItemChanger(int position) {
//                Log.i("测试", "onItemChanger = " + position);
//                viewPager.setCurrentItem(position);
//            }
//        });
    }

//    private ViewPager.OnPageChangeListener vpListener = new ViewPager.OnPageChangeListener() {
//        @Override
//        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.i("测试", "position = " + position + "，positionOffset = " + positionOffset + "，positionOffsetPixels = " + positionOffsetPixels);
//        }
//
//        @Override
//        public void onPageSelected(int position) {
//            Log.i("测试", "position = " + position);
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int state) {
//            Log.i("测试", "onPageScrollStateChanged = " + state);
//        }
//    };
}
