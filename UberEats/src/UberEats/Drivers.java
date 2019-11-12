package UberEats;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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
	}
	
	private class OfferDriverServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				
				ACLMessage reply = msg.createReply();
				
				if (true) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					// Envia como resposta o x o y o Ranking e o preço
					reply.setContent(String.valueOf(x) + ";" +String.valueOf(y) + ";" + String.valueOf(timestamp));
				// x-y;timestamp
				}
				else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
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
					// ACCEPT_PROPOSAL Message received. Process it
					// atualizar timestamp = timestamp atual + tempo para entregar o pedido
					ACLMessage reply = msg.createReply();
					// o comprador deve enviar as suas coordenadas 
	
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent(String.valueOf(timestamp));
					myAgent.send(reply);
				}
				else {
					block();
				}
			
		}
		
	}

}
