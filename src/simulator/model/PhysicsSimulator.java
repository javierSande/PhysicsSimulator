package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class PhysicsSimulator implements Observable<SimulatorObserver> {

	private double _realTimePerStep;
	private ForceLaws _f;
	private List<Body> _bs;
	private double _time;
	private List<SimulatorObserver> _os;

	public PhysicsSimulator(double realTimePerStep, ForceLaws f) throws IllegalArgumentException {

		if (realTimePerStep < 0)
			throw new IllegalArgumentException("Real time per step must be greather than 0");
		if (f == null)
			throw new IllegalArgumentException("Null force");

		_f = f;
		_realTimePerStep = realTimePerStep;
		_time = 0.0;

		_bs = new ArrayList<Body>();
		_os = new ArrayList<SimulatorObserver>();
	}

	public void advance() {

		for (Body b : _bs) {
			b.resetForce();
		}

		_f.apply(_bs);

		for (Body b : _bs) {
			b.move(_realTimePerStep);
		}

		_time += _realTimePerStep;

		for (SimulatorObserver o : _os)
			o.onAdvance(_bs, _time);
	}

	public void addBody(Body b) throws IllegalArgumentException {

		if (b == null)
			throw new IllegalArgumentException("Null body");
		if (_bs.contains(b))
			throw new IllegalArgumentException("Dupliacted bodies in the input file");

		_bs.add(b);

		for (SimulatorObserver o : _os)
			o.onBodyAdded(_bs, b);
	}

	public void reset() {

		_bs = new ArrayList<Body>();

		_time = 0.0;

		for (SimulatorObserver o : _os)
			o.onReset(_bs, _time, _realTimePerStep, _f.toString());
	}

	public void setDeltaTime(double dt) {
		if (dt == 0.0)
			throw new IllegalArgumentException();

		_realTimePerStep = dt;

		for (SimulatorObserver o : _os)
			o.onDeltaTimeChanged(dt);

	}

	public void setForceLaws(ForceLaws forceLaws) {

		if (forceLaws == null)
			throw new IllegalArgumentException();

		_f = forceLaws;

		for (SimulatorObserver o : _os)
			o.onForceLawsChanged(forceLaws.toString());

	}

	public void addObserver(SimulatorObserver o) {
		if (o == null || _os.contains(o))
			throw new IllegalArgumentException();

		_os.add(o);

		o.onRegister(_bs, _time, _realTimePerStep, _f.toString());
	}

	public JSONObject getState() {

		JSONObject j = new JSONObject();

		j.put("time", _time);

		JSONArray ja = new JSONArray();

		for (Body b : _bs) {
			ja.put(b.getState());
		}

		j.put("bodies", ja);

		return j;

	}

	public String toString() {
		return getState().toString();
	}

	public void delBody(Body b) {
		_bs.remove(b);
		for (SimulatorObserver o : _os)
			o.onBodyDeleted(_bs, b);
	}

}
