package com.weinong.images;

import com.weinong.config.WNConfig;
import yao.config.exception.ConfigException;
import yao.config.tool.SCM;

import java.io.IOException;

public class Conf {

	private static WNConfig image_conf ;

	static {
		try{
			image_conf = new WNConfig("weinong","image_server.ini");

		} catch (ConfigException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void init() throws IOException, ConfigException {

		if (null == image_conf) {
			image_conf = new WNConfig("weinong","image_server.ini");
		}
	}

	public static final String image_dir = image_conf.getString("image_dir");
	public static final String image_http_page = image_conf.getString("image_http_page");
	public static final String imgee_dir_spilt = image_conf.getString("img_url_spilt");

	public static final int red = image_conf.getInteger("water_color.red");
	public static final int green = image_conf.getInteger("water_color.green");
	public static final int blue = image_conf.getInteger("water_color.blue");




	public static String getSecret(String secret_name) {
		return image_conf.getString(secret_name);
	}

	public static void main(String[] args) throws IOException, ConfigException {

		image_conf = new WNConfig("weinong","image_server.ini");
		System.out.println(image_conf +image_dir +image_http_page+imgee_dir_spilt+red+green+blue);
	}
}
