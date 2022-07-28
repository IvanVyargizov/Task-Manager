package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.ConfigTest;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
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
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.ID;
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
public class TaskStatusControllerTests {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    @BeforeAll
    public void createUser() throws Exception {
        utils.tearDown();
        utils.regDefaultUser();
    }

    @AfterAll
    public void clear() {
        utils.tearDown();
    }

    @Test
    @Order(1)
    public void createNewTaskStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());

        final TaskStatusDto taskStatusToSave = new TaskStatusDto("test task status");

        final var request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(taskStatusToSave))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final TaskStatus saveTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatusRepository.getById(saveTaskStatus.getId())).isNotNull();
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    @Order(2)
    public void getAllStatus() throws Exception {
        final var response = utils.perform(get(TASK_STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatus).hasSize(1);
    }

    @Test
    @Order(3)
    public void getTaskStatusById() throws Exception {
        final TaskStatus expectedTaskStatus = taskStatusRepository.findAll().get(0);

        final var response = utils.perform(
                get(TASK_STATUS_CONTROLLER_PATH + ID, expectedTaskStatus.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTaskStatus.getId(), taskStatus.getId());
        assertEquals(expectedTaskStatus.getName(), taskStatus.getName());
    }

    @Test
    @Order(4)
    @Transactional
    public void updateTaskStatus() throws Exception {
        final Long statusId = taskStatusRepository.findAll().get(0).getId();
        final var taskStatusDto = new TaskStatusDto("new test task status");

        final var updateRequest = put(TASK_STATUS_CONTROLLER_PATH + ID, statusId)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(updateRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final TaskStatus savedTaskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(taskStatusRepository.existsById(statusId));
        assertThat(taskStatusRepository.getById(savedTaskStatus.getId()).getName()).isEqualTo("new test task status");
    }

    @Test
    @Order(5)
    public void deleteTaskStatus() throws Exception {
        final Long statusId = taskStatusRepository.findAll().get(0).getId();
        utils.perform(delete(TASK_STATUS_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }

}
