package com.example.test_task.mapper;

import com.example.test_task.dto.phoneData.PhoneDataCreateDto;
import com.example.test_task.dto.phoneData.PhoneDataDto;
import com.example.test_task.entity.PhoneData;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PhoneDataMapper {

  List<PhoneDataDto> toDtoList(List<PhoneData> entities);

  PhoneDataDto toDto(PhoneData entity);

  PhoneData toEntity(PhoneDataCreateDto entity);

  PhoneData toEntity(PhoneDataDto entity);

}

