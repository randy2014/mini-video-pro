package com.video.entitlement.common.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.regex.Pattern;

public final class UrlValidator {
    private static final Set<String> ALLOWED_SCHEMES = Set.of("https");
    private static final Set<String> BLOCKED_SCHEMES = Set.of("javascript", "file", "data", "content");
    private static final Pattern HOST_PATTERN = Pattern.compile("^[a-zA-Z0-9]([a-zA-Z0-9\\-]*[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]*[a-zA-Z0-9])?)*$");

    private UrlValidator() {}

    public static boolean isValidHttpsUrl(String url) {
        try {
            URI uri = new URI(url);
            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                return false;
            }
            if (BLOCKED_SCHEMES.contains(uri.getScheme().toLowerCase())) {
                return false;
            }
            String host = uri.getHost();
            if (host == null || host.isEmpty()) {
                return false;
            }
            return HOST_PATTERN.matcher(host).matches();
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public static String extractHost(String url) {
        try {
            return new URI(url).getHost();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static String extractScheme(String url) {
        try {
            return new URI(url).getScheme();
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static String normalizeUrl(String url) {
        try {
            URI uri = new URI(url);
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null).toString();
        } catch (URISyntaxException e) {
            return url;
        }
    }
}
