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

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.lb.utils.CacheUtil;
import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.MainActivity;
import com.lb.wecharenglish.R;
import com.lb.wecharenglish.global.ImageLoaderOptions;
import com.lb.wecharenglish.global.Keys;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/9 16:23<br>
 * 类描述：程序的启动界面 <br>
 */
public class SplashActivity extends BaseActivity {
    private static final int ENTER_HOME = 0;

    //===Desc:成员变量===============================================================================
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENTER_HOME:
                    CacheUtil.putBoolean(mContext, Keys.Key_IS_FIRST_RUN_APP, false);
                    enterHome();
                    break;
            }
        }
    };

    /**
     * 加载启动图片
     */
    private ImageView iv_splash_pic;

    //===Desc:复写父类中的方法===============================================================================

    @Override
    protected void initData() {
        showActionBar = false;
    }

    @Override
    protected View createView() {
        return View.inflate(mContext, R.layout.activity_splash, null);
    }

    @Override
    protected void findView() {
        iv_splash_pic = ViewUtil.findViewById(rootView, R.id.iv_splash_pic);
    }

    @Override
    protected void setViewData() {
        //设置启动图片，使用ImageLoader 防止内存溢出
        ImageLoader.getInstance().displayImage("drawable://" + R.drawable.splash_pic,
                iv_splash_pic, ImageLoaderOptions.options);
        //延迟3秒进入主页面
        mHandler.sendEmptyMessageDelayed(ENTER_HOME, 3 * 1000);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    //===Desc:本类中使用的方法===============================================================================


    /**
     * 进入主页面的方法
     */
    private void enterHome() {
        if (CacheUtil.getBoolean(mContext, Keys.Key_IS_FIRST_RUN_APP, true)) {
            startActivity(new Intent(mContext, GuideActivity.class));
            finish();
        } else {
            startActivity(new Intent(mContext, MainActivity.class));
            finish();
        }
    }
}
