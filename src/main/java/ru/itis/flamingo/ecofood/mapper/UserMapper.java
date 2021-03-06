package ru.itis.flamingo.ecofood.mapper;

import org.mapstruct.Mapper;
import ru.itis.flamingo.ecofood.domain.dto.CommonSellerDto;
import ru.itis.flamingo.ecofood.domain.dto.SignUpUserDto;
import ru.itis.flamingo.ecofood.domain.dto.SimpleUserDto;
import ru.itis.flamingo.ecofood.domain.dto.UserDto;
import ru.itis.flamingo.ecofood.domain.entity.User;

@Mapper(componentModel = "spring", uses = {BuyMapper.class})
public interface UserMapper {

    UserDto mapToDto(User entity);

    User mapToEntity(UserDto dto);

    User mapToEntity(SignUpUserDto dto);

    CommonSellerDto mapToCommonSellerDto(User entity);

    SimpleUserDto mapToSimpleUser(User user);

}
