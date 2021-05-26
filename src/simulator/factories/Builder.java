package simulator.factories;

import org.json.JSONObject;

public abstract class Builder<T> {

	protected String _type;
	protected String _desc;

	public Builder(String type, String desc) {
		_type = type;
		_desc = desc;
	}

	public T createInstance(JSONObject info) throws IllegalArgumentException {

		if (info.get("type").equals(_type))
			try {
				return createTheInstance(info.getJSONObject("data"));
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		else
			return null;
	}

	public abstract T createTheInstance(JSONObject info);

	public JSONObject getBuilderInfo() {

		JSONObject info = new JSONObject();

		info.put("type", _type);
		info.put("data", getData());
		info.put("desc", _desc);

		return info;
	}

	public abstract JSONObject getData();

}
