package pro.siberians.ws.stream.service.ec2;

import java.io.IOException;
import java.util.Map;
import java.util.Arrays;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.util.IOUtils;
import com.amazonaws.waiters.Waiter;
import com.amazonaws.waiters.WaiterParameters;
import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
public class Ec2RunInstServiceProd implements Ec2RunInstService {

	private Logger log = LoggerFactory.getLogger(Ec2RunInstServiceProd.class);

	@Autowired private AmazonEC2 ec2;

	@Value("${aws.openvidu.sshkeyname}") private String sshKeyName;
	@Value("${aws.openvidu.secgroupid}") private String secGroupId;
	@Value("${aws.openvidu.subnetid}") private String subnetId;
	@Value("${aws.openvidu.imageid}") private String imageId;
	@Value("classpath:static/ec2-openvidu-init.sh") private Resource initScriptRes;

	public String createEc2Instance(
		String awsInstType,
		String awsDnsDomain,
		String openViduSecret,
		Map<String, String> tags
	) throws Exception {
		log.info("Creating new EC2 instance with type {}", awsInstType);

		InstanceNetworkInterfaceSpecification netIfSpec = new InstanceNetworkInterfaceSpecification()
			.withSubnetId(subnetId)
			.withAssociatePublicIpAddress(true)
			.withGroups(secGroupId)
			.withDeviceIndex(0)
			.withDeleteOnTermination(true);

		EbsBlockDevice ebsCfg = new EbsBlockDevice()
			.withVolumeSize(100)
			.withDeleteOnTermination(true);

		BlockDeviceMapping blkDevMap = new BlockDeviceMapping()
			.withDeviceName("/dev/sda1")
			.withEbs(ebsCfg);

		String initScript = getInitScriptBase64(awsDnsDomain, openViduSecret);
		TagSpecification tagSpec = Ec2TagSpec.getTagSpec("instance", tags);
		RunInstancesRequest runInstReq = new RunInstancesRequest()
			.withMinCount(1)
			.withMaxCount(1)
			.withImageId(imageId)
			.withInstanceType(InstanceType.fromValue(awsInstType))
			.withNetworkInterfaces(Arrays.asList(netIfSpec))
			.withBlockDeviceMappings(Arrays.asList(blkDevMap))
			.withKeyName(sshKeyName)
			.withUserData(initScript)
			.withTagSpecifications(tagSpec);

		RunInstancesResult runInstResp = ec2.runInstances(runInstReq);
		String newInstId = runInstResp.getReservation().getInstances().get(0).getInstanceId();

		log.info("New EC2 instance {} has been created", newInstId);

		return newInstId;
	}

	public void terminateEc2Instance(String awsInstId) {
		log.info("Terminating EC2 instance {}", awsInstId);
		TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(awsInstId);
		ec2.terminateInstances(request);
	}

	public void waitUntilEc2InstanceRunning(String instanceId) {
		DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceId);
		Waiter<DescribeInstancesRequest> waiter = ec2.waiters().instanceRunning();
		WaiterParameters<DescribeInstancesRequest> params = new WaiterParameters<DescribeInstancesRequest>(request);
		waiter.run(params);
	}

	public void waitUntilEc2InstanceTerminated(String instanceId) {
		DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceId);
		Waiter<DescribeInstancesRequest> waiter = ec2.waiters().instanceTerminated();
		WaiterParameters<DescribeInstancesRequest> params = new WaiterParameters<DescribeInstancesRequest>(request);
		waiter.run(params);
	}

	private String getInitScriptBase64(String awsDnsDomain, String openViduSecret) throws IOException {
		String initScript = IOUtils.toString(initScriptRes.getInputStream());
		initScript = initScript.replaceFirst("(?m)^DOMAIN_OR_PUBLIC_IP=.*$", "DOMAIN_OR_PUBLIC_IP=" + awsDnsDomain);
		initScript = initScript.replaceFirst("(?m)^OPENVIDU_SECRET=.*$", "OPENVIDU_SECRET=" + openViduSecret);
		return Base64.getEncoder().encodeToString(initScript.getBytes());
	}

}
