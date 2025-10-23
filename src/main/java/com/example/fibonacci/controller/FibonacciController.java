package com.example.fibonacci.controller;

import com.example.fibonacci.service.FibonacciJobManager;
import com.example.fibonacci.service.FibonacciResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fibonacci")
public class FibonacciController {
    private final FibonacciJobManager jobManager;

    @Autowired
    public FibonacciController(FibonacciJobManager jobManager) {
        this.jobManager = jobManager;
    }

    @GetMapping("/async/{n}")
    public ResponseEntity<Map<String, String>> startFibonacciJob(@PathVariable("n") int n) {
        String jobId = jobManager.submitJob(n);
        return ResponseEntity.ok(Map.of("jobId", jobId));
    }

    @GetMapping("/result/{jobId}")
    public ResponseEntity<FibonacciResult> getFibonacciJobResult(@PathVariable("jobId") String jobId) {
        FibonacciResult result = jobManager.getJobResult(jobId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
