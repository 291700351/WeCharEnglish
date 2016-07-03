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
package com.lb.wecharenglish.net;

import com.lb.utils.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 项目名称：ysp-android<br>
 * 作者：IceLee<br>
 * 邮箱：lb291700351@live.cn<br>
 * 时间：2016/7/3 23:05<br>
 * 类描述：请求网络的帮助类 <br>
 */
public class Httphelper {

    //===Desc:禁止实例化该类===============================================================================================
    private static String TAG = "Httphelper";

    private Httphelper() {
        throw new UnsupportedOperationException("The class " + getClass().getSimpleName() + " can not be instance......" );
    }

    //===Desc:外界使用的方法===============================================================================================

    /**
     * get方式请求网络，获取响应的字符串
     *
     * @param url 请求的url地址
     * @return 响应的字符串
     */
    public static String get(String url) {
        try {
            LogUtil.log(TAG, "Request Url : " + url);
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET" );
            // 设置连接超时时间10秒
            conn.setConnectTimeout(1000 * 10);
            conn.setReadTimeout(1000 * 10);
            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                String result = inputStream2String(conn.getInputStream());
                LogUtil.e(TAG, "Response Data : " + result);
                return result;
            }
        } catch (IOException e) {
            LogUtil.log(TAG, e);
        }


        return "";
    }

    /**
     * 将InputStream中的是数据转换成字符串
     *
     * @param is InputStream流
     * @return 将流转换成字符串的表现形式
     * @throws IOException 抛出异常
     */
    private static String inputStream2String(InputStream is) throws IOException {
        StringWriter sw = new StringWriter();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while ((line = br.readLine()) != null) {
            sw.write(line);
            sw.flush();
        }
        String temp = sw.toString();
        br.close();
        sw.close();
        return temp;
    }
}
