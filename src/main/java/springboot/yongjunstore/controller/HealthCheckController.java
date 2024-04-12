package springboot.yongjunstore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public ResponseEntity healthCheck(){

        return ResponseEntity.status(HttpStatus.OK).body("setUp");
    }

    @GetMapping("/")
    public ResponseEntity healthCheckRoot(){

        return ResponseEntity.status(HttpStatus.OK).body("setUp");
    }
}
