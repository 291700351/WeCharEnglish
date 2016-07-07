package com.lb.wecharenglish.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lb.wecharenglish.domain.EnglishBean;

import java.util.List;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/3 22:35<br>
 * 类描述：每日一句的dao层 <br>
 */
public interface EnglishDao {

    /**
     * 向数据库插入一条数据
     *
     * @param context 上下文对象
     * @param bean    需要插入的javabean
     * @return true:插入成功,false:插入失败
     */
    boolean add(Context context, EnglishBean bean);

    /**
     * 根据id查询数据库中对应的对象
     *
     * @param context 上下文对象
     * @param id      需要查询的id
     * @return 数据库中存在对象返回对应的对象，不存在返回null
     */
    EnglishBean findByID(Context context, String id);

    /**
     * 根据pageNo和pageSize枫叶查询贝蒂数据
     *
     * @param context  赏析文对象
     * @param begin    开始查询的位置
     * @param pageSize 一页显示的条数
     * @return 对应的数据集合
     */
    List<EnglishBean> getDataByPage(Context context, int begin, int pageSize);

    /**
     * DAO中统计个数的方法
     *
     * @return 数据库中数据条数
     */
    int getTotalCount(Context context);

//    /**
//     * 从给定数据库中复制数据到本地数据库中
//     *
//     * @param db 指定数据库元
//     * @return 本地数据库变动的条数
//     */
//    int copyDataFromDatabas(SQLiteDatabase db);

}
