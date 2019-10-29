package UberEats;
import java.util.Hashtable;


import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
public class Restaurant extends Agent {
	// The catalogue of foods for sale (maps the title of a book to its price)
	private Hashtable catalogue;
	// The GUI by means of which the user can add books in the catalogue
	private RestaurantGui myGui;
	private int x;
	private int y;
	private int ranking;

	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
		catalogue = new Hashtable();

		// Create and show the GUI 
		myGui = new RestaurantGui(this);
		myGui.showGui();

		// Register the book-selling service in the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("food-selling");
		sd.setName("JADE-buy-food");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestsServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
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
		myGui.dispose();
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
				ACLMessage reply = msg.createReply();
				// ve se serve a comida que o cliente quer se sim envia-lhe as suas informa��es
				Integer price = (Integer) catalogue.get(foodType);
				if (price != null) {
					// The requested book is available for sale. Reply with the price
					reply.setPerformative(ACLMessage.PROPOSE);
					// Envia como resposta o x o y o Ranking e o pre�o
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

	/**
	   Inner class PurchaseOrdersServer.
	   This is the behaviour used by Book-seller agents to serve incoming 
	   offer acceptances (i.e. purchase orders) from buyer agents.
	   The seller agent removes the purchased book from its catalogue 
	   and replies with an INFORM message to notify the buyer that the
	   purchase has been sucesfully completed.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
				// o comprador deve enviar as suas coordenadas 

				/*if (price != null) {
					reply.setPerformative(ACLMessage.INFORM);
					System.out.println(title+" sold to agent "+msg.getSender().getName());
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("not-available");
				}*/
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent("Order in process");
				myAgent.send(reply);
			}
			else {
				block();
			}
			/*AGORA deve mandar mensagem a todos os drivers mensagem a perguntar 
			 * quem esta disponivel(ocupado se estiver a entregar um pedido)
			 * os drivers mandam mensagem para o restaurante com as suas coordenadas x y, 
			 * o restaurante deve escolher o que demore menos tempo,
			 * depois de escolhido envia mensagem ao driver que escolheu a perguntar se pode fazer o servi�o 
			 * se puder, fica tratado 
			 * se n�o tem que ver os outros drivers e escolher outro
			 * */
		}
	}  // End of inner class OfferRequestsServer
}
