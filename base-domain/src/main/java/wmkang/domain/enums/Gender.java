package wmkang.domain.enums;


import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Gender implements Symbolic {


    MALE    ("M"),
    FEMALE  ("F");


    private String symbol;


    @Override
    public String getSymbol() {
        return symbol;
    }
}