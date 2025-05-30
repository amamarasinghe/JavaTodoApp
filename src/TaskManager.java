import java.sql.*;
import java.util.Scanner;

public class TaskManager {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== TO-DO APP ===");
            System.out.println("1. View Tasks");
            System.out.println("2. Add Task");
            System.out.println("3. Delete Task");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1 -> viewTasks();
                case 2 -> {
                    System.out.print("Task description: ");
                    String desc = sc.nextLine();
                    addTask(desc);
                }
                case 3 -> {
                    System.out.print("Task ID to delete: ");
                    int id = sc.nextInt();
                    deleteTask(id);
                }
                case 0 -> {
                    System.out.println("Bye!");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void viewTasks() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM tasks";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.println("\n--- Current Tasks ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String desc = rs.getString("description");
                boolean done = rs.getBoolean("is_done");
                System.out.println("[" + (done ? "x" : " ") + "] " + id + ": " + desc);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addTask(String desc) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO tasks(description) VALUES(?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, desc);
            stmt.executeUpdate();
            System.out.println("✅ Task added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void deleteTask(int id) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM tasks WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows > 0)
                System.out.println("🗑 Task deleted.");
            else
                System.out.println("⚠ Task not found.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
