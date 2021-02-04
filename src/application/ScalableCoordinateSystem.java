package application;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Stack;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

public class ScalableCoordinateSystem {
	
	private Pane graph;
	
	private int WIDTH = (int)(Menu.WIDTH - 360 - 200);
	private int HEIGHT = (int)(Menu.HEIGHT / 1.2);
	
	private int cell_size = 50;
	private int cell_size_max = 100;
	private int cell_size_min = 50;
	
	private int scroll_speed = 10;
	
	private int midPointV, midPointH;
	
	private float scale = 1;
	
	private Stack<String> shape_state;	
	private ArrayList<DrawPolygon> shapes;
	private ArrayList<double[]> point_circles;
	private ArrayList<BresenhamPath> bresenham_paths;
	private ArrayList<DDAPath> dda_paths;
	private ArrayList<DrawCircle> circles;
	
	private Button btn_reset;
	private Button btn_undo;
	private Menu menu;

	public ScalableCoordinateSystem(Menu menu) {
		this.menu = menu;
		shapes = new ArrayList<DrawPolygon>();
		point_circles = new ArrayList<double[]>();
		circles = new ArrayList<DrawCircle>();
		bresenham_paths = new ArrayList<BresenhamPath>();
		dda_paths = new ArrayList<DDAPath>();
		
		shape_state = new Stack<String>();		
	}
	
	public Pane drawGraph() {
		 graph = new Pane();
		 
		 graph.setStyle("-fx-background-color: #f3f3f3;");
		 graph.setPrefSize(WIDTH, HEIGHT);
		 graph.setMaxSize(WIDTH, HEIGHT);
		 graph.setMinSize(WIDTH, HEIGHT);
		 
		 Rectangle clip = new Rectangle(WIDTH, HEIGHT);
		 graph.setClip(clip);
		 
		 midPointV = HEIGHT / 2;
		 midPointH = WIDTH / 2;
		 
		 drawAllShapes();
		 drawAllBresenhamPaths();
		 drawAllDDAPaths();
		 drawAllCircles();
		 drawAllPointCircles();
		 
		 createLineAxes();
		 addAxesText();
		 
		
		 
		 HBox graph_action_btn = new HBox(20);
		 
		 btn_reset = new Button("Reset");
		 btn_reset.getStyleClass().add("btn-reset");
		 
		 btn_undo = new Button("Undo");
		 btn_undo.setLayoutX(110);
		 btn_undo.getStyleClass().add("btn-reset");
		 
		 btn_reset.setOnAction(e -> {
			 clearGraph();
			 menu.drawGraph();
		 });
		 
		 btn_undo.setOnAction(e -> {
			 if (shape_state.empty()) return;
			 String last_shape = shape_state.pop();
			 
			 switch (last_shape) {
			 case "Irregular":
				 if (shapes.size() > 0)
					 for (int i = 0; i < shapes.get(shapes.size() - 1).getSides(); i++) {
						 point_circles.remove(point_circles.size() - 1);
						 shape_state.pop();
					 }
					 shapes.remove(shapes.size() - 1);
				 break;
			 case "Regular":
				 if (shapes.size() > 0)
					 shapes.remove(shapes.size() - 1);
				 break;
			 case "bresenham":
				 if (bresenham_paths.size() > 0)
					 for (int i = 0; i < 2; i++) {
						 point_circles.remove(point_circles.size() - 1);
						 shape_state.pop();
					 }
					 bresenham_paths.remove(bresenham_paths.size() - 1);
				 break;
			 case "dda":
				 if (dda_paths.size() > 0)
					 for (int i = 0; i < 2; i++) {
						 point_circles.remove(point_circles.size() - 1);
						 shape_state.pop();
					 }
					 dda_paths.remove(dda_paths.size() - 1);
				 break;
			 case "circle":
				 if (circles.size() > 0)
					 circles.remove(circles.size() - 1);
				 break;
			 case "circle_point":
				 if (point_circles.size() > 0)
					 menu.getClickedPoints().remove(menu.getClickedPoints().size() - 1);
				 	 menu.getClickedPoints().remove(menu.getClickedPoints().size() - 1);
					 point_circles.remove(point_circles.size() - 1);
				 break;
			 }
			 
			 menu.drawGraph();
		 });
		 
		 graph_action_btn.getChildren().addAll(btn_reset, btn_undo);
		 
		 graph.getChildren().addAll(graph_action_btn);
		 
		 return graph;
	}
	
