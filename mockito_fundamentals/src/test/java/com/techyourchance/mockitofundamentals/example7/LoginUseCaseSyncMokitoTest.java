package com.techyourchance.mockitofundamentals.example7;


import com.techyourchance.mockitofundamentals.example7.authtoken.AuthTokenCache;
import com.techyourchance.mockitofundamentals.example7.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.example7.eventbus.LoggedInEvent;
import com.techyourchance.mockitofundamentals.example7.networking.LoginHttpEndpointSync;
import com.techyourchance.mockitofundamentals.example7.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.util.List;


public class LoginUseCaseSyncMokitoTest {

    public static String USERNAME = "username";
    public static String PASSWORD = "password";
    public static String AUTH_TOKEN = "auth_token";

    LoginUseCaseSync SUT;
    LoginHttpEndpointSync loginHttpEndpointSyncMock;
    AuthTokenCache authTokenCacheMock;
    EventBusPoster eventBusPosterMock;

    @Before
    public void setUp() throws Exception {
        loginHttpEndpointSyncMock = Mockito.mock(LoginHttpEndpointSync.class);
        authTokenCacheMock = Mockito.mock(AuthTokenCache.class);
        eventBusPosterMock = Mockito.mock(EventBusPoster.class);
        SUT = new LoginUseCaseSync(loginHttpEndpointSyncMock, authTokenCacheMock, eventBusPosterMock);
        success();
    }

    //username and password passed to endpoint
    @Test
    public void loginSync_loginPasswordPassedToEndpoint_loginSuccess() throws NetworkErrorException {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verify(loginHttpEndpointSyncMock).loginSync(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        MatcherAssert.assertThat(captures.get(0), is(USERNAME));
        MatcherAssert.assertThat(captures.get(1), is(PASSWORD));
    }

    //if login succeeds - auth token must be cached
    @Test
    public void loginSync_success_authTokenCached() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verify(authTokenCacheMock).cacheAuthToken(ac.capture());
        MatcherAssert.assertThat(ac.getValue(), is(AUTH_TOKEN));
    }

    // if login fails- old auth token shouldn't be changed
    @Test
    public void loginSync_generalError_authTokenNotChanged() throws NetworkErrorException {
        generalError();
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verifyNoMoreInteractions(authTokenCacheMock);
    }

    @Test
    public void loginSync_authError_authTokenNotChanged() throws NetworkErrorException {
        authError();
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verifyNoMoreInteractions(authTokenCacheMock);
    }

    @Test
    public void loginSync_serverError_authTokenNotChanged() throws NetworkErrorException {
        serverError();
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verifyNoMoreInteractions(authTokenCacheMock);
    }


    // if login succeeds - event bus is posted
    @Test
    public void loginSync_success_eventBusPosted() {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verify(eventBusPosterMock).postEvent(ac.capture());
        MatcherAssert.assertThat(ac.getValue(), is(instanceOf(LoggedInEvent.class)));
    }


    // if login fails - event bus is not posted
    @Test
    public void loginSync_generalError_eventBusNotPosted() throws NetworkErrorException {
        generalError();
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void loginSync_serverError_eventBusNotPosted() throws NetworkErrorException {
        serverError();
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void loginSync_authError_eventBusNotPosted() throws NetworkErrorException {
        authError();
        SUT.loginSync(USERNAME, PASSWORD);
        Mockito.verifyNoMoreInteractions(eventBusPosterMock);
    }


    // if login succeeds - SUCCESS returned
    @Test
    public void loginSync_success_successReturned() {
        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        MatcherAssert.assertThat(result, is(LoginUseCaseSync.UseCaseResult.SUCCESS));
    }


    //if login failed - FAIL returned
    @Test
    public void loginSync_generalError_failureReturned() throws NetworkErrorException {
        generalError();
        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        MatcherAssert.assertThat(result, is(LoginUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_server_failureReturned() throws NetworkErrorException {
        serverError();
        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        MatcherAssert.assertThat(result, is(LoginUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void loginSync_authError_failureReturned() throws NetworkErrorException {
        authError();
        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        MatcherAssert.assertThat(result, is(LoginUseCaseSync.UseCaseResult.FAILURE));
    }


    //if network error- network error returned
    @Test
    public void loginSync_networkError_networkErrorReturned() throws NetworkErrorException {
        networkError();
        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
        MatcherAssert.assertThat(result, is(LoginUseCaseSync.UseCaseResult.NETWORK_ERROR));

    }

    //----------------------------------------------------------------------------------------------------------//
    //Helper classes
    private void success() throws NetworkErrorException {
        Mockito.when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class))).thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.SUCCESS, AUTH_TOKEN));
    }

    private void generalError() throws NetworkErrorException {
        Mockito.when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class))).thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, ""));
    }

    private void serverError() throws NetworkErrorException {
        Mockito.when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class))).thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, ""));
    }

    private void authError() throws NetworkErrorException {
        Mockito.when(loginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class))).thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, ""));
    }

    private void networkError() throws NetworkErrorException {
        Mockito.doThrow(new NetworkErrorException()).when(loginHttpEndpointSyncMock).loginSync(any(String.class), any(String.class));
    }

}
