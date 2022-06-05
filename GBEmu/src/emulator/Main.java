package emulator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("GBEmu");
		Pane root = new VBox();
		Scene scene = new Scene(root);
		stage.setScene(scene);

		Menu fileMenu = new Menu("File");
		MenuItem open = new MenuItem("Open...");
		open.setOnAction((event) -> {
			FileChooser chooser = new FileChooser();
			// TODO set initial directory?
			chooser.getExtensionFilters().add(new ExtensionFilter("GameBoy ROMs", "*.gb"));
			File file = chooser.showOpenDialog(stage);
			if (file != null) {
				loadROM(file.toPath());
			}
		});
		fileMenu.getItems().add(open);

		MenuBar menuBar = new MenuBar(fileMenu);
		root.getChildren().add(menuBar);

		// TODO make constants for screen dimensions
		Pane display = new VBox();
		display.setPrefWidth(160);
		display.setPrefHeight(144);
		root.getChildren().add(display);

		stage.show();

		// Load ROM entered at command line
		List<String> args = getParameters().getRaw();
		if (!args.isEmpty()) {
			Path path = Paths.get(args.get(0));
			if (Files.notExists(path)) {
				throw new IllegalArgumentException("Invalid path to file: " + path.toString());
			}

			loadROM(path);
		}
	}

	private void loadROM(Path romPath) {
		// TODO check file extension, throw exception if invalid

		try {
			byte[] romData = Files.readAllBytes(romPath);
			Cartridge cartridge = new Cartridge(romData);
		} catch (IOException e) {
			System.err.println("Unable to load ROM.");
			// TODO implement logging
			e.printStackTrace();
		}
	}

}
