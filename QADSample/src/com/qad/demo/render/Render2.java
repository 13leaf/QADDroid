package com.qad.demo.render;

import java.util.ArrayList;

import android.os.Bundle;

import com.qad.app.BaseListActivity;
import com.qad.demo.R.drawable;
import com.qad.demo.R.layout;
import com.qad.loader.ImageLoader;
import com.qad.loader.service.LoadServices;
import com.qad.render.RenderEngine;

public class Render2 extends BaseListActivity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<RenderEntitiy2> entities=buildEntities();
		RenderEngine.render(getListView(), layout.render2, entities,new ImageLoader(LoadServices.newHttpImageNoCache(me),getResources().getDrawable(drawable.icon)),null);

	}

	private ArrayList<RenderEntitiy2> buildEntities() {
		ArrayList<RenderEntitiy2> entities=new ArrayList<RenderEntitiy2>();
		entities.add(new RenderEntitiy2("", "德国14岁少年校园开枪射击 操场与警方对峙", "2012-05-23 11:16:00", "德国警方逮捕一名在校园内开枪射击的14岁少年。中新网5月23日电据外电报道，德国巴伐利亚州梅明根市一名14岁少年22日在校园内持枪射击，后遭到警方逮捕。据悉，该男孩当时在学校的操场内与警方对峙，还将枪指向自己威胁要自杀。报"));
		entities.add(new RenderEntitiy2("http://res01.mimg.ifeng.com/g?url=http://y1.ifengimg.com/news_spider/dci_2012/05/2752bf82d2c00a60b5a7f4349ae38ba5.jpg&w=100&h=-1&v=202f8241d0&r=1", "陈水扁戒护就医结束 医生：血块不是恶性肿瘤", "2012-05-23 15:03:00", "资料图：陈水扁。中新网5月23日电据台湾“中广新闻”报道，陈水扁今天(23日)前往林口长庚医院戒护就医，在中午诊疗结束，随后戒护返回台北监狱，受委托前往关心的台大医院柯文哲医生表示，有一些心血管阻塞的现象，不过还没有到临床上有生"));
		entities.add(new RenderEntitiy2("", "广东广电局副局长：广电总局从来没有发过限娱令", "2012-05-23 15:55:37", "羊城晚报讯记者刘玮宁、通讯员陈燕舒、实习生秦雪星报道：22日上午，记者从广东省广电局上线省政风行风热线节目“民声热线”中了解到，2011年，广东省广播电影电视局受理观众对在境外电视节目中违规插播广告的投诉有253件。对此，有关负责人表示"));
		entities.add(new RenderEntitiy2("http://res01.mimg.ifeng.com/g?url=http://y3.ifengimg.com/abaae711ba7f5202/2012/0523/rdn_4fbc928337db6.jpg&w=100&h=-1&v=8a6c495e66&r=1", "毛泽东送毛岸英赴朝细节：很高兴将酒一饮而尽", "2012-05-23 15:33:44", "核心提示：豪爽的彭德怀亲见毛岸英的参军热情、又见毛泽东希望岸英能参军入伍，遂答应了他们父子俩的请求。那天，毛泽东兴致极高，竟将杯中酒一饮而尽，并连喝数杯。毛泽东饮酒照资料图本文摘自人民网作者：佚名原题为：毛泽东：“喝酒"));
		entities.add(new RenderEntitiy2("http://res01.mimg.ifeng.com/g?url=http://y3.ifengimg.com/7b28ede2da65d6e2/2012/0523/ori_4fbc9c0a5dcfd.jpeg&w=100&h=-1&v=15d34ccc55&r=1", "专家：水中含雌激素不奇怪 没必要担心", "2012-05-23 16:14:48", "漫画/陈春鸣据新华社电日前，一条称“自来水中含有避孕药”的微博引起网民热议。相关领域专家告诉记者，所谓“避孕药”的说法实属噱头，其实准确的说法应该是水里检测出雌激素成分。据专家解释，在合格水质下，雌激素的含量比较微量，不会对"));
		entities.add(new RenderEntitiy2("", "长江流域进入汛期 海事部门严防船舶触礁搁浅", "2012-05-23 12:04:00", "宜昌港停泊囤船巳加长“跳船”应对水位上涨。　望作信　摄中新网宜昌5月23日电(望作信李麟)受近期长江流域连续强降雨和上游及各支流来水增多的双重影响，长江重庆至上海，各主要港口相继告别枯水位，长江干线结束长达七个月的枯水期，长江流"));
		entities.add(new RenderEntitiy2("http://res01.mimg.ifeng.com/g?url=http://y1.ifengimg.com/news_spider/dci_2012/05/df48952462d1797b0b45decef5a9c7fd.jpg&w=100&h=-1&v=557e2dbaa2&r=1", "英一硕士投简历逾万次未果 背广告牌推销自己", "2012-05-23 14:41:46", "英国硕士罗宾·诺顿背广告牌推销自己（网页截图）　　国际在线专稿：据英国《每日邮报》5月22日报道，英国一位硕士过去十年间投简历1.5万份，但却没有找到一份全职工作。他目前整天背着广告牌沿着公路行走，希望能够找到工作。这名硕士叫"));
		entities.add(new RenderEntitiy2("", "张弛：“是中国人就转”的话语困境", "2012-05-23 13:57:42", "网上总有一些话，能让人看后长吁短叹又忍俊不禁，比如出镜率极高的“是中国人的就转”，又或者是它的孪生兄弟“不转不是中国人”。每每看到这句话，我在未点击鼠标左键之后都要掏出身份证来确认一下，看看它背后的“中华人民共和国”会不会易名改姓。只是"));
		entities.add(new RenderEntitiy2("", "以色列防长称以仍未排除军事打击伊朗可能性", "2012-05-23 13:57:00", "中新网5月23日电据外媒报道，以色列国防部长巴拉克23日表示，伊朗与国际原子能机构达成的开放核设施的初步协议，并不能让以色列排除军事打击伊朗的可能。巴拉克说，他对伊朗与国际原子能机构达成的这一初步协议“持怀疑态度”，认为这一协议不过是伊"));
		entities.add(new RenderEntitiy2("http://res01.mimg.ifeng.com/g?url=http://y1.ifengimg.com/1b0c4ed4543066da/2012/0523/ori_4fbc5737d7df0.jpeg&w=100&h=-1&v=6a6263212f&r=1", "李克峰：那一年，我读列夫·托尔斯泰", "2012-05-23 11:20:33", "1983年，我是学生，托尔斯泰是书架上的藏书。当时，没有酒吧、歌舞厅和洗脚城。从军队或农村回京的青年，骑自行车到消息灵通人士家里泡着，谈“人家美国……”，那叫沙龙。大家可以拎着烧开水的大铝壶，到饭馆去打1块4毛钱1斤的散装啤酒喝，但"));
		entities.add(new RenderEntitiy2("", "印度一公交车冲进恒河致26人死亡 系超载失控", "2012-05-23 15:19:00", "中新网5月23日电据外媒报道，印度警方透露，一辆公交车当地时间22日晚间冲进恒河，造成26人死亡，另有4人受重伤。救援人员已经在河中工作超过4个小时搜寻失踪者以及遇难者遗体。当地警方透露，这辆公交车载有45人，在开往圣城里希盖什的路"));
		entities.add(new RenderEntitiy2("", "奥巴马称卡梅伦在北约峰会期间曾偷溜外出观光", "2012-05-23 13:05:58", "5月21日，美国总统奥巴马（前右）和英国首相卡梅伦（前左）出席北约峰会阿富汗会议(新华社)国际在线专稿：据英国《每日邮报》5月22日报道，美国总统奥巴马（BarackObama）日前曝光称，英国首相卡梅伦（DavidCamero"));
		entities.add(new RenderEntitiy2("http://res01.mimg.ifeng.com/g?url=http://y0.ifengimg.com/news_spider/dci_2012/05/8baed005fac3fc57d07354a5101d2a7f.jpg&w=100&h=-1&v=e702ebc3b4&r=1", "伦敦奥运门票即将开售 开幕式最高票价2012英镑", "2012-05-23 13:38:00", "伦敦奥运门票图样（图片来源：黑龙江日报）中广网北京5月22日消息据中国之声《全球华语广播网》报道，因为是在本土举办，英国人今年看奥运的热情空前高涨。不过，伦敦奥运会的门票现在是又贵又难买，看开幕式最贵门票合人民币2万多，要知道4年"));
		return entities;
	}
}
