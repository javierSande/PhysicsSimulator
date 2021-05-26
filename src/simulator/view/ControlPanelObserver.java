package simulator.view;

public interface ControlPanelObserver {
	public void onRegister();

	public void onStartSimulation();
	
	public void onEndSimulation();
}
