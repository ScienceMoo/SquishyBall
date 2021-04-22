package comp559.particle;

import javax.vecmath.Vector3d;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;

/**
 * Spring class for 599 assignment 1
 * @author kry
 */
public class Spring {

    Particle p1;
    Particle p2;
    boolean visible;
    boolean useDefaultK;
    boolean useDefaultC;
    double k;
    double c;

    /** Spring stiffness, sometimes written k_s in equations */
    public static double default_k = 1;
    /** Spring damping (along spring direction), sometimes written k_d in equations */
    public static double default_c = 1;
    /** Rest length of this spring */
    double l0 = 0;
    
    /**
     * Creates a spring between two particles
     * @param p1
     * @param p2
     */
    public Spring( Particle p1, Particle p2 ) {
        this.p1 = p1;
        this.p2 = p2;
        this.visible = true;
        this.useDefaultK = true;
        this.useDefaultC = true;
        recomputeRestLength();
        p1.springs.add(this);
        p2.springs.add(this);
        this.apply();
    }

    /**
     * Constructor for invisible mouse spring
     * @param p1
     * @param p2
     */
    public Spring( Particle p1, Particle p2, double speficic_k, double speficic_c, double specific_l0, boolean vis ) {
        this.p1 = p1;
        this.p2 = p2;
        this.k = speficic_k;
        this.c = speficic_c;
        this.l0 = specific_l0;
        this.visible = vis;
        this.useDefaultK = false;
        this.useDefaultC = false;

        p1.springs.add(this);
        p2.springs.add(this);
        this.apply();
    }

    /**
     * Computes and sets the rest length based on the original position of the two particles 
     */
    public void recomputeRestLength() {

        l0 = p1.p0.distance( p2.p0 );
    }
    
    /**
     * Applies the spring force by adding a force to each particle
     */
    public void apply() {
        // TODO: Objective 1, FINISH THIS CODE!

        Vector3d force = new Vector3d();
        force.sub( p2.p, p1.p );
        double l = force.length();
        force.normalize();

        // force due to spring displacement from rest length
        if (this.useDefaultK) {
            force.scale( (l-l0)*default_k );
        }else {
            force.scale( (l-l0)*this.k );
        }


        Vector3d force2 = new Vector3d();
        Vector3d v = new Vector3d();
        force2.sub( p2.p, p1.p );
        force2.normalize();
        v.sub(p2.v, p1.v);
        double rv = force2.dot(v);

        // force due to damping
        if (this.useDefaultC) {
            force2.scale(default_c*rv);
        }else {
            force2.scale(this.c*rv);
        }

        force.add(force2);

        // apply force to each particle
        p1.addForce(force);
        force.scale(-1);
        p2.addForce(force);
    }
   
    /** the functions below are for the backwards Euler solver */
    
    /**
     * Computes the force and adds it to the appropriate components of the force vector.
     * (This function is something you might use for a backward Euler integrator)
     * @param f
     */
    public void addForce( Vector f ) {
        // TODO: Objective 8, FINISH THIS CODE for backward Euler method (probably very simlar to what you did above)

        // same as above except the forces are added differently

        Vector3d force = new Vector3d();

        force.sub( p2.p, p1.p );
        double l = force.length();
        force.normalize();
        if (this.useDefaultK) {
            force.scale( (l-l0)*default_k );
        }else {
            force.scale( (l-l0)*this.k );
        }
        f.add(p1.index*3,force.x);
        f.add(p1.index*3+1,force.y);
        f.add(p1.index*3+2,force.z);
        force.scale(-1);
        f.add(p2.index*3,force.x);
        f.add(p2.index*3+1,force.y);
        f.add(p2.index*3+2,force.z);

        // calculate force from kinetic energy
        force.sub( p2.p, p1.p );
        force.normalize();
        Vector3d v = new Vector3d();
        v.sub(p2.v, p1.v);
        double rv = force.dot(v);
        if (this.useDefaultC) {
            force.scale(default_c*rv);
        }else {
            force.scale(this.c*rv);
        }
        f.add(p1.index*3,force.x);
        f.add(p1.index*3+1,force.y);
        f.add(p1.index*3+2,force.z);
        force.scale(-1);
        f.add(p2.index*3,force.x);
        f.add(p2.index*3+1,force.y);
        f.add(p2.index*3+2,force.z);
    }

    public void addParticle(Matrix mtrx, Particle p1, Particle p2, DenseMatrix tmpM) {
        mtrx.add(p1.index*3, p2.index*3, tmpM.get(0, 0));
        mtrx.add(p1.index*3, p2.index*3+1, tmpM.get(0, 1));
        mtrx.add(p1.index*3, p2.index*3+2, tmpM.get(0, 2));
        mtrx.add(p1.index*3+1, p2.index*3, tmpM.get(1, 0));
        mtrx.add(p1.index*3+1, p2.index*3+1, tmpM.get(1, 1));
        mtrx.add(p1.index*3+1, p2.index*3+2, tmpM.get(1, 2));
    }
    
    /**
     * Adds this springs contribution to the stiffness matrix
     * @param dfdx
     */
    public void addDfdx( Matrix dfdx ) {
        // TODO: Objective 8, FINISH THIS CODE... necessary for backward euler integration
        // dfdx = -k * ((1 - l0/|l|)(I - l * l^T) + l * l^T)

        DenseVector tmpV = new DenseVector(3);
        DenseMatrix tmpM = new DenseMatrix(3,3);
        DenseMatrix I = new DenseMatrix(3,3);

        Vector3d l = new Vector3d(p1.p.x - p2.p.x, p1.p.y - p2.p.y, p1.p.z - p2.p.z);

        tmpV.set(0,l.x/l.length());
        tmpV.set(1,l.y/l.length());
        tmpV.set(2,l.z/l.length());

        tmpM.rank1(tmpV);
        I.set(tmpM);
        I.scale(-1);
        I.add(0, 0, 1.0);
        I.add(1, 1, 1.0);
        I.add(2, 2, 1.0);

        I.scale(1 - l0/l.length());

        tmpM.add(I);
        if (this.useDefaultK) {
            tmpM.scale(-default_k);
        }else {
            tmpM.scale(-this.k);
        }

        addParticle(dfdx, p1, p1, tmpM);
        addParticle(dfdx, p2, p2, tmpM);

        tmpM.scale(-1);

        addParticle(dfdx, p1, p2, tmpM);
        addParticle(dfdx, p2, p1, tmpM);

    }   
 
    /**
     * Adds this springs damping contribution to the implicit damping matrix
     * @param dfdv
     */
    public void addDfdv( Matrix dfdv ) {
        // TODO: Objective 8, FINISH THIS CODE... necessary for backward Euler integration
        // dfdv = -c * l * l^T

        DenseVector tmpV = new DenseVector(3);
        DenseMatrix tmpM = new DenseMatrix(3,3);

        Vector3d l = new Vector3d(p1.p.x - p2.p.x, p1.p.y - p2.p.y, p1.p.z - p2.p.z);

        tmpV.set(0,l.x/l.length());
        tmpV.set(1,l.y/l.length());
        tmpV.set(2,l.z/l.length());

        tmpM.rank1(tmpV);
        if (this.useDefaultC) {
            tmpM.scale(-default_c);
        }else {
            tmpM.scale(-this.c);
        }

        addParticle(dfdv, p1, p1, tmpM);
        addParticle(dfdv, p2, p2, tmpM);

        tmpM.scale(-1);

        addParticle(dfdv, p1, p2, tmpM);
        addParticle(dfdv, p2, p1, tmpM);
    } 
    
}
