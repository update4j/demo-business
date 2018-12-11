package org.update4j.demo.business;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.update4j.LaunchContext;
import org.update4j.SingleInstanceManager;
import org.update4j.demo.bootstrap.JavaFxDelegate;
import org.update4j.service.Launcher;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class JavaFxLauncher implements Launcher {

	private static final Path LOCK_DIR = Paths.get(System.getProperty("user.home"), ".update4j-demo");
	private static Stage primaryStage;

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	@Override
	public long version() {
		return 0;
	}

	@Override
	public void run(LaunchContext ctx) {
		if ("true".equals(ctx.getArgs().get(0))) {
			try {
				Files.createDirectories(LOCK_DIR);
			} catch (IOException e) {
				e.printStackTrace();
			}

			SingleInstanceManager.execute(ctx.getArgs(), args -> {
				ButtonType dismiss = new ButtonType("Dismiss", ButtonData.OK_DONE);
				Platform.runLater(() -> showDialog("Launcher Message", args.get(1), dismiss));
			}, LOCK_DIR);

			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					Thread.sleep(5000);
					if (isEmptyDirectory(LOCK_DIR)) {
						Files.deleteIfExists(LOCK_DIR);
					}
				} catch (Exception e) {
				}
			}));
		}

		Platform.runLater(() -> {
			// FXML loader should find newly added classes on the dynamic classpath.
			Thread.currentThread().setContextClassLoader(ctx.getClassLoader());
			
			boolean usePrimary = !"true".equals(ctx.getArgs().get(2));

			primaryStage = usePrimary ? JavaFxDelegate.getPrimaryStage() : new Stage();
			primaryStage.setTitle("Update4j Demo Business");
			primaryStage.setMinWidth(650);
			primaryStage.setMinHeight(500);

			LibraryView libraries = new LibraryView();

			if (!usePrimary) {
				Scene scene = new Scene(libraries);
				scene.getStylesheets().setAll(JavaFxDelegate.getPrimaryStage().getScene().getStylesheets());

				primaryStage.getIcons().setAll(JavaFxDelegate.getPrimaryStage().getIcons());
				primaryStage.setScene(scene);
				primaryStage.showAndWait();
			} else {
				JavaFxDelegate.getViewStack().push(libraries);
			}
		});

	}

	private static boolean isEmptyDirectory(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> dir = Files.newDirectoryStream(path)) {
				return !dir.iterator().hasNext();
			}
		}

		return false;
	}
	
	public static ButtonType showDialog(String header, String message, ButtonType... buttonTypes) {
		JFXDialogLayout layout = new JFXDialogLayout();

		Label messageLabel = new Label(message);
		layout.setBody(messageLabel);

		Label caption = new Label(header);
		layout.setHeading(caption);

		List<JFXButton> buttons = new ArrayList<>();

		JFXDialog alert = new JFXDialog();

		ButtonType[] pressed = new ButtonType[1];
		for (ButtonType type : buttonTypes) {
			JFXButton b = new JFXButton(type.getText()); //FIXME: toUpperCase()
			b.setDefaultButton(type.getButtonData().isDefaultButton());
			b.setCancelButton(type.getButtonData().isCancelButton());

			b.setOnAction(evt -> {
				alert.close();
				pressed[0] = type;
			});

			buttons.add(b);
		}
		layout.setActions(buttons);

		alert.setContent(layout);
		alert.show((StackPane) getPrimaryStage().getScene().getRoot());

		JavaFxLauncher.getPrimaryStage().getScene().getRoot().requestFocus();

		return pressed[0];
	}
}
