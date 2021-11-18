package payroll;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ProductController implements Initializable {

    private Parent root;

    @FXML
    private Button btnDelete, btnSair, btn_add;

    @FXML
    private TableView<PayrollTable> table;

    @FXML
    private TextField tx_name, tx_id;

    @FXML
    private TableColumn<PayrollTable, String> tc_id, tc_name;

    ObservableList<PayrollTable> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

    }

    public void calculateValues() throws FileNotFoundException, IOException {

        Properties prop = new Properties();
        InputStream is = this.getClass().getResourceAsStream("/LCTech/config.properties" );
        //prop.load(new FileInputStream("./config/config.properties"));
        prop.load(is);
        String company = prop.getProperty("company.name");

    }

    private void DisplayError(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void onTableClick() {
        tx_id.setText(table.getSelectionModel().getSelectedItem().getId());
        tx_name.setText(table.getSelectionModel().getSelectedItem().getName());

        //      calculateValues();
        btnDelete.setDisable(false);
        //btnReport.setDisable(false);
        tx_id.setEditable(false);
    }

    @FXML
    public void onButtonSave(ActionEvent event) throws IOException {

        if (tx_id.getText().equals("")) {
            DisplayError("Erro", "Por favor informe um valor correto.", "O campo código não pode ficar vazio.");
            return;
        }
        if (tx_name.getText().equals("") || tx_name.getText().length() < 2) {
            DisplayError("Erro", "Por favor informe um valor correto.", "O campo PRODUTO não pode ficar vazio ou ser menor que 2 caracteres.");
            return;
        }

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = Payroll.conn.createStatement();

            //String sql = "INSERT INTO PRODUCTS VALUES(" + " null "+ ", '" + tx_name.getText() + "');";
            String sql = "INSERT INTO PRODUCTS VALUES(" + tx_id.getText() + ", '" + tx_name.getText() + "');";

            System.out.println("" + sql);
            stmt.execute(sql);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Payroll.fxml"));
            root = loader.load();

            PayrollController controller = loader.getController();
            controller.Reload();
            Reload();
            Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
            dialogoInfo.setTitle("Cadastro de Produtos");
            dialogoInfo.setHeaderText("Cadastro realizado com sucesso!");
            dialogoInfo.setContentText("O produto " + tx_name.getText() + " foi cadastrado com sucesso!");
            dialogoInfo.showAndWait();

        } catch (SQLException ex) {

            if (ex.getMessage().contains("PRIMARY")) {
                try {
                    stmt = Payroll.conn.createStatement();
                    String sql = "UPDATE PRODUCTS SET DESCRIPTION = '" + tx_name.getText() + "' WHERE ID = '" + tx_id.getText() + "';";
                    System.out.println("erroooo" + sql);
                    stmt.execute(sql);
                    Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                    dialogoInfo.setTitle("Cadastro de Produtos");
                    dialogoInfo.setHeaderText("Alteração realizado com sucesso!");
                    dialogoInfo.setContentText("O produto " + tx_name.getText() + " foi alterado com sucesso!");
                    dialogoInfo.showAndWait();
                    Reload();
                } catch (SQLException ex2) {
                    System.out.println("SQLException: " + ex2.getMessage());
                    System.out.println("SQLState: " + ex2.getSQLState());
                    System.out.println("VendorError: " + ex2.getErrorCode());
                }
            }

            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }

        onButtonClear();
    }

    @FXML
    private void btnSairOnAction(ActionEvent event) {
        Stage stage = (Stage) btnSair.getScene().getWindow();

        stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));

        stage.close();
    }

    @FXML
    public void Reload() {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = Payroll.conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM PRODUCTS");
            data.clear();

            while (rs.next()) {

                String id = rs.getString("id");
                String name = rs.getString("description");

                System.out.println(id + ", " + name);

                PayrollTable pt = new PayrollTable(id, name);
                data.add(pt);
            }
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }

    }

    @FXML
    public void onButtonClear() {
        tx_id.setText("");
        tx_name.setText("");

//        calculateValues();
        btnDelete.setDisable(true);
        tx_id.setEditable(true);
    }

    @FXML
    public void onButtonDelete() {

        Alert alert = new Alert(AlertType.WARNING, "Deseja excluir o item " + tx_name.getText() + " ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Exclusão de Produto ");
        alert.setHeaderText("Atenção!");

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {

            Statement stmt = null;
            ResultSet rs = null;

            try {
                stmt = Payroll.conn.createStatement();
                stmt.execute("DELETE FROM PRODUCTS WHERE id = " + tx_id.getText());
            } catch (SQLException ex) {
                // ignore
            } finally {

                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlEx) {
                    } // ignore

                    rs = null;
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException sqlEx) {
                    } // ignore

                    stmt = null;
                }
            }
        }

        onButtonClear();
        Reload();
        btnDelete.setDisable(true);
        tx_id.setEditable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            calculateValues();
            tc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
            table.setItems(data);
            Reload();
        } catch (IOException ex) {
            Logger.getLogger(ProductController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
