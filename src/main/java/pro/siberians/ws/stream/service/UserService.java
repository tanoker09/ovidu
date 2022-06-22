package pro.siberians.ws.stream.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.*;
import pro.siberians.ws.stream.repository.*;
import pro.siberians.ws.stream.exception.*;

@Service
public class UserService {

	@Autowired UserRepository userRepository;

	static String DEFAULT_USER_ROLE = "contestant";

	public Optional<UserModel> findByEmail(String email) {
		return userRepository.find(email);
	}

	public UserModel findByEmailOrThrow(String email) throws UserNotFoundException {
		return findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found!"));
	}

	public PagedResults<UserModel> findAllOnPage(LastEvaluatedKey lek, Integer limit) {
		return userRepository.findAllOnPage(lek, limit);
	}

	public PagedResults<UserModel> findAllOnPageWithEmail(LastEvaluatedKey lek, Integer limit, String email) {
		return userRepository.findAllOnPageWithEmail(lek, limit, email);
	}

	public UserModel create(String email, String name) {
		return create(email, name, DEFAULT_USER_ROLE);
	}

	public UserModel create(String email, String name, String role) {
		UserModel user = new UserModel();
		user.setEmail(email);
		user.setName(name);
		user.setRole(role);
		userRepository.save(user);
		return user;
	}

	public void update(UserModel user, String name, String role) {
		user.setName(name);
		user.setRole(role);
		userRepository.save(user);
	}

	public void changeName(UserModel user, String newName) {
		user.setName(newName);
		userRepository.save(user);
	}

}
