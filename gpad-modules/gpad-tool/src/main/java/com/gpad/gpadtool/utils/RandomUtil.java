package com.gpad.gpadtool.utils;

import java.util.Random;
/**
 *  微信小程序工具类
 * @Date: 2023/10/28
 * @Author: LF
 **/
public class RandomUtil {

	/**
	 * 字符数字混合随机码
	 * 
	 * @param length
	 * @return
	 */
	public static String generateRandom(int length) {
		StringBuffer sb = new StringBuffer("a,b,c,d,e,f,g,h,i,g,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z");
		sb.append(",A,B,C,D,E,F,G,H,I,G,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z");
		// sb.append(",~,@,#,$,%,^,&,*,(,),_,+,|,`,.");
		sb.append(",1,2,3,4,5,6,7,8,9,0");
		String[] array = sb.toString().split(",");
		StringBuffer resultStringBuffer = new StringBuffer();
		Random random;
		int k;
		for (int i = 0; i < length; i++) {
			random = new java.util.Random();
			k = random.nextInt();
			resultStringBuffer.append(String.valueOf(array[Math.abs(k % 61)]));
		}
		return resultStringBuffer.toString();
	}

}