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
package com.lb.wecharenglish.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;

import com.lb.wecharenglish.dao.EnglishDao;
import com.lb.wecharenglish.db.EnglishDatabaseHelper;
import com.lb.wecharenglish.domain.EnglishBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/3 22:34<br>
 * 类描述：每日一句的dao层具体实现 <br>
 */
public class EnglishImpl implements EnglishDao {

    //===Desc:添加的相关操作===============================================================================================

    /**
     * 向数据库插入一条数据
     *
     * @param context 上下文对象
     * @param bean    需要插入的javabean
     * @return true:插入成功,false:插入失败
     */
    @Override
    public boolean add(Context context, EnglishBean bean) {
        EnglishDatabaseHelper helper = new EnglishDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EnglishDatabaseHelper.T_ID, bean.getId());
        values.put(EnglishDatabaseHelper.T_TITLE, bean.getTitle());
        values.put(EnglishDatabaseHelper.T_DESC, bean.getDesc());
        values.put(EnglishDatabaseHelper.T_DATE, bean.getDate());
        values.put(EnglishDatabaseHelper.T_LOAD_DATE, bean.getLoadDate());
        values.put(EnglishDatabaseHelper.T_IS_SHOW, bean.isShow() ? 1 : 0);
        long insert = db.insert(EnglishDatabaseHelper.TABLE_NAME, null, values);
        db.close();
        return insert > 0;
    }

    //===Desc:查询相关的方法===============================================================================================

    /**
     * 根据id查询数据库中对应的对象
     *
     * @param context 上下文对象
     * @param id      需要查询的id
     * @return 数据库中存在对象返回对应的对象，不存在返回null
     */
    @Override
    public EnglishBean findByID(Context context, String id) {
        EnglishDatabaseHelper helper = new EnglishDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        //进行查询
        Cursor cursor = db.query(EnglishDatabaseHelper.TABLE_NAME, null, EnglishDatabaseHelper.T_ID + "=?", new String[]{id}, null, null, EnglishDatabaseHelper.T_DATE + " ASC");

        if (null != cursor) {
            if (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_TITLE));
                String desc = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_DESC));
                long date = cursor.getLong(cursor.getColumnIndex(EnglishDatabaseHelper.T_DATE));
                long loadDate = cursor.getLong(cursor.getColumnIndex(EnglishDatabaseHelper.T_LOAD_DATE));
                int dbIsShow = cursor.getInt(cursor.getColumnIndex(EnglishDatabaseHelper.T_IS_SHOW));

                EnglishBean bean = new EnglishBean();
                bean.setId();
                bean.setTitle(title);
                //noinspection deprecation
                bean.setDesc(Html.fromHtml(desc).toString());
                bean.setDate(date);
                bean.setLoadDate(loadDate);
                bean.setShow(dbIsShow != 0);

                cursor.close();
                db.close();
                return bean;
            } else {
                cursor.close();
                db.close();
                return null;
            }
        } else {
            db.close();
            return null;
        }
    }

    /**
     * 根据pageNo和pageSize枫叶查询贝蒂数据
     *
     * @param context  赏析文对象
     * @param begin    开始查询的位置
     * @param pageSize 一页显示的条数
     * @return 对应的数据集合
     */
    public List<EnglishBean> getDataByPage(Context context, int begin, int pageSize) {
        List<EnglishBean> list = new ArrayList<>();

        EnglishDatabaseHelper helper = new EnglishDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(EnglishDatabaseHelper.TABLE_NAME, null, EnglishDatabaseHelper.T_IS_SHOW + "=?",
                new String[]{String.valueOf(1)}, null, null, EnglishDatabaseHelper.T_DATE + " ASC", begin + "," + pageSize);
        if (null == cursor) {
            db.close();
            return list;
        }

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_ID));
            String title = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_TITLE));
            String desc = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_DESC));
            long date = cursor.getLong(cursor.getColumnIndex(EnglishDatabaseHelper.T_DATE));
            long loadDate = cursor.getLong(cursor.getColumnIndex(EnglishDatabaseHelper.T_LOAD_DATE));
            int dbIsShow = cursor.getInt(cursor.getColumnIndex(EnglishDatabaseHelper.T_IS_SHOW));

            EnglishBean bean = new EnglishBean();
            bean.setId(id);
            bean.setTitle(title);
            bean.setDesc(desc);
            bean.setDate(date);
            bean.setLoadDate(loadDate);
            bean.setShow(dbIsShow != 0);

            list.add(bean);
        }

        cursor.close();
        db.close();

        return list;

    }

    /**
     * 获取数据库中总条数
     *
     * @return 数据库中总条数
     */
    @Override
    public int getTotalCount(Context context) {
        EnglishDatabaseHelper helper = new EnglishDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        // 调用查找书库代码并返回数据源
        Cursor cursor = db.rawQuery("select count(*) from " + EnglishDatabaseHelper.TABLE_NAME, null);
        //游标移到第一条记录准备获取数据
        cursor.moveToFirst();
        // 获取数据中的LONG类型数据
        Long count = cursor.getLong(0);
        cursor.close();
        db.close();
        return count.intValue();
    }
}
