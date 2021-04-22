package comp559.particle;
import java.util.LinkedList;
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

    List<Particle[]> triangle_list;

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
        triangle_list = new LinkedList<Particle[]>();

        gridNormals = new Vector3d[N];

        for (int i = 0; i < particles.size(); i++) {
            mesh[i] = particles.get(i);
            gridNormals[i] = new Vector3d();
        }

        for (int i = 0; i < triangles.size(); i++) {
            triangle_list.add(triangles.get(i));
        }
    }

    public void subdivideIcosphere(List<Spring> springs, List<Particle[]> triangles) {

        int num_springs = springs.size();
        Particle[] new_particles = new Particle[springs.size()];
        List<Particle[]> new_triangles = new LinkedList<Particle[]>();

        for (int i = 0; i < triangles.size(); i++) {

            triangle_list.add(triangles.get(i));
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
//            float[] frontColour = { .71f, .6f, .2f,    1};
            float[] frontColour = { .71f,  0,   .71f, 1};
            float[] backColour  = { .71f,  0,   .71f, 1};
            gl.glMaterialfv( GL.GL_FRONT, GL2.GL_DIFFUSE, frontColour, 0 );
            gl.glMaterialfv( GL.GL_BACK, GL2.GL_DIFFUSE, backColour, 0 );

            if ( drawSmoothShaded.getValue() ) {
                // note that this could be made much faster by setting
                // up index and vertex buffers and using glDrawElements
                // but this is just a rendering efficiency issue.
                Vector3d n;
                // compute per vertex normals!
                for (int i = 0; i < triangle_list.size(); i++) {
                    Point3d p1 = triangle_list.get(i)[0].p;
                    Point3d p2 = triangle_list.get(i)[1].p;
                    Point3d p3 = triangle_list.get(i)[2].p;
                    n = gridNormals[triangle_list.get(i)[0].index];
                    v1.sub( p1, p2);
                    v2.sub( p1, p3 );
                    n.cross(v1,v2);
                    n.normalize();
                }
                gl.glBegin(GL.GL_TRIANGLES);
                for (int i = 0; i < triangle_list.size(); i++) {
                    Particle p1 = triangle_list.get(i)[0];
                    Particle p2 = triangle_list.get(i)[1];
                    Particle p3 = triangle_list.get(i)[2];
                    int p2_index = p2.index;
                    int p3_index = p3.index;


                    n = gridNormals[p1.index]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p1.p.x,p1.p.y,p1.p.z);
                    n = gridNormals[p2_index]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p2.p.x,p2.p.y,p2.p.z);
                    n = gridNormals[p3_index]; gl.glNormal3d( n.x,n.y,n.z);
                    gl.glVertex3d(p3.p.x,p3.p.y,p3.p.z);

                }

                gl.glEnd();
            } else {
                Vector3d n = new Vector3d();
                gl.glBegin(GL.GL_TRIANGLES);
                for (int i = 0; i < triangle_list.size(); i++) {
                    Particle p1 = triangle_list.get(i)[0];
                    Particle p2 = triangle_list.get(i)[1];
                    Particle p3 = triangle_list.get(i)[2];
                    int p2_index = p2.index;
                    int p3_index = p3.index;

                    v1.sub(p2.p,p1.p);
                    v2.sub(p3.p,p1.p);
                    n.cross(v1,v2);
                    n.normalize();
                    gl.glNormal3d(n.x,n.y,n.z);
                    gl.glVertex3d(p1.p.x,p1.p.y,p1.p.z);
                    gl.glVertex3d(p2.p.x,p2.p.y,p2.p.z);
                    gl.glVertex3d(p3.p.x,p3.p.y,p3.p.z);

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