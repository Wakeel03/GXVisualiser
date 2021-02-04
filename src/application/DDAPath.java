package application;

public class DDAPath {
	
	private double[] endPoints;
	private boolean dashed, dotted;
	private int thickness;

	public DDAPath(double[] endPoints, boolean dashed, boolean dotted, int thickness) {
		this.endPoints = endPoints;
		this.dashed = dashed;
		this.dotted = dotted;
		this.thickness = thickness;
	}
	
	public int getThickness() {
		return thickness;
	}
	
	public double[] getEndPoints() {
		return endPoints;
	}

	public void setEndPoints(double[] endPoints) {
		this.endPoints = endPoints;
	}

	public boolean isDashed() {
		return dashed;
	}

	public void setDashed(boolean dashed) {
		this.dashed = dashed;
	}

	public boolean isDotted() {
		return dotted;
	}

	public void setDotted(boolean dotted) {
		this.dotted = dotted;
	}
}
