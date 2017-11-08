package cs520EleNa;

public class MapNode {
	private MapNode[] nodes;
	private int elevation;
	private int x;
	private int y;
	//implements a new node with an empty node array
	public MapNode(int x, int y, int elevation) {
		this.x = x;
		this.y = y;
		this.elevation = elevation;
		nodes = new MapNode[0];
	}
	//if you already have a node array for this particular node, you can use this constructor.
	public MapNode(int x, int y, int elevation, MapNode[] nodes) {
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
	//getters for x, y, and the elevation. Do we need setters?
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public int getElevation() {
		return elevation;
	}
	//adds a node to the end of the node array
	public void addNode(MapNode node) {
		MapNode[] temp = nodes;
		nodes = new MapNode[temp.length+1];
		for(int i = 0; i < temp.length; i++) {
			nodes[i] = temp[i];
		}
		nodes[nodes.length-1] = node; 
	}
	//returns true if it can find the node specified by x, y, and elevation inside the node array. Returns false if it cannot find the node inside the node array.
	public boolean hasNode(int x, int y, int elevation) {
		for(int i = 0; i < nodes.length; i++) {
			if(x == nodes[i].getX() && y == nodes[i].getY() && elevation == nodes[i].getElevation()) {
				return true;
			}
		}
		return false;
	}
	//returns the mapnode specified by x, y, and elevation inside the node array. Throws an exception if it cannot be found.
	public MapNode findNode(int x, int y, int elevation) throws NodeNotFoundException{
		for(int i = 0; i < nodes.length; i++) {
			if(x == nodes[i].getX() && y == nodes[i].getY() && elevation == nodes[i].getElevation()) {
				return nodes[i];
			}
		}
		throw new NodeNotFoundException("Node was not found.");
	}
	//TODO: Remove node (do we need this?)
	//public boolean removeNode() {
	//}
	
}
