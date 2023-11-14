package com.techyourchance.testdoublesfundamentals.exercise4;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.hamcrest.MatcherAssert;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FetchUserProfileUseCaseSyncTest {

    public static String USER_ID = "user id";
    public static String FULL_NAME = "full name";
    public static String IMAGE_URL = "image url";

    FetchUserProfileUseCaseSync fetchUserProfileUseCaseSync;
    UserCacheTd userCacheTd;
    UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;


    @Before
    public void setUp() throws Exception {
        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        userCacheTd = new UserCacheTd();
        fetchUserProfileUseCaseSync = new FetchUserProfileUseCaseSync(userProfileHttpEndpointSyncTd, userCacheTd);

    }
    // End point passed success

    @Test
    public void userProfile_success_userIdPassedToEndPoint() {
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        MatcherAssert.assertThat(userProfileHttpEndpointSyncTd.mUserId, is(USER_ID));
    }

    // cache user -success
    @Test
    public void userProfile_success_cacheUser() {
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        User user = userCacheTd.getUser(USER_ID);
        MatcherAssert.assertThat(user.getUserId(), is(USER_ID));
        MatcherAssert.assertThat(user.getFullName(), is(FULL_NAME));
        MatcherAssert.assertThat(user.getImageUrl(), is(IMAGE_URL));
    }

    //cache user -do not change user

    @Test
    public void userProfile_generalError_cacheUserDoesNotChange() {
        userProfileHttpEndpointSyncTd.mIsGeneralError = true;
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        User user = userCacheTd.getUser(USER_ID);
        MatcherAssert.assertThat(userCacheTd.getUser(USER_ID), is(nullValue()));
    }
    @Test
    public void userProfile_authError_cacheUserDoesNotChange() {
        userProfileHttpEndpointSyncTd.mIsAuthError = true;
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        User user = userCacheTd.getUser(USER_ID);
        MatcherAssert.assertThat(userCacheTd.getUser(USER_ID), is(nullValue()));
    }

    @Test
    public void userProfile_serverError_cacheUserDoesNotChange() {
        userProfileHttpEndpointSyncTd.mIsServerError = true;
        fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        MatcherAssert.assertThat(userCacheTd.getUser(USER_ID), is(nullValue()));
    }



    //fetch user-success
    @Test
    public void userProfile_success_successReturned() {
        FetchUserProfileUseCaseSync.UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        MatcherAssert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS));
    }

    //fetch user-failure

    @Test
    public void userProfile_generalError_failureReturned() {
        userProfileHttpEndpointSyncTd.mIsGeneralError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        MatcherAssert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void userProfile_authError_failureReturned() {
        userProfileHttpEndpointSyncTd.mIsAuthError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        MatcherAssert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void userProfile_serverError_failureReturned() {
        userProfileHttpEndpointSyncTd.mIsServerError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        MatcherAssert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    //fetch user-network failure
    @Test
    public void userProfile_networkError_networkFailureReturned() {
        userProfileHttpEndpointSyncTd.mIsNetworkError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID);
        MatcherAssert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }


    //-----------------------------------------------------------------------------------------------------------//
    // Helper classes
    public static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {
        public String mUserId = "";
        public boolean mIsGeneralError;
        public boolean mIsServerError;
        public boolean mIsAuthError;
        public boolean mIsNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;
            if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }

    public static class UserCacheTd implements UsersCache {

        private List<User> mUsers = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User existingUser = getUser(user.getUserId());
            if (existingUser != null) {
                mUsers.remove(existingUser);
            }
            mUsers.add(user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            for (User user : mUsers) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }
    }
}