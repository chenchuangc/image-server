package com.weinong.images.core;

import javax.servlet.ServletContext;

import com.sun.org.apache.xpath.internal.SourceTree;
import org.springframework.web.context.WebApplicationContext;

import yao.config.tool.SCM;
import yao.springmvc.WebApplicationContextInitializer;
import yao.util.init.Initer;
import yao.util.log.Console;

public class InitListener implements WebApplicationContextInitializer {

	private Initer initer;

	public void beforeWebApplicationContextInitialized(ServletContext servletContext) {
		Console.info(this, "Start system...");
		try {
			Console.info(this, "Init config...");
			SCM.init();
			System.out.println(System.getProperty("java.library.path"));
			System.loadLibrary("webp-imageio");

		} catch (Exception e) {
			Console.error(this, "Start error：", e);
			throw new RuntimeException(e);
		}
		initer = new Initer(this);
		initer.registTool(DB_Images.IT);
		try {
			initer.init();
		} catch (Exception e) {
			Console.error(this, "Start error：", e);
			throw new RuntimeException(e);
		}
	}

	public void afterWebApplicationContextDestroy() {
		Console.info(this, "Stop system...");
		initer.shutdown();
	}

	public void afterWebApplicationContextInitialized(WebApplicationContext webApplicationContext) {}
}
