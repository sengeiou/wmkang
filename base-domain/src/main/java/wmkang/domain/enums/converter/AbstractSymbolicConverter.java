package wmkang.domain.enums.converter;


import java.lang.reflect.ParameterizedType;
import java.util.EnumSet;

import javax.persistence.AttributeConverter;

import org.springframework.core.convert.converter.Converter;

import wmkang.domain.enums.Symbolic;


/**
 * Symbolic 타입의 HTTP 파라미터 및 DB 저장/로드시, Symbol 문자로 처리하기 위한 범용 타입 컨버터
 */
public abstract class AbstractSymbolicConverter<E extends Enum<E>> implements Converter<String, E>,
                                                                              AttributeConverter<E, String> {

    private EnumSet<E> symbolicEnumSet;


    @SuppressWarnings("unchecked")
    public AbstractSymbolicConverter() {
        Class<E> parameterizedClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (!Symbolic.class.isAssignableFrom(parameterizedClass)) {
            throw new RuntimeException("Enum type should be '" + Symbolic.class.getName() + "' sub-class - " + parameterizedClass.getName());
        }
        symbolicEnumSet = EnumSet.allOf(parameterizedClass);
    }

    /**
     * HTTP Parameter -> Symbolic
     * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
     */
    @Override
    public E convert(String symbol) {
        for (E e : symbolicEnumSet) {
            if (((Symbolic) e).getSymbol().equals(symbol)) {
                return e;
            }
        }
        return null;
    }

    /**
     * Symbolic -> DB
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(E e) {
        return ((Symbolic)e).getSymbol();
    }

    /**
     * DB -> Symbolic
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public E convertToEntityAttribute(String symbol) {
        for (E e : symbolicEnumSet) {
            if (((Symbolic) e).getSymbol().equals(symbol)) {
                return e;
            }
        }
        return null;
    }
}