package comp559.particle;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import mintools.parameters.BooleanParameter;
import mintools.swing.CollapsiblePanel;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.SceneGraphNode;

/**
 * Geometry for a box shaped room to help give our whole
 * simulation some context.
 *
 * @author Paul Kry
 */
public class BoxRoom implements SceneGraphNode {

    private BooleanParameter drawFloor = new BooleanParameter("draw floor", true);

    /** Bounding box of the floor and walls */
    private double xl, xh, yl, yh, zl, zh;

    /**
     * Creates a room in which we can display something.
     */
    public BoxRoom() {
        Point3d ll = new Point3d(-4,-4,-4);
        Point3d ur = new Point3d( 4, 4, 4);
        setBoundingBox( ll, ur );
    }

    /**
     * Sets the bounding box of the room from the provided
     * lower left (ll) and upper right (ur) corners.
     * @param ll
     * @param ur
     */
    public void setBoundingBox( Tuple3d ll, Tuple3d ur ) {
        xl = ll.x;
        xh = ur.x;
        yl = ll.y;
        yh = ur.y;
        zl = ll.z;
        zh = ur.y;
    }

    public void display( GLAutoDrawable drawable ) {
        GL2 gl = drawable.getGL().getGL2();

        if ( drawFloor.getValue () ) {

            final float[] shinycolour = new float[] {0,0,0,1};
            final float[] floorcolour = { 0.5f, 0.5f, 0.65f, 1 };
            gl.glEnable( GL.GL_CULL_FACE );
            gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE, floorcolour, 0 );
            gl.glMaterialfv( GL.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, shinycolour, 0 );
            gl.glMateriali( GL.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 0 );

            // cheepo per pixel lighting imitation by drawing lots of quads

            int N = 10;

            // draw floor
            for ( int j = 0; j < N; j++ ) {
                double za = zl + (zh-zl)*j/(N);
                double zb = zl + (zh-zl)*(j+1)/(N);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                gl.glNormal3d( 0, 1, 0 );
                for ( int i = 0; i <= N; i++ ) {
                    double xa = xl + (xh-xl)*i/(N);
                    gl.glVertex3d( xa, yl, za );
                    gl.glVertex3d( xa, yl, zb );
                }
                gl.glEnd();
            }

            // draw the left wall...
            for ( int j = 0; j < N; j++ ) {
                double za = zl + (zh-zl)*j/(N);
                double zb = zl + (zh-zl)*(j+1)/(N);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                gl.glNormal3d( 1, 0, 0 );
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);
                    gl.glVertex3d( xl, ya, zb );
                    gl.glVertex3d( xl, ya, za );
                }
                gl.glEnd();
            }


            // draw the right wall...
            for ( int j = 0; j < N; j++ ) {
                double za = zl + (zh-zl)*j/(N);
                double zb = zl + (zh-zl)*(j+1)/(N);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                gl.glNormal3d( -1, 0, 0 );
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);

                    gl.glVertex3d( xh, ya, za );
                    gl.glVertex3d( xh, ya, zb );
                }
                gl.glEnd();
            }

            // draw back wall
            for ( int j = 0; j < N; j++ ) {
                double xa = zl + (zh-zl)*j/(N);
                double xb = zl + (zh-zl)*(j+1)/(N);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                gl.glNormal3d( 0, 0, 1);
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);
                    gl.glVertex3d( xa, ya, zl );
                    gl.glVertex3d( xb, ya, zl );
                }
                gl.glEnd();
            }

            // draw the front wall...
            for ( int j = 0; j < N; j++ ) {
                double xa = zl + (zh-zl)*j/(N);
                double xb = zl + (zh-zl)*(j+1)/(N);
                gl.glBegin(GL2.GL_QUAD_STRIP);
                gl.glNormal3d( 0, 0, -1);
                for ( int i = 0; i <= N; i++ ) {
                    double ya = yl + (yh-yl)*i/(N);
                    gl.glVertex3d( xb, ya, zh );
                    gl.glVertex3d( xa, ya, zh );
                }
                gl.glEnd();
            }
        }
    }

    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.setBorder( new TitledBorder("floor controls" ) );
        vfp.add( drawFloor.getControls() );
        CollapsiblePanel cp = new CollapsiblePanel( vfp.getPanel() );
        cp.collapse();
        return cp;
    }

    public void init(GLAutoDrawable drawable) {
        // do nothing
    }

}