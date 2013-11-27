

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package computer.vision;

import java.util.ArrayList;
/**
 *
 * @author s0943941
 */
public class BlobObject {
    
    public ArrayList<Integer> xpoints;
    public ArrayList<Integer> ypoints;
    
    public BlobObject(ArrayList<Integer> xpoints, ArrayList<Integer> ypoints) {
        this.xpoints = xpoints;
        this.ypoints = ypoints;
    }
    
}
