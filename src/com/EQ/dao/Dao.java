package com.EQ.dao;

import java.awt.Rectangle;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import com.EQ.userList.User;

/**
 * ���ݿ�ӿ��࣬����ʹ��΢�����ݿ⡪��Derby���������ݿ��ļ��ᱣ������Ŀ��Ŀ¼���Զ����ɵ�"db_EQ"�ļ�����
 */
public class Dao {
	private static final String drive = 
			"org.apache.derby.jdbc.EmbeddedDriver";
	private static String url = "jdbc:derby:db_EQ";
	private static Connection conn = null;
	private static Dao dao = null;
	private Dao() {
		try {
			Class.forName(drive);
			if(!dbExists()) {
				conn = DriverManager.
						getConnection(url + ";create=ture");
				createTable();
			} else
				conn = DriverManager.getConnection(url);
			addDefuser();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "���ݿ������쳣�����߱�����Ѿ����С�");
			System.exit(0);
		}
	}

	/**
	 * �������ݿ��Ƿ����
	 * @return ���ݿ��Ƿ����
	 */
	private boolean dbExists() {
		// TODO �Զ����ɵķ������
		boolean bExists = false;
		File dbFileDir = new File("db_EQ");
		if (dbFileDir.exists()) {
			bExists = true;
		}
		return bExists;
	}

	/**
	 * �������ݱ��
	 */
	private void createTable() {
		// TODO �Զ����ɵķ������
		String createUserSql = "CREATE TABLE tb_users ("
				+ "ip varchar(16) primary key," + "host varchar(30),"
				+ "name varchar(20)," + "tooltip varchar(50),"
				+ "icon varchar(50))";
		String createLocationSql = "CREATE TABEL tb_location ("
				+ "xLocation int," + "yLocation int," + "width int,"
				+ "height int)";
		try {
			java.sql.Statement stmt = conn.createStatement();
			stmt.execute(createUserSql);
			stmt.execute(createLocationSql);
			addDefLocation();
			stmt.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡDAO����
	 * @return DAO����
	 */
	public static Dao getDao() {
		if (dao == null) 
			dao = new Dao();
		return dao;
	}

	/**
	 * ��ȡ�����û�
	 * @return ��ȡ�����û��ļ���
	 */
	public List<User> getUsers() {
		List<User> users = new ArrayList<User>();
		try {
			String sql = "select * from tb_users";
			java.sql.Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(sql);
			while(rs.next()) {
				User user = new User();
				user.setIp(rs.getString(1));
				user.setHost(rs.getString(2));
				user.setName(rs.getString(3));
				user.setTipText(rs.getString(4));
				user.setIcon(rs.getString(5));
				users.add(user);
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return users;
	}

	/**
	 * ��ȡָ��IP���û�
	 * @param ip 
	 * 		-IP��ַ
	 * @return �鿴���û���Ϣ
	 */
	public User getUser(String ip) {
		String sql = "select * from tb_users where ip=?";
		User user = null;
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, ip);
			ResultSet rs = pst.executeQuery();
			if(rs.next()) {
				user = new User();
				user.setIp(rs.getString(1));
				user.setHost(rs.getString(2));
				user.setName(rs.getString(3));
				user.setTipText(rs.getString(4));
				user.setIcon(rs.getString(5));
			}
			rs.close();					
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * ����û�
	 * @param user 
	 * 		-����ӵ��û�
	 */
	public void addUser(User user) {
		try {
			String sql = "insert into tb_users values(?,?,?,?)";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getIp());
			ps.setString(2, user.getHost());
			ps.setString(3, user.getName());
			ps.setString(4, user.getTipText());
			ps.setString(5, user.getIcon());
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * �޸��û�
	 * @param user 
	 * 		-���޸ĵ��û�
	 */
	public void updateUser(User user) {
		try {
			String sql = "update tb_users set host=?,name=?,tooltip=?,icon=?,where ip='"
					+ user.getIp() + "'";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getHost());
			ps.setString(2, user.getName());
			ps.setString(3, user.getTipText());
			ps.setString(4, user.getIcon());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * ɾ���û�
	 * @param user 
	 * 		-��ɾ�����û�
	 */
	public void delUser(User user) {
		try {
			String sql = "delete from tb_users where ip=?";
			PreparedStatement ps = null;
			ps = conn.prepareStatement(sql);
			ps.setString(1, user.getIp());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * ��������������Ļ����ʾ��λ��
	 * @param location 
	 * 		-��Ļλ�ö���
	 */
	public void updateLocation(Rectangle location) {
		String sql = "update tb_location set xLocation=?,yLocation=?,width=?,height=?";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, location.x);
			pst.setInt(2, location.y);
			pst.setInt(3, location.width);
			pst.setInt(4, location.height);
			pst.executeUpdate();
			pst.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ����λ��
	 * @return ��Ļλ�ö���
	 */
	public Rectangle getLocation() {
		Rectangle rec = new Rectangle(100, 0, 240, 500);
		String sql = "select * from tb_location";
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				rec.x = rs.getInt(1);
				rec.y = rs.getInt(2);
				rec.width = rs.getInt(3);
				rec.height = rs.getInt(4);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return rec;
	}

	/**
	 * ��Ӵ���Ĭ��λ��
	 */
	private void addDefLocation() {
		// TODO �Զ����ɵķ������
		String sql = "insert into tb_location values(?,?,?,?)";
		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setInt(1, 100);
			pst.setInt(2, 0);
			pst.setInt(3, 240);
			pst.setInt(4, 500);
			pst.executeUpdate();
			pst.close();

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	/**
	 * ʹ�ñ�����IP��ַ����Ĭ�ϵ��û�����������ӵ����ݿ���
	 */
	private void addDefuser() {
		// TODO �Զ����ɵķ������
		try {
			InetAddress local = InetAddress.getLocalHost();
			User user = new User();
			user.setIp(local.getHostAddress());
			user.setHost(local.getHostName());
			user.setName(local.getHostName());
			user.setTipText(local.getHostAddress());
			user.setIcon("1.gif");
			if (getUser(user.getIp()) == null) {
				addUser(user);
			}

		} catch (UnknownHostException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
