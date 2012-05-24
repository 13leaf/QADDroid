package com.qad.form;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.qad.annotation.FillAble;
import com.qad.annotation.FillPojo;
import com.qad.annotation.FillType;
import com.qad.util.ViewTool;

/**
 * <h3>POJOFiller是一个POJO与View之间互相映射的注入工具。</h3>
 * <p>
 * PojoFiller的原理是这样的:<ul>
 * <li>首先,PojoFiller假定每个Pojo对象的某一个字段会与一个View的某个属性发生联系。</li>
 * <li>接着,PojoFiller会通过某个规则根据Pojo中字段的命名来查找相同id名称的View。如Pojo中有个String name的属性，那么与它映射的
 * <code>&lt;View android:id="@+id/name"/&gt;</code></li>
 * <li>另外，针对业务的情况。有时View的id命名会在Pojo字段名的基础上增加一些前缀或者后缀。那么在构造函数中你可以设置映射的统一前后缀。前后缀
 * 与字段名称之间会用_隔开。如某字段名为name,命名时前缀是relate,后缀是1。则该View的id全称为relate_name_1</li>
 * <li>当确立了View和字段之间的关联之后。还必须做一件事情，那就是确定字段与View属性的关系。这一点可以通过注解 {@link FillAble}或 {@link POJOFiller}来实现</li>
 * <li><strong>需要注意的是,PojoFiller对View的id名称关联要求十分严格。必须十分小心对待</strong></li>
 * </ul>
 * 目前{@link POJOFiller}的实现类包括 {@link PurePojoFiller}和 {@link AdapterPOJOFiller}两种。PurePojoFiller是指单纯的一对一属性映射，如setText(pojoAttr)<br>
 * 而AdapterPojoFiller则是针对一个Pojo属性对于列表/数组中的某一项的操作。类似setSelect(pojoAttri)和pojoAttr=getSelect()这种操作。
 * </p>
 * @deprecated 使用{@link com.qad.render.RenderEngine}替代
 * @see AdapterPOJOFiller
 * @see PurePojoFiller
 */
public abstract class POJOFiller {

	private static final Class<?>[] emptyClassParams=new Class[]{};
	private static final Object[] empyObjectParams=new Object[]{};
	
	private ViewTool pojoViewTool;
	
	private Activity activity;//若是使用Activity的构造函数,则其DecorView可能会发生改变。因此必须在每次get,fill时都重新初始化pojoViewTool
	/**
	 * 映射前缀名称
	 */
	private String prefix;
	
	/**
	 * 映射后缀名称
	 */
	private String postfix;
	
	/**
	 * 是否映射关系中使用了组合名称
	 */
	private boolean isCombineName;
	
	/**
	 * 遵循UnixCase命名规则。组合名称时会在prefix和postfix添加本属性
	 */
	public static final String NAME_SPLIT="_";
	
	
	/**
	 * 将POJOFiller的观察对象设置成为特定的View,并对其进行填充或获取操作。<br>
	 * 你可以通过调用setGroupView来实时更换POJO的填充目标组。
	 * @param groupView 填充的View目标组。
	 * @param prefix 前缀,若设置为null或空，则忽略。命名使用Unix规则， 请见{@link POJOFiller#NAME_SPLIT}
	 * @param postfix 后缀，若设置为null或空，则忽略。
	 */
	public POJOFiller(View groupView,String prefix,String postfix)
	{
		isCombineName=
			(prefix!=null)||(postfix!=null);
		this.prefix=prefix;
		this.postfix=postfix;
		pojoViewTool=new ViewTool(groupView);
	}
	
	/**
	 * 使用Activity重载函数构造的POJOFiller会观察Activity的当前View，并对其进行填充或获取操作。<br>
	 * 若调用setGroupView()方法将会使POJOFiller的观察对象从Activity转到特定View上。并且之后的填充操作都会根据特定View来，等同于groupView的构造函数<br>
	 * @param context
	 * @param prefix
	 * @param postfix
	 */
	public POJOFiller(Activity context,String prefix,String postfix)
	{
		this(context.getWindow().getDecorView(),prefix,postfix);
		activity=context;
	}
	
	public POJOFiller(View groupView)
	{
		this(groupView,null,null);
	}
	
	public POJOFiller(Activity context)
	{
		this(context.getWindow().getDecorView());
		activity=context;
	}
	
	public View getGroupView()
	{
		return pojoViewTool.getView();
	}
	
