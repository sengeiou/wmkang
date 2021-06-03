package wmkang.domain.enums;


import com.fasterxml.jackson.annotation.JsonValue;


public interface Symbolic {


    /**
     * Json <-> Symbol 상호 변환
     */
    @JsonValue
    String getSymbol();

}