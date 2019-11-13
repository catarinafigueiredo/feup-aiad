package UberEats;
import java.util.Hashtable;
import java.lang.Math;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SSResponderDispatcher;
public class Restaurant extends Agent {

	private static final long serialVersionUID = 1L;
	// The catalogue of foods for sale (maps the title of a book to its price)
	private Hashtable catalogue;
	// The GUI by means of which the user can add books in the catalogue
	private int x;
	private int y;
	private String name;
	private int ranking;
	private AID[] driverAgents; 
	
	public Restaurant(String name,int x,int y, int ranking, Hashtable catalogue) {
		this.x=x;
		this.y=y;
		this.name=name;
		this.ranking=ranking;
		this.catalogue=catalogue;
		
	}
	
	public String getRestaurantName(){
		return this.name;
	}
	
	
	
	// Put agent initializations here
	@Override
	protected void setup() {
	
       // encontrar todos os drivers para lhes poder mandar mensagem
		// Create and show the GUI 
		//myGui = new RestaurantGui(this);
		//myGui.showGui();

		// Register the book-selling service in the yellow pages
		System.out.println("I'm restaurant "+ getAID().getName()+".");
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("food-selling");
		sd.setName("JADE-book-trading");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
	
		
		addBehaviour(new TickerBehaviour(this,100) {
			private static final long serialVersionUID = 1L;

			protected void onTick() { // atualiza de x em x os drivers disponiveis
				//System.out.println("Trying to order "+food);
				// Update the list of seller agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("food-delivery");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template); 
					//System.out.println("Found the following Drivers:");
					driverAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						driverAgents[i] = result[i].getName();
						//System.out.println(driverAgents[i].getName());
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
			}
		});

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestsServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServe());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		//myGui.dispose();
		// Printout a dismissal message
		System.out.println("Seller-agent "+getAID().getName()+" terminating.");
	}

	/**
     This is invoked by the GUI when the user adds a new book for sale
	 */
	public void updateCatalogue(final String foodType, final int price) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				catalogue.put(foodType, new Integer(price));
				System.out.println(foodType+" inserted into catalogue. Price = "+price);
			}
		} );
	}

	/**
	   Inner class OfferRequestsServer.
	   This is the behaviour used by Restaurants agents to serve incoming requests 
	   for offer from buyer agents.
	   If the requested book is in the local catalogue the seller agent replies 
	   with a PROPOSE message specifying the price. Otherwise a REFUSE message is
	   sent back.
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				
				// CFP Message received. Process it
				String foodType = msg.getContent();
				System.out.println("Recebeu msg em offerRequests COMIDA PEDIDA->"+ foodType);
				ACLMessage reply = msg.createReply();
				// ve se serve a comida que o cliente quer se sim envia-lhe as suas informações
				
				Integer price = (Integer) catalogue.get(foodType);
				System.out.println("O preco de "+ foodType +" e "+ price);
				if (price != null) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					// Envia como resposta o x o y o Ranking e o preço
					reply.setContent(String.valueOf(x) + ";" +String.valueOf(y) + ";" + String.valueOf(ranking) + ";" +String.valueOf(price.intValue()));
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

	
	
	private class PurchaseOrdersServe extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				System.out.print("Recebi proposal vou enviar inform");
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				String[]tokens= title.split(";");
				String food = tokens[0];
				int clientX=Integer.parseInt(tokens[1]);
				int clientY=Integer.parseInt(tokens[2]);
				ACLMessage reply = msg.createReply();
				// o comprador deve enviar as suas coordenadas 

				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("Order in process");
				myAgent.send(reply);
				// n funciona
				myAgent.addBehaviour(new FindDrivers(food, clientX, clientY));
				
			}
			else {
				block();
			}
		
			}
		
	}  // End of inner class OfferRequestsServer
	
	private class FindDrivers extends Behaviour{

		private static final long serialVersionUID = 1L;
		private AID bestDriver;
		private int bestDriverX;
		private int bestDriverY;
		private int bestDriverTimestamp;
		private double bestDriverDist;
		String food;
		int clientX;
		int clientY;
		double distClientRest;
		
		private int step=1;
		private int repliesCnt = 0;
		private MessageTemplate mt;
		
		FindDrivers(String food, int clientX, int clientY) {
			this.food = food;
			this.clientX = clientX;
			this.clientY = clientY;
			
			this.distClientRest = Math.sqrt((y -clientY ) * (y - clientY) + (x - clientX) * (x - clientX));
		}
		
		public void action() {
			System.out.println("Entrei em FindDrivers e step é ->"+ step);
			switch(step) { 
			case 1:
				// Send the cfp to all drivers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < driverAgents.length; ++i) {
					cfp.addReceiver(driverAgents[i]);
				} 
				cfp.setContent("info");
				cfp.setConversationId("deliver-food");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				System.out.println("enviei cfp para drivers");
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("deliver-food"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 2;
				break;
			case 2:
				// Receive all proposals/refusals from drivers
				//escolhe sempre o driver mais perto?!
				ACLMessage reply= myAgent.receive(mt);
				if(reply!=null) {
					System.out.println("recebi resposta do driver");
					if(reply.getPerformative()==ACLMessage.PROPOSE) {
						// Pode fazer o pedido 
						String[]tokens= reply.getContent().split(";");
						
						int drivX=Integer.parseInt(tokens[0]);
						int drivY=Integer.parseInt(tokens[1]);
						int drivTS=Integer.parseInt(tokens[2]);
						
						double distRestDriv = Math.sqrt((y -drivY ) * (y - drivY) + (x - drivX) * (x - drivX));
						double totalDist = this.distClientRest + distRestDriv;
						drivTS+=totalDist;
						
						if(this.bestDriver==null || drivTS<this.bestDriverTimestamp) {
							this.bestDriverX = drivX;
							this.bestDriverY = drivY;
							this.bestDriverTimestamp = drivTS;
							this.bestDriverDist = totalDist;
							
							bestDriver = reply.getSender();
						}
						
					
					}
					repliesCnt++;
					if(repliesCnt >= driverAgents.length) {
						step=3;
					}
				}
				break;
			case 3:
				// Send the delivery order to the driver that provided the choosed offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestDriver);
				order.setContent(String.valueOf(this.bestDriverDist)); // deve mandar o xy do cliente para o driver entregar o pedido
				order.setConversationId("food-delivery");//book-trade
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("food-delivery"),//book-trade
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 4;
				break;
			case 4:
				// order delivered
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(" Food delivered by "+reply.getSender().getName() + " at " + reply.getContent());
						//System.out.println("Price = "+bestPrice);
						//System.out.println("Waiting order Arraival");
						//myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: restaurant not working rigth.");
					}

					step = 5;
				}
				else {
					block();
				}
				break;
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
		
				if (step == 3 && bestDriver == null) {
					System.out.println("Attempt failed: "+food +" not available for sale");
				}
				return ((step == 3 && bestDriver == null) || step == 5);
			
		}
		
	}
	/**
	   Inner class PurchaseOrdersServer.
	   This is the behaviour used by Book-seller agents to serve incoming 
	   offer acceptances (i.e. purchase orders) from buyer agents.
	   The seller agent removes the purchased book from its catalogue 
	   and replies with an INFORM message to notify the buyer that the
	   purchase has been sucesfully completed.
	 */
	/*Deve mandar cfp para os drivers - ver se CYCLIc funciona senão trocar para behaviour*/ 
	private class PurchaseOrdersServer extends CyclicBehaviour { // por steps
		private AID bestDriver;
		private int step=0;
		private int repliesCnt = 0;
		private MessageTemplate mt;
		public void action() {
			switch(step) {
			case 0:
				
				mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL); // criar thread para o resto 
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					System.out.print("Recebi proposal");
					// ACCEPT_PROPOSAL Message received. Process it
					String title = msg.getContent();
					ACLMessage reply = msg.createReply();
					// o comprador deve enviar as suas coordenadas 
	
					reply.setPerformative(ACLMessage.INFORM);
					reply.setContent("Order in process");
					myAgent.send(reply);
				}
				else {
					block();
				}
				// SSResponderDispatcher
				/*addBehaviour(new SSResponderDispatcher(myAgent, mt) {
		
					@Override
					protected Behaviour createResponder(ACLMessage arg0) {
						// TODO Auto-generated method stub
						return null;
					}
				});*/
				
				step=1;
				break;
			case 1:
				// Send the cfp to all drivers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < driverAgents.length; ++i) {
					cfp.addReceiver(driverAgents[i]);
				} 
				cfp.setContent("coordenadas cliente + restaurant");
				cfp.setConversationId("deliver-food");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("deliver-food"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 2;
				break;
			case 2:
				// Receive all proposals/refusals from drivers
				//escolhe sempre o driver mais perto?!
				ACLMessage reply= myAgent.receive(mt);
				if(reply!=null) {
					if(reply.getPerformative()==ACLMessage.PROPOSE) {
						// Pode fazer o pedido 
						String[]tokens= reply.getContent().split(";");
						int driverX=Integer.parseInt(tokens[0]);
						int driverY=Integer.parseInt(tokens[1]);
						int driverTimestamp=Integer.parseInt(tokens[2]);
					/*
					  vê quando demora do driver para o restaurante para o cliente 
					  fica o driver que demorar menos  
					 
					 escolher o driver apenas
					 */
					
					}
					repliesCnt++;
					if(repliesCnt >= driverAgents.length) {
						step=3;
					}
				}
				break;
			case 3:
				// Send the delivery order to the driver that provided the choosed offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestDriver);
				order.setContent(";"+x+"-"+y); // deve mandar o xy do cliente para o driver entregar o pedido
				order.setConversationId("food-delivery");//book-trade
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("food-delivery"),//book-trade
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 4;
				break;
			case 4:
				// order delivered
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(" Food delivered by "+reply.getSender().getName() + " at" + reply.getContent());
						//System.out.println("Price = "+bestPrice);
						//System.out.println("Waiting order Arraival");
						//myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: restaurant not working rigth.");
					}

					step = 4;
				}
				else {
					block();
				}
				break;
			}
		}
	}  // End of inner class OfferRequestsServer
}
