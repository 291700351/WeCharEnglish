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
package com.lb.wecharenglish.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.lb.materialdesigndialog.impl.MaterialDialogNormal;
import com.lb.utils.LogUtil;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/6 23:36<br>
 * 类描述：申请权限的工具类 <br>
 */
public class PermissionUtil {
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10;

    private boolean canMakeSmores() {

        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);

    }


    /**
     * 申请权限
     *
     * @param activity              申请权限的activity
     * @param requestPermissionCode 申请权限的code。<br>
     *                              PermissionUtil.EXTERNAL_STORAGE_REQ_CODE:写入存储设备的权限code
     */
    public static void requestPermission(Activity activity, int requestPermissionCode, Runnable r) {
        //根据用户传递的请求权限码设置不同的权限
        String requestPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (requestPermissionCode == EXTERNAL_STORAGE_REQ_CODE)
            requestPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;


//        // Here, thisActivity is the current activity
//        if (ContextCompat.checkSelfPermission(activity,
//                Manifest.permission.READ_CONTACTS)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                    requestPermission)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(activity,
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        requestPermissionCode);
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(activity,
                requestPermission)
                != PackageManager.PERMISSION_GRANTED) {
            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    requestPermission)) {
                Toast.makeText(activity, "权限不足，不能继续操作，请授予权限", Toast.LENGTH_SHORT).show();
//                MaterialDialogNormal dia = new MaterialDialogNormal(activity);
            } else {
                //进行权限请求
                ActivityCompat.requestPermissions(activity,
                        new String[]{requestPermission},
                        EXTERNAL_STORAGE_REQ_CODE);
            }
            LogUtil.log(activity,"没有权限");
        } else {
            LogUtil.log(activity,"有权限");
            new Handler().post(r);
        }
    }
}
