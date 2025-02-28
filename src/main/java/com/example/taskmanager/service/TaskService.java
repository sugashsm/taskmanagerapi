package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.TaskExecution;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.lang.ProcessBuilder;

@Service
@Slf4j
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public Task getTaskById(String id) {
        return taskRepository.findById(id).orElse(null);
    }
    
    public List<Task> findTasksByName(String name) {
        return taskRepository.findByNameContaining(name);
    }
    
    public Task createTask(Task task) {
        validateCommand(task.getCommand());
        task.setTaskExecutions(new ArrayList<>());
        return taskRepository.save(task);
    }
    
    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }
    
    public TaskExecution executeTask(String id) throws Exception {
        log.debug("Attempting to execute task with id: {}", id);
        
        try {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
            
            log.debug("Found task: {}", task);
            
            TaskExecution execution = new TaskExecution();
            execution.setStartTime(new Date());
            
            try {
                ProcessBuilder processBuilder = new ProcessBuilder();
                
                // Set up the command based on OS
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    processBuilder.command("cmd.exe", "/c", task.getCommand());
                } else {
                    processBuilder.command("sh", "-c", task.getCommand());
                }
                
                processBuilder.redirectErrorStream(true);
                log.debug("Executing command: {}", task.getCommand());
                
                Process process = processBuilder.start();
                
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }
                
                boolean completed = process.waitFor(10, TimeUnit.SECONDS);
                execution.setEndTime(new Date());
                
                if (!completed) {
                    process.destroyForcibly();
                    throw new RuntimeException("Command execution timed out");
                }
                
                int exitCode = process.exitValue();
                if (exitCode == 0) {
                    execution.setOutput(output.toString().trim());
                } else {
                    throw new RuntimeException("Command failed with exit code: " + exitCode);
                }
                
                if (task.getTaskExecutions() == null) {
                    task.setTaskExecutions(new ArrayList<>());
                }
                task.getTaskExecutions().add(execution);
                taskRepository.save(task);
                
                return execution;
                
            } catch (IOException e) {
                log.error("IO Error during command execution: ", e);
                throw new RuntimeException("Failed to execute command: " + e.getMessage());
            }
        } catch (Exception e) {
            log.error("Error executing task: ", e);
            throw new RuntimeException("Failed to execute task: " + e.getMessage());
        }
    }
    
    private void validateCommand(String command) {
        // Add your command validation logic here
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty");
        }
        // Add more validation rules as needed
        if (command.contains(";") || command.contains("&&") || command.contains("|")) {
            throw new IllegalArgumentException("Command contains unsafe characters");
        }
    }
} 