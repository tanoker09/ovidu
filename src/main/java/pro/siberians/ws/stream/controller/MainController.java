package pro.siberians.ws.stream.controller;

import org.springframework.web.bind.annotation.*;
import pro.siberians.ws.stream.web.response.MessageResponse;

@RestController
@RequestMapping("/api/main")
public class MainController {

	@RequestMapping(value = "/healthCheck", method = RequestMethod.GET)
	public MessageResponse healthCheck() {
		return new MessageResponse("Healthy!");
	}

}
