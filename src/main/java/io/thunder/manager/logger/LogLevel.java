package io.thunder.manager.logger;

public enum LogLevel {

    OFF, //Nothing will be logged
    ALL, //Everything will be logged
    WARNING, //Only warnings will be logged
    INFO, //ONly information will be logged
    DEBUG, //Only debug will be logged (Useful if the Developer tells you)
    ERROR //Only errors will be logged (Recommended)
}
