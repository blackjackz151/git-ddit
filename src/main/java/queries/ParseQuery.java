package queries;

import java.util.HashMap;
import java.util.Map;

class ParseQuery {

    private final Map<String, String> qualifierAndValue;

    ParseQuery(String[] query) {
        qualifierAndValue = new HashMap<>();
        parseConditions(query);
    }

    Map<String, String> getQualifierAndValue() {
        return qualifierAndValue;
    }

    private void parseConditions(String[] query) {
        for (String condition : query) {
            String[] splitCondition = condition.split(":");
            if (splitCondition.length == 2) {
                qualifierAndValue.put(splitCondition[0].toLowerCase(), splitCondition[1].toLowerCase());
            } else {
                System.out.println("Condition: " + condition + " not valid");
            }
        }
    }

}
