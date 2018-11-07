/**
 * 
 */
package test;

import server.ServerCenter;
import serverImpl.ServerCenterImpl;
import service.HelloService;
import serviceImpl.HelloServiceImpl;

/**
 * @author Cyril
 * 下午10:06:29
 */
public class ServerTest {
	
	public static void main(String[] args) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ServerCenter server = new ServerCenterImpl();
				server.register(HelloService.class, HelloServiceImpl.class);
				server.start();
			}
		}).start();
		
	}

}
