package pro.siberians.ws.stream.service.route53;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("dev")
@Service
public class Route53ServiceDev implements Route53Service {

	private Logger log = LoggerFactory.getLogger(Route53ServiceDev.class);

	public void createDomainRecord(String domainName, String destinationIp) {
		log.info("Creating new Route53 DNS record {} pointing to {}", domainName, destinationIp);
	}

	public void deleteDomainRecord(String domainName, String destinationIp) {
		log.info("Deleting Route53 DNS record for {}", domainName);
	}

}
