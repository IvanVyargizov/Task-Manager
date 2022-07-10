package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public Label createNewLabel(LabelDto dto) {
        final Label label = new Label();
        label.setName(dto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(long id, LabelDto dto) {
        final Label labelToUpdate = labelRepository.findById(id).get();
        labelToUpdate.setName(dto.getName());
        return labelRepository.save(labelToUpdate);
    }
}
