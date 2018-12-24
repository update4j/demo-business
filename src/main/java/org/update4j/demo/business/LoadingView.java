package org.update4j.demo.business;

import org.update4j.demo.bootstrap.FXMLView;

import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class LoadingView extends FXMLView {

	private FadeTransition fade;

	public LoadingView() {
		fade = new FadeTransition(Duration.millis(200), this);
		fade.setFromValue(0);
		fade.setToValue(1);

		setOpacity(0);
	}

	public void darken() {
		fade.playFromStart();
	}

	public void lighten() {
		fade.setRate(-1);
		fade.playFromStart();
	}

}
