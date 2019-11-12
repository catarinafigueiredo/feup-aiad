package UberEats;
import jade.core.Agent;
public class Drivers extends Agent{
	private int x;
	private int y;
	private int timestamp;
	
	protected void setup() {
		System.out.println("I'm driver "+ getAID().getName()+".");
		Object[] args=getArguments();
		if(args !=null && args.length>0) {
			x=(int) args[0];
			System.out.println("x is "+ x);
			y=(int) args[1];
			System.out.println("y is "+ y);
			timestamp=(int) args[2];
			System.out.println("Timestamp is "+ timestamp);
			
		}
	}
	

}
