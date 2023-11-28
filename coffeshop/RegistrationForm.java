import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

public class RegistrationForm extends JDialog {
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JTextField tfAddress;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JPanel registerPanel;
    private JTable data;
    private JButton btnUpdate;
    private JButton btnDelete;

    // Declare the user variable here
    private User user;
    public User getUser() {
        return user;
    }

    public RegistrationForm(JFrame parent) {
        super(parent);
        setTitle("Create a new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(800, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameToDelete = tfName.getText();
                if (!nameToDelete.isEmpty()) {
                    deleteUserFromDatabase(nameToDelete);
                    displayUserData(); // Refresh the displayed data after deletion
                } else {
                    JOptionPane.showMessageDialog(RegistrationForm.this,
                            "Please enter a name to delete",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nameToUpdate = tfName.getText();
                String email = tfEmail.getText();
                String phone = tfPhone.getText();
                String address = tfAddress.getText();
                String password = String.valueOf(pfPassword.getPassword());

                if (!nameToUpdate.isEmpty()) {
                    updateUserInDatabase(nameToUpdate, email, phone, address, password);
                    displayUserData(); // Refresh the displayed data after update
                } else {
                    JOptionPane.showMessageDialog(RegistrationForm.this,
                            "Please enter a name to update",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Buat model tabel untuk JTable
        DefaultTableModel model = new DefaultTableModel();
        data.setModel(model);

// Panggil fungsi untuk menampilkan data di JTable saat program pertama kali dijalankan
        displayUserData();


        // Add a ListSelectionListener to the JTable
        data.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = data.getSelectedRow();
                    if (selectedRow != -1) {
                        fillFormFromSelectedRow(selectedRow);
                    }
                }
            }
        });

        // Panggil fungsi untuk menampilkan data di JTable saat program pertama kali dijalankan
        displayUserData();

        setVisible(true);
    }

    private void fillFormFromSelectedRow(int selectedRow) {
        DefaultTableModel model = (DefaultTableModel) data.getModel();

        // Get data from the selected row in the JTable
        String name = (String) model.getValueAt(selectedRow, 0);
        String email = (String) model.getValueAt(selectedRow, 1);
        String phone = (String) model.getValueAt(selectedRow, 2);
        String address = (String) model.getValueAt(selectedRow, 3);
        String password = (String) model.getValueAt(selectedRow, 4);

        // Set the text fields with the selected data
        tfName.setText(name);
        tfEmail.setText(email);
        tfPhone.setText(phone);
        tfAddress.setText(address);
        pfPassword.setText(password);
    }

    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String address = tfAddress.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter all fields",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Confirm Password does not match",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Panggil fungsi untuk menambahkan user ke database
        user = addUserToDatabase(name, email, phone, address, password);

        // Tambahkan pemanggilan fungsi untuk menampilkan data di JTable setelah registrasi
        if (user != null) {
            displayUserData();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register new user",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void displayUserData() {
        // Membuat koneksi ke database
        final String DB_URL = "jdbc:mysql://localhost/coffeshopp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            // Query untuk membaca data dari tabel users
            String sql = "SELECT * FROM users";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Mengambil metadata dari hasil query
            int columnCount = resultSet.getMetaData().getColumnCount();

            // Mengambil model tabel dari JTable
            DefaultTableModel model = (DefaultTableModel) data.getModel();

            // Clear existing columns in the model
            model.setColumnCount(0);

            // Mengisi tabel dengan data dari database
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);

                // Exclude the "id" column
                if (!columnName.equalsIgnoreCase("id")) {
                    model.addColumn(columnName);
                }
            }

            model.setRowCount(0);

            Vector<Object> headerRow = new Vector<>();
            headerRow.add("Name");
            headerRow.add("Email");
            headerRow.add("Phone");
            headerRow.add("Address");
            headerRow.add("Password");
            model.addRow(headerRow);

            while (resultSet.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    // Exclude the "id" column
                    if (!resultSet.getMetaData().getColumnName(i).equalsIgnoreCase("id")) {
                        row.add(resultSet.getObject(i));
                    }
                }
                model.addRow(row);
            }

            // Menutup koneksi dan statement
            resultSet.close();
            preparedStatement.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private User addUserToDatabase(String name, String email, String phone, String address, String password) {
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost/coffeshopp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO users (name, email, phone, address, password) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, password);

            // Insert row into the table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.address = address;
                user.password = password;
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }


    private void deleteUserFromDatabase(String name) {
        final String DB_URL = "jdbc:mysql://localhost/coffeshopp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "DELETE FROM users WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);

            int deletedRows = preparedStatement.executeUpdate();
            if (deletedRows > 0) {
                System.out.println("User deleted: " + name);
                JOptionPane.showMessageDialog(this,
                        "User deleted successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                displayUserData();
            } else {
                System.out.println("User not found: " + name);
            }

            preparedStatement.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateUserInDatabase(String name, String email, String phone, String address, String password) {
        final String DB_URL = "jdbc:mysql://localhost/coffeshopp?serverTimezone=UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            String sql = "UPDATE users SET email = ?, phone = ?, address = ?, password = ? WHERE name = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, password);
            preparedStatement.setString(5, name);

            int updatedRows = preparedStatement.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("User updated: " + name);
                JOptionPane.showMessageDialog(this,
                        "User update successfully",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                displayUserData();
            } else {
                System.out.println("User not found: " + name);
            }

            preparedStatement.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        RegistrationForm myForm = new RegistrationForm(null);
        User user = myForm.user;
        if (user != null) {
            System.out.println("Successful registration of: " + user.name);
        } else {
            System.out.println("Registration canceled");
        }
    }
}
