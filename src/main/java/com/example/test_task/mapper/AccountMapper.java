package com.example.test_task.mapper;

import com.example.test_task.dto.accountData.AccountDto;
import com.example.test_task.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {

  AccountDto toDto(Account entity);

}

