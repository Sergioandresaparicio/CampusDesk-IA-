package com.roles.usermanagement.modules.ai.mapper;

import com.roles.usermanagement.modules.ai.dto.AiQueryDto;
import com.roles.usermanagement.modules.ai.dto.AiRatingDto;
import com.roles.usermanagement.modules.ai.dto.AiResponseDto;
import com.roles.usermanagement.modules.ai.dto.AiResponseSourceDto;
import com.roles.usermanagement.modules.ai.entity.AiQuery;
import com.roles.usermanagement.modules.ai.entity.AiRating;
import com.roles.usermanagement.modules.ai.entity.AiResponse;
import com.roles.usermanagement.modules.ai.entity.AiResponseSource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AiInteractionMapper {

    @Mapping(target = "response", source = "response")
    AiQueryDto toQueryDto(AiQuery query);

    List<AiQueryDto> toQueryDtoList(List<AiQuery> queries);

    @Mapping(target = "sources", source = "sources")
    @Mapping(target = "rating", source = "rating")
    AiResponseDto toResponseDto(AiResponse response);

    @Mapping(target = "articleId", source = "article.id")
    @Mapping(target = "articleTitle", source = "article.title")
    AiResponseSourceDto toSourceDto(AiResponseSource source);

    List<AiResponseSourceDto> toSourceDtoList(List<AiResponseSource> sources);

    AiRatingDto toRatingDto(AiRating rating);
}
