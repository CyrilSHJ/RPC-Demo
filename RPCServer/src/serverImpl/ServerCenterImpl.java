/**
 * 
 */
package serverImpl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import server.ServerCenter;

/**
 * @author Cyril 下午9:19:54
 */
public class ServerCenterImpl implements ServerCenter {

	private final int port = 12000;
	@SuppressWarnings("rawtypes")
	private static HashMap<String, Class> register = new HashMap<>();
	// 连接池：连接池中存在多个连接对象，每个对象都可以处理一个客户请求
	private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	// 是否启动线程
	private static boolean isRunning = false;

	@SuppressWarnings("resource")
	@Override
	public void start() {
		ServerSocket server = null;
		try {
			server = new ServerSocket();
			server.bind(new InetSocketAddress(port));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		isRunning = true;
		System.out.println("开启服务线程...");
		while (true) {
			Socket socket = null;
			try {
				socket = server.accept();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			executor.execute(new ServiceTask(socket));
		}

	}

	// 关停请求线程
	@Override
	public void stop() {
		// 如果有线程运行，则关闭
		if (isRunning) {
			executor.shutdown();
		}

	}

	// 注册服务
	@Override
	public void register(Class service, Class serviceImpl) {
		register.put(service.getName(), serviceImpl);

	}

	private static class ServiceTask implements Runnable {

		private Socket socket;

		public ServiceTask(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			ObjectInputStream input = null;
			ObjectOutputStream output = null;
			try {

				// 等待客户端连接
				input = new ObjectInputStream(socket.getInputStream());

				// 序列流需要顺序接收解析
				String serviceName = input.readUTF();
				String methodName = input.readUTF();
				@SuppressWarnings("rawtypes")
				Class[] parametersTypes = (Class[]) input.readObject();
				Object[] args = (Object[]) input.readObject();

				// 根据client请求找到具体接口
				Class<?> serviceClass = register.get(serviceName);
				// 根据方法名和参数类型获取方法
				Method method = serviceClass.getMethod(methodName, parametersTypes);
				// 执行方法
				Object res = method.invoke(serviceClass.newInstance(), args);

				// 返回结果
				output = new ObjectOutputStream(socket.getOutputStream());
				output.writeObject(res);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

}
