package models;
import enums.Role;
import java.time.LocalDate;
import utils.HashUtility;

public class User {
    protected String name;
    protected String cnic;
    protected LocalDate dateOfBirth;
    protected String phoneNo;
    protected String address;
    protected Role userRole;
    protected String username;
    protected String password;

    public User(String name, String cnic, String dateOfBirth, String phoneNo,
            String address, Role userRole, String username, String password) {

        setName(name);
        setCnic(cnic);
        setDateOfBirth(dateOfBirth);
        setPhoneNo(phoneNo);
        setAddress(address);
        setUserRole(userRole);
        setUsername(username);
        setPassword(password);
    }

    private void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
        this.name = name.trim();
    }

    private void setCnic(String cnic) {
        if (cnic == null || !cnic.matches("\\d{5}-\\d{7}-\\d{1}")) {
            throw new IllegalArgumentException("CNIC must follow the format XXXXX-XXXXXXX-X.");
        }
        this.cnic = cnic;
    }

    private void setDateOfBirth(String dateOfBirth) {
        if (dateOfBirth == null) {
            throw new IllegalArgumentException("Date of birth cannot be null.");
        }
        LocalDate dob;
        try {
            dob = LocalDate.parse(dateOfBirth);
        } catch (Exception e) {
            throw new IllegalArgumentException("Date of birth must be in the format YYYY-MM-DD.");
        }
        if (dob.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future.");
        }
        this.dateOfBirth = dob;
    }

    public void setPhoneNo(String phoneNo) {
        if (phoneNo == null || !phoneNo.matches("\\d{11}")) {
            throw new IllegalArgumentException("Phone number must be exactly 11 digits.");
        }
        this.phoneNo = phoneNo;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be null or empty.");
        }
        this.address = address.trim();
    }

    private void setUserRole(Role userRole) {
        if (userRole == null) {
            throw new IllegalArgumentException("User role cannot be null.");
        }
        this.userRole = userRole;
    }

    private void setUsername(String username) {
        if (username == null || username.trim().length() < 4) {
            throw new IllegalArgumentException("Username must be at least 4 characters long.");
        }
        if (username.contains(" ")) {
            throw new IllegalArgumentException("Username cannot contain spaces.");
        }
        this.username = username.trim();
    }

    public void setPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long.");
        }
        this.password = HashUtility.hashWithSHA256(plainTextPassword);
    }

    public boolean verifyPassword(String loginAttemptPassword) {
        if (this.password == null) {
            return false;
        }
        String hashedAttempt = HashUtility.hashWithSHA256(loginAttemptPassword);
        return this.password.equals(hashedAttempt);
    }

    public String getName() {
        return name;
    }

    public String getCnic() {
        return cnic;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public Role getUserRole() {
        return userRole;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
