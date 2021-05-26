package simulator.control;

import simulator.model.Body;
import simulator.model.ForceLaws;
import simulator.model.PhysicsSimulator;
import simulator.model.SimulatorObserver;
import simulator.factories.Factory;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Controller {

	private PhysicsSimulator _s;
	private Factory<Body> _bf;
	private Factory<ForceLaws> _ff;

	public Controller(PhysicsSimulator s, Factory<Body> bf, Factory<ForceLaws> ff) {
		_s = s;
		_bf = bf;
		_ff = ff;
		
	}

	public void loadBodies(InputStream in) {

		JSONObject jsonInput = new JSONObject(new JSONTokener(in));

		JSONArray bodies = jsonInput.getJSONArray("bodies");

		for (int i = 0; i < bodies.length(); i++) {
			_s.addBody(_bf.createInstance(bodies.getJSONObject(i)));
		}
	}

	public void run(int n, OutputStream out, InputStream expOut, StateComparator cmp) throws CmpException {

		boolean compare = (expOut != null);

		JSONArray expStates = null;

		if (compare) {

			JSONObject jsonExpOut = new JSONObject(new JSONTokener(expOut));
			expStates = jsonExpOut.getJSONArray("states");
		}

		PrintStream p = new PrintStream(out);

		p.println("{");
		p.println("\"states\": [");

		// Compare state 0
		if (compare) {
			if (!cmp.equal(_s.getState(), expStates.getJSONObject(0)))
				throw new CmpException(
						String.format("Error on step 0:%n%s%n%s", _s.toString(), expStates.get(0).toString()));
		}

		// Print state 0
		p.println(_s);

		for (int i = 1; i <= n; i++) {
			_s.advance();

			if (compare) {
				if (!cmp.equal(_s.getState(), expStates.getJSONObject(i)))
					throw new CmpException(
							String.format("Error on step %d:%n%s%n%s", i, _s.toString(), expStates.get(i).toString()));
			}

			p.println("," + _s);
		}

		p.println("]");
		p.println("}");

	}

	public void run(int n) {
		
		for (int i = 1; i <= n; i++)
			_s.advance();
		
	}

	public void reset() {
		_s.reset();
	}

	public void setDeltaTime(double dt) {
		_s.setDeltaTime(dt);
	}

	public void addObserver(SimulatorObserver o) {
		_s.addObserver(o);
	}

	public List<JSONObject> getForceLawsInfo() {
		return _ff.getInfo();
	}

	public void setForcesLaws(JSONObject info) {
		_s.setForceLaws(_ff.createInstance(info));
	}
	
	public void addBody(JSONObject b) {
		_s.addBody(_bf.createInstance(b));
	}
	
	public void delBody(Body b) {
		_s.delBody(b);
	}

}
