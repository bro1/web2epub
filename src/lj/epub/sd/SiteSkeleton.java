package lj.epub.sd;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;


public abstract class SiteSkeleton implements Runnable {

	protected JProgressBar progressBar;
	protected JButton[] buttons;

	public SiteSkeleton() {
		super();
	}
	
	class ProgressUpdaterThread implements Runnable {

		private int progressValue;

		public ProgressUpdaterThread(int p) {
			this.progressValue = p;
		}

		@Override
		public void run() {

			if (progressBar != null) {
				progressBar.setValue(progressValue);
			}

		}

	}

	class EnableButtonsThread implements Runnable {

		@Override
		public void run() {

			if (buttons != null) {
				for (JButton button : buttons) {
					button.setEnabled(true);
				}
			}

		}

	}


	public void setProgress(int p) {
	
		SwingUtilities.invokeLater(new ProgressUpdaterThread(p));
	
	}

	protected void setEnable() {
	
		SwingUtilities.invokeLater(new EnableButtonsThread());
	}
	
	abstract protected  void doWork() throws Exception; 

	@Override
	public void run() {
	
		try {
			doWork();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
	}

	public void setProgressBar(JProgressBar jProgressBar) {
		progressBar = jProgressBar;
	}

	public void setButtons(JButton[] jButtons) {
		buttons = jButtons;
	}

}