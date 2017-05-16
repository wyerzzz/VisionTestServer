import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.SystemColor;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainServer extends JFrame {

	private static ServerSocket serverSocket;
	private static Socket socket;
	private static BufferedReader serverR;
	private static BufferedWriter serverW;

	private JPanel contentPane;
	private static JLabel lb_show;
	private static JLabel lb_direction;
	private static JLabel lb_serverMsg;
	private static JButton b_reset;
	private static JLabel lb_vision;

	private static String[] picE = {
		"/drawable/eUp.png",
		"/drawable/eDown.png", 
		"/drawable/eLeft.png",
		"/drawable/eRight.png"};
	
	private static int indexE, correctTimes = 0, wrongTimes = 0, picSize = 1;
	private static float yourVision = 0.1f;

	// 上下左右 0123

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			MainServer frame = new MainServer();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			serverSocket = new ServerSocket(5055);
			while (!serverSocket.isClosed()) {
				System.out.println("server is waiting");
				lb_serverMsg.setText("Server Message : Server is waiting ...");
				waitUser();
				Reset();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void waitUser(){
		try {
			socket = serverSocket.accept();
		System.out.println("a new client connection");
		lb_serverMsg.setText("Server Message : The user is connected.");

		serverR = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		serverW = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream()));

		while (socket.isConnected()) {
			int result = serverR.read();
			if(result == -1){// 表示串流已經讀到底 沒有資料了 必須跳出
				break;
			}
			visionJudge(result);
			System.out.println(result);
		}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("IOE");
			e.printStackTrace();
		}
	}
	
	private static void showE(int size) {
		// TODO Auto-generated method stub
		lb_show.setIcon(new ImageIcon(MainServer.class.getResource("/drawable/ww.jpg")));
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("showE:sleep");
			e.printStackTrace();
		}
		indexE = (int) (Math.random() * (picE.length));
		ImageIcon tempIcon = new ImageIcon(
				MainServer.class.getResource(picE[indexE]));
		//取得Icon圖片並重新設定寬高,放入新的Icon中
		Image tempImg = tempIcon.getImage().getScaledInstance(
				tempIcon.getIconWidth() / size, 
				tempIcon.getIconHeight() / size,
				java.awt.Image.SCALE_SMOOTH);
		ImageIcon imgIcon = new ImageIcon(tempImg);
		lb_show.setIcon(imgIcon);
		lb_vision.setText(String.format("%.1f", yourVision));
	}

	private static void visionJudge(int ans) {
		if (ans == 5) {//start
			showE(picSize);
			lb_direction.setText("Test Start.");
		} else {//judge answer
			if (ans == indexE) {// correct
				correctTimes++;
				if (correctTimes == 3) {
					yourVision += 0.1f;
					picSize++;
					correctTimes = 0;
					wrongTimes = 0;
				}
				showE(picSize);
			} else {// wrong
				wrongTimes++;
				if (wrongTimes == 3) {
					try {
						String str = "";
						if(yourVision<=0.1f){
							str = " < 0.1";
						}else{
							float f = (Float.parseFloat(lb_vision.getText())) - 0.1f;
							str = String.format("%.1f", f);
						}
						serverW.write(str, 0, str.length());
						serverW.flush();
						System.out.println("write : "+str);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Reset();
					return;
				}
				showE(picSize);
			}
		}
	}
	private static void Reset(){
		lb_direction.setText("Press the \"Start\" button to begin.");
		lb_show.setIcon(new ImageIcon(MainServer.class.getResource("/drawable/ww.jpg")));
		lb_vision.setText("");
		correctTimes = 0;
		wrongTimes = 0;
		yourVision = 0.1f;
		picSize = 1;
		System.out.println("reset------------");
	}

	/**
	 * Create the frame.
	 */
	public MainServer() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 50, 800, 800);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.activeCaption);
		panel.setBounds(10, 10, 774, 189);
		contentPane.add(panel);
		panel.setLayout(null);

		lb_serverMsg = new JLabel(
				"Server Message : Server is waiting ...");
		lb_serverMsg.setBounds(10, 152, 399, 27);
		panel.add(lb_serverMsg);
		lb_serverMsg.setFont(new Font("Vrinda", Font.PLAIN, 18));
		JLabel lb_ip;
		try {
			lb_ip = new JLabel("IP: "
					+ InetAddress.getLocalHost().getHostAddress());
			lb_ip.setBounds(10, 107, 399, 35);
			panel.add(lb_ip);
			lb_ip.setFont(new Font("Verdana", Font.BOLD, 30));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JLabel lb_title = new JLabel("DrVision_Server");
		lb_title.setBackground(SystemColor.info);
		lb_title.setHorizontalAlignment(SwingConstants.CENTER);
		lb_title.setFont(new Font("Vani", Font.BOLD, 40));
		lb_title.setBounds(119, 21, 525, 76);
		panel.add(lb_title);

		b_reset = new JButton("Reset");
		b_reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Reset();
			}
		});
		b_reset.setBounds(646, 129, 118, 50);
		panel.add(b_reset);
		b_reset.setBackground(UIManager.getColor("Button.darkShadow"));
		b_reset.setForeground(Color.DARK_GRAY);
		b_reset.setFont(new Font("Arial Black", Font.PLAIN, 24));

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(10, 21, 754, 60);
		panel.add(lblNewLabel_1);
		lblNewLabel_1.setOpaque(true);
		lblNewLabel_1.setBackground(UIManager
				.getColor("List.selectionBackground"));
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 20));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(10, 209, 774, 553);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		lb_show = new JLabel(new ImageIcon(MainServer.class.getResource("/drawable/ww.jpg")),
				SwingConstants.CENTER);
		lb_show.setBounds(194, 100, 400, 400);
		panel_1.add(lb_show);
		lb_show.setForeground(new Color(0, 0, 0));
		lb_show.setBackground(Color.WHITE);

		lb_direction = new JLabel("Press the \"Start\" button to begin.");
		lb_direction.setBounds(10, 10, 363, 35);
		panel_1.add(lb_direction);
		lb_direction.setFont(new Font("Vrinda", Font.PLAIN, 24));
		
		lb_vision = new JLabel("");
		lb_vision.setHorizontalAlignment(SwingConstants.CENTER);
		lb_vision.setFont(new Font("Vrinda", Font.PLAIN, 22));
		lb_vision.setBounds(666, 11, 98, 35);
		panel_1.add(lb_vision);

	}
}
