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

import com.lb.wecharenglish.dao.EnglishImgDao;
import com.lb.wecharenglish.db.EnglishDatabaseHelper;
import com.lb.wecharenglish.domain.EnglishImgBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：WeCharEnglish<br>
 * 作者：Ice<br>
 * 邮箱： lb291700351@live.cn<br>
 * 时间：2016/7/5 18:52<br>
 * 类描述：<br>
 */
public class EnglishImgImpl implements EnglishImgDao {
    //===Desc:添加相关的方法==========================================================================================

    /**
     * 向数据库插入一条数据
     *
     * @param context 上下文对象
     * @return 插入成功返回true
     */
    @Override
    public boolean add(Context context, EnglishImgBean bean) {
        EnglishDatabaseHelper helper = new EnglishDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EnglishDatabaseHelper.T_IMG_ID, bean.getId());
        values.put(EnglishDatabaseHelper.T_IMG_URL, bean.getUrl());
        values.put(EnglishDatabaseHelper.T_IMG_ENGLISG, bean.getEnglishId());
        long insert = db.insert(EnglishDatabaseHelper.TABLE_NAME_IMG, null, values);
        db.close();
        return insert > 0;
    }

    /**
     * 通过每日一句的id查询数据库中对应的图片集合
     *
     * @param context   上下文对象
     * @param englishId 每日一句的id
     * @return 对应的图片数据集合
     */
    @Override
    public List<EnglishImgBean> getImgsByEnglishId(Context context, String englishId) {
        List<EnglishImgBean> list = new ArrayList<>();

        EnglishDatabaseHelper helper = new EnglishDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(EnglishDatabaseHelper.TABLE_NAME_IMG, null,
                EnglishDatabaseHelper.T_IMG_ENGLISG + "=?",
                new String[]{englishId}, null, null, null);
        if (null == cursor)
            return list;
        EnglishImgBean bean;
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_IMG_ID));
            String url = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_IMG_URL));
            String eId = cursor.getString(cursor.getColumnIndex(EnglishDatabaseHelper.T_IMG_ENGLISG));
            bean = new EnglishImgBean();
            bean.setId(id);
            bean.setUrl(url);
            bean.setEnglishId(eId);
            list.add(bean);
        }
        cursor.close();
        db.close();

        return list;
    }
}