	public void setGroupView(View view)
	{
		if(view==null) throw new NullPointerException("view can't be null!");
		activity=null;//invalidate activity mode
		
		pojoViewTool.setDecorView(view);
	}
		
	
	/**
	 * 从表单中获得设置后的POJO。若转换出错，则将抛出异常<br>
	 * 目前尚不支持从ImageView反射回到Bitmap。图像仅仅提供Drawable的支持
	 * @param classOfT POJO的类型
	 * @param prefix 控件字段id的前缀
	 * @param RId 资源ID定义文件
	 * @param pojo POJO实例。调用此方法后将会覆盖冲突属性。若pojo为空，此方法会动态创建新实例。
	 * @throws Exception 
	 * */
	@SuppressWarnings("unchecked")
	public <classOfT> classOfT getPOJO(Class<?> pojoType, Object pojo) throws CastException {

		if(activity!=null)
			pojoViewTool.setDecorView(activity.getWindow().getDecorView());
		// 通过反射实例化一个对象
		try {
			if(pojo==null) 
				pojo=pojoType.getConstructors()[0].newInstance(empyObjectParams);//反射实例化
		} catch (Exception e) {
			//ignore
		}
		
		boolean all=detectAll(pojoType);
		Field[] pojoFields=pojoType.getDeclaredFields();
		for (Field field : pojoFields) {
			FillType fillType=detectFillType(all, field);
			if(fillType!=null && fillType!=FillType.none)
			{
				String viewName=getViewName(field.getName());
				Object setValue=getViewValue(
						pojoViewTool.findViewByIdName(viewName), fillType,fillType==FillType.custom?field.getAnnotation(FillAble.class):null);
				
				if(!field.getType().isAssignableFrom(setValue.getClass())){
					//try to cast String
					setValue=stringCast(setValue.toString(), field.getType());
				}
				try {
					Method setMethod=pojoType.getDeclaredMethod(getSetterMethod(field), field.getType());
					setMethod.invoke(pojo, setValue);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (classOfT) pojo;
	}
	
	public <classOfT> classOfT getPOJO(Class<?> classofT) throws CastException{
		return getPOJO(classofT,null);
	}

	/**
	 * 将指定的字符串转化为class类型。若遇到了primitiveType或者wrapperType,则会自动转化。
	 * @param <classOfT>
	 * @param s
	 * @param classOfT
	 * @return
	 * @throws Exception 转换出错
	 */
	@SuppressWarnings("unchecked")
	public static <classOfT> classOfT stringCast(String s,Class<?> classOfT) throws CastException
	{
		Object castValue;
		if(!String.class.equals(classOfT)){
			if(s==null || s.length()==0 || s.equals("null"))
				return null;
		}
		try{
		if(classOfT.equals(int.class) || classOfT.equals(Integer.class)){
			castValue=Integer.parseInt(s);
		}
		else if(classOfT.equals(short.class) || classOfT.equals(Short.class))
		{
			castValue=Short.parseShort(s);
		}
		else if(classOfT.equals(long.class) || classOfT.equals(Long.class))
		{
			castValue=Long.parseLong(s);
		}
		else if(classOfT.equals(byte.class) || classOfT.equals(Byte.class))
		{
			castValue=Byte.parseByte(s);
		}
		else if(classOfT.equals(float.class) || classOfT.equals(Float.class))
		{
			castValue=Float.parseFloat(s);
		}
		else if(classOfT.equals(double.class) || classOfT.equals(Double.class))
		{
			castValue=Double.parseDouble(s);
		}
		else if(classOfT.equals(char.class) || classOfT.equals(Character.class))
		{
			castValue=s.charAt(0);
		}
		else if(classOfT.equals(boolean.class) || classOfT.equals(Boolean.class))
		{
			castValue=Boolean.parseBoolean(s);
		}else {
			castValue=classOfT.cast(s);
		}
		}catch (Exception e) {
			throw  new CastException(e);
		}
		return (classOfT) castValue;
	}
	
	/**
	 * 通过已知的POJO类型填充表单，注意应在setContentView()之后调用本方法。
	 * @param POJO 具有getter,setter特征的类
	 * @param prefix context控件前缀，作为findViewById的依据
	 * @param RId 资源ID定义文件
	 * */
	public void fillPOJO(Object pojo)
	{
		//1.获得pojo的所有字段名称。
		//2.配合前缀得到pojo字段对应的id。
		//3.利用context.findViewById来找到对应的TextView(或者TextView的子类)控件。
		//4.若为null则跳过，否则设置TextView的text属性。
		if(activity!=null)
			pojoViewTool.setDecorView(activity.getWindow().getDecorView());
		
		
		Field[] fields=pojo.getClass().getDeclaredFields();//获得所有的字段
		Class<?> pojoType=pojo.getClass();
		
		boolean all=detectAll(pojoType);
		
		for (Field field : fields) {
		try{
			FillType fillType=detectFillType(all, field);
			Object getValue=
				pojoType.getMethod(getGetterMethod(field), emptyClassParams)
				.invoke(pojo, empyObjectParams);
			
			if(fillType!=null && fillType!=FillType.none){
				String viewName=getViewName(field.getName());
					setValue2View(
							getValue,
								pojoViewTool.findViewByIdName(viewName), 
									fillType);
			}
		}catch (Exception e) {
			//ignore
		}

		}
	}
	
	/**
	 * 根据pojo的字段名称来获取映射的View的名称。
	 * 若存在前后缀，则为字段名称添加前后缀.否则返回字段名称。
	 * @param fieldName
	 * @return
	 */
	private String getViewName(String fieldName)
	{
		if(isCombineName) {
			StringBuilder sb=new StringBuilder(fieldName);
			if(prefix!=null && prefix.length()!=0) sb.insert(0,prefix+NAME_SPLIT);
			if(postfix!=null && postfix.length()!=0) sb.append(NAME_SPLIT+postfix);
			return sb.toString();
		}else{
			return fieldName;
		}
	}
	
	/**
	 * 若all为true，并且没有设置FillAble，则认为是auto模式。
	 * 否则反射查看该字段有无FillAble注解，并据此获得FillType。
	 * 当FillType被认为auto时,会判断field的类型来完成映射。
	 * @param all
	 * @param field
	 * @return
	 */
	private FillType detectFillType(boolean all,Field field)
	{
		FillAble fillAble=field.getAnnotation(FillAble.class);
		if((fillAble==null && all)||(fillAble.type()==FillType.auto)) {
			Class<?> fieldType=field.getType();
			if(String.class==fieldType || 
					(fieldType==double.class || fieldType==Double.class || fieldType==Float.class || fieldType==float.class))
			{
				return FillType.text;
			}else if(Drawable.class.isAssignableFrom(fieldType) || Bitmap.class==fieldType){
				return FillType.image;
			}else if(boolean.class==fieldType || Boolean.class==fieldType)
			{
				return FillType.check;
			}else if(Number.class.isAssignableFrom(fieldType) || fieldType.isPrimitive())
			{
				return FillType.progress;
			}else {
				//若为其他普通的类型，则认为是text类型处理
				return FillType.text;
			}
		}else {
			return fillAble.type();
		}
	}
	
	/**
	 * 反射查看是否存在FillPojo注解。若不存在，则返回true。否则返回反射中的注解设置
	 * @param pojo
	 * @return
	 */
	private boolean detectAll(Class<?> pojoType)
	{
		FillPojo mFillPojo=pojoType.getAnnotation(FillPojo.class);
		if(mFillPojo==null) return false;
		else return mFillPojo.all();
	}

	/**
	 * 将头一个字母大写
	 * @param name
	 * @return
	 */
	protected String toWordCaseString(String name) {
		return Character.toUpperCase(name.charAt(0))+name.substring(1);
	}

	/**
	 * 根据字段获得setter方法的名称
	 * @param field
	 * @return
	 */
	protected String getSetterMethod(Field field) {
		if(field.getType()==boolean.class || field.getType()==Boolean.class)
		{
			if(field.getName().startsWith("is"))
			{
				return "set"+toWordCaseString(field.getName().substring(2));//舍弃开头的is
			} else {
				return "set"+toWordCaseString(field.getName());//按原名返回
			}
		}else {
			return "set"+toWordCaseString(field.getName());
		}
	}

	/**
	 * 根据字段获得getter方法的名称
	 * @param field
	 * @return
	 */
	protected String getGetterMethod(Field field) {
		if(field.getType()==boolean.class || field.getType()==Boolean.class)
		{
			if(field.getName().startsWith("is"))
			{
				return field.getName();//按原名返回
			} else {
				return "is"+toWordCaseString(field.getName());
			}
		}else {
			return "get"+toWordCaseString(field.getName());
		}
	}

	/**
	 * 将值设置到View中
	 * @param fieldValue pojo中要fill的字段值
	 * @param relateView 要fill的目标View
	 * @param type fill的类型
	 */
	protected abstract void setValue2View(Object fieldValue,View relateView,FillType type);
	
	/**
	 * 从View中获取值
	 * @param field 若POJO是1对多的映射关系，则此属性代表获取的POJO的字段名。
	 * 
	 * @return 若获取失败，则应返回null。
	 */
	protected abstract Object getViewValue(View relateView,FillType type,FillAble fillAble);
}
