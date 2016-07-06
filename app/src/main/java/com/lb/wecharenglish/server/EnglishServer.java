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
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;

import com.lb.utils.EncryptUtil;
import com.lb.utils.LogUtil;
import com.lb.wecharenglish.dao.EnglishDao;
import com.lb.wecharenglish.dao.impl.EnglishImpl;
import com.lb.wecharenglish.domain.EnglishBean;
import com.lb.wecharenglish.domain.EnglishImgBean;
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

        //解析出img标签，添加进数据库
        Document document = Jsoup.parse(bean.getDesc());
        Elements imgElements = document.select("img");
        for (int j = 0; j < imgElements.size(); j++) {
            Element imgElement = imgElements.get(j);
            String url = imgElement.attr("src");
            EnglishImgBean imgBean = new EnglishImgBean(url, bean.getId());
            LogUtil.log(this, imgBean);
            //向数据库插入一张图片
            new EnglishImgServer().add(context, imgBean);
        }

        String desc = bean.getDesc();
//        String br = EncryptUtil.md5(context.getResources().getString(com.lb.utils.R.string.app_name));
//        desc = desc.replaceAll("<[^>]*>", br);
//        while (desc.contains(br + br)) {
//            desc = desc.replaceAll(br + br, br);
//        }
//        desc = desc.replaceAll("&quot;", "\"");
//        desc = desc.replaceAll("&nbsp;", " ");
        bean.setDesc(desc);
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
//        try {
        String s = "<rss version=\"2.0\">\n" +
                "<channel>\n" +
                "<title>何凯文考研英语</title>\n" +
                "<link>http://www.iwgc.cn/list/6307</link>\n" +
                "<description>何凯文考研英语辅导账号</description>\n" +
                "<item>\n" +
                "<title>少年,资本的力量，企业的并购</title>\n" +
                "<link>http://www.iwgc.cn/link/1784565</link>\n" +
                "<description>\n" +
                "<p><span></span></p><p><span><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=134&amp;play_length=05:37\" name=\"134\" play_length=\"337000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTYw\"></mpvoice><p><p><span></span><br/></p><p><span>&quot;他们说生命就是周而复始 可是昙花不是 流水不是 少年在每一分秒的绽放与流动中 也从来不是.&quot;</span><br/></p><p>一晃又到了七月，今天在温大，遇到几个离校的上一届考生；再走进去年的教室时，一切如初，有一切都不一样了。</p><p>祝福远行的你们！</p><p>剩下的我们一起加油！</p><p><span>今天句子的题目：</span></p><p><span>Many captive shippers also worry they will soon be hit with a round of huge rate increases. The railroad industry as a whole, despite its brightening fortunes, still does not earn enough to cover the cost of the capital it must invest to keep up with its surging traffic. Yet railroads continue to borrow billions to acquire one another, with Wall Street cheering them on. Consider the $10.2 billion bid by Norfolk&nbsp;</span><span>Southern and CSX to acquire Conrail this year. Conrail’s net railway operating income in 1996 was just $427 million, less than half of the carrying costs of the transaction. Who’s going to pay for the rest of the bill? Many captive shippers fear that&nbsp;</span></p><p><span>they will, as Norfolk Southern and CSX increase their grip on the market.</span></p><p>According to the text, the cost increase in the rail industry is mainly caused by ________.</p><p>[A] the continuing acquisition</p><p>[B] the growing traffic</p><p>[C] the cheering Wall Street</p><p>[D] the shrinking market</p><p><iframe scrolling=\"no\" frameborder=\"0\" class=\"vote_iframe js_editor_vote_card\" data-display-style=\"height: 199px;\" data-display-src=\"/cgi-bin/readtemplate?t=vote/vote-new_tmpl&amp;__biz=MjM5NTEzMjY4MQ==&amp;supervoteid=4136454&amp;token=205136463&amp;lang=zh_CN\" data-src=\"/mp/newappmsgvote?action=show&amp;__biz=MjM5NTEzMjY4MQ==&amp;supervoteid=4136454#wechat_redirect\" data-supervoteid=\"4136454\" allowfullscreen=\"\"></iframe><span></span><span></span></span></p><p><span>昨天句子的解析：</span><span><br/></span></p><p>Across the Continent, officials are already vocally trying to entice financial services firms, technology startups and others to forgo the British capital for cities like Paris, Luxembourg, Frankfurt and even the relatively tiny Lithuanian capital, Vilnius. They warn that businesses will suffer if they stay in a Britain that no longer has unfettered access to the EU and its hundreds of millions of potential customers.</p><p><span>词汇突破：</span>1. Entice 引诱</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2. startup 创业型公司</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3. forgo ...for...放弃...到...地方去</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4. unfetter access to 不受限制的进入(获得)</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 5. vocally 大声地</p><p>Across the Continent, officials are already vocally trying to entice financial services firms, technology startups and others to forgo the British capital for cities like Paris, Luxembourg, Frankfurt and even the relatively tiny Lithuanian capital, Vilnius.</p><p><span>参考译文：</span>欧洲大陆各地的官员已经纷纷发声，竭力怂恿金融服务公司、科技初创企业以及其他机构抛弃英国首都伦敦，前往巴黎、卢森堡、法兰克福，甚至是相对较小的立陶宛首都维尔纽斯之类的城市。</p><p>They warn that businesses will suffer if they stay in a Britain that no longer has unfettered access to the EU and its hundreds of millions of potential customers.</p><p><span>确定主干：</span>They warn that…</p><p><span>切分成分：</span>1.Businesses will suffer if they stay in a Britain 宾语从句</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 企业的日子会很不好过。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2. (That =Britain) no longer has unfettered access to the EU and its hundreds of millions of potential customers.</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 英国不再拥有无条件进入欧盟市场、接触其亿万潜在消费者资格的</p><p><span>参考译文：</span>他们警告说，如果留守英国，企业的日子会很不好过,因为英国不再拥有无条件进入欧盟市场、接触其亿万潜在消费者的资格。</p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Mon, 04 Jul 2016 14:11:24 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>2017考研阅读热点预测之：英国脱欧系列报道</title>\n" +
                "<link>http://www.iwgc.cn/link/1773152</link>\n" +
                "<description>\n" +
                "<p><span></span></p><p><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=133&amp;play_length=08:10\" name=\"133\" play_length=\"490000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTU2\"></mpvoice></p><p><span></span><br/></p><p><span>从南京到温州，四个小时的车程，看书的好机会。</span></p><p>身体和灵魂总有一个要在路上，所以在旅途中看书是极好的事情。</p><p>前段时间闹得沸沸扬扬的英国脱欧，现在有一系列的文章开始报道了。</p><p><img src=\"http://img02.iwgc.cn/getimg.php?url=http://mmbiz.qpic.cn/mmbiz/8IHrq7Veh7vEMdzeuSA4bhODDnZCsDfZNRAicL1IvmAnk9er6upKwf8XPGG6vUicUiatJMEtkDuaa6jSI5vbVibRjQ/0?wx_fmt=jpeg\"/><br/></p><p>After ‘Brexit’ Vote, Europeans Jockey for London’s Business</p><p>退欧公投后，欧洲城市争夺伦敦商业地位</p><p>Within hours of Britain’s vote to leave the European Union, it started.</p><p>在英国投票脱欧不久后，事情就开始了。</p><p>尤其是中国和俄罗斯的豪们以后就不会在去英国了！</p><p>1.Britons had only just begun to digest the results of their referendum when cities and companies across Europe leapt into action, all of them jockeying to lure businesses, entrepreneurs and investment from London, the region’s economic behemoth.</p><p>词汇突破：1. their referendum 全民公投</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2. leapt into action 迅速开始行动</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3. jockey to do sth/ jockey for sth 谋求做…/谋求获得什么</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4.behemoth 超级巨擘，超级公司;巨无霸</p><p>参考译文：英国人才开始试着理解他们公投的后果，全欧洲的城市和公司已经迅速行动起来竞相抛出诱饵，想要把企业、创业者以及投资从身为欧洲经济巨无霸的伦敦吸引过去。</p><p>2. Much remains up in the air, as the country embarks on a yearslong process to leave the 28-nation bloc. Britain has not even officially filed to leave, and there will be painful negotiations on matters including trade and whether people from European Union nations will have the freedom to work in Britain, as they do now.</p><p>词汇突破：1. Up in the air 悬而未决</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2. embark on 开始</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3. bloc 联盟</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4. file to 申请</p><p>参考译文：英国要花费数年时间，才能脱离由28个成员国组成的欧盟。当它踏上这漫漫征程的时候，许多事情都悬而未决。英国甚至尚未正式提交脱欧申请，而且要就一些事情——比如贸易，再比如来自欧盟成员国的人们是否还能像现在这样拥有在英国工作的自由——进行艰苦的谈判。</p><p>3.Despite the high levels of uncertainty, others in Europe are, in effect, looking to gain from Britain’s pain.</p><p>尽管存在太多不确定性，但欧洲其他国家其实正期待从英国的痛苦中获益。</p><p>（这句话写的好！哈哈哈）</p><p><span>今天的句子：</span></p><p>4.Across the Continent, officials are already vocally trying to entice financial services firms, technology startups and others to forgo the British capital for cities like Paris, Luxembourg, Frankfurt and even the relatively tiny Lithuanian capital, Vilnius. They warn that businesses will suffer if they stay in a Britain that no longer has unfettered access to the EU and its hundreds of millions of potential customers.</p><p><br/></p><p>5. When it comes to the financial services industry, several cities have rolled out the red carpet to lure London-based companies.</p><p>至于金融服务业，若干城市已经铺开红毯，以便吸引总部位于伦敦的公司。</p><p>6. “When Britain goes out, a lot of firms will have to relocate in Europe,” Pécresse said. “There is a competition that is going to take place. It is already taking place between the big metropolises in Europe.”</p><p>“当英国退出时，许多公司都会在欧洲重新选择办公地点，”佩克雷斯说。“届时肯定会有竞争。事实上，欧洲各个大都会之间的竞争已经开始了。”</p><p>7. Along with those large cities, smaller financial centers — in Belgium, Luxembourg and the Netherlands — are also getting involved. Even Lithuania, a country not typically noted as a hub for business in Europe, much less high finance, is involved.</p><p>词汇突破：1.along with 除了</p><p>除了这些大城市，比利时、卢森堡以及荷兰的一些较小的金融中心也正加入战团。就连通常不会被视为欧洲商业中心，更别说是金融中心的立陶宛也加入了进来。</p><p>8.Despite the overtures from elsewhere, officials in London still believe they will retain most of the business and finance that is currently based there.</p><p>尽管其他地方纷纷抛出橄榄枝，但伦敦的官员仍然相信，自己可以留住大多数总部设在伦敦的商业和金融企业。</p><p>9.The competition, though, extends outside of financial services — Europe’s tech hubs are turning on the charm offensive, as well.</p><p>不过，竞争已经超出金融服务的范畴——欧洲的一些科技中心也发动了魅力攻势。</p><p>10.Real estate agents in major cities across the Continent, including Stockholm and Frankfurt, are also reporting increased interest from banks and technology companies casting their eyes away from London.</p><p>斯德哥尔摩、法兰克福等欧洲大陆各大城市的房地产经纪机构也纷纷表示，银行和科技公司现在更愿意把目光从伦敦转向其他地方。</p><p>昨天句子的解析：</p><p>Probably there is no one here/ who has not in the course of the day had occasion to set in motion a complex train of reasoning, of the very same kind, though differing in degree, as that which a scientific man goes through in tracing the causes of natural phenomena.</p><p>词汇讲解： 1. in course of 在……的过程中,在……中</p><p>如：The new textbook is in course ofpreparation.新教科书正在准备之中。</p><p>in the course of = during 在……期间，在……的时候</p><p>如：He has seen many changes in the course of his long life.</p><p>他在漫长的一生中目睹了许许多多的变化。</p><p>The company will face major challenges in the course of the next few years.</p><p>这家公司以后几年将面临重大的挑战。</p><p>所以这里的in the course of day 就直接理解为：</p><p>一天之中 （这是一个很装的用法，我们写作的时候不能用）</p><p>2. occasion 时候，时机</p><p>3. set in motion + sth 短语：发起…，着手做…</p><p>4. a train of 一系列的，一连串的</p><p>5. scientific man 科学家</p><p>确定主干：there is no one here</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 不存在这样一个人</p><p>切分成分+独立成句：&nbsp;</p><p>1.定语从句的主干</p><p>who has not in the course of the day had occasion to set in motion a complex train of reasoning. (has had 完成时态)</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;他一整天都没有机会进行一连串复杂的思考活动，</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;2. of the very same kind as that （reasoning）.</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;这种思考活动和下面的思考活动是同一类型。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;3. a scientific man goes through(this reasoning) in tracing the causes of natural phenomena. (Which = reasoning)&nbsp;</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 科学家在探索自然现象原因时也经历了这样的思考活动</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;4. though differing in degree （状语）</p><p>虽然在程度上不一样。</p><p>解释的还算清楚吧，KK够体贴了吧。</p><p>参考译文： 世上大概不会有人一整天都没有机会进行一连串复杂的思考活动，而这些思考活动与科学家在探索自然现象原因时所经历的思考活动，尽管复杂程度不同，但在类型上是完全一样的。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;改译：</p><p>世界上每一个人在一天中都会有机会进行一连串复杂的思考活动，而这些思考活动与科学家在探索自然现象原因时所经历的思考活动，尽管复杂程度不同，但在类型上是完全一样的。</p><p>温情提示：不要沮丧于看不懂，因为你之前就没看过，没人天生就会。都是慢慢积累的。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 不要沮丧于说不明白，因为你之前不这样说话，没有人天生就这样不正常。慢慢就会变得像这个句子一样不正常的。</p><p>又是新的一周，这样体贴的KK值得你的包养！</p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Sun, 03 Jul 2016 14:33:05 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>一个很难的句子，来挑战一下吧！</title>\n" +
                "<link>http://www.iwgc.cn/link/1763207</link>\n" +
                "<description>\n" +
                "<p><span></span></p><p><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=132&amp;play_length=04:31\" name=\"132\" play_length=\"271000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTUw\"></mpvoice></p><p><span>今天的句子：</span></p><p><span>一个很难的句子，不要退却！谁都是一边受伤，一边学会坚强的！</span></p><p><span>Probably there is no one here who has not in the course of the day had occasion to set in motion a complex train of reasoning, of the very same kind, though&nbsp;</span></p><p><span>differing in degree, as that which a scientific man goes through in tracing the&nbsp;</span></p><p><span>causes of natural phenomena.</span><br/></p><p><span>昨天句子的解析：</span><span><br/></span></p><p>Friedman relies on a lean staff of 20 in Austin.&nbsp;</p><p><span>弗莱德曼，依靠在奥斯丁的精干的20名员工。</span><br/></p><p>2.Several of his staff members have military-intelligence backgrounds.&nbsp;</p><p>其中的几名员工拥有军事情报的背景。</p><p>3.He sees the firm’s outsider status as the key to its success.&nbsp;</p><p>他把这个公司的局外人的地位当做是其成功的关键。</p><p>4.Straitford’s briefs don’t sound like the usual Washington back-and-forthing, whereby agencies avoid dramatic declarations on the chance they might be wrong.&nbsp;</p><p>Straightford公司的简报和常见的华盛顿官方开出的闪烁其词的简报并不一样，因为这些机构会避免剧烈的声明以防他们可能会是错了的。</p><p>5.Straitford, says Friedman, takes pride in its independent voice.</p><p>弗雷德曼说道，Straitford对于他们独立的声音感到自豪。</p><p>Straitford is most proud of its ________.</p><p>[A] official status</p><p>[B] nonconformist image</p><p>[C] efficient staff</p><p>[D] military background</p><p><span>题型识别：</span>细节题</p><p><span>题干定位：</span>本题为最后一个题目，同时是一个细节题，因此优先对应原文最后一段关于Straitford 的表述，并且关注题干中的most 一词，因为有可能意味着选项中有多项都是Straiford 所骄傲的，但是需要找出最为骄傲的内容。</p><p><span>思路解析：</span>最后一段中都是对于Straitford 的表述，需要逐一比对，并且关注题干中的most 一词，因为有可能意味着选项中有多项都是Straiford 所骄傲的，但是需要找出最为骄傲的内容。</p><p><span>选项分析：</span>[A] official status为错误选项，</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 原文明确说到了the firm’s outsider status（公司局外人的地位），就 &nbsp; &nbsp; &nbsp; &nbsp;说明其不是官方的，并且在前面也提到了这家公司是一家私人公司。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; [C] efficient staff 有效率的员工，这在原文中是提到的，Friedman relies on a lean staff of 20 in Austin.（这个公司只依靠很少的20多个员工）这里只是事实，而不是其骄傲的内容，属于定位错误。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; [D] military background军事背景，这在原文中也提到：Several of his staff members have military-intelligence &nbsp;backgrounds.（有一些雇员是有军事情报背景的），这是事实表述也为定位错误。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; [B] nonconformist image这是对于原文中：independent voice（独立声音）的模糊替换：非传统的形象，为最佳选项，所以入选答案为B。</p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Sat, 02 Jul 2016 15:24:21 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>读书的意义+经典的讲解+南京的雨夜</title>\n" +
                "<link>http://www.iwgc.cn/link/1755316</link>\n" +
                "<description>\n" +
                "<p><span><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=131&amp;play_length=07:22\" name=\"131\" play_length=\"442000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTQ2\"></mpvoice><p><p>前天和大家分享了我对读书的看法，今天把文字版写给大家，和大家共勉：</p><p>一个人内在越丰富，对人性就越了解，外在的需求就越少，内心的宁静就足够让我们走的很从容。所以我们可以去习惯独处，不必去羡慕说走就走的随性，一次旅行，一次相遇，一次离开，甚至一次伤害都不会改变我们的人生状态，只有丰富的精神世界才能让我们变得强大，这可能就是读书的意义。</p><p><span>今天的题目：</span></p><p>Friedman relies on a lean staff of 20 in Austin. Several of his staff members have military-intelligence backgrounds. He sees the firm’s outsider status as the key to its success. Straitford’s briefs don’t sound like the usual Washington back-and-forthing, whereby agencies avoid dramatic declarations on the chance they might be wrong. Straitford, says Friedman, takes pride in its independent voice.</p><p>45.<span>\t</span>Straitford is most proud of its ________.</p><p>[A] official status</p><p>[B] nonconformist image</p><p>[C] efficient staff</p><p>[D] military background</p><p><iframe scrolling=\"no\" frameborder=\"0\" class=\"vote_iframe js_editor_vote_card\" data-display-style=\"height: 199px;\" data-display-src=\"/cgi-bin/readtemplate?t=vote/vote-new_tmpl&amp;__biz=MjM5NTEzMjY4MQ==&amp;supervoteid=4118563&amp;token=2038423661&amp;lang=zh_CN\" data-src=\"/mp/newappmsgvote?action=show&amp;__biz=MjM5NTEzMjY4MQ==&amp;supervoteid=4118563#wechat_redirect\" data-supervoteid=\"4118563\" allowfullscreen=\"\"></iframe><span></span><span></span></span></p><p><span>昨天题目的解析：（一定要听语音解析！）</span></p><p>1. Straitford president George Friedman says he sees the online world as a kind of mutually reinforcing tool for both information collection and distribution, a spymaster’s dream.&nbsp;</p><p>Straiford的公司总裁乔治•弗雷德曼说，他将互联网当做是一种相互作用的工具，既可以用于情报的收集又可以用于情报的散发，这正是一个间谍大师所要实现的梦想。</p><p>2. Last week his firm was busy vacuuming up data bits from the far corners of the world and predicting a crisis in Ukraine.&nbsp;</p><p>上一周他的公司一直忙于从世界的各个遥远的角落去收集各种信息以预测在乌克兰可能会发生的危机。</p><p>3.“As soon as that report runs, we’ll suddenly get 500 new Internet sign-ups from Ukraine,” says Friedman, a former political science professor. “And we’ll hear back from some of them.”</p><p>“预测刚一开始，我们就立即收到500个来自于乌克兰的注册用户。”弗雷德曼说到，他曾经是一名政治学教授，“我们也会获取一些他们的反馈。”</p><p>4.Open-source spying does have its risks, of course, since it can be difficult to tell good information from bad. 5.That’s where Straitford earns its keep.</p><p>当然开放资源的情报活动是有其风险的，因为他很难区分出好的信息和坏的信息。而这正是Straitford赚钱的门道。</p><p>44.It can be learned from Paragraph 4 that ________.</p><p>&nbsp; 从第四段中可以得知 &nbsp; &nbsp; 。</p><p>[A] Straitford’s prediction about Ukraine has proved true</p><p>Straitford公司对于乌克兰的预测被证明是正确的</p><p>[B] Straitford guarantees the truthfulness of its information</p><p>Straitford公司保证其提供信息的正确性</p><p>[C] Straitford’s business is characterized by unpredictability</p><p>Straitford公司业务的特点是不可预测性</p><p>[D] Straitford is able to provide fairly reliable information</p><p>Straitford公司能提供相对可靠的信息</p><p><span>题型识别：</span>段落推理题</p><p><span>题干定位：</span>对应第四段</p><p><span>思路解析：</span>解题方式就是四个选项和原文相关处逐一比对，需要知道的是段落推理题答案往往和论点有关，而论点往往在段落的开头和结尾处，同时也可能与转折处有关。</p><p><span>选项分析：</span><span>[A]</span> Straitford’s prediction about Ukraine has proved true和原文中对于Ukraine的表述相关，但是动词的时态错误，并不是已经证明预测正确。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <span> [B]</span> Straitford guarantees the truthfulness of its information和原文最后二句相关，但是动词错误，guarantee在文中并未提及。</p><p><span> &nbsp; [C] </span>Straitford’s business is characterized by unpredictability和原文倒数第二句相关但是主语错误，不确定的不是Straitford的业务而是Open-source spying（公开资源的情报活动）。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <span> &nbsp;[D] </span>Straitford is able to provide fairly reliable information是对于原文最后一句的同义替换，That’s where Straitford earns its keep. 前面说到将好的信息和坏的信息分开很难，后面又说到，这就是Straitford能挣钱的地方，就说明Straitford 是可以提供相对可靠地信息的。</p><p><span>这才是解析应该有的态度！</span></p><p><br/></p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Fri, 01 Jul 2016 15:10:17 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>航班取消，继续发题</title>\n" +
                "<link>http://www.iwgc.cn/link/1742561</link>\n" +
                "<description>\n" +
                "<p><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=130&amp;play_length=07:21\" name=\"130\" play_length=\"441000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTQy\"></mpvoice></p><p>国航1819航班，一上飞机我就睡着了，不知道过了多久，空乘把我叫醒，我还挺开心，这么快。就到了。</p><p>“先生不好意思，航班取消了。”</p><p>这种事情不应该只有蒋老师才会遇到的吗？</p><p>一群人堵在国航的柜台前，各种愤怒，然后我在这里发句子。</p><p>先把句子搞定再说其他的吧：）</p><p><span>这个题目是经典中的经典:</span><br/></p><p>这就是文章的第四段：</p><p>Straitford president George Friedman says he sees the online world as a kind of mutually reinforcing tool for both information collection and distribution, a spymaster’s dream. Last week his firm was busy vacuuming up data bits from the far corners of the world and predicting a crisis in Ukraine. “As soon as that report runs, we’ll suddenly get 500 new Internet sign-ups from Ukraine,” says Friedman, a former political science professor. “And we’ll hear back from some of them.” Open-source spying does have its risks, of course, since it can be difficult to tell good information from bad. That’s where Straitford earns its keep.</p><p><br/></p><p>44.<span>\t</span>It can be learned from Paragraph 4 that ________.</p><p>[A] Straitford’s prediction about Ukraine has proved true</p><p>[B] Straitford guarantees the truthfulness of its information</p><p>[C] Straitford’s business is characterized by unpredictability</p><p>[D] Straitford is able to provide fairly reliable information</p><p><span>昨天句子的解析：</span></p><p>In America /the science of biotic control has its obscure beginnings/ a century ago/ with the first attempts to introduce natural enemies of insects/ that were proving troublesome to farmers, /an effort /that sometimes moved slowly or not at all, /but now and again gathered speed and momentum/ under the impetus of an outstanding success.</p><p>当句子比较长的时候，就一定是切分后独立成句了，至于各部分叫什么，也真的不重要了。</p><p>1.In America /the science of biotic control has its obscure beginnings/ a century ago/</p><p>在美国，一个世纪前，生物控制学就已经隐约出现了。</p><p>2. （1）with the first attempts to introduce natural enemies of insects/</p><p>&nbsp; &nbsp;（2）that were proving troublesome to farmers,</p><p>&nbsp; &nbsp; &nbsp; &nbsp; 翻译的时候调整语序为21；</p><p>&nbsp; &nbsp; &nbsp; （ 当时，） 一些昆虫给农民带来了麻烦，（有人）首次试图引入这些昆虫 &nbsp; &nbsp;的天敌，</p><p>3. &nbsp;an effort /that sometimes moved slowly or not at all, /but now and again gathered speed and momentum/ under the impetus of an outstanding success（状语）.</p><p>这种情况下：an effort 常常不用翻译；因为后面的that= effort ,翻译一次就够了。</p><p>该项工作有时进展缓慢，有时停滞不前，但有时在一项突出的成就的促使下，它又加速发展，突飞猛进。</p><p>同学们感觉到切分的乐趣了吧，一切解千愁啊！</p><p>参考译文：在美国，一个世纪前，生物控制学就已经隐约出现了。当时，一些昆虫给农民带来了麻烦，有人首次试图引入这些昆虫的天敌。这项工作有时进展缓慢，有时停滞不前，但有时在一项突出成就的促使下，它又加速发展，突飞猛进。</p><p>如果感谢这样的解析看的不太懂的同学，可以先从历史消息看起。这个阶段大家应该有一定的自主学习能力的。</p><p>我的解析也是对大家的一种鞭策！</p><p><img src=\"http://img02.iwgc.cn/getimg.php?url=http://mmbiz.qpic.cn/mmbiz/8IHrq7Veh7uOmIdA6ibtnQ1nIBsQEXeFu8C8SobY1EUic5D4ia1kjUjI84fibTL1GWYIrfVfvMicb4iaK7RcWuQAQSWA/0?wx_fmt=jpeg\"/><br/></p><p><br/></p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Thu, 30 Jun 2016 14:40:43 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>一听可乐，录课结束，高铁回京，再出彩蛋！</title>\n" +
                "<link>http://www.iwgc.cn/link/1728558</link>\n" +
                "<description>\n" +
                "<p><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=129&amp;play_length=10:24\" name=\"129\" play_length=\"624000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTM3\"></mpvoice></p><p>上课间隙，曲阜文都的老师买来几百听可乐发给每位同学，和我一起举杯共祝考研成功，这是我第一次听到几百听可乐同时打开的声音，像庆祝胜利的爆竹，在我的课程中我也录制进去了对大家的祝福。有时候幸福的感觉很简单，就是一听可乐。感谢同学们三天的陪伴！也期待暑期同学听到我送给大家的祝福！</p><p><br/></p><p><span>今天的句子是非常经典的翻译句子同学试着翻译一下:</span></p><p>In America the science of biotic control has its obscure beginnings a century ago with the first attempts to introduce natural enemies of insects that were proving troublesome to farmers, an effort that sometimes moved slowly or not at all, but now and again gathered speed and momentum under the impetus of an&nbsp;</p><p>outstanding success.</p><p><span>词汇突破:</span> the science of biotic control 生物控制学</p><p>obscure 隐隐约约的</p><p>now and again 有时</p><p>momentum 力量</p><p>impetus 促进</p><p><span>昨天题目的解析：</span></p><p><br/></p><p><span>48.According to the text, what is beyond man’s ability now is to design a robot that can ________.</span></p><p>设计一个能做_____的机器人是超越了现有人类的能力的？</p><p>=现在人们还设计不出一个能做_____的机器人</p><p>=现在的机器人做不了什么！</p><p>（这样的题干也是醉了啊！但是都是套路啊！）</p><p>1.And thanks to the continual miniaturization of electronics and micro-mechanics, there are already robot systems that can perform some kinds of brain and bone surgery with submillimeter accuracy -- far greater precision than highly skilled physicians can achieve with their hands alone.</p><p>这个句子是长难句书中讲过的：由于微电子和微机械的发展，现在的机器人做手术的精度比熟练的外科医生要高。（在考场上理解成这样就可以了！不用做精确的翻译！）</p><p>因为不是线索句！题干问的是：机器人做不了！所以不是线索句！</p><p>可以根据这句话排除<span>A fulfill delicate tasks like performing brain surgery（</span>这是现在能做的!）</p><p>2.But if robots are to reach the next stage of laborsaving utility, they will have to operate with less human supervision and be able to make at least a few decisions for themselves -- goals that pose a real challenge.</p><p>2.但是如果机器人想达到下一阶段的应用就必须：1.更少被人类监管。2.能至少自己做一些决定。（这些是真正有挑战的目标！）也就是现在机器人做不到的事情!是线索句！但是很遗憾没有和其相匹配的选项。</p><p>&nbsp;“While we know how to tell a robot to handle a specific error,” says Dave Lavery, manager of a robotics program at NASA, “we can’t yet give a robot enough ‘common sense’ to reliably interact with a dynamic world.”</p><p>3.虽然我们知道如何告知机器人处理一个特定的错误。NASA的一个经理说到。</p><p>&nbsp;但是我们不能给机器人足够的常识来和变化得世界可靠的互动！</p><p>我们不能给机器人足够的与世界互动的常识不等于</p><p>我们不能设计出有一点常识的机器人。<span>[C] have a little common sense</span></p><p>所以C选项不对！排除！</p><p>’ to reliably interact with a dynamic world.”</p><p><span>=[D] respond independently to a changing world</span></p><p>完美替换所以为正确答案！</p><p><span>[B] interact with human beings verbally</span> (文中没有说能做不能做，不过前文已经谈到了能做。)</p><p><span><br/></span></p><p><img src=\"http://img02.iwgc.cn/getimg.php?url=http://mmbiz.qpic.cn/mmbiz/8IHrq7Veh7uhiaIaGPEuAUwjVtDKicuLctLkdDCxyiaQ53xkwpl6yqvSdkZLxfMgeH4lPz9e7XbxcFxPMic3noB2Vg/0?wx_fmt=jpeg\"/></p><p><img src=\"http://img02.iwgc.cn/getimg.php?url=http://mmbiz.qpic.cn/mmbiz/8IHrq7Veh7uhiaIaGPEuAUwjVtDKicuLctbLhY2tJry4ax1r3mrDZttibCwEeDT4FoF5oKvD233S2Qlb9uvRQqbMw/0?wx_fmt=jpeg\"/></p><p><img src=\"http://img02.iwgc.cn/getimg.php?url=http://mmbiz.qpic.cn/mmbiz/8IHrq7Veh7uhiaIaGPEuAUwjVtDKicuLctuBLcM9aeq9icVxHJibSribG8mdF4TOCwfS9TgYUcggTfaTsIyWpxibUm5Q/0?wx_fmt=jpeg\"/></p><p><img src=\"http://img02.iwgc.cn/getimg.php?url=http://mmbiz.qpic.cn/mmbiz/8IHrq7Veh7uhiaIaGPEuAUwjVtDKicuLctl3FtlUwpDic7NI7tohJpsSOhGiav7ibYreJxKaHWqk1DYRDBp61tVOkHg/0?wx_fmt=jpeg\"/></p><p><br/></p><p><span><br/></span></p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Wed, 29 Jun 2016 14:15:59 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>2017考研阅读绝对热点预测：人工智能</title>\n" +
                "<link>http://www.iwgc.cn/link/1716465</link>\n" +
                "<description>\n" +
                "<p><span></span></p><p><span><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=128&amp;play_length=06:41\" name=\"128\" play_length=\"401000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTI4\"></mpvoice><p><p><span>阅读热点预测来了，特别关注！（录音有彩蛋）</span></p><p>这周的各大媒体都推出了人工智能的专刊，下面有来自两家媒体的报道：</p><p>第一家：</p><p>After many false dawns, AI has made extraordinary progress in the past few years, thanks to a versatile technique called “deep learning”. &nbsp;Stephen Hawking, Elon Musk and others wonder whether AI could get out of control, precipitating a sci-fi conflict between people and machines. &nbsp;Others worry that AI will cause widespread unemployment, by automat- ing cognitive tasks that could previously be done only by people.&nbsp;</p><p>第二家：</p><p>After many false starts, artificial intelligence has taken off. Will it cause mass unemployment or even destroy mankind? History can provide some helpful clues, says Tom Standage.</p><p>同学们可以发现这里的</p><p>dawn=start; &nbsp;</p><p>has taken off=has made extraordinary progress;</p><p>widespread unemployment=mass unemployment</p><p>a sci-fi conflict between people and machines=destroy mankind</p><p>语言不一样，但是内容就是人们在担心人工智能会不会带来失业问题和安全问题。</p><p>早在15年前考研英语就涉及到人工智能话题了，只不过那个时候涉及到的是下面的话题。</p><p><span>今天的题目：</span></p><p>And thanks to the continual miniaturization of electronics and micro-mechanics, there are already robot systems that can perform some kinds of brain and bone surgery with submillimeter accuracy -- far greater precision than highly skilled physicians can achieve with their hands alone.But if robots are to reach the next stage of laborsaving utility, they will have to operate with less human supervision and be able to make at least a few decisions for themselves -- goals that pose a real challenge. “While we know how to tell a robot to handle a specific error,” says Dave Lavery, manager of a robotics program at NASA, “we can’t yet give a robot enough ‘common sense’ to reliably interact with a dynamic world.”</p><p>48.According to the text, what is beyond man’s ability now is to design a robot that can ________.</p><p>[A] fulfill delicate tasks like performing brain surgery</p><p>[B] interact with human beings verbally</p><p>[C] have a little common sense</p><p>[D] respond independently to a changing world</p><p><iframe scrolling=\"no\" frameborder=\"0\" class=\"vote_iframe js_editor_vote_card\" data-display-style=\"height: 199px;\" data-display-src=\"/cgi-bin/readtemplate?t=vote/vote-new_tmpl&amp;__biz=MjM5NTEzMjY4MQ==&amp;supervoteid=4091182&amp;token=1755925324&amp;lang=zh_CN\" data-src=\"/mp/newappmsgvote?action=show&amp;__biz=MjM5NTEzMjY4MQ==&amp;supervoteid=4091182#wechat_redirect\" data-supervoteid=\"4091182\" allowfullscreen=\"\"></iframe><span></span><span></span></span></p><p>从现实穿越到过去，从当下的期刊中找到热点，再回看到当年的真题，这样的用心值得你的赞赏哦！</p><p><span>昨天句子的解析：</span></p><p>One of the biggest shifts under way is to phase out the “fee for service” model, in which hospitals and doctors’ surgeries are reimbursed for each test or treatment with no regard for the outcome, encouraging them to put patients through unnecessary and expensive procedures.&nbsp;</p><p><span>词汇突破：</span>1.phase something out: 逐步或分阶段撤出或终止某事物</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2. reimburse 补偿，偿还，补贴</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3. introduce 制定</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4. under way 正在进行的&nbsp;</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 5. with no regard for 无论</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 6. surgery 外科手术</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 7. procedure 治疗手段</p><p><span>确定主干：</span>One of the biggest shifts under way is to phase out the “fee for service” model&nbsp;</p><p><span>切分成分：</span>in which hospitals and doctors’ surgeries are reimbursed /for each test or treatment/ with no regard for the &nbsp;outcome 定语从句（独立成句）</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 在这个模式中，医院和医生所实施外科手术，无论结果怎样，都可以从每一次测试或治疗中获得回报，</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; encouraging them to put patients through unnecessary and expensive procedures&nbsp;</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 定语（独立成句）</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 发出动作的就是前面的“整个句子”=这；</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 这就鼓励他们让病人接受一些不必要的和昂贵的治疗。</p><p><span>参考译文：</span>正在进行的一个最大的变化是逐步的停止“费用换服务”的模式，在这 个模式中，医院和医生所实施外科手术，无论结果怎样，都可以从每一次测试或治疗中获得回报，这就鼓励他们让病人接受一些不必要的和昂贵的治疗。</p><p>（我想起来了，我想唱的是值得！）录音有彩蛋！</p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Tue, 28 Jun 2016 14:38:00 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>非常有特色的医疗改革的句子</title>\n" +
                "<link>http://www.iwgc.cn/link/1704629</link>\n" +
                "<description>\n" +
                "<p><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=127&amp;play_length=05:30\" name=\"127\" play_length=\"330000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTIz\"></mpvoice></p><p>今天一天阅读的强化课程，曲阜的同学们很配合，很投入！</p><p>期待大家能早点看到！</p><p>站了一天，有点累了。</p><p>还是得挺住了，明天继续！凯文加油！</p><p><span>都在说特朗普和希拉里到底会谁当选，但是今年的热点一定会有对奥巴马政绩的盘点；虽然奥巴马也没什么政绩，但是其医疗改革还算是有一定结果的，褒贬不一，其中就有这样的句子:</span></p><p><span>今天的句子：</span></p><p>One of the biggest shifts under way is to phase out the “fee for service” model, in which hospitals and doctors’ surgeries are reimbursed for each test or treatment with no regard for the outcome, encouraging them to put patients through&nbsp;</p><p>unnecessary and expensive procedures.&nbsp;</p><p>词汇突破：1.phase something out: 逐步或分阶段撤出或终止某事物</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2. reimburse 补偿，偿还，补贴</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3. introduce 制定</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4. under way 正在进行的&nbsp;</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 5. with no regard for 无论</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 6. surgery 外科手术</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 7. procedure 治疗手段</p><p><span>昨天句子的解析：</span><span><br/></span></p><p>But this time round a slump would be unlikely to lead to a broader contagion, since it would be confined to private markets and a few large firms with strong&nbsp;</p><p>balance-sheets.</p><p><span>词汇突破：</span></p><p>1. this time round 这一次（时间状语）</p><p>2. slump 经济衰退</p><p>3. contagion 影响</p><p>4. be confined to 局限于</p><p><span> &nbsp; 例句：</span></p><p><span>The significance of hot spots is not confined to their role as a frame of reference.</span></p><p><span>热点的重要性不仅只是局限于其参照框架的作用。（考研真题例句）</span></p><p>5. strong balance-sheets 优良的资产负债表</p><p><span>确定主干：</span>a slump would be unlikely to lead to a broader contagion</p><p><span>切分成分：</span>since引导的原因状语从句since it would be confined to private marke &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;ts and a few large firms with strong balance- &nbsp;sheets构成。It指代slump，</p><p><span>参考译文：</span>但这一次，经济衰退可能不会有大范围的影响，因为它将只局限于私人市场和拥有漂亮资产负债表的几个大公司</p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Mon, 27 Jun 2016 15:17:54 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>动车夜行，忘了充电，所以晚了，各位见谅</title>\n" +
                "<link>http://www.iwgc.cn/link/1692869</link>\n" +
                "<description>\n" +
                "<p><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=126&amp;play_length=06:30\" name=\"126\" play_length=\"390000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTE4\"></mpvoice></p><p>愿意不愿意，暑假班都这样就来了。兴奋同样会疲惫；</p><p>生活就是这样，总会给我们很多未知，而未知就会让人恐惧。我们只能选择勇敢一点：</p><p>Be Brave: When something scares you, don&#39;t forget: everyone else is just as&nbsp;</p><p>scared as you are.</p><p>勇敢一点：当你害怕的时候，不要忘了，其他人也跟你一样害怕。</p><p>（和大家一起共勉！）</p><p><span>今天的句子：</span></p><p>经济学的句子，句子不复杂，单词很棒！</p><p><span>But this time round a slump would be unlikely to lead to a broader contagion,&nbsp;</span></p><p><span>since it would be confined to private markets and a few large firms with strong&nbsp;</span></p><p><span>balance-sheets.</span></p><p><span>昨天句子的解析：</span></p><p>A changed relationship between the schools and the schooled is manifested in students’ interactions with colleges even before they enroll, as those institutions, intent on increasing the number of applications they receive and on snagging as many top ones, class presidents and soccer captains as they can, come at them as merchants, clamoring for their attention, competing for their affection and unfurling their wares with as much ceremony and gloss as possible.</p><p>词汇突破：</p><p>1. snagging students 招收学生</p><p>2. clamor for 要求，吸引= claim</p><p>3. competing for 博取</p><p>4. unfurl= demonstrate 展现</p><p>5. ceremony and gloss 形式</p><p>6. wares 货品</p><p>7. intent on= in order to&nbsp;</p><p>8. the schooled 学生=students</p><p>9. enroll 入学</p><p>10. come at them 来到他们面前</p><p>确定主干: A changed relationship is manifested in students’ interactions with&nbsp;</p><p>colleges.</p><p>其他成分+独立成句</p><p>1.even before they enroll 在他们入学之前</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2. as those institutions, intent on increasing the number of applications they receive and on snagging as many top ones, class presidents and soccer captains as they can, come at them as merchants, clamoring for their attention, competing for their affection and unfurling their wares with as much ceremony and gloss as possible.</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 状语从句</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 主干：Those institutions come at them as merchants</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 状语：1. intent on increasing the number of applications they receive</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;为了增加本校收到的入学申请数量</p><p>2.（intent） on snagging as many top ones, class presidents and soccer captains as they can,</p><p>&nbsp; 并尽可能招揽到更多的最佳毕业生、年级长和足球队长，</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3. clamoring for their attention,</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 引起他们的注意</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4. competing for their affection</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 博取他们的好感</p><p>5. unfurling their wares with as much ceremony and gloss as possible</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;使出浑身解数展示自己的‘货品’。</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;（这里的ceremony和gloss用的非常到位！）</p><p>参考译文：这种转变甚至在学生入学前与学校的互动中就有所体现。为了增加本校收到的入学申请数量，并尽可能招揽到更多的最佳毕业生、年级长和足球队长，那些学校像商贩一样来到学生面前，想要引起他们的注意，博取他们的好感，并使出浑身解数展示自己的‘货品’。</p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Sun, 26 Jun 2016 15:29:51 +0000</pubDate>\n" +
                "</item>\n" +
                "<item>\n" +
                "<title>找个好的句子不容易，要好好的珍惜：美国大学改革</title>\n" +
                "<link>http://www.iwgc.cn/link/1683787</link>\n" +
                "<description>\n" +
                "<p><mpvoice frameborder=\"0\" class=\"res_iframe js_editor_audio audio_iframe\" src=\"/cgi-bin/readtemplate?t=tmpl/audio_tmpl&amp;name=125&amp;play_length=04:38\" name=\"125\" play_length=\"278000\" voice_encode_fileid=\"MjM5NTEzMjY4MV8yNjUxMDI0MTEz\"></mpvoice></p><p>找到一个好的句子不容易，要好好的珍惜！</p><p>美国大学正通过创新吸引并服务于新一代学子，与此同时，学校和学生之间的关系发生了变化。这是过去四分之一世纪里，美国高等教育领域最为惊人的转变。最近NYT有这样的报道：<br/><span>今天的句子：</span></p><p>A changed relationship between the schools and the schooled is manifested in&nbsp;</p><p>students’ interactions with colleges even before they enroll, as those institutions,&nbsp;</p><p>intent on increasing the number of applications they receive and on snagging as many top ones, class presidents and soccer captains as they can, come at them&nbsp;</p><p>as&nbsp;<span>merchants, clamoring for their attention, competing for their affection and&nbsp;</span></p><p>unfurling their wares with as much ceremony and gloss as possible.</p><p>词汇突破：</p><p>1. snagging students 招收学生</p><p>2. clamor for 要求，吸引= claim</p><p>3. competing for 博取</p><p>4. unfurl= demonstrate 展现</p><p>5. ceremony and gloss 形式</p><p>6. wares 货品。</p><p><span>昨天句子的解析：</span></p><p>The Conservatives’ main move has been to back away from their own reforms, passed in 2012, which increased competition, gave health officials more autonomy and handed control over the purchase of care to groups of local doctors.</p><p><span>词汇突破：</span>1.Conservatives 保守党</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 2.Move 举动，行动</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 3.Autonomy 自主权</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 4.Control （名词）控制权 Control over A 对于A的控制权</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 5.purchase &nbsp;v.或者n. 购买</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 6. Hand A to B 将A交给B</p><p><span>主干识别：</span>The Conservatives’ main move has been to back away from their&nbsp;</p><p>own reforms</p><p><span>切分成分+独立成句：</span></p><p>passed in 2012 定语</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; The reform increased competition, gave health officials more autonomy and handed control over the purchase of care to groups of local doctors. 定语</p><p>(which=reform)</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; Reform发出了三个动作：</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; increased competition</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; gave health officials more autonomy</p><p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; handed control over the purchase of care to groups of local doctors.</p><p><span>参考译文：</span>保守党的主要举动是退出自己2012年通过的改革，这次改革增加了竞争，给卫生官员更多的自主权，并将购买医疗服务的控制权交给了本地的医生团体。</p><p><br/></p>\n" +
                "</description>\n" +
                "<pubDate>Sat, 25 Jun 2016 14:34:29 +0000</pubDate>\n" +
                "</item>\n" +
                "</channel>\n" +
                "</rss>";
        Document document = Jsoup.parse(s);
//            Document document = Jsoup.connect(Urls.ENGLISH_URL).get();
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
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return list;
    }

}
