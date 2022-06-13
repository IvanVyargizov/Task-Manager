package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    private final LabelRepository labelRepository;

    @Autowired
    private final UserService userService;

    @Override
    public Task createNewTask(TaskDto dto) {
        final Task newTask = fromDto(dto);
        return taskRepository.save(newTask);
    }

    @Override
    public Task updateTask(long id, TaskDto dto) {
        final Task task = taskRepository.findById(id).get();
        merge(task, dto);
        return taskRepository.save(task);
    }

    private void merge(final Task task, final TaskDto postDto) {
        final Task newTask = fromDto(postDto);
        if (newTask.getName() != null) {
            task.setName(newTask.getName());
        }
        if (newTask.getDescription() != null) {
            task.setDescription(newTask.getDescription());
        }
        if (newTask.getExecutor() != null) {
            task.setExecutor(newTask.getExecutor());
        }
        if (newTask.getTaskStatus() != null) {
            task.setTaskStatus(newTask.getTaskStatus());
        }

    }

    private Task fromDto(final TaskDto dto) {
        final User author = userService.getCurrentUser();

        final TaskStatus taskStatus = this.taskStatusRepository.findById(dto.getTaskStatusId()).get();

        User executor = null;
        if (dto.getExecutorId() != null) {
            executor = userRepository.findById(dto.getExecutorId()).orElse(null);
        }
        Set<Label> labelSet = null;
        if (dto.getLabelIds() != null) {
            labelSet = dto.getLabelIds().stream()
                    .map((x) -> labelRepository.findById(x).get())
                    .collect(Collectors.toSet());
        }
        return Task.builder()
                .author(author)
                .name(dto.getName())
                .description(dto.getDescription())
                .taskStatus(taskStatus)
                .executor(executor)
                .labels(labelSet)
                .build();
    }

}
