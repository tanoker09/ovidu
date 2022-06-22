package pro.siberians.ws.stream.service.ec2;

import java.util.List;
import java.util.Map;

public interface Ec2DescribeService {
	Map<String, String> getInstanceStates(List<String> instanceIds);
}
