package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.example7.LoginUseCaseSync;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class UpdateUsernameUseCaseSyncTest {

    public static String USER_ID = "user id";
    public static String USER_NAME = "user name";

    UpdateUsernameUseCaseSync SUT;
    @Mock UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointSync;
    @Mock UsersCache usersCache;
    @Mock EventBusPoster eventBusPoster;

    @Before
    public void setUp() throws Exception {
        SUT = new UpdateUsernameUseCaseSync(updateUsernameHttpEndpointSync, usersCache, eventBusPoster);
        success();
    }


    @Test
    public void updateUserName_usernameAndIdPassedToEndpoint_syncSuccess() throws NetworkErrorException {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verify(updateUsernameHttpEndpointSync).updateUsername(ac.capture(), ac.capture());
        List<String> values = ac.getAllValues();
        MatcherAssert.assertThat(values.get(0), is(USER_ID));
        MatcherAssert.assertThat(values.get(1), is(USER_NAME));
    }
    @Test
    public void updateUserName_success_eventBusPosted() {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verify(eventBusPoster).postEvent(ac.capture());
        MatcherAssert.assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }
    @Test
    public void updateUserName_generalError_eventBusNotPosted() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verifyNoMoreInteractions(eventBusPoster);
    }

    @Test
    public void updateUserName_serverError_eventBusNotPosted() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verifyNoMoreInteractions(eventBusPoster);
    }

    @Test
    public void updateUserName_authError_eventBusNotPosted() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verifyNoMoreInteractions(eventBusPoster);
    }

    @Test
    public void updateUserName_success_cached() {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verify(usersCache).cacheUser(ac.capture());
        MatcherAssert.assertThat(ac.getValue().getUserId(), is(USER_ID));
        MatcherAssert.assertThat(ac.getValue().getUsername(), is(USER_NAME));
    }
    @Test
    public void updateUserName_generalError_notCached() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUserName_serverError_notCached() throws NetworkErrorException {
        serverError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUserName_authError_notCached() throws NetworkErrorException {
        generalError();
        SUT.updateUsernameSync(USER_ID, USER_NAME);
        Mockito.verifyNoMoreInteractions(usersCache);
    }

    @Test
    public void updateUserName_success_successReturned() {
        UpdateUsernameUseCaseSync.UseCaseResult result=SUT.updateUsernameSync(USER_ID, USER_NAME);
        MatcherAssert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUserName_serverError_failureReturned() throws NetworkErrorException {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result=SUT.updateUsernameSync(USER_ID, USER_NAME);
        MatcherAssert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUserName_generalError_failureReturned() throws NetworkErrorException {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result=SUT.updateUsernameSync(USER_ID, USER_NAME);
        MatcherAssert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUserName_authError_failureReturned() throws NetworkErrorException {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result=SUT.updateUsernameSync(USER_ID, USER_NAME);
        MatcherAssert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUserName_networkError_failureReturned() throws NetworkErrorException {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result=SUT.updateUsernameSync(USER_ID, USER_NAME);
        MatcherAssert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    //----------------------------------------------------------------------------------------------------------//
    //Helper classes
    private void success() throws NetworkErrorException {
        Mockito.when(updateUsernameHttpEndpointSync.updateUsername(USER_ID, USER_NAME)).thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USER_ID, USER_NAME));
    }

    private void generalError() throws NetworkErrorException {
        Mockito.when(updateUsernameHttpEndpointSync.updateUsername(USER_ID, USER_NAME)).thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, USER_ID, USER_NAME));
    }
    private void serverError() throws NetworkErrorException {
        Mockito.when(updateUsernameHttpEndpointSync.updateUsername(USER_ID, USER_NAME)).thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, USER_ID, USER_NAME));
    }
    private void authError() throws NetworkErrorException {
        Mockito.when(updateUsernameHttpEndpointSync.updateUsername(USER_ID, USER_NAME)).thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, USER_ID, USER_NAME));
    }
    private void networkError() throws NetworkErrorException {
        Mockito.doThrow(new NetworkErrorException()).when(updateUsernameHttpEndpointSync).updateUsername(any(String.class), any(String.class));
    }
}