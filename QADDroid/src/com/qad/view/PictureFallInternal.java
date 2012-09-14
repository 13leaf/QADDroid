package com.qad.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.qad.form.PageLoadAdapter;
import com.qad.form.PageManager;
import com.qad.form.PageManager.PageLoadListener;
import com.qad.loader.LoadContext;
import com.qad.loader.LoadListener;
import com.qad.loader.QueueLoader;
import com.qad.loader.service.LoadServices;
import com.qad.util.WLog;

public class PictureFallInternal extends View {
	// properties
	private int xpadding = 10;
	private int ypadding = 10;
	private int numColumn = 3;
	private float borderWidth = 2;

	// entry layout
	private int regularWidth = 0;
	private ArrayList<ArrayList<FallEntry>> entries;
	private FallEntry selectedEntry;
	private int[] score = new int[numColumn];// 每一列的深度评分，不代表实际高度

	/**
	 * 当前的滚动窗口并不是实际滚动区域，而是一个大于区域的逻辑。该值与缓存策略有关
	 */
	private int logicalWindowSize = 30;
	private LinkedList<FallEntry> outEntries = new LinkedList<FallEntry>();

	private int mHeight;
	private int parentHeight;
	private int scrollY;

	private QueueLoader<String, FallEntry, Bitmap>[] loaders;
	private File cacheFolder = new File(
			Environment.getExternalStorageDirectory(), "waterfall");

	private onFallClikedListener clickListener;

	private Bitmap defaultFall;
	private Bitmap errorFall;

	private PageManager<ArrayList<FallEntry>> pageManager;
	private String loadErrorMsg = "加载失败...";

	private WLog logger = WLog.getMyLogger(PictureFall.class);

	public PictureFallInternal(Context context) {
		super(context);
		init();
	}

	public PictureFallInternal(Context context, AttributeSet set) {
		super(context, set);
		init();
	}

