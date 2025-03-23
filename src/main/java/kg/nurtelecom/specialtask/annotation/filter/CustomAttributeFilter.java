package kg.nurtelecom.specialtask.annotation.filter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.stereotype.Component;
import java.io.IOException;

/**
 * Custom filter that checks if the request contains the attribute "customAttribute".
 * If the attribute is missing, it assigns a default value.
 */

@Component
public class CustomAttributeFilter implements  Filter{

    /**
     * Intercepts incoming requests to check for the "customAttribute".
     * If not present, it assigns a default value before passing the request along the filter chain.
     *
     * @param request  the incoming servlet request
     * @param response the outgoing servlet response
     * @param chain    the filter chain to proceed with request processing
     * @throws IOException      if an I/O error occurs during filtering
     * @throws ServletException if a servlet-related error occurs during filtering
     */

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request.getAttribute("customAttribute") == null) {
            request.setAttribute("customAttribute", "DefaultCustomAttribute");
        }
        chain.doFilter(request, response);
    }
}
