package application;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class DrawPolygon {
	private int sides, stroke;
	private String type;
	private double[] points;
	private ArrayList<Double> clicked_points;
	private Color color;
	private double mouseX, mouseY;
	private int size = 5;
	private Color stroke_color;
	
	public DrawPolygon(int sides, int stroke, Color color, String type, int size, Color stroke_color, double mouseX, double mouseY, ArrayList<Double> clicked_points) {
		this.sides = sides;
		this.type = type;
		this.stroke = stroke;
		this.stroke_color = stroke_color;
		this.color = color;
		this.size = size;
		this.clicked_points = clicked_points;
		
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		
		createPolygon();
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getSides() {
		return sides;
	}

	public void setSides(int sides) {
		this.sides = sides;
	}

	public int getStroke() {
		return stroke;
	}

	public void setStroke(int stroke) {
		this.stroke = stroke;
	}

	public String getType() {
		return type;
	}

	public Color getStrokeColor() {
		return stroke_color;
	}

	public void setStrokeColor(Color stroke_color) {
		this.stroke_color = stroke_color;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double[] createPolygon() {
		points = new double[sides * 2]; 
		
		if (type.equals("Regular")) {
			for (int i = 0; i < sides * 2; i +=  2) {
				points[i] = mouseX + size * Math.cos(i *Math.PI / sides);
				points[i+1] = mouseY + size * Math.sin(i * Math.PI / sides);
			}
		}
		
		else if (type.equals("Irregular")){
			double[] pts = new double[clicked_points.size()];
			for (int i = 0; i < clicked_points.size(); i++) {
				pts[i] = clicked_points.get(i).doubleValue();
			}
			setPoints(pts);
			/*int minX = 1;
			int maxX = 8;
			int minY = 1;
			int maxY = 8;
			for (int i = 0; i < sides * 2; i += 2) {
				int newRandX = (int)(Math.random() * (maxX - minX + 1) + minX);
				int newRandY = (int)(Math.random() * (maxY - minY + 1) + minY);
				points[i] = mouseX + newRandX * Math.cos(i  * Math.PI / sides);
				points[i+1] = mouseY + newRandY * Math.sin(i * Math.PI / sides);
			}*/     
		}else {
			setPoints(new double[] {mouseX, mouseY});
		}
		return points;
	}

	public double[] getPoints() {
		return points;
	}

	public void setPoints(double[] points) {
		this.points = points;
	}
}
