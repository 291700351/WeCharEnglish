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

import com.lb.wecharenglish.dao.EnglishImgDao;
import com.lb.wecharenglish.dao.impl.EnglishImgImpl;
import com.lb.wecharenglish.domain.EnglishImgBean;

import java.util.List;

/**
 * 项目名称：WeCharEnglish<br>
 * 作者：Ice<br>
 * 邮箱： lb291700351@live.cn<br>
 * 时间：2016/7/5 18:54<br>
 * 类描述：每日一句对应的图片的业务层<br>
 */
public class EnglishImgServer {
    //===Desc:成员变量======================================================================================

    private EnglishImgDao dao;

    //===Desc:构造函数======================================================================================

    public EnglishImgServer() {
        dao = new EnglishImgImpl();
    }

    //===Desc:复写父类中的方法======================================================================================

    /**
     * 业务层向数据库添加一条图片数据的方法
     *
     * @param context 上下文对象
     * @param bean    图片对象
     * @return 添加成功返回true
     */
    public boolean add(Context context, EnglishImgBean bean) {
        return null != bean && dao.add(context, bean);
    }

    /**
     * 通过每日一句的id查询数据库中对应的图片
     * @param context 上下文对象
     * @param englishId 每日一句的id
     * @return 对应的图片的集合
     */
    public List<EnglishImgBean> getImgsByEnglishId(Context context, String englishId) {
        if(TextUtils.isEmpty(englishId)){
            return null;
        }
        return dao.getImgsByEnglishId(context,englishId);
    }
}
