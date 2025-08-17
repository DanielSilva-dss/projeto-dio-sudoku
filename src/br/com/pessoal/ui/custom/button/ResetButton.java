package br.com.pessoal.ui.custom.button;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ResetButton extends JButton {

    public ResetButton(final ActionListener actionListener) {
        this.setText("Reniciar o Jogo");
        this.addActionListener(actionListener);
    }

}
