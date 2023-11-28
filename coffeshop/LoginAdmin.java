import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginAdmin extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel loginPanel;

    public LoginAdmin(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                User user = getAuthenticatedUser(email, password);

                if (user != null) {
                    dispose(); // Close the login form
                    NewJFrame newJFrame = new NewJFrame(); // Open the NewJFrame form
                    newJFrame.setVisible(true);
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

        setVisible(true);
    }

    public User user;

    private User getAuthenticatedUser(String email, String password) {
        User user = null;

        final String DB_URL = "jdbc:mysql://localhost/coffeshopp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            // Connected to the database successfully...

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
        LoginAdmin loginAdmin = new LoginAdmin(null);
        User user = loginAdmin.user;
        if (user != null) {
            System.out.println("Successful Authentication of: " + user.name);
            System.out.println("          Email: " + user.email);
        } else {
            System.out.println("Authentication canceled");
        }
    }
}
