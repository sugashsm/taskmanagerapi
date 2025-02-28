 # Task Manager API

A Spring Boot application that provides a REST API for managing and executing shell commands as tasks in a Kubernetes pod environment. The application uses MongoDB Atlas for data persistence.


## Technologies Used
- Java 17
- Spring Boot 3.2.2
- MongoDB Atlas
- Maven

## Project Structure
```
taskmanager/
├── src/                      # Source files
│   └── main/
│       ├── java/             # Backend code
│       │   └── com/
│       │       └── example/
│       │           └── taskmanager/
│       │               ├── controller/        # Controller layer
│       │               │   └── TaskController.java    # Task API controller
│       │               ├── model/            # Data models
│       │               │   ├── Task.java           # Task entity
│       │               │   └── TaskExecution.java  # Task execution entity
│       │               ├── repository/       # Database interactions
│       │               │   └── TaskRepository.java # Task repository
│       │               ├── service/          # Business logic
│       │               │   └── TaskService.java    # Task service layer
│       │               └── TaskManagerApplication.java # Main application class
│       └── resources/        # Configuration files
│           └── application.properties   # Application properties
└── pom.xml                    # Maven configuration
```



## Setup and Installation

1. Clone the repository:

``bash 
git clone https://github.com/yourusername/task-manager-api.git
``

``bash 
cd task-manager-api
``
2. Configure MongoDB:
- Update `src/main/resources/application.properties` with your MongoDB connection string
3. Build the project:
``bash
mvn clean install
``   

4. Run the application:

``bash 
mvn spring-boot:run
``
The application will start on `http://localhost:8080`

## API Endpoints

### 1. Get All Tasks
http
GET /api/tasks

Response:
``json
[
{
"id": "67c1348bacc5d22bd6df2627",
"name": "Print Hello",
"owner": "Sugash Srimari",
"command": "echo Hello World!",
"taskExecutions": []
}
]``

### 2. Create Task'

``json 
http
POST /api/tasks
Content-Type: application/json
{
"name": "Print Hello",
"owner": "Sugash Srimari",
"command": "echo Hello World!"
}``

### 3. Get Task by ID

http
GET /api/tasks/{id}


### 5. Execute Task
http
POST /api/tasks/{id}/execute

### 6. Delete Task
http
DELETE /api/tasks/{id}


## Testing Screenshots
1. Creating a task
2. Listing tasks
3. Executing a task
4. Task execution results]

## Security Features
- Command validation to prevent unsafe shell commands
- Error handling for invalid requests
- MongoDB connection security

## Error Handling
The API provides appropriate error responses:
- 404 Not Found for non-existent resources
- 400 Bad Request for invalid input
- 500 Internal Server Error for server issues

## Future Improvements
- Add authentication and authorization
- Implement rate limiting
- Add more command validation rules
- Add unit and integration tests

## Author
Sugash Srimari R
