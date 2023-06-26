package com.angela.betaplan.userauthentication.services;
import com.angela.betaplan.userauthentication.models.LoginUser;
import com.angela.betaplan.userauthentication.models.User;
import com.angela.betaplan.userauthentication.repositories.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    // This method will be called from the controller
    // whenever a user submits a registration form.

    public User register(User newUser, BindingResult result) {

        // TO-DO - Reject values or register if no errors:

        // Reject if email is taken (present in database)

//		if (userRepo.findByEmail(newUser.getEmail()) != null) {
//			result.rejectValue("email", "EmailTaken", "Email address is already in use.");  // another way of doing it?
//		}

        Optional<User> potentialUser = this.userRepo.findByEmail(newUser.getEmail());

        if (potentialUser.isPresent()) {
            result.rejectValue("email", "EmailTaken", "Email address is already in use.");
        }

        // Reject if password doesn't match confirmation

        if (!newUser.getPassword().equals(newUser.getConfirm())) {
            result.rejectValue("confirm", "Matches", "The Confirm Password must match Password!");
        }

        // Return null if result has errors

        if (result.hasErrors()) {
            return null;
        }

        // Hash and set password, save user to database

        else {
            String hashed = BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt());
            newUser.setPassword(hashed);
            return userRepo.save(newUser);
        }

    }

    // This method will be called from the controller
    // whenever a user submits a login form.

    public User login(LoginUser newLoginObject, BindingResult result) {

        // Find user in the DB by email
        Optional<User> potentialUser = this.userRepo.findByEmail(newLoginObject.getEmail());
        // Reject if NOT present

        if (!potentialUser.isPresent()) {
            result.rejectValue("email", "EmailNotFound", "No user found with that Email address.");
        } else {

            if (!BCrypt.checkpw(newLoginObject.getPassword(), potentialUser.get().getPassword())) {
                // Reject if BCrypt password match fails
                result.rejectValue("password", "Matches", "Invalid Password!");
            }
        }

        if (result.hasErrors()) {
            // Return null if result has errors
            return null;
        } else {
            // Otherwise, return the user object
            return potentialUser.get();
        }
    }

    public User findOneUser(Long id) {
        return this.userRepo.findById(id).orElse(null);
    }


}