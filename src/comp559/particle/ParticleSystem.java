package comp559.particle;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import mintools.viewer.EasyViewer;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.parameters.IntParameter;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.SceneGraphNode;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;

/**
 * Implementation of a simple particle system
 * @author kry
 */
public class ParticleSystem implements SceneGraphNode, Function, Filter {

    private List<Particle> particles;
    private List<Spring> springs;
    private List<Particle[]> triangles;
    private DenseVector x0;
    private DenseVector v0;
    private DenseVector xNew;
    private DenseVector vNew;
    private DenseVector tmp;
    
    /**
     * Creates an empty particle system
     */
    public ParticleSystem() {
        particles = new LinkedList<Particle>();
        springs = new LinkedList<Spring>();
        triangles = new LinkedList<Particle[]>();
        createSimpleIcosphere();
    }

    /**
     * The surface of the sphere which can be shaded smooth by calculating the normals
     */
    private IcosphereGenerator surface = new IcosphereGenerator();

    public void createSimpleIcosphere() {
        particles.clear();
        springs.clear();
        triangles.clear();
        int p_index = 0;

//         use this to test!
        double radius = 1;
        Particle p1 = new Particle( new Point3d(0, 0, 0), new Vector3d(0, 0, 0), p_index++);
        Particle p2 = new Particle( new Point3d(0, 0, radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p3 = new Particle( new Point3d(0, 0, -radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p4 = new Particle( new Point3d(0.8973 * radius, 0, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p5 = new Particle( new Point3d(0.722 * radius, 0.5244 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p6 = new Particle( new Point3d(0.279 * radius, 0.853 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++);
        Particle p7 = new Particle( new Point3d(-0.279 * radius, 0.853 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p8 = new Particle( new Point3d(-0.722 * radius, 0.5244 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p9 = new Particle( new Point3d(-0.8973 * radius, 0, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p10 = new Particle( new Point3d(-0.722 * radius, -0.5244 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p11 = new Particle( new Point3d(-0.279 * radius, -0.853 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++);
        Particle p12 = new Particle( new Point3d(0.279 * radius,-0.853 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        Particle p13 = new Particle( new Point3d(0.722 * radius,-0.5244 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );

//        radius = 0.8;
//        Particle p14 = new Particle( new Point3d(0, 0, 0), new Vector3d(0, 0, 0), p_index++);
//        Particle p15 = new Particle( new Point3d(0, 0, radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p16 = new Particle( new Point3d(0, 0, -radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p17 = new Particle( new Point3d(0.8973 * radius, 0, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p18 = new Particle( new Point3d(0.722 * radius, 0.5244 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p19 = new Particle( new Point3d(0.279 * radius, 0.853 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++);
//        Particle p20 = new Particle( new Point3d(-0.279 * radius, 0.853 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p21 = new Particle( new Point3d(-0.722 * radius, 0.5244 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p22 = new Particle( new Point3d(-0.8973 * radius, 0, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p23 = new Particle( new Point3d(-0.722 * radius, -0.5244 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p24 = new Particle( new Point3d(-0.279 * radius, -0.853 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++);
//        Particle p25 = new Particle( new Point3d(0.279 * radius,-0.853 * radius, 0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
//        Particle p26 = new Particle( new Point3d(0.722 * radius,-0.5244 * radius, -0.445 * radius), new Vector3d(0, 0, 0), p_index++ );
        particles.add( p1 );
        particles.add( p2 );
        particles.add( p3 );
        particles.add( p4 );
        particles.add( p5 );
        particles.add( p6 );
        particles.add( p7 );
        particles.add( p8 );
        particles.add( p9 );
        particles.add( p10 );
        particles.add( p11 );
        particles.add( p12 );
        particles.add( p13 );
//         p1.pinned = true;
        // middle vertex
        springs.add( new Spring( p1, p2 ) );
        springs.add( new Spring( p1, p3 ) );
        springs.add( new Spring( p1, p4 ) );
        springs.add( new Spring( p1, p5 ) );
        springs.add( new Spring( p1, p6 ) );
        springs.add( new Spring( p1, p7 ) );
        springs.add( new Spring( p1, p8 ) );
        springs.add( new Spring( p1, p9 ) );
        springs.add( new Spring( p1, p10 ) );
        springs.add( new Spring( p1, p11 ) );
        springs.add( new Spring( p1, p12 ) );
        springs.add( new Spring( p1, p13 ) );

        // top vertex to top row
        springs.add( new Spring( p2, p4 ) );
        springs.add( new Spring( p2, p6 ) );
        springs.add( new Spring( p2, p8 ) );
        springs.add( new Spring( p2, p10 ) );
        springs.add( new Spring( p2, p12 ) );

        // bottom vertex to bottom row
        springs.add( new Spring( p3, p5 ) );
        springs.add( new Spring( p3, p7 ) );
        springs.add( new Spring( p3, p9 ) );
        springs.add( new Spring( p3, p11 ) );
        springs.add( new Spring( p3, p13 ) );

        // top row
        springs.add( new Spring( p4, p6) );
        springs.add( new Spring( p6, p8) );
        springs.add( new Spring( p8, p10) );
        springs.add( new Spring( p10, p12) );
        springs.add( new Spring( p12, p4) );

        // bottom row
        springs.add( new Spring( p5, p7) );
        springs.add( new Spring( p7, p9) );
        springs.add( new Spring( p9, p11) );
        springs.add( new Spring( p11, p13) );
        springs.add( new Spring( p13, p5) );

        // rest of springs
        springs.add( new Spring( p4, p5 ) );
        springs.add( new Spring( p5, p6 ) );
        springs.add( new Spring( p6, p7 ) );
        springs.add( new Spring( p7, p8 ) );
        springs.add( new Spring( p8, p9 ) );
        springs.add( new Spring( p9, p10 ) );
        springs.add( new Spring( p10, p11 ) );
        springs.add( new Spring( p11, p12 ) );
        springs.add( new Spring( p12, p13 ) );
        springs.add( new Spring( p13, p4 ) );

        Vector3d v1 = new Vector3d();
        Vector3d v2 = new Vector3d();

        for ( int i = 3; i < 13; i++ ) {
            Particle triangle_p1;
            Particle triangle_p2;
            Particle triangle_p3;
            triangle_p1 = particles.get(i);
            if (i == 12) {
                triangle_p2 = particles.get(3);
                triangle_p3 = particles.get(4);
            }
            else if (i == 11) {
                triangle_p2 = particles.get(i+1);
                triangle_p3 = particles.get(3);
            }
            else {
                if (i % 2 == 0) {
                    triangle_p2 = particles.get(i+1);
                    triangle_p3 = particles.get(i+2);
                }
                else {
                    triangle_p2 = particles.get(i+2);
                    triangle_p3 = particles.get(i+1);
                }

            }

            Particle[] t = new Particle[3];
            t[0] = triangle_p1;
            t[1] = triangle_p2;
            t[2] = triangle_p3;
            triangles.add(t);


            if (i % 2 == 0) {
                triangle_p1 = particles.get(2);
            }
            else {
                triangle_p1 = particles.get(1);
            }

            triangle_p2 = particles.get(i);
            if (i == 11) {
                triangle_p3 = particles.get(3);
            }
            else if (i == 12) {
                triangle_p3 = particles.get(4);
            }
            else {
                triangle_p3 = particles.get(i+2);
            }

            t = new Particle[3];
            t[0] = triangle_p1;
            t[1] = triangle_p2;
            t[2] = triangle_p3;
            triangles.add(t);
        }

        // triangle test
//        Particle p1 = new Particle( new Point3d(300, 310, 10), new Vector3d(0, 0, 0), 0 );
//        Particle p2 = new Particle( new Point3d(310, 310, -10), new Vector3d(0, 0, 0), 1 );
//        Particle p3 = new Particle( new Point3d(290, 310, -10), new Vector3d(0, 0, 0), 2 );
//        Particle p4 = new Particle( new Point3d(300, 300, 0), new Vector3d(0, 0, 0), 3 );
//        particles.add( p1 );
//        particles.add( p2 );
//        particles.add( p3 );
//        particles.add( p4 );
//        p1.pinned = true;
//        p2.pinned = true;
//        p3.pinned = true;
//        springs.add( new Spring( p1, p2 ) );
//        springs.add( new Spring( p2, p3 ) );
//        springs.add( new Spring( p3, p1 ) );
//        Particle[] t = new Particle[3];
//        t[0] = p1;
//        t[1] = p2;
//        t[2] = p3;
//        triangles.add(t);

        surface.createSimpleIcosphere( particles, springs, triangles );

        init();
    }

    
    /**
     * Gets the particles in the system
     * @return the particle set
     */
    public List<Particle> getParticles() {
        return particles;
    }
    
    /**
     * Gets the springs in the system
     * @return the spring list
     */
    public List<Spring> getSprings() {
    	return springs;
    }
    
    /**
     * Resets the positions of all particles
     */
    public void resetParticles() {
        for ( Particle p : particles ) {
            p.reset();
        }
        time = 0;
    }
    
    /**
     * Deletes all particles
     */
    public void clearParticles() {
        particles.clear();
        springs.clear();
        triangles.clear();
        surface.clear();
    }
    
    /**
     * Gets the phase space state of the particle system
     * @param phaseSpaceState
     */
    public void getPhaseSpace( double[] phaseSpaceState ) {
        int count = 0;
        for ( Particle p : particles ) {
            phaseSpaceState[count++] = p.p.x;
            phaseSpaceState[count++] = p.p.y;
            phaseSpaceState[count++] = p.p.z;
            phaseSpaceState[count++] = p.v.x;
            phaseSpaceState[count++] = p.v.y;
            phaseSpaceState[count++] = p.v.z;
        }
    }
    
    /**
     * Gets the dimension of the phase space state
     * (particles * 2 dimensions * 2 for velocity and position)
     * @return dimension
     */
    public int getPhaseSpaceDim() {        

        return particles.size() * 6;
    }
    
    /**
     * Sets the phase space state of the particle system
     * @param phaseSpaceState
     */
    public void setPhaseSpace( double[] phaseSpaceState ) {
        int count = 0;
        for ( Particle particle : particles ) {
            if ( particle.pinned ) {
                count += 6;
            } else {
                particle.p.x = phaseSpaceState[count++];
                particle.p.y = phaseSpaceState[count++];
                particle.p.z = phaseSpaceState[count++];
                particle.v.x = phaseSpaceState[count++];
                particle.v.y = phaseSpaceState[count++];
                particle.v.z = phaseSpaceState[count++];
            }
        }
    }
    
    /** Elapsed simulation time */
    public double time = 0;

    /** The explicit integrator to use, if not performing backward Euler implicit integration */
    public Integrator integrator;
    
    public double[] state = new double[1];
    public double[] stateOut = new double[1];

    // these get created in init() and are probably useful for Backward Euler computations
    private ConjugateGradientMTJ CG;
    private DenseMatrix A;
    private FlexCompRowMatrix I;
    private DenseMatrix dfdx;
    private DenseMatrix dfdv;
    private DenseVector deltaxdot;
    private DenseVector b;
    private DenseVector f;
    private DenseVector xdot;
    
    /**
     * Initializes the system 
     * Allocates the arrays and vectors necessary for the solve of the full system
     */
    public void init() {
        int N = particles.size();
        // create matrix and vectors for solve
        CG = new ConjugateGradientMTJ(3*N);
        CG.setFilter(this);
        A = new DenseMatrix(3*N, 3*N);
        I = new FlexCompRowMatrix(3*N, 3*N);
        dfdx = new DenseMatrix(3*N, 3*N);
        dfdv = new DenseMatrix(3*N, 3*N);
        deltaxdot = new DenseVector(3*N);
        b = new DenseVector(3*N);
        f = new DenseVector(3*N);
        xdot = new DenseVector(3*N);
    }
    
    /**
     * Fills in the provided vector with the particle velocities.
     * @param xd
     */
    private void getVelocities(DenseVector xd) {
        for ( Particle p : particles ) {
            int j = p.index * 3;
            if( p.pinned ) {
                xd.set( j, 0 );
                xd.set( j+1, 0 );
                xd.set( j+2, 0 );
            } else {
                xd.set( j, p.v.x );
                xd.set( j+1, p.v.y );
                xd.set( j+2, p.v.z );
            }
        }       
    }

    /**
     * Sets the velocities of the particles given a vector
     * @param xd
     */
    private void setVelocities(DenseVector xd) {
        for ( Particle p : particles ) {
            int j = p.index * 3;
            if( p.pinned ) {
                p.v.set(0,0,0);
            } else {
                p.v.x = xd.get(j);
                p.v.y = xd.get(j+1);
                p.v.z = xd.get(j+2);
            }
        }
    }
    
    /**
     *  Evaluates derivatives for ODE integration.
     * @param t time 
     * @param p phase space state
     * @param dpdt to be filled with the derivative
     */
    @Override
    public void derivs(double t, double[] p, double[] dpdt) {
        // set particle positions to given values
        setPhaseSpace( p );
        
        // TODO: Objective 2, for explicit integrators, compute forces, and accelerations, and set dpdt

        for(Particle particle: particles)
        {
            particle.clearForce();

            // apply force due to gravity on each particle
            if(useGravity.getValue())
            {
                particle.f.y = - gravity.getValue() * particle.mass;
            }

            // viscous damping is a force directly applied by the environment
            particle.f.x -= viscousDamping.getValue() * particle.v.x;
            particle.f.y -= viscousDamping.getValue() * particle.v.y;
            particle.f.z -= viscousDamping.getValue() * particle.v.z;
        }

        for(Spring s: springs)
        {
            // Applies the spring force by adding an equal force to each particle
            s.apply();
        }

        // overwrite dpdt
        int i = 0;
        for(Particle particle: particles)
        {
            dpdt[i++] = particle.v.x;
            dpdt[i++] = particle.v.y;
            dpdt[i++] = particle.v.z;
            // compute the accelerations from the forces
            dpdt[i++] = particle.f.x / particle.mass;
            dpdt[i++] = particle.f.y / particle.mass;
            dpdt[i++] = particle.f.z / particle.mass;
        }

    }
    
    /** Time in seconds that was necessary to advance the system */
    public double computeTime;
    
    /**
     * Advances the state of the system
     * @param elapsed
     */
    public void advanceTime( double elapsed ) {
        Spring.default_k = springStiffness.getValue();
        Spring.default_c = springDamping.getValue();
            
        int n = getPhaseSpaceDim();
        
        long now = System.nanoTime();        
        
        if ( explicit.getValue() ) {
            if ( n != state.length ) {
                state = new double[n];
                stateOut = new double[n];
            }
            // TODO: See explicit stepping here
            getPhaseSpace(state);         
            integrator.step( state, n, time, elapsed, stateOut, this);                
            setPhaseSpace(stateOut);
        } else {        
            if ( f == null || f.size() != n ) {
                init();
            }
            if ( n != state.length ) {
                state = new double[n];
                stateOut = new double[n];
            }
            
            // TODO: Objective 8, your backward Euler implementation will go here!
            // Note that the init() method called above creates a bunch of very 
            // useful MTJ working variables for you, and the ConjugateGradientMTJ object.
            // Go look at that code now!

            getPhaseSpace(state);
            backwardEuler(state, n, elapsed, stateOut);
            setPhaseSpace(stateOut);
            
        }
        time = time + elapsed;

        if(usePenaltyForces.getValue())
        {
            penaltyForces(elapsed);
        }

        if(usePostStepFix.getValue())
        {
            postStepFixBox();
        }

        computeTime = (System.nanoTime() - now) / 1e9;
    }
    
    @Override
    public void filter(Vector v) {
        for ( Particle p : particles ) {
            if ( !p.pinned ) continue;
            v.set( p.index*3+0, 0 );
            v.set( p.index*3+1, 0 );
            v.set( p.index*3+2, 0 );
        }
    }


    
    public void remove( Particle p ) {
    	for ( Spring s : p.springs ) {
    		Particle other = s.p1 == p ? s.p2 : s.p1; 
    		other.springs.remove( s );
            springs.remove( s );
    	}
    	p.springs.clear(); // not really necessary
        particles.remove( p );
    	// reset indices of each particle :(
    	for ( int i = 0 ; i < particles.size(); i++ ) {
            particles.get(i).index = i;
    	}
    }
    
    /**
     * Creates a new spring between two particles and adds it to the system.
     * @param p1
     * @param p2
     * @return the new spring
     */
    public Spring createSpring( Particle p1, Particle p2 ) {
        Spring s = new Spring( p1, p2 );
        springs.add( s );
        return s;
    }

    /**
     * Creates the mouse spring between two particles and adds it to the system.
     * @param p1
     * @param p2
     * @return the new spring
     */
    public Spring createMouseSpring( Particle p1, Particle p2, double k, double c, double l0) {
        Spring s = new Spring( p1, p2, k, c, l0, false);
        springs.add( s );
        return s;
    }
    
    /**
     * Removes a spring between p1 and p2 if it exists, does nothing otherwise
     * @param p1
     * @param p2
     * @return true if the spring was found and removed
     */
    public boolean removeSpring( Particle p1, Particle p2 ) {
    	Spring found = null;
    	for ( Spring s : springs ) {
    		if ( ( s.p1 == p1 && s.p2 == p2 ) || ( s.p1 == p2 && s.p2 == p1 ) ) {
    			found = s;
    			break;
    		}
    	}
    	if ( found != null ) {
    		found.p1.springs.remove(found);
    		found.p2.springs.remove(found);
            springs.remove(found);
			return true;
    	}
    	return false;
    }


    private void backwardEuler(double[] state, int n, double h, double[] stateOut)
    {
        int N = particles.size();
        x0 = new DenseVector(3*N);
        v0 = new DenseVector(3*N);
        xNew = new DenseVector(3*N);
        vNew = new DenseVector(3*N);
        tmp = new DenseVector(3*N);

        for(int i = 0; i < 3*N; i++)
        {
            I.set(i, i, 1.0);
        }

        for(int i = 0; i < state.length; i+=6)
        {
            int j = i/2;
            x0.set(j, state[i]);
            x0.set(j+1, state[i+1]);
            x0.set(j+2, state[i+2]);
            v0.set(j, state[i+2]);
            v0.set(j+1, state[i+3]);
            v0.set(j+2, state[i+4]);
        }

        for(Spring s: springs)
        {
            // apply all the spring forces
            s.addForce(f);
            s.addDfdx(dfdx);
            s.addDfdv(dfdv);
        }

        for(Particle p: particles)
        {
            f.add(p.index*3, -viscousDamping.getValue() * p.v.x);
            f.add(p.index*3+1, -viscousDamping.getValue() * p.v.y);
            f.add(p.index*3+2, -viscousDamping.getValue() * p.v.z);
            f.add(p.index*3+1, - gravity.getValue() * p.mass);
        }

        dfdx.mult(v0, tmp);
        A.set(I.add(dfdv.scale(-h)).add(dfdx.scale(-h*h)));
        b.set(f.add(tmp.scale(h)).scale(h));

        CG.solve(A, b, deltaxdot, iterations.getValue());

        vNew.set(v0.add(deltaxdot));
        tmp.set(vNew);
        xNew.set(x0.add(tmp.scale(h)));

        for(int i = 0; i < stateOut.length; i+=6)
        {
            int j = i/2;
            stateOut[i] = xNew.get(j);
            stateOut[i+1] = xNew.get(j+1);
            stateOut[i+2] = xNew.get(j+2);
            stateOut[i+3] = vNew.get(j);
            stateOut[i+4] = vNew.get(j+1);
            stateOut[i+5] = vNew.get(j+2);
        }
    }



    
    @Override
    public void init(GLAutoDrawable drawable) {
        // do nothing
    }

    private int height;
    private int width;

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glDisable( GL2.GL_LIGHTING );
        if ( drawPoints.getValue() ) {
            gl.glPointSize( 10 );
            final double[] particleColour = { 0,.95, 0, 0.5 };
            gl.glColor4dv( particleColour, 0 );

            for ( Particle p : particles ) {
                double alpha = 0.5;
                gl.glBegin( GL.GL_POINTS );
                gl.glVertex3d( p.p.x, p.p.y, p.p.z );
                gl.glEnd();
            }
        }
        gl.glEnable( GL2.GL_LIGHTING );

        surface.display( drawable );

        if ( drawSprings.getValue() ) {
            gl.glColor4d(0,.5,.5,.5);
            gl.glLineWidth(2f);
            gl.glBegin( GL.GL_LINES );
            for (Spring s : springs) {
                if (s.visible) {
                    gl.glVertex3d( s.p1.p.x, s.p1.p.y, s.p1.p.z );
                    gl.glVertex3d( s.p2.p.x, s.p2.p.y, s.p2.p.z );
                }
            }
            gl.glEnd();
        }
    }

    public DoubleParameter mass = new DoubleParameter( "mass", 0.5, 0.1, 100 );
    public BooleanParameter useGravity = new BooleanParameter( "use gravity", true );
    public DoubleParameter gravity = new DoubleParameter( "gravity", 9.8, 0.01, 1000 );
    public DoubleParameter springStiffness = new DoubleParameter( "spring stiffness", 300, 0, 10000 );
    public DoubleParameter springDamping = new DoubleParameter( "spring damping", 1.5, 0, 50 );
    public DoubleParameter viscousDamping = new DoubleParameter( "viscous damping", 0, 0, 10 );
    public DoubleParameter restitution = new DoubleParameter( "r", 0, 0, 1 );
    public DoubleParameter H = new DoubleParameter( "thickness", 0.1, 0, 1 );
    public JTextArea comments = new JTextArea("enter comments in control panel");
    public IntParameter iterations = new IntParameter( "iterations", 100, 1, 100 );

    /** controls weather explicit or implicit integration is used */
    public BooleanParameter explicit = new BooleanParameter( "explicit", true );

    private BooleanParameter usePostStepFix = new BooleanParameter( "use post step fix" , true );
    private BooleanParameter usePenaltyForces = new BooleanParameter( "use penalty forces" , false );

    private BooleanParameter drawSprings = new BooleanParameter( "draw springs", false );
    private BooleanParameter drawPoints = new BooleanParameter( "draw points", false );

    /** parameters for controlling the position and size of an optional obstacle */
    private DoubleParameter xpos = new DoubleParameter( "x position", .5, -2, 2 );
    private DoubleParameter ypos = new DoubleParameter( "y position", 0, -2, 2 );
    private DoubleParameter zpos = new DoubleParameter( "z position", -.5, -2, 2 );
    private DoubleParameter radius = new DoubleParameter( "radius", .5, 0, 2 );
    private DoubleParameter radiusratio = new DoubleParameter( "radius display ratio", .95, .9, 1 );

    @Override
    public JPanel getControls() {
        VerticalFlowPanel vfp = new VerticalFlowPanel();

        vfp.add( surface.getControls() );

        vfp.add( comments );
        vfp.add( useGravity.getControls() );
        vfp.add( usePostStepFix.getControls() );
        vfp.add( usePenaltyForces.getControls() );
        vfp.add( mass.getSliderControls(true) );
        vfp.add( gravity.getSliderControls(true) );
        vfp.add( springStiffness.getSliderControls(false) );
        vfp.add( springDamping.getSliderControls(false) );
        vfp.add( viscousDamping.getSliderControls(false) );
        vfp.add( restitution.getSliderControls(false) );
        vfp.add( H.getSliderControls(false) );
        vfp.add( iterations.getSliderControls() );
        vfp.add( explicit.getControls() );

        vfp.add( xpos.getSliderControls(false) );
        vfp.add( ypos.getSliderControls(false) );
        vfp.add( zpos.getSliderControls(false) );
        vfp.add( radius.getSliderControls(false) );
        vfp.add( radiusratio.getSliderControls(false) );

        vfp.add( drawSprings.getControls() );
        vfp.add( drawPoints.getControls() );
        return vfp.getPanel();
    }
    
    @Override
    public String toString() {
        String ret = "Marie Cornellier\n" +
                     comments.getText() + "\n" +
                     "particles = " + particles.size() + "\n";
        if ( explicit.getValue() ) {
            ret += "integrator = " + integrator.getName() + "\n";
        } else {
            ret += "integrator = Backward Euler\n";
        }
        ret += "k = " + springStiffness.getValue() + "\n" +
               "c = " + springDamping.getValue() + "\n" +
               "b = " + viscousDamping.getValue() +"\n" + 
               "time = " + time;
        return ret;
    }

    /**
     * Fixes positions and velocities after a step to deal with collisions
     */
    public void postStepFixBox() {
        for ( Particle p : particles ) {
            if ( p.pinned ) {
                p.v.set(0,0,0);
            }
        }
        // do wall collisions
        double r = restitution.getValue();
        int width = 4;
        int height = 4;

        for ( Particle p : particles ) {
            if ( p.p.x <= -width ) {
                p.p.x = -width;
                if ( p.v.x < 0 ) p.v.x = - p.v.x * r;
                if ( p.f.x < 0 ) p.f.x = 0;
            }
            if ( p.p.x >= width ) {
                p.p.x = width;
                if (p.v.x > 0 ) p.v.x = - p.v.x * r;
                if (p.f.x > 0 ) p.f.x = 0;
            }

            if ( p.p.y >= height ) {
                p.p.y = height;
                if ( p.v.y > 0 ) p.v.y = - p.v.y * r;
                if ( p.f.y > 0 ) p.f.y = 0;
            }
            if ( p.p.y <= -height ) {
                p.p.y = -height;
                if ( p.v.y < 0 ) p.v.y = - p.v.y * r;
                if ( p.f.y < 0 ) p.f.y = 0;
            }

            if ( p.p.z <= -width ) {
                p.p.z = -width;
                if ( p.v.z < 0 ) p.v.z = - p.v.z * r;
                if ( p.f.z < 0 ) p.f.z = 0;
            }
            if ( p.p.z >= width ) {
                p.p.z = width;
                if (p.v.z > 0 ) p.v.z = - p.v.z * r;
                if (p.f.z > 0 ) p.f.z = 0;
            }
        }
    }

    private void penaltyForces(double h)
    {
        for(Particle p: particles)
        {
            for(Particle[] t: triangles)
            {
                if(p != t[0] && p != t[1] && p != t[2])
                {
                    Point3d p1 = t[0].p;
                    Point3d p2 = t[1].p;
                    Point3d p3 = t[2].p;

                    Vector3d v1 = new Vector3d();
                    Vector3d v2 = new Vector3d();
                    Vector3d normal = new Vector3d();

                    v1.sub(p2,p1);
                    v2.sub(p3,p1);
                    normal.cross(v1,v2);
                    normal.normalize();

                    Vector3d v = new Vector3d();
                    v.sub(p.p,p1);

                    double dot = normal.dot(v);

                    if(Math.abs(dot) < H.getValue())
                    {
                        double l = v.dot(normal);
                        normal.scale(l);

                        Point3d project = new Point3d();
                        project.sub(p.p, normal);

                        Vector3d v0 = new Vector3d();
                        v0.sub(project,p1);

                        Vector3d normalA = new Vector3d();
                        Vector3d normalB = new Vector3d();
                        Vector3d normalC = new Vector3d();

                        Vector3d v3 = new Vector3d();
                        Vector3d v4 = new Vector3d();
                        Vector3d v5 = new Vector3d();
                        Vector3d v6 = new Vector3d();

                        v3.sub(p3,p2);
                        v4.sub(project,p2);
                        v5.sub(p1,p3);
                        v6.sub(project,p3);

                        normalA.cross(v3, v4);
                        normalB.cross(v5, v6);
                        normalC.cross(v1, v0);

                        normal.cross(v1,v2);

                        double a = normal.dot(normalA)/normal.lengthSquared();
                        double b = normal.dot(normalB)/normal.lengthSquared();
                        double c = normal.dot(normalC)/normal.lengthSquared();

                        normal.normalize();

                        if(0 < a && a < 1 && 0 < b && b < 1 && 0 < c && c < 1)
                        {
                            Vector3d temp = new Vector3d();
                            temp.sub(project,p.p);

                            if(normal.dot(p.v) < 0)
                            {
                                normal.negate();
                            }

                            double d = H.getValue() - normal.dot(temp);

                            Vector3d vRel = new Vector3d();
                            Vector3d vTriangle = new Vector3d(a*t[0].v.x + b*t[1].v.x + c*t[2].v.x, a*t[0].v.y + b*t[1].v.y + c*t[2].v.y, a*t[0].v.z + b*t[1].v.z + c*t[2].v.z);

                            vRel.sub(p.v, vTriangle);

                            if(vRel.dot(normal) >= 0.1*d/h)
                            {
                                double j = -Math.min(h*Spring.default_k*d, p.mass*(0.1*d/h - vRel.dot(normal)));
                                double i = 2.0*j / (1.0+a*a+b*b+c*c);

                                if(!p.pinned)
                                {
                                    p.v.add(new Vector3d(-i*normal.x/p.mass, -i*normal.y/p.mass, -i*normal.z/p.mass));
                                }
                                if(!t[0].pinned)
                                {
                                    t[0].v.add(new Vector3d(i*a*normal.x/t[0].mass, i*a*normal.y/t[0].mass, i*a*normal.z/t[0].mass));
                                }
                                if(!t[1].pinned)
                                {
                                    t[1].v.add(new Vector3d(i*b*normal.x/t[1].mass, i*b*normal.y/t[1].mass, i*b*normal.z/t[1].mass));
                                }
                                if(!t[2].pinned)
                                {
                                    t[2].v.add(new Vector3d(i*c*normal.x/t[2].mass, i*c*normal.y/t[2].mass, i*c*normal.z/t[2].mass));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
