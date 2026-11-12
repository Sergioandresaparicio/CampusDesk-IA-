package com.roles.usermanagement.modules.knowledgebase.service;

import com.roles.usermanagement.core.common.PageRequestDto;
import com.roles.usermanagement.core.common.PageResponse;
import com.roles.usermanagement.core.exception.ResourceNotFoundException;
import com.roles.usermanagement.modules.knowledgebase.dto.KnowledgeArticleCreateDto;
import com.roles.usermanagement.modules.knowledgebase.dto.KnowledgeArticleDto;
import com.roles.usermanagement.modules.knowledgebase.entity.KnowledgeArticle;
import com.roles.usermanagement.modules.knowledgebase.mapper.KnowledgeArticleMapper;
import com.roles.usermanagement.modules.knowledgebase.repository.KnowledgeArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class KnowledgeBaseService {

    private final KnowledgeArticleRepository repository;
    private final KnowledgeArticleMapper mapper;

    public KnowledgeBaseService(KnowledgeArticleRepository repository, KnowledgeArticleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public KnowledgeArticleDto createArticle(KnowledgeArticleCreateDto dto) {
        KnowledgeArticle article = mapper.toEntity(dto);
        article.setCreatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(article));
    }

    @Transactional(readOnly = true)
    public KnowledgeArticleDto getArticle(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo de conocimiento no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public PageResponse<KnowledgeArticleDto> getActiveArticles(PageRequestDto pageRequest) {
        Page<KnowledgeArticle> page = repository.findByActiveTrueAndVerifiedTrue(pageRequest.toPageable());
        return PageResponse.from(page.map(mapper::toDto));
    }

    @Transactional(readOnly = true)
    public List<KnowledgeArticleDto> search(String query) {
        if (query == null || query.isBlank()) {
            return mapper.toDtoList(repository.findByActiveTrueAndVerifiedTrue());
        }
        return mapper.toDtoList(repository.searchByKeyword(query.trim()));
    }

    @Transactional
    public KnowledgeArticleDto toggleActive(Long id, boolean active) {
        KnowledgeArticle article = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artículo no encontrado: " + id));
        article.setActive(active);
        article.setUpdatedAt(LocalDateTime.now());
        return mapper.toDto(repository.save(article));
    }
}
