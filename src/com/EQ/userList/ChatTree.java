package com.EQ.userList;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.EQ.EQ;
import com.EQ.dao.Dao;

/**
 * 用户树
 * @author Administration
 *
 */
public class ChatTree extends JTree {
	private DefaultMutableTreeNode root;
	private DefaultTreeModel treeModel;
	private List<User> userMap;
	private Dao dao;
	private EQ eq;

	public ChatTree(EQ eq) {
		super();
		root = new DefaultMutableTreeNode("root");
		treeModel = new DefaultTreeModel(root);
		userMap = new ArrayList<User>();
		dao = Dao.getDao();
		addMouseListener(new ThisMouseListener());
		setRowHeight(50);
		setToggleClickCount(2);
		setRootVisible(false);
		DefaultTreeCellRenderer defaultRanderer = new DefaultTreeCellRenderer();
		UserTreeRanderer treeRanderer = new UserTreeRanderer(
				defaultRanderer.getOpenIcon(),
				defaultRanderer.getClosedIcon(),
				defaultRanderer.getLeafIcon());
		setCellRenderer(treeRanderer);
		setModel(treeModel);
		sortUsers();
		this.eq = eq;
	}

	/**
	 * 排序用户列表
	 */
	private synchronized void sortUsers() {
		new Thread(new Runnable() {
			public void run() {
				// TODO 自动生成的方法存根
				try {
					Thread.sleep(100);
					root.removeAllChildren();
					String ip = InetAddress.getLocalHost().getHostAddress();
					User localUser = dao.getUser(ip);
					if (localUser != null) {
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(localUser);
						root.add(node);
					}
					userMap = dao.getUsers();
					Iterator<User> iterator = userMap.iterator();
					while (iterator.hasNext()) {
						User user = iterator.next();
						if (user.getIp().equals(localUser.getIp())) {
							continue;
						}
						root.add(new DefaultMutableTreeNode(user));
					}
					treeModel.reload();
					ChatTree.this.setSelectionRow(0);
					if (eq != null) {
						eq.setStatic("总人数: " + getRowCount());
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 删除用户
	 */
	public void delUser() {
		TreePath path = getSelectionPath();
		if (path == null) {
			return;
		}
		User user = (User)((DefaultMutableTreeNode)path
				.getLastPathComponent()).getUserObject();
		int operation = JOptionPane.showConfirmDialog(this, "确定要删除用户: " + user
				+ "?", "删除用户", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (operation == JOptionPane.YES_OPTION) {
			dao.delUser(user);
			root.remove((DefaultMutableTreeNode) path.getLastPathComponent());
			treeModel.reload();
		}
	}

	/**
	 * 添加用户
	 * 
	 * @param ip
	 * 		-用户IP
	 * @param operation
	 * 		-调用此方法的属于那种业务
	 * @return -是否添加成功
	 */
	public boolean addUser(String ip, String operation) {
		try {
			if (ip == null) {
				return false;
			}
			User oldUser = dao.getUser(ip);
			if (oldUser == null) {
				InetAddress addr = InetAddress.getByName(ip);
				if (addr.isReachable(1500)) {
					String host = addr.getHostName();
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(new User(host, ip));
					root.add(newNode);
					User newUser = new User();
					newUser.setIp(ip);
					newUser.setHost(host);
					newUser.setName(host);
					newUser.setIcon("1.gif");
					dao.addUser(newUser);
					sortUsers();
					if (!operation.equals("search")) {
						JOptionPane.showMessageDialog(EQ.frame, "用户" + host + "添加成功", "添加用户", JOptionPane.INFORMATION_MESSAGE);
					}
					return true;
				}else {
					if (!operation.equals("search")) {
						JOptionPane.showMessageDialog(EQ.frame, "检测不到用户IP: " + ip, "错误添加用户", JOptionPane.ERROR_MESSAGE);
					}
					return false;
				}
			}else {
				if (!operation.equals("search")) {
					JOptionPane.showMessageDialog(EQ.frame, "已经存在用户IP" + ip, "不能添加用户",JOptionPane.WARNING_MESSAGE);
				}
				return false;
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取用户树节点数据模型
	 * 
	 * @return 用户树节点数据模型
	 */
	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	/**
	 * 自定义鼠标监听事件
	 * 
	 */
	private class ThisMouseListener extends MouseAdapter{
		public void mousePressed(final MouseEvent e) {
			if(e.getButton() == 3) {
				TreePath path = getPathForLocation(e.getX(), e.getY());
				if(!isPathSelected(path)) {
					setSelectionPath(path);
				}
			}
		}
	}
}
