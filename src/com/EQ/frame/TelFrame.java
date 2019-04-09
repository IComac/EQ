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
 * ���촰��
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
	 * �����촰��
	 * 
	 * @param ssArg
	 * 		-UDP���ݰ�
	 * @param dp
	 * 		-UDP�׽���
	 * @param treeArg
	 * 		-�û���
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
	 * ���๹�췽��
	 * 
	 * @param ssArg
	 * 		-UDP���ݰ�
	 * @param dpArg
	 * 		-UDP�׽���
	 * @param treeArg
	 * 		-�û���
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
			receiveText.setFont(new Font("����", Font.PLAIN, 12));
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
			sendButton.setText("����");

			buttonPanel.add(messageButton);
			messageButton.setMargin(new Insets(0, 14, 0, 14));
			messageButton.addActionListener(new MessageButtonActionListener());
			messageButton.setText("��Ϣ��¼");

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
			toolFaceButton.setToolTipText("����������ɫ�͸�ʽ");
			toolBar.add(toolFaceButton);
			toolFaceButton.addActionListener(toolListener);
			toolFaceButton.setToolTipText("ѡ�����");
			toolFaceButton.setFocusPainted(false);
			toolFaceButton.setMargin(new Insets(0, 0, 0, 0));

			ImageIcon toolbarFaceIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarFace.png"));
			toolFaceButton.setIcon(toolbarFaceIcon);
			toolBar.add(toolbarSendFile);

			toolbarSendFile.addActionListener(toolListener);
			toolbarSendFile.setToolTipText("�����ļ�");
			toolbarSendFile.setFocusPainted(false);
			toolbarSendFile.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarPictureIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarPicture.png"));
			toolbarSendFile.setIcon(toolbarPictureIcon);

			toolBar.add(toolbarShakeFrame);
			toolbarShakeFrame.setActionCommand("shaking");
			toolbarShakeFrame.addActionListener(toolListener);
			toolbarShakeFrame.setToolTipText("���ʹ��ڶ���");
			toolbarShakeFrame.setMargin(new Insets(0, 0, 0, 0));
			ImageIcon toolbarShakeIcon = new ImageIcon(EQ.class.getResource
					("/image/telFrameImage/toolbarImage/ToolbarShake.png"));
			toolbarShakeFrame.setIcon(toolbarShakeIcon);

			toolbarCaptureScreen.setActionCommand("CaptureScreen");
			toolbarCaptureScreen.addActionListener(toolListener);
			toolbarCaptureScreen.setToolTipText("��ͼ");
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
			sendText.setFont(new Font("����",Font.PLAIN, 12));
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
					+ "<tr><td>�û���: <br>&nbsp;&nbsp;" + user.getName() + "</td></tr>"
					+ "<tr><td>������: <br>&nbsp;&nbsp;" + user.getHost() + "</td></tr>"
					+ "<tr><td>IP��ַ: <br>&nbsp;&nbsp;" + user.getIp() + "</td></tr>"
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
		setTitle("�� [" + user +"]ͨѶ��");
	}

	/**
	 * �ô��ڶ���
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
	 * ��ȡ�������ݣ�����ӵ������¼�����
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
				appendReceiveText("[�Է�����һ����������]\n", Color.RED);
				shaking();
			}else {
				appendReceiveText(rText + "\n", null);
				ChatLog.writeLog(user.getIp(), info);
				ChatLog.writeLog(user.getIp(), rText);
			}
		}
	}

	/**
	 * ���Ͱ�ť�����¼�
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
	 * ��������������
	 * 
	 */
	class ToolbarActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			String command = e.getActionCommand();
			switch (command) {
			case "shaking":
				insertUserInfoToReceiveText();
				appendReceiveText("[��������һ���������ڣ�3��֮����ٴη���]\n", Color.GRAY);
				ChatLog.writeLog(user.getIp(), "[���ʹ��ڶ�������]");
				sendShakeCommand(e);
				break;
			case "CaptureScreen":
				new CaptureScreenUtil();
				break;
			default:
				JOptionPane.showMessageDialog(TelFrame.this, "�˹������ڽ����С�");
			}
		}
	}

	/**
	 * ���ʹ��ڶ���ָ��
	 * 
	 * @param e
	 * 		-��������ָ������
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
	 * ���ڹر��¼�
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
	 * ��Ϣ��¼��ť�����¼�
	 * 
	 */
	private class MessageButtonActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			new ChatDialog(frame, user);
		}
	}

	/**
	 * ��ݼ����̼�����
	 */
	private class SendTextKeyListener extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_ENTER) {
				sendButton.doClick();
			}
		}
	}

	/**
	 * ���غ�����Ϣ����¼�
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
	 * ���ô��ڶ�ȡ����Ϣ����
	 * 
	 * @param bufs
	 * 		-��Ϣ����
	 */
	public void setBufs(byte[] bufs) {
		this.buf = bufs;
	}

	/**
	 * ��ȡҪ���͵���Ϣ
	 * 
	 * @return Ҫ���͵���Ϣ
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
			JOptionPane.showMessageDialog(TelFrame.this, "���ܷ��Ϳ���Ϣ��");
			return null;
		}
		return sendInfo;
	}

	/**
	 * �����¼���ڲ��뵱ǰ�û�
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
	 * �����¼��׷���ı�
	 * 
	 * @param sendInfo
	 * 		-׷���ı�����
	 * @param color
	 * 		-�ı���ɫ
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
