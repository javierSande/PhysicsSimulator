package simulator.factories;

import org.json.JSONObject;

import simulator.control.MassEqualStates;
import simulator.control.StateComparator;

public class MassEqualStatsBuilder extends Builder<StateComparator> {

	private static String type = "masseq";
	private static String desc = "Mass comparator";

	public MassEqualStatsBuilder() {
		super(type, desc);
	}

	public StateComparator createTheInstance(JSONObject data) {
		return new MassEqualStates();
	}

	public JSONObject getData() {
		return new JSONObject();
	}

}
