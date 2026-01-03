package com.pskwiercz.app.helper;

import com.pskwiercz.app.dto.ChatEntryDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static com.pskwiercz.app.helper.RegPattern.*;

@Slf4j
@Getter
@Setter
public class CustomerInfoHelper {
    private String emailAddress;
    private String phoneNumber;
    private String orderNumber;

    public static CustomerInfo extractUserInformationFromChatHistory(List<ChatEntryDTO> history) {
        if (history == null || history.isEmpty()) {
            return new CustomerInfo(null, null, null);
        }

        Optional<String> emailAddress = history.stream()
                .filter(entry -> "user".equalsIgnoreCase(entry.role()))
                .map(ChatEntryDTO::content)
                .filter(content -> content != null && !content.isBlank())
                .map(CustomerInfoHelper::extractEmailAddress)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();


        Optional<String> phoneNumber = history.stream()
                .filter(entry -> "user".equalsIgnoreCase(entry.role()))
                .map(ChatEntryDTO::content)
                .filter(content -> content != null && !content.isBlank())
                .map(CustomerInfoHelper::extractPhoneNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();

        Optional<String> orderNumber = history.stream()
                .filter(entry -> "user".equalsIgnoreCase(entry.role()))
                .map(ChatEntryDTO::content)
                .filter(content -> content != null && !content.isBlank())
                .map(CustomerInfoHelper::extractOrderNumber)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();


        return new CustomerInfo(emailAddress.orElse(null), phoneNumber.orElse(null), orderNumber.orElse(null));
    }


    private static Optional<String> extractEmailAddress(String emailText) {
        return Optional.ofNullable(emailText)
                .filter(s -> !s.isEmpty())
                .flatMap(t -> {
                    Matcher matcher = EMAIL_PATTERN.matcher(t);
                    return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
                });
    }

    private static Optional<String> extractPhoneNumber(String emailText) {
        return Optional.ofNullable(emailText)
                .filter(s -> !s.isEmpty())
                .flatMap(t -> {
                    Matcher matcher = PHONE_PATTERN.matcher(t);
                    return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
                });
    }

    private static Optional<String> extractOrderNumber(String emailText) {
        return Optional.ofNullable(emailText)
                .filter(s -> !s.isEmpty())
                .flatMap(t -> {
                    Matcher matcher = ORDER_NUMBER_PATTERN.matcher(t);
                    return matcher.find() ? Optional.of(matcher.group()) : Optional.empty();
                });
    }
}