package pro.siberians.ws.stream.service.ec2;

import java.util.Map;

public interface Ec2ElasticIpService {

    public class IpAllocation {
		public String awsIpAllocId;
		public String awsIpAllocAddr;

		IpAllocation(String allocationId, String publicIp) {
			this.awsIpAllocId = allocationId;
			this.awsIpAllocAddr = publicIp;
		}
	}

    IpAllocation allocateElasticIpAddress(Map<String, String> tags);
    void releaseElasticIpAddress(String awsIpAllocId);
    String associateElasticIpAddress(String awsIpAllocId, String awsInstId);
    void disassociateElasticIpAddress(String awsIpAssocId, String awsInstId);

}
