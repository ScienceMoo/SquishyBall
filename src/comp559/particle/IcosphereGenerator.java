package comp559.particle;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import mintools.parameters.BooleanParameter;
import mintools.parameters.IntParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.SceneGraphNode;

/**
 * A container for a grid of particles.
 *
 * Note that this grid of particles will look like
 * a piece of cloth, but the spring based bending
 * forces are not a fantastic approximation.
 * @author kry
 */
public class IcosphereGenerator implements SceneGraphNode {

    /**
     * Grid of particles.  This double array helps us keep track of these
     * particles for the purposes of rendering.
     */
    Particle[] mesh = null;

    /**
     * Per particle normals for our grid, since we want to render them
     * as a surface.
     */
    private Vector3d[] gridNormals = null;

    /**
     * Creates a new particle grid container, but does not actually
     * create any particles.
     */
    public IcosphereGenerator() {
        // do nothing
    }

    /**
     * Clears the grid of particles
     */
    public void clear() {
        mesh = null;
    }

    /**
     * Creates a grid of particles and connects them with springs.
     * Two corners of the grid are pinned.
     * The particles and springs created are added to the list and set
     * objects which are passed as parameters.
     * @param particles
     * @param springs
     */
    public void createSimpleIcosphere(List<Particle> particles, List<Spring> springs, List<Particle[]> triangles ) {

        Point3d p = new Point3d();
        Vector3d zero = new Vector3d( 0,0,0 );

        int N = particles.size();
//        double particleMass = 1.0 / (N*N);
        mesh = new Particle[N];
        gridNormals = new Vector3d[N];

        for (int i = 0; i < particles.size(); i++) {
            mesh[i] = particles.get(i);
            gridNormals[i] = new Vector3d();
        }
    }

    public void init(GLAutoDrawable drawable) {
        // nothing to do!
    }

