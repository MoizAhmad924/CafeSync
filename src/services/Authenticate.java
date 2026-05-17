package services;
import repository.UserRepository;
import models.User;

public class Authenticate {
    public User login(String username, String password) {
        UserRepository userRepo = new UserRepository();
        User user = userRepo.findById(username);
        if(user != null && user.verifyPassword(password)) {
            return user;
        }
        return null;
    }
    
}
