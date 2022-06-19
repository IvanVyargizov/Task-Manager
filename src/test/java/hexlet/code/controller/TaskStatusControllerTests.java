package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.ConfigTest;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.ConfigTest.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.ID;
import static hexlet.code.utils.TestUtils.TEST_TASK_STATUS_2;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = ConfigTest.class)
public class TaskStatusControllerTests {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TestUtils utils;

    public TaskStatusControllerTests() {
    }

    @BeforeEach
    public void createUser() throws Exception {
        utils.tearDown();
        utils.regDefaultUser();
    }

    @AfterEach
    public void clear() {
        utils.tearDown();
    }

    @Test
    public void createTaskStatus() throws Exception {
        assertEquals(0, taskStatusRepository.count());
        utils.createDefaultTaskStatus().andExpect(status().isCreated());
        assertEquals(1, taskStatusRepository.count());
    }

    @Test
    public void getAllStatus() throws Exception {
        utils.createDefaultTaskStatus();
        final var response = utils.perform(get(TASK_STATUS_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<TaskStatus> taskStatus = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(taskStatus).hasSize(1);
    }

    @Test
    public void getTaskStatusById() throws Exception {
        utils.createDefaultTaskStatus();
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
    public void updateTaskStatus() throws Exception {
        utils.createDefaultTaskStatus();
        final Long statusId = taskStatusRepository.findAll().get(0).getId();
        final var taskStatusDto = new TaskStatusDto(TEST_TASK_STATUS_2);

        final var updateRequest = put(TASK_STATUS_CONTROLLER_PATH + ID, statusId)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(taskStatusRepository.existsById(statusId));
        assertEquals(taskStatusRepository.findById(statusId).get().getName(), TEST_TASK_STATUS_2);
        assertEquals(1, taskStatusRepository.count());

    }

    @Test
    public void deleteTaskStatus() throws Exception {
        utils.createDefaultTaskStatus();
        final Long statusId = taskStatusRepository.findAll().get(0).getId();

        utils.perform(delete(TASK_STATUS_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, taskStatusRepository.count());
    }

}
