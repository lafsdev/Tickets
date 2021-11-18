package payroll;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PayrollController implements Initializable {

    @FXML
    private Button btnDelete, btnReport;

    @FXML
    private TextField tx_name, tx_id, searchBox;
    @FXML
    private TableView<PayrollTable> table;

    @FXML
    private Spinner sp_qtd;

    @FXML
    private TableColumn<PayrollTable, String> tc_id, tc_name;

    ObservableList<PayrollTable> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initSpinner();
    }

    private void DisplayError(String title, String header, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void calculateValues() {

        double gross, over_time;

    }

    private boolean searchFindsOrder(PayrollTable prtable, String searchText) {
        return (prtable.getName().toLowerCase().contains(searchText.toLowerCase()))
                || Integer.valueOf(prtable.getId()).toString().equals(searchText.toLowerCase());
    }

    private ObservableList<PayrollTable> filterList(List<PayrollTable> list, String searchText) {
        List<PayrollTable> filteredList = new ArrayList<>();
        for (PayrollTable prtable : list) {
            if (searchFindsOrder(prtable, searchText)) {
                filteredList.add(prtable);
            }
        }
        return FXCollections.observableList(filteredList);
    }

    @FXML
    public void onButtonSave(ActionEvent event) {

        if (tx_id.getText().equals("")) {
            DisplayError("Erro", "Por favor informe um valor correto.", "O campo não pode ficar vazio.");
            return;
        }

        if (tx_name.getText().equals("") || tx_name.getText().length() < 3) {
            DisplayError("Erro", "Por favor informe um valor correto.", "O campo não pode ficar vazio ou ser menor que 3 caracteres.");
            return;
        }

        calculateValues();

        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = Payroll.conn.createStatement();

            String sql = "INSERT INTO `payroll` VALUES('" + tx_id.getText() + "', '" + tx_name.getText() + "', '" + tx_id.getText() + "', '" + tx_id.getText() + "');";

            System.out.println("" + sql);
            stmt.execute(sql);
            Reload();
        } catch (SQLException ex) {

            if (ex.getMessage().contains("Duplicate")) {
                try {
                    stmt = Payroll.conn.createStatement();
                    String sql = "UPDATE `payroll` SET `name` = '" + tx_name.getText() + "' WHERE `id` = '" + tx_id.getText() + "';";
                    stmt.execute(sql);
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
                }
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }

                stmt = null;
            }
        }
        onButtonClear();
    }

    @FXML
    public void Reload() {

        Statement stmt = null;
        ResultSet rs = null;
        System.out.println("Reload");
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

            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }

                stmt = null;
            }
            table.refresh();
        }
    }

    public void onSearchBoxClick() {
        searchBox.selectAll();
        searchBox.setText("");
        searchBox.clear();

    }

    public void onTableExit() {
        //   searchBox.selectAll();
        //   table.setSelectionModel(table.getSelectionModel());

    }

    public void onTableClick() {
        //   tx_id.setText(table.getSelectionModel().getSelectedItem().getId());
        tx_name.setText(table.getSelectionModel().getSelectedItem().getName());

        calculateValues();
        //searchBox.clear();
        //   btnDelete.setDisable(false);
        btnReport.setDisable(false);
        //   tx_id.setEditable(false);
        tx_name.setEditable(false);
    }

    @FXML
    public void onButtonClear() {
        tx_id.setText("");
        tx_name.setText("");

        calculateValues();

        btnDelete.setDisable(true);
        btnReport.setDisable(true);
        tx_id.setEditable(true);
    }

    @FXML
    public void onButtonClose() {

        Platform.exit();
    }

    @FXML
    public void onButtonDelete() {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = Payroll.conn.createStatement();
            stmt.execute("DELETE FROM payroll WHERE id = " + tx_id.getText());
        } catch (SQLException ex) {

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                }
                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                }

                stmt = null;
            }
        }

        onButtonClear();
        Reload();
        btnDelete.setDisable(true);
        tx_id.setEditable(true);
    }

    @FXML
    public void onNewProduct() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Product.fxml"));

            ProductController controller = new ProductController();

            fxmlLoader.setController(controller);
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Cadastro de Produtos");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            //stage.initStyle(StageStyle.TRANSPARENT);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    searchBox.setText("");
                    searchBox.clear();
                    Reload();
                    table.setItems(data);

                }
            });
            stage.show();

            System.out.println("TRYING!");
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    @FXML
    public void onButtonReport() {

        String id = table.getSelectionModel().getSelectedItem().getId();
        String name = table.getSelectionModel().getSelectedItem().getName();
        String qtd = sp_qtd.getValue().toString();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("Report.fxml"));

            ReportController controller = new ReportController(id, name, qtd);

            fxmlLoader.setController(controller);
            Scene scene = new Scene(fxmlLoader.load(), 300, 400);
            Stage stage = new Stage();
            stage.setTitle("Tickets Report");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
            System.out.println("TRYING!");
        } catch (IOException e) {
            // ignore
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tc_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        tc_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        table.setItems(data);

        tx_name.setEditable(false);

        searchBox.textProperty().addListener((observable, oldValue, newValue)
                -> table.setItems(filterList(data, newValue))
        );
        Reload();

    }

    private void initSpinner() {
        sp_qtd.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10)
        );
    }

}
