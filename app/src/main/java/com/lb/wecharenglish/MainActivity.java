package com.lb.wecharenglish;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lb.utils.ToastUtil;
import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.global.Keys;
import com.lb.wecharenglish.server.EnglishServer;
import com.lb.wecharenglish.service.TimingTaskService;
import com.lb.wecharenglish.ui.activity.BaseActivity;
import com.lb.wecharenglish.ui.activity.EnglishDetailActivity;
import com.lb.wecharenglish.ui.activity.SettingActivity;
import com.lb.wecharenglish.ui.adapter.HomeAdapter;

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

    //===Desc:侧滑菜单控件===============================================================================================

    private DrawerLayout dl_main_drawermenu;
    /**
     * 侧滑菜单控件
     */
    private ScrollView sv_main_leftmenu;

    /**
     * 显示用户名的文本控件
     */
    private TextView tv_main_menu_username;

    /**
     * 侧滑菜单设置按钮
     */
    private TextView tv_main_menu_setting;

    //===Desc:复写父类的方法===============================================================================================
    @Override
    protected void initData() {
        Intent service = new Intent(mContext, TimingTaskService.class);
        startService(service);

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
        //侧滑菜单
        dl_main_drawermenu = ViewUtil.findViewById(this, R.id.dl_main_drawermenu);
        sv_main_leftmenu = ViewUtil.findViewById(this, R.id.sv_main_leftmenu);
        tv_main_menu_username = ViewUtil.findViewById(this, R.id.tv_main_menu_username);
        tv_main_menu_setting = ViewUtil.findViewById(this, R.id.tv_main_menu_setting);
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

        //初始化侧滑菜单宽度
        initMenuData();
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

        //设置条目点击事件
        lv_main_datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                EnglishBean englishBean = datas.get(i - lv_main_datas.getHeaderViewsCount());
                Intent intent = new Intent(mContext, EnglishDetailActivity.class);
                intent.putExtra(Keys.KEY_ENGLISH_BEAN, englishBean);
                startActivity(intent);
            }
        });

        //设置按钮点击事件
        tv_main_menu_setting.setOnClickListener(this);
    }

    //===Desc:本类使用的方法===============================================================================================

    private void loadData() {
        new Thread() {
            @Override
            public void run() {
                List<EnglishBean> remoteData = new EnglishServer().getDataFromRemote();
                //调用业务层进行添加
                final int oldSize = datas.size();

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

    /**
     * 初始化侧滑菜单部分的控件数据显示
     */
    private void initMenuData() {
        //获取屏幕宽度
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        //noinspection deprecation
        int width = wm.getDefaultDisplay().getWidth();
        sv_main_leftmenu.getLayoutParams().width = width * 70 / 100;
        sv_main_leftmenu.requestLayout();
        //设置用户名
        tv_main_menu_username.setText("沙飞");
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_main_menu_setting://设置按钮点击事件处理
                dl_main_drawermenu.closeDrawers();
                Intent settingIntent = new Intent(mContext, SettingActivity.class);
                startActivity(settingIntent);
                break;
        }
    }

    private Handler mHandle = new Handler();

    private boolean isLoadMore;
}
