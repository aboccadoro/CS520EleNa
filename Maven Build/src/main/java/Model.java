
public class Model {

	public static MapNode head;
	//initializes with the starting node
	public Model(int x, int y, int elevation) {
		head = new MapNode(x,y,elevation);
	}
	//TODO: Populate the model with model data.
	public static void main(String[] args) {
		Model model = new Model(554,123,52);
	}
	//adds node to the head, also creating a return link in the process.
	public void addNode(double x, double y, double elevation, double distance) {
		DistPair branchNode = new DistPair(x,y,elevation,distance);
		head.addNode(branchNode);
		branchNode.getNode().addNode(head, distance);	
	}
}
