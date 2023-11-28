import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginAdmin extends JFrame {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel loginPanel;

    public LoginAdmin() {
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                User user = getAuthenticatedUser(email, password);

                if (user != null) {
                    dispose(); // Tutup form login
                    RegistrationForm registrationForm = new RegistrationForm(LoginAdmin.this); // Buka RegistrationForm
                    registrationForm.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(LoginAdmin.this,
                            "Email or Password Invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setLocationRelativeTo(null); // Atur frame di tengah layar
        setVisible(true);
    }

    private User getAuthenticatedUser(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/coffeshopp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Terhubung ke database dengan sukses...

            String sql = "SELECT * FROM admin WHERE email=? AND password=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
                preparedStatement.setString(1, email);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user = new User();
                        user.email = resultSet.getString("email");
                        user.password = resultSet.getString("password");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginAdmin();
        });
    }
}
