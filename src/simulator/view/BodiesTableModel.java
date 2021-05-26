package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class BodiesTableModel extends AbstractTableModel implements SimulatorObserver {

	private static final long serialVersionUID = 1L;

	private List<Body> _bodies;
	private final String[] columnNames = { "Id", "Mass", "Position", "Velocity", "Force" };

	BodiesTableModel(Controller ctrl) {
		_bodies = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return _bodies.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return _bodies.get(rowIndex).getState().get(getKey(columnIndex));
	}

	//Returns the key corresponding to each column index
	private String getKey(int i) {
		switch (i) {
		case 0:
			return "id";
		case 1:
			return "m";
		case 2:
			return "p";
		case 3:
			return "v";
		case 4:
			return "f";

		default:
			throw new IllegalArgumentException("Unexpected value: " + i);
		}
	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_bodies = bodies;
		fireTableStructureChanged();
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_bodies = bodies;
		fireTableStructureChanged();

	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		_bodies = bodies;
		fireTableStructureChanged();
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		fireTableStructureChanged();
	}

	@Override
	public void onDeltaTimeChanged(double dt) {}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {}

	@Override
	public void onBodyDeleted(List<Body> bodies, Body b) {
		_bodies = bodies;
		fireTableStructureChanged();
	}
}
