package com.weinong.images.core;

import yao.config.tool.SCM;
import yao.util.db.DBPool;
import yao.util.db.PoolConf;
import yao.util.db.tool.Select;
import yao.util.db.tool.Update;
import yao.util.init.Tool;
import yao.util.log.Console;

/**
 * 数据库类（使用的yao-util2中的数据库组件）
 * 
 * @author myao
 */
public class DB_Images extends Tool {

	/** 从库，用来做查询操作 */
	public static final DB_Images IT = new DB_Images("images");

	private Select select;
	private Update update;
	private DBPool dbPool;

	private DB_Images(String name) {
		super(name);
	}

	protected void doReInit() {
		PoolConf pc = new PoolConf();
		pc.setDriver(SCM.getConfig().getString(name + ".driver"));
		pc.setUrl(SCM.getConfig().getString(name + ".url"));
		pc.setUser(SCM.getConfig().getString(name + ".user"));
		pc.setPassword(SCM.getConfig().getString(name + ".password"));
		pc.setMaxActive(SCM.getConfig().getInteger(name + ".maxActive"));
		pc.setCheckTimes(SCM.getConfig().getInteger(name + ".checkTimes"));
		pc.setMaxIdle(SCM.getConfig().getInteger(name + ".maxIdle"));
		pc.setDefaultQueryTimeout(SCM.getConfig().getInteger(name + ".queryTimeout"));
		pc.setAllowSelect(SCM.getConfig().getBoolean(name + ".allowSelect"));
		pc.setAllowUpdate(SCM.getConfig().getBoolean(name + ".allowUpdate"));
		Console.info(DB_Images.class.getSimpleName(), name + ".allowSelect=" + pc.isAllowSelect());
		Console.info(DB_Images.class.getSimpleName(), name + ".allowUpdate=" + pc.isAllowUpdate());
		try {
			dbPool = new DBPool(pc);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		select = new Select(dbPool);
		update = new Update(dbPool);
	}

	protected void doShutdown() {
		dbPool.shutdown();
	}

	public Select getSelect() {
		return select;
	}

	public Update getUpdate() {
		return update;
	}

	public DBPool getDbPool() {
		return dbPool;
	}

}
