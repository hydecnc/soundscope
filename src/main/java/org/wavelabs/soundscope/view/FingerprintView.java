package org.wavelabs.soundscope.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintController;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintState;
import org.wavelabs.soundscope.interface_adapter.fingerprint.FingerprintViewModel;

public class FingerprintView extends JButton implements ActionListener, PropertyChangeListener {
    private FingerprintController fingerprintController;

    public FingerprintView(FingerprintViewModel fingerprintViewModel) {
        this.setText("Fingerprint");
        this.addActionListener(this);
        fingerprintViewModel.addPropertyChangeListener(this);
    }

    public void setFingerprintController(FingerprintController fingerprintController) {
        this.fingerprintController = fingerprintController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (fingerprintController != null) {
            fingerprintController.execute();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        final FingerprintState state = (FingerprintState) evt.getNewValue();
        // Display popup with the message from state
        JOptionPane.showMessageDialog(
            this,
            state.getFingerprint(),
            "Fingerprint Result",
            // state.isSuccess() ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}
