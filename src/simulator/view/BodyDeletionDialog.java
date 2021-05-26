package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BodyDeletionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private List<String> _bodiesList;

	private int _status;

	private JComboBox<String> _bodiesComboBox;
	private DefaultComboBoxModel<String> _comboBoxModel;

	public BodyDeletionDialog(Frame frame, List<String> bodies) {
		super(frame, true);
		_bodiesList = bodies;
		_status = -1;
		initGUI();
	}

	private void initGUI() {
		setTitle("Body deletion");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		// Help msg
		JLabel text = new JLabel(
				"<html><p>Select a body to delete</p></html>");

		text.setAlignmentX(CENTER_ALIGNMENT);
		mainPanel.add(text, BorderLayout.PAGE_START);

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

		// Combo Box
		_comboBoxModel = new DefaultComboBoxModel<>();
		_bodiesComboBox = new JComboBox<>(_comboBoxModel);
		setBodies();
		mainPanel.add(_bodiesComboBox);
		_bodiesComboBox.addActionListener((e) -> {
			_status = _bodiesComboBox.getSelectedIndex();
		});

		// Buttons
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);

		JButton okButton = new JButton("OK");
		okButton.addActionListener((e) -> {
			setVisible(false);
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener((e) -> {
			_status = -1;
			setVisible(false);
		});

		buttonsPanel.add(okButton);
		buttonsPanel.add(cancelButton);
		mainPanel.add(buttonsPanel);

		setContentPane(mainPanel);
		setPreferredSize(new Dimension(200, 150));
		setVisible(false);

	}
	
	public void setBodies(List<String> bodies) {
		_bodiesList = bodies;
		setBodies();
	}

	private void setBodies() {
		_comboBoxModel.removeAllElements();
		for (String b : _bodiesList)
			_comboBoxModel.addElement(b);
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


}
