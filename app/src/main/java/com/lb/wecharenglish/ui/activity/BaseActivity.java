//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼                  BUG辟易
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
package com.lb.wecharenglish.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.lb.utils.LogUtil;
import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.R;
import com.sackcentury.shinebuttonlib.ShineButton;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/3 23:52<br>
 * 类描述：应用程序中activity的基类 <br>
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    //===Desc:成员变量===============================================================================

    /**
     * 是否是返回当前界面
     * true：是
     * false：不是
     */
    protected boolean isResume;

    /**
     * 是否重新加载数据
     */
    protected boolean reLoadData;

    /**
     * actionBar控件
     */
    private ActionBar actionBar;

    /**
     * 显示在界面上的view
     */
    protected View rootView;

    /**
     * 提供给子类使用的上下文对象
     */
    protected Context mContext;

    //===Desc:actionBar相关的控件===============================================================================================

    /**
     * 是否显示actionbar，默认是显示（true）,可以再initData()方法中设置不显示
     */
    protected boolean showActionBar;
    /**
     * 显示在actionbar上面的view
     */
    private View actionBarView;

    /**
     * actionbar上面的title控件
     */
    private TextView tv_base_actionbar_title;

    /**
     * actionbar里面添加收藏按钮
     */
    private ShineButton sb_base_actionbar_like;


    //===Desc:复写父类中的方法===============================================================================

    /**
     * 禁止子类重写
     */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isResume = false;
        reLoadData = true;

        mContext = this;

        showActionBar = true;

        initData();

        //设置actionbar
        initActionBar();

        //设置布局显示
        rootView = createView();
        if (null == rootView)
            throw new IllegalStateException("You must return a not null view to show at the createView() method......");
        LogUtil.log(this, getClass().getSimpleName() + " onCreate...");

        setContentView(rootView);

        findView();

    }

    @Override
    protected void onResume() {
        setViewData();

        setListener();

        super.onResume();

        isResume = true;
    }

    //处理actionbar只有按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home://返回按钮
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //===Desc:本类中使用的方法===============================================================================

    /**
     * 初始化actionbar
     */
    private void initActionBar() {
        //得到actionBar，注意我的是V7包，使用getSupportActionBar()
        actionBar = getSupportActionBar();
        if (null == actionBar) return;
        //判断是否设置了不显示actionbar
        if (!showActionBar) {
            actionBar.hide();
            return;
        }

        actionBar.setDisplayShowTitleEnabled(false);
        //在使用v7包的时候显示icon和标题需指定一下属性。
        actionBar.setDisplayShowHomeEnabled(true);
//         actionBar.setLogo(R.mipmap.ic_launcher);
        actionBar.setDisplayUseLogoEnabled(true);
        // 返回箭头（默认不显示）
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 左侧图标点击事件使能
        actionBar.setHomeButtonEnabled(true);
        //显示自定义的actionBar
        actionBar.setDisplayShowCustomEnabled(true);
        actionBarView = getLayoutInflater().inflate(R.layout.actionbar_base, null, false);
        //初始化控件
        findActionBarView();
        //设置显示
        actionBar.setCustomView(actionBarView);
    }

    /**
     * 初始化actionbar中的控件
     */
    private void findActionBarView() {
        //标题
        tv_base_actionbar_title = ViewUtil.findViewById(actionBarView, R.id.tv_base_actionbar_title);
        //收藏按钮
        sb_base_actionbar_like = ViewUtil.findViewById(actionBarView, R.id.sb_base_actionbar_like);
    }

    //===Desc:子类必须实现===============================================================================================

    /**
     * 子类实现该方法显示一个view
     *
     * @return 显示在界面上的view对象
     */
    protected abstract View createView();

    //===Desc:子类可选实现===============================================================================================

    /**
     * 初始化数据的方法，可用于获取三个界面传递的数据
     */
    protected void initData() {
    }


    /**
     * 初始化界面中的控件的方法
     */
    protected void findView() {
    }

    /**
     * 给界面中的控件设置数据显示的方法
     */
    protected void setViewData() {
    }

    /**
     * 给界面中的控件设置监听的方法
     */
    protected void setListener() {
    }

    //===Desc:请求权限的回调===============================================================================================

    /**
     * 但界面需要申请权限时，申请权限成功回调这个方法
     */
    protected void requestPermissionsSuccess() {

    }

    /**
     * 但界面需要申请权限时，申请权限失败回调这个方法
     */
    protected void requestPermissionsFail() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 如果请求被拒绝，那么通常grantResults数组为空
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //申请成功，进行相应操作
            requestPermissionsSuccess();

        } else {
            //申请失败，可以继续向用户解释。
            requestPermissionsFail();
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
//        int permissionCode = PermissionUtil.EXTERNAL_STORAGE_REQ_CODE;
//        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
//            permissionCode = PermissionUtil.EXTERNAL_STORAGE_REQ_CODE;
        return super.shouldShowRequestPermissionRationale(permission);
    }

    //===Desc:提供给子类使用的方法===============================================================================================

    /**
     * 设置actionbar标题
     *
     * @param title 需要设置的标题
     */
    protected void setActionBarTitle(@NonNull String title) {
        if (TextUtils.isEmpty(title))
            return;
        tv_base_actionbar_title.setText(title);
    }

    /**
     * 是否显示收藏按钮
     *
     * @param showLikeBtn 是否显示<br>&#9;true：显示<br>&#9false：不显示
     */
    protected void showLikeBtn(boolean showLikeBtn) {
        sb_base_actionbar_like.setVisibility(showLikeBtn ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置收藏按钮是否选中
     *
     * @param checked 是否选中<br>&#9;true：选中<br>&#9;false：不选中
     */
    protected void setLikeChecked(boolean checked) {
        sb_base_actionbar_like.setChecked(checked);
    }

    /**
     * 设置收藏按钮的点击事件
     *
     * @param listener 点击事件
     */
    protected void setListClicklistener(View.OnClickListener listener) {
        if (sb_base_actionbar_like.getVisibility() == View.VISIBLE)
            sb_base_actionbar_like.setOnClickListener(listener);
    }

    /**
     * 初始化actionbar上面控件数据显示
     *
     * @param showBackBtn    是否显示返回按钮 <br>&#9;true：显示<br>&#9false：不显示
     * @param title          需要设置的标题
     * @param showLikeBtn    是否显示收藏按钮，不显示收藏按钮键不会设置下面两个属性<br>&#9;true：显示<br>&#9false：不显示
     * @param likeBtnChecked 设置收藏按钮是否选中<br>&#9;true：选中<br>&#9false：不选中
     * @param listener       设置收藏按钮的点击事件
     */
    protected void setActionBarDatas(boolean showBackBtn, String title,
                                     boolean showLikeBtn,
                                     boolean likeBtnChecked,
                                     View.OnClickListener listener) {
        tv_base_actionbar_title.setFocusable(true);
        actionBar.setDisplayHomeAsUpEnabled(showBackBtn);
        setActionBarTitle(title);
        showLikeBtn(showLikeBtn);
        //如果收藏按钮不显示就不需要设置属性了
        if (showLikeBtn) {
            setLikeChecked(likeBtnChecked);
            setListClicklistener(listener);
        }
    }
}
