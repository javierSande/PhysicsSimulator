package simulator.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.misc.Vector2D;
import simulator.model.Body;
import simulator.model.SimulatorObserver;

public class Viewer extends JComponent implements SimulatorObserver, ControlPanelObserver {

	private static final long serialVersionUID = 1L;

	static final int RADIUS = 5;
	
	private int _centerX;
	private int _centerY;
	private double _scale;
	
	private List<Body> _bodies;
	private int _counter;

	private Controller _ctrl;
	
	private boolean _showHelp;
	private boolean _showVectors;
	private Body _dgBody;
	
	private Map<String, Color> _colors;
	
	private boolean _enabled;

	Viewer(Controller ctrl) {
		initGUI();
		ctrl.addObserver(this);
		_ctrl = ctrl;
		
		_enabled = true;

		_colors = new HashMap<String, Color>();
		_counter = 0;
	}

	private void initGUI() {
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black, 2), "Viewer",
				TitledBorder.LEFT, TitledBorder.TOP));
		setPreferredSize(new Dimension(500, 500));
		
		_bodies = new ArrayList<>();
		_scale = 1.0;
		_showHelp = true;
		_showVectors = true;
		
		addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case '-':
					_scale = _scale * 1.1;
					repaint();
					break;
				case '+':
					_scale = Math.max(1000.0, _scale / 1.1);
					repaint();
					break;
				case '=':
					autoScale();
					repaint();
					break;
				case 'h':
					_showHelp = !_showHelp;
					repaint();
					break;
				case 'v':
					_showVectors = !_showVectors;
					repaint();
					break;
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		});

		addMouseListener(new MouseListener() {
			@Override
			public void mouseEntered(MouseEvent e) {
				requestFocus();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				
				if (e.getClickCount() > 1 && _enabled) {
					
					Body b = getSelectedBody(e.getX(), e.getY());
					
					if (b == null) {
						//Creates a body
						_centerX = getWidth() / 2;
						_centerY = getHeight() / 2;
						
						double x = (e.getX() - _centerX) * _scale;
						double y = (-e.getY() + _centerY) * _scale;
						
						createBody(x, y);
						repaint();
						
					} else {
						//Opens the colour chooser for the selected body
						Color c = JColorChooser.showDialog(null,"Body Color",Color.BLUE); 
						if ( c!=null) {
					    	_colors.replace(b.getId(), c);
					    	repaint();
					    }
					} 
				}
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (_enabled)
					_dgBody = getSelectedBody(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				_dgBody = null;
			}

			@Override
			public void mouseExited(MouseEvent e) {}
		});
		
		addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (_dgBody != null && _enabled) {
					double x = (e.getX() - _centerX) * _scale;
					double y = (-e.getY() + _centerY) * _scale;
					_dgBody.setPosition(x, y);
					repaint();
				}
			}
		});

	}
	
	protected Body getSelectedBody(double x, double y) {
		for (Body b : _bodies) {
			double bx = b.getX() / _scale  + _centerX;
			double by = -b.getY() / _scale  + _centerY;
			
			if (Math.sqrt(Math.pow(x - bx, 2) + Math.pow(y - by, 2)) <= RADIUS) {
				return b;
			}
		}
		return null;
	}
	
	protected JSONObject createBody(double x, double y) {
		JSONObject body = new JSONObject();		
		
		JSONObject data = new JSONObject();
		String id = "mb" + _counter;
		_counter++;
		
		data.put("id", id);
		data.put("m", 3.0e28);
		data.put("p", new Vector2D(x, y).asJSONArray());
		data.put("v", new Vector2D().asJSONArray());
		
		body.put("type", "basic");
		body.put("data", data);
		

		_ctrl.addBody(body);
		
		return body;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// use ’gr’ to draw not ’g’ --- it gives nicer results
		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		// calculate the centre
		_centerX = getWidth() / 2;
		_centerY = getHeight() / 2;

		gr.setColor(Color.black);
		gr.drawLine(_centerX - 3, _centerY, _centerX + 3, _centerY);
		gr.drawLine(_centerX, _centerY - 3, _centerX, _centerY + 3);

		for (Body b : _bodies) {
			
			//Calculates the unit vectors
			Vector2D p = b.getPosition().scale(1 / _scale);
			Vector2D f = p.plus(b.getForce().direction().scale(20));
			Vector2D v = p.plus(b.getVelocity().direction().scale(20));
			
			int fx, fy, px, py, vx, vy;

			if (_showVectors) {
				fx = _centerX + (int) (f.getX());
				fy = _centerY - (int) (f.getY());
				
				vx = _centerX + (int) (v.getX());
				vy = _centerY - (int) (v.getY());
				
				px = _centerX + (int) p.getX();
				py = _centerY - (int) p.getY();
				
				//Draws the vectors
				drawLineWithArrow(gr, px, py, fx, fy, RADIUS, RADIUS, Color.green, Color.green);
				drawLineWithArrow(gr, px, py, vx, vy, RADIUS, RADIUS, Color.red, Color.red);
			}

			//Draws the body
			gr.setColor(_colors.get(b.getId()));
			gr.fillOval(_centerX + (int) p.getX() - RADIUS, _centerY - (int) p.getY() - RADIUS, 10, 10);

			//Draws the body Id
			gr.setColor(Color.black);
			gr.drawString(b.getId(), _centerX + (int) p.getX() - RADIUS, _centerY - (int) p.getY() - 8);
		}

		//Draws the help message if needed
		if (_showHelp) {
			gr.setColor(Color.red);
			gr.drawString("h: toggle help, v: toggle vectors, +: zoom-in, -: zoom-out:, =: fit", 10, 30);
			gr.drawString(String.format("Scaling ratio: %f", _scale), 10, 45);
		}
	}

	// other private/protected methods
	private void autoScale() {
		double max = 1.0;
		for (Body b : _bodies) {
			Vector2D p = b.getPosition();
			max = Math.max(max, Math.abs(p.getX()));
			max = Math.max(max, Math.abs(p.getY()));
		}
		double size = Math.max(1.0, Math.min(getWidth(), getHeight()));
		_scale = max > size ? 4.0 * max / size : 1.0;
	}

	// This method draws a line from (x1,y1) to (x2,y2) with an arrow.
	// The arrow is of height h and width w.
	// The last two arguments are the colors of the arrow and the line
	private void drawLineWithArrow(//
			Graphics g, //
			int x1, int y1, //
			int x2, int y2, //
			int w, int h, //
			Color lineColor, Color arrowColor) {
		int dx = x2 - x1, dy = y2 - y1;
		double D = Math.sqrt(dx * dx + dy * dy);
		double xm = D - w, xn = xm, ym = h, yn = -h, x;
		double sin = dy / D, cos = dx / D;
		x = xm * cos - ym * sin + x1;
		ym = xm * sin + ym * cos + y1;
		xm = x;
		x = xn * cos - yn * sin + x1;
		yn = xn * sin + yn * cos + y1;
		xn = x;
		int[] xpoints = { x2, (int) xm, (int) xn };
		int[] ypoints = { y2, (int) ym, (int) yn };
		g.setColor(lineColor);
		g.drawLine(x1, y1, x2, y2);
		g.setColor(arrowColor);
		g.fillPolygon(xpoints, ypoints, 3);
	}
	
	// SimulatorObserver methods
	@Override
	public void onRegister(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_bodies = bodies;
		
		//Assigns blue colour to every body by default
		for (Body b: bodies)
			_colors.put(b.getId(), Color.blue);
		
		autoScale();
		repaint();
	}

	@Override
	public void onReset(List<Body> bodies, double time, double dt, String fLawsDesc) {
		_bodies = bodies;
		_colors.clear();
		_counter = 0;
		
		//Assigns blue colour to every body by default
		for (Body b: bodies)
			_colors.put(b.getId(), Color.blue);
				
		autoScale();
		repaint();
	}

	@Override
	public void onBodyAdded(List<Body> bodies, Body b) {
		_bodies = bodies;
		_colors.put(b.getId(), Color.blue);
		autoScale();
		repaint();
	}

	@Override
	public void onAdvance(List<Body> bodies, double time) {
		repaint();
	}

	@Override
	public void onDeltaTimeChanged(double dt) {}

	@Override
	public void onForceLawsChanged(String fLawsDesc) {}

	public void onBodyDeleted(List<Body> bodies, Body b) {
		_bodies = bodies;
		_colors.remove(b.getId());
		autoScale();
		repaint();
	}
	
	@Override
	public void onRegister() {}

	@Override
	public void onStartSimulation() {
		_enabled = false;
	}
	
	@Override
	public void onEndSimulation() {
		_enabled = true;
	}
}
