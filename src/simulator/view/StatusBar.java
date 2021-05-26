package simulator.view;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class StatusBar extends JPanel implements SimulatorObserver {

	private static final long serialVersionUID = 1L;
	private JLabel _currTime; // for current time
	private JLabel _currLaws; // for force laws
	private JLabel _numOfBodies; // for number of bodies

	StatusBar(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
	}

	private void initGUI() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setBorder(BorderFactory.createBevelBorder(1));

		_currTime = new JLabel("Time: 0.0");
		_currTime.setPreferredSize(new Dimension(150,20));
		_currTime.setMaximumSize(new Dimension(150,20));
		_currTime.setMinimumSize(new Dimension(150,20));
		_currTime.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(_currTime);

		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setMaximumSize(new Dimension(20,20));
		this.add(separator);
		
		_numOfBodies = new JLabel("Bodies: 0");
		_numOfBodies.setPreferredSize(new Dimension(100,20));
		_numOfBodies.setMaximumSize(new Dimension(100,20));
		_numOfBodies.setMinimumSize(new Dimension(100,20));
		_numOfBodies.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(_numOfBodies);
		
		separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setMaximumSize(new Dimension(20,20));
		this.add(separator);
		
		_currLaws = new JLabel("Laws: ");
		_currLaws.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.add(_currLaws);
		
		this.setSize(getPreferredSize());

	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_currTime.setText("Time: " + time);
		_numOfBodies.setText("Bodies: " + bodies.size());
		_currLaws.setText("Laws: " + fLawsDesc);
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_currTime.setText("Time: " + time);
		_numOfBodies.setText("Bodies: " + bodies.size());
		_currLaws.setText("Laws: " + fLawsDesc);
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		_numOfBodies.setText("Bodies: " + bodies.size());
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		_currTime.setText("Time: " + time);
	}

	@Override
	public void onDeltaTimeChanged(double dt) {}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {
		_currLaws.setText("Laws: " + fLawsDesc);
	}

	@Override
	public void onBodyDeleted(List<Body> bodies, Body b) {
		_numOfBodies.setText("Bodies: " + bodies.size());	
	}
}
