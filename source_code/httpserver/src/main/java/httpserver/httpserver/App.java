package httpserver.httpserver;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Enumeration;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;

import java.nio.file.*;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;



public class App implements Runnable {

    ServerSocket serverSocket;//服务器Socket
    public static int PORT=8080;//监听8080端口
    private static final String QR_CODE_IMAGE_PATH = "src/main/MyQRCode.png";
     // 开始服务器 Socket 线程.
    public App() {
    	
        try {
            serverSocket=new ServerSocket(PORT);
        } catch(Exception e) {
            System.out.println("无法启动HTTP服务器:"+e.getLocalizedMessage());
        }
        if(serverSocket==null)  System.exit(1);//无法开始服务器
        
		System.out.println("本机IP:" + getLANAddressOnWindows());
		changeIP();
        String host = null;
		host = "http://"+getLANAddressOnWindows()+":8080";
        new Thread(this).start();
        try {
            createQRcode(host, 350, 350, QR_CODE_IMAGE_PATH);
        } catch (WriterException e) {
            System.out.println("Could not generate QR Code, WriterException :: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Could not generate QR Code, IOException :: " + e.getMessage());
        }

        createWindow();
        System.out.println("HTTP服务器正在运行,端口:"+PORT);
    }
    public static void createWindow() {    
        JFrame frame = new JFrame("Online board v1");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createUI(frame);
        frame.setSize(800, 450);      
        frame.setLocationRelativeTo(null);  
        frame.setVisible(true);
     }

     public static void createUI(final JFrame frame){  
    	 frame.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        ImageIcon img = new ImageIcon("src/main/MyQRCode.png");//创建图片对象
        label.setIcon(img);
//        panel.add(label);
        
//        panel.add(Box.createVerticalStrut(20));
        JButton button2 = new JButton("用户界面");    //创建JButton对象
        button2.setFont(new Font("黑体",Font.BOLD,30));    //修改字体样式
        button2.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	openExplorerURL("http://"+getLANAddressOnWindows()+":8080");
            }
        });
        button2.setPreferredSize(new Dimension(150, 60));
        
        JTextField txtfield1=new JTextField();    //创建文本框
        txtfield1.setText("http://"+getLANAddressOnWindows()+":8080");    //设置文本框的内容
        txtfield1.setFont(new java.awt.Font("Dialog", 1, 30));
        txtfield1.setHorizontalAlignment(JTextField.CENTER);
        java.awt.Dimension dm = new java.awt.Dimension(280, 30);
        txtfield1.setPreferredSize(dm);
        panel.add(txtfield1);
        JButton button1 = new JButton("管理界面");    //创建JButton对象
        button1.setFont(new Font("黑体",Font.BOLD,30));    //修改字体样式
        button1.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	openExplorer("src/main/superuser.html");
            }
        });
        button1.setPreferredSize(new Dimension(150, 60));
        
        
        
//        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        
        frame.add(button2,BorderLayout.NORTH);
        frame.add(txtfield1,BorderLayout.CENTER);
        frame.add(button1,BorderLayout.SOUTH);
        frame.add(label,BorderLayout.WEST);
