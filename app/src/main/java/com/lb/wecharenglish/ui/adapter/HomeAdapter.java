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
package com.lb.wecharenglish.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lb.utils.ViewUtil;
import com.lb.wecharenglish.R;
import com.lb.wecharenglish.domain.EnglishBean;

import java.util.List;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/4 0:54<br>
 * 类描述：主页数据的adapter <br>
 */
public class HomeAdapter extends BaseAdapter {

    //===Desc:成员变量===============================================================================
    private LayoutInflater inflater;
    private List<EnglishBean> datas;

    //===Desc:构造函数===============================================================================================

    public HomeAdapter(Context context, List<EnglishBean> datas) {
        this.inflater = LayoutInflater.from(context);
        this.datas = datas;
    }

    //===Desc:复写父类中的方法===============================================================================
    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public EnglishBean getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (null == view) {
            view = inflater.inflate(R.layout.adapter_home, viewGroup, false);
        }
        ViewHolder holder = ViewHolder.getHolder(view);
        //设置数据显示
        EnglishBean bean = datas.get(i);
        holder.tv_item_title.setText(bean.getTitle());
        holder.tv_item_date.setText(bean.getDate());
        holder.tv_item_desc.setText(Html.fromHtml(bean.getDesc()));



        return view;
    }
    //===Desc:本类中使用的方法===============================================================================

    private static class ViewHolder {
        TextView tv_item_title;
        TextView tv_item_date;
        TextView tv_item_desc;


        private ViewHolder(View view) {
            tv_item_title = ViewUtil.findViewById(view, R.id.tv_item_title);
            tv_item_date = ViewUtil.findViewById(view, R.id.tv_item_date);
            tv_item_desc = ViewUtil.findViewById(view, R.id.tv_item_desc);

        }

        public static ViewHolder getHolder(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (null == holder) {
                holder = new ViewHolder(view);
                view.setTag(holder);
            }
            return holder;
        }
    }


}
