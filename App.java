import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);

        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setTitle("Simple Editor");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

class AppController {

    @FXML
    private TextArea codeTextArea;

    @FXML
    private Label statusBar;

    @FXML
    private ListView<String> directoryListView;

    @FXML
    private StackPane directoryPane;

    @FXML
    private TextArea terminalTextArea;

    @FXML
    private TextField terminalInputField;

    @FXML
    private StackPane terminalPane;

    @FXML
    private void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(directoryPane.getScene().getWindow());

        directoryListView.getItems().clear();
        if (selectedDirectory != null) {
            File[] directories = selectedDirectory.listFiles(File::isDirectory);
            if (directories != null) {
                for (File directory : directories) {
                    directoryListView.getItems().add(directory.getName());
                }
                statusBar.setText("Directories loaded from: " + selectedDirectory.getAbsolutePath());
            } else {
                statusBar.setText("No subdirectories found in the selected directory.");
            }
        } else {
            statusBar.setText("No directory selected.");
        }
    }

    @FXML
    private void executeCommand() {
        String command = terminalInputField.getText();
        executeCommand(command, terminalTextArea);
    }

    private void executeCommand(String command, TextArea terminalTextArea) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                terminalTextArea.appendText(line + "\n");
            }

            int exitCode = process.waitFor();
            terminalTextArea.appendText("Exit Code: " + exitCode + "\n");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            terminalTextArea.appendText("Error executing the command.\n");
        }
    }
}
