package com.weinong.images.modules.upload;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadImageToWeixinAction extends UploadImageAction {

	private String access_token;
	private static final String url = "https://qyapi.weixin.qq.com/cgi-bin/media/upload?type=image";

	@Override
	protected void doSomeThing(String path, String filename) {
		File file = new File(path, filename);
		if (!file.exists() || !file.isFile()) { return; }

		try {
			/**
			 * 第一部分
			 */
			URL urlObj = new URL(url + "&access_token=" + access_token);
			HttpURLConnection con = null;
			con = (HttpURLConnection) urlObj.openConnection();

			/**
			 * 设置关键值
			 */
			con.setRequestMethod("POST"); // 以Post方式提交表单，默认get方式
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false); // post方式不能使用缓存

			// 设置请求头信息
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");

			// 设置边界
			String BOUNDARY = "----------" + System.currentTimeMillis();
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

			// 请求正文信息

			// 第一部分：
			StringBuilder sb = new StringBuilder();
			sb.append("--"); // ////////必须多两道线
			sb.append(BOUNDARY);
			sb.append("\r\n");
			sb.append("Content-Disposition: form-data;name=\"media\";filename=\"" + file.getName() + "\"\r\n");
			sb.append("Content-Type:image/jpeg\r\n\r\n");

			byte[] head = sb.toString().getBytes("utf-8");

			// 获得输出流

			OutputStream out = new DataOutputStream(con.getOutputStream());
			out.write(head);

			// 文件正文部分
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			int bytes = 0;
			byte[] bufferOut = new byte[1024];
			while ((bytes = in.read(bufferOut)) != -1) {
				out.write(bufferOut, 0, bytes);
			}
			in.close();

			// 结尾部分
			byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线

			out.write(foot);

			out.flush();
			out.close();

			/**
			 * 读取服务器响应，必须读取,否则提交不成功
			 */
			if (con.getResponseCode() == 200) {
				StringBuffer stringBuffer = new StringBuffer();
				try {
					// 定义BufferedReader输入流来读取URL的响应
					BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String line = null;
					while ((line = reader.readLine()) != null) {
						System.out.println(line);
						stringBuffer.append(line);
					}
				} catch (Exception e) {
					System.out.println("发送POST请求出现异常！" + e);
					e.printStackTrace();
				}
				setMessage(stringBuffer.toString());
			} else {
				error("无法连接微信服务器!", null);
			}
		} catch (IOException e) {
			e.printStackTrace();
			error("无法连接微信服务器!", e);
		}

		/**
		 * 下面的方式读取也是可以的
		 */

		// try {
		// // 定义BufferedReader输入流来读取URL的响应
		// BufferedReader reader = new BufferedReader(new InputStreamReader(
		// con.getInputStream()));
		// String line = null;
		// while ((line = reader.readLine()) != null) {
		// System.out.println(line);
		// }
		// } catch (Exception e) {
		// System.out.println("发送POST请求出现异常！" + e);
		// e.printStackTrace();
		// }
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
}
