/**
 * 
 */
package serviceImpl;

import service.HelloService;

/**
 * @author Cyril
 * 下午10:00:03
 */
public class HelloServiceImpl implements HelloService {


	@Override
	public String Hello(String name) {
		
		return "Hello，" + name;
	}

}
