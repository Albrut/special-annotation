package kg.nurtelecom.specialtask.annotation.resolver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kg.nurtelecom.specialtask.annotation.annotations.BindRequest;
import kg.nurtelecom.specialtask.annotation.annotations.RequestType;
import kg.nurtelecom.specialtask.annotation.enums.TypeOfDataRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * A custom Spring MVC HandlerMethodArgumentResolver that binds method parameters
 * annotated with {@link BindRequest} to values extracted from various parts of the HTTP request.
 * <p>
 * This resolver supports binding for both Java record types and traditional JavaBeans.
 * It retrieves values from headers, parameters, path variables, attributes, cookies, session, body, and multipart data.
 * The resolver uses a {@link ConversionService} to convert raw values into the desired target type.
 * </p>
 *
 * @see BindRequest
 * @see RequestType
 * @see TypeOfDataRequest
 */
@Component
public class RequestsArgumentResolver implements HandlerMethodArgumentResolver {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Logger LOGGER = Logger.getLogger(RequestsArgumentResolver.class.getName());

    private final ConversionService conversionService;

    /**
     * Constructs a new RequestsArgumentResolver with the provided ConversionService.
     *
     * @param conversionService the ConversionService to use for converting raw values to target types.
     */
    @Autowired
    public RequestsArgumentResolver(@Lazy ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Checks if the method parameter is annotated with {@link BindRequest}.
     *
     * @param parameter the method parameter to check.
     * @return true if the parameter is annotated with {@link BindRequest}; false otherwise.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(BindRequest.class);
    }

    /**
     * Resolves the method argument by binding request data to an instance of the parameter type.
     * This method supports both record types and traditional JavaBeans.
     *
     * @param parameter   the method parameter to resolve.
     * @param mavContainer the ModelAndViewContainer for the current request.
     * @param webRequest   the current web request.
     * @param binderFactory a factory for creating WebDataBinder instances.
     * @return the resolved argument.
     * @throws Exception if binding fails due to missing data or conversion errors.
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Class<?> modelClass = parameter.getParameterType();

        if (modelClass.isRecord()) {
            RecordComponent[] components = modelClass.getRecordComponents();
            Object[] args = new Object[components.length];
            for (int i = 0; i < components.length; i++) {
                RequestType requestType = components[i].getAnnotation(RequestType.class);
                if (requestType != null) {
                    String key = requestType.key();
                    args[i] = resolveValueForType(request, requestType.value(), key, components[i].getType());
                } else {
                    throw new IllegalStateException("Missing @RequestType annotation for record component: "
                            + components[i].getName());
                }
            }
            Class<?>[] parameterTypes = new Class<?>[components.length];
            for (int i = 0; i < components.length; i++) {
                parameterTypes[i] = components[i].getType();
            }
            Constructor<?> canonicalConstructor = modelClass.getDeclaredConstructor(parameterTypes);
            canonicalConstructor.setAccessible(true);
            return canonicalConstructor.newInstance(args);
        } else {
            Object model = modelClass.getDeclaredConstructor().newInstance();
            Field[] fields = modelClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.isAnnotationPresent(RequestType.class)) {
                    RequestType requestType = field.getAnnotation(RequestType.class);
                    String key = requestType.key();
                    field.setAccessible(true);
                    Object value = resolveValueForType(request, requestType.value(), key, field.getType());
                    field.set(model, value);
                }
            }
            return model;
        }
    }

    /**
     * Resolves a value from the HttpServletRequest based on the specified source type and key,
     * and converts it to the target type using the ConversionService.
     *
     * @param request    the HttpServletRequest from which to retrieve data.
     * @param type       the source type (HEADER, PARAM, PATH, ATTRIBUTE, COOKIE, SESSION, BODY, or MULTIPART).
     * @param key        the key identifying the data.
     * @param targetType the desired target type for the data.
     * @return the resolved and converted value.
     * @throws Exception if the value is not found or conversion fails.
     */
    private Object resolveValueForType(HttpServletRequest request, TypeOfDataRequest type, String key, Class<?> targetType) throws Exception {
        LOGGER.info("Getting value for key: " + key + " with type: " + type.name());
        Object rawValue = null;
        if (type == TypeOfDataRequest.HEADER) {
            rawValue = request.getHeader(key);
        } else if (type == TypeOfDataRequest.PARAM) {
            rawValue = request.getParameter(key);
        } else if (type == TypeOfDataRequest.PATH) {
            Object pathVarsObj = request.getAttribute("org.springframework.web.servlet.HandlerMapping.uriTemplateVariables");
            if (pathVarsObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, String> pathVars = (Map<String, String>) pathVarsObj;
                rawValue = pathVars.get(key);
            }
        } else if (type == TypeOfDataRequest.ATTRIBUTE) {
            rawValue = request.getAttribute(key);
        } else if (type == TypeOfDataRequest.COOKIE) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    Cookie cookie = cookies[i];
                    if (cookie.getName().equals(key)) {
                        rawValue = convertCookieValue(cookie.getValue(), targetType);
                        break;
                    }
                }
            }
        } else if (type == TypeOfDataRequest.SESSION) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                rawValue = session.getAttribute(key);
            }
        } else if (type == TypeOfDataRequest.BODY) {
            if (request instanceof MultipartHttpServletRequest) {
                rawValue = ((MultipartHttpServletRequest) request).getParameter(key);
            } else {
                String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                if (!requestBody.isEmpty()) {
                    JsonNode rootNode = OBJECT_MAPPER.readTree(requestBody);
                    if (rootNode.has(key)) {
                        rawValue = rootNode.get(key).asText();
                    } else {
                        throw new IllegalStateException("Key '" + key + "' not found in the request body");
                    }
                }
            }
        } else if (type == TypeOfDataRequest.MULTIPART) {
            if (request instanceof MultipartHttpServletRequest) {
                MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
                if (MultipartFile.class.isAssignableFrom(targetType)) {
                    rawValue = multipartRequest.getFile(key);
                } else {
                    rawValue = multipartRequest.getParameter(key);
                }
            }
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + type);
        }
        if (rawValue == null) {
            throw new IllegalStateException("Value not found for key '" + key + "' with type "
                    + targetType.getName() + " from data source " + type);
        }
        if (!targetType.isInstance(rawValue)) {
            try {
                Object convertedValue = conversionService.convert(rawValue, targetType);
                LOGGER.info("Received and converted value: " + convertedValue);
                return convertedValue;
            } catch (Exception e) {
                throw new IllegalStateException("Failed to convert value for key '" + key + "' to type "
                        + targetType.getName(), e);
            }
        }
        LOGGER.info("Received value: " + rawValue);
        return rawValue;
    }

    /**
     * Converts the cookie value to the specified target type. If the target type is UUID,
     * the value is parsed as a UUID.
     *
     * @param value      the cookie value as a String.
     * @param targetType the target type for conversion.
     * @return the converted value.
     */
    private Object convertCookieValue(String value, Class<?> targetType) {
        if (UUID.class.isAssignableFrom(targetType)) {
            return UUID.fromString(value);
        }
        return conversionService.convert(value, targetType);
    }
}
