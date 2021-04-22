package comp559.particle;
import java.util.LinkedList;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
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

    public void subdivideIcosphere(List<Particle> particles, List<Spring> springs, List<Particle[]> triangles) {

        int num_triangles = triangles.size();
        int num_particles = particles.size();
        Vector3d zero = new Vector3d(0,0,0);

        // this is used temporarily here but the actual new particles are appended to the system
        Particle[] new_particles_temp = new Particle[springs.size()];

        List<Spring[]> springs_temp = new LinkedList<Spring[]>();
        List<Particle[]> particles_temp2 = new LinkedList<Particle[]>();

        int particle_index = num_particles;
        Particle p0 = particles.get(0);

        // first get spring indeces and create new particles
        for (int i = 0; i < num_triangles; i++) {
            Particle[] t = triangles.get(i);

            Particle p1 = t[0];
            Particle p2 = t[1];
            Particle p3 = t[2];

            Spring s1 = null;
            Spring s2 = null;
            Spring s3 = null;

            Particle p1_new = null;
            Particle p2_new = null;
            Particle p3_new = null;

            Particle[] t_new1 = new Particle[3];
            Particle[] t_new2 = new Particle[3];
            Particle[] t_new3 = new Particle[3];
            Particle[] t_new4 = new Particle[3];

            float factor = 1.1f;
            // find spring from p1 to p2
            for (int s = 0; s < p1.springs.size(); s++) {
                if ((p1.springs.get(s).p2.index == p2.index) || (p1.springs.get(s).p1.index == p2.index )) {
                    s1 = p1.springs.get(s);
                    break;
                }
            }
            // find spring from p2 to p3
            for (int s = 0; s < p2.springs.size(); s++) {
                if ((p2.springs.get(s).p2.index == p3.index)|| (p2.springs.get(s).p1.index == p3.index )) {
                    s2 = p2.springs.get(s);
                    break;
                }
            }
            // find spring from p3 to p1
            for (int s = 0; s < p3.springs.size(); s++) {
                if ((p3.springs.get(s).p2.index == p1.index )|| (p3.springs.get(s).p1.index == p1.index )) {
                    s3 = p3.springs.get(s);
                    break;
                }
            }

            if (s1 == null || s2 == null || s3 == null ) {
                System.out.println("error");
            }

            Spring[] three_springs = {s1, s2, s3};
            springs_temp.add(three_springs);

            Point3d pos = new Point3d(0,0,0);

            if (new_particles_temp[s1.index] != null) {
                p1_new = new_particles_temp[s1.index];
            }
            else {
                pos.set(p1.p);
                pos.add(p2.p);
                pos.scale(0.5);
                pos.scale(factor);

                p1_new = new Particle(pos, zero, particle_index++);
                new_particles_temp[s1.index] = p1_new;
                particles.add(p1_new);
            }
            if (new_particles_temp[s2.index] != null) {
                p2_new = new_particles_temp[s2.index];
            }
            else {
                pos.set(p2.p);
                pos.add(p3.p);
                pos.scale(0.5);
                pos.scale(factor);

                p2_new = new Particle(pos, zero, particle_index++);
                new_particles_temp[s2.index] = p2_new;
                particles.add(p2_new);
            }
            if (new_particles_temp[s3.index] != null) {
                p3_new = new_particles_temp[s3.index];
            }
            else {
                pos.set(p3.p);
                pos.add(p1.p);
                pos.scale(0.5);
                pos.scale(factor);

                p3_new = new Particle(pos, zero, particle_index++);
                new_particles_temp[s3.index] = p3_new;
                particles.add(p3_new);
            }

            Particle[] three_particles = {p1_new, p2_new, p3_new};
            particles_temp2.add(three_particles);

            // first triangle
            t_new1[0] = p1;
            t_new1[1] = p1_new;
            t_new1[2] = p3_new;
            // second triangle
            t_new2[0] = p2;
            t_new2[1] = p2_new;
            t_new2[2] = p1_new;
            // third triangle
            t_new3[0] = p3;
            t_new3[1] = p3_new;
            t_new3[2] = p2_new;
            // middle triangle
            t_new4[0] = p1_new;
            t_new4[1] = p2_new;
            t_new4[2] = p3_new;

            triangles.add(t_new1);
            triangles.add(t_new2);
            triangles.add(t_new3);
            triangles.add(t_new4);

        }

        int spring_index = 0;
        // start over to retrieve new particles
        particle_index = num_particles;

        // add new springs and remove old springs, remove old triangles also
        for (int i = 0; i < num_triangles; i++) {
            Particle[] t = triangles.get(0);
            triangles.remove(t);

            Particle p1 = t[0];
            Particle p2 = t[1];
            Particle p3 = t[2];

            Spring s1 = springs_temp.get(i)[0];
            Spring s2 = springs_temp.get(i)[1];
            Spring s3 = springs_temp.get(i)[2];

            Particle p1_new = particles_temp2.get(i)[0];
            Particle p2_new = particles_temp2.get(i)[1];
            Particle p3_new = particles_temp2.get(i)[2];

            // side 1
            springs.remove(s1);
            springs.add(new Spring(p1, p1_new, spring_index++));
            springs.add(new Spring(p1_new, p2, spring_index++));

            // side 2
            springs.remove(s2);
            springs.add(new Spring(p2, p2_new, spring_index++));
            springs.add(new Spring(p2_new, p3, spring_index++));

            // side 3
            springs.remove(s3);
            springs.add(new Spring(p3, p3_new, spring_index++));
            springs.add(new Spring(p3_new, p1, spring_index++));

            // add springs for middle triangle
            springs.add(new Spring(p1_new, p2_new, spring_index++));
            springs.add(new Spring(p2_new, p3_new, spring_index++));
            springs.add(new Spring(p3_new, p1_new, spring_index++));

            // inside spring
            springs.add(new Spring(p0, p1_new, spring_index++));
            springs.add(new Spring(p0, p2_new, spring_index++));
            springs.add(new Spring(p0, p3_new, spring_index++));

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