package wmkang.domain.enums;


import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Switch implements Symbolic {


    ON("O"), OFF("F");


    private String symbol;


    @Override
    public String getSymbol() {
        return symbol;
    }
}