    /**
     * This display call will draw the mesh of particles as a
     * smooth sheet, either with flat shaded triangles, or smooth
     * shaded triangles where per vertex (i.e., per particle) normals
     * are computed by looking at the neighbouring vertices.
     */
    public void display( GLAutoDrawable drawable ) {
        GL2 gl = drawable.getGL().getGL2();

        if ( mesh != null && drawSurface.getValue() ) {
            Vector3d v1 = new Vector3d();
            Vector3d v2 = new Vector3d();

            gl.glDisable( GL.GL_CULL_FACE );
            gl.glLightModeli( GL2.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_TRUE );
            float[] frontColour = { .71f, .6f, .2f,    1};
            float[] backColour  = { .71f,  0,   .71f, 1};
            gl.glMaterialfv( GL.GL_FRONT, GL2.GL_DIFFUSE, frontColour, 0 );
            gl.glMaterialfv( GL.GL_BACK, GL2.GL_DIFFUSE, backColour, 0 );

            if ( drawSmoothShaded.getValue() ) {
                // note that this could be made much faster by setting
                // up index and vertex buffers and using glDrawElements
                // but this is just a rendering efficiency issue.
                Vector3d n;
                // compute per vertex normals!
                for ( int i = 3; i < mesh.length; i++ ) {
                    Point3d p1;
                    Point3d p2;
                    Point3d p3;
                    int p2_index;
                    int p3_index;
                    p1 = mesh[i].p;
                    if (i == 12) {
                        p2 = mesh[3].p;
                        p3 = mesh[4].p;
                        p2_index = mesh[3].index;
                        p3_index = mesh[4].index;
                    }
                    else if (i == 11) {
                        p2 = mesh[3].p;
                        p3 = mesh[i+1].p;
                        p2_index = mesh[3].index;
                        p3_index = mesh[i+1].index;
                    }
                    else {
                        if (i % 2 == 0) {
                            p2 = mesh[i+1].p;
                            p3 = mesh[i+2].p;
                            p2_index = mesh[i+1].index;
                            p3_index = mesh[i+2].index;
                        }
                        else {
                            p2 = mesh[i+2].p;
                            p3 = mesh[i+1].p;
                            p2_index = mesh[i+2].index;
                            p3_index = mesh[i+1].index;
                        }

                    }
                    v1.sub( p1, p2);
                    v2.sub( p1, p3 );

                    // set value
                    n = gridNormals[i];
                    n.cross(v1,v2);
                    n.normalize();

                    if (i % 2 == 0) {
                        p1 = mesh[2].p;
                        p2 = mesh[i].p;
                        n = gridNormals[2];
                    }
                    else {
                        p1 = mesh[1].p;
                        p2 = mesh[i].p;
                        n = gridNormals[1];
                    }
                    if (i == 11) {
                        p3 = mesh[3].p;
                    }
                    else if (i == 12) {
                        p3 = mesh[4].p;
                    }
                    else {
                        p3 = mesh[i+2].p;
                    }
                    v1.sub( p1, p2);
                    v2.sub( p1, p3 );
                    n.cross(v1,v2);
                    n.normalize();
                }
                gl.glBegin(GL.GL_TRIANGLES);
                for ( int i = 3; i < 13; i++ ) {
                    Point3d p1;
                    Point3d p2;
                    Point3d p3;
                    int p2_index;
                    int p3_index;
                    p1 = mesh[i].p;
                    if (i == 12) {
                        p2 = mesh[3].p;
                        p3 = mesh[4].p;
                        p2_index = mesh[3].index;
                        p3_index = mesh[4].index;
                    }
                    else if (i == 11) {
                        p2 = mesh[3].p;
                        p3 = mesh[i+1].p;
                        p2_index = mesh[3].index;
                        p3_index = mesh[i+1].index;
                    }
                    else {
                        if (i % 2 == 0) {
                            p2 = mesh[i+1].p;
                            p3 = mesh[i+2].p;
                            p2_index = mesh[i+1].index;
                            p3_index = mesh[i+2].index;
                        }
                        else {
                            p2 = mesh[i+2].p;
                            p3 = mesh[i+1].p;
                            p2_index = mesh[i+2].index;
                            p3_index = mesh[i+1].index;
                        }

                    }

                    n = gridNormals[i]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p1.x,p1.y,p1.z);
                    n = gridNormals[p2_index]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p2.x,p2.y,p2.z);
                    n = gridNormals[p3_index]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p3.x,p3.y,p3.z);

                    if (i % 2 == 0) {
                        p1 = mesh[2].p;
                    }
                    else {
                        p1 = mesh[1].p;
                    }

                    p2 = mesh[i].p;
                    if (i == 11) {
                        p3 = mesh[3].p;
                    }
                    else if (i == 12) {
                        p3 = mesh[4].p;
                    }
                    else {
                        p3 = mesh[i+2].p;
                    }

                    n = gridNormals[i]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p1.x,p1.y,p1.z);
                    n = gridNormals[p2_index]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p2.x,p2.y,p2.z);
                    n = gridNormals[p3_index]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p3.x,p3.y,p3.z);

                }

                gl.glEnd();
            } else {
                Vector3d n = new Vector3d();
                gl.glBegin(GL.GL_TRIANGLES);
                for ( int i = 3; i < 13; i++ ) {
                    Point3d p1;
                    Point3d p2;
                    Point3d p3;
                    p1 = mesh[i].p;
                    if (i == 12) {
                        p2 = mesh[3].p;
                        p3 = mesh[4].p;
                    }
                    else if (i == 11) {
                        p2 = mesh[i+1].p;
                        p3 = mesh[3].p;
                    }
                    else {
                        if (i % 2 == 0) {
                            p2 = mesh[i+1].p;
                            p3 = mesh[i+2].p;
                        }
                        else {
                            p2 = mesh[i+2].p;
                            p3 = mesh[i+1].p;
                        }

                    }

                    v1.sub(p2,p1);
                    v2.sub(p3,p1);
                    n.cross(v1,v2);
                    n.normalize();
                    gl.glNormal3d(n.x,n.y,n.z);
                    gl.glVertex3d(p1.x,p1.y,p1.z);
                    gl.glVertex3d(p2.x,p2.y,p2.z);
                    gl.glVertex3d(p3.x,p3.y,p3.z);

                    if (i % 2 == 0) {
                        p1 = mesh[2].p;
                    }
                    else {
                        p1 = mesh[1].p;
                    }

                    p2 = mesh[i].p;
                    if (i == 11) {
                        p3 = mesh[3].p;
                    }
                    else if (i == 12) {
                        p3 = mesh[4].p;
                    }
                    else {
                        p3 = mesh[i+2].p;
                    }

                    v1.sub(p2,p1);
                    v2.sub(p3,p1);
                    n.cross(v1,v2);
                    n.normalize();
                    gl.glNormal3d(n.x,n.y,n.z);
                    gl.glVertex3d(p1.x,p1.y,p1.z);
                    gl.glVertex3d(p2.x,p2.y,p2.z);
                    gl.glVertex3d(p3.x,p3.y,p3.z);
                }
                gl.glEnd();
            }
            gl.glLightModeli( GL2.GL_LIGHT_MODEL_TWO_SIDE, GL.GL_FALSE );
            gl.glEnable( GL.GL_CULL_FACE );
        }
    }

    private BooleanParameter drawSurface = new BooleanParameter( "draw grid", true );
    private IntParameter gridDimension = new IntParameter ("grid dimension", 15, 2, 40 );
    private BooleanParameter drawSmoothShaded = new BooleanParameter( "smooth shaded", true );

    @Override
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.add( gridDimension.getControls() );
        vfp.add( drawSurface.getControls() );
        vfp.add( drawSmoothShaded.getControls() );
        return vfp.getPanel();
    }

}