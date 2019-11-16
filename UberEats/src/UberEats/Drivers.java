package UberEats;

import java.io.FileOutputStream;
import java.io.IOException;

/*
 * class Drivers
 * 
 * - Responsavel por implementar o agente Driver. */

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Drivers extends Agent {
	
	private String name;
	private int x;
	private int y;
	private int timestamp;
	
	FileOutputStream  writer;
	
	public Drivers(String name,int x, int y, int timestamp){
		this.name = name;
		this.x = x;
		this.y = y;
		this.timestamp = timestamp;
		
		try {
			this.writer = new FileOutputStream("driver"+name+".txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getDriverName(){
		return this.name;
	}
	protected void setup() {
		//System.out.println("Driver "+ getAID().getName()+" pronto.");
		
			try {
				this.writer.write("Estou pronto para entregar pedidos.\n".getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		// Register the DRIVER service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("food-delivery");
		sd.setName("Driver");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	
		
		// add the behaviour serving queries from restaurant agents
		addBehaviour(new OfferDriverServer());
		
		addBehaviour(new DeliverFoodServer());
		
		/*addBehaviour(new TickerBehaviour(this,30000) {
			
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				
				takeDown();
				
			}
			
		});*/
	}
	
	private class OfferDriverServer extends CyclicBehaviour {
		
		private static final long serialVersionUID = 1L;

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				
				ACLMessage reply = msg.createReply();
				
				//if (true) {
					// The requested food is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					// Envia como resposta o x o y o Ranking e o preço
					reply.setContent(String.valueOf(x) + ";" +String.valueOf(y) + ";" + String.valueOf(timestamp));
				// x;y;timestamp
				/*}
				else {
					// The requested food is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}*/
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer
	
	private class DeliverFoodServer extends CyclicBehaviour{
		
		private MessageTemplate mt;
		public void action() {
			 mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					String[]tokens= msg.getContent().split(";");
					int clientX=Integer.parseInt(tokens[1]);
					int clientY=Integer.parseInt(tokens[2]);
					double time= Double.parseDouble(tokens[0]);
					timestamp += time;
					x=clientX;
					y=clientY;
					ACLMessage reply = msg.createReply();
					// o comprador deve enviar as suas coordenadas 
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(timestamp));
					myAgent.send(reply);
					try {
						writer.write("Terminei um pedido.\n".getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else {
					block();
				}
			
		}
		
	}
	
	
	// Put agent clean-up operations here
		protected void takeDown() {
			try {
				this.writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Deregister from the yellow pages
			try {
				DFService.deregister(this);
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}

			//System.out.println("Seller-agent "+getAID().getName()+" terminating.");
		}

}
