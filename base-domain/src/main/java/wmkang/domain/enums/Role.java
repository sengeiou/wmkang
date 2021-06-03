package wmkang.domain.enums;


import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;

import wmkang.domain.util.C;


public enum Role implements Symbolic {


    USER    ("U"),
    MANAGER ("M", USER),
    ADMIN   ("A", MANAGER, USER);


    private String symbol;
    private Role[] lowerRoles;


    Role(String symbol, Role... lowerRoles) {
        this.symbol = symbol;
        this.lowerRoles = lowerRoles;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public Role[] getLowerRoles() {
        return lowerRoles;
    }

    public static RoleHierarchy buildRoleHierachy() {
        StringBuilder builder = new StringBuilder();
        for (Role role : Role.values()) {
            if (role.getLowerRoles() == null)
                continue;
            for (Role lowerRole : role.getLowerRoles()) {
                builder.append(role.name()).append(" > ").append(lowerRole.name()).append(C.LF);
            }
        }
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(builder.toString());
        return hierarchy;
    }
}
