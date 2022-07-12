package hexlet.code.controller;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import com.querydsl.core.types.Predicate;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@SecurityRequirement(name = "javainuseapi")
@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";

    private static final String ONLY_AUTHOR_BY_ID =
            "@taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()";


    private final TaskRepository taskRepository;
    private final TaskService taskService;

    @Operation(summary = "Get All tasks")
    @GetMapping
    public Iterable<Task> getAll(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return taskRepository.findAll(predicate);
    }

    @Operation(summary = "Get task by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task found"),
            @ApiResponse(responseCode = "404", description = "task with that id not found")
    })
    @GetMapping(ID)
    public Task getById(@PathVariable final Long id) {
        return taskRepository.findById(id).get();
    }

    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "task created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Task createNewTask(@RequestBody @Valid final TaskDto dto) {
        return taskService.createNewTask(dto);
    }

    @Operation(summary = "Update task")
    @ApiResponse(responseCode = "200", description = "task updated")
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    @PutMapping(ID)
    public Task updateTask(@PathVariable final Long id,
                           // Schema используется, чтобы указать тип данных для параметра
                           @Parameter(schema = @Schema(implementation = TaskDto.class))
                           @RequestBody @Valid final TaskDto dto) {
        return taskService.updateTask(id, dto);
    }

    @Operation(summary = "Delete task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "task deleted"),
            @ApiResponse(responseCode = "404", description = "task with that id not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable final Long id) {
        taskRepository.deleteById(id);
    }

}
