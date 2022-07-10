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
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

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

        task.setName(newTask.getName());
        task.setDescription(newTask.getDescription());
        task.setExecutor(newTask.getExecutor());
        task.setTaskStatus(newTask.getTaskStatus());
    }

    private Task fromDto(final TaskDto dto) {
        final User author = userService.getCurrentUser();

        final TaskStatus taskStatus = this.taskStatusRepository.findById(dto.getTaskStatusId()).get();

        User executor = null;
        if (dto.getExecutorId() != null) {
            executor = userRepository.findById(dto.getExecutorId()).orElse(null);
        }

        final Set<Label> labels = Optional.ofNullable(dto.getLabelIds())
                .orElse(Set.of())
                .stream()
                .filter(Objects::nonNull)
                .map((labelId) -> labelRepository.findById(labelId).get())
                .collect(Collectors.toSet());

        return Task.builder()
                .author(author)
                .name(dto.getName())
                .description(dto.getDescription())
                .taskStatus(taskStatus)
                .executor(executor)
                .labels(labels)
                .build();
    }

}
