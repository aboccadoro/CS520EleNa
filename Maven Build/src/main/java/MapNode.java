
public class MapNode {
	private DistPair[] nodes;
	private double elevation;
	private double x;
	private double y;
	//implements a new node with an empty node array
	public MapNode(double x, double y, double elevation) {
		this.x = x;
		this.y = y;
		this.elevation = elevation;
		nodes = new DistPair[0];
	}
	//if you already have a node array for this particular node, you can use this constructor.
	public MapNode(double x, double y, double elevation, DistPair[] nodes) {
		this.x = x;
		this.y = y;
		this.elevation = elevation;
		this.nodes = nodes;
	}
	//Check to see if this node contains any nodes.
	public boolean hasNodes() {
		if(nodes != null && nodes.length > 0) {
			return true;
		}
		return false;
	}
	//getters for x, y, size, nodes, and the elevation. Do we need setters?
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getSize() {
		return nodes.length;
	}
	public DistPair[] getPairs() {
		return nodes;
	}
	public MapNode[] getNodes() {
		MapNode[] returnNodes = new MapNode[nodes.length];
		for(int i = 0; i < nodes.length; i++) {
			returnNodes[i] = nodes[i].getNode();
		}
		return returnNodes;
	}
	public double getElevation() {
		return elevation;
	}
	//adds a node to the end of the node array
	public void addNode(MapNode node, double distance) {
		DistPair[] temp = nodes;
		nodes = new DistPair[temp.length+1];
		for(int i = 0; i < temp.length; i++) {
			nodes[i] = temp[i];
		}
		nodes[nodes.length-1] = new DistPair(node, distance); 
		sortNodesByDistance();
	}
	public void addNode(DistPair pair) {
		DistPair[] temp = nodes;
		nodes = new DistPair[temp.length+1];
		for(int i = 0; i < temp.length; i++) {
			nodes[i] = temp[i];
		}
		nodes[nodes.length-1] = pair; 
		sortNodesByDistance();
	}
	//returns true if it can find the node specified by x, y, and elevation inside the node array. Returns false if it cannot find the node inside the node array.
	public boolean hasNode(double x, double y) {
		for(int i = 0; i < nodes.length; i++) {
			if(x == nodes[i].getNode().getX() && y == nodes[i].getNode().getY()) {
				return true;
			}
		}
		return false;
	}
	public boolean hasNode(MapNode node) {
		for(int i = 0; i < nodes.length; i++) {
			if(node == nodes[i].getNode()) {
				return true;
			}
		}
		return false;
	}
	//returns the mapnode specified by x, y, and elevation inside the node array. Throws an exception if it cannot be found.
	public MapNode findNode(double x, double y) throws NodeNotFoundException{
		for(int i = 0; i < nodes.length; i++) {
			if(x == nodes[i].getNode().getX() && y == nodes[i].getNode().getY()) {
				return nodes[i].getNode();
			}
		}
		throw new NodeNotFoundException("Node was not found.");
	}
	//implements a bubble sort to sort the nodes using getDistance
	public void sortNodesByDistance() {
		int n = nodes.length;
		boolean swapped = true;
		while(swapped) {
			swapped = false;
			for(int i = 1; i < n; i++) {
				if(nodes[i-1].getDistance() > nodes[i].getDistance()) {
					DistPair temp = nodes[i-1];
					nodes[i-1] = nodes[i];
					nodes[i] = temp;
					swapped = true;
				}
			}
		}
	}
	public double getDistanceToNode(MapNode node) throws NodeNotFoundException {
		for(int i = 0; i < nodes.length; i++) {
			if(node == nodes[i].getNode()) {
				return nodes[i].getDistance();
			}
		}
		throw new NodeNotFoundException("Node was not found.");
	}
	public double getDistanceToNode(double x, double y) throws NodeNotFoundException {
		return getDistanceToNode(findNode(x,y));
	}
	//does pythagorean theorem for distance to a node
//	public double getDistance(MapNode node) {
//		return Math.sqrt(Math.abs((this.x-node.getX())*(this.x-node.getX()))+Math.abs((this.y-node.getY())*(this.y-node.getY())));
//	}
	
}