	public PictureFallInternal(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		defaultPaint = new Paint();
		defaultPaint.setColor(Color.WHITE);
		defaultPaint.setStyle(Style.FILL);

		borderPaint = new Paint();
		borderPaint.setARGB(0xff, 0xc0, 0xc0, 0xc0);// 0xffc0c0c0
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(borderWidth);

		selectedPaint = new Paint();
		selectedPaint.setStyle(Style.FILL);
		selectedPaint.setColorFilter(new PorterDuffColorFilter(Color.argb(0, 0,
				0, 0), Mode.SRC_ATOP));// transparent default

		xpadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				xpadding, getResources().getDisplayMetrics());
		ypadding = xpadding;
		borderWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				borderWidth, getResources().getDisplayMetrics());

	}

	public void setOnFallClickedListener(onFallClikedListener listener) {
		this.clickListener = listener;
	}

	public void setCacheFolder(File cacheFolder) {
		if (cacheFolder != null)
			this.cacheFolder = cacheFolder;
	}

	public void setLoadErrorMsg(String loadErrorMsg) {
		if (loadErrorMsg != null)
			this.loadErrorMsg = loadErrorMsg;
	}

	public String getLoadErrorMsg() {
		return loadErrorMsg;
	}

	private PageLoadListener pageListener = new PageLoadAdapter() {
		@SuppressWarnings("unchecked")
		public void onPageLoadComplete(int loadPageNo, int pageSum,
				Object content) {
			addEntries((ArrayList<FallEntry>) content);
		}

		public void onPageLoadFail(int loadPageNo, int pageSum) {
			Toast.makeText(getContext(), loadErrorMsg, Toast.LENGTH_SHORT)
					.show();
		}
	};

	public void bindPageManager(PageManager<ArrayList<FallEntry>> manager) {
		if (pageManager == manager)
			return;
		this.pageManager = manager;
		// reset
		entries = null;
		if (this.pageManager != null) {
			pageManager.addOnPageLoadListioner(pageListener);
			pageManager.next();
		}
	}

	public PageManager<ArrayList<FallEntry>> getPageManager() {
		return pageManager;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		scrollY = t;
		// recycle out window
		if (entries == null)
			return;
		outEntries = getOutWindowEntryList(t);
		for (FallEntry fallEntry : outEntries) {
			if (fallEntry.bitmap != null)
				fallEntry.bitmap.recycle();
			fallEntry.bitmap = null;
		}
		int fix = 20;
		if (pageManager.getLoadState() != PageManager.LOADING
				&& !pageManager.isLast() && t + parentHeight + fix > mHeight) {
			pageManager.next();
		}
		logger.testLog("recycle " + outEntries.size());
	}

	private LoadListener listener = new LoadListener() {
		@Override
		public void loadFail(LoadContext<?, ?, ?> context) {
			FallEntry entry = (FallEntry) context.getTarget();
			if (!entry.loadError) {
				entry.loadError = true;
				invalidate();
			}
		}

		@Override
		public void loadComplete(LoadContext<?, ?, ?> context) {
			FallEntry entry = (FallEntry) context.getTarget();
			Bitmap result = (Bitmap) context.getResult();
			if (outEntries != null && outEntries.contains(entry)) {
				logger.testLog("ignore entry " + entry.top + "," + scrollY
						+ "-" + (scrollY + parentHeight));
				return;
			}
			if (result != null) {
				entry.bitmap = result;
				invalidate();
			}
		}
	};

	/**
	 * 释放所有重要资源，主要是线程和图片
	 */
	public void destroy() {
		// recyle res
		if (defaultFall != null)
			defaultFall.recycle();
		defaultFall = null;
		if (errorFall != null)
			errorFall.recycle();
		errorFall = null;

		if (loaders != null) {
			for (int i = 0; i < loaders.length; i++) {
				loaders[i].removeListener(listener);
				loaders[i].destroy(true);
			}
		}
		clickListener = null;
		listener = null;
		loaders = null;

		removeAllEntries();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		ViewGroup parent = (ViewGroup) getParent();
		parentHeight = parent.getHeight();
		mHeight = h;
		// reset loader
		if (loaders != null) {
			for (int i = 0; i < loaders.length; i++)
				loaders[i].destroy(false);
		}
		loaders = new QueueLoader[numColumn];
		for (int i = 0; i < numColumn; i++) {
			QueueLoader<String, FallEntry, Bitmap> loader = new QueueLoader<String, FallEntry, Bitmap>(
					LoadServices.newHttpImage2Cache(cacheFolder, regularWidth,
							getContext()));
			loader.addListener(listener);
			loaders[i] = loader;
		}
	}

	Paint defaultPaint;
	Paint borderPaint;
	Paint selectedPaint;

	// for recycle use
	Rect bmpBound = new Rect();
	Rect entryBound = new Rect();

	@Override
	protected void onDraw(Canvas canvas) {
		if (entries == null)
			return;
		for (int col = 0; col < entries.size(); col++) {
			canvas.save();
			canvas.translate(regularWidth * col + xpadding * (col + 1),
					ypadding);
			for (FallEntry fallEntry : entries.get(col)) {
				entryBound.set(0, 0, fallEntry.width, fallEntry.height);
				canvas.drawRect(entryBound, borderPaint);
				if (fallEntry.holdBitmap()) {
					bmpBound.set(0, 0, fallEntry.bitmap.getWidth(),
							fallEntry.bitmap.getHeight());
					entryBound.left += borderWidth / 2;
					entryBound.top += borderWidth / 2;
					entryBound.right -= borderWidth / 2;
					entryBound.bottom -= borderWidth / 2;
					canvas.drawBitmap(fallEntry.bitmap, bmpBound, entryBound,
							fallEntry.isSelected ? selectedPaint : null);
				} else if (fallEntry.loadError) {
					if (errorFall != null) {
						bmpBound.set(0, 0, errorFall.getWidth(),
								errorFall.getHeight());
						canvas.drawBitmap(errorFall, bmpBound, entryBound, null);
					}
				} else {
					if (visibleAtWindow(fallEntry) && loaders != null)
						loaders[col].startLoading(fallEntry.url, fallEntry);
					if (defaultFall == null) {
						canvas.drawRect(entryBound, defaultPaint);
					} else {
						bmpBound.set(0, 0, defaultFall.getWidth(),
								defaultFall.getHeight());
						canvas.drawBitmap(defaultFall, bmpBound, entryBound,
								null);
					}
				}
				onDrawEntry(canvas, fallEntry);
				canvas.translate(0, fallEntry.height + ypadding);
			}
			canvas.restore();
		}
	}

	/**
	 * 子类可以实现此处来实现进一步的元素绘制
	 * 
	 * @param canvas
	 * @param fallEntry
	 */
	protected void onDrawEntry(Canvas canvas, FallEntry fallEntry) {
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (entries == null)
			return super.onTouchEvent(event);
		float x = event.getX(), y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// select entry
			selectedEntry = findSelectEntry((int) x, (int) y);
			if (selectedEntry != null) {
				selectedEntry.isSelected = true;
				invalidate();
			}
			return false;
		case MotionEvent.ACTION_UP:
			if (selectedEntry != null && selectedEntry.isSelected
					&& clickListener != null) {
				selectedEntry.isSelected = false;
				clickListener.onFallClicked(selectedEntry);
			}
			break;
		default:
			if (selectedEntry != null)
				selectedEntry.isSelected = false;
			break;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredWidth = getDefaultSize(
				getResources().getDisplayMetrics().widthPixels,
				widthMeasureSpec);
		regularWidth = (measuredWidth - (numColumn + 1) * xpadding) / numColumn;
		ensureEntries();
		int measuredHeight = calcMaxHeight();
		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	public void setDefaultFall(Bitmap defaultFall) {
		this.defaultFall = defaultFall;
	}

	public void setErrorFall(Bitmap errorFall) {
		this.errorFall = errorFall;
	}

	public Bitmap getDefaultFall() {
		return defaultFall;
	}

	public Bitmap getErrorFall() {
		return errorFall;
	}

	public ArrayList<ArrayList<FallEntry>> getEntries() {
		return entries;
	}

	public void removeAllEntries() {
		if (entries != null) {
			for (ArrayList<FallEntry> col : entries) {
				for (FallEntry fallEntry : col) {
					if (fallEntry.holdBitmap())
						fallEntry.bitmap.recycle();
					fallEntry.bitmap = null;
				}
			}
			entries.clear();
			entries = null;
		}
		requestLayout();
	}

	public void addEntries(ArrayList<FallEntry> all) {
		resizeEntries(all);
		if (entries == null) {
			entries = alignment(all, numColumn, score);
		} else {
			merge(entries, alignment(all, numColumn, score));
		}
		requestLayout();
	}

	private void ensureEntries() {
		if (getVisibility() != View.VISIBLE || regularWidth == 0
				|| entries == null)
			return;
		for (ArrayList<FallEntry> col:entries) {
			resizeEntries(col);
		}
	}

	private void resizeEntries(ArrayList<FallEntry> myEntries) {
		int mTop = ypadding;
		for (FallEntry entry : myEntries) {
			entry.scale(regularWidth);
			entry.top = mTop;
			mTop += entry.height + ypadding;
		}
	}

	//
	public FallEntry getEntry(int column, int pos) {
		return entries.get(column).get(pos);
	}

	public void setSelectedFilterColor(int filterColor) {
		selectedPaint.setColorFilter(new PorterDuffColorFilter(filterColor,
				Mode.SRC_ATOP));
	}

	public void setBorderWidth(float borderWidth) {
		this.borderWidth = borderWidth;
		requestLayout();
	}

	public void setXpadding(int xpadding) {
		this.xpadding = xpadding;
		requestLayout();
	}

	public void setYpadding(int ypadding) {
		this.ypadding = ypadding;
		requestLayout();
	}

	public void setNumColumn(int numColumn) {
		if (numColumn <= 0)
			throw new IllegalArgumentException("setNumColumn " + numColumn);
		this.numColumn = numColumn;
		this.score = new int[numColumn];
		ArrayList<FallEntry> all = new ArrayList<FallEntry>();
		for (ArrayList<FallEntry> col : entries) {
			all.addAll(col);
		}
		entries = null;
		addEntries(all);
		requestLayout();// remeasure
	}

	public int getXpadding() {
		return xpadding;
	}

	public int getYpadding() {
		return ypadding;
	}

	public int getNumColumn() {
		return numColumn;
	}

	/**
	 * 该entry是否包含在显示窗口
	 * 
	 * @return
	 */
	private boolean visibleAtWindow(FallEntry entry) {
		return (entry.top >= scrollY && entry.top <= scrollY + parentHeight)
				|| (entry.top + entry.height >= scrollY && entry.top
						+ entry.height <= scrollY + parentHeight);
	}

	/**
	 * 搜索并查找得到在对应坐标上的元素
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private FallEntry findSelectEntry(int x, int y) {
		int selectCol = (int) (x / (regularWidth + xpadding));
		FallEntry testEntry = new FallEntry(null, "", 1, 1);
		testEntry.top = y;
		int selectRow = Collections.binarySearch(entries.get(selectCol),
				testEntry);
		if (selectRow < 0) {
			selectRow = -selectRow - 2;// 应该是插入点的前面一个
		}
		if (selectRow >= 0 && selectCol >= 0
				&& // 上界
				selectCol < numColumn
				&& selectRow < entries.get(selectCol).size()) {// 下界
			return getEntry(selectCol, selectRow);
		} else {
			return null;
		}
	}

	/**
	 * 获得在当前滚动窗口区域之外的所有Fall元素,详见logicalWindowSize
	 * 
	 * @param v
	 *            纵向滚动偏移值
	 * @return
	 */
	private LinkedList<FallEntry> getOutWindowEntryList(int v) {
		if (v <= 0)
			v = 1;
		FallEntry targetEntry = new FallEntry(null, "", 1, 1);
		targetEntry.top = v;
		int[] initialIndex = new int[numColumn];
		for (int i = 0; i < initialIndex.length; i++) {
			int targetIndex = Collections.binarySearch(entries.get(i),
					targetEntry);
			if (targetIndex < 0)
				targetIndex = -targetIndex - 1;
			initialIndex[i] = targetIndex;
		}

		int maxEntrySize = Integer.MIN_VALUE;
		int calcedCount = numColumn;
		for (int i = 0; i < entries.size(); i++) {
			if (maxEntrySize < entries.get(i).size())
				maxEntrySize = entries.get(i).size();
		}

		int step = 1;
		int[][] bound = new int[numColumn][2];// 每列的上下界
		for (int i = 0; i < numColumn; i++) {
			bound[i][0] = 0;
			bound[i][1] = entries.get(i).size() - 1;
		}

		while (calcedCount < logicalWindowSize && step <= maxEntrySize) {
			for (int i = 0; i < numColumn; i++) {
				int upIndex = initialIndex[i] + step;
				int downIndex = initialIndex[i] - step;
				if (downIndex >= 0) {
					bound[i][0] = downIndex;
					calcedCount++;
				}
				if (upIndex < entries.get(i).size()) {
					bound[i][1] = upIndex;
					calcedCount++;
				}
			}
			step++;
		}

		LinkedList<FallEntry> out = new LinkedList<FallEntry>();
		for (int i = 0; i < numColumn; i++) {
			for (int j = 0; j < entries.get(i).size(); j++) {
				if (j < bound[i][0] || j > bound[i][1])
					out.add(entries.get(i).get(j));
			}
		}
		return out;
	}

	/**
	 * 前置条件 arr不为空,fix.length=nCol 结果:根据贪婪算法放置生成flow列表
	 * 
	 * @param arr
	 *            全局数组大小
	 * @param nCol
	 *            列数
	 * @param fix
	 *            修复值
	 * @param paddingY
	 *            纵向padding值
	 */
	private ArrayList<ArrayList<FallEntry>> alignment(ArrayList<FallEntry> arr,
			int nCol, int fix[]) {
		if (nCol <= 0 || fix.length != nCol) {
			throw new IllegalArgumentException(String.format(
					"Invalid argument nCol %s ,fix %s", nCol,
					Arrays.toString(fix)));
		}
		ArrayList<LinkedList<FallEntry>> splitArray = new ArrayList<LinkedList<FallEntry>>();
		// init
		for (int i = 0; i < nCol; i++)
			splitArray.add(new LinkedList<FallEntry>());
		for (int i = 0; i < arr.size(); i++) {
			int selectCol = minColIndex(fix);
			splitArray.get(selectCol).add(arr.get(i));
			fix[selectCol] += arr.get(i).height + ypadding;
		}
		// convert
		ArrayList<ArrayList<FallEntry>> out = new ArrayList<ArrayList<FallEntry>>();
		for (LinkedList<FallEntry> colList : splitArray) {
			out.add(new ArrayList<FallEntry>(colList));
		}
		return out;
	}

	/**
	 * 
	 * @return
	 */
	private int calcMaxHeight() {
		if (entries == null)
			return getSuggestedMinimumHeight();
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < numColumn; i++) {
			int colHeight = 0;
			for (FallEntry entry : entries.get(i)) {
				colHeight += entry.height + ypadding;
			}
			if (max < colHeight)
				max = colHeight;
		}
		return max;
	}

	private int minColIndex(int[] fix) {
		int min = Integer.MAX_VALUE;
		int minIndex = 0;
		for (int i = 0; i < fix.length; i++) {
			if (min > fix[i]) {
				min = fix[i];
				minIndex = i;
			}
		}
		return minIndex;
	}

	/**
	 * 合并append到origin中
	 * 
	 * @param origin
	 * @param append
	 */
	private void merge(ArrayList<ArrayList<FallEntry>> origin,
			ArrayList<ArrayList<FallEntry>> append) {
		if (origin.size() != append.size()) {
			throw new IllegalArgumentException(
					"Origin's col size unequal append's col size "
							+ origin.size() + "," + append.size());
		}
		for (int i = 0; i < origin.size(); i++) {
			origin.get(i).addAll(append.get(i));
		}
	}
}
