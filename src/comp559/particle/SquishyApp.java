package comp559.particle;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;

import javax.swing.*;

import mintools.parameters.BooleanParameter;
import mintools.parameters.DoubleParameter;
import mintools.parameters.IntParameter;
import mintools.swing.FileSelect;
import mintools.swing.HorizontalFlowPanel;
import mintools.swing.VerticalFlowPanel;
import mintools.viewer.*;


/**
 * Provided code for particle system simulator.
 * This class provides the mouse interface for clicking and dragging particles, and the 
 * code to draw the system.  When the simulator is running system.advanceTime is called
 * to numerically integrate the system forward.
 * @author kry
 */
public class SquishyApp implements SceneGraphNode, Interactor {

    private EasyViewer ev;

    private ParticleSystem system;

    /**
     * Entry point for application
     * @param args
     */
    public static void main(String[] args) {
        new SquishyApp();
    }
    
    /**
     * Creates the application / scene instance
     */
    public SquishyApp() {
        system = new ParticleSystem();
        system.integrator = rk4;

        // create the simulation viewing window
        // used to be 640 by 360 but i changed it, doesn't really matter
        ev = new EasyViewer( "COMP 559 - Squishy Ball Project", this, new Dimension(640,600), new Dimension(640,600) );
        ev.addInteractor(this);
    }
     
    @Override
    public void init(GLAutoDrawable drawable) {
        GL gl = drawable.getGL();
        gl.glEnable( GL.GL_BLEND );
        gl.glBlendFunc( GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA );
        gl.glEnable( GL.GL_LINE_SMOOTH );
        gl.glEnable( GL2.GL_POINT_SMOOTH );
        system.init(drawable);

//        system.loadSystem( "./savedSystems/spider_ball.xlsx" );
    }

    private FancyAxis fa = new FancyAxis(0.5);

    private BoxRoom boxRoom = new BoxRoom();

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        if ( run.getValue() ) {
            for ( int i = 0; i < substeps.getValue(); i++ ) {
                system.advanceTime( stepsize.getValue() / substeps.getValue() );                
            }
        }

        if ( drawAxis.getValue() ) { fa.draw(gl); }
        boxRoom.display(drawable);
        system.display( drawable );

