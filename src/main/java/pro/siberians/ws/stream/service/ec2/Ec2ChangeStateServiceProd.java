package pro.siberians.ws.stream.service.ec2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.RebootInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
public class Ec2ChangeStateServiceProd implements Ec2ChangeStateService {

	private Logger log = LoggerFactory.getLogger(Ec2ChangeStateServiceProd.class);

	@Autowired private AmazonEC2 ec2;

	public void startEc2Instance(String instanceId) {
		log.info("Starting EC2 instance {}", instanceId);
		StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instanceId);
		ec2.startInstances(request);
	}

	public void stopEc2Instance(String instanceId) {
		log.info("Stopping EC2 instance {}", instanceId);
		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
		ec2.stopInstances(request);
	}

	public void rebootEc2Instance(String instanceId) {
		log.info("Rebooting EC2 instance {}", instanceId);
		RebootInstancesRequest request = new RebootInstancesRequest().withInstanceIds(instanceId);
		ec2.rebootInstances(request);
	}

}
