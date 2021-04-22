package comp559.particle;

public class SymplecticEuler implements Integrator {

    @Override
    public String getName() {
        return "symplectic Euler";
    }
    private double[] dpdt;

    @Override
    public void step(double[] p, int n, double t, double h, double[] pout, Function derivs) {
        // TODO: Objective 7, complete the symplectic Euler integration method.
    	// note you'll need to know how p is packed to properly implement this, so go
    	// look at ParticleSystem.getPhaseSpace()
        if ( dpdt == null || dpdt.length != n ) {
            dpdt = new double[n];
        }

        derivs.derivs(t, p, dpdt);

        for(int i = 0; i < n; i+=6)
        {
            pout[i+3] = p[i+3] + h*dpdt[i+3];
            pout[i+4] = p[i+4] + h*dpdt[i+4];
            pout[i+5] = p[i+5] + h*dpdt[i+5];
            pout[i] = p[i] + h*pout[i+3];
            pout[i+1] = p[i+1] + h*pout[i+4];
            pout[i+2] = p[i+2] + h*pout[i+5];
        }
    }

}
