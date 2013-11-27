package ui;

import java.io.Serializable;

public class SavableUserSettings implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String working_directory = "";
	
	public SavableUserSettings(String workingDir){
		working_directory = workingDir;
	}
	
	public String getWorkingDir(){
		return working_directory;
	}

	
}
