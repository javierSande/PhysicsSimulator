package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.ForceLaws;
import simulator.model.MovingTowardsFixedPoint;

public class MovingTowarsdFixedPointBuilder extends Builder<ForceLaws> {

	private static String type = "mtfp";
	private static String desc = "Moving towards a fixed point";

	public MovingTowarsdFixedPointBuilder() {
		super(type, desc);
	}

	public ForceLaws createTheInstance(JSONObject data) throws IllegalArgumentException {

		Vector2D c = new Vector2D();
		double g = 9.81;

		if (data.has("c")) {
			if (data.getJSONArray("c").length() != 2)
				throw new IllegalArgumentException("C vector must be bidimensional");
			else
				c = new Vector2D(data.getJSONArray("c").getDouble(0), data.getJSONArray("c").getDouble(1));
		}

		if (data.has("g"))
			g = data.getDouble("g");

		return new MovingTowardsFixedPoint(c, g);
	}

	public JSONObject getData() {
		JSONObject data = new JSONObject();
		data.put("c", "the point towards which bodies move (a json list of 2 numbers, e.g., [100.0,50.0])");
		data.put("g", "the length of the acceleration vector (a number)");
		return data;
	}

}
