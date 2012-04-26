package com.qad.loader;

/**
 * 载入上下文
 * @author 13leaf
 *
 * @param <Param>
 * @param <Target>
 * @param <Result>
 */
public class LoadContext<Param,Target,Result> {
	Param param;
	Target target;
	Result result;
	
	public LoadContext(Param param, Target target) {
		this.param = param;
		this.target = target;
		if(param==null || target==null)
			throw new NullPointerException("Param or Target can not be null!");
	}

	@Override
	public String toString() {
		return "LoadContext [param=" + param + ", target=" + target
				+ ", result=" + result + "]";
	}
	
	@Override
	public int hashCode() {
		return param.hashCode();
	}
	
	public Result getResult() {
		return result;
	}
	
	public Target getTarget() {
		return target;
	}
	
	public Param getParam() {
		return param;
	}
	
	public void setResult(Result result) {
		this.result = result;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o==null) return false;
		if(!(o instanceof LoadContext)) return false;
		@SuppressWarnings("rawtypes")
		LoadContext context=(LoadContext) o;
		return param.equals(context.param) && target.equals(context.target);
	}
	
}
