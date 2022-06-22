package pro.siberians.ws.stream.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import pro.siberians.ws.stream.model.ContestModel;
import pro.siberians.ws.stream.service.ContestService;
import pro.siberians.ws.stream.service.ec2.*;
import pro.siberians.ws.stream.service.route53.*;

@Service
public class DeleteContestOpenViduInstTask {

	private Logger log = LoggerFactory.getLogger(DeleteContestOpenViduInstTask.class);

	@Autowired ContestService contestService;

	@Autowired Ec2RunInstService ec2RunInstService;
    @Autowired Ec2ElasticIpService ec2ElasticIpService;
    @Autowired Route53Service route53Service;

	@Async
	public void deleteOpenViduInstAsync(String contestGuid) throws Exception {
		log.info("Deleting AWS components for contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		try {

			String awsInstId = contest.getAwsInstId();
			String awsIpAssocId = contest.getAwsIpAssocId();
			ec2ElasticIpService.disassociateElasticIpAddress(awsIpAssocId, awsInstId);

			contestService.unsetAwsIpAssociation(contest);

			ec2RunInstService.terminateEc2Instance(awsInstId);

			contestService.unsetAwsInstance(contest);

			ec2RunInstService.waitUntilEc2InstanceTerminated(awsInstId);

			String awsIpAllocId = contest.getAwsIpAllocId();
			ec2ElasticIpService.releaseElasticIpAddress(awsIpAllocId);

			contestService.unsetAwsIpAllocation(contest);

			String awsDnsDomain = contest.getAwsDnsDomain();
			String awsIpAllocAddr = contest.getAwsIpAllocAddr();
			route53Service.deleteDomainRecord(awsDnsDomain, awsIpAllocAddr);

			contestService.unsetDnsRecord(contest);

		} catch (Exception e) {
			contestService.setAwsLastError(contest, e.getMessage());
			throw e;
		}
	}

}
