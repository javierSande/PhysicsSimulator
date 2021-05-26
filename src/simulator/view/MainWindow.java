package simulator.view;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import simulator.control.Controller;

@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private Controller _ctrl;

	public MainWindow(Controller ctrl) {
		super("Physics Simulator");
		_ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		this.add(mainPanel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setSize(getPreferredSize());
	}
	
	// other private/protected methods
	private void setContentPane(JPanel mainPanel) {
		
		//Controller
		ControlPanel ctrlPanel = new ControlPanel(_ctrl);
		mainPanel.add(ctrlPanel, BorderLayout.PAGE_START);

		//Panel with bodies table and viwer
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		//Bodies table
		BodiesTable table = new BodiesTable(_ctrl);
		table.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(table);

		//Viewer
		Viewer viewer = new Viewer(_ctrl);
		viewer.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(new JScrollPane(viewer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		ctrlPanel.addObserver(viewer);

		mainPanel.add(panel, BorderLayout.CENTER);
		
		//Status bar
		mainPanel.add(new StatusBar(_ctrl), BorderLayout.PAGE_END);

	}
}
