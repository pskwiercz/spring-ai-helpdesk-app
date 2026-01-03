package com.pskwiercz.app.helper;

import com.pskwiercz.app.dto.ChatEntryDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

@Slf4j
@Getter
@Setter
public class UserInfoHelper {
    public static UserInfo extractUserInformationFromChatHistory(List<ChatEntryDTO> history) {
        if (history == null || history.isEmpty()) {
            return new UserInfo(null, null);
        }

        Optional<String> emailAddress = history.stream()
                .filter(entry -> "user".equalsIgnoreCase(entry.role()))
                .map(ChatEntryDTO::content)
                .filter(content -> content != null && !content.isBlank())
                .map(UserInfoHelper::extractEmailAddress)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();


        Optional<String> phoneNumber = history.stream()
                .filter(entry -> "user".equalsIgnoreCase(entry.role()))
                .map(ChatEntryDTO::content)
                .filter(content -> content != null && !content.isBlank())
                .map(UserInfoHelper::extractPhoneNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();


        return new UserInfo(emailAddress.orElse(null), phoneNumber.orElse(null));
    }


    private static Optional<String> extractEmailAddress(String emailText) {
        return Optional.ofNullable(emailText)
                .filter(s -> !s.isEmpty())
                .flatMap(t -> {
                    Matcher matcher = RegPattern.EMAIL_PATTERN.matcher(t);
                    return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
                });
    }

    private static Optional<String> extractPhoneNumber(String phoneText) {
        return Optional.ofNullable(phoneText)
                .filter(s -> !s.isEmpty())
                .flatMap(t -> {
                    Matcher matcher = RegPattern.PHONE_PATTERN.matcher(t);
                    return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
                });
    }
}
