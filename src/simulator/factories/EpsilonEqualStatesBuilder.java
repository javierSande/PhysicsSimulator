package simulator.factories;

import org.json.JSONObject;

import simulator.control.EpsilonEqualStates;
import simulator.control.StateComparator;

public class EpsilonEqualStatesBuilder extends Builder<StateComparator> {

	private static String type = "epseq";
	private static String desc = "Epsilon comparator";

	public EpsilonEqualStatesBuilder() {
		super(type, desc);
	}

	public StateComparator createTheInstance(JSONObject data) {

		double eps = 0.0;

		if (data.has("eps"))
			eps = data.getDouble("eps");

		return new EpsilonEqualStates(eps);

	}

	public JSONObject getData() {
		JSONObject data = new JSONObject();
		data.put("eps", 0.1);
		return data;
	}

}
