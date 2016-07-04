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
package com.lb.wecharenglish.server;

import android.content.Context;
import android.text.TextUtils;

import com.lb.wecharenglish.dao.EnglishDao;
import com.lb.wecharenglish.dao.impl.EnglishImpl;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.net.Urls;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/3 22:36<br>
 * 类描述：每日一句的业务层代码实现 <br>
 */
public class EnglishServer {
    //===Desc:成员变量===============================================================================
    private EnglishDao dao;

    //===Desc:构造函数===============================================================================================
    public EnglishServer() {
        dao = new EnglishImpl();
    }

    //===Desc:本类中使用的方法===============================================================================

    /**
     * 向数据库插入一条数据
     *
     * @param context 上下文对象
     * @param bean    需要插入的javabean
     * @return true:插入成功,false:插入失败
     */
    public boolean add(Context context, EnglishBean bean) {
        if (null == bean) return false;
        //设置对象的id，根据title和date进行MD5
        bean.setId();
        //设置加载时间
        bean.setLoadDate(System.currentTimeMillis());
        //设置显示
        bean.setShow(true);
        //先判断数据库中是否已经包含该对象，已经包含就不用添加
        EnglishBean dbBean = findById(context, bean.getId());
        if (null != dbBean) return false;
        //调用dao
        return dao.add(context, bean);
    }

    /**
     * 根据id查询数据库中的对象
     *
     * @param context 上下文对象
     * @param id      需要查询的id
     * @return 数据库中存在对象返回对应的对象，不存在返回null
     */
    public EnglishBean findById(Context context, String id) {
        if (TextUtils.isEmpty(id)) return null;
        //调用dao
        return dao.findByID(context, id);
    }

    /**
     * 根据pageNo和pageSize枫叶查询贝蒂数据
     *
     * @param context  赏析文对象
     * @param pageNo   页号
     * @param pageSize 一页显示的条数
     * @return 对应的数据集合
     */
    public List<EnglishBean> getDataByPage(Context context, int pageNo, int pageSize) {


        //计算pageNo的最大数，获取数据库中的总条数
        int totalCount = dao.getTotalCount(context);
        int totalPage;
        if (totalCount % pageSize == 0) {
            totalPage = totalCount / pageSize;
        } else {
            totalPage = totalCount / pageSize + 1;
        }
        if (pageNo > totalPage)
            pageNo = totalPage;
        //通过pageNo计算出数据库中应该开始的位置
        if (pageNo < 1) {
            pageNo = 1;
        }
        int begin = (pageNo - 1) * pageSize;
        return dao.getDataByPage(context, begin, pageSize);
    }

    /**
     * 从远端获取数据
     */
    public List<EnglishBean> getDataFromRemote() {
        List<EnglishBean> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect(Urls.ENGLISH_URL).get();
            if (null == document) return list;
            Elements items = document.getElementsByTag("item");
            if (null == items || items.size() == 0) return list;

            for (Element item : items) {

                Elements titles = item.getElementsByTag("title");
                if (null == titles || titles.size() == 0) return list;
                String title = titles.get(0).html();

                Elements descs = item.getElementsByTag("description");
                if (null == descs || descs.size() == 0) return list;
                String desc = descs.get(0).html();

                Elements dates = item.getElementsByTag("pubDate");
                if (null == dates || dates.size() == 0) return list;
                String date = dates.get(0).html();

                //创建对象进行封装
                EnglishBean bean = new EnglishBean();
                bean.setTitle(title);
                bean.setDesc(desc);
                bean.setDate(new Date(date).getTime());

                list.add(bean);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
