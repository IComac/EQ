package com.EQ.frame;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.*;

import com.EQ.EQ;
import com.EQ.dao.Dao;
import com.EQ.userList.ChatLog;
import com.EQ.userList.ChatTree;
import com.EQ.userList.User;

/**
 * 聊天窗口
 *
 */

public class TelFrame extends JFrame {
	private Dao dao;
	private User user;
	private JTextPane receiveText = new JTextPane();
	private JTextPane sendText = new JTextPane();
	private JButton sendButton = new JButton();
	private final JButton messageButton = new JButton();

	private final static Map<String, TelFrame> instance = new HashMap<String, TelFrame>();
	private JToolBar toolBar = new JToolBar();
	private JToggleButton toolFontButton = new JToggleButton();
	private JButton toolFaceButton = new JButton();
	private JButton toolbarSendFile = new JButton();
	private JButton toolbarShakeFrame = new JButton();
	private JButton toolbarCaptureScreen = new JButton();
	private final JButton hideBtn = new JButton();

	private JLabel otherSideInfo = new JLabel();
	private final JLabel label_1 = new JLabel();
	private JPanel panel_3 = new JPanel();
	private byte[] buf;
	private DatagramSocket ss;
	private String ip;
	private DatagramPacket dp;
	private TelFrame frame;
	private ChatTree tree;
	private int rightPanelWidth = 148;

	private final String SHAKING = "c)3a^1]g0";

	/**
	 * 打开聊天窗口
	 * 
	 * @param ssArg
	 * 		-UDP数据包
	 * @param dp
	 * 		-UDP套接字
	 * @param treeArg
	 * 		-用户树
	 * @return
	 */
	public static synchronized TelFrame getInstance(DatagramSocket ssArg,
			DatagramPacket dp, ChatTree treeArg) {
		InetAddress packetAddress = dp.getAddress();
		String tmpIp = packetAddress.getHostAddress();
		TelFrame frame;
		if (!instance.containsKey(tmpIp)) {
			frame = new TelFrame(ssArg, dp, treeArg);
			instance.put(tmpIp, frame);
		}else {
			frame = instance.get(tmpIp);
			frame.setBufs(dp.getData());
		}
		frame.receiveInfo();
		if (!frame.isVisible()) {
			frame.setVisible(true);
		}
		frame.setState(JFrame.NORMAL);
		frame.toFront();
		return frame;
	}

