package simulator.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.Set;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.json.JSONObject;

import simulator.control.Controller;

public class ForceLawsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private Controller _ctrl;

	private int _status;
	private int _selectedLaw;

	private JTable _dataTable;
	private JsonTableModel _tableModel;
	private JComboBox<String> _forceLawsCombo;
	private DefaultComboBoxModel<String> _forceLawsModel;

	private class JsonTableModel extends AbstractTableModel {

		private static final long serialVersionUID = 1L;

		private String[] _header = { "Key", "Value", "Description" };
		String[][] _data;

		JsonTableModel() {
			_data = new String[0][_header.length];
			clear();
		}

		//Updates the values of each cell given the index of the selected law
		public void update(int lawIndex) {
			JSONObject law = _ctrl.getForceLawsInfo().get(lawIndex).getJSONObject("data");
			Set<String> keys = law.keySet();
			int i = 0;
			_data = new String[keys.size()][_header.length];
			clear();
			for (String key : keys) {
				setValueAt(key, i, 0);
				setValueAt("", i, 1);
				setValueAt(law.getString(key), i, 2);
				i++;
			}
			fireTableStructureChanged();
		}

		public void clear() {
			for (int i = 0; i < _data.length; i++)
				for (int j = 0; j < _header.length; j++)
					_data[i][j] = "";
			fireTableStructureChanged();
		}

		@Override
		public String getColumnName(int column) {
			return _header[column];
		}

		@Override
		public int getRowCount() {
			return _data.length;
		}

		@Override
		public int getColumnCount() {
			return _header.length;
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == 1);
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return _data[rowIndex][columnIndex];
		}

		@Override
		public void setValueAt(Object o, int rowIndex, int columnIndex) {
			_data[rowIndex][columnIndex] = o.toString();
		}

		public String getData() {
			StringBuilder s = new StringBuilder();
			s.append('{');
			for (int i = 0; i < _data.length; i++) {
				if (!_data[i][0].isEmpty() && !_data[i][1].isEmpty()) {
					s.append('"');
					s.append(_data[i][0]);
					s.append('"');
					s.append(':');
					s.append(_data[i][1]);
					s.append(',');
				}
			}

			if (s.length() > 1)
				s.deleteCharAt(s.length() - 1);
			s.append('}');

			return s.toString();
		}
	}

	public ForceLawsDialog(Frame frame, Controller ctrl) {
		super(frame, true);
		_ctrl = ctrl;
		_selectedLaw = 0;
		initGUI();
	}

	private void initGUI() {
		setTitle("Force Laws Selection");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// Help msg
		JLabel text = new JLabel(
				"<html><p>Select a force law and provide values for the parameters in the Value column "
						+ "(default values are used for parameters with no value)</p></html>");

		text.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(text, BorderLayout.PAGE_START);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Table
		_tableModel = new JsonTableModel();
		_dataTable = new JTable(_tableModel) {
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component component = super.prepareRenderer(renderer, row, column);
				int rendererWidth = component.getPreferredSize().width;
				TableColumn tableColumn = getColumnModel().getColumn(column);
				tableColumn.setPreferredWidth(
						Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
				return component;
			}
		};
		JScrollPane tabelScroll = new JScrollPane(_dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPanel.add(tabelScroll);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Combo Box
		_forceLawsModel = new DefaultComboBoxModel<>();
		_forceLawsCombo = new JComboBox<>(_forceLawsModel);
		setLaws();
		mainPanel.add(_forceLawsCombo);
		_forceLawsCombo.addActionListener((e) -> {
			_selectedLaw = _forceLawsCombo.getSelectedIndex();
			refresh();
		});

		// Buttons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);

		JButton okButton = new JButton("OK");
		okButton.addActionListener((e) -> {
			_status = 1;
			setVisible(false);
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((e) -> {
			_status = 0;
			setVisible(false);
		});

		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		mainPanel.add(buttonsPanel);
		refresh();

		setContentPane(mainPanel);
		setPreferredSize(new Dimension(400, 350));
		setVisible(false);

	}

	private void setLaws() {
		for (JSONObject o : _ctrl.getForceLawsInfo())
			_forceLawsModel.addElement(o.getString("desc"));
	}

	private void refresh() {
		_tableModel.update(_selectedLaw);
	}

	public int open() {

		if (getParent() != null)
			setLocation(//
					getParent().getLocation().x + getParent().getWidth() / 2 - getWidth() / 2, //
					getParent().getLocation().y + getParent().getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
		return _status;
	}

	public JSONObject getJSON() {
		//Returns the JSONObject of the selected law with the parameters values of the table
		JSONObject law = new JSONObject();
		JSONObject data = new JSONObject(_tableModel.getData());
		law.put("type", _ctrl.getForceLawsInfo().get(_selectedLaw).get("type"));
		law.put("data", data);
		return law;
	}

}
