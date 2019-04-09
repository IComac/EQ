package com.EQ.frame;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.EQ.userList.ChatLog;
import com.EQ.userList.User;


/**
 * 聊天记录对话框
 *
 */
public class ChatDialog extends JDialog{
	/**
	 * 构造方法
	 * 
	 * @param owner
	 * 		-展示在哪个窗口之上
	 * @param user
	 * 		-展示哪个用户的聊天记录
	 */
	public ChatDialog(Frame owner, User user) {
		super(owner, true);
		int x = owner.getX();
		int y = owner.getY();
		setBounds(x + 20, y + 20, 400, 350);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("与[" + user + "] 消息记录");
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setWrapStyleWord(true);
		List<String> logs = ChatLog.readAllLog(user.getIp());
		if (logs.size() == 0) {
			area.append("(无)");
		}else {
			for(String log:logs) {
				area.append(log + "\n");
			}
		}
		JScrollPane scro = new JScrollPane(area);
		scro.doLayout();
		JScrollBar scrollBar = scro.getVerticalScrollBar();
		scrollBar.setValue(scrollBar.getMaximum());
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(scro, BorderLayout.CENTER);
		setContentPane(mainPanel);
		setResizable(false);
		setVisible(true);
	}
}
