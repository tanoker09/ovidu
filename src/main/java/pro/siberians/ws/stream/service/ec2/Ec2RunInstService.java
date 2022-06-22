package pro.siberians.ws.stream.service.ec2;

import java.util.Map;

public interface Ec2RunInstService {

	String createEc2Instance(
		String awsInstType,
		String awsDnsDomain,
		String openViduSecret,
		Map<String, String> tags
	) throws Exception;

	void terminateEc2Instance(String awsInstId);
	void waitUntilEc2InstanceRunning(String instanceId);
	void waitUntilEc2InstanceTerminated(String instanceId);

}
