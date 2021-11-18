/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LCTech;

import javafx.scene.control.TextField;

/**
 *
 * @author Leandro
 */
public class LCTechNumberTextField extends TextField {

    public LCTechNumberTextField() {
        this.setPromptText("Nº Impressões");
    }

    @Override
    public void replaceText(int start, int end, String text) {

        if (text.matches("[0-9]") || text.isEmpty()) {
            super.replaceText(start, end, text); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void replaceSelection(String replacement) {
        super.replaceSelection(replacement); //To change body of generated methods, choose Tools | Templates.
    }

}
