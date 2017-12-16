
public class MapPath {
	private double totalDistance;
	private MapNode startNode;
	private MapNode currentNode;
	private MapNode[] nodelist;
	private int nodesInPath;
	public MapPath(MapNode startNode) {
		this.startNode = startNode;
		currentNode = startNode;
		totalDistance = 0;
		nodesInPath = 1;
		nodelist = new MapNode[1];
		nodelist[0] = startNode;
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
		MapNode[] temp = new MapNode[nodelist.length + 1];
		for(int i = 0; i < nodelist.length; i++) {
			temp[i] = nodelist[i];
		}
		temp[nodelist.length] = node;
		nodelist = temp;
	}
	public MapPath getSubPath(int x, int y) {
		//MapNode[] sublist = new MapNode[y-x+1];
		MapPath sublist = new MapPath(this.startNode);
		for(int i = 0; i <= y-x; i++) {
			try{
				sublist.addNodeToPath(this.getNode(y-x+i));
			}
			catch(NodeNotFoundException e){
				e.printStackTrace();
			}
			
			//sublist[i] = nodelist[i+x-1];
		}
		return sublist;
	}
	public MapNode[] getNodeList() {
		return nodelist;
	}
	public MapNode getNode(int i){
		return nodelist[i];
	}
}
