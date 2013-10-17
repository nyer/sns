package org.nyer.sns.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NumberUtil {

    private static Log log = LogFactory.getLog(NumberUtil.class);

    private static Pattern patternFloat = Pattern.compile("(\\d+\\.\\d*)|(\\d*\\.\\d+)|(\\d+)");

    private static Pattern patternDouble = patternFloat;

    private static Pattern patternInt = Pattern.compile("\\d+");

    private static Pattern patternLong = patternInt;

    private static String findFirstMatch(Pattern pattern, String string) {
        if (string == null) {
            return null;
        }
        Matcher matcher = patternFloat.matcher(string);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return null;
        }
    }

    public static float parseFloat(String string, float defaultValue, boolean tryHard) {
        if (string != null) {
            if (tryHard) {
                string = findFirstMatch(patternFloat, string);
            }
            try {
                return Float.parseFloat(string);
            } catch (NumberFormatException e) {
                log.error(string, e);
            }
        }
        return defaultValue;
    }

    public static double parseDouble(String string, double defaultValue, boolean tryHard) {
        if (string != null) {
            if (tryHard) {
                string = findFirstMatch(patternDouble, string);
            }
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                log.error(string, e);
            }
        }
        return defaultValue;
    }

    public static int parseInt(String string, int defaultValue, boolean tryHard) {
        if (string != null) {
            if (tryHard) {
                string = findFirstMatch(patternInt, string);
            }
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                log.error(string, e);
            }
        }
        return defaultValue;
    }

    public static long parseLong(String string, long defaultValue, boolean tryHard) {
        if (string != null) {
            if (tryHard) {
                string = findFirstMatch(patternLong, string);
            }
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException e) {
                log.error(string, e);
            }
        }
        return defaultValue;
    }

    public static List<Integer> getRandomNumbers(int max, int count) {
        List<Integer> resultList = new ArrayList<Integer>();
        for (int i = 0; i < count; i++) {
            int num = -1;
            while (num == -1 || resultList.contains(num) == true) {
                num = RandomUtils.nextInt(max);
            }
            resultList.add(num);
        }
        return resultList;
    }

    public static boolean isPositiveInteger(String number) {
        String regex = "^[1-9]+\\d*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(number);

        return m.matches();
    }

    public static boolean isNonzeroInteger(String number) {
        String regex = "^(-?)[1-9]+\\d*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(number);

        return m.matches();
    }
}
