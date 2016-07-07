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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lb.utils.LogUtil;
import com.lb.wecharenglish.utils.PermissionUtil;

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
     * 显示在界面上的view
     */
    protected View rootView;

    /**
     * 提供给子类使用的上下文对象
     */
    protected Context mContext;


    //===Desc:复写父类中的方法===============================================================================

    /**
     * 禁止子类重写
     */
    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        initData();

        View rootView = createView();
        if (null == rootView)
            throw new IllegalStateException("You must return a not null view to show at the createView() method......");
        LogUtil.log(this, getClass().getSimpleName() + " onCreate...");

        setContentView(rootView);

        findView();

        setViewData();

        setListener();

    }


    //===Desc:本类中使用的方法===============================================================================


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
//        if (requestPermissionCode == EXTERNAL_STORAGE_REQ_CODE)
        int permissionCode = PermissionUtil.EXTERNAL_STORAGE_REQ_CODE;
        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionCode = PermissionUtil.EXTERNAL_STORAGE_REQ_CODE;
        return super.shouldShowRequestPermissionRationale(permission);
//        if (!b)
//            PermissionUtil.requestPermission(this, permissionCode);
//        else {
//            return true;
//        }
//        return false;
    }
}
