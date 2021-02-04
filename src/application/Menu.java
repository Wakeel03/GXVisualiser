package application;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import javafx.scene.*;

public class Menu {
	private Stage window;
	
	public static  int WIDTH;
	public static int HEIGHT;
	
	private GridPane grid;
	
	private ScalableCoordinateSystem sc;
	private Pane graph;
	
	private String selected_tool = "";
	
	private String selected_line_algo = "DDA";
	private String selected_line_option = "Solid";
	
	private Color color;
	private Color polygon_stroke_color = Color.BLACK;
	private Color circle_color = Color.BLACK;
	
	CheckBox cb_no_fill;
	
	private Spinner<Integer> num_sides_picker;
	private Spinner<Integer> stroke_picker;
	private Spinner<Integer> size_picker;
	private Spinner<Integer> radius_picker;
	private Spinner<Integer> circle_stroke_picker;
	private Spinner<Integer> thickness_picker;
	
	private ArrayList<Double> clicked_points;
	
	private String type = "Regular";
	
	public Menu(Stage window) {
		this.window = window;
		clicked_points = new ArrayList<Double>();
		
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		WIDTH = (int)(primaryScreenBounds.getWidth());
		HEIGHT = (int)(primaryScreenBounds.getHeight());
		
		/*Rectangle gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		WIDTH = (int) gd.getWidth();
		HEIGHT = (int)gd.getHeight();*/
	}

	public Scene getMenuScene() {
		ScrollPane s1 = new ScrollPane();
		s1.setMaxHeight(HEIGHT);
		s1.setMinHeight(HEIGHT);
		s1.setPrefHeight(HEIGHT);
		
		s1.setMaxWidth(380);
		s1.setMinWidth(380);
		s1.setPrefWidth(380);
		
		VBox sidebar = createSidebar();

		sidebar.setMinHeight(HEIGHT);
		
		s1.setContent(sidebar);
		
		sc = new ScalableCoordinateSystem(this);
		
		grid = new GridPane();
		grid.setHgap(100);
		
		GridPane.setConstraints(s1, 0, 0);
		
		grid.getChildren().add(s1);
		
		drawGraph();
		
		//grid.setAlignment(Pos.CENTER_LEFT);
		Scene scene = new Scene(grid, WIDTH, HEIGHT);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		return scene;
	}
		
	public void drawGraph() {
		grid.getChildren().remove(graph);
        graph = sc.drawGraph();
        addMouseScrolling(graph);
        addMouseClickListener(graph);
        GridPane.setConstraints(graph, 1, 0);
        grid.getChildren().add(graph);
	}
	
