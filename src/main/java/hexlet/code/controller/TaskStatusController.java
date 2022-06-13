package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
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
import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {

    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    private final TaskStatusRepository taskStatusRepository;


    @Operation(summary = "Create new status")
    @ApiResponse(responseCode = "201", description = "Status created")
    @PostMapping
    @ResponseStatus(CREATED)
    public TaskStatus registerNew(@RequestBody @Valid final TaskStatusDto dto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(dto.getName());
        return taskStatusRepository.save(taskStatus);
    }


    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = TaskStatus.class))
    ))
    @GetMapping
    public List<TaskStatus> getAll() {
        return taskStatusRepository.findAll()
                .stream()
                .toList();
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public TaskStatus getTaskStatusById(@PathVariable final Long id) {
        return taskStatusRepository.findById(id).get();
    }

    @PutMapping(ID)
    public TaskStatus update(@PathVariable final long id, @RequestBody @Valid final TaskStatusDto dto) {
        final TaskStatus taskStatusToUpdate = taskStatusRepository.findById(id).get();
        taskStatusToUpdate.setName(dto.getName());
        return taskStatusRepository.save(taskStatusToUpdate);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final long id) {
        taskStatusRepository.deleteById(id);
    }

}
