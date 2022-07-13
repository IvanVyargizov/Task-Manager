package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Map;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {

    public static final String TEST_USERNAME = "email@email.com";
    public static final String TEST_USERNAME_2 = "email2@email.com";
    public static final String TEST_TASK_STATUS = "Name";
    public static final String TEST_TASK_STATUS_2 = "New name";
    public static final String TEST_LABEL = "Label";
    public static final String TEST_LABEL_2 = "New label";
    public static final String TEST_TASK_NAME = "Task";
    public static final String TEST_TASK_NAME_2 = "New task";
    public static final String TEST_TASK_DESCRIPTION = "Task description";
    public static final String TEST_TASK_DESCRIPTION_2 = "New task description";
    public static final Long TASK_STATUS_ID = 1L;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private JWTHelper jwtHelper;

    @Autowired
    private MockMvc mockMvc;

    private final UserDto testRegistrationDto = new UserDto(
            TEST_USERNAME,
            "fname",
            "lname",
            "pwd"
    );

    private final TaskDto testCreateTaskDto = new TaskDto(
            TEST_TASK_NAME,
            TEST_TASK_DESCRIPTION,
            null,
            TASK_STATUS_ID,
            null
    );

    private final TaskStatusDto testCreateTaskStatusDto = new TaskStatusDto(
            TEST_TASK_STATUS
    );

    private final LabelDto testCreateLabelDto = new LabelDto(
            TEST_LABEL
    );

    public UserDto getTestRegistrationDto() {
        return testRegistrationDto;
    }

    public void tearDown() {
        labelRepository.deleteAll();
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    public ResultActions regDefaultUser() throws Exception {
        return regUser(testRegistrationDto);
    }

    public ResultActions regUser(final UserDto dto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request);
    }

    public ResultActions createDefaultTask() throws Exception {
        return createTask(testCreateTaskDto);
    }

    public ResultActions createTask(final TaskDto dto) throws Exception {
        final var request = post(TASK_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions createDefaultTaskStatus() throws Exception {
        return createTaskStatus(testCreateTaskStatusDto);
    }

    public ResultActions createTaskStatus(final TaskStatusDto dto) throws Exception {
        final var request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions createDefaultLabel() throws Exception {
        return createLabel(testCreateLabelDto);
    }

    public ResultActions createLabel(final LabelDto dto) throws Exception {
        final var request = post(LABEL_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);

        return perform(request, TEST_USERNAME);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    public static String asJson(final Object object) throws JsonProcessingException {
        return MAPPER.writeValueAsString(object);
    }

    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }

}
