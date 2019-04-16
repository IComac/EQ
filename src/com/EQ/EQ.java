/**
 * 
 */
/**
 * @author IComac
 *
 */
package com.EQ;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.ScrollPane;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Popup;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JPopupMenu.Separator;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.EQ.dao.Dao;
import com.EQ.frame.TelFrame;
import com.EQ.system.Resource;
import com.EQ.userList.ChatTree;
import com.EQ.userList.User;


public class EQ extends Dialog{
	public static EQ frame = null;
	private JTextField ipEndTField;
	private JTextField ipStartTField;
	private ChatTree chatTree;
	private JPopupMenu popupMenu;
	private JTabbedPane tabbedPane;
	private JToggleButton searchUserButton;
	private JProgressBar progressBar;
	private JList faceList;
	private JButton selectInterfaceOKButton;
	private DatagramSocket ss;
	private final JLabel stateLabel;
	private Rectangle location;
	public static TrayIcon trayicon;
	private Dao dao;
	public final static Preferences preferences = Preferences.systemRoot();
	public JButton userInfoButton;

	public static void main(String args[]) {
		try {
			String laf = preferences.get("lookAndFeel", "java默认");
			if (laf.contains("当前系统"))
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			EQ frame = new EQ();
			frame.setVisible(true);
			frame.SystemTrayInitial();
			frame.server();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 主类构造方法
	 */
	public EQ(){
		super(new Frame());
		frame = this;
		dao = Dao.getDao();
		location = dao.getLocation();
		setTitle("企业QQ");
		setBounds(location);
		progressBar = new JProgressBar();
		progressBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		tabbedPane = new JTabbedPane();
		popupMenu = new JPopupMenu();
		chatTree = new ChatTree(this);
		stateLabel = new JLabel();
		addWindowListener(new FrameWindowListener());
		addComponentListener(new ComponentAdapter() {
			public void componentResized(final ComponentEvent e) {
				saveLocation();
			}

			public void componentMoved(final ComponentEvent e) {
				saveLocation();
			}
		});
		try {
			ss = new DatagramSocket(1111);
		} catch (SocketException e2) {
			// TODO: handle exception
			if (e2.getMessage().startsWith("Address already in use")) {
				JOptionPane.showMessageDialog(this, "服务端口被占用，或者本软件已经运行。");
			}
			System.exit(0);
		}
		final JPanel BannerPanel = new JPanel();
		BannerPanel.setLayout(new BorderLayout());
		add(BannerPanel, BorderLayout.NORTH);
		userInfoButton = new JButton();
		BannerPanel.add(userInfoButton, BorderLayout.WEST);
		userInfoButton.setMargin(new Insets(0, 0, 0, 10));
		initUserInfoButton();

		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.setTabPlacement(SwingConstants.LEFT);
		ImageIcon userTicon = new ImageIcon(EQ.class.getResource
				("/image/tabIcon/tabLeft.PNG"));
		tabbedPane.addTab(null, userTicon, createUserList(), "用户列表");;
		ImageIcon sysOTicon = new ImageIcon(EQ.class.getResource
				("/image/tabIcon/tabLeft2.PNG"));
		tabbedPane.addTab(null, sysOTicon, createSysToolPanel(), "系统操作");
		setAlwaysOnTop(true);
	}

	/**
	 * 用户列表面板
	 * 
	 * @return
	 */
	public JScrollPane createUserList() {
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy
		(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		addUserPopup(chatTree, getPopupMenu());
		scrollPane.setViewportView(chatTree);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		chatTree.addMouseListener(new ChatTreeMouseListener());
		return scrollPane;
	}

	/**
	 * 系统工具面板
	 * 
	 * @return
	 */
	private JScrollPane createSysToolPanel() {
		JPanel sysToolPanel = new JPanel();
		sysToolPanel.setLayout(new BorderLayout());
		JScrollPane sysToolScrollPanel = new JScrollPane();
		sysToolScrollPanel.setHorizontalScrollBarPolicy
		(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sysToolScrollPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		sysToolScrollPanel.setViewportView(sysToolPanel);
		sysToolPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

		JPanel interfacePanel = new JPanel();
		sysToolPanel.add(interfacePanel, BorderLayout.NORTH);
		interfacePanel.setLayout(new BorderLayout());
		interfacePanel.setBorder(new TitledBorder("界面选择-再次启动生效"));
		faceList = new JList(new String[] {"当前系统","java默认"});
		interfacePanel.add(faceList);
		faceList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		final JPanel interfaceSubPanel = new JPanel();
		interfaceSubPanel.setLayout(new FlowLayout());
		interfacePanel.add(interfaceSubPanel, BorderLayout.SOUTH);
		selectInterfaceOKButton = new JButton("确定");
		selectInterfaceOKButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (faceList.getSelectedValue() == null) {
					JOptionPane.showMessageDialog(EQ.this, 
							"你未选中任何主题", "提示", JOptionPane.ERROR_MESSAGE);
					return;
				}
				preferences.put("lookAndFeel", faceList.getSelectedValue().toString());
				JOptionPane.showMessageDialog(EQ.this, "重新运行本软件后生效");
			}
		});
		interfaceSubPanel.add(selectInterfaceOKButton);

		JPanel searchUserPanel = new JPanel();
		sysToolPanel.add(searchUserPanel);
		searchUserPanel.setLayout(new BorderLayout());
		final JPanel searchControlPanel = new JPanel();
		searchControlPanel.setLayout(new GridLayout(0, 1));
		searchUserPanel.add(searchControlPanel, BorderLayout.SOUTH);
		final JList searchUserList = new JList(new String[] {"检测用户列表"});
		final JScrollPane scrollPane_2 = new JScrollPane(searchUserList);
		scrollPane_2.setDoubleBuffered(true);
		searchUserPanel.add(scrollPane_2);
		searchUserList.setBorder(new BevelBorder(BevelBorder.LOWERED));
		searchUserButton = new JToggleButton();
		searchControlPanel.add(progressBar);
		searchControlPanel.add(searchUserButton);
		searchUserPanel.setBorder(new TitledBorder("搜索用户"));

		final JPanel ipPanel = new JPanel();
		final GridLayout gridLayout_2 = new GridLayout(0, 1);
		gridLayout_2.setVgap(5);
		ipPanel.setLayout(gridLayout_2);
		ipPanel.setMaximumSize(new Dimension(600, 90));
		ipPanel.setBorder(new TitledBorder("IP搜索范围"));
		final JPanel panel_5 = new JPanel();
		panel_5.setLayout(new BoxLayout(panel_5, BoxLayout.X_AXIS));
		ipPanel.add(panel_5);
		panel_5.add(new JLabel("起始IP: "));
		ipStartTField = new JTextField("192.168.0.1");
		panel_5.add(ipStartTField);
		final JPanel panel_6 = new JPanel();
		panel_6.setLayout(new BoxLayout(panel_6, BoxLayout.X_AXIS));
		ipPanel.add(panel_6);
		panel_6.add(new JLabel("终止IP: "));
		ipEndTField = new JTextField("192.168.1.255");
		panel_6.add(ipEndTField);
		sysToolPanel.add(ipPanel, BorderLayout.SOUTH);

		stateLabel.setText("总人数: " + chatTree.getRowCount());
		return sysToolScrollPanel;
	}

	/**
	 * 初始化用户信息按钮
	 */
	private void initUserInfoButton() {
		try {
			String ip = InetAddress.getLocalHost().getHostAddress();
			User user = dao.getUser(ip);
			userInfoButton.setIcon(user.getIconImg());
			userInfoButton.setText(user.getName());
			userInfoButton.setIconTextGap(JLabel.RIGHT);
			userInfoButton.setToolTipText(user.getTipText());
			userInfoButton.getParent().doLayout();
		} catch (UnknownHostException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		}
	}

	/**
	 * 搜索用户事件
	 * 
	 */
	class SearchUserActionListener implements ActionListener{
		private final JList list;

		SearchUserActionListener(JList list){
			this.list =list;
		}

		public void actionPerformed(ActionEvent e) {
			String regex = "(\\d[1-9]\\d|1\\d{2}2[0-4]\\d|25[0-5])" + 
					"(\\.(\\d[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
			String ipStart = ipStartTField.getText().trim();
			String ipEnd = ipEndTField.getText().trim();
			if (ipStart.matches(regex) && ipEnd.matches(regex)) {
				if (searchUserButton.isSelected()) {
					searchUserButton.setText("停止搜索");
					new Thread(new Runnable() {
						public void run(){
							Resource.searchUsers
							(chatTree, progressBar, list, searchUserButton, ipStart, ipEnd);
						}
					}).start();
				}else {
					searchUserButton.setText("搜索新用户");
				}
			}else {
				JOptionPane.showMessageDialog(EQ.this, "请检查IP地址格式", "注意", JOptionPane.WARNING_MESSAGE);
				searchUserButton.setSelected(false);
			}
		}
	}

	/**
	 * 用户列表的监听器
	 * 
	 * @author Administrator
	 * 
	 */
	private class ChatTreeMouseListener extends MouseAdapter{
		public void mouseClicked(final MouseEvent e) {
			if(e.getClickCount() == 2) {
				TreePath path = chatTree.getSelectionPath();
				if (path == null) {
					return;
				}
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
				User user = (User)node.getUserObject();
				try {
					DatagramPacket packet = new DatagramPacket(new byte[0], 0, InetAddress.getByName(user.getIp()), 1111);
					TelFrame.getInstance(ss, packet, chatTree);
				} catch (IOException e1) {
					// TODO: handle exception
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * 启动服务器
	 */
	private void server() {
		new Thread(new Runnable() {
			public void run() {
				// TODO 自动生成的方法存根
				while(true) {
					if (ss != null) {
						byte[] buf = new byte[4096];
						DatagramPacket dp = new DatagramPacket(buf, buf.length);
						try {
							ss.receive(dp);
							TelFrame.getInstance(ss, dp, chatTree);
						} catch (IOException e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}

	/**
	 * 右键弹出菜单添加用户
	 * 
	 * @param component
	 * 		-触发右键的组件
	 * @param popup
	 * 		-弹出式菜单
	 */
	private void addUserPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			/**
			 * 展示右键菜单栏
			 */
			private void showMenu(MouseEvent e) {
				if (chatTree.getSelectionPaths() == null) {
					popupMenu.getComponent(0).setEnabled(false);
					popupMenu.getComponent(2).setEnabled(false);
					popupMenu.getComponent(3).setEnabled(false);
					popupMenu.getComponent(4).setEnabled(false);
				}else {
					if (chatTree.getSelectionPaths().length < 2) {
						popupMenu.getComponent(3).setEnabled(false);
					}else {
						popupMenu.getComponent(3).setEnabled(true);
					}
					popupMenu.getComponent(0).setEnabled(true);
					popupMenu.getComponent(2).setEnabled(true);
					popupMenu.getComponent(4).setEnabled(true);
				}
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	/**
	 * 保存主窗口位置的方法
	 */
	private void saveLocation() {
		location = getBounds();
		dao.updateLocation(location);
	}

	/**
	 * 创建右键弹出菜单
	 * 
	 * @return
	 */
	protected JPopupMenu getPopupMenu() {
		if (popupMenu == null) {
			popupMenu = new JPopupMenu();
			popupMenu.setOpaque(false);
		}
		final JMenuItem rename = new JMenuItem();
		popupMenu.add(rename);
		rename.addActionListener(new RenameActionListener());
		rename.setText("更名");
		final JMenuItem addUser = new JMenuItem();
		addUser.addActionListener(new AddUserActionListener());
		popupMenu.add(addUser);
		addUser.setText("添加用户");
		final JMenuItem delUser = new JMenuItem();
		delUser.addActionListener(new delUserActionListener());
		popupMenu.add(delUser);
		delUser.setText("删除用户");
		final JMenuItem messagerGroupSend = new JMenuItem();
		messagerGroupSend.addActionListener(new messagerGroupSendActionListener());
		messagerGroupSend.setText("信使群发");
		popupMenu.add(messagerGroupSend);
		final JMenuItem accessComputerFolder = new JMenuItem("访问主机资源");
		accessComputerFolder.setActionCommand("computer");
		popupMenu.add(accessComputerFolder);
		accessComputerFolder.addActionListener(new accessFolderActionListener());
		return popupMenu;
	}

	/**
	 * 设置状态栏信息
	 * 
	 * @param str
	 */
	public void setStatic(String str) {
		if (stateLabel != null) {
			stateLabel.setText(str);
		}
	}

	/**
	 * 显示更名对话框
	 * 
	 * @param str
	 * 		-旧名字
	 * @param 新名字
	 */
	private String showInputDialog(String str) {
		String newName = JOptionPane.showInputDialog(this, "<html>输入<font color=red>"
				+ str + "</font>的新名字</html>");
		return newName;
	}

	/**
	 * 访问对方共享资源文件夹
	 * 
	 */
	private class accessFolderActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			TreePath path = chatTree.getSelectionPath();
			if (path == null) {
				return;
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			User user = (User)node.getUserObject();
			String ip = "\\\\" + user.getIp();
			String command = e.getActionCommand();
			if (command.equals("computer")) {
				Resource.startFolder(ip);
			}
		}
	}

	/**
	 * 更改用户名事件
	 * 
	 */
	private class RenameActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			TreePath path = chatTree.getSelectionPath();
			if (path == null) {
				return;
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
			User user = (User)node.getUserObject();
			String newName = showInputDialog(user.getName());
			if (newName != null && !newName.isEmpty()) {
				user.setName(newName);
				dao.updateUser(user);
				DefaultTreeModel model = (DefaultTreeModel)chatTree.getModel();
				model.reload();
				chatTree.setSelectionPath(path);
				initUserInfoButton();
			}
		}
	}

	/**
	 * 窗口关闭事件
	 * 
	 */
	private class FrameWindowListener extends WindowAdapter{
		public void windowClosing(final WindowEvent e) {
			setVisible(false);
		}
	}

	/**
	 * 添加用户动作事件
	 * 
	 */
	private class AddUserActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			String ip = JOptionPane.showInputDialog(EQ.this, "输入新用户IP地址");
			if (ip != null) {
				chatTree.addUser(ip, "add");
			}
		}
	}

	/**
	 * 删除用户动作事件
	 * 
	 */
	private class delUserActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			chatTree.delUser();
		}
	}

	/**
	 * 群发消息动作事件
	 * 
	 */
	private class messagerGroupSendActionListener implements ActionListener{
		public void actionPerformed(final ActionEvent e) {
			String systemName = System.getProperty("os.name");
			if (systemName != null && systemName.equals("Windows 7")) {
				String message = JOptionPane.showInputDialog(EQ.this, "请输入群发消息", "信使群发", JOptionPane.INFORMATION_MESSAGE);
				if (message != null && !message.equals("")) {
					TreePath[] selectionPaths = chatTree.getSelectionPaths();
					Resource.sendGroupMessenger(ss, selectionPaths, message);
				}else if (message != null && message.isEmpty()) {
					JOptionPane.showMessageDialog(EQ.this, "不能发送空消息");
				}
			}else {
				JOptionPane.showMessageDialog(EQ.this, "此功能只能在Windows7环境使用!", "提示", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	/**
	 * 系统栏初始化
	 */
	private void SystemTrayInitial() {
		if (!SystemTray.isSupported()) {
			return;
		}
		try {
			String title = "企业QQ";
			String company = "四川省XXX有限公司";
			SystemTray sysTray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage
					(EQ.class.getResource("/icons/sysTray.png"));
			trayicon = new TrayIcon(image, title + "\n" + company, createMenu());
			trayicon.setImageAutoSize(true);
			trayicon.addActionListener(new SysTrayActionListener());
			sysTray.add(trayicon);
			trayicon.displayMessage(title, company, MessageType.INFO);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 创建最小化菜单栏
	 * 
	 * @return
	 */
	private PopupMenu createMenu() {
		PopupMenu menu = new PopupMenu();
		MenuItem exitltem = new MenuItem("退出");
		exitltem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		MenuItem openItem = new MenuItem("打开");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO 自动生成的方法存根
				if (!isVisible()) {
					setVisible(true);
					toFront();
				}else {
					toFront();
				}
			}
		});
		menu.add(openItem);
		menu.addSeparator();
		menu.add(exitltem);
		return menu;
	}

	/**
	 * 最小化系统栏双击事件
	 * 
	 */
	class SysTrayActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			setVisible(true);
			toFront();
		}
	}
}