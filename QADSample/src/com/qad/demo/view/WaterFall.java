package com.qad.demo.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

import com.qad.app.BaseActivity;
import com.qad.form.PageEntity;
import com.qad.form.PageLoader;
import com.qad.form.PageManager;
import com.qad.lang.Files;
import com.qad.loader.BeanLoader;
import com.qad.util.IntentFactory;
import com.qad.view.FallEntry;
import com.qad.view.PictureFall;
import com.qad.view.onFallClikedListener;

class MyFallPageEntity implements PageEntity
{
	private final String[] urls = {
			"http://img.youa.com/img_new/c12f5eacf9d2f04e358b79b8--208-500-11",
			"http://img.youa.com/img_new/56945cf313ce2c785d26706d--208-500-11",
			"http://img.youa.com/img_new/d34dab090048533b25af097a--208-500-11",
			"http://img.youa.com/img_new/ea2e09b20d3f6054cee42a4b--208-500-11",
			"http://img.youa.com/img_new/54b9982102558aec75fb890b--144-108-12",
			"http://img.youa.com/img_new/56938468bee6d7995332e0f8--144-108-12",
			"http://img.youa.com/img_new/7cf40f37525c9ec8a563a508--208-500-11",
			"http://img.youa.com/img_new/c364a304497e23088c3fd8c0--208-500-11",
			"http://img.youa.com/img_new/5d96b6c1b69ff2100e1f5dc6--208-500-11",
			"http://img.youa.com/img_new/4300a2b98b2614e80f397ef1--144-108-12",
			"http://img.youa.com/img_new/4e72cfff5979647c85248636--144-108-12",
			"http://img.youa.com/img_new/d951342a440c9a133658d360--208-500-11",
			"http://img.youa.com/img_new/092e8150dd9eb47d00352060--144-108-12",
			"http://img.youa.com/img_new/8f1ab730ddf817538c0d72f9--144-108-12",
			"http://img.youa.com/img_new/612c371ecdc25a62400cbc6d--208-500-11",
			"http://img.youa.com/img_new/1adeac581c7c80db150a7614--208-500-11",
			"http://img.youa.com/img_new/5f3ad12ee11d7b12160a76e8--208-500-11",
			"http://img.youa.com/img_new/40b94cc7523dffd4ea13c403--208-500-11",
			"http://img.youa.com/img_new/e1cd8e29f9514056e9cc8d5a--208-500-11",
			"http://img.youa.com/img_new/0cdc6cef65b5f96a1a380409--208-500-11",
			"http://img.youa.com/img_new/7dd99317050cf8f180cb4d4f--208-500-11",
			"http://img.youa.com/img_new/631c40ef5466a2e20ae1996f--208-500-11",
			"http://img.youa.com/img_new/305336a3de1cd72293ffb69b--208-500-11",
			"http://img.youa.com/img_new/37a4f83f2989d63b77fb89bb--208-500-11",
			"http://img.youa.com/img_new/819bcd947b41edb8e32c4922--144-108-12",
			"http://img.youa.com/img_new/ba3582476bdd1f9d87f42334--144-108-12",
			"http://img.youa.com/img_new/cf1ebef3ac5e0c20b4eb064a--208-500-11",
			"http://img.youa.com/img_new/97e45ea0b5ee7858f9361944--208-500-11",
			"http://img.youa.com/img_new/0b60f1da8eb8e20c19e0d5fc--144-108-12",
			"http://img.youa.com/img_new/7612f89c81b944f3725f5cbd--144-108-12",
			"http://img.youa.com/img_new/cfd1632db23ff9552d233d63--208-500-11",
			"http://img.youa.com/img_new/1a24e40329bba7393fe83f2f--144-108-12",
			"http://img.youa.com/img_new/1ab121167ca194f096c5c8ed--144-108-12",
			"http://img.youa.com/img_new/351c1612b9daa4da874c2df9--208-500-11",
			"http://img.youa.com/img_new/4207420543d53c712ff71c06--208-500-11",
			"http://img.youa.com/img_new/d640670937bef781f436ebbe--208-500-11",
			"http://img.youa.com/img_new/0301e37f9cfc2512992982a4--208-500-11",
			"http://img.youa.com/img_new/dda21362b23ba36d5d23aa24--208-500-11",
			"http://img.youa.com/img_new/c5667442f31adc7a874c2d73--208-500-11",
			"http://img.youa.com/img_new/759a3b21c7e38d55feeb6912--208-500-11",
			"http://img.youa.com/img_new/addd9aea9b8892a10f397eb6--208-500-11",
			"http://img.youa.com/img_new/bc7cb5b97a3940a80319b3c8--144-108-12",
			"http://img.youa.com/img_new/c95b50e95e6785fb8ed15889--144-108-12",
			"http://img.youa.com/img_new/14d36ee813e3c38d1ae0d572--208-500-11",
			"http://img.youa.com/img_new/5dde3062ae4a0ed305239e0c--208-500-11",
			"http://img.youa.com/img_new/dbd0ea49428096d2da23424b--208-500-11",
			"http://img.youa.com/img_new/ca6343002068d1a00c47fad8--208-500-11",
			"http://img.youa.com/img_new/a3f0589dbdca3818246c55fc--208-500-11",
			"http://img.youa.com/img_new/b674eb657e4dd537b96ae102--208-500-11",
			"http://img.youa.com/img_new/eee0052298eb6b6339221d1f--208-500-11",
			"http://img.youa.com/img_new/38ef21d488bcdaa4f5eac308--144-108-12",
			"http://img.youa.com/img_new/c451ac38a7020e4604ed241f--144-108-12",
			"http://img.youa.com/img_new/3ded9ef996a89784b47d395a--208-500-11",
			"http://img.youa.com/img_new/96d833fe0d22cebc660136dd--144-108-12",
			"http://img.youa.com/img_new/ce047c0b715204b811d8f78f--144-108-12",
			"http://img.youa.com/img_new/6d9773800c69b437b77d399a--208-500-11",
			"http://img.youa.com/img_new/0023a9e42abacf0565013662--208-500-11",
			"http://img.youa.com/img_new/d0203a4de62308198d3fd884--208-500-11",
			"http://img.youa.com/img_new/286a9c6e2df315149bdce8c7--208-500-11",
			"http://img.youa.com/img_new/3372b71f069bddcfafd4d29e--208-500-11",
			"http://img.youa.com/img_new/b3b1b5e7e6938e86440eb470--208-500-11",
			"http://img.youa.com/img_new/6ddd608f55dc63cf303690f1--208-500-11",
			"http://img.youa.com/img_new/c1e2ade9921b358dd02f2245--144-108-12",
			"http://img.youa.com/img_new/e870854f6c9281df0c47fa6d--144-108-12",
			"http://img.youa.com/img_new/fcf972188b0b0b2713d64c0d--208-500-11",
			"http://img.youa.com/img_new/a877ed4b4b3fca0af8030f37--144-108-12",
			"http://img.youa.com/img_new/5db175f2cdb2430d94ea50e7--144-108-12",
			"http://img.youa.com/img_new/9ab0b7578574dcd38d1f6cd4--208-500-11",
			"http://img.youa.com/img_new/65e31b2f7d17704f05ed2494--208-500-11",
			"http://img.youa.com/img_new/a8cdd765472aaf3335d8ab3e--208-500-11",
			"http://img.youa.com/img_new/50edebc031e05d30058c5320--144-108-12",
			"http://img.youa.com/img_new/2d23b1a8ec66346bd8170af0--144-108-12",
			"http://img.youa.com/img_new/5d5c295325d384142d233db6--208-500-11",
			"http://img.youa.com/img_new/d556492d62afd3b8bcd16d60--144-108-12",
			"http://img.youa.com/img_new/11e20b0c92382b938531a963--144-108-12",
			"http://img.youa.com/img_new/ed367d1af788e3634decc628--208-500-11",
			"http://img.youa.com/img_new/df88a40a42305fadb96ae1d4--208-500-11",
			"http://img.youa.com/img_new/09c2d7b30bc118f1ce2b6a1b--208-500-11",
			"http://img.youa.com/img_new/79d537cc792c196cb77d398c--208-500-11",
			"http://img.youa.com/img_new/3b240367aa9e338b410cbc4b--208-500-11",
			"http://img.youa.com/img_new/731fd33728c3d20a9829bd86--208-500-11",
			"http://img.youa.com/img_new/d68c0b8a3b3dde2b611fac36--208-500-11",
			"http://img.youa.com/img_new/00d0bddc12db0eb892b8a36f--208-500-11",
			"http://img.youa.com/img_new/78102795450cac66c2091345--208-500-11" };
	private final int[] widths={
			208,
			208,
			208,
			208,
			144,
			144,
			208,
			208,
			208,
			144,
			144,
			208,
			144,
			144,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			144,
			144,
			208,
			208,
			144,
			144,
			208,
			144,
			144,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			144,
			144,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			144,
			144,
			208,
			144,
			144,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			144,
			144,
			208,
			144,
			144,
			208,
			208,
			208,
			144,
			144,
			208,
			144,
			144,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			208,
			208
	};
	private final int[] heights={
			500,
			500,
			500,
			500,
			108,
			108,
			500,
			500,
			500,
			108,
			108,
			500,
			108,
			108,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			108,
			108,
			500,
			500,
			108,
			108,
			500,
			108,
			108,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			108,
			108,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			108,
			108,
			500,
			108,
			108,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			108,
			108,
			500,
			108,
			108,
			500,
			500,
			500,
			108,
			108,
			500,
			108,
			108,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			500,
			500
	};
	
