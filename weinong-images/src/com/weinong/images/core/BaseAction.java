package com.weinong.images.core;

import yao.springmvc.Action;

public abstract class BaseAction extends Action {

	@SuppressWarnings("unchecked")
	protected <T> T sessionBean(T obj, Class<T> clazz) {
		String skey = clazz.getName() + "@" + getClass().getName();
		if (null != obj) {
			request.getSession().setAttribute(skey, obj);
			return obj;
		}
		obj = (T) request.getSession().getAttribute(skey);
		if (null != obj) { return obj; }
		try {
			obj = clazz.newInstance();
			request.getSession().setAttribute(skey, obj);
			return obj;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
