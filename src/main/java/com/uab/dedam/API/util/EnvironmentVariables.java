package com.uab.dedam.API.util;

import java.util.Map;

/**
 * Created by haduart on 30/04/17.
 */
public class EnvironmentVariables {

    public static String getVariableValue(String keyName, String defaultValue) {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            if (envName.equals(keyName))
                return env.get(envName);
        }

        System.out.printf("No environment value for " + keyName + ", returning default value:" + defaultValue);
        return defaultValue;
    }
}
