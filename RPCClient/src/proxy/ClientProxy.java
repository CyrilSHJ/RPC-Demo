/**
 * 
 */
package proxy;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author Cyril 下午10:09:30
 */
public class ClientProxy {
	@SuppressWarnings("unchecked")
	public static <T> T getRemoteProxyRes(Class serviceInterface, InetSocketAddress addr) {

		/**
		 * 1. 类加载器 classLoader: 加载需要代理的类（HelloServic接口） 2. 需要代理对象，具有哪些功能 接口 3.
		 */
		return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class<?>[] { serviceInterface },
				new InvocationHandler() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see
					 * java.lang.reflect.InvocationHandler#invoke(java.lang.
					 * Object, java.lang.reflect.Method, java.lang.Object[])
					 * proxy: 代理对象 method: 哪个方向 args ：参数列表
					 */
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						ObjectOutputStream output = null;
						ObjectInputStream input = null;
						try {
							@SuppressWarnings("resource")
							Socket socket = new Socket();
							socket.connect(addr);
							// 发送信息
							output = new ObjectOutputStream(socket.getOutputStream());
							// 接口名
							output.writeUTF(serviceInterface.getName());
							// 方法名
							output.writeUTF(method.getName());
							// 参数类型
							output.writeObject(method.getParameterTypes());
							// 参数列表
							output.writeObject(args);

							// 等待服务器处理...
							System.out.println("等待服务器处理...");
							
							// 读取服务器返回的结果
							input = new ObjectInputStream(socket.getInputStream());
							return input.readObject();

						} catch (Exception e) {
							e.printStackTrace();
							return null;
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
				});

	}

}
