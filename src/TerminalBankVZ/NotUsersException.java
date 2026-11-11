package TerminalBankVZ;

/**
 * Если польщователя не существует
 * Или написан с ошибкой
 * */

public class NotUsersException extends Exception {
    public NotUsersException(String message) {
        super(message);
    }
}
