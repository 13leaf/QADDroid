package com.qad.form;


import android.app.Activity;

/**
 * 处理POJO与AdapterView(列表)的映射关系
 * 本POJO类假定你要映射的是AdapterView列表。
 * 若你的映射是单一编辑情况，请使用POJOTextFiller
 * <h3>已废除，待用时再修改</h3>
// * TODO 若View不是列表样式，而是RadioGroup的形式。那么该如何解决？
 * @see POJOTextFiller
 * @author Administrator
 * @deprecated 使用{@link com.qad.render.RenderEngine}替代
 */
public abstract class AdapterPOJOFiller extends POJOFiller{

	public AdapterPOJOFiller(Activity context) {
		super(context);
	}
	/*private Activity activity;
	
	private Map<String, ArrayList<Object>> relations; 
	
	*//**
	 * 
	 * @param context
	 * @param relations 该属性表示值与选择列表的关系。Key为POJO的属性值<br>
	 * 					Value为该POJO属性值对应的可选列表。
	 *//*
	public POJOAdapterFiller(Activity context,Map<String, ArrayList<Object>> relations)
	{
		this.activity=context;
		this.relations=relations;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String getViewValue(String field, int id) {
		View view=activity.findViewById(id);
		if(view instanceof AdapterView)
		{
			AdapterView<Adapter> adapterView=(AdapterView<Adapter>)view;
			return relations.get(field).get(adapterView.getSelectedItemPosition()).toString();
		}else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void setValue2View(String field, String value, int id) {
		ArrayList<Object> a=relations.get(field);
		if(a==null) return;//找不到对应字段
		else {
			int position=relations.get(field).indexOf(value);
			View view=activity.findViewById(id);
			if(view instanceof AdapterView)
			{
				AdapterView<Adapter> adapterView=(AdapterView<Adapter>)view;
				adapterView.setSelection(position);
			}
		}
	}
	
	*//**
	 * 将POJO的属性填充到一个列表View.
	 * @param pojo 要填充的POJO
	 * @param prefix 填充目标AdapterView的命名前缀
	 * @param attris 要填充的pojo属性
	 * @param RId 资源id定义文件
	 *//*
	public void fillAdapterPOJO(Object pojo,String prefix,Class<?> RId,String... attris)
	{
		
	}
	
	*//**
	 * 
	 * @param <classOfT>
	 * @param classOfT pojo类型
	 * @param prefix 获得adpter view的前缀
	 * @param RId	资源id定义文件
	 * @param pojo	要获得的pojo实例
	 * @param attris 要获得的pojo属性
	 * @return
	 *//*
	public <classOfT> classOfT getPOJO(Class<?> classOfT, String prefix,
			Class<?> RId,Object pojo,String... attris) 
	{
		return null;
	}	*/
}