	/**
	 * 本类构造方法
	 * 
	 * @param ssArg
	 * 		-UDP数据包
	 * @param dpArg
	 * 		-UDP套接字
	 * @param treeArg
	 * 		-用户树
	 */
	private TelFrame(DatagramSocket ssArg, DatagramPacket dpArg,
			final ChatTree treeArg) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.tree = treeArg;
		ip = dpArg.getAddress().getHostAddress();
		dao = Dao.getDao();
		user = dao.getUser(ip);
		frame = this;
		ss = ssArg;
		dp = dpArg;
		buf = dp.getData();
		try {
			setBounds(200, 100, 521, 424);
			JSplitPane splitPane = new JSplitPane();
			JScrollPane scrollPane = new JScrollPane();
			getContentPane().add(splitPane);
			splitPane.setDividerSize(2);
			splitPane.setResizeWeight(0.8);
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			splitPane.setLeftComponent(scrollPane);
			scrollPane.setViewportView(receiveText);
			receiveText.setFont(new Font("宋体", Font.PLAIN, 12));
			receiveText.setInheritsPopupMenu(true);
			receiveText.setDragEnabled(true);
			receiveText.setMargin(new Insets(0, 0, 0, 0));
			receiveText.setEditable(false);
			receiveText.addComponentListener(new ComponentAdapter() {
				public void componentResized(final ComponentEvent e) {
					scrollPane.getVerticalScrollBar().setValue(
							receiveText.getHeight());
				}
			});
			receiveText.setDoubleBuffered(true);

			JPanel receiveTextPanel = new JPanel();

			splitPane.setRightComponent(receiveTextPanel);
			receiveTextPanel.setLayout(new BorderLayout());

			final FlowLayout flowLayout = new FlowLayout();
			flowLayout.setHgap(4);
			flowLayout.setAlignment(FlowLayout.LEFT);
			flowLayout.setVgap(0);
			JPanel buttonPanel = new JPanel();
			receiveTextPanel.add(buttonPanel, BorderLayout.SOUTH);
			final FlowLayout flowLayout_1 = new FlowLayout();
			flowLayout_1.setVgap(3);
			flowLayout_1.setHgap(20);
			buttonPanel.setLayout(flowLayout_1);

			buttonPanel.add(sendButton);
			sendButton.setMargin(new Insets(0, 14, 0, 14));
			sendButton.addActionListener(new sendActionListener());
			sendButton.setText("发送");

			buttonPanel.add(messageButton);
			messageButton.setMargin(new Insets(0, 14, 0, 14));
			messageButton.addActionListener(new MessageButtonActionListener());
			messageButton.setText("消息记录");

			JPanel toolbarPanel = new JPanel();
			receiveTextPanel.add(toolbarPanel, BorderLayout.NORTH);
			toolbarPanel.setLayout(new BorderLayout());

			ToolbarActionListener toolListener = new ToolbarActionListener();
			toolbarPanel.add(toolBar);
			toolBar.setBorder(new BevelBorder(BevelBorder.RAISED));
			toolBar.setFloatable(false);
			toolBar.add(toolFontButton);
			toolFontButton.setFocusPainted(false);
			toolFontButton.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarFontIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarFont.png"));
			toolFontButton.setIcon(toolbarFontIcon);
			toolFaceButton.setToolTipText("设置字体颜色和格式");
			toolBar.add(toolFaceButton);
			toolFaceButton.addActionListener(toolListener);
			toolFaceButton.setToolTipText("选择表情");
			toolFaceButton.setFocusPainted(false);
			toolFaceButton.setMargin(new Insets(0, 0, 0, 0));

			ImageIcon toolbarFaceIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarFace.png"));
			toolFaceButton.setIcon(toolbarFaceIcon);
			toolBar.add(toolbarSendFile);

			toolbarSendFile.addActionListener(toolListener);
			toolbarSendFile.setToolTipText("发送文件");
			toolbarSendFile.setFocusPainted(false);
			toolbarSendFile.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarPictureIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarPicture.png"));
			toolbarSendFile.setIcon(toolbarPictureIcon);

			toolBar.add(toolbarShakeFrame);
			toolbarShakeFrame.setActionCommand("shaking");
			toolbarShakeFrame.addActionListener(toolListener);
			toolbarShakeFrame.setToolTipText("发送窗口抖动");
			toolbarShakeFrame.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarShakeIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarShake.png"));
			toolbarShakeFrame.setIcon(toolbarShakeIcon);

			toolbarCaptureScreen.setActionCommand("CaptureScreen");
			toolbarCaptureScreen.addActionListener(toolListener);
			toolbarCaptureScreen.setToolTipText("截图");
			toolbarCaptureScreen.setFocusPainted(false);
			toolbarCaptureScreen.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarCaptureScreenIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarSceneCaptureScreen.png"));
			toolbarCaptureScreen.setIcon(toolbarCaptureScreenIcon);
			toolBar.add(toolbarCaptureScreen);

			JScrollPane scrollPane_1 = new JScrollPane();
			toolbarPanel.add(hideBtn, BorderLayout.EAST);
			hideBtn.addActionListener(new hideBtnActionListener());
			hideBtn.setMargin(new Insets(0, 0, 0, 0));
			hideBtn.setText(">");

			JPanel sendTextPanel = new JPanel();
			receiveTextPanel.add(sendTextPanel);
			sendTextPanel.setLayout(new BorderLayout());
			sendTextPanel.add(scrollPane_1);
			scrollPane_1.setHorizontalScrollBarPolicy
			(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			sendText.setInheritsPopupMenu(true);
			sendText.addKeyListener(new SendTextKeyListener());
			sendText.setVerifyInputWhenFocusTarget(false);
			sendText.setFont(new Font("宋体",Font.PLAIN, 12));
			sendText.setMargin(new Insets(0, 0, 0, 0));
			sendText.setDragEnabled(true);
			sendText.requestFocus();
			scrollPane_1.setViewportView(sendText);

			addWindowListener(new TelFrameClosing(tree));

			JScrollPane infoScrollPane = new JScrollPane();
			add(panel_3, BorderLayout.EAST);
			panel_3.setLayout(new BorderLayout());
			panel_3.add(infoScrollPane);
			infoScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
			infoScrollPane.setViewportView(otherSideInfo);
			otherSideInfo.setIconTextGap(-1);
			String imgPath = EQ.class.getResource("/image/telFrameImage/telUserInfo.png") + "";
			otherSideInfo.setText("<html><body background='" + imgPath 
					+ "'><table width='" + rightPanelWidth + "'>"
					+ "<tr><td>用户名: <br>&nbsp;&nbsp;" + user.getName() + "</td></tr>"
					+ "<tr><td>主机名: <br>&nbsp;&nbsp;" + user.getHost() + "</td></tr>"
					+ "<tr><td>IP地址: <br>&nbsp;&nbsp;" + user.getIp() + "</td></tr>"
					+ "<tr><td colspan='2' height=" + this.getHeight()*2 + "></td></tr>"
					+ "</table></body></html>");
			panel_3.add(label_1, BorderLayout.NORTH);
			label_1.setIcon(new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/telUserImage.png")));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		setVisible(true);
		setTitle("与 [" + user +"]通讯中");
	}

	/**
	 * 让窗口抖动
	 */
	private void shaking() {
		int x = getX();
		int y = getY();
		for(int i = 0;i < 10;i++) {
			if(i % 2 == 0){
				x += 5;
				y += 5;
			}else {
				x -=5;
				y -=5;
			}
			setLocation(x,y);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取聊天数据，并添加到聊天记录面板中
	 * 
	 */
	private void receiveInfo() {
		if (buf.length > 0) {
			String rText = new String(buf).replace("" + (char) 0, "");
			String hostAddress = dp.getAddress().getHostAddress();
			String info = dao.getUser(hostAddress).getName();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
			info = info + " (" + sdf.format(new Date()) + ")";
			appendReceiveText(info, Color.BLUE);
			if (rText.equals(SHAKING)) {
				appendReceiveText("[对方发送一个抖动窗口]\n", Color.RED);
				shaking();
			}else {
				appendReceiveText(rText + "\n", null);
				ChatLog.writeLog(user.getIp(), info);
				ChatLog.writeLog(user.getIp(), rText);
			}
		}
	}

	/**
	 * 发送按钮触发事件
	 * 
	 */
	class sendActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			String sendInfo = getSendInfo();
			if (sendInfo == null) {
				return;
			}
			insertUserInfoToReceiveText();
			appendReceiveText(sendInfo + "\n", null);
			byte[] tmpBuf = sendInfo.getBytes();
			DatagramPacket tdp = null;
			try {
				tdp = new DatagramPacket(tmpBuf, tmpBuf.length,
						new InetSocketAddress(ip, 1111));
				ss.send(tdp);
				ChatLog.writeLog(user.getIp(), sendInfo);
			} catch (SocketException e2) {
				// TODO: handle exception
				e2.printStackTrace();
			} catch (IOException e1) {
				// TODO: handle exception
				e1.printStackTrace();
				JOptionPane.showMessageDialog(TelFrame.this, e1.getMessage());
			}
			sendText.setText(null);
			sendText.requestFocus();
		}
	}

	/**
	 * 功能栏触发监听
	 * 
	 */
	class ToolbarActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			String command = e.getActionCommand();
			switch (command) {
			case "shaking":
				insertUserInfoToReceiveText();
				appendReceiveText("[您发送了一个抖动窗口，3秒之后可再次发送]\n", Color.GRAY);
				ChatLog.writeLog(user.getIp(), "[发送窗口抖动命令]");
				sendShakeCommand(e);
				break;
			case "CaptureScreen":
				new CaptureScreenUtil();
				break;
			default:
				JOptionPane.showMessageDialog(TelFrame.this, "此功能尚在建设中。");
			}
		}
	}

