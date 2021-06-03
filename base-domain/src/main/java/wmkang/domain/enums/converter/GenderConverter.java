package wmkang.domain.enums.converter;


import javax.persistence.Converter;

import wmkang.domain.enums.Gender;


@Converter(autoApply = true)
public class GenderConverter extends AbstractSymbolicConverter<Gender> {
}