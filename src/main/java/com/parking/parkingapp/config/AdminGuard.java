package com.parking.parkingapp.config;
import com.parking.parkingapp.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component("adminGuard")
@RequiredArgsConstructor
public class AdminGuard {

    @Value("${simulator.allowed-email}")
    private String allowedEmail;

    @Value("${simulator.secret-header-name}")
    private String secretHeaderName;

    @Value("${simulator.secret-header-value}")
    private String secretHeaderValue;

    public void enforce(HttpServletRequest req) {
        String email = SecurityUtils.currentEmail();
        if (email == null || !email.equalsIgnoreCase(allowedEmail)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin email required");
        }

        if (secretHeaderName == null || secretHeaderName.isBlank()
                || secretHeaderValue == null || secretHeaderValue.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin header not configured");
        }

        String got = req.getHeader(secretHeaderName);
        if (got == null || !got.equals(secretHeaderValue)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid admin secret");
        }
    }

    public boolean isAllowed(HttpServletRequest req) {
        try {
            enforce(req);
            return true;
        } catch (ResponseStatusException ex) {
            return false;
        }
    }
}
