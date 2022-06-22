package pro.siberians.ws.stream.service.ec2;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class Ec2DescribeServiceDev implements Ec2DescribeService {

    private Logger log = LoggerFactory.getLogger(Ec2DescribeServiceDev.class);

	static public Map<String, String> savedStates = new HashMap<String, String>();

	public Map<String, String> getInstanceStates(List<String> instanceIds) {
		log.info("Describing EC2 instances {}", String.join(", ", instanceIds));
		Map<String, String> states = new HashMap<String, String>();
        for (String instId : instanceIds) {
			if (!savedStates.containsKey(instId)) savedStates.put(instId, "stopped");
			String instState = savedStates.get(instId);
			states.put(instId, instState);
		}
		return states;
	}
}