//        frame.add(panel);

     }  
     public static void openExplorerURL(String url) {

    	 if (java.awt.Desktop.isDesktopSupported()) {

    	 try {

    	 try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	 } catch (IOException e) {

    	 e.printStackTrace();

    	 }

    	 }

    	 }
     public static void openExplorer(String htmlFile) {

    	 if (java.awt.Desktop.isDesktopSupported()) {

    	 try {

    	 Desktop.getDesktop().open(new File(htmlFile));

    	 } catch (IOException e) {

    	 e.printStackTrace();

    	 }

    	 }

    	 }
    public static void createQRcode(String text, int width, int height, String filePath) throws WriterException, IOException {
    	QRCodeWriter qrCodeWriter = new QRCodeWriter();
		
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
		
		Path path = FileSystems.getDefault().getPath(filePath);
		
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }
    
    private static String getLinuxLocalIp() throws SocketException {
		String ip = "";
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				String name = intf.getName();
				if (!name.contains("docker") && !name.contains("lo")) {
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String ipaddress = inetAddress.getHostAddress().toString();
							if (!ipaddress.contains("::") && !ipaddress.contains("0:0:")
									&& !ipaddress.contains("fe80")) {
								ip = ipaddress;
							}
						}
					}
				}
			}
		} catch (SocketException ex) {
			ip = "127.0.0.1";
			ex.printStackTrace();
		}
		return ip;
	}
    public static boolean isWindowsOS() {
		boolean isWindowsOS = false;
		// 注：这里的system，系统指的是 JRE (runtime)system，不是指 OS
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("windows") > -1) {
			isWindowsOS = true;
		}
		return isWindowsOS;
	}
    public static String getLocalIP() {
		if (isWindowsOS()) {
			try {
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				return getLinuxLocalIp();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static String getLANAddressOnWindows() {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();

                if (nif.getName().startsWith("wlan")) {
                    Enumeration<InetAddress> addresses = nif.getInetAddresses();

                    while (addresses.hasMoreElements()) {

                        InetAddress addr = addresses.nextElement();
                        if (addr.getAddress().length == 4) { // 速度快于 instanceof
                        	System.out.print(addr.toString().substring(1));
                            return addr.toString().substring(1);
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    public static String getIpAddress() {
	    try {
	      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	      InetAddress ip = null;
	      while (allNetInterfaces.hasMoreElements()) {
	        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
	        if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
	          continue;
	        } else {
	          Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
	          while (addresses.hasMoreElements()) {
	            ip = addresses.nextElement();
	            if (ip != null && ip instanceof Inet4Address ) {
	              return ip.getHostAddress();
	            }
	          }
	        }
	      }
	    } catch (Exception e) {
	    	System.err.println("IP地址获取失败" + e.toString());
	    }
	    return "";
	  }
    public void changeIP() {

        
        FileReader reader;
		try {
			reader = new FileReader("src/main/hello.html");
			BufferedReader br = new BufferedReader(reader);
			CharArrayWriter  tempStream = new CharArrayWriter();  
	        String str = null;
	       
	        try {
	        	System.out.println("yes");
				while((str = br.readLine()) != null) {
				     if (str.contains("8089")) {
				    	 //socket = new WebSocket("ws://localhost:8089");
				    	 String[] stri = str.split("//");
				    	 String[] strii = stri[1].split(":");
				    	 tempStream.write(stri[0]+"//"+getLANAddressOnWindows()+":"+strii[1]);
				    	 tempStream.append(System.getProperty("line.separator"));
				     }else {
				    	 tempStream.write(str);
				    	 tempStream.append(System.getProperty("line.separator"));
				     }
				}
				br.close();
		        reader.close();
		        
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        FileWriter out;
			try {
				File file = new File("src/main/index.html"); 
				out = new FileWriter(file);
				System.out.println("no");
				tempStream.writeTo(out);  
		        out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        
	       
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }

     // 运行服务器主线程, 监听客户端请求并返回响应.
    public void run() {
        while(true) {
            try {
                Socket client=null;//客户Socket
                client=serverSocket.accept();//客户机(这里是 IE 等浏览器)已经连接到当前服务器
                if(client!=null) {
                    System.out.println("连接到服务器的用户:"+client);
                    try {
                        // 第一阶段: 打开输入流
                        BufferedReader in=new BufferedReader(new InputStreamReader(
                                client.getInputStream()));
                        
                        System.out.println("客户端发送的请求信息: ***************");
                        // 读取第一行, 请求地址
                        String line=in.readLine();
                        System.out.println(line);
                        String resource=line.substring(line.indexOf('/')+1,line.lastIndexOf('/')-5);
                        //获得请求的资源的地址
                        resource=URLDecoder.decode(resource, "UTF-8");//反编码 URL 地址
                        String method = new StringTokenizer(line).nextElement().toString();// 获取请求方法, GET 或者 POST

                        // 读取所有浏览器发送过来的请求参数头部信息
                        while( (line = in.readLine()) != null) {
//                            System.out.println(line);
                            if(line.equals("")) break;  //读到尾部即跳出
                        }
                        
                        System.out.println("请求信息结束 ***************");
                        System.out.println("用户请求的资源是:"+resource);
                        System.out.println("请求的类型是: " + method);

                        //如果请求的是空则返回首页
                        if(resource.equals("")&&method.equals("GET")){
                            String ConTentType="Content-Type: text/html;charset=UTF-8"; //发送文本形式的首页
                            fileService("src/main/index.html",client,ConTentType);
                            closeSocket(client);
                        }
                        if(resource.contains(".html")&&method.equals("GET")){
                            String ConTentType="Content-Type: text/html;charset=UTF-8"; //发送文本形式的首页
                            fileService("src/main/"+resource,client,ConTentType);
                            closeSocket(client);
                        }
                        //如果请求的是js文件则按照js返回
                        if(resource.endsWith(".js")&&method.equals("GET")) {
                            String ConTentType="Content-Type: application/javascript;charset=UTF-8";  //js的内容发送表明类型
                            fileService("src/main/"+resource, client,ConTentType);
                            closeSocket(client);
                            continue;
                        }    
                    } catch(Exception e) {
                        System.out.println("HTTP服务器错误:"+e.getLocalizedMessage());
                    }
                }
                //System.out.println(client+"连接到HTTP服务器");//如果加入这一句,服务器响应速度会很慢
            } catch(Exception e) {
                System.out.println("HTTP服务器错误:"+e.getLocalizedMessage());
            }
        }
    }
    
    
     //关闭客户端 socket 并打印一条调试信息.
    void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println(socket + "离开了HTTP服务器");        
    }
    
    /**
     * 读取一个文件的内容并返回给浏览器端.
     * @param fileName 文件名
     * @param socket 客户端 socket.
     */
    void fileService(String fileName, Socket socket,String ConTentType)
    {
        try
        {
        	System.out.println("Filename:" + fileName);
        	File fileToSend;
        	boolean status = fileName.contains("?");
        	System.out.println("bool:" + status);
        	if (status) {
        		String[] ff = fileName.split("\\?");
        		System.out.println("传送文件时出错:" + ff[0]);
        		fileToSend = new File(ff[0]);
        	}
        	else {
        		fileToSend = new File(fileName);
        	}
        	
        	
            PrintStream out = new PrintStream(socket.getOutputStream(), true);
            
            if(fileToSend.exists() && !fileToSend.isDirectory())
            {
                out.println("HTTP/1.0 200 OK");//返回应答消息,并结束应答
                out.println(ConTentType);   //返回文件的格式
                out.println("Content-Length: " + fileToSend.length());// 返回内容字节数
                out.println();// 根据 HTTP 协议, 空行将结束头信息

                FileInputStream fis = new FileInputStream(fileToSend);
                byte data[] = new byte[fis.available()];
                fis.read(data);
                out.write(data);
                out.close();
                fis.close();
            }
        }catch(Exception e){
            System.out.println("传送文件时出错:" + e.getLocalizedMessage());
        }
    }
    
    //命令行打印用途说明.
    private static void usage() {
        System.out.println("Usage: java HTTPServer <port> Default port is 80.");
    }

     


    /**
     * 启动简易 HTTP 服务器
     */
    public static void main(String[] args) {
        try {
            if(args.length != 1) {
                usage();
            } else if(args.length == 1) {
                PORT = Integer.parseInt(args[0]);
            }
        } catch (Exception ex) {
            System.err.println("Invalid port arguments. It must be a integer that greater than 0");
        }
        
        new App();   //创建一个
        
    }
}