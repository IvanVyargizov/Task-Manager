package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.ConfigTest;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.LabelController.ID;
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
public class LabelControllerTests {

    @Autowired
    private LabelRepository labelRepository;

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
    public void createNewLabel() throws Exception {
        assertEquals(0, labelRepository.count());

        final LabelDto labelToSave = new LabelDto("test label");

        final var request = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelToSave))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(request, TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Label savedLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(labelRepository.getById(savedLabel.getId())).isNotNull();
        assertEquals(1, labelRepository.count());
    }

    @Test
    @Order(2)
    public void getAllLabels() throws Exception {
        final var response = utils.perform(get(LABEL_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(labels).hasSize(1);
    }

    @Test
    @Order(3)
    public void getLabelById() throws Exception {
        final Label expectedLabel = labelRepository.findAll().get(0);

        final var response = utils.perform(
                get(LABEL_CONTROLLER_PATH + ID, expectedLabel.getId()), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label label = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());
    }

    @Test
    @Order(4)
    @Transactional
    public void updateLabel() throws Exception {
        final Long labelId = labelRepository.findAll().get(0).getId();
        final LabelDto labelToSave = new LabelDto("new test label");

        final var updateRequest = put(LABEL_CONTROLLER_PATH + ID, labelId)
                .content(asJson(labelToSave))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(updateRequest, TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label savedLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(labelRepository.existsById(labelId));
        assertThat(labelRepository.getById(savedLabel.getId()).getName()).isEqualTo("new test label");
    }

    @Test
    @Order(5)
    public void deleteLabel() throws Exception {
        final Long statusId = labelRepository.findAll().get(0).getId();
        utils.perform(delete(LABEL_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, labelRepository.count());
    }

}
