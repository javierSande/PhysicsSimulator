package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.MassLossingBody;

public class MassLosingBodyBuilder extends Builder<Body> {

	private static String type = "mlb";
	private static String desc = "Mass losing body";

	public MassLosingBodyBuilder() {
		super(type, desc);
	}

	public Body createTheInstance(JSONObject data) throws IllegalArgumentException {

		String id = data.getString("id");

		double m = data.getDouble("m");
		double freq = data.getDouble("freq");
		double factor = data.getDouble("factor");

		if (data.getJSONArray("p").length() != 2)
			throw new IllegalArgumentException("Positon vector must be bidimensional");

		Vector2D p = new Vector2D(data.getJSONArray("p").getDouble(0), data.getJSONArray("p").getDouble(1));

		if (data.getJSONArray("v").length() != 2)
			throw new IllegalArgumentException("Velocity vector must be bidimensional");

		Vector2D v = new Vector2D(data.getJSONArray("v").getDouble(0), data.getJSONArray("v").getDouble(1));

		return new MassLossingBody(id, p, v, m, factor, freq);

	}

	public JSONObject getData() {
		JSONObject data = new JSONObject();
		data.put("id", "b1");
		Vector2D p = new Vector2D(-3.5e10, 0.0);
		data.put("p", p.asJSONArray());
		Vector2D v = new Vector2D(0.0, 1.4e3);
		data.put("v", v.asJSONArray());
		data.put("m", 3.0e28);
		data.put("freq", 1e3);
		data.put("factor", 1e-3);
		return data;
	}

}
