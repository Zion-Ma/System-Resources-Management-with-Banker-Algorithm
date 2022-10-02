import java.util.concurrent.locks.*;
import java.lang.IllegalMonitorStateException;
class Lock_Resource{
	Lock[] first; Lock[] second; Lock[] third; int f, s, t;
	public Lock_Resource(){ // total resource: 10, 5, 7
		first = new ReentrantLock[10];
		for (int i = 0; i < 10; i++){first[i] = new ReentrantLock();} 
		second = new ReentrantLock[5];
		for (int i = 0; i < 5; i++){second[i] = new ReentrantLock();}  
		third = new ReentrantLock[7];
		for (int i = 0; i < 7; i++){third[i] = new ReentrantLock();} 
		f = 0; s = 0; t = 0;
	}
	public synchronized void flag(Lock[] lock, int w, int v, int bound){
		for(int i = w; i < (w + v); i++){
			if (i == (bound)){
				System.out.println("Out of resources, Deadlock will occur.");
				i = 0;
			}
			try{
				lock[i].lock(); //lock resource
			}
			catch(IllegalMonitorStateException e){}
			finally{}
		}
		for(int i = (w + v); i > w ; i--){
			if (i >= (bound)){
				// System.out.println("Out of resources, Deadlock will occur.");
				i = 0;
			}
			try{
				lock[i].unlock(); //return resource
			}
			catch(IllegalMonitorStateException e){}
			finally{}
		}
	}
	public synchronized  void access(int a, int b, int c, String thread){
		System.out.println(thread + " is now executing.");
		flag(this.first, f, a, 10);
		this.f = f + a;
		System.out.println(thread + " got its all first locks.");
		flag(this.second, s, b, 5);
		System.out.println(thread + " got its all second locks.");
		this.s = s + b;
		flag(this.third, t, c, 7);
		System.out.println(thread + " got its all third locks.");
		this.t = t + c;
	}
	
	
}


class A implements Runnable
{
	Lock_Resource L;
	int f, s, t;
	String thread;
	
	public A(Lock_Resource R, int a, int b, int c, String name) {
		L = R;
		f = a; s = b; t = c;
		thread = name;
	}
	public void run(){
		L.access(f, s, t, thread);
	}
}



public class B10641020_DeadlockExample2 
{  
	public static void main(String arg[]) {

		/*initialize total resources */
		Lock_Resource L = new Lock_Resource();

		// Thread T1 = new Thread(new A(L, 7, 5, 3, "T1"));
		// Thread T2 = new Thread(new A(L, 3, 2, 2, "T2"));
		// Thread T3 = new Thread(new A(L, 9, 0, 2, "T3"));
		// Thread T4 = new Thread(new A(L, 6, 8, 2, "T4"));
		// Thread T5 = new Thread(new A(L, 4, 3, 3, "T5"));

		Thread T1 = new Thread(new A(L, 1, 1, 1, "T1"));
		Thread T2 = new Thread(new A(L, 1, 1, 1, "T2"));
		Thread T3 = new Thread(new A(L, 9, 0, 2, "T3"));
		Thread T4 = new Thread(new A(L, 6, 8, 2, "T4"));
		Thread T5 = new Thread(new A(L, 4, 3, 3, "T5"));

		T2.start();
		T1.start();
		T3.start();
		T4.start();
		T5.start();

	}
}
