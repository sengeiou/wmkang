package wmkang.domain.enums.converter;


import javax.persistence.Converter;

import wmkang.domain.enums.FileCategory;


@Converter(autoApply = true)
public class FileCategoryConverter extends AbstractSymbolicConverter<FileCategory> {
}
