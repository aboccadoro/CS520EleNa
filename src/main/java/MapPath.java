
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
		nodesInPath = 1;
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
		nodesInPath++;
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

	//takes two indecies of nodes and sets the edge between them to be Active
	public void setEdgeActive(int n1, int n2, boolean set){
		MapNode a = nodelist[n1];
		MapNode b = nodelist[n2];
		for(int c = 0; c < a.getPairs().length; c++){
			DistPair[] pairs = a.getPairs();
			if(pairs[c].getNode().equals(b)){
				pairs[c].setActive(set);
			}
		}
		for(int c = 0; c < b.getPairs().length; c++){
			DistPair[] pairs = b.getPairs();
			if(pairs[c].getNode().equals(a)){
				pairs[c].setActive(set);
			}
		}
	}

	public int getNumberNodes(){
		return nodesInPath;
	}

	public MapNode getStartNode(){
		return startNode;
	}

	public static MapPath mergePaths(MapPath a, MapPath b){
		MapPath result = new MapPath(a.getStartNode());
		MapNode[] list = new MapNode[a.getNumberNodes() + b.getNumberNodes()];
		System.arraycopy(a.getNodeList(), 0, list, 0, a.getNumberNodes());
		System.arraycopy(b.getNodeList(), a.getNumberNodes(), list, 0, b.getNumberNodes());
		result.nodelist = list;
		result.nodesInPath = list.length;
		result.totalDistance = a.getTotalDistance() + b.getTotalDistance();
		result.startNode = list[0];
		result.currentNode = list[list.length - 1];
	}
}
