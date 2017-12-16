
public class DistPair {
	private MapNode node;
	private double distance;
	public DistPair(MapNode node, double distance) {
		this.node = node;
		this.distance = distance;
	}
	public DistPair(double x, double y, double elevation, double distance) {
		this.node = new MapNode(x,y,elevation);
		this.distance = distance;
	}
	public MapNode getNode() {
		return node;
	}
	public double getDistance() {
		return distance;
	}
}
