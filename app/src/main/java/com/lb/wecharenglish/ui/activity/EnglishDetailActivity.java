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

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.lb.utils.LogUtil;
import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.R;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.global.Keys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/5 20:36<br>
 * 类描述： <br>
 */
public class EnglishDetailActivity extends BaseActivity {
    //===Desc:成员变量===============================================================================

    /**
     * 上一个界面传递过来的每日一句对象
     */
    private EnglishBean englishBean;

    /**
     * 显示标题的控件
     */
    private TextView tv_detail_title;

    /**
     * 显示时间的控件
     */
    private TextView tv_detail_time;

    /**
     * 显示详情的控件
     */
    private TextView tv_detail_desc;


    //===Desc:复写父类中的方法===============================================================================


    @Override
    protected void initData() {
        englishBean = (EnglishBean) getIntent().getSerializableExtra(Keys.KEY_ENGLISH_BEAN);
        if (null == englishBean)//如果三个界面没有传递过来对象 就关闭当前页面
            finish();

    }

    @Override
    protected View createView() {
        return View.inflate(mContext, R.layout.activity_english_detail, null);
    }

    @Override
    protected void findView() {
        tv_detail_title = ViewUtil.findViewById(this, R.id.tv_detail_title);
        tv_detail_time = ViewUtil.findViewById(this, R.id.tv_detail_time);
        tv_detail_desc = ViewUtil.findViewById(this, R.id.tv_detail_desc);
    }


    @Override
    protected void setViewData() {
        tv_detail_title.setText(englishBean.getTitle());
        String time = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(new Date(englishBean.getDate()));
        tv_detail_time.setText(time);
        String desc = englishBean.getDesc();

        desc = desc.replaceAll("<img[^>]*>", " ");
        LogUtil.e(this, desc);


        //noinspection deprecation
        tv_detail_desc.setText(Html.fromHtml(desc));
    }

    @Override
    protected void setListener() {

    }

    //===Desc:本类中使用的方法===============================================================================

}
