package pro.siberians.ws.stream.tasks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import pro.siberians.ws.stream.model.ContestModel;
import pro.siberians.ws.stream.service.ContestService;
import pro.siberians.ws.stream.service.ec2.*;
import pro.siberians.ws.stream.service.route53.*;

@Service
public class CreateContestOpenViduInstTask {

	private Logger log = LoggerFactory.getLogger(CreateContestOpenViduInstTask.class);

	@Autowired ContestService contestService;

	@Autowired Ec2RunInstService ec2RunInstService;
	@Autowired Ec2ElasticIpService ec2ElasticIpService;
	@Autowired Route53Service route53Service;

	@Value("${aws.openvidu.projtag}") private String projTag;
	@Value("${aws.openvidu.envtag}") private String envTag;
	@Value("${aws.openvidu.ownertag}") private String ownerTag;
	@Value("${aws.openvidu.domaintpl}") private String domainTpl;

	@Async
	public void createOpenViduInstAsync(String contestGuid) throws Exception {
		log.info("Creating and configuring AWS components for contest {}", contestGuid);
		ContestModel contest = contestService.findByGuidOrThrow(contestGuid);
		try {

			Ec2ElasticIpService.IpAllocation ipAllocation = allocateNewElasticIp(contestGuid);

			contestService.setAwsIpAllocation(
				contest,
				ipAllocation.awsIpAllocId,
				ipAllocation.awsIpAllocAddr
			);

			String awsInstType = contest.getAwsInstType();
			String awsDnsDomain = getAwsDnsDomain(contestGuid);
			String openViduSecret = getOpenViduSecret(8);

			contestService.setOpenViduSecret(contest, openViduSecret);

			String awsInstId = runNewEc2Instance(
				contestGuid,
				awsInstType,
				awsDnsDomain,
				openViduSecret
			);

			contestService.setAwsInstance(contest, awsInstId);

			ec2RunInstService.waitUntilEc2InstanceRunning(awsInstId);

			String awsIpAssocId = ec2ElasticIpService.associateElasticIpAddress(
				ipAllocation.awsIpAllocId, awsInstId
			);

			contestService.setAwsIpAssociation(contest, awsIpAssocId);

			route53Service.createDomainRecord(awsDnsDomain, ipAllocation.awsIpAllocAddr);

			contestService.setDnsRecord(contest, awsDnsDomain);

		} catch (Exception e) {
			contestService.setAwsLastError(contest, e.getMessage());
			throw e;
		}
	}

	private Ec2ElasticIpService.IpAllocation allocateNewElasticIp(String contestGuid) throws Exception {
		Map<String, String> ipAllocTags = getIpAllocTags(contestGuid);
		return ec2ElasticIpService.allocateElasticIpAddress(ipAllocTags);
	}

	private String runNewEc2Instance(
		String contestGuid,
		String awsInstType,
		String awsDnsDomain,
		String openViduSecret
	) throws Exception {
		Map<String, String> ec2InstTags = getEc2InstTags(contestGuid);
		return ec2RunInstService.createEc2Instance(
			awsInstType,
			awsDnsDomain,
			openViduSecret,
			ec2InstTags
		);
	}

	private Map<String, String> getIpAllocTags(String contestGuid) {
		Map<String, String> ipAllocTags = new HashMap<String, String>();
		ipAllocTags.put("Name", "contest-streamer-openvidu-" + contestGuid);
		ipAllocTags.put("Project", projTag);
		ipAllocTags.put("Environment", envTag);
		ipAllocTags.put("Owner", ownerTag);
		return ipAllocTags;
	}

	private Map<String, String> getEc2InstTags(String contestGuid) {
		Map<String, String> ec2InstTags = new HashMap<String, String>();
		ec2InstTags.put("Name", "contest-streamer-openvidu-" + contestGuid);
		ec2InstTags.put("Project", projTag);
		ec2InstTags.put("Environment", envTag);
		ec2InstTags.put("Owner", ownerTag);
		return ec2InstTags;
	}

	private String getAwsDnsDomain(String contestGuid) {
		return domainTpl.replaceFirst("\\{\\}", contestGuid);
	}

	private String getOpenViduSecret(Integer length) {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, length);
	}

}
