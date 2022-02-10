package pl.edu.pw.ii.jee.chat_client;

import java.util.Optional;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application 
{
	public static final String appName = "Sockets-JavaFX-MVC";
    
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			ViewLoader<AnchorPane, ChatController> viewLoader = new ViewLoader<>("ChatClient.fxml");
			viewLoader.getController().setUserName(getUserName());
			viewLoader.getController().setHost("localhost");
			viewLoader.getController().setPort(10005);
			viewLoader.getController().run();
			Scene scene = new Scene(viewLoader.getLayout());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Sockets-JavaFX-MVC");
			primaryStage.setOnHiding( e -> primaryStage_Hiding(e, viewLoader.getController()));
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}		
	}
	
	private String getUserName() {
    	TextInputDialog textInputDialog = new TextInputDialog("Anonymous");
    	textInputDialog.setTitle("");
    	textInputDialog.setHeaderText("");
    	textInputDialog.setContentText("Imie");
    	Optional<String> result = textInputDialog.showAndWait();
    	return result.orElse("Anonymous");
    }
	
	private void primaryStage_Hiding(WindowEvent e, ChatController controller) {
		try {
			controller.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
