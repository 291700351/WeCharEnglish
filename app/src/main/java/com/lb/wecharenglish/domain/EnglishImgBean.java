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

/**
 * 项目名称：WeCharEnglish<br>
 * 作者：Ice<br>
 * 邮箱： lb291700351@live.cn<br>
 * 时间：2016/7/5 18:44<br>
 * 类描述：每日一句对应的图片的bean对象<br>
 */
public class EnglishImgBean {

    private String iId;
    private String iUrl;
    private String iEnglishId;
    //===Desc:构造函数==========================================================================================

    public String getId() {
        return iId;
    }

    public void setId(String id) {
        this.iId = id;
    }

    public String getUrl() {
        return iUrl;
    }

    public void setUrl(String iUrl) {
        this.iUrl = iUrl;
    }

    public String getEnglishId() {
        return iEnglishId;
    }

    public void setEnglishId(String iEnglishId) {
        this.iEnglishId = iEnglishId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EnglishImgBean that = (EnglishImgBean) o;

        return iId != null ? iId.equals(that.iId) : that.iId == null
                && (iUrl != null ? iUrl.equals(that.iUrl) : that.iUrl == null
                && (iEnglishId != null ? iEnglishId.equals(that.iEnglishId) : that.iEnglishId == null));

    }

    @Override
    public int hashCode() {
        int result = iId != null ? iId.hashCode() : 0;
        result = 31 * result + (iUrl != null ? iUrl.hashCode() : 0);
        result = 31 * result + (iEnglishId != null ? iEnglishId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EnglishImgBean{" +
                "iId='" + iId + '\'' +
                ", iUrl='" + iUrl + '\'' +
                ", iEnglishId='" + iEnglishId + '\'' +
                '}';
    }
}
