package payroll;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import static payroll.PayrollController.round;



public class PayrollTable {

        public final SimpleStringProperty tc_name, tc_id;

        PayrollTable(String id, String name){
            this.tc_id = new SimpleStringProperty(id);
            this.tc_name = new SimpleStringProperty(name);
        }
        
        public final String getId() { return tc_id.getValue(); }
        public final String getName() { return tc_name.getValue(); }

        
        public final StringProperty idProperty() { return tc_id; }
        public final StringProperty nameProperty() { return tc_name; }

} 