	public Stack<String> getShape_state() {
		return shape_state;
	}

	public void setShape_state(Stack<String> shape_state) {
		this.shape_state = shape_state;
	}

	public ArrayList<double[]> getPoint_circles() {
		return point_circles;
	}

	public void setPoint_circles(ArrayList<double[]> point_circles) {
		this.point_circles = point_circles;
	}

	private void createLineAxes() {
		
		Line yAxis = new Line(midPointH, 0, midPointH, HEIGHT);
		yAxis.setStroke(Color.BLACK);
		
		graph.getChildren().add(yAxis);
		
		//Vertical Lines
		for (int i = midPointH + cell_size; i < WIDTH; i += cell_size) {
					
			Line line= new Line(i, 0, i, HEIGHT);
			line.setStroke(Color.LIGHTGRAY);			
			
			graph.getChildren().add(line);
		}
		
		for (int i = midPointH; i > 0; i -= cell_size) {
			
			Line line= new Line(i, 0, i, HEIGHT);
			line.setStroke(Color.LIGHTGRAY);			
			
			graph.getChildren().add(line);
		}
		
		Line xAxis = new Line(0, midPointV, WIDTH, midPointV);
		xAxis.setStroke(Color.BLACK);
		graph.getChildren().add(xAxis);
		
		//Horizontal Lines
		for (int j = midPointV + cell_size; j < HEIGHT; j+= cell_size) {
			
			Line line= new Line(0, j, WIDTH, j);
			
			line.setStroke(Color.LIGHTGRAY);

			graph.getChildren().add(line);
		}
		
		for (int j = midPointV; j > 0; j -= cell_size) {
			
			Line line= new Line(0, j, WIDTH, j);
			
			line.setStroke(Color.LIGHTGRAY);

			graph.getChildren().add(line);
		}
		
	}
	
	private void addAxesText() {

		int index = 0;
		Float value = 0f;
		
		for (int i = midPointH; i < WIDTH; i += cell_size) {
			value = index * scale;
			Text t = new Text (i, midPointV, value.toString());
			index += 1;
			
			if (index == 1) continue;
			t.setFill(Color.DARKBLUE);
			graph.getChildren().add(t);
		}
		
		index = 0;
		for (int i = midPointH; i > 0; i -= cell_size) {
			value = index * scale;
			Text t = new Text (i, midPointV, value.toString());
			index -= 1;
			
			if (index == -1) continue;
			t.setFill(Color.DARKBLUE);
			graph.getChildren().add(t);
		}

		index = 0;
		for (int j = midPointV; j < HEIGHT; j += cell_size) {
			value = index * scale;
			Text t = new Text (midPointH, j, value.toString());
			index -= 1;
			
			if (index == -1) continue;
			t.setFill(Color.DARKBLUE);
			graph.getChildren().add(t);
		}
		
		index = 0;
		for (int j = midPointV; j > 0; j -= cell_size) {
			value = index * scale;
			Text t = new Text (midPointH, j, value.toString());
			index += 1;
			
			if (index == 1) continue;
			t.setFill(Color.DARKBLUE);
			graph.getChildren().add(t);
		}
		
	}
	
	public void zoom(double deltaY) {
		if (deltaY < 0) {
			cell_size += scroll_speed;
			if (cell_size > cell_size_max) {
				cell_size = cell_size_min;
				scale /= 2;
			}
		}else {
			cell_size -= scroll_speed;
			if (cell_size < cell_size_min) {
				cell_size = cell_size_max;
				scale *= 2;
			}
		}
	}
	
	public void addShape(DrawPolygon polygon) {
		shapes.add(polygon);
		shape_state.push(polygon.getType());
	}
	
