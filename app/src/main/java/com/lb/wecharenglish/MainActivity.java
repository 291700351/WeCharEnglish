package com.lb.wecharenglish;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import com.lb.utils.LogUtil;
import com.lb.utils.ToastUtil;
import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.server.EnglishServer;
import com.lb.wecharenglish.ui.activity.BaseActivity;
import com.lb.wecharenglish.ui.adapter.HomeAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {
    //===Desc:成员变量===============================================================================================

    private int pageSize = 10;
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

        int pageNo = datas.size() / pageSize + 1;
        List<EnglishBean> dbList = new EnglishServer().getDataByPage(mContext, pageNo, pageSize);

        //数据去重
        for (EnglishBean bean : dbList) {
            if (!datas.contains(bean)) {
                datas.add(bean);
            }
        }
        Collections.sort(datas);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void setListener() {
        sr_main_refresh.setOnRefreshListener(this);

        lv_main_datas.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    // 当不滚动时
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        // 判断滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            isLoadMore = true;
                            sr_main_refresh.post(new Runnable() {
                                @Override
                                public void run() {
                                    sr_main_refresh.setRefreshing(true);
                                    onRefresh();
                                }
                            });
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
    }

    //===Desc:本类使用的方法===============================================================================================

    private void loadData() {
        new Thread() {
            @Override
            public void run() {
                List<EnglishBean> remoteData = new EnglishServer().getDataFromRemote();
                //调用业务层进行添加
                final int oldSize = datas.size();
                LogUtil.e(this,"---------------------------------");

                if (null != remoteData && remoteData.size() != 0) {
                    for (EnglishBean bean : remoteData) {
                        new EnglishServer().add(mContext, bean);
                        if (!datas.contains(bean)) {
                            datas.add(bean);
                        }
                    }
                }
                Collections.sort(datas);
                mHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (datas.size() > oldSize)
                            ToastUtil.showShortToast(mContext, "更新" + (datas.size() - oldSize) + "条数据");
                        else
                            ToastUtil.showShortToast(mContext, "暂无更新");
                        adapter.notifyDataSetChanged();
                        sr_main_refresh.setRefreshing(false);
                    }
                }, 500);

            }
        }.start();
    }

    private void loadMore() {
        int pageNo = datas.size() / pageSize + 1;

        final int oldSize = datas.size();
        List<EnglishBean> dbList = new EnglishServer().getDataByPage(mContext, pageNo, pageSize);
        LogUtil.e(this, dbList.get(0).getDate());

        //数据去重
        for (EnglishBean bean : dbList) {
            if (!datas.contains(bean)) {
                datas.add(bean);
            }
        }
        Collections.sort(datas);
        final int size = datas.size();

        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                sr_main_refresh.setRefreshing(false);
                if (size > oldSize) {
                    View itemView = lv_main_datas.getAdapter().getView(oldSize, null, lv_main_datas);
                    itemView.measure(0, 0);
                    lv_main_datas.smoothScrollBy(itemView.getMeasuredHeight(), 500);
                } else {
                    ToastUtil.showShortToast(mContext, "没有更多数据了");
                }
            }
        }, 1000);

        isLoadMore = false;
    }

    //===Desc:刷新的监听===============================================================================================
    @Override
    public void onRefresh() {
        if (isLoadMore) {
            loadMore();
        } else {
            loadData();
        }
    }

    private Handler mHandle = new Handler();

    private boolean isLoadMore;
}
