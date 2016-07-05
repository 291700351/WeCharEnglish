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
package com.lb.wecharenglish.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/3 22:13<br>
 * 类描述：每日一句英语的数据库帮助类 <br>
 */
public class EnglishDatabaseHelper extends SQLiteOpenHelper {
    //===Desc:成员变量===============================================================================

    /**
     * 数据库名称
     */
    public static String DB_NAME = "english.db";

    /**
     * 表名
     */
    public static String TABLE_NAME = "englishs";


    /**
     * 数据库字段id
     */
    public static String T_ID = "_id";

    /**
     * 数据库字段title
     */
    public static String T_TITLE = "_title";

    /**
     * 数据库字段desc
     */
    public static String T_DESC = "_desc";

    /**
     * 数据库字段date
     */
    public static String T_DATE = "_date";

    /**
     * 数据库字段_loadDate  加载时间
     */
    public static String T_LOAD_DATE = "_loadDate";

    /**
     * 数据库字段isShow，是否显示
     */
    public static String T_IS_SHOW = "_isShow";

    //===Desc:图片数据库相关的字段==========================================================================================
    /**
     * 图片的表名
     */
    public static String TABLE_NAME_IMG = "englishs_img";

    /**
     * 图片数据库的图片id
     */
    public static String T_IMG_ID = "_img_id";

    /**
     * 图片数据库的图片url
     */
    public static String T_IMG_URL = "_img_url";

    /**
     * 图片数据  图片对应的额英语的id
     */
    public static String T_IMG_ENGLISG = "_img_englisg";

    //===Desc:构造函数===============================================================================================
    public EnglishDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    //===Desc:复写父类中的方法===============================================================================
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //每日一句表
        String sql = "create table " + TABLE_NAME + "("
                + T_ID + " VARCHAR PRIMARY KEY,"
                + T_TITLE + "  VARCHAR,"
                + T_DESC + "  VARCHAR,"
                + T_DATE + "  VARCHAR,"
                + T_LOAD_DATE + "  VARCHAR,"
                + T_IS_SHOW + "  int)";
        sqLiteDatabase.execSQL(sql);
        //对应的图片表
        String imgSql = "create table " + TABLE_NAME_IMG + "("
                + T_IMG_ID + " VARCHAR PRIMARY KEY,"
                + T_IMG_URL + "  VARCHAR,"
                + T_IMG_ENGLISG + "  VARCHAR)";
        sqLiteDatabase.execSQL(imgSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    //===Desc:本类中使用的方法===============================================================================


    //===Desc:外界使用的方法==========================================================================================


}
