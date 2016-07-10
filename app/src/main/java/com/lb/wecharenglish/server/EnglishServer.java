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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;

import com.lb.utils.LogUtil;
import com.lb.utils.SdCardUtil;
import com.lb.wecharenglish.dao.EnglishDao;
import com.lb.wecharenglish.dao.impl.EnglishImpl;
import com.lb.wecharenglish.db.EnglishDatabaseHelper;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.domain.EnglishImgBean;
import com.lb.wecharenglish.net.Urls;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        //设置不是收藏
        bean.setLike(false);
        //设置显示
        bean.setShow(true);
        //先判断数据库中是否已经包含该对象，已经包含就不用添加
        EnglishBean dbBean = findById(context, bean.getId());
        if (null != dbBean) return false;

        //解析出img标签，添加进数据库
        //noinspection deprecation
        Document document = Jsoup.parse(Html.fromHtml(bean.getDesc()).toString());
        Elements imgElements = document.select("img");
        for (int j = 0; j < imgElements.size(); j++) {
            Element imgElement = imgElements.get(j);
            String url = imgElement.attr("src");
            EnglishImgBean imgBean = new EnglishImgBean();
            imgBean.setEnglishId(bean.getId());
            imgBean.setUrl(url);
            imgBean.setId(UUID.randomUUID().toString().replace("-", ""));
            //向数据库插入一张图片
            new EnglishImgServer().add(context, imgBean);
        }
        bean.setDesc(bean.getDesc());
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
     * @param isLike   是否是收藏 0：全部，1：收藏的，2：不是收藏的
     * @param pageNo   页号
     * @param pageSize 一页显示的条数
     * @return 对应的数据集合
     */
    public List<EnglishBean> getDataByPage(Context context, int isLike, int pageNo, int pageSize) {
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
        return dao.getDataByPage(context, isLike, begin, pageSize);
    }

    /**
     * 从远端获取数据
     */
    public List<EnglishBean> getDataFromRemote() {
        List<EnglishBean> list = new ArrayList<>();
        try {
            Document document;
            if (LogUtil.isDebug)
                document = Jsoup.connect(Urls.ENGLISH_URL).timeout(10).get();
            else
                document = Jsoup.connect(Urls.ENGLISH_URL).timeout(60 * 1000).get();
            if (null == document) return list;
            Elements items = document.getElementsByTag("item");
            if (null == items || items.size() == 0) return list;

            for (Element item : items) {

                Elements titles = item.getElementsByTag("title");
                if (null == titles || titles.size() == 0) return list;
                String title = titles.get(0).html();

                Elements descs = item.getElementsByTag("description");
                if (null == descs || descs.size() == 0) return list;
                //将详情里面的转义字符转换成标签
                //noinspection deprecation
                String desc = descs.get(0).html();
                Elements dates = item.getElementsByTag("pubDate");
                if (null == dates || dates.size() == 0) return list;
                String date = dates.get(0).html();

                //创建对象进行封装
                EnglishBean bean = new EnglishBean();
                bean.setTitle(title);
                bean.setDesc(desc);
                //noinspection deprecation
                bean.setDate(new Date(date).getTime());

                list.add(bean);
            }
            return list;
        } catch (IOException e) {
            LogUtil.log(this, e);
        }

        return list;
    }

    /**
     * 从给定数据库中复制数据到本地数据库中
     *
     * @param db 指定数据库元
     * @return 本地数据库变动的条数
     */
    public int copyDataFromDatabas(Context context, @NonNull SQLiteDatabase db) {
        //读取指定数据库的数据
        Cursor cursor = db.query(EnglishDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (null == cursor) return 0;
        //使用计数器
        int count = 0;
        EnglishBean bean;
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_ID));
            String title = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_TITLE));
            String desc = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_DESC));
            long date = cursor.getLong(cursor.getColumnIndex(EnglishDatabaseHelper.T_DATE));
//            int dbIsLike = cursor.getInt(cursor.getColumnIndex(EnglishDatabaseHelper.T_IS_LIKE));
            int dbIsShow = cursor.getInt(cursor.getColumnIndex(EnglishDatabaseHelper.T_IS_SHOW));

            bean = new EnglishBean();
            bean.setId(id);
            bean.setTitle(title);
            bean.setDesc(desc);
            bean.setDate(date);
            bean.setLike(false);
            bean.setShow(dbIsShow != 0);

            //先本地数据进行添加操作
            if (add(context, bean))
                count++;
        }
        cursor.close();
        return count;
    }

    /**
     * 更新一条数据库中的数据
     *
     * @param context   上下文
     * @param englishId 每日一句的id
     * @param isLike    设置成收藏或移除收藏
     * @return 修改成功返回true
     */
    public boolean setLike(Context context, String englishId, boolean isLike) {
        //获取数据库中对应id的bean对象
        EnglishBean dbBean = findById(context, englishId);
        if (null == dbBean)
            return false;
        dbBean.setLike(isLike);
        int count = dao.update(context, dbBean);
        return count > 1;
    }

    public boolean exportFile(Context context, List<EnglishBean> datas) {
        if (null == datas || datas.size() == 0) return false;
        String path = SdCardUtil.getSDCardPath() + File.separator + context.getPackageName() + File.separator
                + "export" + File.separator + "local";
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd-HH-mm-ss", Locale.CHINESE);
        String fileName = "everyday_export_" + format.format(date) + ".md";
        File exportFile = new File(path);
        if (!exportFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            exportFile.mkdirs();
        }

        exportFile = new File(exportFile, fileName);
        if (!exportFile.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                exportFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(exportFile);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (EnglishBean bean : datas) {
                bw.write("##<font color=red>" + bean.getTitle() + "</font>");
                bw.newLine();

                //noinspection deprecation
                String desc = Html.fromHtml(bean.getDesc()).toString();
                Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);
                Matcher m_html = p_html.matcher(desc);
                desc = m_html.replaceAll("<br/>"); // 过滤html标签

                //noinspection deprecation
                bw.write(Html.fromHtml(desc).toString());
                bw.newLine();
                String time = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINESE)
                        .format(new Date(bean.getDate()));
                bw.write(time);
                bw.newLine();
                bw.write("<hr/>");
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bw) {
                try {
                    bw.close();
                } catch (IOException e) {
                    LogUtil.log(this, e);
                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

}