package pro.siberians.ws.stream.service.ec2;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.HandlerContextAware;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.AmazonEC2Exception;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
public class Ec2DescribeServiceProd implements Ec2DescribeService {

	private Logger log = LoggerFactory.getLogger(Ec2DescribeServiceProd.class);

	@Autowired private AmazonEC2 ec2;

	public Map<String, String> getInstanceStates(List<String> instanceIds) {
		log.info("Describing EC2 instances {}", String.join(", ", instanceIds));

		Map<String, String> states = new HashMap<String, String>();
		for (String instId : instanceIds) states.put(instId, null);

		boolean notFoundHandlerCalled = false;
		DescribeInstancesRequest request = new DescribeInstancesRequest().withInstanceIds(instanceIds);
		while (true) {
			DescribeInstancesResult response;
			try {
				response = ec2.describeInstances(request);
			} catch (AmazonEC2Exception error) {
				if (!error.getErrorCode().equals("InvalidInstanceID.NotFound")) throw error;
				if (notFoundHandlerCalled) throw error;
				notFoundHandlerCalled = true;
				boolean proceed = handleNotFoundInstancesError(error, request, states);
				if (proceed) continue; else break;
			}

			for (Reservation reservation : response.getReservations()) {
				for (Instance instance : reservation.getInstances()) {
					String instId = instance.getInstanceId();
					String instState = instance.getState().getName();

					log.info("EC2 instance {} state is {}", instId, instState);

					states.put(instId, instState);
				}
			}

			String nextToken = response.getNextToken();
			if (nextToken == null) break;
			request.setNextToken(nextToken);
		}

		return states;
	}

	private boolean handleNotFoundInstancesError(AmazonEC2Exception error, DescribeInstancesRequest request, Map<String, String> states) {
		List<String> nonExistingInstanceIds = extractNonExistingInstanceIds(error);
		List<String> remainingInstanceIds = new ArrayList<String>();
		for (String id : request.getInstanceIds()) {
			boolean notExist = nonExistingInstanceIds.stream().filter(neId -> neId.equals(id)).findAny().isPresent();
			if (notExist) {
				states.put(id, "not-exist");
			} else {
				remainingInstanceIds.add(id);
			}
		}
		if (remainingInstanceIds.size() > 0) {
			request.setInstanceIds(remainingInstanceIds);
			return true;
		} else {
			return false;
		}
	}

	private List<String> extractNonExistingInstanceIds(AmazonEC2Exception error) {
		List<String> ids = new ArrayList<String>();
		Matcher m = Pattern.compile("\\bi-[a-z0-9]+\\b").matcher(error.getMessage());
		while (m.find()) ids.add(m.group());
		return ids;
	}
}
