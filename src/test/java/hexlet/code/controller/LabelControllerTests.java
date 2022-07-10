package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.ConfigTest;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.LabelController.ID;
import static hexlet.code.utils.TestUtils.TEST_LABEL_2;
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
public class LabelControllerTests {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

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
    public void createLabel() throws Exception {
        assertEquals(0, labelRepository.count());
        utils.createDefaultLabel().andExpect(status().isCreated());
        assertEquals(1, labelRepository.count());
    }

    @Test
    public void getAllLabels() throws Exception {
        utils.createDefaultLabel();
        final var response = utils.perform(get(LABEL_CONTROLLER_PATH), TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(labels).hasSize(1);
    }

    @Test
    public void getLabelById() throws Exception {
        utils.createDefaultLabel();
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
    public void updateLabel() throws Exception {
        utils.createDefaultLabel();
        final Long statusId = labelRepository.findAll().get(0).getId();
        final var labelDto = new LabelDto(TEST_LABEL_2);

        final var updateRequest = put(LABEL_CONTROLLER_PATH + ID, statusId)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, TEST_USERNAME).andExpect(status().isOk());

        assertTrue(labelRepository.existsById(statusId));
        assertEquals(labelRepository.findById(statusId).get().getName(), TEST_LABEL_2);
        assertEquals(1, labelRepository.count());

    }

    @Test
    public void deleteLabel() throws Exception {
        utils.createDefaultLabel();
        final Long statusId = labelRepository.findAll().get(0).getId();
        utils.perform(delete(LABEL_CONTROLLER_PATH + ID, statusId), TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, labelRepository.count());
    }

}


