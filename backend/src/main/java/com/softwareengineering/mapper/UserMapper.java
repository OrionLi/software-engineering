package com.softwareengineering.mapper;

import com.softwareengineering.entity.User;
import com.softwareengineering.vo.UserRegisterVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registerDate", ignore = true)
    @Mapping(target = "lastModifyDate", ignore = true)
    @Mapping(target = "sex", expression = "java(userRegisterVO.getSex().charAt(0))")
    User toEntity(UserRegisterVO userRegisterVO);

}