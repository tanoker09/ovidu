package pro.siberians.ws.stream.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import io.openvidu.java.client.OpenVidu;
import io.openvidu.java.client.OpenViduRole;
import io.openvidu.java.client.Recording;
import io.openvidu.java.client.RecordingMode;
import io.openvidu.java.client.Session;
import io.openvidu.java.client.SessionProperties;
import io.openvidu.java.client.TokenOptions;
import io.openvidu.java.client.OpenViduHttpException;
import io.openvidu.java.client.OpenViduJavaClientException;

import pro.siberians.ws.stream.model.*;
import pro.siberians.ws.stream.exception.*;

@Service
public class StreamingService {

	private Logger log = LoggerFactory.getLogger(StreamingService.class);

	public String getStreamingToken(ContestModel contest)
		throws OpenViduHttpException, OpenViduJavaClientException,
			OpenViduNotFoundException, OpenViduSessionNotFoundException
	{
		String ovUrl = "https://" + contest.getAwsDnsDomain();
		String ovSecret = contest.getOpenViduSecret();
		String ovSessionId = contest.getGuid();
		Session ovSession = null;

		OpenVidu ov = new OpenVidu(ovUrl, ovSecret);

		try {
			SessionProperties sessionProperties = new SessionProperties.Builder()
				.customSessionId(ovSessionId)
				.recordingMode(RecordingMode.ALWAYS)
				.defaultOutputMode(Recording.OutputMode.INDIVIDUAL)
				.build();
			ovSession = ov.createSession(sessionProperties);
		} catch (OpenViduHttpException error) {
			if (error.getStatus() == 509) {
				log.info("Session already exists on the OpenVidu server!");
				ovSession = findActiveOpenViduSessionById(ov, ovSessionId);
				if (ovSession == null) {
					log.error("OpenVidu session with ID {} not found on the OpenVidu server!", ovSessionId);
					throw new OpenViduSessionNotFoundException("OpenVidu session not found!");
				}
			} else throw error;
		}

		TokenOptions tokenOptions = new TokenOptions.Builder()
			.role(OpenViduRole.PUBLISHER)
			.build();
		return ovSession.generateToken(tokenOptions);
	}

	private Session findActiveOpenViduSessionById(OpenVidu ov, String id) {
		List<Session> sessions = ov.getActiveSessions();
		for (Session s : sessions)
			if (s.getSessionId().equals(id))
				return s;
		return null;
	}

}