	public double[] getRealPosition(double[] points) {
		double[] actualPoints = new double[points.length];
		
		for (int i = 0; i < points.length; i++) {
			if (i % 2 == 0) {
				//x values
				actualPoints[i] = (points[i] * cell_size * (1 / scale)) + (WIDTH/2);
			}else {
				//y values
				actualPoints[i] = -(points[i] * cell_size * (1 / scale))  + (HEIGHT / 2);
			}
			
		}
		
		return actualPoints;
		
	}
	
	private void drawAllShapes() {
		for (DrawPolygon p : shapes) {
			Polygon polygon = new Polygon(getRealPosition(p.getPoints()));
			polygon.setFill(p.getColor());
			polygon.setStrokeWidth(p.getStroke());
			polygon.setStroke(p.getStrokeColor());
			graph.getChildren().add(polygon);
		}
	}
	
	private void drawAllPointCircles() {
		for (double[] p : point_circles) {
			double[] pts = getRealPosition(p);
			Circle circle_point = new Circle(pts[0], pts[1], 6);
			graph.getChildren().add(circle_point);
		}	
	}
	
	public void addPointCircle(double[] points) {
		point_circles.add(points);
		shape_state.push("circle_point");
	}
	
	private void drawAllBresenhamPaths() {
		for (BresenhamPath p : bresenham_paths) {
			ArrayList<Double> path_points = bresenhamAlgorithm(p.getEndPoints());
			int dashed_count = 0;
			int dotted_count = 0;
			
			int dash_length = 10 * p.getThickness();
			int dot_length = 2 * p.getThickness();
			
			for (int i = 0; i < path_points.size()-1; i+= 2) {
				if (dashed_count > dash_length) {
					dashed_count = 0;
				}
				if (dotted_count > dot_length) {
					dotted_count = 0;
				}
				if (p.isDashed() && dashed_count++ > dash_length / 2) continue;
				if (p.isDotted() && dotted_count++ > dot_length / 2) continue;
				Rectangle pixel = new Rectangle(path_points.get(i), path_points.get(i + 1), p.getThickness(), p.getThickness());
				graph.getChildren().add(pixel);
			}
			
		}
	}
	
	private void drawAllDDAPaths() {
		for (DDAPath p : dda_paths) {
			ArrayList<Double> path_points = DDAAlgorithm(p.getEndPoints());
			int dashed_count = 0;
			int dotted_count = 0;
			
			int dash_length = 10 * p.getThickness();
			int dot_length = 2 * p.getThickness();
			
			for (int i = 0; i < path_points.size()-1; i+= 2) {
				if (dashed_count > dash_length) {
					dashed_count = 0;
				}
				if (dotted_count > dot_length) {
					dotted_count = 0;
				}
				if (p.isDashed() && dashed_count++ > dash_length / 2) continue;
				if (p.isDotted() && dotted_count++ > dot_length / 2) continue;
				Rectangle pixel = new Rectangle(path_points.get(i), path_points.get(i + 1), p.getThickness(), p.getThickness());
				graph.getChildren().add(pixel);
			}
			
		}
	}
	
	private void drawAllCircles() {
		for (DrawCircle circle : circles) {
			double[] centers = getRealPosition(new double[] {circle.getCenterX(), circle.getCenterY()});
			ArrayList<Double> path_points = circle.MidCircleAlgoBresenham(cell_size, scale, centers[0], centers[1]);
			double[] pts = new double[path_points.size()];
			for (int i = 0; i < path_points.size(); i++) {
				pts[i] = path_points.get(i);
			}
			for (int i = 0; i < pts.length-1; i+= 2) {
				Rectangle pixel = new Rectangle(pts[i], pts[i+1], circle.getStroke(), circle.getStroke());
				pixel.setFill(circle.getColor());
				graph.getChildren().add(pixel);
			}
		}
	}
	