        EasyViewer.beginOverlay( drawable );
        String text = system.toString() + "\n" +
                "h = " + stepsize.getValue() + "\n" +
                "substeps = " + substeps.getValue() + "\n" +
                "computeTime = " + system.computeTime;
        EasyViewer.printTextLines( drawable, text );
        EasyViewer.endOverlay(drawable);

    }
    
    private BooleanParameter record = new BooleanParameter( "record (press ENTER in canvas to toggle)", false );
    private JTextField videoFileName = new JTextField("demo.mp4");
    private boolean recordingStarted = false;    
    private int numRecordedFrames = 0;
    
    /** 
     * boolean to signal that the system was stepped and that a 
     * frame should be recorded if recording is enabled
     */
    private boolean stepped = false;

    private BooleanParameter run = new BooleanParameter( "simulate", false );
    private DoubleParameter stepsize = new DoubleParameter( "step size", 0.0185, 1e-5, 1 );
    private IntParameter substeps = new IntParameter( "sub steps", 20, 1, 100);
    private BooleanParameter drawAxis = new BooleanParameter( "draw axis (to help get your bearings in 3D)", false );

    @Override
    public JPanel getControls() {
        HorizontalFlowPanel hfp0 = new HorizontalFlowPanel();

        HorizontalFlowPanel hfp02 = new HorizontalFlowPanel();

        JButton subdivideButton = new JButton("Subdivide");
        subdivideButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                system.subdivide( );
            }
        });

        hfp02.add( subdivideButton );

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                system.createSimpleIcosphere( );
            }
        });

        hfp02.add( resetButton );

        VerticalFlowPanel vfp = new VerticalFlowPanel();
        vfp.add( hfp0.getPanel() );
        vfp.add( hfp02.getPanel() );

        HorizontalFlowPanel hfp1 = new HorizontalFlowPanel();
        JButton res2 = new JButton("1280x720");
        hfp1.add( res2);
        res2.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                ev.glCanvas.setSize( 1280, 720 );
                ev.frame.setSize( ev.frame.getPreferredSize() );
            }
        });   
        JButton res1 = new JButton("640x360");
        hfp1.add( res1 );
        res1.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ev.glCanvas.setSize( 640, 360 );
                ev.frame.setSize( ev.frame.getPreferredSize() );
            }
        });

        vfp.add( hfp1.getPanel() );

        vfp.add( system.getControls() );

        vfp.add( videoFileName );
        vfp.add( record.getControls() );
        vfp.add( run.getControls() );
        vfp.add( stepsize.getSliderControls(true) );
        vfp.add( substeps.getControls() );

        vfp.add( drawAxis.getControls());
        
        return vfp.getPanel();
    }

    @Override
    public void attach(Component component) {
        component.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // space to run
                if ( e.getKeyCode() == KeyEvent.VK_SPACE ) {
                    run.setValue( ! run.getValue() );
                }
                // S to step forward
                else if ( e.getKeyCode() == KeyEvent.VK_S ) {
                    for ( int i = 0; i < substeps.getValue(); i++ ) {
                        system.advanceTime( stepsize.getValue() / substeps.getValue() );                
                    }
                    stepped = true;
                }
                // R to reset
                else if ( e.getKeyCode() == KeyEvent.VK_R ) {
                    system.resetParticles();                    
                }
                // C to clear
                else if ( e.getKeyCode() == KeyEvent.VK_C ) {
                    system.clearParticles();
                }
                // 1 : Forward Euler
                else if ( e.getKeyCode() == KeyEvent.VK_1 ) {
                    system.explicit.setValue(true);
                    system.integrator = forwardEuler;                    
                }
                // 2 : Midpoint
                else if ( e.getKeyCode() == KeyEvent.VK_2 ) {
                    system.explicit.setValue(true);
                    system.integrator = midpoint;
                }
                // 3 : Modified Midpoint
                else if ( e.getKeyCode() == KeyEvent.VK_3 ) {
                    system.explicit.setValue(true);
                    system.integrator = modifiedMidpoint;
                }
                // 4 : Symplectic Euler
                else if ( e.getKeyCode() == KeyEvent.VK_4 ) {
                    system.explicit.setValue(true);
                    system.integrator = symplecticEuler;
                }
                // 5 : RK4
                else if ( e.getKeyCode() == KeyEvent.VK_5 ) {
                    system.explicit.setValue(true);
                    system.integrator = rk4;
                }
                // 6 : explicit off
                else if ( e.getKeyCode() == KeyEvent.VK_6 ) {
                    system.explicit.setValue(false);     // turn explicit on or off
                }
                // Esc: quit application
                else if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
                    ev.stop();
                }
                else if ( e.getKeyCode() == KeyEvent.VK_Z ) {
                	for ( Particle p : system.getParticles() ) {
                		p.v.set(0,0,0);
                	}
                }
                // enter : record
                else if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    record.setValue( ! record.getValue() ); // start or stop recording
                }
                // B : both run and record
                else if ( e.getKeyCode() == KeyEvent.VK_B ) {
                    record.setValue( ! record.getValue() ); // start or stop recording
                    run.setValue( ! run.getValue() );
                }
                else if ( e.getKeyCode() == KeyEvent.VK_UP ) {
                    substeps.setValue( substeps.getValue() + 1 ); // increase number of substeps
                }
                else if ( e.getKeyCode() == KeyEvent.VK_DOWN ) {
                    substeps.setValue( substeps.getValue() - 1 ); // decrease number of substeps
                }
                if ( e.getKeyCode() != KeyEvent.VK_ESCAPE ) ev.redisplay();
            }
        } );
    }
    
    private ForwardEuler forwardEuler = new ForwardEuler();    
    private Midpoint midpoint = new Midpoint();
    private ModifiedMidpoint modifiedMidpoint = new ModifiedMidpoint();
    private RK4 rk4 = new RK4();
    private SymplecticEuler symplecticEuler = new SymplecticEuler();
    
}
