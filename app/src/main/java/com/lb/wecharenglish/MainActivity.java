package com.lb.wecharenglish;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.ListView;

import com.lb.utils.LogUtil;
import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.server.EnglishServer;
import com.lb.wecharenglish.ui.activity.BaseActivity;
import com.lb.wecharenglish.ui.adapter.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    //===Desc:成员变量===============================================================================================

    /**
     * 显示在界面上的数据展示
     */
    private ListView lv_main_datas;

    private SwipeRefreshLayout sr_main_refresh;

    private List<EnglishBean> datas;
    private HomeAdapter adapter;

    //===Desc:复写父类的方法===============================================================================================
    @Override
    protected void initData() {
        datas = new ArrayList<>();
        adapter = new HomeAdapter(mContext, datas);
    }

    @Override
    protected View createView() {
        return View.inflate(mContext, R.layout.activity_main, null);
    }

    @Override
    protected void findView() {
        sr_main_refresh = ViewUtil.findViewById(this, R.id.sr_main_refresh);
        lv_main_datas = ViewUtil.findViewById(this, R.id.lv_main_datas);
    }

    @Override
    protected void setViewData() {
        lv_main_datas.setAdapter(adapter);
        sr_main_refresh.post(new Runnable() {
            @Override
            public void run() {
                sr_main_refresh.setRefreshing(true);
                onRefresh();
            }
        });

        int pageSize = 10;
        int pageNo = datas.size() / pageSize + 1;
        List<EnglishBean> dbList = new EnglishServer().getDataByPage(mContext, pageNo, pageSize);

        //数据去重
        for (EnglishBean bean : dbList) {
            if (!datas.contains(bean))
                datas.add(bean);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void setListener() {
        sr_main_refresh.setOnRefreshListener(this);

    }

    //===Desc:本类使用的方法===============================================================================================

    private void loadData() {
        new Thread() {
            @Override
            public void run() {
                List<EnglishBean> remoteData = new EnglishServer().getDataFromRemote();
                //调用业务层进行添加
                if (null != remoteData && remoteData.size() != 0) {
                    for (EnglishBean bean : remoteData) {
                        new EnglishServer().add(mContext, bean);
                        if (!datas.contains(bean)) {
                            datas.add(bean);
                        }
                    }
                }
                mHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        sr_main_refresh.setRefreshing(false);
                    }
                }, 500);

            }
        }.start();
    }

    //===Desc:刷新的监听===============================================================================================
    @Override
    public void onRefresh() {
//        loadData();
    }

    private Handler mHandle = new Handler();
}
