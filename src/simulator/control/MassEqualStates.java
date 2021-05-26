package simulator.control;

import org.json.JSONArray;
import org.json.JSONObject;

public class MassEqualStates implements StateComparator {

	public boolean equal(JSONObject s1, JSONObject s2) {

		// Check time equality
		if (s1.getDouble("time") != s2.getDouble("time"))
			return false;

		JSONArray a1 = s1.getJSONArray("bodies");
		JSONArray a2 = s2.getJSONArray("bodies");

		// Check that both states contain the same number of bodies
		if (a1.length() != a2.length())
			return false;

		// Check mass and id equality
		for (int i = 0; i < a1.length(); i++) {

			if (!a1.getJSONObject(i).getString("id").equals(a2.getJSONObject(i).getString("id")))
				return false;

			if (a1.getJSONObject(i).getDouble("m") != a2.getJSONObject(i).getDouble("m"))
				return false;
		}

		return true;
	}

}
