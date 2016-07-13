package com.lb.wecharenglish;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.lb.utils.CacheUtil;
import com.lb.utils.LogUtil;
import com.lb.utils.Screenutil;
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
import com.lb.wecharenglish.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemLongClickListener {
    //===Desc:成员变量===============================================================================================

    /**
     * 显示的是否是收藏的数据
     */
    private boolean isShowLike;

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
    private LinearLayout ll_main_menu_setting;
    /**
     * 侧滑菜单显示我的收藏按钮
     */
    private LinearLayout ll_main_menu_showLike;

    /**
     * 侧滑菜单导出功能按钮
     */
    private LinearLayout ll_main_menu_export;


    //===Desc:复写父类的方法===============================================================================================
    @Override
    protected void initData() {
        Intent service = new Intent(mContext, TimingTaskService.class);
        startService(service);

        datas = new ArrayList<>();

    }

    @Override
    protected View createView() {
        return View.inflate(mContext, R.layout.activity_main, null);
    }

    @Override
    protected void findView() {
        sr_main_refresh = ViewUtil.findViewById(rootView, R.id.sr_main_refresh);
        lv_main_datas = ViewUtil.findViewById(this, R.id.lv_main_datas);
        //侧滑菜单
        dl_main_drawermenu = ViewUtil.findViewById(this, R.id.dl_main_drawermenu);
        sv_main_leftmenu = ViewUtil.findViewById(this, R.id.sv_main_leftmenu);
        tv_main_menu_username = ViewUtil.findViewById(this, R.id.tv_main_menu_username);
        ll_main_menu_setting = ViewUtil.findViewById(this, R.id.ll_main_menu_setting);
        ll_main_menu_showLike = ViewUtil.findViewById(this, R.id.ll_main_menu_showLike);
        ll_main_menu_export = ViewUtil.findViewById(this, R.id.ll_main_menu_export);

    }

    @Override
    protected void setViewData() {

        setActionBarDatas(false, getString(R.string.app_name), false, false, null);
        //如果是返回当前界面，不是重新创建就不请求服务器加载数据了
        if (!isResume)
            sr_main_refresh.post(new Runnable() {
                @Override
                public void run() {
                    sr_main_refresh.setRefreshing(true);
                    onRefresh();
                }
            });

        if (null == adapter) {
            adapter = new HomeAdapter(mContext, datas);
            lv_main_datas.setAdapter(adapter);
        }
        if (reLoadData) {
            int pageNo = datas.size() / pageSize + 1;
            List<EnglishBean> dbList = new EnglishServer().getDataByPage(mContext, 0, pageNo, pageSize);

            //数据去重
            for (EnglishBean bean : dbList) {
                if (!datas.contains(bean)) {
                    datas.add(bean);
                }
            }
            Collections.sort(datas);
        }
        adapter.notifyDataSetChanged();

        //初始化侧滑菜单宽度
        initMenuData();
        reLoadData = false;
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
                            if (showLoadMore) {
                                isLoadMore = true;
                                sr_main_refresh.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        sr_main_refresh.setRefreshing(true);
                                        onRefresh();
                                    }
                                });
                            } else {
                                showLoadMore = true;
                            }
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
                if (!adapter.isInEditMode()) {
                    EnglishBean englishBean = datas.get(i - lv_main_datas.getHeaderViewsCount());
                    Intent intent = new Intent(mContext, EnglishDetailActivity.class);
                    intent.putExtra(Keys.KEY_ENGLISH_BEAN, englishBean);
                    startActivity(intent);
                } else {
                    adapter.toggleSelectedPoistion(i);
                }
            }
        });

        //设置ListView长按事件监听
        lv_main_datas.setOnItemLongClickListener(this);

        //设置按钮点击事件
        ll_main_menu_setting.setOnClickListener(this);
        ll_main_menu_showLike.setOnClickListener(this);//显示我的收藏
        ll_main_menu_export.setOnClickListener(this);//导出md文档
    }

    @Override
    public void onBackPressed() {
        //判断菜单是否显示
        if (dl_main_drawermenu.isDrawerOpen(Gravity.LEFT)) {
            dl_main_drawermenu.closeDrawers();
        } else {
            if (adapter.isInEditMode()) {
                adapter.setEditMode(false);
            } else {
                //跳转到桌面
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                ToastUtil.showShortToast(mContext, getResources().getString(R.string.app_name) + "后台运行");
            }
        }
    }
    //===Desc:本类使用的方法===============================================================================================

    private void loadData() {
        if (isShowLike) {
            sr_main_refresh.setRefreshing(false);
            return;
        }
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
        List<EnglishBean> dbList = new EnglishServer().getDataByPage(mContext, isShowLike ? 1 : 0, pageNo, pageSize);

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
        showLoadMore = false;
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
        sv_main_leftmenu.getLayoutParams().width = width * 40 / 100;
        sv_main_leftmenu.requestLayout();
        //设置用户名 从sp缓存中获取用户名  如果没有就像是沙飞
        tv_main_menu_username.setText(CacheUtil.getString(mContext, Keys.USER_NAME, "沙飞"));
    }

    private boolean showLoadMore;

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
            case R.id.ll_main_menu_setting://设置按钮点击事件处理
                Intent settingIntent = new Intent(mContext, SettingActivity.class);
                startActivity(settingIntent);
                dl_main_drawermenu.closeDrawers();
                break;
            case R.id.ll_main_menu_showLike://显示我的收藏或者全部
                TextView tv_main_menu_like = ViewUtil.findViewById(rootView, R.id.tv_main_menu_like);
                //更新数据源，将页面listView
                List<EnglishBean> list;
                //调用业务层
                if (!isShowLike) {
                    //需要显示都长的
                    tv_main_menu_like.setText(getResources().getString(R.string.txt_show_all));
                    list = new EnglishServer().getDataByPage(mContext, 1, 1, pageSize);
                } else {
                    tv_main_menu_like.setText(getResources().getString(R.string.txt_show_like));
                    list = new EnglishServer().getDataByPage(mContext, 0, 1, pageSize);
                }
                //清空listView数据源，设置为当前常寻道的数据
                datas.clear();
                datas.addAll(list);
                adapter.notifyDataSetChanged();
                isShowLike = !isShowLike;
                //关闭菜单
                dl_main_drawermenu.closeDrawers();
                break;

            case R.id.ll_main_menu_export:
                PermissionUtil.requestPermission(this, PermissionUtil.EXTERNAL_STORAGE_REQ_CODE, new Runnable() {
                    @Override
                    public void run() {
                        export();
                    }
                });
                break;
        }
    }

    @Override
    protected void requestPermissionsFail() {
        ToastUtil.showShortToast(mContext, "您还没有读写存储设备的权限，操作不能继续");
    }

    @Override
    protected void requestPermissionsSuccess() {
        LogUtil.log(this, "开始导出");
        export();
    }

    private Handler mHandle = new Handler();

    private boolean isLoadMore;

    //===Desc:ListView条目长按事件的处理===============================================================================================

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
//        adapter.setEditMode(true);
        return true;
    }

    private boolean isExporting;

    private void export() {
        if (isExporting) {
            ToastUtil.showShortToast(mContext, "正在导出");
            return;
        }
        isExporting = true;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return new EnglishServer().exportFile(mContext, datas);
            }

            @Override
            protected void onPostExecute(Boolean exportSuccess) {
                if (exportSuccess)
                    ToastUtil.showShortToast(mContext, "导出成功");
                else
                    ToastUtil.showShortToast(mContext, "导出失败");
                isExporting = false;
            }
        }.execute();
    }
}
