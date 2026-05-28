package util;

public final class RoleUtil {

    private RoleUtil() {
    }

    public static String normalize(String role) {
        if (role == null) {
            return null;
        }
        return role.trim().toUpperCase();
    }

    public static boolean isAdmin(String role) {
        return "ADMIN".equals(normalize(role));
    }

    public static boolean isLibrarian(String role) {
        return "LIBRARIAN".equals(normalize(role));
    }

    public static boolean isEmployee(String role) {
        return isAdmin(role) || isLibrarian(role);
    }
}
