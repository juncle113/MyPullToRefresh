package com.study.sl.mypulltorefresh;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private PullToRefreshListView pullToRefresh;
    private List<String> mDatas;
    private int rowNo = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullToRefresh = findViewById(R.id.pull_to_refresh);
        mDatas = new ArrayList<>();

        LoadDatas();
        setPullToRefresh();
    }

    /**
     * 模拟装载数据
     */
    private void LoadDatas() {
        for (int i = 0; i < 10; i++) {
            mDatas.add(String.format(Locale.CHINA, "下拉列表中的第%d条数据", rowNo));
            rowNo++;
        }
    }

    /**
     * 设置下拉刷新
     */
    private void setPullToRefresh() {
        pullToRefresh.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDatas));

//        // 1 实现下拉刷新
//        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//            @Override
//            public void onRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
//                new MyAsyncTask().execute();
//            }
//        });

        // 2 同时实现上拉跟下拉刷新
        // 2.1 设置监听事件
        pullToRefresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            // 模拟上拉装载数据
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                new MyAsyncTask().execute();
            }

            // 模拟下拉装载数据
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> pullToRefreshBase) {
                new MyAsyncTask().execute();
            }
        });

        // 2.2 设置下拉刷新模式BOYH|END|START
        pullToRefresh.setMode(PullToRefreshBase.Mode.BOTH);  // 同时上拉下拉

        // 设置自定义下拉刷新动画文字  getLoadingLayoutProxy(true, false)，参数分别代表要设置上或下的文字修改
        ILoadingLayout headerLayout = pullToRefresh.getLoadingLayoutProxy(true, false);
        headerLayout.setPullLabel("向下拖动完成刷新...");
        headerLayout.setRefreshingLabel("正在加载新数据...");
        headerLayout.setReleaseLabel("释放完成刷新...");

        // 设置底部刷新文字
        ILoadingLayout footLayout = pullToRefresh.getLoadingLayoutProxy(false, true);
        footLayout.setPullLabel("向上拽动完成刷新...");
        footLayout.setRefreshingLabel("正在疯刷新数据...");
        footLayout.setReleaseLabel("松开完成刷新...");
//        footLayout.setLoadingDrawable(getResources().getDrawable(R.drawable.default_ptr_flip));
    }

    /**
     * 异步任务类，下拉或者上拉时模拟请求数据
     * pullToRefresh.onRefreshComplete()方法一定要在异步中请求，否则可能无效果
     */
    class MyAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LoadDatas();
            return "success";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if ("success".equals(s)) {
                // 异步调用pullToRefreshListView.onRefreshComplete是会消失的，同步调用头部动画不会消失。
                pullToRefresh.onRefreshComplete();
            }
        }
    }
}
