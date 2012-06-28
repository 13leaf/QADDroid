package com.qad.util;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

/**
 * 实现一个简易的MediaPlayerControl。将大多数方法实现完成。<br>
 * 注意:该实现将覆写MediaPlayer的onBufferedListener。若在SimpleMediaPlayerControl后重新注册，会覆写<br>
 * 默认的getBufferPercentage实现。
 * @author 13leaf
 */
public class SimpleMediaPlayerControl implements MediaPlayerControl {

	private int bufferedPercent;//播放进度
	
	private MediaPlayer player;
	
	private MediaController controller;
	
	private static final int timeOut=2500;//播放控制器显示、关闭的延迟时间。默认设置为1.5秒的时间
	
	/**
	 * 构造一个实现与MediaPlayer绑定的简单MediaControl。该实例可以与MediaController.setMediaPlayer()构造绑定。<br>
	 * 本类还有一个实用的静态方法attachMediaController()，该方法用SimpleMediaControl作为桥梁，构建一个与之绑定的MediaController对象<br>
	 * 并且该MediaController与指定的contextView绑定在一起。当点击contextView的时候，会显示或隐藏controller。
	 * @param player
	 */
	public SimpleMediaPlayerControl(MediaPlayer player)
	{
		this.player=player;
	}
	
	/**
	 * 构造一个简单的播放控制器，使用的是android built-in的MediaController。<br>
	 * 构造出来的MediaController可以通过getMediaController访问到。<br>
	 * {@link SimpleMediaPlayerControl}会对contextView的touch,click事件注册一个默认侦听器，用于处理controller的开关显示。<br>
	 * 
	 * @param player 构造的关联播放器
	 * @param contextView 上下文View，构造完成后播放控制器将显示在contextView的下面位置。
	 */
	SimpleMediaPlayerControl(MediaPlayer player,View contextView)
	{
		this.player=player;
		
		player.setOnBufferingUpdateListener(new BufferedObserver());
		controller=new MediaController(contextView.getContext(),true);
		controller.setMediaPlayer(this);
		//make that contextView be a built view
		if(contextView!=null){
			contextView.setOnTouchListener(new TouchToggler());
			controller.setAnchorView(contextView);
		}
	}
	
	/**
	 * 构造一个简单的播放控制器，并将其贴到关联的上下文contextView中。返回的是android built-in的MediaController。<br>
	 * {@link SimpleMediaPlayerControl}会对contextView的touch,click事件注册一个默认侦听器，用于处理controller的开关显示。<br>
	 * 注:为了避免意外，构造出来的MediaController处于禁止状态，当载入完成时请手动调用MediaController.setEnabled()方法来启用。
	 * @param player 构造的关联播放器
	 * @param contextView 上下文View，构造完成后播放控制器将显示在contextView的下面位置。
	 * @return 构造完成的MediaController
	 */
	public static MediaController attachMediaController(MediaPlayer player,View contextView)
	{
		MediaController controller= new SimpleMediaPlayerControl(player, contextView).getMediaController();
		controller.setEnabled(false);
		return controller;
	}
	
	final class BufferedObserver implements OnBufferingUpdateListener{
		
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			bufferedPercent=percent;
		}
	}
	
	final class TouchToggler implements OnTouchListener{
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			toggleController();
			return false;
		}
		
	}
	
	/**
	 * 只有MediaPlayer进入播放状态后才允许进行toggle操作。
	 */
	public void toggleController()
	{
		if(isPlaying()&&controller.isShowing()){
			controller.hide();
		}else {
			controller.show(timeOut);
		}
	}
	
	/**
	 * 获得构造出的MediaController
	 * @return
	 */
	public MediaController getMediaController() {
		return controller;
	}
	
	@Override
	public void start() {
		//uncheck..may be exception
		player.start();
	}

	@Override
	public void pause() {
		//uncheck..may be exception
		player.pause();
	}

	@Override
	public int getDuration() {
		return player.getDuration();
	}

	@Override
	public int getCurrentPosition() {
		return player.getCurrentPosition();
	}

	@Override
	public void seekTo(int pos) {
		player.seekTo(pos);
	}

	@Override
	public boolean isPlaying() {
		return player.isPlaying();
	}

	@Override
	public int getBufferPercentage() {
		return bufferedPercent;
	}

	//compact for api level5
	public boolean canPause() {
		return true;
	}

	public boolean canSeekBackward() {
		return true;
	}

	public boolean canSeekForward() {
		return true;
	}

}
