package cn.like.code.config.support.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
public final class ResponseWrapper {
    private static final String RESPONSE_CONTENT_TYPE = "application/json;charset=UTF-8";

    private ResponseWrapper() {
    }

    private static void preHandle(HttpServletResponse response) {
        response.setContentType(RESPONSE_CONTENT_TYPE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
    }

    private static void wrapResponse(HttpStatus httpStatus, HttpServletResponse response, String message) throws IOException {
        preHandle(response);
        response.setStatus(httpStatus.value());
        try (final PrintWriter writer = response.getWriter()) {
            writer.write(
                    new ResponseEntity<Object>(message, httpStatus).toString()
            );
            writer.flush();
        }
    }

    /**
     * Description: 包装 HttpStatus 为 200 的 Response
     *
     * @param response {@link HttpServletResponse}
     * @param message  消息
     * @return void
     * @author LiKe
     * @date 2020-05-09 10:27:59
     */
    public static void okResponse(HttpServletResponse response, String message) throws IOException {
        wrapResponse(HttpStatus.OK, response, message);
    }

    /**
     * Description: 包装 HttpStatus 为 401 的 Response
     *
     * @param response {@link HttpServletResponse}
     * @param message  消息
     * @return void
     * @author LiKe
     * @date 2020-05-08 18:31:22
     */
    public static void unauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        wrapResponse(HttpStatus.UNAUTHORIZED, response, message);
    }

    /**
     * Description: 包装 HttpStatus 为 403 的 Response
     *
     * @param response {@link HttpServletResponse}
     * @param message  消息
     * @return void
     * @author LiKe
     * @date 2020-05-08 18:31:09
     */
    public static void forbiddenResponse(HttpServletResponse response, String message) throws IOException {
        wrapResponse(HttpStatus.FORBIDDEN, response, message);
    }

    public static void redirectResponse(HttpServletResponse response, String uri) throws IOException {
        response.setHeader("Location", "/authorization-server/login");
        wrapResponse(HttpStatus.FOUND, response, HttpStatus.FOUND.getReasonPhrase());
    }

}