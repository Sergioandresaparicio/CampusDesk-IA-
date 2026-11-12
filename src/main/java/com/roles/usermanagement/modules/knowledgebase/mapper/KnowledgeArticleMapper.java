package com.roles.usermanagement.modules.knowledgebase.mapper;

import com.roles.usermanagement.modules.knowledgebase.dto.KnowledgeArticleCreateDto;
import com.roles.usermanagement.modules.knowledgebase.dto.KnowledgeArticleDto;
import com.roles.usermanagement.modules.knowledgebase.entity.KnowledgeArticle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KnowledgeArticleMapper {

    KnowledgeArticleDto toDto(KnowledgeArticle article);

    List<KnowledgeArticleDto> toDtoList(List<KnowledgeArticle> articles);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "embeddingId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    KnowledgeArticle toEntity(KnowledgeArticleCreateDto dto);
}
