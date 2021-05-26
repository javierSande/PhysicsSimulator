package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public class EpsilonEqualStates implements StateComparator {

	private double eps;

	public EpsilonEqualStates(double eps) {
		this.eps = eps;
	}

	public boolean equal(JSONObject s1, JSONObject s2) {

		// Check time equality
		if (s1.getDouble("time") != s2.getDouble("time"))
			return false;

		JSONArray a1 = s1.getJSONArray("bodies");
		JSONArray a2 = s2.getJSONArray("bodies");

		// Check that both states contain the same number of bodies
		if (a1.length() != a2.length())
			return false;

		// Check mass, position, force and velocity equality
		for (int i = 0; i < a1.length(); i++) {

			JSONObject a = a1.getJSONObject(i);
			JSONObject b = a2.getJSONObject(i);

			if (!a.getString("id").equals(b.getString("id")))
				return false;

			if (Math.abs(a.getDouble("m") - b.getDouble("m")) > eps)
				return false;

			if (!equalEps(a.getJSONArray("p"), b.getJSONArray("p")))
				return false;

			if (!equalEps(a.getJSONArray("v"), b.getJSONArray("v")))
				return false;

			if (!equalEps(a.getJSONArray("f"), b.getJSONArray("f")))
				return false;

		}

		return true;
	}

	private boolean equalEps(JSONArray a, JSONArray b) {

		Vector2D vA = new Vector2D(a.getDouble(0), a.getDouble(1));
		Vector2D vB = new Vector2D(b.getDouble(0), b.getDouble(1));

		if (Math.abs(vA.distanceTo(vB)) > eps)
			return false;

		return true;

	}

}
