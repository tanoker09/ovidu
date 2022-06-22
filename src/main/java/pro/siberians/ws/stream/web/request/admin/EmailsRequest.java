package pro.siberians.ws.stream.web.request.admin;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pro.siberians.ws.stream.exception.InvalidEmailException;

public class EmailsRequest {

    private List<String> emails;

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]+$", Pattern.CASE_INSENSITIVE);

    public static boolean validEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.matches();
    }

    public List<String> getValidatedEmails() throws InvalidEmailException {
        for (String email : emails) {
            if (!validEmail(email)) throw new InvalidEmailException("Invalid email: " + email);
        }
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

}
