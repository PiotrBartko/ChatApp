package pl.edu.pw.ii.jee.chat_client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.web.WebView;

public class ChatController implements AutoCloseable{

	@Override
	public void close() throws Exception {
//		socket.close();		
	}

	private String host;
	private int port;
	private Socket socket;
	private BufferedReader inputBufferedReader;
	private PrintWriter outputPrintWriter;
	private final int PROTOCOL_PREFIX_LENGTH = 3;
	private Document messagesLayout;
	private String userName = "";
	private String senderName;
	Task<Void> task;
	
	@FXML TextField messageTextField;
	@FXML Label welcomeLabel;
	@FXML WebView webViewMessages;
	@FXML Circle circleImage;
	@FXML ImageView sendImageview;
	
	@FXML public void initialize() {
		String welcome = "Nice to see you there! This is a welcome message." + "Say hello to other users.";
		messagesLayout = Jsoup.parse(
				"<html><head><meta charset='UTF-8'>"+
				"</head><body><ul><li class=\"welcome\"><div class=\"message\"><div class=\"content\">"+
				welcome +
				"</div></div></li></ul></body></html>",
				"UTF-16", Parser.xmlParser());
		webViewMessages.getEngine().loadContent(messagesLayout.html());
		webViewMessages.getEngine().setUserStyleSheetLocation(getClass().getResource("application.css").toString());
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		welcomeLabel.setText("Hello "+this.userName+"!");
		
		String path;
		try {
			path = this.getClass().getResource("mikeross.png").toURI().toString();
			Image myImage = new Image(path);
			ImagePattern pattern = new ImagePattern(myImage);
			circleImage.setFill(pattern);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		
		try {
			socket = new Socket(host, port);
			inputBufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputPrintWriter = new PrintWriter(socket.getOutputStream(), true);
			sendMessage(userName);
			task = new Task<Void>() {
				@Override
				protected Void call() {
				try {
					while (true) {
						if (isCancelled()) {
							return null;
						}
						String msg = receiveMessage();
						String decodedMsg = decodeUID(msg);
						showMessage(toHTML(decodedMsg, "response"));
						System.out.println(msg);
						Thread.sleep(100);
					}
				} catch (IOException | InterruptedException ex) {
					if (isCancelled()) {
						return null;
					}
				}
				return null;
				}
			};
			new Thread(task).start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String decodeUID(String msg) {
		msg = msg.substring(PROTOCOL_PREFIX_LENGTH);
		char sep = (char) 31;
		String[] param = msg.split(String.valueOf(sep));
		senderName = param[0];
		return msg.substring(senderName.length());
	}
	
	public void sendMessage(String message) {
		outputPrintWriter.println(message);
	}
	
	private String receiveMessage() throws IOException {
		return inputBufferedReader.readLine();
	}
	
	@FXML
	private void sendImageView_MouseReleased() {
//		if (messageTextField.getLength() == 0) return;
//		sendMessage(messageTextField.getText());
//		showMessage(toHTML(messageTextField.getText(), "request"));
//		messageTextField.clear();
	}
	@FXML
	private void messageTextField_KeyPressed(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
		sendImageView_MouseReleased() ;
		sendMessage(messageTextField.getText());
		showMessage(toHTML(messageTextField.getText(), "request"));
		messageTextField.clear();
		}
	}
	
	@FXML
	private void messageButonOnAction(ActionEvent e) {
		if (messageTextField.getLength() == 0) return;
		sendMessage(messageTextField.getText());
		showMessage(toHTML(messageTextField.getText(), "request"));
		messageTextField.clear();
	}
	
	private void showMessage(Element message) {
		Element wrapper = messagesLayout.getElementsByTag("ul").first();
		wrapper.appendChild(message);
		Platform.runLater(new Runnable() {
			public void run() {
				webViewMessages.getEngine().loadContent(messagesLayout.html());
			}
		});
	}
	
	private Element toHTML(String message, String msgClass) {
		System.out.println("toHTML:" + message);
		
		Element wrapper = new Element("li").attr("class", msgClass);
		
		Element image = new Element("img").attr("class", "avatar").attr("src", getClass().getResource("harveyspecter.png").toString());
	
		if (senderName == null) {
			senderName= this.userName;
		}
		if (msgClass.equals("request")) {
			image.attr("src", getClass().getResource("mikeross.png").toString());
			new Element("span").attr("class", "author").append(senderName).appendTo(wrapper);
		}
		if (msgClass.equals("response")) {
			image.attr("src", getClass().getResource("harveyspecter.png").toString());
			new Element("span").attr("class", "author").append(senderName).appendTo(wrapper);
		}
		image.appendTo(wrapper);
		Element message_div = new Element("div").attr("class", "message").appendTo(wrapper);
		new Element("div").attr("class", "content").append(message).appendTo(message_div);
		return wrapper;
	}

}
