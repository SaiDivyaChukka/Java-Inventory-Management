import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryGUI {
    private JFrame frame;
    private JTextField nameField, quantityField, priceField;
    private JTextArea displayArea;
    private JButton updateButton, deleteButton; // declare buttons as class variables

    public InventoryGUI() {
        frame = new JFrame("Inventory Management");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        nameField = new JTextField(10);
        quantityField = new JTextField(5);
        priceField = new JTextField(5);

        JButton addButton = new JButton("Add Product");
        JButton viewButton = new JButton("View Products");
        updateButton = new JButton("Update Product");
        deleteButton = new JButton("Delete Product");

        displayArea = new JTextArea(15, 40);
        displayArea.setEditable(false);

        // Add components to frame
        frame.add(new JLabel("Name:"));
        frame.add(nameField);
        frame.add(new JLabel("Quantity:"));
        frame.add(quantityField);
        frame.add(new JLabel("Price:"));
        frame.add(priceField);
        frame.add(addButton);
        frame.add(viewButton);
        frame.add(updateButton);
        frame.add(deleteButton);
        frame.add(new JScrollPane(displayArea));

        // Add action listeners
        addButton.addActionListener(e -> addProduct());
        viewButton.addActionListener(e -> viewProducts());
        updateButton.addActionListener(e -> updateProduct());
        deleteButton.addActionListener(e -> deleteProduct());

        frame.setVisible(true);
    }

    private void addProduct() {
        String name = nameField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO products (name, quantity, price) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setInt(2, quantity);
            ps.setDouble(3, price);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Product added!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void viewProducts() {
        displayArea.setText("");
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM products";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                displayArea.append(rs.getInt("id") + ": " + rs.getString("name") +
                        " | Qty: " + rs.getInt("quantity") +
                        " | Price: " + rs.getDouble("price") + "\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateProduct() {
        String idStr = JOptionPane.showInputDialog(frame, "Enter Product ID to update:");
        if (idStr == null) return;
        int id = Integer.parseInt(idStr);

        String quantityStr = JOptionPane.showInputDialog(frame, "Enter new quantity:");
        if (quantityStr == null) return;
        int quantity = Integer.parseInt(quantityStr);

        String priceStr = JOptionPane.showInputDialog(frame, "Enter new price:");
        if (priceStr == null) return;
        double price = Double.parseDouble(priceStr);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE products SET quantity = ?, price = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setDouble(2, price);
            ps.setInt(3, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Product updated!");
            } else {
                JOptionPane.showMessageDialog(frame, "Product ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteProduct() {
        String idStr = JOptionPane.showInputDialog(frame, "Enter Product ID to delete:");
        if (idStr == null) return;
        int id = Integer.parseInt(idStr);

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM products WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(frame, "Product deleted!");
            } else {
                JOptionPane.showMessageDialog(frame, "Product ID not found.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new InventoryGUI();
    }
}
