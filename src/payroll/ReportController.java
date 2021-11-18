package payroll;

import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class ReportController implements Initializable {

    /**
     * Initializes the controller class.
     */
    private String id, name, qtd;

    @FXML
    private AnchorPane root;

    @FXML
    private Button btnPrint;

    @FXML
    private Label tx_name, tx_id, tx_qtd, tx_date, tx_title, tx_nightcode;

    @FXML
    private Spinner sp_qtd;

    public ReportController(String id, String name, String qtd) {
        this.id = id;
        this.name = name;
        this.qtd = qtd;

    }

    public void calculateValues() throws FileNotFoundException, IOException, NoSuchAlgorithmException {

        Properties prop = new Properties();
        InputStream is = this.getClass().getResourceAsStream("/LCTech/config.properties" );
        prop.load(is);
        //prop.load(new FileInputStream("./config/config.properties"));
        String company = prop.getProperty("company.name");

        tx_id.setText(id);
        tx_name.setText(name);
        tx_qtd.setText(qtd);
        tx_title.setText(company);

        Date today = new Date();
        DateFormat lctechFormater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        tx_date.setText(lctechFormater.format(today));
        String nightCode;
        nightCode = lctechFormater.format(today);
        System.out.println(nightCode);
        System.out.println(nightCode.substring(0, 5));
        MessageDigest m=MessageDigest.getInstance("MD5");
        m.update(nightCode.substring(0, 5).getBytes(),0,nightCode.substring(0, 5).length());
        System.out.println("MD5: "+new BigInteger(1,m.digest()).toString(16));
        tx_nightcode.setText(new BigInteger(1,m.digest()).toString(16).substring(0, 5).toUpperCase());

    }

    @FXML
    private void onButtonPrint() {

        btnPrint.setVisible(false);
        //     int qtd = Integer.parseInt(tx_qtd.getText());
        for (int i = 0; i < Integer.parseInt(qtd); i++) {
            System.out.println("Impressão " + i);
            print(root);
        }
        //print(root);
    }

      private void print(Node node) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(node.getScene().getWindow())){
            boolean success = job.printPage(node);
            if (success) {
                job.endJob();
            }
        }
        btnPrint.setVisible(true);
        Stage stage = (Stage) btnPrint.getScene().getWindow();
        stage.close();
    }
    //private void print(Node node) {
       // PrinterJob job = PrinterJob.createPrinterJob();

        //Paper paper = PrintHelper.createPaper("Roll80", 80, 270, Units.MM);
       // PageLayout pageLayout = job.getPrinter().createPageLayout(paper, PageOrientation.PORTRAIT, 0, 0, 0, 0);
       // double scale = 0.791;
       // node.getTransforms().add(new Scale(scale, scale));
        /*
        if (job != null && job.showPrintDialog(node.getScene().getWindow())){
            boolean success = job.printPage(node);            
            if (success) {
                job.endJob();
            } 
        }*/
       // if (job != null && job.showPrintDialog(node.getScene().getWindow())){
       //     boolean success = job.printPage(pageLayout, node);
      //      if (success) {
        //        job.endJob();

    //        }
        //}
      //  btnPrint.setVisible(true);
    //    Stage stage = (Stage) btnPrint.getScene().getWindow();

  //      stage.close();

//    } 

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {
            calculateValues();
        } catch (IOException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
