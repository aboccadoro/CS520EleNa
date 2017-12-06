
public class MapPath {
	private double totalDistance;
	private MapNode startNode;
	private MapNode currentNode;
	public MapPath(MapNode startNode) {
		this.startNode = startNode;
		currentNode = startNode;
		totalDistance = 0;
	}
	public double getTotalDistance() {
		return totalDistance;
	}
	public void addNodeToPath(MapNode node) throws NodeNotFoundException {
		if(currentNode.hasNode(node)) {
			totalDistance += currentNode.getDistanceToNode(node);
			currentNode = node;
		}
		else {
			throw new NodeNotFoundException("Node was not found.");
		}
	}
	
}
