package wmkang.common.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;


/**
 * 사업자번호 유효성 검증
 * 검증 규칙 : https://gs.saro.me/dev?page=6&tn=8
 */
public class BusinessNumberValidator implements ConstraintValidator<BusinessNumberConstraint, String> {


    private final static int[] AUTH_KEY = {1, 3, 7, 1, 3, 7, 1, 3, 5, 1};


    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        value = value.replace("-", "");
        if (!StringUtils.isNumeric(value) || value.length() != 10) {
            return false;
        }

        int sum = 0;
        int j = -1;
        for(int i = 0; i < 9; i++) {
            j = Character.getNumericValue(value.charAt(i));
            sum += j * AUTH_KEY[i];
        }
        sum += (int) (Character.getNumericValue(value.charAt(8)) * 5 /10);
        int checkNum = (10 - sum % 10) % 10 ;
        return (checkNum == Character.getNumericValue(value.charAt(9)));
    }
}
