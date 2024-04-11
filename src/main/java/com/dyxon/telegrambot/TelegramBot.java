package com.dyxon.telegrambot;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final Map<Long, ComplainingMessage> chatStates = new HashMap<>();
    private static final ZoneId ZONE_ID_UTC_5 = ZoneId.of("UTC+5");

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.equals("/start")) {
                startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
            } else {
                handleComplaint(chatId, messageText);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("initiateComplaint")) {
                initiateComplaint(chatId);
            }
        }
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Здравствуйте, " + name + "!" + "\n" +
                "Если у вас появились жалобы по отношению к нашему клубу," + "\n" +
                "вы можете отправить их, нажав кнопку \"Создать обращение\"." + "\n" +  "\n" +
                "Не беспокойтесь, все сообщения анонимны. Но мы обязательно услышим вас и пойдем к вам на встречу.";
        sendInlineKeyboardMessage(chatId, answer, true);
    }

    private void initiateComplaint(Long chatId) {
        chatStates.put(chatId, new ComplainingMessage());
        sendMessage(chatId, "Подскажите, кто из персонала задействован в данной проблеме:", false);
    }

    private void handleComplaint(Long chatId, String message) {
        ComplainingMessage complaint = chatStates.get(chatId);
        if (complaint != null) {
            if (complaint.getPosition() == null) {
                complaint.setPosition(message);
                complaint.setZonedDateTime(ZonedDateTime.now());
                askForHappenedTime(chatId);
            } else if (complaint.getHappenedTime() == null) {
                complaint.setHappenedTime(message);
                askForDescription(chatId);
            } else if (complaint.getMessage() == null) {
                complaint.setMessage(message);
                try {
                    sendComplaint(chatId, complaint);
                    sendMessage(chatId, "Успешно отправлено, Спасибо! Вы помогаете делать наш сервис лучше!\n"
                            + "Если вы захотите снова к нам обратиться, нажимайте на кнопку ниже", true);
                    chatStates.remove(chatId);
                } catch (TelegramApiException e) {
                    sendMessage(chatId, "Произошла ошибка при отправке жалобы.", true);
                }
            }
        }
    }

    private void askForHappenedTime(Long chatId) {
        sendMessage(chatId, "Когда это произошло?", false);
    }

    private void askForDescription(Long chatId) {
        sendMessage(chatId, "Пожалуйста, опишите саму проблему:", false);
    }

    private void sendMessage(Long chatId, String textToSend, boolean showComplaintButton) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        if (showComplaintButton) {
            sendInlineKeyboardMessage(chatId, textToSend, true);
        } else {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                sendMessage(chatId, "Произошла ошибка при отправке жалобы.", true);
            }
        }
    }

    private void sendComplaint(Long chatId, ComplainingMessage complaint) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(botConfig.getAdminId());
        sendMessage.setText(formatComplaintMessage(complaint));
        execute(sendMessage);
    }

    private String formatComplaintMessage(ComplainingMessage complaint) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        ZonedDateTime utc5DateTime = complaint.getZonedDateTime().withZoneSameInstant(ZONE_ID_UTC_5);
        String formattedDateTime = utc5DateTime.format(formatter);
        return
                        "Новое обращение!" + "\n" + "\n"+
                        "Время отправки: " + formattedDateTime  + "\n" +
                        "Время происшествия: " + complaint.getHappenedTime()+ "\n"  +
                        "Сотрудники: " + complaint.getPosition() + "\n" +
                        "Жалоба: " + complaint.getMessage();
    }

    private void sendInlineKeyboardMessage(Long chatId, String textToSend, boolean showComplaintButton) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        if (showComplaintButton) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Создать обращение");
            inlineKeyboardButton.setCallbackData("initiateComplaint");
            row.add(inlineKeyboardButton);
            keyboard.add(row);
            inlineKeyboardMarkup.setKeyboard(keyboard);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            sendMessage(chatId, "Произошла ошибка при отправке сообщения.", true);
        }
    }
}
