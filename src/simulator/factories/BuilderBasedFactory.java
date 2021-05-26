package simulator.factories;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {

	private List<Builder<T>> _builders;
	private List<JSONObject> _infoList;

	public BuilderBasedFactory(List<Builder<T>> builders) {

		_builders = builders;
		_infoList = new ArrayList<JSONObject>();

		for (Builder<T> b : builders)
			_infoList.add(b.getBuilderInfo());

	}

	public T createInstance(JSONObject info) {

		T t = null;

		for (Builder<T> b : _builders) {
			t = b.createInstance(info);
			if (t != null)
				return t;
		}

		return null;
	}

	public List<JSONObject> getInfo() {
		return _infoList;
	}

}
