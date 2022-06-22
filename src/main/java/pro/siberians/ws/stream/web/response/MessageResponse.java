package pro.siberians.ws.stream.web.response;

public class MessageResponse {

	private String message;

	public MessageResponse(String message) {
		this.message = message;
	}

	public MessageResponse(Exception error) {
		this.message = error.getMessage();
	}

	public String getMessage() {
		return message;
	}

}