	@Override
	public int getPageSum() {
		return 5;
	}

	@Override
	public List<?> getData() {
		ArrayList<FallEntry> initial=new ArrayList<FallEntry>();
		for(int i=0;i<urls.length;i++){
			FallEntry entry=new FallEntry(null, urls[i], widths[i], heights[i]);
			initial.add(entry);
		}
		return initial;
	}
	
}

public class WaterFall extends BaseActivity implements PageLoader<ArrayList<FallEntry>>{


	PictureFall customView;
	Handler handler=new Handler();
	File cacheFolder = new File(
			Environment.getExternalStorageDirectory(), "waterfall2");
	BeanLoader<Object> loader=new BeanLoader<Object>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customView=new PictureFall(this);
		customView.setBackgroundColor(Color.argb(0xff, 0xf2, 0xf5, 0xf8));
		customView.setOnFallClickedListener(new onFallClikedListener() {
			@Override
			public void onFallClicked(FallEntry selectedEntry) {
				startActivity(IntentFactory.getBrowser(Uri.parse(selectedEntry.url)));
			}
		});
		customView.setCacheFolder(cacheFolder);
		customView.bindPageManager(getPager());
		setContentView(customView);
	}
	
	@Override
	public boolean loadPage(final int pageNo, int pageSize) {
		showMessage("start Loading");
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				MyFallPageEntity myFallPageEntity=new MyFallPageEntity();
				getPager().notifyPageLoad(LOAD_COMPLETE, pageNo,myFallPageEntity.getPageSum(), (ArrayList<FallEntry>) myFallPageEntity.getData());
				showMessage("load "+pageNo);
			}
		}, 1*1000);
		return false;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		customView.destroy();//release resources
		loader.destroy(true);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 1, "setNumColumn");
		menu.add(0,2,2,"setItemPadding");
		menu.add(0,3,3,"setSelectedColor");
		menu.add(0,4,4,"clear cache");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			customView.setNumColumn(4);
			break;
		case 2:
			customView.setXpadding(20);
			customView.setYpadding(20);
			break;
		case 3:
			customView.setSelectedFilterColor(Color.argb(128, 128, 128, 255));
			break;
		case 4:
			Files.deleteDir(cacheFolder);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	PageManager<ArrayList<FallEntry>> pager=new PageManager<ArrayList<FallEntry>>(this,20);
	@Override
	public PageManager<ArrayList<FallEntry>> getPager() {
		return pager;
	}
}
