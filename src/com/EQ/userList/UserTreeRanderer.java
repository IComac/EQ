package com.EQ.userList;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import com.EQ.EQ;

/**
 * 用户(树)节点面板类
 *
 */
public class UserTreeRanderer extends JPanel implements TreeCellRenderer {
	private Icon openIcon;
	private Icon closedIcon;
	private Icon leafIcon;
	private String tipText = "";
	private final JCheckBox label = new JCheckBox();
	private final JLabel headImg = new JLabel();
	private static User user;

	public UserTreeRanderer() {
		super();
		user = null;
	}

	/**
	 * 用户(数)节点面板类构造方法
	 * 
	 * @param open
	 * 		-节点展开时的图标
	 * @param closed
	 * 		-节点关闭时的图标
	 * @param leaf
	 * 		-节点默认图标
	 */
	public UserTreeRanderer(Icon open, Icon closed, Icon leaf) {
		openIcon = open;
		closedIcon = closed;
		leafIcon = leaf;
		setBackground(new Color(0xF5B9BF));
		label.setFont(new Font("宋体", Font.BOLD, 14));
		URL trueUrl = EQ.class.getResource("/image/chexkBoxImg/CheckBoxTrue.png");
		label.setSelectedIcon(new ImageIcon(trueUrl));
		URL falseUrl = EQ.class.getResource("/image/chexkBoxImg/CheckBoxFalse.png");
		label.setIcon(new ImageIcon(falseUrl));
		label.setForeground(new Color(0, 64, 128));
		final BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		user = null;
	}

	/**
	 * @param tree
	 * @param value
	 * 		-当前树单元格的值
	 * @param selected
	 * 		-单元格已选择
	 * @param expanded
	 * 		-是否当前扩展该节点
	 * @param leaf
	 * 		-该节点是否为叶节点
	 * @param row
	 * 		-放置位置
	 * @param hasFocus
	 * 		-该节点是否拥有焦点
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (value instanceof DefaultMutableTreeNode) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
			Object uo = node.getUserObject();
			if (uo instanceof User) {
				user = (User)uo;
			}
		}else if (value instanceof User) {
			user = (User)value;
		}
		if (user != null && user.getIcon() != null) {
			int width = EQ.frame.getWidth();
			if (width > 0) {
				setPreferredSize(new Dimension(width, user.getIconImg().getIconHeight()));
			}
			headImg.setIcon(user.getIconImg());
			tipText = user.getName();
		}else {
			if(expanded) {
				headImg.setIcon(openIcon);
			}else if (leaf) {
				headImg.setIcon(leafIcon);
			}else {
				headImg.setIcon(closedIcon);
			}
		}
		add(headImg, BorderLayout.WEST);
		label.setText(value.toString());
		label.setOpaque(false);
		add(label, BorderLayout.CENTER);
		if (selected) {
			label.setSelected(true);
			setBorder(new LineBorder(new Color(0xD46D73), 2, false));
			setOpaque(true);
		}else {
			setOpaque(false);
			label.setSelected(false);
			setBorder(new LineBorder(new Color(0xD46D73), 0, false));
		}
		return this;
	}

	/**
	 * 获取提示内容
	 */
	public String getToolTipText() {
		return tipText;
	}
}