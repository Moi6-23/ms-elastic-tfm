package com.parking.parkingapp.config;

import com.parking.parkingapp.config.AdminAccessProperties;
import com.parking.parkingapp.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component("adminGuard")
@RequiredArgsConstructor
public class AdminGuard {
    private final AdminAccessProperties props;

    public void enforce(HttpServletRequest req) {
        String email = SecurityUtils.currentEmail();
        if (email == null || !email.equalsIgnoreCase(props.allowedEmail())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin email required");
        }
        String headerName = props.secretHeaderName();
        String expected = props.secretHeaderValue();
        if (headerName == null || headerName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin header not configured");
        }
        String got = req.getHeader(headerName);
        if (got == null || !got.equals(expected)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid admin secret");
        }
    }

    public boolean isAllowed(HttpServletRequest req) {
        try { enforce(req); return true; } catch (ResponseStatusException ex) { return false; }
    }
}