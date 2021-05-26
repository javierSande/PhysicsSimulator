package simulator.model;

import java.util.List;

import simulator.misc.Vector2D;

public class NewtonUniversalGravitation implements ForceLaws {

	private double _g;

	public NewtonUniversalGravitation(double g) {
		_g = g;
	}

	public void apply(List<Body> bs) {

		for (Body a : bs) {
			for (Body b : bs) {
				if (!a.equals(b))
					a.addForce(force(a, b));
			}
		}
	}

	private Vector2D force(Body a, Body b) {

		Vector2D d = b.getPosition().minus(a.getPosition());

		double distance = d.magnitude();

		double f = 0;

		if (distance > 0)
			f = _g * a.getMass() * b.getMass() / (distance * distance);

		return d.direction().scale(f);
	}

	public String toString() {
		return "Newton’s Universal Gravitation with G=" + _g;
	}

}