	/**
	 * 发送窗口抖动指令
	 * 
	 * @param e
	 * 		-触发抖动指令的组件
	 */
	private void sendShakeCommand(ActionEvent e) {
		Thread t = new Thread() {
			public void run() {
				Component c = (Component)e.getSource();
				try {
					byte[] tmpBuf = SHAKING.getBytes();
					DatagramPacket tdp = new DatagramPacket
							(tmpBuf, tmpBuf.length, new InetSocketAddress(ip, 1111));
					ss.send(tdp);
				} catch (IOException e1) {
					// TODO: handle exception
					e1.printStackTrace();
				} finally {
					c.setEnabled(false);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e1) {
						// TODO: handle exception
						e1.printStackTrace();
					} finally {
						c.setEnabled(true);
					}
				}
			};
		};
		t.start();
	}

	/**
	 * 窗口关闭事件
	 * 
	 */
	private final class TelFrameClosing extends WindowAdapter{
		private final JTree tree;

		private TelFrameClosing(JTree tree) {
			this.tree = tree;
		}

		public void windowClosing(final WindowEvent e) {
			tree.setSelectionPath(null);
			TelFrame.this.setState(ICONIFIED);
			TelFrame.this.dispose();
		}
	}

	/**
	 * 消息记录按钮动作事件
	 * 
	 */
	private class MessageButtonActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			new ChatDialog(frame, user);
		}
	}

	/**
	 * 快捷键键盘监听类
	 */
	private class SendTextKeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				sendButton.doClick();
			}
		}
	}

	/**
	 * 隐藏好友信息面板事件
	 * 
	 */
	private class hideBtnActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if (">".equals(hideBtn.getText())) {
				hideBtn.setText("<");
			}else {
				hideBtn.setText(">");
			}
			panel_3.setVisible(!panel_3.isVisible());
			TelFrame.this.setVisible(true);
		}
	}

	/**
	 * 设置窗口读取的消息数据
	 * 
	 * @param bufs
	 * 		-消息数据
	 */
	public void setBufs(byte[] bufs) {
		this.buf = bufs;
	}

	/**
	 * 获取要发送的消息
	 * 
	 * @return 要发送的消息
	 */
	public String getSendInfo() {
		String sendInfo = "";
		Document doc = sendText.getDocument();
		try {
			sendInfo = doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (sendInfo.equals("")) {
			JOptionPane.showMessageDialog(TelFrame.this, "不能发送空消息。");
			return null;
		}
		return sendInfo;
	}

	/**
	 * 聊天记录窗口插入当前用户
	 */
	private void insertUserInfoToReceiveText() {
		String info = null;
		try {
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			info = dao.getUser(hostAddress).getName();
		} catch (UnknownHostException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		info = info + " (" + sdf.format(new Date()) + ")";
		appendReceiveText(info, new Color(68, 184, 29));
		ChatLog.writeLog(user.getIp(), info);
	}

	public JTextPane getSendText() {
		return sendText;
	}

	/**
	 * 聊天记录框追加文本
	 * 
	 * @param sendInfo
	 * 		-追加文本内容
	 * @param color
	 * 		-文本颜色
	 */
	public void appendReceiveText(String sendInfo, Color color) {
		Style style = receiveText.addStyle("title", null);
		if (color != null) {
			StyleConstants.setForeground(style, color);
		}else {
			StyleConstants.setForeground(style, Color.BLACK);
		}
		receiveText.setEditable(true);
		receiveText.setCaretPosition(receiveText.getDocument().getLength());
		receiveText.setCharacterAttributes(style, false);
		receiveText.replaceSelection(sendInfo + "\n");
		receiveText.setEditable(false);
	}
}