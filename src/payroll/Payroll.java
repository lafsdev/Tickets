package payroll;

import java.io.FileInputStream;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;

public class Payroll extends Application {

    static Connection conn = null;

    @Override
    public void start(Stage stage) throws Exception {

        Properties prop = new Properties();
        InputStream is = this.getClass().getResourceAsStream("/LCTech/config.properties");
        // prop.load(new FileInputStream("./config/config.properties"));
        prop.load(is);
        String url = prop.getProperty("database.url");
        String username = prop.getProperty("database.username");
        String password = prop.getProperty("database.password");
        String company = prop.getProperty("company.name");
        String driver = prop.getProperty("database.driver");
        String expirationDate = prop.getProperty("expiration.date");
        SimpleDateFormat lctechFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();
        Date d1 = lctechFormater.parse(expirationDate);
        System.out.println(today + "Todayyyyy");
        System.out.println(d1 + "Dllllllllll");
        if (d1.compareTo(today) > 0 || d1.compareTo(today) == 0) {
            try {
                Class.forName(driver);

                try {
                    conn = DriverManager.getConnection(url, username, password);
                } catch (SQLException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Banco de Dados");
                    alert.setHeaderText("Erro de Conexão");
                    alert.setContentText("Não foi possível conectar com o Banco de Dados!");
                    alert.showAndWait();
                    throw new IllegalStateException("Não foi possível conectar com o Banco de Dados!", e);
                }

            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Não foi possível encontrar o driver de conexão!", e);
            }

            Parent root = FXMLLoader.load(getClass().getResource("Payroll.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add("./LCTech/lctech.css");

            stage.setScene(scene);
            stage.setTitle("Tickets - " + company);
            stage.setResizable(false);
            stage.show();

        } else if (d1.compareTo(today) < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Programa Expirado");
            alert.setHeaderText("Essa licença de uso foi bloqueada em " + lctechFormater.format(d1));
            alert.setContentText("Entre em contato com a LCTech para adquirir uma nova licença.");
            alert.showAndWait();
        } 
    }

    public static void main(String[] args) {
        launch(args);
    }
}
