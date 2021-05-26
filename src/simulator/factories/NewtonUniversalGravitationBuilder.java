package simulator.factories;

import org.json.JSONObject;

import simulator.model.ForceLaws;
import simulator.model.NewtonUniversalGravitation;

public class NewtonUniversalGravitationBuilder extends Builder<ForceLaws> {

	private static String type = "nlug";
	private static String desc = "Newton’s law of universal gravitation";

	public NewtonUniversalGravitationBuilder() {
		super(type, desc);
	}

	public ForceLaws createTheInstance(JSONObject data) {

		double g = 6.67e-11;

		if (data.has("G"))
			g = data.getDouble("G");

		return new NewtonUniversalGravitation(g);
	}

	public JSONObject getData() {
		JSONObject data = new JSONObject();
		data.put("G", "the gravitational constant (a number)");
		return data;
	}

}
