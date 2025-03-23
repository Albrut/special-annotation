package kg.nurtelecom.specialtask.annotation.enums;

/**
 * Enum representing the different types of data that can be consumed and handled by annotations.
 * The main purpose of this enum is to indicate which type of data will be used or processed.
 */
public enum TypeOfDataRequest {

    /**
     * Represents data from the HTTP request header.
     */
    HEADER,

    /**
     * Represents data from the request parameters (query parameters).
     */
    PARAM,

    /**
     * Represents data from the URL path (path variables).
     */
    PATH,

    /**
     * Represents data from the cookies in the request.
     */
    COOKIE,

    /**
     * Represents data from the request attributes.
     */
    ATTRIBUTE,

    /**
     * Represents data from the request body (e.g., JSON, XML, form-data).
     */
    BODY,

    /**
     * Represents data from the session attributes.
     */
    SESSION,

    /**
     * Represents data from multipart/form-data requests (e.g., file uploads).
     */
    MULTIPART
}