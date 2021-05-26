package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;

public class BasicBodyBuilder extends Builder<Body> {

	private static String type = "basic";
	private static String desc = "Basic body";

	public BasicBodyBuilder() {
		super(type, desc);
	}

	public Body createTheInstance(JSONObject data) throws IllegalArgumentException {

		String id = data.getString("id");
		double m = data.getDouble("m");

		if (data.getJSONArray("p").length() != 2)
			throw new IllegalArgumentException("Positon vector must be bidimensional");

		Vector2D p = new Vector2D(data.getJSONArray("p").getDouble(0), data.getJSONArray("p").getDouble(1));

		if (data.getJSONArray("v").length() != 2)
			throw new IllegalArgumentException("Velocity vector must be bidimensional");

		Vector2D v = new Vector2D(data.getJSONArray("v").getDouble(0), data.getJSONArray("v").getDouble(1));

		return new Body(id, p, v, m);
	}

	public JSONObject getData() {
		JSONObject data = new JSONObject();
		data.put("id", "b1");
		Vector2D p = new Vector2D(0.0, 0.0);
		data.put("p", p.asJSONArray());
		Vector2D v = new Vector2D(0.05e04, 0.0);
		data.put("v", v.asJSONArray());
		data.put("m", 5.97e24);

		return data;
	}
}
