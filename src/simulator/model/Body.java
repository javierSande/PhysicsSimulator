package simulator.model;

import org.json.JSONObject;

import simulator.misc.Vector2D;

public class Body {

	protected String _id;
	protected Vector2D _p;
	protected Vector2D _v;
	protected Vector2D _f;
	protected double _m;

	public Body(String id, Vector2D p, Vector2D v, double m) {

		_id = id;
		_p = p;
		_v = v;
		_m = m;

		_f = new Vector2D(0, 0);

	}

	public String getId() {
		return _id;
	}

	public Vector2D getPosition() {
		return _p;
	}
	
	public double getX() {
		return _p.getX();
	}
	
	public double getY() {
		return _p.getY();
	}
	
	public void setPosition(double x, double y) {
		_p = new Vector2D(x, y);
	}

	public Vector2D getVelocity() {
		return _v;
	}

	public Vector2D getForce() {
		return _f;
	}

	public double getMass() {
		return _m;
	}

	void addForce(Vector2D f) {
		_f = _f.plus(f);
	}

	void resetForce() {

		_f = new Vector2D(0, 0);

	}

	void move(double t) {

		Vector2D a;

		// Computes acceleration
		if (_m > 0)
			a = _f.scale(1.0 / _m);
		else
			a = new Vector2D(0, 0);

		// Changes position

		_p = _p.plus(_v.scale(t).plus(a.scale(0.5 * t * t)));

		// Changes velocity
		_v = _v.plus(a.scale(t));

	}

	public JSONObject getState() {

		JSONObject j = new JSONObject();

		j.put("id", _id);
		j.put("m", _m);
		j.put("p", _p.asJSONArray());
		j.put("v", _v.asJSONArray());
		j.put("f", _f.asJSONArray());

		return j;

	}

	public String toString() {

		return getState().toString();

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Body other = (Body) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		return true;
	}

}
