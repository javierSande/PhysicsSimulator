package simulator.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import simulator.control.Controller;
import simulator.model.Body;
import simulator.model.Observable;
import simulator.model.SimulatorObserver;

public class ControlPanel extends JPanel implements SimulatorObserver, ActionListener, Observable<ControlPanelObserver> {

	private static final long serialVersionUID = 1L;
	private Controller _ctrl;
	private boolean _stopped;

	protected JButton openButton, forceLawsButton, runButton, stopButton, resetButton, deleteButton, exitButton;
	private JSpinner stepSpinner;
	private JTextField deltaTimeField;
	private JLabel stepsLabel, deltaTimeLabel;
	private ForceLawsDialog forcesLawsDialog;
	private BodyDeletionDialog deletionDialog;
	
	private List<Body> _bodies;
	private List<String> _bodiesId;

	private JFileChooser fc;
	
	private List<ControlPanelObserver> _obs;

	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		_stopped = true;
		_bodies = new ArrayList<>();
		_bodiesId = new ArrayList<>();
		
		_obs = new ArrayList<ControlPanelObserver>();
		
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {

		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		//Open button
		openButton = new JButton(new ImageIcon("resources/icons/open.png"));
		openButton.setToolTipText("Open a file");
		openButton.addActionListener(this);
		openButton.setAlignmentX(Component.LEFT_ALIGNMENT);
		toolBar.add(openButton);
		toolBar.addSeparator();

		//Force Laws button
		forceLawsButton = new JButton(new ImageIcon("resources/icons/physics.png"));
		forceLawsButton.addActionListener(this);
		forceLawsButton.setToolTipText("Set a force law");
		toolBar.add(forceLawsButton);
		toolBar.addSeparator();

		//Run button
		runButton = new JButton(new ImageIcon("resources/icons/run.png"));
		runButton.addActionListener(this);
		runButton.setToolTipText("Run the simulator");
		toolBar.add(runButton);

		//Stop button
		stopButton = new JButton(new ImageIcon("resources/icons/stop.png"));
		stopButton.addActionListener(this);
		stopButton.setToolTipText("Stop the simulator");
		toolBar.add(stopButton);
		
		//Reset button
		resetButton = new JButton(new ImageIcon("resources/icons/reset.png"));
		resetButton.addActionListener(this);
		resetButton.setToolTipText("Reset the simulator");
		toolBar.add(resetButton);
		
		//Delete button
		deleteButton = new JButton(new ImageIcon("resources/icons/delete.png"));
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText("Deletes a body");
		toolBar.add(deleteButton);
		
		toolBar.addSeparator();

		//Steps label
		stepsLabel = new JLabel("Steps: ");
		toolBar.add(stepsLabel);

		stepSpinner = new JSpinner();
		stepSpinner.setValue(10000);
		stepSpinner.setToolTipText("Number of steps");
		stepSpinner.setMaximumSize(new Dimension(80, 30));
		stepSpinner.setMinimumSize(new Dimension(80, 30));
		toolBar.add(stepSpinner);

		//Delta-time label
		deltaTimeLabel = new JLabel("Delta-Time: ");
		toolBar.add(deltaTimeLabel);
		
		deltaTimeField = new JTextField("2500.0");
		deltaTimeField.setToolTipText("Real time (seconds) corresponding to a step");
		deltaTimeField.setMaximumSize(new Dimension(80, 30));
		deltaTimeField.setMinimumSize(new Dimension(80, 30));
		toolBar.add(deltaTimeField);
		toolBar.addSeparator();

		toolBar.add(Box.createHorizontalGlue());

		//Exit button
		exitButton = new JButton(new ImageIcon("resources/icons/exit.png"));
		exitButton.addActionListener(this);
		exitButton.setToolTipText("Exit");
		exitButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		toolBar.add(exitButton);
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.add(toolBar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == openButton) {
			
			fc = new JFileChooser();
			fc.setCurrentDirectory(new File("resources/"));
			int returnVal = fc.showOpenDialog(this);
			
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				InputStream in = null;
				
				try {
					in = new FileInputStream(file);
					_ctrl.reset();
					_ctrl.loadBodies(in);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(this, e1.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		} else if (e.getSource() == forceLawsButton) {
			
			try {
				if (forcesLawsDialog == null)
					forcesLawsDialog = new ForceLawsDialog((Frame) SwingUtilities.getWindowAncestor(this), _ctrl);
				int status = forcesLawsDialog.open();

				if (status == 1) {
					_ctrl.setForcesLaws(forcesLawsDialog.getJSON());
				}
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(this, e2.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
			}

		} else if (e.getSource() == runButton) {
			
			_stopped = false;
			setEnabledButtons(false);
			_ctrl.setDeltaTime(Double.parseDouble(deltaTimeField.getText()));
			
			//Notify the ControlPanel observers
			for(ControlPanelObserver o: _obs)
				o.onStartSimulation();
			
			run_sim((Integer) stepSpinner.getValue());

		} else if (e.getSource() == stopButton) {
			_stopped = true;
			
			//Notify the ControlPanel observers
			for(ControlPanelObserver o: _obs)
				o.onEndSimulation();
			
		} else if (e.getSource() == resetButton) {
			_ctrl.reset();
		} else if (e.getSource() == deleteButton) {
			
			if (deletionDialog == null)
				deletionDialog = new BodyDeletionDialog((Frame) SwingUtilities.getWindowAncestor(this), _bodiesId);
			deletionDialog.setBodies(_bodiesId);
			int status = deletionDialog.open();

			if (status != -1) {
				try {
					_ctrl.delBody(_bodies.get(status));
				} catch (Exception e2) {
					JOptionPane.showMessageDialog(this, "The body couldn't be deleted", "ERROR", JOptionPane.ERROR_MESSAGE);
				}
			}
		}else if (e.getSource() == exitButton) {
			System.exit(0);
		}
	}

	private void setEnabledButtons(boolean enable) {
		openButton.setEnabled(enable);
		forceLawsButton.setEnabled(enable);
		runButton.setEnabled(enable);
		resetButton.setEnabled(enable);
		deleteButton.setEnabled(enable);
		stepSpinner.setEnabled(enable);
		deltaTimeField.setEnabled(enable);
		exitButton.setEnabled(enable);
	}
	
	protected void setBodiesId() {	
		//Adds the id of the bodies to the list of ids
		_bodiesId.clear();
		for (Body b : _bodies)
			_bodiesId.add(b.getId());
	}

	private void run_sim(int n) {

		if (n > 0 && !_stopped) {
			try {
				_ctrl.run(1);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Run error", JOptionPane.ERROR_MESSAGE);
				_stopped = true;
				setEnabledButtons(true);
				return;
			}

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					run_sim(n - 1);
				}
			});

		} else {
			_stopped = true;
			setEnabledButtons(true);
			for(ControlPanelObserver o: _obs)
				o.onEndSimulation();
		}
	}

	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_bodies = bodies;
		setBodiesId();
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_bodies = bodies;
		setBodiesId();
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		_bodies = bodies;
		setBodiesId();
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {}

	@Override
	public void onDeltaTimeChanged(double dt) {}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {}

	@Override
	public void onBodyDeleted(List<Body> bodies, Body b) {
		_bodies = bodies;
		setBodiesId();
	}
	
	@Override
	public void addObserver(ControlPanelObserver o) {
		_obs.add(o);
		o.onRegister();	
	}

}
