package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelRepository labelRepository;


    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "label created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Label registerNew(@RequestBody @Valid final LabelDto dto) {
        Label label = new Label();
        label.setName(dto.getName());
        return labelRepository.save(label);
    }


    @ApiResponses(@ApiResponse(responseCode = "200", content =
    @Content(schema = @Schema(implementation = Label.class))
    ))
    @GetMapping
    public List<Label> getAll() {
        return labelRepository.findAll()
                .stream()
                .toList();
    }

    @ApiResponses(@ApiResponse(responseCode = "200"))
    @GetMapping(ID)
    public Label getTaskStatusById(@PathVariable final Long id) {
        return labelRepository.findById(id).get();
    }

    @PutMapping(ID)
    public Label update(@PathVariable final long id, @RequestBody @Valid final LabelDto dto) {
        final Label labelToUpdate = labelRepository.findById(id).get();
        labelToUpdate.setName(dto.getName());
        return labelRepository.save(labelToUpdate);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final long id) {
        labelRepository.deleteById(id);
    }

}
