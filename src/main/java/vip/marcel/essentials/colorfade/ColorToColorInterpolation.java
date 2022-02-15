package vip.marcel.essentials.colorfade;

import java.awt.Color;

/**
 *
 * @author Marcel
 */
public class ColorToColorInterpolation {
    
    private Color           from;
    private Color           to;
    private int             steps;
    private FromToDelta     a;
    private FromToDelta     r;
    private FromToDelta     g;
    private FromToDelta     b;
     
    public ColorToColorInterpolation( Color from, Color to, int steps ) {
        this.from = from;
        this.to = to;
        this.steps = steps;
         
        int         fromARGB = from.getRGB();
        int         toARGB = to.getRGB();
        a = new FromToDelta( fromARGB, toARGB, 24 );
        r = new FromToDelta( fromARGB, toARGB, 16 );
        g = new FromToDelta( fromARGB, toARGB,  8 );
        b = new FromToDelta( fromARGB, toARGB,  0 );
    }
 
    public Color getInterpolated( int step ) {
        double      multiplier = (double)step / steps;
        return new Color(
                r.interpolate( multiplier ),
                g.interpolate( multiplier ),
                b.interpolate( multiplier ),
                a.interpolate( multiplier ) );
    }
     
    private static class FromToDelta {
        
        int         from;
        int         to;
        int         delta;
         
        public FromToDelta( int fromColor, int toColor, int bitShift ) {
            from = (fromColor >> bitShift) & 0xff;
            to   = (  toColor >> bitShift) & 0xff;
            delta= to - from;
        }
         
        /**
         * 
         * @param multiplier a value from 0.0 through 1.0
         * @return
         */
        public int interpolate( double multiplier )
        {
            return (int)( from + delta * multiplier );
        }
    }
}
