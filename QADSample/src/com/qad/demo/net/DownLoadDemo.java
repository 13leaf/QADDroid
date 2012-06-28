package com.qad.demo.net;

import java.io.File;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qad.annotation.InjectView;
import com.qad.app.BaseActivity;
import com.qad.demo.R.id;
import com.qad.demo.R.layout;
import com.qad.inject.ViewInjector;
import com.qad.service.DownloadReceiver;
import com.qad.service.DownloadService;

public class DownLoadDemo extends BaseActivity {
	String[] downUrls=new String[]{
			"http://202.107.35.126:8011/main_setup.exe",
			"http://xiazai.xiazaiba.com/Soft/9/7z_9.25.00_XiaZaiBa.exe"
	};
	String[] targetPaths=new String[]{
			"waga.exe",
			"7z.exe"
	};
	
	private LinearLayout root;
	
	class TrashDownloadReceiver extends DownloadReceiver
	{
		@InjectView(id=id.name)
		TextView name;
		@InjectView(id=id.progress)
		ProgressBar progressBar;
		@InjectView(id=id.progress_text)
		TextView progressText;
		
		@Override
		protected void onNewDownload(String downloadUrl, String targetPath,
				Context context) {
			LinearLayout downItem=(LinearLayout) getLayoutInflater().inflate(layout.download_item, null);
			ViewInjector.inject(downItem, this);
			
			root.addView(downItem);
			name.setText(targetPath);
		}
		
		@Override
		protected void onPublishProgress(int progress, Context context) {
			progressBar.setProgress(progress);
			progressText.setText(progress+"");
		}
		
		@Override
		protected void onDownloadDone(boolean success, Context context) {
			showMessage("download "+success);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		root=new LinearLayout(this);
		root.setOrientation(LinearLayout.VERTICAL);
		setContentView(root);
		registerManagedReceiver(new TrashDownloadReceiver());
		for(int i=0;i<downUrls.length;i++)
		{
			File targetFile=new File(Environment.getExternalStorageDirectory(),targetPaths[i]);
			DownloadService.start(this, downUrls[i], targetFile.getAbsolutePath(), null);
		}
	}
}
