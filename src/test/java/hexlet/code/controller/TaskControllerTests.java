package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.ConfigTest;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static hexlet.code.config.ConfigTest.TEST_PROFILE;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.TASK_STATUS_ID;
import static hexlet.code.utils.TestUtils.TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ConfigTest.class)
public class TaskControllerTests {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils utils;

    @BeforeAll
    public void createUserAndTaskStatus() throws Exception {
        utils.tearDown();
        utils.regDefaultUser();

        utils.perform(
                post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(new TaskStatusDto("test task status")))
                .contentType(APPLICATION_JSON), TEST_USERNAME
        );
    }

    @AfterAll
    public void clear() {
        utils.tearDown();
    }

    @Test
    @Order(1)
    public void createNewTask() throws Exception {
        assertEquals(0, taskRepository.count());

        final TaskDto taskToSave = new TaskDto(
                "test task",
                "test task description",
                null,
                TASK_STATUS_ID,
                null
        );

        final var request = post(TASK_CONTROLLER_PATH)
                .content(asJson(taskToSave))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task savedTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskRepository.getById(savedTask.getId())).isNotNull();
        assertEquals(1, taskRepository.count());
    }

    @Test
    @Order(2)
    public void getAllTasks() throws Exception {
        final var response = utils.perform(get(TASK_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks).hasSize(1);
    }

    @Test
    @Order(3)
    public void getTaskById() throws Exception {
        final Task expectedTask = taskRepository.findAll().get(0);

        final var response = utils.perform(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getName(), task.getName());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getAuthor().getEmail(), task.getAuthor().getEmail());
        assertEquals(expectedTask.getTaskStatus().getId(), task.getTaskStatus().getId());
    }

    @Test
    @Order(4)
    @Transactional
    public void updateTask() throws Exception {
        final Long statusId = taskRepository.findAll().get(0).getId();
        final TaskDto taskDto = new TaskDto(
                "new test task",
                "test task description",
                null,
                TASK_STATUS_ID,
                null
        );
        final var updateRequest = put(TASK_CONTROLLER_PATH
                + TaskStatusController.ID, statusId)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(updateRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task savedTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(taskRepository.existsById(statusId));
        assertEquals(taskRepository.getById(savedTask.getId()).getName(), "new test task");
        assertEquals(taskRepository.getById(savedTask.getId()).getDescription(), "test task description");
    }

    @Test
    @Order(5)
    public void deleteTask() throws Exception {
        final Long statusId = taskRepository.findAll().get(0).getId();
        utils.perform(delete(TASK_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, taskRepository.count());
    }

}
