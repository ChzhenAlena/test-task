package com.example.test_task.mapper;

import com.example.test_task.dto.emailData.EmailDataCreateDto;
import com.example.test_task.dto.emailData.EmailDataDto;
import com.example.test_task.entity.EmailData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmailDataMapper {

  List<EmailDataDto> toDtoList(List<EmailData> entities);

  EmailDataDto toDto(EmailData entity);

  EmailData toEntity(EmailDataCreateDto entity);

  EmailData toEntity(EmailDataDto entity);

}

