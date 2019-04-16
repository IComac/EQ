package com.EQ.frame;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.EQ.EQ;

/**
 * 截图工具类，此类本身是一个JWindow窗体，窗体会覆盖整个屏幕，
 * 覆盖之前会捕获当期桌面的影响图片，但所有的操作其实是在此窗体上的
 * @author IComac
 *
 */
public class CaptureScreenUtil extends JWindow{
	private int startX, startY;
	private int endX, endy;
	private BufferedImage screenImage = null;
	private BufferedImage tempImage = null;
	private BufferedImage saveImage = null;
	private ToolsWindow toolWindow = null;
	private Toolkit tool = null;
	
	public CaptureScreenUtil() {
		tool = Toolkit.getDefaultToolkit();
		Dimension d = tool.getScreenSize();
		setBounds(0, 0, d.width, d.height);
		
		Robot robot;
		try {
			robot = new Robot();
			Rectangle fanwei = new Rectangle(0, 0, d.width, d.height);
			screenImage = robot.createScreenCapture(fanwei);
			addAction();
			setVisible(true);
		} catch (AWTException e) {
			// TODO: handle exception
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "截图功能无法使用", "错误", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * 添加动作监听
	 */
	private void addAction() {
		//截图窗体添加鼠标事件监听
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO 自动生成的方法存根
				startX = e.getX();
				startY = e.getY();
				
				if (toolWindow != null) {
					toolWindow.setVisible(false);
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if (toolWindow == null) {
					toolWindow = new ToolsWindow(e.getX(), e.getY());
				}else {
					toolWindow.setLocation(e.getX(), e.getY());
				}
				toolWindow.setVisible(true);
				toolWindow.toFront();
			}
		});
		//截图窗体添加鼠标拖拽事件监听
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				endX = e.getX();
				endy = e.getY();
				Image backgroundImage = createImage(getWidth(), getHeight());
				Graphics g = backgroundImage.getGraphics();
				g.drawImage(tempImage, 0, 0, null);
				int x = Math.min(startX, endX);
				int y = Math.min(startY, endy);
				int width = Math.abs(endX - startX);
				int height = Math.abs(endy - startY);
				g.setColor(Color.BLUE);
				g.drawRect(x - 1, y - 1, width + 1, height + 1);
				saveImage = screenImage.getSubimage(x, y, width, height);
				g.drawImage(saveImage, x, y, null);
				getGraphics().drawImage(backgroundImage, 0, 0, CaptureScreenUtil.this);
			}
		});
	}
	
	/**
	 * 绘制组件
	 */
	public void paint(Graphics g) {
		RescaleOp ro = new RescaleOp(0.5f, 0, null);
		tempImage = ro.filter(screenImage, null);
		g.drawImage(tempImage, 0, 0, this);
	}
	
	/**
	 * 将当前截图保存到本地
	 */
	public void saveImage() {
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("保存图片");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG", "jpg");
		jfc.setFileFilter(filter);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
		String fileName = sdf.format(new Date());
		FileSystemView view = FileSystemView.getFileSystemView();
		File filepath = view.getHomeDirectory();
		File saveFile = new File(filepath, fileName + ".jpg");
		jfc.setSelectedFile(saveFile);
		int flag = jfc.showSaveDialog(this);
		if (flag == JFileChooser.APPROVE_OPTION) {
			try {
				ImageIO.write(saveImage, "jpg", saveFile);
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "文件无法保存", "错误", JOptionPane.ERROR);
			} finally {
				disposeAll();
			}
		}
	}
	
	/**
	 * 把截图放入剪切板
	 */
	private void imagetoClipboard() {
		//创建传输接口的对象，使用接口本身创建内部类
		Transferable trans = new Transferable() {
			@Override
			/**
			 * 返回将要被传输的对象的数据特征数组
			 */
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[]{DataFlavor.imageFlavor};
			}
			
			@Override
			/**
			 * 判断参数传入的特征是否符合我们要求的特征
			 */
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return DataFlavor.imageFlavor.equals(flavor);
			}
			
			@Override
			/**
			 * 返回将要被传输的对象
			 */
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException{
				if (isDataFlavorSupported(flavor)) {
					return saveImage;
				}
				return null;
			}
		};
		Clipboard clipboard = tool.getSystemClipboard();
		clipboard.setContents(trans, null);
	}
	
	/**
	 * 工具栏窗体
	 */
	private class ToolsWindow extends JWindow{
		/**
		 * 工具栏窗体构造方法
		 * 
		 * @param x
		 * 		-工具栏显示的横坐标
		 * @param y
		 * 		-工具栏显示的纵坐标
		 */
		public ToolsWindow(int x, int y) {
			setLocation(x, y);
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			JToolBar toolBar = new JToolBar();
			toolBar.setFloatable(false);
			JButton saveButton = new JButton();
			Icon saveIcon = new ImageIcon(EQ.class.getResource("/image/telFrameImage/CaptureScreen/save.png"));
			saveButton.setIcon(saveIcon);
			saveButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO 自动生成的方法存根
					saveImage();
				}
			});
			toolBar.add(saveButton);
			JButton closeButton = new JButton();
			Icon closeIcon = new ImageIcon(EQ.class.getResource("/image/telFrameImage/CaptureScreen/close.png"));
			closeButton.setIcon(closeIcon);
			closeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO 自动生成的方法存根
					disposeAll();
				}
			});
			toolBar.add(closeButton);
			JButton copyButton = new JButton();
			Icon copyIcon = new ImageIcon(EQ.class.getResource("/image/telFrameImage/CaptureScreen/copy.png"));
			copyButton.setIcon(copyIcon);
			copyButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO 自动生成的方法存根
					imagetoClipboard();
					disposeAll();
				}
			});
			toolBar.add(copyButton);
			mainPanel.add(toolBar, BorderLayout.NORTH);
			setContentPane(mainPanel);
			pack();
			setVisible(true);
		}
	}
	
	/**
	 * 销毁所有截图窗体
	 */
	public void disposeAll() {
		toolWindow.dispose();
		dispose();
	}
	
	public static void main(String[] args) {
		new CaptureScreenUtil();
	}
}