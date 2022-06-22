package pro.siberians.ws.stream.authentication;

import java.util.Base64;
import java.io.UnsupportedEncodingException;

// Based on https://github.com/rrohitramsen/aws_jwt_cognito
public final class AwsCognitoJwtParser {

	public static String getHeader(String jwt) throws UnsupportedEncodingException {
		String header = jwt.split("\\.")[0];
		byte [] headerBytes = Base64.getUrlDecoder().decode(header);
		return new String(headerBytes, "UTF-8");
	}

	public static String getPayload(String jwt) throws UnsupportedEncodingException {
		String payload = jwt.split("\\.")[1];
		byte[] payloadBytes =  Base64.getUrlDecoder().decode(payload);
		return new String(payloadBytes, "UTF-8");
	}

}
