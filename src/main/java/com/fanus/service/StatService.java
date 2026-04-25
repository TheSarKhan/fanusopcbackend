package com.fanus.service;

import com.fanus.dto.StatDto;
import com.fanus.dto.StatRequest;
import com.fanus.entity.Stat;
import com.fanus.exception.ResourceNotFoundException;
import com.fanus.repository.StatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatService {

    private final StatRepository repo;

    public List<StatDto> findAll() {
        return repo.findAllByOrderByDisplayOrderAsc().stream().map(this::toDto).toList();
    }

    @Transactional
    public StatDto create(StatRequest req) {
        Stat s = Stat.builder().statValue(req.statValue()).suffix(req.suffix())
            .label(req.label()).subLabel(req.subLabel()).displayOrder(req.displayOrder()).build();
        return toDto(repo.save(s));
    }

    @Transactional
    public StatDto update(Long id, StatRequest req) {
        Stat s = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stat not found: " + id));
        s.setStatValue(req.statValue()); s.setSuffix(req.suffix());
        s.setLabel(req.label()); s.setSubLabel(req.subLabel()); s.setDisplayOrder(req.displayOrder());
        return toDto(repo.save(s));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Stat not found: " + id);
        repo.deleteById(id);
    }

    private StatDto toDto(Stat s) {
        return new StatDto(s.getId(), s.getStatValue(), s.getSuffix(), s.getLabel(), s.getSubLabel(), s.getDisplayOrder());
    }
}
