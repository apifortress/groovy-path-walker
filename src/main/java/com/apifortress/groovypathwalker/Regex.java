package com.apifortress.groovypathwalker;

public class Regex {

    public static final String REGEX_FUNC = "\\w*\\(\\d?\\)";
    public static final String REGEX_VAR = "\\$\\D*\\$";
    public static final String REGEX_LIST = "\\w*\\[\\d*\\]";
    public static final String REGEX_SQUARE_BRACKETS_SINGLE_QUOTE = "\\w*\\[\'\\w*\'\\]";

    public static final String START_FUNC = "(";
    public static final String START_VAR = "$";
    public static final String START_LIST = "[";

    public static final String END_FUNC = ")";
    public static final String END_VAR = "$";
    public static final String END_LIST = "]";

    public static final String NORMALIZED_PATH_DOUBLE_QUOTES = "\\[\"(.*?)\"\\]";
    //public static final String NORMALIZED_PATH_SINGLE_QUOTES = "\\[\'(.*?)\'\\]";
    public static final String NORMALIZED_PATH_VARIABLE = "\\[([a-z]*?)\\]";
    public static final String NORMALIZED_PATH_QUESTIONE_MARK = "\\?";

    public static final String REGEX_UNSUPPORTED_BRACES = "\\{.*?\\}";
    public static final String REGEX_UNSUPPORTED_STARTS = "\\*";
    public static final String REGEX_UNSUPPORTED_OPERATOR = ".*?\\->.*?";
    public static final String REGEX_UNSUPPORTED_EXCLAMATION_MARK = ".*?\\!.*?";
    public static final String REGEX_UNSUPPORTED_ASSIGNEMENT_OPERATOR = ".*?\\=.*?";

}
