# Squishy

Welcome to my first squishy ball physics simulation using Java! See [particlesystem.java](src/comp559/particle/ParticleSystem.java) for the icosahedron formula. Run configurations are set for intelliJ.
* Check out the [report](report.pdf) and [video demonstration](https://www.youtube.com/watch?v=-ZCJqIITaxQ)!

![pic](Screen Shot 2021-09-29 at 1.59.38 PM.png)
![pic](Screen Shot 2021-09-29 at 2.08.02 PM.png)
![pic](Screen Shot 2021-09-29 at 2.09.13 PM.png)
![pic](Screen Shot 2021-09-29 at 2.11.02 PM.png)

## The GUI
* Click and scroll to zoom in.
* Starts with an icosahedron, can subdivide it a few times before it crashes.
* Decrease spring damping for more jiggly ball.
* Keyboard commands:
  * 1: forward euler (not recomended)
  * 2: symplectic euler
  * 3: midpoint
  * 4: modified midpoint
  * 5: rk4
  * 6: backward euler
  * space: run
  * R: restart
