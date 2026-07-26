package com.TashchatGPT;
import java.util.Scanner;

public class AntiSpamFIlter {
    static void main() {
        Scanner scanner = new Scanner(System.in);
        String[] blackList = {"Реклама", "Казино", "Акции", "Спам"};
        int totalMessage = 0;
        int spamMessage = 0;

        while (true){
            System.out.print("Введите текст(Если хотите выйти напишите выход): ");
            String inputMessage = scanner.nextLine();
            String[] wordSum = inputMessage.split(" ");
            totalMessage++;

            if(inSpam(blackList, inputMessage, spamMessage)){
                spamMessage++;
                String censured = censorMessage(inputMessage, blackList);
                System.out.println(censured);
            } else if (inputMessage.equalsIgnoreCase("Выход")) {
                System.out.println("Досвидание");
                System.out.println(totalMessage + " " + spamMessage);
                break;
            } else{
                continue;
            }
        }
        scanner.close();
    }
    public static boolean inSpam(String[] blackList, String text, int sumSpam){
        String[] mess = text.split(" ");
        for (int i = 0; i < mess.length; i++) {
            for (int j = 0; j < blackList.length; j++) {
                if(blackList[j].equalsIgnoreCase(mess[i])){
                    System.out.println("Найдено запрещеное слово");
                    mess[i].replace(mess[i], "*" );
                    return true;
                }
            }
        }
        return false;
    }
    public static String censorMessage(String text, String[] blackList){
        String result = text;
        for(String spamWord : blackList){
            result = result.replace(spamWord, "***");
        }
        return result;
    }
}
