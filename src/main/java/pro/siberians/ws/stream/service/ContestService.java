package pro.siberians.ws.stream.service;

import java.util.Optional;
import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.*;
import pro.siberians.ws.stream.repository.*;
import pro.siberians.ws.stream.exception.*;

@Service
public class ContestService {

	@Autowired ContestRepository contestRepository;

	public Optional<ContestModel> findByGuid(String guid) {
		return contestRepository.find(guid);
	}

	public ContestModel findByGuidOrThrow(String guid) throws ContestNotFoundException {
		return findByGuid(guid).orElseThrow(() -> new ContestNotFoundException("Contest not found!"));
	}

	public PagedResults<ContestModel> findAllOnPage(LastEvaluatedKey lek, Integer limit) {
		return contestRepository.findAllOnPage(lek, limit);
	}

	public ContestModel create(String name, Date startAt, Date finishAt, String awsInstType) {
		String guidPrefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String guidSuffix = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);
		String guid = guidPrefix + "-" + guidSuffix;

		ContestModel contest = new ContestModel();
		contest.setGuid(guid);
		contest.setName(name);
		contest.setStatus("active");
		contest.setStartAt(startAt);
		contest.setFinishAt(finishAt);
		contest.setAwsInstType(awsInstType);
		contestRepository.save(contest);
		return contest;
	}

	public void update(ContestModel contest, String name, Date startAt, Date finishAt) {
		contest.setName(name);
		contest.setStartAt(startAt);
		contest.setFinishAt(finishAt);
		contestRepository.save(contest);
	}

	public void delete(ContestModel contest) {
		contest.setStatus("deleted");
		contestRepository.save(contest);
	}

	// AWS & OpenVidu attrs

	public void setOpenViduSecret(ContestModel contest, String openViduSecret) {
		contest.setOpenViduSecret(openViduSecret);
		contestRepository.save(contest);
	}

	public void setAwsLastError(ContestModel contest, String awsLastError) {
		contest.setAwsLastError(awsLastError);
		contestRepository.save(contest);
	}

	public void setAwsIpAllocation(ContestModel contest, String awsIpAllocId, String awsIpAllocAddr) {
		contest.setAwsIpAllocated(true);
		contest.setAwsIpAllocId(awsIpAllocId);
		contest.setAwsIpAllocAddr(awsIpAllocAddr);
		contestRepository.save(contest);
	}

	public void unsetAwsIpAllocation(ContestModel contest) {
		contest.setAwsIpAllocated(false);
		contestRepository.save(contest);
	}

	public void setAwsIpAssociation(ContestModel contest, String awsIpAssocId) {
		contest.setAwsIpAssociated(true);
		contest.setAwsIpAssocId(awsIpAssocId);
		contestRepository.save(contest);
	}

	public void unsetAwsIpAssociation(ContestModel contest) {
		contest.setAwsIpAssociated(false);
		contestRepository.save(contest);
	}

	public void setAwsInstance(ContestModel contest, String awsInstId) {
		contest.setAwsInstCreated(true);
		contest.setAwsInstId(awsInstId);
		contestRepository.save(contest);
	}

	public void unsetAwsInstance(ContestModel contest) {
		contest.setAwsInstCreated(false);
		contestRepository.save(contest);
	}

	public void setDnsRecord(ContestModel contest, String awsDnsDomain) {
		contest.setAwsDnsRecorded(true);
		contest.setAwsDnsDomain(awsDnsDomain);
		contestRepository.save(contest);
	}

	public void unsetDnsRecord(ContestModel contest) {
		contest.setAwsDnsRecorded(false);
		contestRepository.save(contest);
	}

}
