package pro.siberians.ws.stream.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import pro.siberians.ws.stream.model.*;
import pro.siberians.ws.stream.repository.*;

@Service
public class MembershipService {

    private Logger log = LoggerFactory.getLogger(MembershipService.class);

    @Autowired UserRepository userRepository;
    @Autowired ContestRepository contestRepository;
    @Autowired UserContestRepository userContestRepository;
    @Autowired ContestUserRepository contestUserRepository;

    // Get list of contests for given user
    public List<UserContestModel> findUserContests(UserModel user) {
		return userContestRepository.findAll(user.getEmail());
    }

    // Get list of contests for given user filtered by status
    public List<UserContestModel> findUserContestsWithStatus(UserModel user, String contestStatus) {
		return userContestRepository.findAllWithStatus(user.getEmail(), contestStatus);
    }

    // Get list of members for contest
	public List<ContestUserModel> findContestUsers(ContestModel contest) {
		return contestUserRepository.findAll(contest.getGuid());
	}

    // Check if user has access to contest
    public boolean isMember(UserModel user, ContestModel contest) {
        Optional<UserContestModel> userContest = findUserContest(user.getEmail(), contest);
        Optional<ContestUserModel> contestUser = findContestUser(contest, user.getEmail());

        if (userContest.isPresent() ^ contestUser.isPresent()) {
            log.error(
                "Incomplete membership record found for user {} and contest {}!",
                user.getEmail(), contest.getGuid()
            );
        }

        return userContest.isPresent() || contestUser.isPresent();
    }

    // Add memebers
    // P.S. User model record may not even exists in DB yet! And it is OK!
    public void addMemberByEmail(String userEmail, ContestModel contest) {
        createUserContest(userEmail, contest);
        createContestUser(contest, userEmail);
    }

    // Delete members
    // P.S. User model record may not even exists in DB yet! And it is OK!
    public void deleteMemberByEmail(String userEmail, ContestModel contest) {
        deleteUserContest(userEmail, contest);
        deleteContestUser(contest, userEmail);
    }

    // Synchronize contest info in membership records with main contest record.
    // Copy actual contest info to UserContestModel records when contest info updated.
    public void updateContestInfo(ContestModel contest) {
        for (ContestUserModel contestUser : findContestUsers(contest)) {
            String userEmail = contestUser.getUserEmail();
            updateUserContest(userEmail, contest);
        }
    }

    //
    // Helper methods
    //

	private Optional<UserContestModel> findUserContest(String userEmail, ContestModel contest) {
        return userContestRepository.find(
            userEmail,
            contest.getStatus(),
            contest.getGuid()
        );
	}

    private Optional<ContestUserModel> findContestUser(ContestModel contest, String userEmail) {
        return contestUserRepository.find(
            contest.getGuid(),
            userEmail
        );
	}

    private UserContestModel createUserContest(String userEmail, ContestModel contest) {
        UserContestModel userContest = new UserContestModel();
        userContest.setUserEmail(userEmail);
        userContest.setContestStatusAndGuid(contest.getStatus(), contest.getGuid());
        userContest.setContestName(contest.getName());
        userContest.setContestStartAt(contest.getStartAt());
        userContest.setContestFinishAt(contest.getFinishAt());
        userContestRepository.save(userContest);
        return userContest;
    }

    private ContestUserModel createContestUser(ContestModel contest, String userEmail) {
        ContestUserModel contestUser = new ContestUserModel();
        contestUser.setContestGuid(contest.getGuid());
        contestUser.setUserEmail(userEmail);
        contestUserRepository.save(contestUser);
        return contestUser;
    }


    private void deleteUserContest(String userEmail, ContestModel contest) {
        findUserContest(userEmail, contest).ifPresent(
            (userContest) -> { userContestRepository.delete(userContest); }
        );
    }

    private void deleteContestUser(ContestModel contest, String userEmail) {
        findContestUser(contest, userEmail).ifPresent(
            (contestUser) -> { contestUserRepository.delete(contestUser); }
        );
    }

    private void updateUserContest(String userEmail, ContestModel contest) {
        findUserContest(userEmail, contest).ifPresent(
            (userContest) -> {
                userContest.setContestName(contest.getName());
                userContest.setContestStartAt(contest.getStartAt());
                userContest.setContestFinishAt(contest.getFinishAt());
                userContestRepository.save(userContest);
            }
        );
    }

}
