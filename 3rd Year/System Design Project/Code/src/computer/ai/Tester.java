/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package computer.ai;

import computer.simulator.Pitch;
import computer.simulator.Robot;
import java.util.Date;

/**
 *
 * @author s0943941
 */
public class Tester extends AI {

    Date start = new Date();
    
    public Tester(Pitch pitch, Robot self) {
        super(pitch, self);
    }

    
    @Override
    public void run() {
            if (new Date().getTime() - start.getTime() < 20000) {
                System.out.println(self.getOrientation().radians);
            }
    }

    @Override
    public void robotCollided() {
    }
    
}
