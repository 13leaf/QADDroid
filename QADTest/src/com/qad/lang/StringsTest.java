package com.qad.lang;

import junit.framework.TestCase;

public class StringsTest extends TestCase{

	public void testAppendUrlParam(){
		String testParam="xx=xx&yy=yy";
		//only baseUrl
		String testUrl1="http://www.baidu.com";
		assertEquals("http://www.baidu.com?xx=xx&yy=yy", Strings.appendUrlParam(testUrl1, testParam));
		
		//baseUrl with param
		String testUrl2="http://www.baidu.com?zz=zz";
		assertEquals("http://www.baidu.com?zz=zz&xx=xx&yy=yy",Strings.appendUrlParam(testUrl2, testParam));
		
		//baseUrl with fragment
		String testUrl3="http://www.baidu.com#abc";
		assertEquals("http://www.baidu.com?xx=xx&yy=yy#abc", Strings.appendUrlParam(testUrl3, testParam));
		
		//baseUrl with param and fragment
		String testUrl4="http://www.baidu.com?zz=zz#abc";
		assertEquals("http://www.baidu.com?zz=zz&xx=xx&yy=yy#abc", Strings.appendUrlParam(testUrl4, testParam));
		
		//invalid param
		assertEquals("http://www.baidu.com", Strings.appendUrlParam("http://www.baidu.com", null));
		
	}
}
