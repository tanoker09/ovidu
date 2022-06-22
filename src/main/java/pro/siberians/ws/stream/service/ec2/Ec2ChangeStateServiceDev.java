package pro.siberians.ws.stream.service.ec2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class Ec2ChangeStateServiceDev implements Ec2ChangeStateService {

	private Logger log = LoggerFactory.getLogger(Ec2ChangeStateServiceDev.class);

	public void startEc2Instance(String instanceId) {
		log.info("Starting EC2 instance {}", instanceId);
		Ec2DescribeServiceDev.savedStates.put(instanceId, "running");
	}

	public void stopEc2Instance(String instanceId) {
		log.info("Stopping EC2 instance {}", instanceId);
		Ec2DescribeServiceDev.savedStates.put(instanceId, "stopped");
	}

	public void rebootEc2Instance(String instanceId) {
		log.info("Rebooting EC2 instance {}", instanceId);
		Ec2DescribeServiceDev.savedStates.put(instanceId, "pending");
	}

}
