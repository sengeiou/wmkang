package wmkang.domain.enums;


import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum ActionType implements Symbolic {


    CREATE ("C"),
    READ   ("R"),
    UPDATE ("U"),
    DELETE ("D");


    private String symbol;


    @Override
    public String getSymbol() {
        return symbol;
    }
}
