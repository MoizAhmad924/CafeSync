package repository;
import enums.Role;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.User;

public class UserRepository implements interfaces.CSVRepository<User> {

    private static final String FILE_PATH = "data/users.csv";
    private static final String HEADER = "name,cnic,dob,phone,address,role,username,password";

    // CSV line → User object
    private User deserialize(String line) {
        String[] p = line.split(",");
        return new User(p[0], p[1], p[2], p[3], p[4], Role.valueOf(p[5]), p[6], p[7]);
    }

    // User object → CSV line
    private String serialize(User u) {
        return String.join(",",
            u.getName(), u.getCnic(), u.getDateOfBirth().toString(), u.getPhoneNo(), u.getAddress(), u.getUserRole().name(), u.getUsername(), u.getPassword());
    }

    @Override
    public boolean save(User user) {
        try(FileWriter writer = new FileWriter(FILE_PATH, true)) {
            if(new java.io.File(FILE_PATH).length() == 0) {
                writer.append(HEADER + "\n");
            }
            writer.append(serialize(user) + "\n");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<User> findAll() {
        File file = new File(FILE_PATH);
    
        try(Scanner scanner = new Scanner(file);) {
            List<User> users = new ArrayList<>();
            if(scanner.hasNextLine()) {
                scanner.nextLine();
            }
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                users.add(deserialize(line));
            }
            scanner.close();
            return users;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    public User findById(String username) {
        List<User> users = findAll();
        for(User u : users) {
            if(u.getUsername().equals(username)) {
                return u;
            }
        }
        System.out.println("User not found.");
        return null;
    }

    @Override
    public boolean update(User user) {
        List<User> users = findAll();
        for(User u : users) {
            if(u.getUsername().equals(user.getUsername())) {
                users.remove(u);
                users.add(user);
                rewriteAll(users);
                return true;
            }
        }
        System.out.println("User not found.");
        return false;
    }

    @Override
    public boolean delete(String username) {
        List<User> users = findAll();
        for(User u : users) {
            if(u.getUsername().equals(username)) {
                users.remove(u);
                rewriteAll(users);
                return true;
            }
        }
        System.out.println("User not found.");
        rewriteAll(users);
        return false;
    }

    private void rewriteAll(List<User> users) {
        try(FileWriter writer = new FileWriter(FILE_PATH, false)) {
            writer.append(HEADER + "\n");
            for(User u : users) {
                writer.append(serialize(u) + "\n");
            }
        } catch (Exception e) {
            System.out.println("Error occurred while rewriting users.");
            e.printStackTrace();
        }
    }
}

