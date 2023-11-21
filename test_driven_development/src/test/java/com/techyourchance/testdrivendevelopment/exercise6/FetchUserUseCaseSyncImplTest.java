package com.techyourchance.testdrivendevelopment.exercise6;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {

    //region constants
    private static final String USER_ID = "userId";
    private static final String USERNAME = "username";
    private static final User USER = new User(USER_ID, USERNAME);
    //end region constants

    //region helper fields
    FetchUserHttpEndpointSyncTestDouble fetchUserHttpEndpointSyncTestDouble;
    @Mock UsersCache usersCache;
    FetchUserUseCaseSyncImpl SUT;
    //end region helper fields
    @Before
    public void setup() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(fetchUserHttpEndpointSyncTestDouble,usersCache);
        userNotInCache();
        endpointSuccess();
    }
    @Test
    public void fetchUserSync_notInCache_correctUserIdPassedToEndpoint() throws Exception {
        // Arrange
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(fetchUserHttpEndpointSyncTestDouble.mUserId, Is.is(USER_ID));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_successStatus() throws Exception {
        // Arrange
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), Is.is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_correctUserReturned() throws Exception {
        // Arrange
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), Is.is(USER));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_userCached() throws Exception {
        // Arrange
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCache).cacheUser(ac.capture());
        assertThat(ac.getValue(), Is.is(USER));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_failureStatus() throws Exception {
        // Arrange
        endpointAuthError();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), Is.is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_nullUserReturned() throws Exception {
        // Arrange
        endpointAuthError();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), nullValue());
    }

    @Test
    public void fetchUserSync_notInCacheEndpointAuthError_nothingCached() throws Exception {
        // Arrange
        endpointAuthError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCache, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_failureStatus() throws Exception {
        // Arrange
        endpointServerError();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), Is.is(FetchUserUseCaseSync.Status.FAILURE));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_nullUserReturned() throws Exception {
        // Arrange
        endpointServerError();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), Is.is(nullValue()));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointServerError_nothingCached() throws Exception {
        // Arrange
        endpointServerError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCache, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_failureStatus() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), Is.is(FetchUserUseCaseSync.Status.NETWORK_ERROR));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_nullUserReturned() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), Is.is(nullValue()));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointNetworkError_nothingCached() throws Exception {
        // Arrange
        endpointNetworkError();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCache, never()).cacheUser(any(User.class));
    }

    @Test
    public void fetchUserSync_correctUserIdPassedToCache() throws Exception {
        // Arrange
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        verify(usersCache).getUser(ac.capture());
        assertThat(ac.getValue(), Is.is(USER_ID));
    }

    @Test
    public void fetchUserSync_inCache_successStatus() throws Exception {
        // Arrange
        userInCache();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getStatus(), Is.is(FetchUserUseCaseSync.Status.SUCCESS));
    }

    @Test
    public void fetchUserSync_inCache_cachedUserReturned() throws Exception {
        // Arrange
        userInCache();
        // Act
        FetchUserUseCaseSync.UseCaseResult result = SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(result.getUser(), Is.is(USER));
    }

    @Test
    public void fetchUserSync_inCache_endpointNotPolled() throws Exception {
        // Arrange
        userInCache();
        // Act
        SUT.fetchUserSync(USER_ID);
        // Assert
        assertThat(fetchUserHttpEndpointSyncTestDouble.mRequestCount, Is.is(0));
    }

    //region helper methods

    //end region helper methods
    private void userNotInCache() {
        when(usersCache.getUser(anyString())).thenReturn(null);
    }

    private void userInCache() {
        when(usersCache.getUser(anyString())).thenReturn(USER);
    }

    private void endpointSuccess() {
        // endpoint test double is set up for success by default; this method is for clarity of intent
    }

    private void endpointAuthError() {
        fetchUserHttpEndpointSyncTestDouble.mAuthError = true;
    }

    private void endpointServerError() {
        fetchUserHttpEndpointSyncTestDouble.mServerError = true;
    }

    private void endpointNetworkError() {
        fetchUserHttpEndpointSyncTestDouble.mNetworkError = true;
    }
    //region helper classes
    private class FetchUserHttpEndpointSyncTestDouble implements FetchUserHttpEndpointSync {

        private int mRequestCount;
        private String mUserId = "";
        public boolean mAuthError;
        public boolean mServerError;
        public boolean mNetworkError;

        @Override
        public EndpointResult fetchUserSync(String userId) throws NetworkErrorException {
            mRequestCount ++;
            mUserId = userId;

            if (mAuthError) {
                return new EndpointResult(EndpointStatus.AUTH_ERROR, "", "");
            } else if (mServerError) {
                return new EndpointResult(EndpointStatus.GENERAL_ERROR, "", "");
            } else if (mNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USERNAME);
            }
        }
    }
    //end region helper classes
}