package com.geotdb.compile.utils;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class ProfileUtil {

	private static AtomicInteger counter = new AtomicInteger(0);

	/**
	 * 生成一个10位纯数字的ID.用于MySql的int型数据.
	 * 
	 * @return
	 */
	public static int getAtomicCounter() {
		String tempValue2 = "";
		int a[] = new int[4];
		for (int i = 0; i < a.length; i++) {
			a[i] = (int) (10 * (Math.random()));
			tempValue2 += "" + a[i];
		}

		if (counter.get() > 999999) {
			counter.set(1);
		}
		long time = System.currentTimeMillis();
		long tempValue = time * 100 + counter.incrementAndGet();

		//System.out.println("tempValue:" + tempValue);

		int returnValue = Integer.valueOf(1+tempValue2+String.valueOf(tempValue).substring(10, 15));
		

		return returnValue;
	}
	
	public static void main(String[] args) {

		System.out.println(ProfileUtil.getAtomicCounter());

		System.out.println(ProfileUtil.getAtomicCounter());

		System.out.println(ProfileUtil.getAtomicCounter());
		

	}

}
