package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NoForce;

public class NoForceBuilder extends Builder<ForceLaws> {

	private static String type = "nf";
	private static String desc = "No force";

	public NoForceBuilder() {
		super(type, desc);
	}

	public ForceLaws createTheInstance(JSONObject data) {
		return new NoForce();
	}

	@Override
	public JSONObject getData() {
		return new JSONObject();
	}

}
