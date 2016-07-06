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
package com.lb.wecharenglish.domain;

import android.support.annotation.NonNull;

import com.lb.utils.EncryptUtil;

import java.io.Serializable;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/3 22:31<br>
 * 类描述：英语每日一句对应的javabean封装 <br>
 */
public class EnglishBean implements Serializable, Comparable<EnglishBean> {

    private String id;//id
    private String title;//标题
    private String desc;//详情
    private long date;//时间
    private long loadDate;//加载的时间
    private boolean isShow;//是否在界面上显示

    public String getId() {
        return EncryptUtil.md5(getTitle() + getDate());
    }

    public void setId() {
        this.id = EncryptUtil.md5(getTitle() + getDate());
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(long loadDate) {
        this.loadDate = loadDate;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnglishBean that = (EnglishBean) o;

        return getId().equals(that.getId());

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (int) (loadDate ^ (loadDate >>> 32));
        result = 31 * result + (isShow ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnglishBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", date='" + date + '\'' +
                ", loadDate='" + loadDate + '\'' +
                ", isShow=" + isShow +
                '}';
    }

    @Override
    public int compareTo(@NonNull EnglishBean englishBean) {
        //        当两个对象进行比较时，
        //  返回0代表它们相等；
        // 返回值<0（如例子中返回-1）代表this排在被比较对象之前；
        // 反之代表在被比较对象之后
        if (getDate() == englishBean.getDate()) {
            if (getLoadDate() == englishBean.getLoadDate()) {
                return getTitle().compareTo(englishBean.getTitle());
            }
            return (int) (englishBean.getLoadDate() - getLoadDate());
        }
        return (int) (englishBean.getDate() - getDate());
    }


}
