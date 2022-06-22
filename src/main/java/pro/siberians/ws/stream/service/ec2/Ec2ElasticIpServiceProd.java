package pro.siberians.ws.stream.service.ec2;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AllocateAddressRequest;
import com.amazonaws.services.ec2.model.AllocateAddressResult;
import com.amazonaws.services.ec2.model.AssociateAddressRequest;
import com.amazonaws.services.ec2.model.AssociateAddressResult;
import com.amazonaws.services.ec2.model.DisassociateAddressRequest;
import com.amazonaws.services.ec2.model.DomainType;
import com.amazonaws.services.ec2.model.ReleaseAddressRequest;
import com.amazonaws.services.ec2.model.TagSpecification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
public class Ec2ElasticIpServiceProd implements Ec2ElasticIpService {

    private Logger log = LoggerFactory.getLogger(Ec2ElasticIpServiceProd.class);

	@Autowired private AmazonEC2 ec2;

	public IpAllocation allocateElasticIpAddress(Map<String, String> tags) {
		log.info("Allocating new elastic IP address");
		TagSpecification tagSpec = Ec2TagSpec.getTagSpec("elastic-ip", tags);
		AllocateAddressRequest request = new AllocateAddressRequest()
			.withDomain(DomainType.Vpc)
			.withTagSpecifications(tagSpec);
		AllocateAddressResult response = ec2.allocateAddress(request);
		IpAllocation ipAllocation = new IpAllocation(response.getAllocationId(), response.getPublicIp());
		log.info("New elastic IP address {} has been allocated at {}", ipAllocation.awsIpAllocAddr, ipAllocation.awsIpAllocId);
		return ipAllocation;
	}

	public void releaseElasticIpAddress(String awsIpAllocId) {
		log.info("Releasing elastic IP address");
		ReleaseAddressRequest request = new ReleaseAddressRequest().withAllocationId(awsIpAllocId);
		ec2.releaseAddress(request);
	}

	public String associateElasticIpAddress(String awsIpAllocId, String awsInstId) {
		log.info("Associating elastic IP address allocation {} with EC2 instance {}", awsIpAllocId, awsInstId);
		AssociateAddressRequest request = new AssociateAddressRequest()
			.withInstanceId(awsInstId)
			.withAllocationId(awsIpAllocId);
		AssociateAddressResult response = ec2.associateAddress(request);
		String associationId = response.getAssociationId();
		log.info("Elastic IP address {} has been associated at {}", awsIpAllocId, associationId);
		return associationId;
	}

	public void disassociateElasticIpAddress(String awsIpAssocId, String awsInstId) {
		log.info("Disassociating elastic IP address association {} from EC2 instance {}", awsIpAssocId, awsInstId);
		DisassociateAddressRequest request = new DisassociateAddressRequest().withAssociationId(awsIpAssocId);
		ec2.disassociateAddress(request);
	}
}
