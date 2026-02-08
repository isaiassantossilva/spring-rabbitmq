package com.santos.spring_rabbitmq.mapper;

import com.santos.spring_rabbitmq.dto.EmailDTO;
import com.santos.spring_rabbitmq.entity.EmailEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmailMapper {

    EmailEntity toEmailEntity(EmailDTO emailDTO);
}