	private ArrayList<Double> DDAAlgorithm(double[] endPoints){
		ArrayList<Double> pixel_coordinates = new ArrayList<Double>();
		
		double[] pts = endPoints;
		
		pts = getRealPosition(pts);
		
		double valueX = pts[0];
		double valueY = pts[1];
		
		double tmp_x, tmp_y;
		
		double dy = pts[3] - pts[1];
		double dx = pts[2] - pts[0];
		
		double count;
	   	
	   	if (Math.abs(dx) > Math.abs(dy))
	   		count = Math.abs(dx);
	   	else
	   		count = Math.abs(dy);
	   	
	   	float xInc = (float)dx/(float)count;
	   	float yInc = (float)dy/(float)count;
	   	
	   	for (int i = 0; i < count; i++) {
	   		pixel_coordinates.add(valueX);
	   		pixel_coordinates.add(valueY);
	   		
	   		valueX += xInc;
	   		valueY += yInc;
	   	}
		
		return pixel_coordinates;
	}
	
	private ArrayList<Double> bresenhamAlgorithm(double[] endPoints) {		
		ArrayList<Double> x = new  ArrayList<Double>();
		ArrayList<Double> y = new  ArrayList<Double>();
		
		ArrayList<Double> pixel_coordinates = new ArrayList<Double>();
			
		double[] pts = endPoints;
						
			pts = getRealPosition(pts);
	
			double prelimX[]= {pts[0], pts[2]};
			double prelimY[]= {pts[1], pts[3]};
			
			double deltaY = prelimY[1] - prelimY[0];
			double deltaX = prelimX[1] - prelimX[0];
			
			x.add(prelimX[0]);
			y.add(prelimY[0]);
			
			short x_direction = 1, y_direction = 1;
			
			if (deltaX < 0) {
				deltaX = -deltaX;
	    		x_direction = -1;
	    	}
	    	
	    	if(deltaY < 0) {
	    		deltaY = -deltaY;
	    		y_direction = -1;
	    	}
	    	
	    	if (deltaX > deltaY) {
	    		double p = 2 * deltaY - deltaX;
	    		double dydx2 = 2 * deltaY - 2 * deltaX;
				double dy2 = 2 * deltaY;
				
				for (int i = 0; i < Math.abs(deltaX); i++) {
					x.add(x.get(i) + x_direction);
					
					if(p < 0) {
						p += dy2;
						y.add(y.get(i));
					}
					else {
						p += dydx2;
						y.add(y.get(i) + y_direction);
					}
					
					pixel_coordinates.add(x.get(i));
					pixel_coordinates.add(y.get(i));
				}
				
	    	}else {
	    		double p = 2 * deltaX - deltaY;
	    		double dx2 = 2 * deltaX;
	    		double dxdy2 = 2 * deltaX - 2 * deltaY;
	    		
	    		for (int i = 0; i < Math.abs(deltaY); i++) {
	    			y.add(y.get(i) + y_direction);
	    			
	    			if (p < 0) {
	    				p += dx2;
	    				x.add(x.get(i));
	    			}else {
	    				p += dxdy2;
	    				x.add(x.get(i) + x_direction);
	    			}
	    			
		    		pixel_coordinates.add(x.get(i));
					pixel_coordinates.add(y.get(i));
	    		}
	    	}
				
		return pixel_coordinates;
	}
	
	public void addBresenhamPath(double[] path, boolean dashed, boolean dotted, int thickness) {
			bresenham_paths.add(new BresenhamPath(path, dashed, dotted, thickness));
			shape_state.push("bresenham");

	}
	
	public void addDDAPath(double[] path, boolean dashed, boolean dotted, int thickness) {
		dda_paths.add(new DDAPath(path, dashed, dotted, thickness));
		shape_state.push("dda");
	}
	
	public void addCircle(int radius, int stroke, Color color, double centerX, double centerY) {
		circles.add(new DrawCircle(radius, stroke, color, centerX, centerY));
		shape_state.push("circle");

	}

	public double[] getMouseCoordinates(double mouseX, double mouseY) {
		double x = ((mouseX - (WIDTH/2)) / cell_size) * scale;
		double y = (((HEIGHT/2) - mouseY) / cell_size) * scale;
		
		return new double[] {x, y};
	}

	public void clearGraph() {
		menu.getClickedPoints().clear();
		
		 shapes.clear();
		 bresenham_paths.clear();
		 dda_paths.clear();
		 circles.clear();
		 point_circles.clear();
	}
	
}
