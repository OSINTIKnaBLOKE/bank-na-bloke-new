package TerminalBankVZ;

/**
 * Недостаточно средст
 * неверная сумма
 * */

class InsufficientFindException extends Exception{
    public InsufficientFindException(String message) {
        super(message);
    }
}
