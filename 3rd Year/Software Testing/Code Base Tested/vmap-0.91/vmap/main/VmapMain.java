/*FreeMind - A Program for creating and viewing Mindmaps
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
/*$Id: VmapMain.java,v 1.1.1.1 2004/09/11 17:31:22 veghead Exp $ */

package vmap.main;

import java.awt.Container;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JLayeredPane;

import vmap.controller.Controller;
import vmap.controller.MenuBar;
import vmap.view.mindmapview.MapView;

public interface VmapMain {
    public boolean isApplet();

    public MapView getView();

    public void setView(MapView view);

    public Controller getController();

    public void setWaitingCursor(boolean waiting);

    public File getPatternsFile();

    public MenuBar getVmapMenuBar();

    /**Returns the ResourceBundle with the current language*/
    public ResourceBundle getResources();

    public Container getContentPane();
    
    public void out (String msg);

    public void err (String msg);

    /**
     * Open url in WWW browser. This method hides some differences between operating systems.
     */
    public void openDocument(URL location) throws Exception;

    /**remove this!*/
    public void repaint();

    public URL getResource(String name);

    public String getProperty(String key);

    public void setProperty(String key, String value);

    public void saveProperties();

    /** Returns the path to the directory the vmap auto properties are in, or null, if not present.*/
    public String getVmapDirectory();

    public JLayeredPane getLayeredPane();

    public Container getViewport();

    public void setTitle(String title);

     // to keep last win size (PN)
    public int getWinHeight();
    public int getWinWidth();
    public int getWinState();

    // version info:
    public String getVmapVersion();

    /* To obtain a logging element, ask here. */
    public java.util.logging.Logger getLogger(String forClass);
}
