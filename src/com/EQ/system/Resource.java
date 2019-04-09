package com.EQ.system;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import com.EQ.userList.ChatLog;
import com.EQ.userList.ChatTree;
import com.EQ.userList.User;

/**
 * 
 * @author Administrator
 *
 */
public class Resource {
	/**
	 * �����û�
	 * 
	 * @param tree
	 * 		-�û���
	 * @param progressBar
	 * 		-������
	 * @param list
	 * 		-������û��б�
	 * @param button
	 * 		-��������İ�ť
	 * @param ipStart
	 * 		-��ʼIP��ַ
	 * @param ipEnd
	 * 		-����IP��ַ
	 * 
	 */
	public static void searchUsers(ChatTree tree, JProgressBar progressBar,
			JList<?> list, JToggleButton button, String ipStart, String ipEnd) {
		String[] is = ipStart.split("\\.");
		String[] ie = ipEnd.split("\\.");
		int[] ipsInt = new int[4];
		int[] ipeInt = new int[4];
		for (int i = 0; i < 4; i++) {
			ipsInt[i] = Integer.parseInt(is[i]);
			ipeInt[i] = Integer.parseInt(ie[i]);
		}
		progressBar.setIndeterminate(true);
		progressBar.setStringPainted(true);
		DefaultListModel model = new DefaultListModel();
		model.addElement("�������:");
		list.setModel(model);
		try {
			int a = ipsInt[0], b = ipsInt[1], c = ipsInt[2], d = ipsInt[3];
			one:while (a <= ipeInt[0]) {
				two:while(b <= 255) {
					three:while(c <= 255) {
						four:while(d <= 255) {
							if(!button.isSelected()) {
								progressBar.setIndeterminate(false);
								return;
							}
							Thread.sleep(100);
							String ip = a + "." + b + "." + c + "." + d;
							progressBar.setString("����������" + ip);
							if (tree.addUser(ip, "search")) {
								model.addElement("<html><b><font color=green>���"
										+ ip + "</font></b></html>");
							}
							if (a == ipeInt[0] && b == ipeInt[1] && c == ipeInt[2] && d == ipeInt[3]) {
								break one;
							}
							d++;
							if (d > 255) {
								d = 0;
								break four;
							}
						}
			c++;
			if (c > 255) {
				c = 0;
				break three;
			}
					}
			b++;
			if (b > 255) {
				b = 0;
				break two;
			}
				}
			a++;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			progressBar.setIndeterminate(false);
			progressBar.setString("�������");
			button.setText("�������û�");
			button.setSelected(false);
		}
	}

	/**
	 * ��ϢȺ�����˹��ܿ���Ⱥ��֪ͨ��ÿһ���յ���Ϣ���û����ᵯ������Ự��չʾȺ������Ϣ
	 * 
	 * @param ss
	 * 		-UDP�׽���
	 * @param selectionPaths
	 * 		-��ѡ�е��û����·��
	 * @param message
	 * 		-Ⱥ������Ϣ
	 */
	public static void sendGroupMessenger(final DatagramSocket ss, 
			final TreePath[] selectionPaths, final String message) {
		new Thread(new Runnable() {
			public void run() {
				MessageFrame messageFrame = new MessageFrame();
				messageFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				try {
					for(TreePath path : selectionPaths) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
								.getLastPathComponent();
						User user = (User)node.getUserObject();
						messageFrame.setStateBarInfo("<html>���ڸ�<font color=bule>"
								+ user.getName()
								+ "</font>������Ϣ......</html>");
						Thread.sleep(20);
						byte[] strData = message.getBytes();
						InetAddress toAddress = InetAddress.getByName(user.getIp());
						DatagramPacket tdp = null;
						tdp = new DatagramPacket(strData, strData.length, toAddress, 1111);
						ss.send(tdp);
						messageFrame.addMessage(user.getName() + "������ϣ�", true);
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String title = user.getName() + " ("
								+ sdf.format(new Date()) + ")";
						ChatLog.writeLog(user.getIp(), title);
						ChatLog.writeLog(user.getIp(), "[Ⱥ������]" + message);
					}
					messageFrame.setStateBarInfo("��Ϣ������ϣ����Թرմ��ڡ�");
				} catch (UnknownHostException e) {
					// TODO: handle exception
					e.printStackTrace();
				}catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}catch (IOException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * �򿪶Է������ļ���
	 * 
	 * @param str
	 * 		�Է�IP
	 */
	public static void startFolder(String str) {
		try {
			Runtime.getRuntime().exec("cmd /c start" + str);
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}

