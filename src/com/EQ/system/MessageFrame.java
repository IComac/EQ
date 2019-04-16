package com.EQ.system;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

/**
 * 群发消息窗口
 *
 */
public class MessageFrame extends JFrame {
	private final ImageIcon successIcon = new ImageIcon(
			MessageFrame.class.getResource("/messSendIcon/Success.gif"));

	private final ImageIcon failIcon = new ImageIcon(
			MessageFrame.class.getResource("/messSendIcon/Fail.gif"));

	private JTextPane textPane;

	private final JLabel stateLabel = new JLabel();

	private final JScrollPane scrollPane = new JScrollPane();

	public MessageFrame() {
		setBounds(100, 100, 307, 383);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setTitle("群发消息");
		setVisible(true);

		getContentPane().add(stateLabel, BorderLayout.SOUTH);
		stateLabel.setText("请稍等消息结果。");

		getContentPane().add(scrollPane);
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
		textPane.setFont(new Font("", Font.PLAIN, 14));
		textPane.setDragEnabled(true);
		textPane.setBorder(new BevelBorder(BevelBorder.LOWERED));
	}

	/**
	 * 添加消息内容
	 * 
	 * @param message
	 * 		消息内容
	 * @param success
	 * 		是否成功发送消息
	 */
	public void addMessage(String message, boolean success) {
		textPane.setEditable(true);
		textPane.setCaretPosition(textPane.getDocument().getLength());
		if (success) {
			textPane.insertIcon(successIcon);
		}else {
			textPane.insertIcon(failIcon);
		}
		textPane.setCaretPosition(textPane.getDocument().getLength());

		textPane.replaceSelection(message + "\n");
		if (!isVisible()) {
			setVisible(true);
		}
		textPane.setEditable(false);
	}

	/**
	 * 设置状态栏消息
	 * 
	 * @param str
	 * 		-状态栏消息
	 */
	public void setStateBarInfo(String str) {
		stateLabel.setText(str);
	}

}