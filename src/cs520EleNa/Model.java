package cs520EleNa;

public class Model {

	public static MapNode head;
	//initializes with the starting node
	public Model(int x, int y, int elevation) {
		head = new MapNode(x,y,elevation);
	}
	//TODO: Populate the model with model data.
	public static void main(String[] args) {
		Model model = new Model(554,123,52);
		model.addNode(242,212,46);
		model.addNode(521,275,24);
		model.addNode(432,112,65);
		System.out.println(head.getSize());
		System.out.println(head.getDistance(head.getNodes()[0]));
		System.out.println(head.getDistance(head.getNodes()[1]));
		System.out.println(head.getDistance(head.getNodes()[2]));
		model.addNode(553,100,23);
		System.out.println(head.getDistance(head.getNodes()[0]));
		
	}
	//adds node to the head, also creating a return link in the process.
	public void addNode(int x, int y, int elevation) {
		MapNode branchNode = new MapNode(x,y,elevation);
		head.addNode(branchNode);
		branchNode.addNode(head);	
	}
}
