import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuAwal extends JFrame {
    private JButton btnAdmin;
    private JButton btnKasir;

    public MenuAwal() {
        setMinimumSize(new Dimension(450, 474));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setTitle("Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new java.awt.FlowLayout());

        btnAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAdminLoginForm();
            }
        });

        btnKasir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openKasirLoginForm();
            }
        });

        add(btnAdmin);
        add(btnKasir);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void openAdminLoginForm() {
        SwingUtilities.invokeLater(() -> {
            LoginAdmin loginAdmin = new LoginAdmin();
            loginAdmin.setVisible(true);
            dispose();
        });
    }

    private void openKasirLoginForm() {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm(MenuAwal.this);
            loginForm.setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuAwal();
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
