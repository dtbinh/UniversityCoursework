/*Vmap - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2001  Joerg Mueller <joergmueller@bigfoot.com>
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/*$Id: FileMode.java,v 1.3 2005/03/30 14:21:44 veghead Exp $ */

package vmap.modes.filemode;

import vmap.modes.Mode;
import vmap.modes.ModeController;
import vmap.controller.Controller;

import javax.swing.JMenu;
import javax.swing.JToolBar;

public class FileMode implements Mode {

    private Controller c;
    private FileController modecontroller;
    private JToolBar toolbar;
    private static final String MODENAME = "File";
    private static boolean isRunning = false;

    public FileMode() {
    }

    public void init(Controller c) {
	this.c = c;
	modecontroller = new FileController(this);
	toolbar = new FileToolBar(modecontroller);
    }
    

    public String toString() {
	return MODENAME;
    }

    /**
     * Called whenever this mode is chosen in the program.
     * (updates Actions etc.)
     */
    public void activate() {
	if (!isRunning) {
	    getModeController().newMap();
	    isRunning = true;
	} else {
            c.getMapModuleManager().changeToMapOfMode(this);
	}
	//	menu.add( ((FileController)getModeController()).center );
// 	getController().cut.setEnabled(false);
// 	getController().paste.setEnabled(false);
    }
    
    public void restore(String restoreable) {
    }

    public Controller getController() {
	return c;
    }

    public ModeController getModeController() {
	return modecontroller;
    }

    public JMenu getModeFileMenu() {
	return null;
    }

    public JMenu getModeInsertMenu() {
	return null;
    }

    public JMenu getModeEditMenu() {
       return ((FileController)getModeController()).getEditMenu();
    }

    public JToolBar getModeToolBar() {
	return toolbar;
    }

    public JToolBar getLeftToolBar() {
	return null;
    }

    public JMenu getModeFormatMenu() {
        return null;
    }
}
