package ui.models;

import java.awt.Color;
import java.util.ArrayList;

import ui.utilities.DeleteMode;
import ui.utilities.LabelledImage;
import ui.utilities.Point;
import ui.utilities.PointEditState;
import ui.utilities.Region;

public class ActiveImageModel {

	private LabelledImage current_image;
	private ArrayList<Point> activePoints = null;
	private PointEditState state = PointEditState.CREATE;
	private DeleteMode d_state = DeleteMode.ASK_DELETE;
	private Point editingPoint;
	private Point originalPoint;
	private Color editorColor = new Color(0,0,1,0.3f);
	
	public void setImage(LabelledImage image){
		current_image = image;
	}
	
	public LabelledImage getCurrentImage(){
		return current_image;
	}

	public ArrayList<Point> getActivePoints() {
		return activePoints;
	}
	
	public void openActivePoints(){
		this.activePoints = new ArrayList<Point>();
	}
	
	public void closeActivePoints(String RegionName){
		if (activePoints != null && activePoints.size() > 2) {
			//Use following line to have snap effect close region
			//activePoints.set(activePoints.size()-1, activePoints.get(0));
			Region reg = new Region(RegionName, activePoints);
			reg.setRegionColor(editorColor);
			current_image.addRegion(reg);
			activePoints = null;
		}
	}
	
	public void clearActivePoints(){
		activePoints = null;
	}

	public void setActivePoints(ArrayList<Point> activeRegionPoints) {
		this.activePoints = activeRegionPoints;
	}
	
	public void addActivePoint(Point p){
		if (activePoints != null) {
			activePoints.add(p);
		}
	}
	
	public void setEditPoint(Point p){
		for (Region reg: current_image.getRegions()){
			for(Point pi: reg.getPoints()){
				if (nearPoint(pi, p)){
					editingPoint = p;
					originalPoint = pi;
					System.out.println("Edit Point Set");
					return;
				}
			}
		}
	}

	public void editSavedPoint(Point p) {
		if (editingPoint != null){
			editingPoint = p;
		}
		for (Region reg: current_image.getRegions()){
			for(int i = 0; i < reg.getPoints().size(); i++){
				Point pi = reg.getPoints().get(i);
				if (nearPoint(pi, originalPoint)){
					/*System.out.println("Edit Point Closed");
					System.out.println("Original Point:" + originalPoint);
					System.out.println(" X: "+ pi.getX() + " Y: " + pi.getY());
					System.out.println("Edited Point:" + editingPoint);
					System.out.println(" X: "+ editingPoint.getX() + " Y: " + editingPoint.getY());
					*/
					reg.replacePoint(i, editingPoint);
					
					originalPoint = editingPoint;
					return;
				}
			}
		}
		
	}
	
	public void finishSavedPointEdit(Point p){
		if (editingPoint != null){
			editingPoint = p;
		}
		for (Region reg: current_image.getRegions()){
			for(int i = 0; i < reg.getPoints().size(); i++){
				Point pi = reg.getPoints().get(i);
				if (nearPoint(pi, originalPoint)){
					/*
					System.out.println("Edit Point Closed");
					System.out.println("Original Point:" + originalPoint);
					System.out.println(" X: "+ pi.getX() + " Y: " + pi.getY());
					System.out.println("Edited Point:" + editingPoint);
					System.out.println(" X: "+ editingPoint.getX() + " Y: " + editingPoint.getY());
					*/
					reg.replacePoint(i, editingPoint);
					
					editingPoint = null;
					originalPoint = null;
					return;
				}
			}
		}
	}
	
	public void editActivePoint(int i, Point p){
		if (activePoints != null && activePoints.size()> i-1) {
			activePoints.set(i,p);
		}
	}
	
	public void editActivePoint(Point p){
		if (activePoints != null && activePoints.size()> 0) {
			editActivePoint(activePoints.size()-1, p);
		}
	}
	
	public Boolean nearPoint(Point a, Point b){
		if (Math.abs(a.getX()- b.getX()) < 6 && Math.abs(a.getY()- b.getY()) < 6){
			System.out.println("Near Point");
			return true;
		} else {
			return false;
		}
	}

	public PointEditState getEditState() {
		return state;
	}

	public void setEditState(PointEditState state) {
		this.state = state;
	}
	
	public String getEditStatusString(){
		switch(state){
		case CREATE:
			return "Click Image to open a label";
		case EDIT:
			return "Click here to cancel label";
		case DELETE:
			return "Warning ";
		default:
			return "Click Image to open a label";
		}
	}

	public DeleteMode getD_state() {
		return d_state;
	}

	public void setD_state(DeleteMode d_state) {
		this.d_state = d_state;
	}
	
	public void deleteClickedPoint(Point p){
		Boolean wasActive = false;
		if (activePoints != null && activePoints.size() != 0) {
			for (Point pi : activePoints) {
				if (nearPoint(pi, p)) {
					activePoints.remove(pi);
					wasActive = true;
					break;
				}
			}
		}
		if (!wasActive){
			for (Region reg: current_image.getRegions()){
				Boolean found =reg.deletePoint(p);
				if (found){
					System.out.println("Found");
					break;
				}
			}
		}
	}

	public void cleanRegions() {
		for (int i = 0; i < current_image.getRegions().size(); i++){
			if (current_image.getRegion(i).getPoints().size()==0 ||current_image.getRegion(i).getPoints() ==null){
				current_image.deleteRegion(i);
				
			}
		}
		
	}

	public Color getEditorColor() {
		return editorColor;
	}

	public void setEditorColor(Color editorColor) {
		this.editorColor = editorColor;
	}





	
	
	
}
