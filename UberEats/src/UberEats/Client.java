package UberEats;
import javax.swing.JFrame;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.lang.Math; 
public class Client extends Agent{
	private String name;
	private String food;
	private int x;
	private int y;
	private String criterion;
	
	private AID[] restaurantAgents;
	
	public Client(String name, int x,int y, String food, String criterion) {
		this.name=name;
		this.x=x;
		this.y=y;
		this.food=food;
		this.criterion=criterion;
	}
	public String getClientName(){
		return this.name;
	}
	@Override
	protected void setup() {
		System.out.println("I'm client "+ getAID().getName()+".");
		Object[] args= getArguments();
		if (true) {
			/*x = (int) args[0];
			System.out.println("x is "+x);
			y=(int) args[1];
			System.out.println("y is "+y);
			food = (String) args[2];
			System.out.println("food is "+food);
			criterion = (String) args[3];
			System.out.println("Criterion is "+criterion);*/

			/*-> Pergunta quem tem o que ele quer
			   addBehavior(new MakeOrder(this)); 
			 
			 
			 
			 
			 */
		
			
			 System.out.println("Trying to order "+food);
			 /*addBehaviour(new Behaviour() {
				

					public void action() {
						
						// Update the list of seller agents
						DFAgentDescription template = new DFAgentDescription();
						ServiceDescription sd = new ServiceDescription();
						sd.setType("food-selling");
						template.addServices(sd);
						try {
							DFAgentDescription[] result = DFService.search(myAgent, template); 
							//System.out.println("Found the following Restaurants:");
							restaurantAgents = new AID[result.length];
							for (int i = 0; i < result.length; ++i) {
								restaurantAgents[i] = result[i].getName();
								//System.out.println(restaurantAgents[i].getName());
							}
						}
						catch (FIPAException fe) {
							fe.printStackTrace();
						}

						// Perform the request
						//myAgent.addBehaviour(new RequestPerformer());
					}

					@Override
					public boolean done() {
						// TODO Auto-generated method stub
						return false;
					}
				} );*/
			 
			// Add a TickerBehaviour that schedules a request to seller agents every minute
			addBehaviour(new TickerBehaviour(this, 10000) {
				protected void onTick() {
					
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("food-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template); 
						System.out.println("Found the following Restaurants:");
						restaurantAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							restaurantAgents[i] = result[i].getName();
							System.out.println(restaurantAgents[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer());
				}
			} );
			//addBehaviour(new RequestPerformer());
		}
		else {
			// Make the agent terminate
			System.out.println("No food available");
			doDelete();
		}
	}


	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Buyer-agent "+getAID().getName()+" terminating.");
	}

	/**
	   Inner class RequestPerformer.
	   This is the behaviour used by Book-buyer agents to request seller 
	   agents the target book.
	 */
	private class RequestPerformer extends Behaviour {
		private AID bestSeller; // The agent who provides the best offer depends on the criterion
		private double bestPrice;  // The best offered price
		private double betterQuality; 
		private double faster;
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;

		@Override
		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				System.out.println("Step 0 client");
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < restaurantAgents.length; ++i) {
					cfp.addReceiver(restaurantAgents[i]);
				} 
				cfp.setContent(food);
				cfp.setConversationId("buy-food");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("buy-food"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				System.out.println("Step 1 client");
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						// parse content 
						String[] tokens = reply.getContent().split(";");
						int RestaurantX=Integer.parseInt(tokens[0]);
						int RestaurantY=Integer.parseInt(tokens[1]);
						int RestaurantRanking=Integer.parseInt(tokens[2]);
						int price = Integer.parseInt(tokens[3]);
						double dist = Math.sqrt((y -RestaurantY ) * (y - RestaurantY) + (x - RestaurantX) * (x - RestaurantX));
						
						// A melhor oferta depende do criterio escolhido pelo cliente
						// Mais rapido que tem a dist mais pequena
						//Mais barato - combina a distancia do cliente e do restaurante + preco da comida 
						//Com mais qualidade - s� tem em conta o ranking do restaurante
						
					        switch(criterion) 
					        { 
					            case "cheaper": 
					            	if (bestSeller == null || price < bestPrice) {
										// This is the best offer at present
										bestPrice = price;
										bestSeller = reply.getSender();
									}
					                System.out.println("one"); 
					                break; 
					            case "faster": 
					            	if (bestSeller == null || dist < faster ) {
										// This is the best offer at present
					            		faster = dist;
										bestSeller = reply.getSender();
									}
					                System.out.println("two"); 
					                break; 
					            case "BetterQuality": 
					            	if (bestSeller == null ||  RestaurantRanking < betterQuality) {
										// This is the best offer at present
										bestPrice = price;
										bestSeller = reply.getSender();
									}
					                System.out.println("three"); 
					                break; 
					            case "BetterPriceQuality": 
					            	if (bestSeller == null ||  RestaurantRanking < betterQuality) {
										// This is the best offer at present
										bestPrice = price;
										bestSeller = reply.getSender();
									}
					                System.out.println("three"); 
					                break; 
					            default:
					                System.out.println("no match"); 
					        } 
					    
						
						
					}
					repliesCnt++;
					if (repliesCnt >= restaurantAgents.length) {
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:
				System.out.println("Step 2 - O restaurante escolhido e ->"+ bestSeller.getName());
				// Send the purchase order to the seller that provided the choosed offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				
				order.addReceiver(bestSeller);
				order.setContent(food + ";"+x+"-"+y);
				order.setConversationId("food-trade");//book-trade
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("food-trade"),//book-trade
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(food +" successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Price = "+bestPrice);
						System.out.println("Waiting order Arraival");
						myAgent.doDelete();
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
			case 4:   // s� recebe a mensagem com a informa��o do driver que vai levar a comida 
				// fica � espera que o restaurante mande o driver entregar a comida
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println(food +" successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Price = "+bestPrice);
						System.out.println("Waiting order Arraival");
						myAgent.doDelete();
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

		public boolean done() {
			if (step == 2 && bestSeller == null) {
				System.out.println("Attempt failed: "+food +" not available for sale");
			}
			return ((step == 2 && bestSeller == null) || step == 4);
		}
	}  // End of inner class RequestPerformer
}

