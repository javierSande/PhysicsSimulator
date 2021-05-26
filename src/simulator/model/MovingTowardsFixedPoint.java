package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class MovingTowardsFixedPoint implements ForceLaws {

	private Vector2D _c;
	private double _g;

	public MovingTowardsFixedPoint(Vector2D c, double g) {
		_c = c;
		_g = g;
	}

	public void apply(List<Body> bs) {

		for (Body b : bs) {
			b.addForce(_c.minus(b.getPosition()).direction().scale(_g * b.getMass()));
		}

	}

	public String toString() {
		return "Moving towards " + _c + " with constant acceleration " + _g;
	}

}
