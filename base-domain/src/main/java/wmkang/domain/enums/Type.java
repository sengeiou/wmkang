package wmkang.domain.enums;


import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Type implements Symbolic {


    OBJECT  ("O"),
    CLASS   ("C");


    private String symbol;


    @Override
    public String getSymbol() {
        return symbol;
    }
}
