package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    private static final int workload = 12;

    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(workload); // Gera um salt aleatório
        return BCrypt.hashpw(plainPassword, salt); // Gera o hash da senha com o salt
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Hash fornecido inválido.");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword); // Verifica se a senha bate com o hash armazenado
    }
}