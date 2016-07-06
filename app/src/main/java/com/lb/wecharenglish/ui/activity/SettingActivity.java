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

import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lb.materialdesigndialog.base.DialogBase;
import com.lb.materialdesigndialog.base.DialogWithTitle;
import com.lb.materialdesigndialog.impl.MaterialDialogInput;
import com.lb.utils.CacheUtil;
import com.lb.utils.LogUtil;
import com.lb.utils.SdCardUtil;
import com.lb.utils.ToastUtil;
import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.R;
import com.lb.wecharenglish.global.Keys;
import com.lb.wecharenglish.utils.PermissionUtil;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.download.DownloadListener;
import com.yolanda.nohttp.download.DownloadRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/6 20:55<br>
 * 类描述：设置界面 <br>
 */
public class SettingActivity extends BaseActivity {

    /**
     * 使用nohttp下载
     */
    private DownloadRequest downloadRequest;

    //===Desc:成员变量===============================================================================

    /**
     * 个性签名按钮
     */
    private LinearLayout ll_setting_setusername;

    /**
     * 同步远程数据库按钮
     */
    private LinearLayout ll_setting_synchronous;

    private ProgressBar pb_setting_synchronousing;

    /**
     * 备份数据库
     */
    private LinearLayout ll_setting_backup;

    //===Desc:复写父类中的方法===============================================================================
    @Override
    protected View createView() {
        return View.inflate(mContext, R.layout.activity_setting, null);
    }

    @Override
    protected void findView() {
        ll_setting_setusername = ViewUtil.findViewById(this, R.id.ll_setting_setusername);
        //同步数据库
        ll_setting_synchronous = ViewUtil.findViewById(this, R.id.ll_setting_synchronous);
        pb_setting_synchronousing = ViewUtil.findViewById(this, R.id.pb_setting_synchronousing);

        //备份本地数据库
        ll_setting_backup = ViewUtil.findViewById(this, R.id.ll_setting_backup);
    }

    @Override
    protected void setViewData() {
        pb_setting_synchronousing.setVisibility(View.GONE);
    }

    @Override
    protected void setListener() {
        ll_setting_setusername.setOnClickListener(this);
        ll_setting_synchronous.setOnClickListener(this);
        ll_setting_backup.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_setting_setusername://个性签名按钮
                final MaterialDialogInput dialogInput = new MaterialDialogInput(mContext);
                dialogInput.setTitle("修改个性签名");
                dialogInput.setDesc("请输入想要修改的个性签名");
                String cacheName = CacheUtil.getString(mContext, Keys.USER_NAME, "");
                if (TextUtils.isEmpty(cacheName))
                    cacheName = "沙飞";
                dialogInput.setEditTextHintText(cacheName);
                dialogInput.setPositiveButton("保存", new DialogWithTitle.OnClickListener() {
                    @Override
                    public void click(DialogBase dialog, View view) {
                        String userInput = dialogInput.getUserInput().trim();
                        if (TextUtils.isEmpty(userInput)) {
                            //显示动画提醒用户不能为空
                            TranslateAnimation animation = new TranslateAnimation(0, 15, 0, 0);
                            animation.setInterpolator(new OvershootInterpolator());
                            animation.setDuration(50);
                            animation.setRepeatCount(5);
                            animation.setRepeatMode(Animation.REVERSE);
                            dialogInput.getEditText().startAnimation(animation);
                            dialogInput.setDesc("请输入一个签名");
                            return;
                        }
                        //缓存用户设置的用户名
                        CacheUtil.putString(mContext, Keys.USER_NAME, userInput);
                        dialog.dismiss();
                        ToastUtil.showShortToast(mContext, "修改签名成功");
                    }
                });

                dialogInput.setNegativeButton("关闭", new DialogWithTitle.OnClickListener() {
                    @Override
                    public void click(DialogBase dialog, View view) {
                        dialog.dismiss();
                    }
                });
                dialogInput.show();
                break;

            case R.id.ll_setting_synchronous://同步远程数据库点击实现
                //申请权限
                PermissionUtil.requestPermission(SettingActivity.this,
                        PermissionUtil.EXTERNAL_STORAGE_REQ_CODE,
                        new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShortToast(mContext, "即将开始下载");

                                String url = "http://192.168.1.104:8080/tomcat.gif";
                                String path = SdCardUtil.getSDCardPath() + File.separator + getPackageName() + File.separator
                                        + "remote" + File.separator + "db";
                                String fileName = "english";

                                synchronous(url, path, fileName);
                            }
                        });
                break;

            case R.id.ll_setting_backup://备份本地数据库的操作

                String floder = getCacheDir().getParent();
                File file = new File(floder, "databases");
                file = new File(file, "english.db");
                if (file.exists() && file.isFile()) {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        FileOutputStream fos = new FileOutputStream(
                                new File(SdCardUtil.getSDCardPath() + File.separator + "english.db"));

                        byte[] buf = new byte[1024];
                        int len;

                        while ((len = fis.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            fos.flush();
                        }

                        fos.close();
                        fis.close();
                        LogUtil.log(this, "复制完毕");


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;
        }
    }

    @Override
    protected void requestPermissionsSuccess() {
        ToastUtil.showShortToast(mContext, "即将开始下载");

        String url = "http://192.168.1.104:8080/tomcat.gif";
        String path = SdCardUtil.getSDCardPath() + File.separator + getPackageName() + File.separator
                + "remote" + File.separator + "db";
        String fileName = "english";

        synchronous(url, path, fileName);
    }

    @Override
    protected void requestPermissionsFail() {
        ToastUtil.showShortToast(mContext, "没有获取写入存储设备的权限，将不能同步远程数据库");
    }

    //===Desc:本类中使用的方法===============================================================================

    /**
     * 同步远程数据库的方法
     */
    private void synchronous(String url, String filePath, String fileName) {
        LogUtil.log(this, filePath);
        try {
            File file = new File(filePath);
            if (!file.exists() && file.mkdirs()) {
                file = new File(file, fileName);
                if (!file.exists() && file.createNewFile()) {
                    if (downloadRequest != null && downloadRequest.isStarted() && !downloadRequest.isFinished()) {
                        //说明正在下载，不用管
                        return;
                    }
                    downloadRequest = NoHttp.createDownloadRequest(url, filePath, fileName, true, false);
                    // what 区分下载
                    // downloadRequest 下载请求对象
                    // downloadListener 下载监听
                    NoHttp.newDownloadQueue(2).add(0, downloadRequest, new DownloadListener() {
                        @Override
                        public void onDownloadError(int what, Exception exception) {
                            LogUtil.log(this, exception);
                            pb_setting_synchronousing.setVisibility(View.GONE);
                            ToastUtil.showShortToast(mContext, "同步远程数据库失败，请重试");
                        }

                        @Override
                        public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {
                            LogUtil.log(this, "开始下载");
                            pb_setting_synchronousing.setVisibility(View.VISIBLE);//显示进度条
                        }

                        @Override
                        public void onProgress(int what, int progress, long fileCount) {
                            // 更新下载进度
                        }

                        @Override
                        public void onFinish(int what, String filePath) {
                            LogUtil.log(this, "下载完毕");
                            pb_setting_synchronousing.setVisibility(View.GONE);
                            //
                        }

                        @Override
                        public void onCancel(int what) {
                            LogUtil.log(this, "下载取消");
                            pb_setting_synchronousing.setVisibility(View.GONE);
                        }
                    });
                }
            }
        } catch (IOException e) {
            LogUtil.log(this, e);
            ToastUtil.showShortToast(mContext, "创建文件失败，请重试");
        }

    }


}
