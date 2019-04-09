package com.EQ.userList;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *用户类
 */
public class User {
	private String ip;
	private String host;
	private String tipText;
	private String name;
	private String icon;
	private Icon iconImg = null;

	/**
	 * 空构造方法
	 */
	public User() {
		// TODO 自动生成的构造函数存根
	}

	public User(String host, String ip) {
		this.ip = ip;
		this.host = host;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String toString() {
		String strName = getName() == null ? getHost() : getName();
		return strName;
	}

	public String getIcon() {
		return icon;
	}

	/**
	 * 获取头像图片
	 * @return 头像图标头像
	 */
	public Icon getIconImg() {
		int faceNum = 1;
		if (ip != null && !ip.isEmpty()) {
			String[] num = ip.split("\\.");
			if (num.length == 4) {
				Integer num1 = Integer.parseInt(num[2]) + 1;
				Integer num2 = Integer.parseInt(num[3]);
				faceNum = (num1 * num2) % 11 + 1;
			}
		}
		File imageFile = new File("res/NEWFACE/" + faceNum + ".png");
		if (!imageFile.exists()) {
			imageFile = new File("res/NEWFACE/1.png");
		}
		iconImg = new ImageIcon(imageFile.getAbsolutePath());
		return iconImg;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTipText() {
		return tipText;
	}

	public void setTipText(String tipText) {
		this.tipText = tipText;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}	
}
