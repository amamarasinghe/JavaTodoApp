import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TaskManagerGUI extends JFrame {
    private DefaultListModel<String> taskListModel;
    private JList<String> taskList;
    private JTextField taskInput;
    private JButton addButton, deleteButton;

    public TaskManagerGUI() {
        setTitle("To-Do App");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background color for frame
        getContentPane().setBackground(new Color(245, 245, 245));

        // Load icons
        ImageIcon addIcon = new ImageIcon("images/add.png");
        ImageIcon deleteIcon = new ImageIcon("images/delete.png");
        ImageIcon logo = new ImageIcon("images/logo.png");

        // Font
        Font font = new Font("Segoe UI", Font.PLAIN, 14);

        // Top Logo
        if (logo != null) {
            JLabel logoLabel = new JLabel(logo);
            logoLabel.setHorizontalAlignment(JLabel.CENTER);
            add(logoLabel, BorderLayout.NORTH);
        }

        // Task List
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(font);
        taskList.setBackground(new Color(255, 255, 224));
        taskList.setForeground(Color.DARK_GRAY);
        taskList.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JScrollPane scrollPane = new JScrollPane(taskList);

        // Input Panel
        taskInput = new JTextField();
        taskInput.setFont(font);
        addButton = new JButton("Add Task", addIcon);
        addButton.setFont(font);
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.add(taskInput, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);

        // Bottom Panel with Delete
        deleteButton = new JButton("Delete Selected", deleteIcon);
        deleteButton.setFont(font);
        deleteButton.setBackground(new Color(220, 20, 60));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.add(deleteButton);

        // Add components to frame
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Load existing tasks
        loadTasks();

        // Button Listeners
        addButton.addActionListener(e -> {
            String desc = taskInput.getText().trim();
            if (!desc.isEmpty()) {
                addTask(desc);
                taskInput.setText("");
                loadTasks();
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedValue = taskListModel.getElementAt(selectedIndex);
                int id = Integer.parseInt(selectedValue.split(":")[0].replaceAll("[^0-9]", ""));
                deleteTask(id);
                loadTasks();
            }
        });
    }

    private void loadTasks() {
        taskListModel.clear();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tasks")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String desc = rs.getString("description");
                boolean done = rs.getBoolean("is_done");
                String taskStr = "[" + (done ? "x" : " ") + "] " + id + ": " + desc;
                taskListModel.addElement(taskStr);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading tasks:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTask(String desc) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO tasks(description) VALUES(?)")) {
            ps.setString(1, desc);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding task:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTask(int id) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM tasks WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting task:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TaskManagerGUI().setVisible(true);
        });
    }
}
