package wmkang.domain.enums.converter;


import javax.persistence.Converter;

import wmkang.domain.enums.ActionType;


@Converter(autoApply = true)
public class ActionTypeConverter extends AbstractSymbolicConverter<ActionType> {
}
