/**
 * 
 */
package test;

import java.net.InetSocketAddress;

import proxy.ClientProxy;
import service.HelloService;

/**
 * @author Cyril
 * 下午10:12:21
 */
public class ClientTest {
 
	public static void main(String[] args) {
		HelloService hello = null;
		try {
		 hello = ClientProxy.getRemoteProxyRes(Class.forName("service.HelloService"), new InetSocketAddress("127.0.0.1",12000));
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(hello.Hello("zhangsan"));
	}
}
