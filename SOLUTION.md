# Solution Steps

1. Initialize a Spring Boot project with necessary dependencies (Spring Web).

2. Create a main application class (FibonacciApplication.java) in the base package.

3. Implement a FibonacciResult DTO to capture the job status/result/error.

4. Implement FibonacciJobManager as a Spring service that manages job submission, storage (thread-safe), and cleanup.

5. Expose /api/fibonacci/async/{n} to start a background Fibonacci job and return a unique jobId, and /api/fibonacci/result/{jobId} to check job status/result.

6. Set server.port=8080 in application.properties for consistent container exposure.

7. Write a .dockerignore file to ensure only necessary files are sent to Docker builds.

8. Create an optimized Dockerfile using a Maven build stage (compiling the app) and a runtime OpenJDK image, tuning JVM memory.

9. Write docker-compose.yml exposing port 8080 with an environment variable for JVM options.

10. Test locally with 'docker-compose build' and 'docker-compose up' and verify endpoints (use curl or Postman to submit and query jobs).

