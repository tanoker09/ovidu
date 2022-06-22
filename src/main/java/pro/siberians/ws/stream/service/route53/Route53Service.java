package pro.siberians.ws.stream.service.route53;

public interface Route53Service {
	void createDomainRecord(String domainName, String destinationIp);
	void deleteDomainRecord(String domainName, String destinationIp);
}
