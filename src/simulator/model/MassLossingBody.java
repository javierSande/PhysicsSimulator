package simulator.model;

import simulator.misc.Vector2D;

public class MassLossingBody extends Body {

	private double _lossFactor;
	private double _lossFrequency;
	private double _counter;

	public MassLossingBody(String id, Vector2D p, Vector2D v, double m, double lossFactor, double lossFrequency) {
		super(id, p, v, m);

		_lossFactor = lossFactor;
		_lossFrequency = lossFrequency;

		_counter = 0;
	}

	void move(double t) {

		super.move(t);

		_counter += t;

		if (_counter >= _lossFrequency) {
			_m = _m * (1 - _lossFactor); // Reduces mass
			_counter = 0.0;
		}

	}

}
