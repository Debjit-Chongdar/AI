package in.ai.practice.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class MaskingSensitiveInfo implements DocumentPostProcessor {

    private final Pattern emailPattern = Pattern.compile("[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+");
    private final Pattern phonePattern = Pattern.compile("[6-9]\\d{9}");

    @Override
    public List<Document> process(Query query, List<Document> list) {
        return list.stream()
                .map(doc -> {
                    String maskedContent = maskSensitiveData(doc.getText());
                    return doc.mutate().text(maskedContent).build();
                })
                .collect(Collectors.toList());
    }

    private String maskSensitiveData(String text) {
        if (text == null) return null;

        // Mask emails
        Matcher emailMatcher = emailPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (emailMatcher.find()) {
            String email = emailMatcher.group();
            String maskedEmail = maskEmail(email);
            emailMatcher.appendReplacement(sb, maskedEmail);
        }
        emailMatcher.appendTail(sb);

        // Mask phone numbers
        Matcher phoneMatcher = phonePattern.matcher(sb.toString());
        StringBuffer sb2 = new StringBuffer();
        while (phoneMatcher.find()) {
            String phone = phoneMatcher.group();
            String maskedPhone = maskPhone(phone);
            phoneMatcher.appendReplacement(sb2, maskedPhone);
        }
        phoneMatcher.appendTail(sb2);

        return sb2.toString();
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) return "***@***";
        return email.charAt(0) + "***@" + email.substring(atIndex + 1);
    }

    private String maskPhone(String phone) {
        // Show first 2 digits, mask rest
        return phone.substring(0, 2) + "******" + phone.substring(8);
    }
}
