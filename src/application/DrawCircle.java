package application;

import java.util.ArrayList;

import javafx.scene.paint.Color;

public class DrawCircle {
	
	private ArrayList<Double> points;
	private double radius;
	private int stroke;
	private Color color;
	
	private double centerX, centerY;
	
	public DrawCircle(double radius, int stroke, Color color, double centerX, double centerY) {
		this.radius = radius;
		this.stroke = stroke;
		this.color = color;
		this.centerX = centerX;
		this.centerY = centerY;
	}
	
	public ArrayList<Double> MidCircleAlgoBresenham(int cell_size, float scale, double centerx, double centery) {
		ArrayList<Double> pixel_coordinates = new ArrayList<Double>();
		
		double x,y,d;
	    y = radius * cell_size / scale;
	    x = 0;
		
		//pixel_coordinates.add(centerX);
		//pixel_coordinates.add(centerY);
		
		d = (3 - 2 * y);
	    while (x <= y) {
	        if (d <= 0) {
	            d = d + (4 * x + 6);
	        } else {
	            d = d + 4 * (x - y) + 10;
	            y--;
	        }
	        x++;
	        
	        pixel_coordinates.add(centerx + x);
	        pixel_coordinates.add(centery + y);
	        
	        pixel_coordinates.add(centerx - x);
	        pixel_coordinates.add(centery + y);
	        
	        pixel_coordinates.add(centerx + x);
	        pixel_coordinates.add(centery - y);
	        
	        pixel_coordinates.add(centerx - x);
	        pixel_coordinates.add(centery - y);
	       
	        pixel_coordinates.add(centerx + y);
	        pixel_coordinates.add(centery + x);
	        
	        pixel_coordinates.add(centerx - y);
	        pixel_coordinates.add(centery + x);
	       
	        pixel_coordinates.add(centerx + y);
	        pixel_coordinates.add(centery - x);
	        
	        pixel_coordinates.add(centerx - y);
	        pixel_coordinates.add(centery - x);
	    }
	    
	    points = pixel_coordinates;
	    
		return pixel_coordinates;
	}

	public double getCenterX() {
		return centerX;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

	public ArrayList<Double> getPoints() {
		return points;
	}

	public void setPoints(ArrayList<Double> points) {
		this.points = points;
	}

	public double  getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getStroke() {
		return stroke;
	}

	public void setStroke(int stroke) {
		this.stroke = stroke;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
