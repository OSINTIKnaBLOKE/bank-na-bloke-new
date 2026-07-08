package TerminalBankVZ;

public enum TransactionStatusVZ {
    STATUS_SUCCESSFULLY, // операция прошла успешно
    STATUS_ERROR, // Ошибка пользователя
    STATUS_LIMIT, // Лимит на снятие средств
    STATUS_USER_ABSENCE, // отсутствие пользователя
    INSUFFICIENT_FIND, // недостаточно средств
    INVALID_AMOUNT, // неверная сумма
    LIMIT_EXCEEDED, // превышен лимит на разовое снятие
}
