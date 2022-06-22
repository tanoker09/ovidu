package pro.siberians.ws.stream.service.route53;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.route53.*;
import com.amazonaws.services.route53.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("prod")
@Service
public class Route53ServiceProd implements Route53Service {

	private Logger log = LoggerFactory.getLogger(Route53ServiceProd.class);

	@Autowired private AmazonRoute53 route53;

	@Value("${aws.openvidu.dnszoneid}") private String dnsZoneId;

	public void createDomainRecord(String domainName, String destinationIp) {
		log.info("Creating new Route53 DNS record {} pointing to {}", domainName, destinationIp);
		ResourceRecordSet recordSet = getRecordSet(domainName, destinationIp);
		Change change = new Change()
			.withAction("CREATE")
			.withResourceRecordSet(recordSet);
		ChangeBatch changeBatch = new ChangeBatch().withChanges(change);
		ChangeResourceRecordSetsRequest request = new ChangeResourceRecordSetsRequest()
			.withHostedZoneId(dnsZoneId)
			.withChangeBatch(changeBatch);
		route53.changeResourceRecordSets(request);
	}

	public void deleteDomainRecord(String domainName, String destinationIp) {
		log.info("Deleting Route53 DNS record for {}", domainName);
		ResourceRecordSet recordSet = getRecordSet(domainName, destinationIp);
		Change change = new Change()
			.withAction("DELETE")
			.withResourceRecordSet(recordSet);
		ChangeBatch changeBatch = new ChangeBatch().withChanges(change);
		ChangeResourceRecordSetsRequest request = new ChangeResourceRecordSetsRequest()
			.withHostedZoneId(dnsZoneId)
			.withChangeBatch(changeBatch);
		route53.changeResourceRecordSets(request);
	}

	private ResourceRecordSet getRecordSet(String domainName, String destinationIp) {
		return new ResourceRecordSet()
			.withName(domainName)
			.withType("A")
			.withTTL(30L)
			.withResourceRecords(
				new ResourceRecord().withValue(destinationIp)
			);
	}

}
