/**
 * 
 */
package server;

/**
 * @author sihangjun
 * 下午9:17:27
 */
public interface ServerCenter {
	
	public void start();
	
	public void stop();
	
	public void register(Class service , Class serviceImpl);

}
