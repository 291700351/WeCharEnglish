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
package com.lb.wecharenglish.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.Html;

import com.lb.utils.LogUtil;
import com.lb.wecharenglish.R;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.server.EnglishServer;

import java.util.List;

/**
 * 项目名称：WeCharEnglish<br>
 * 作者：Ice<br>
 * 邮箱： lb291700351@live.cn<br>
 * 时间：2016/7/6 17:06<br>
 * 类描述：定时请求网络获取数据的广播接收者<br>
 */
public class TimingTaskReceiver extends BroadcastReceiver {

    //===Desc:复写父类中的方法======================================================================================
    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtil.e(this, "接收到广播");

        String action = intent.getAction();
        if ((context.getPackageName() + ".getRemoteData").equals(action)) {
            new Thread() {
                @Override
                public void run() {
                    //获取远端数据
                    List<EnglishBean> remoteDatas = new EnglishServer().getDataFromRemote();
                    //遍历并添加到数据库
                    for (EnglishBean bean : remoteDatas) {
                        //查询数据库是否包含该条数据
                        bean.setId();
                        EnglishBean dbBean = new EnglishServer().findById(context, bean.getId());
                        if (null == dbBean) {
                            //需要发出状态栏通知
                            LogUtil.e(this, "显示通知");

//                            showNewNotification(context);
                        }

                        //添加进数据库操作
                        new EnglishServer().add(context, bean);
                    }
                }
            }.start();
        } else {
            LogUtil.e(this, "action不一致");
            LogUtil.e(this, "action=" + action);

        }

    }


    /**
     * 状态栏显示一条新同志
     *
     * @param context 上下文对象
     */
    private void showNewNotification(Context context, int id, EnglishBean bean) {
        //使用通知推送消息
        Notification.Builder builder = new Notification.Builder(context);
        //设置消息属性
        //必须设置的属性：小图标 标题 内容
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(bean.getTitle());
        //noinspection deprecation
        builder.setContentText(Html.fromHtml(bean.getDesc()));
        //创建一个通知对象
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = builder.build();
        }
        //使用通知管理器发送一条通知
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);


//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                new Intent(context, MainActivity.class), 0);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification();
//        notification.icon = R.mipmap.ic_launcher;
//        notification.tickerText = "闪屏展示信息";
//        notification.when = System.currentTimeMillis();
//        notification.setLatestEventInfo(context, "通知标题", "通知的详细内容", pendingIntent);
//        notification.number = 1;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//        // 通过通知管理器来发起通知。如果id不同，则每click，在statu那里增加一个提示
//        notification.defaults = Notification.DEFAULT_SOUND;
//        notificationManager.notify(1, notification);
    }
}
