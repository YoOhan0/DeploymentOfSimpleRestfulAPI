package kr.co.yohancompany.my_restful_service.controller;


import jakarta.validation.Valid;
import kr.co.yohancompany.my_restful_service.bean.Post;
import kr.co.yohancompany.my_restful_service.bean.User;
import kr.co.yohancompany.my_restful_service.exception.UserNotFoundException;
import kr.co.yohancompany.my_restful_service.repository.PostRepository;
import kr.co.yohancompany.my_restful_service.repository.UserRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/jpa")
public class UserJpaController {

    private UserRepository userRepository;
    private PostRepository postRepository;

    public UserJpaController(UserRepository userRepository,PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository=postRepository;
    }

    // /jpa/users
    @GetMapping("/users")
    public Map<String, Object> retrieveAllUsers() {

        Map<String, Object> response = new HashMap<>();

        response.put("users", userRepository.findAll());
        response.put("count", userRepository.count());

        return response;
//        return userRepository.findAll();
    }

    // /jpa/users/{id}
    @GetMapping("/users/{id}")
    public ResponseEntity retrieveUserById(@PathVariable int id) {

        Optional<User> user = userRepository.findById(id);

        if (!user.isPresent()) {
            throw new UserNotFoundException("id" + id);
        }

        EntityModel entityModel = EntityModel.of(user.get());

        WebMvcLinkBuilder linTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        entityModel.add(linTo.withRel("all-users")); // all-users ->  http://localhost:8088/users

        return ResponseEntity.ok(entityModel);
    }

    @DeleteMapping("/users/{id}")
    public void deleteUserById(@PathVariable int id) {
        userRepository.deleteById(id);
    }

    // /jpa/users
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User savedUser = userRepository.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllPostsByUser(@PathVariable int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException("id-" + id);
        }

        return user.get().getPosts();
    }

    @PostMapping("/users/{id}/posts")
    public ResponseEntity createPost(@PathVariable int id, @RequestBody Post post) {
        Optional<User> userOptional=userRepository.findById(id);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("id="+id);
        }

        User user =userOptional.get();

        post.setUser(user);

        postRepository.save(post);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(post.getId())
                .toUri();

        return ResponseEntity.created(location).build();

    }
}
