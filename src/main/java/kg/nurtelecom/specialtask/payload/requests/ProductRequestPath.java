package kg.nurtelecom.specialtask.payload.requests;

import kg.nurtelecom.specialtask.annotation.annotations.RequestType;
import kg.nurtelecom.specialtask.annotation.enums.TypeOfDataRequest;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public record ProductRequestPath(
        @RequestType(value = TypeOfDataRequest.BODY, key = "username")
        String name,

        @RequestType(value = TypeOfDataRequest.SESSION, key = "description")
        String description,

        @RequestType(value = TypeOfDataRequest.PATH, key = "quantity")
        Integer quantity,

        @RequestType(value = TypeOfDataRequest.COOKIE, key = "userId")
        UUID userId,

        @RequestType(value = TypeOfDataRequest.HEADER, key = "X-Custom-Header")
        String httpHeader,

        @RequestType(value = TypeOfDataRequest.MULTIPART, key = "file")
        MultipartFile multipartFile,

        @RequestType(value = TypeOfDataRequest.ATTRIBUTE, key = "customAttribute")
        String customAttribute
) {}
