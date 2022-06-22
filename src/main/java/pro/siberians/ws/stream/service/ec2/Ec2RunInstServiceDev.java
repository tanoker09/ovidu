package pro.siberians.ws.stream.service.ec2;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class Ec2RunInstServiceDev implements Ec2RunInstService {

	private Logger log = LoggerFactory.getLogger(Ec2RunInstServiceDev.class);

	public static Integer lastUsedInstId = 0;
	public static String INST_ID_PREFIX = "i-";

	public String createEc2Instance(
		String awsInstType,
		String awsDnsDomain,
		String openViduSecret,
		Map<String, String> tags
	) throws Exception {
		log.info("Creating new EC2 instance with type {}", awsInstType);
		lastUsedInstId++;
		return INST_ID_PREFIX + lastUsedInstId.toString();
	}

	public void terminateEc2Instance(String awsInstId) {
		log.info("Terminating EC2 instance {}", awsInstId);
	}

	public void waitUntilEc2InstanceRunning(String instanceId) {
		try { Thread.sleep(4000); } catch (Exception e) {}
	}

	public void waitUntilEc2InstanceTerminated(String instanceId) {
		try { Thread.sleep(4000); } catch (Exception e) {}
	}

}
