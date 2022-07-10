package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@SecurityRequirement(name = "javainuseapi")
@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelRepository labelRepository;
    private final LabelService labelService;


    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "label created")
    @PostMapping
    @ResponseStatus(CREATED)
    public Label createNewLabel(@RequestBody @Valid final LabelDto dto) {
        return labelService.createNewLabel(dto);
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

    @Operation(summary = "Get label by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label found"),
            @ApiResponse(responseCode = "404", description = "Label with that id not found")
    })
    @GetMapping(ID)
    public Label getTaskStatusById(@PathVariable final Long id) {
        return labelRepository.findById(id).get();
    }

    @Operation(summary = "Update Label")
    @ApiResponse(responseCode = "200", description = "Label updated")
    @PutMapping(ID)
    public Label update(@PathVariable final long id, @RequestBody @Valid final LabelDto dto) {
        return labelService.updateLabel(id, dto);
    }

    @DeleteMapping(ID)
    public void delete(@PathVariable final long id) {
        labelRepository.deleteById(id);
    }

}