	public void addMouseScrolling(Node node) {
        node.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            sc.zoom(deltaY);
            drawGraph();
        });
    }
	
	public void addMouseClickListener(Node node) {
		node.setOnMouseClicked(e -> {
			if (selected_tool.equals("")) return;
			double[] mouseCoordinates = sc.getMouseCoordinates(e.getX(), e.getY());
			//System.out.println(mouseCoordinates[0] + ", " + mouseCoordinates[1]);
			
			if (selected_tool.equals("Line Algorithm")) {
				clicked_points.add(mouseCoordinates[0]);
				clicked_points.add(mouseCoordinates[1]);
				
				//DrawPolygon circle_point = new DrawPolygon(0, 0, color, "circle_point", 0, mouseCoordinates[0], mouseCoordinates[1], clicked_points);
				sc.addPointCircle(new double[] {mouseCoordinates[0], mouseCoordinates[1]});
				
				if (clicked_points.size() == 4) {
					if (selected_line_algo.equals("Bresenham Algorithm"))
						sc.addBresenhamPath(addLine(), selected_line_option.equals("Dashed"), selected_line_option.equals("Dotted"), thickness_picker.getValue());
					else if (selected_line_algo.equals("DDA"))
						sc.addDDAPath(addLine(), selected_line_option.equals("Dashed"), selected_line_option.equals("Dotted"), thickness_picker.getValue());
					clicked_points.clear();
				}
			}
			
			else if (selected_tool.equals("Polygon Drawing")) {
				if (type.equals("Irregular")) {
					clicked_points.add(mouseCoordinates[0]);
					clicked_points.add(mouseCoordinates[1]);
					sc.addPointCircle(new double[] {mouseCoordinates[0], mouseCoordinates[1]});
				}
				
				if (type.equals("Regular") || clicked_points.size() / 2 == num_sides_picker.getValue()) {
					DrawPolygon polygon = new DrawPolygon(num_sides_picker.getValue(), stroke_picker.getValue(), cb_no_fill.isSelected() ? null : color, type, size_picker.getValue(), polygon_stroke_color, mouseCoordinates[0], mouseCoordinates[1], clicked_points);
					sc.addShape(polygon);
				}
							
				if (clicked_points.size() / 2 == num_sides_picker.getValue()) clicked_points.clear();
			}
			else {
				sc.addCircle(radius_picker.getValue(), circle_stroke_picker.getValue(), circle_color, mouseCoordinates[0], mouseCoordinates[1]);
				
			}
			
			drawGraph();
			
		});
	}
	
	private VBox createSidebar() {
		VBox sidebar = new VBox(40);
		//sidebar.prefWidthProperty().bind(window.widthProperty().multiply(0.2));
		//sidebar.setPrefWidth(WIDTH / 4);
		sidebar.getStyleClass().add("sidebar");
		//sidebar.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0), null, null)));
		
		Label lbl_title = new Label("GX VISUALISER");
		lbl_title.getStyleClass().add("sidebar__title");
		sidebar.getChildren().add(lbl_title);
		
		VBox tools = new VBox(20);
		
		Label lbl_tools = new Label("TOOLS");
		lbl_tools.getStyleClass().add("sidebar__subtitle");
		
		HBox tool_line = new HBox();
		Label lbl_line = new Label("Line");
		lbl_line.getStyleClass().add("sidebar__tool");
		Button btn_line = new Button(">");
		
		btn_line.getStyleClass().add("sidebar__btn");
		tool_line.getChildren().addAll(lbl_line, btn_line);
				
		HBox tool_polygon = new HBox();
		Label lbl_polygon = new Label("Polygon");
		lbl_polygon.getStyleClass().add("sidebar__tool");
		Button btn_polygon = new Button(">");
	
		btn_polygon.getStyleClass().add("sidebar__btn");
		tool_polygon.getChildren().addAll(lbl_polygon, btn_polygon);
		
		HBox tool_circle = new HBox();
		Label lbl_circle = new Label("Circle");
		lbl_circle.getStyleClass().add("sidebar__tool");
		Button btn_circle = new Button(">");
		
		btn_circle.getStyleClass().add("sidebar__btn");
		tool_circle.getChildren().addAll(lbl_circle, btn_circle);
		
		VBox line_algo_tools = lineAlgoTools();
		VBox draw_polygons_tools = drawPolygonsTools();
		VBox circle_algo_tools= circleAlgoTools();
		
		btn_line.setOnAction(e -> {
			if (selected_tool.equals("Line Algorithm")) {
				tools.getChildren().removeAll(line_algo_tools, tool_polygon, tool_circle);
				tools.getChildren().addAll(tool_polygon, tool_circle);
				selected_tool = "";
			}else {
				selected_tool = "Line Algorithm";
				tools.getChildren().removeAll(circle_algo_tools, draw_polygons_tools, tool_polygon, tool_circle);
				tools.getChildren().addAll(line_algo_tools, tool_polygon, tool_circle);
			}
		});
		
		btn_polygon.setOnAction(e -> {
			if (selected_tool.equals("Polygon Drawing")) {
				tools.getChildren().removeAll(draw_polygons_tools, tool_circle);
				tools.getChildren().addAll(tool_circle);
				selected_tool = "";
			}else {
				selected_tool = "Polygon Drawing";
				tools.getChildren().removeAll(line_algo_tools, circle_algo_tools, tool_circle);
				tools.getChildren().addAll(draw_polygons_tools, tool_circle);
			}
		});
		
		btn_circle.setOnAction(e -> {
			if (selected_tool.equals("Circle Drawing")) {
				tools.getChildren().removeAll(circle_algo_tools);
				selected_tool = "";
			}else {
				selected_tool = "Circle Drawing";
				tools.getChildren().removeAll(line_algo_tools, draw_polygons_tools);
				tools.getChildren().addAll(circle_algo_tools);
			}
		});
		
		tools.getChildren().addAll(lbl_tools, tool_line, tool_polygon, tool_circle);
		sidebar.getChildren().add(tools);
		
		/*VBox color_palette = new VBox(20);
		color_palette.getStyleClass().add("color_palette");
		
		Label lbl_color_palette = new Label("COLOR PALETTE");
		lbl_color_palette.getStyleClass().add("sidebar__subtitle");
		
		HBox pick_color = new HBox(20);
		Label lbl_pick_color = new Label("Pick a color");
		lbl_pick_color.getStyleClass().add("color_palette__lbl");
		Button btn_color = new Button("");
		btn_color.getStyleClass().add("color_palette__btn");
		pick_color.getChildren().addAll(lbl_pick_color, btn_color);
		
		color_palette.getChildren().addAll(lbl_color_palette, pick_color);
		sidebar.getChildren().add(color_palette);*/
		
		return sidebar;
	}
	
	private VBox lineAlgoTools() {
		VBox toolbar = new VBox(22);
		toolbar.getStyleClass().add("toolbar");
		
		VBox choose_algo = new VBox(12);
		Label lbl_choose_algo = new Label("Choose an algorithm");
		
		String algorithms[] = {"DDA", "Bresenham Algorithm"}; 
		ComboBox algo_combo_box = new ComboBox(FXCollections.observableArrayList(algorithms));
		algo_combo_box.setValue("DDA");
		algo_combo_box.setOnAction((event) -> {
		    int selectedIndex = algo_combo_box.getSelectionModel().getSelectedIndex();
		    Object selectedItem = algo_combo_box.getSelectionModel().getSelectedItem();
		    
		    selected_line_algo = (String) algo_combo_box.getValue();
		});
		
		algo_combo_box.getStyleClass().add("combo_box");
		
		choose_algo.getChildren().addAll(lbl_choose_algo, algo_combo_box);
		
		VBox choose_option = new VBox(12);
		Label lbl_choose_options = new Label("Choose an option");
		
		String options[] = {"Solid", "Dashed", "Dotted"}; 
		ComboBox options_combo_box = new ComboBox(FXCollections.observableArrayList(options));
		options_combo_box.setValue("Solid");
		
		options_combo_box.setOnAction((event) -> {
		    int selectedIndex = options_combo_box.getSelectionModel().getSelectedIndex();
		    Object selectedItem = options_combo_box.getSelectionModel().getSelectedItem();
		    
		    selected_line_option = (String) options_combo_box.getValue();
		});
		
		options_combo_box.getStyleClass().add("combo_box");
		
		choose_option.getChildren().addAll(lbl_choose_options, options_combo_box);
		
		VBox line_thickness = new VBox(12);
		
		Label lbl_line_thickness = new Label("Thickness");
		
		thickness_picker = new Spinner(1, 3, 1);
		thickness_picker.setEditable(true);
		thickness_picker.setPrefSize(50, 30);
		
		line_thickness.getChildren().addAll(lbl_line_thickness, thickness_picker);
		//line_thickness.setAlignment(Pos.CENTER_LEFT);
		
		Label lbl_choose_points = new Label("Choose 2 points on the Graph");
		lbl_choose_points.getStyleClass().add("lbl_tip");
		lbl_choose_points.setAlignment(Pos.CENTER);
		
		toolbar.getChildren().addAll(choose_algo, choose_option, line_thickness, lbl_choose_points);
		toolbar.setAlignment(Pos.CENTER_LEFT);
		
		return toolbar;
	}
	
	private double[] addLine() {
		double[] pts = {clicked_points.get(0), clicked_points.get(1), clicked_points.get(2), clicked_points.get(3)};
		return pts;
	}
	
	private VBox drawPolygonsTools() {
		VBox toolbar = new VBox(22);
		toolbar.getStyleClass().add("toolbar_polygon");
		
		Label lbl_choose_options = new Label("Choose options");
		
		VBox polygon_type_vbox = new VBox(12);
		Label lbl_polygon_type = new Label("Type");
		
		String polygon_types[] = {"Regular", "Irregular"}; 
		ComboBox type_combo_box = new ComboBox(FXCollections.observableArrayList(polygon_types));
		type_combo_box.setValue("Regular");
		type_combo_box.setOnAction((event) -> {
		    int selectedIndex = type_combo_box.getSelectionModel().getSelectedIndex();
		    Object selectedItem = type_combo_box.getSelectionModel().getSelectedItem();
		    
		    type = (String) type_combo_box.getValue();
		    for (int i = 0; i < clicked_points.size() / 2; i++) {
		    	sc.getShape_state().pop();
		    	sc.getPoint_circles().remove(sc.getPoint_circles().size() - 1);
		    }
		    clicked_points.clear();
		    
		});
		
		type_combo_box.getStyleClass().add("combo_box");
			
		polygon_type_vbox.getChildren().addAll(lbl_polygon_type, type_combo_box);
		
		VBox polygon_total_sides = new VBox(12);
		
		Label lbl_polygon_sides = new Label("Vertices");
		
		num_sides_picker = new Spinner(3, 20, 1);
		num_sides_picker.setEditable(true);
		num_sides_picker.setPrefSize(50, 30);
		
		polygon_total_sides.getChildren().addAll(lbl_polygon_sides, num_sides_picker);		
		
		VBox polygon_fill = new VBox(12);
		
		HBox polygon_fill_subheading = new HBox(160);
		Label lbl_polygon_fill= new Label("Fill");
		cb_no_fill = new CheckBox("No Fill");
		cb_no_fill.setSelected(true);
		cb_no_fill.getStyleClass().add("check-box-fill");
		
		polygon_fill_subheading.getChildren().addAll(lbl_polygon_fill, cb_no_fill);
		polygon_fill_subheading.setAlignment(Pos.CENTER_LEFT);
		
		final ColorPicker color_picker = new ColorPicker(null);
		
		color_picker.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		    	 color = color_picker.getValue();
		    	 cb_no_fill.setSelected(false);
		     }
		 });
		//color_picker.getStyleClass().add("color_picker");

		polygon_fill.getChildren().addAll(polygon_fill_subheading, color_picker);
		
		VBox polygon_stroke = new VBox(12);
		
		Label lbl_polygon_stroke= new Label("Stroke");
			
		stroke_picker = new Spinner(0, 5, 1);
		stroke_picker.setEditable(true);
		stroke_picker.setPrefSize(50, 30);
		
		polygon_stroke.getChildren().addAll(lbl_polygon_stroke, stroke_picker);
		
		VBox polygon_stroke_color_vbox = new VBox(12);
		
		Label lbl_polygon_stroke_color= new Label("Stroke Color");
		
		final ColorPicker color_picker_polygon = new ColorPicker(Color.BLACK);
		
		color_picker_polygon.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		    	 polygon_stroke_color = color_picker_polygon.getValue();
		     }
		 });
		
		color_picker_polygon.getStyleClass().add("color_picker");

		polygon_stroke_color_vbox.getChildren().addAll(lbl_polygon_stroke_color, color_picker_polygon);
		
		VBox polygon_size_box = new VBox(12);
		
		Label lbl_polygon_size= new Label("Length");
		
		size_picker = new Spinner(0, 100, 1);
		size_picker.setEditable(true);
		size_picker.setPrefSize(50, 30);
		
		polygon_size_box.getChildren().addAll(lbl_polygon_size, size_picker);
		
		toolbar.getChildren().addAll(lbl_choose_options, polygon_type_vbox, polygon_total_sides, polygon_fill, polygon_stroke, polygon_stroke_color_vbox, polygon_size_box);
		toolbar.setAlignment(Pos.CENTER_LEFT);
		
		return toolbar;
	}
	
	public ArrayList<Double> getClickedPoints() {
		return clicked_points;
	}

	private VBox circleAlgoTools() {
		VBox toolbar = new VBox(22);
		toolbar.getStyleClass().add("toolbar");
		
		Label lbl_choose_options = new Label("Choose options");
			
		VBox circle_radius = new VBox(12);
		
		Label lbl_circle_radius = new Label("Radius");
		
		radius_picker = new Spinner(1, 100, 4);
		radius_picker.setEditable(true);
		radius_picker.setPrefSize(50, 30);
		
		circle_radius.getChildren().addAll(lbl_circle_radius, radius_picker);
		
		VBox circle_stroke_color = new VBox(12);
		
		Label lbl_circle_stroke= new Label("Stroke Color");
		
		final ColorPicker color_picker_circle = new ColorPicker(Color.BLACK);
		
		color_picker_circle.setOnAction(new EventHandler() {
		     public void handle(Event t) {
		    	 circle_color = color_picker_circle.getValue();
		     }
		 });
		
		color_picker_circle.getStyleClass().add("color_picker");

		circle_stroke_color.getChildren().addAll(lbl_circle_stroke, color_picker_circle);
		
		VBox circle_stroke = new VBox(12);
		
		Label lbl_polygon_stroke= new Label("Stroke");
		
		circle_stroke_picker = new Spinner(0, 5, 1);
		circle_stroke_picker.setEditable(true);
		circle_stroke_picker.setPrefSize(50, 30);
		
		circle_stroke.getChildren().addAll(lbl_polygon_stroke, circle_stroke_picker);
		
		toolbar.getChildren().addAll(lbl_choose_options, circle_radius, circle_stroke, circle_stroke_color);
		toolbar.setAlignment(Pos.CENTER);
		
		return toolbar;
	}
}
