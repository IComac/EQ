package com.EQ.userList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

public class ChatLog {

	/**
	 * ��¼������־
	 * @param userIP �Է�IP��ַ
	 * @param message ��¼����Ϣ
	 */
	static public void writeLog(String userIP, String message) {
		File rootdir = new File("db_EQ/chatdata/");
		if (!rootdir.exists()) {
			rootdir.mkdirs();
		}
		File log = new File(rootdir, userIP + ".chat");
		if (!log.exists()) {
			try {
				log.createNewFile();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
				System.out.println("�����¼�ļ��޷������� " + 
						log.getAbsolutePath());
			}
		}
		try (
				FileOutputStream fos = new FileOutputStream(log, true);
				OutputStreamWriter os = new OutputStreamWriter(fos);
				BufferedWriter bw = new BufferedWriter(os);
				){
			bw.write(message);
			bw.newLine();
			bw.flush();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ�����¼
	 * @param userIP ��ȡ����һ��IP��ص������¼
	 * @return -�����¼����
	 */
	static public List<String> readAllLog(String userIP){
		List<String> logs = new LinkedList<String>();
		File rootdir = new File("db_EQ/chatdata/");
		if (!rootdir.exists()) {
			rootdir.mkdirs();
		}
		File log = new File(rootdir, userIP + ".chat");
		if (!log.exists()) {
			return logs;
		}
		try(
				FileInputStream fis = new FileInputStream(log);
				InputStreamReader is = new InputStreamReader(fis);
				BufferedReader br = new BufferedReader(is);
				){
			String oneLine = null;
			while((oneLine = br.readLine()) != null) {
				logs.add(oneLine);
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return logs;
	}

}
