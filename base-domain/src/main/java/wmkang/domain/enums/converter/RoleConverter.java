package wmkang.domain.enums.converter;


import javax.persistence.Converter;

import wmkang.domain.enums.Role;


@Converter(autoApply = true)
public class RoleConverter extends AbstractSymbolicConverter<Role> {
}
