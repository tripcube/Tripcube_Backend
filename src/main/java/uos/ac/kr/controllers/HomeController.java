package uos.ac.kr.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uos.ac.kr.repositories.UserRepository;

@RestController()
@RequestMapping("/")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeController {

    private final UserRepository userRepo;

    @GetMapping("")
    public String getHome() {
        return "Hello;";
    }
}
