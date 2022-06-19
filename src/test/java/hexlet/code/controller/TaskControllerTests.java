package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.ConfigTest;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static hexlet.code.config.ConfigTest.TEST_PROFILE;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.utils.TestUtils.TEST_TASK_DESCRIPTION_2;
import static hexlet.code.utils.TestUtils.TEST_TASK_NAME_2;
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
public class TaskControllerTests {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestUtils utils;

    public TaskControllerTests() {
    }

    @Test
    public void crudTask() throws Exception {
        //create
        utils.tearDown();
        assertEquals(0, taskRepository.count());
        utils.regDefaultUser();
        utils.createDefaultTaskStatus();
        utils.createDefaultTask().andExpect(status().isCreated());
        assertEquals(1, taskRepository.count());

        //getAll
        final var response1 = utils.perform(get(TASK_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final List<Task> tasks = fromJson(response1.getContentAsString(), new TypeReference<>() {
        });
        assertThat(tasks).hasSize(1);

        //getById
        final Task expectedTask = taskRepository.findAll().get(0);
        final var response2 = utils.perform(
                get(TASK_CONTROLLER_PATH + ID, expectedTask.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Task task = fromJson(response2.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedTask.getId(), task.getId());
        assertEquals(expectedTask.getName(), task.getName());
        assertEquals(expectedTask.getDescription(), task.getDescription());
        assertEquals(expectedTask.getAuthor().getEmail(), task.getAuthor().getEmail());
        assertEquals(expectedTask.getTaskStatus().getId(), task.getTaskStatus().getId());

        //update
        final Long statusId = taskRepository.findAll().get(0).getId();
        final var taskDto = new TaskDto(
                TEST_TASK_NAME_2,
                TEST_TASK_DESCRIPTION_2,
                1L,
                1L,
                null
        );
        final var updateRequest = put(TASK_CONTROLLER_PATH + TaskStatusController.ID, statusId)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());
        assertTrue(taskRepository.existsById(statusId));
        assertEquals(taskRepository.findById(statusId).get().getName(), TEST_TASK_NAME_2);
        assertEquals(taskRepository.findById(statusId).get().getDescription(), TEST_TASK_DESCRIPTION_2);
        assertEquals(1, taskRepository.count());

        //delete
        utils.perform(delete(TASK_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());
        assertEquals(0, taskRepository.count());
    }

}
