package UberEats;

/*
 * class Restaurant
 * 
 * - Responsavel por implementar o agente Restaurant. */

import java.util.Hashtable;
//import java.io.FileOutputStream;
//import java.io.IOException;
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

public class Restaurant extends Agent {

	private static final long serialVersionUID = 1L;
	
	private Hashtable catalogue;
	
	private int x;
	private int y;
	
	//FileOutputStream writer;
	
	private String name;
	
	private int ranking;
	
	private AID[] driverAgents;
	
	private int numClients;
	private int receivedOrders = 0;
	private boolean terminate = false;
	
	public Restaurant(String name,int x,int y, int ranking, Hashtable catalogue) {
		this.x = x;
		this.y = y;
		this.name = name;
		this.ranking = ranking;
		this.catalogue = catalogue;
		
		/*try {
			this.writer = new FileOutputStream("restaurant"+name+".txt");
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
	public int calcQuad() {
		if(((x>=0)&&(x<=50))&&((y>=0)&&(y<=50))) {
			return 1;
		}
		else if(((x>=50)&&(x<=100))&&((y>=0)&&(y<=50))) {
			return 2;
		}
		else if(((x>=50)&&(x<=100))&&((y>=50)&&(y<=100))) {
			return 3;
		}
		else if(((x>=0)&&(x<=50))&&((y>=50)&&(y<=100))) {
			return 4;
		}
		
		return -1;
	}
	
	public void setNumClients(int num) {
		this.numClients = num;
		//System.out.println("num de clients updated!");
		
	}
	
	public String getRestaurantName(){
		return this.name;
	}
	
	@Override
	protected void setup() {
		
		//System.out.println("Restaurante " + getAID().getName() + " pronto.");
		/*try {
			this.writer.write((this.name+" pronto.\n").getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}*/
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
		
		addBehaviour(new TickerBehaviour(this,10) {
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("food-delivery");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent, template);
					driverAgents = new AID[result.length];
					for (int i = 0; i < result.length; ++i) {
						driverAgents[i] = result[i].getName();
					}
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
				
			}
		});
		
		/*addBehaviour(new TickerBehaviour(this,30000) {
			
			private static final long serialVersionUID = 1L;

			protected void onTick() {
				
				takeDown();
				
			}
			
		});*/

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestsServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServe());
	}
	
	

	// Put agent clean-up operations here
	@Override
	protected void takeDown() {

		// Deregister from the yellow pages
				try {
					//this.writer.close();
					DFService.deregister(this);
				}
				/*catch (IOException fe) {
					fe.printStackTrace();
				}*/ catch (FIPAException e) {
					e.printStackTrace();
				}

		super.takeDown();

	}

	/**
     This is invoked by the GUI when the user adds a new book for sale
	 */
	/*public void updateCatalogue(final String foodType, final int price) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				catalogue.put(foodType, new Integer(price));
				System.out.println(foodType+" inserted into catalogue. Price = "+price);
			}
		} );
	}*/

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
				ACLMessage reply = msg.createReply();
				
				receivedOrders++;
				
				// ve se serve a comida que o cliente quer se sim envia-lhe as suas informações
				
				Integer price = (Integer) catalogue.get(foodType);
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
			
			MessageTemplate mt2 = MessageTemplate.MatchPerformative(ACLMessage.REJECT_PROPOSAL);
			ACLMessage msg2 = myAgent.receive(mt2);
			
			if (msg != null) {
				//System.out.println(numClients);
				if(receivedOrders >= numClients) {
					terminate = true;
					
				}
				
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				String[]tokens= title.split(";");
				String food = tokens[0];
				int clientX=Integer.parseInt(tokens[1]);
				int clientY=Integer.parseInt(tokens[2]);
				
				/*try {
					writer.write(("Cliente " + msg.getSender().getName() +" pediu " + food +".\n").getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
				
				ACLMessage reply = msg.createReply();

				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("Order in process.");
				myAgent.send(reply);
				myAgent.addBehaviour(new FindDrivers(food, clientX, clientY, msg.getSender().getName()));
				
			}
			if (msg2 != null) {
				//System.out.println("YES! TA A RECEBER!!");
				if(receivedOrders >= numClients) {
					//System.out.println("restaurante TERMINEI!");
					myAgent.doDelete();
					
				}
			}
			else {
				block();
			}
		
			}
		
	}  // End of inner class OfferRequestsServer
	
	private class FindDrivers extends Behaviour {

		private static final long serialVersionUID = 1L;
		private AID bestDriver;
		//private int bestDriverX;
		//private int bestDriverY;
		private int bestDriverTimestamp;
		private double bestDriverDist;
		String food;
		String clientName;
		int clientX;
		int clientY;
		double distClientRest;
		
		private int step=1;
		private int repliesCnt = 0;
		private MessageTemplate mt;
		
		FindDrivers(String food, int clientX, int clientY, String clientName) {
			this.food = food;
			this.clientX = clientX;
			this.clientY = clientY;
			this.clientName = clientName;
			
			this.distClientRest = Math.sqrt((y -clientY ) * (y - clientY) + (x - clientX) * (x - clientX));
		}
		
		public void action() {
			
			switch(step) { 
			
			case 1:
				// Send the cfp to all drivers
				
				//System.out.println(getAID().getName() + " contactando drivers...");
				/*try {
					writer.write(("Cliente " + clientName +" efetuou pagamento. Iniciando contacto com drivers para fazer a entrega...\n").getBytes());
				} catch (IOException e1) {
					e1.printStackTrace();
				}*/
				
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < driverAgents.length; ++i) {
					cfp.addReceiver(driverAgents[i]);
				} 
				cfp.setContent("info");
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
				// faz calculos e escolhe o driver com menor timestamp
				ACLMessage reply= myAgent.receive(mt);
				if(reply!=null) {
					
					if(reply.getPerformative()==ACLMessage.PROPOSE) {
						// Pode fazer o pedido 
						String[]tokens= reply.getContent().split(";");
						
						
						int drivX=Integer.parseInt(tokens[0]);
						int drivY=Integer.parseInt(tokens[1]);
						int drivTS=Integer.parseInt(tokens[2]);
						
						double distRestDriv = Math.sqrt((y -drivY ) * (y - drivY) + (x - drivX) * (x - drivX));
						double totalDist = this.distClientRest + distRestDriv;
						drivTS+=totalDist;
						
						//System.out.println(reply.getSender().getName()+" - "+drivTS+" contra "+this.bestDriverTimestamp);
						
						if(this.bestDriver==null || drivTS<this.bestDriverTimestamp) {
							//this.bestDriverX = drivX;
							//this.bestDriverY = drivY;
							this.bestDriverTimestamp = drivTS;
							this.bestDriverDist = totalDist;
							
							bestDriver = reply.getSender();
						}
					}
					repliesCnt++;
					
					if(repliesCnt >= driverAgents.length) {
						//System.out.println("SISTEMA - driver " + bestDriver.getName() + " selecionado para entregar "+ this.food + " a " + clientName);
						/*try {
							writer.write(("Foi selecionado o driver "+bestDriver.getName() + " para entregar "+ this.food + " a " + clientName+".\n").getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}*/
						step=3;
					}
				}
				break;
				
			case 3:
				// Send the delivery order to the driver that provided the choosed offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestDriver);
				order.setContent(String.valueOf(this.bestDriverDist)+ ";"+ clientX + ";" + clientY + ";" + clientName + ";" + food); // deve mandar o xy do cliente para o driver entregar o pedido
				order.setConversationId("food-delivery");//book-trade
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				
				ACLMessage reject= new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				
				for (int i = 0; i < driverAgents.length; ++i) {
					if(!bestDriver.equals(driverAgents[i]))
						reject.addReceiver(driverAgents[i]);
				} 
				reject.setContent("");
				reject.setConversationId("food-delivery");
				reject.setReplyWith("reject"+System.currentTimeMillis()); // Unique value
				myAgent.send(reject);
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
						//System.out.println("PEDIDO TERMINADO! Comida " + food + " entregue por " + reply.getSender().getName() + " a " + clientName + " em " + reply.getContent() + ".");
						//writer.println("Foi selecionado o driver "+reply.getSender().getName() + " para entregar o pedido.");
						//myAgent.doDelete();
						//System.out.println(terminate);
						if(terminate) {
							//System.out.println("restaurante TERMINEI!");
							myAgent.doDelete();
							
						}
					}
					else {
						System.out.println("Attempt failed: restaurant not working right.");
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
		
				if (step == 3 && bestDriver == null) {
					System.out.println("Attempt failed: " + food + " not available for sale.");
				}
				return ((step == 3 && bestDriver == null) || step == 5);
			
		}
		
	}
	
}
