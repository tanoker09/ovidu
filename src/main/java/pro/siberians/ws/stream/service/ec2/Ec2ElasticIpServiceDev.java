package pro.siberians.ws.stream.service.ec2;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class Ec2ElasticIpServiceDev implements Ec2ElasticIpService {

    private Logger log = LoggerFactory.getLogger(Ec2ElasticIpServiceDev.class);

	public static Integer lastUsedAllocId = 0;
	public static String ALLOC_ID_PREFIX = "eipalloc-";
	public static String ALLOC_ADDR_PREFIX = "10.0.0.";

	public static Integer lastUsedAssocId = 0;
	public static String ASSOC_ID_PREFIX = "eipassoc-";

	public IpAllocation allocateElasticIpAddress(Map<String, String> tags) {
		log.info("Allocating new elastic IP address");
		lastUsedAllocId++;
		return new IpAllocation(
			ALLOC_ID_PREFIX + lastUsedAllocId.toString(),
			ALLOC_ADDR_PREFIX + lastUsedAllocId.toString()
		);
	}

	public void releaseElasticIpAddress(String awsIpAllocId) {
		log.info("Releasing elastic IP address");
	}

	public String associateElasticIpAddress(String awsIpAllocId, String awsInstId) {
		log.info("Associating elastic IP address allocation {} with EC2 instance {}", awsIpAllocId, awsInstId);
		lastUsedAssocId++;
		return ASSOC_ID_PREFIX + lastUsedAssocId;
	}

	public void disassociateElasticIpAddress(String awsIpAssocId, String awsInstId) {
		log.info("Disassociating elastic IP address association {} from EC2 instance {}", awsIpAssocId, awsInstId);
	}

}
