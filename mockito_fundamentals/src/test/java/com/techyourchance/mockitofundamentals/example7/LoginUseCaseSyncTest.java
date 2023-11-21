package com.techyourchance.mockitofundamentals.example7;

import com.techyourchance.mockitofundamentals.example7.authtoken.AuthTokenCache;
import com.techyourchance.mockitofundamentals.example7.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.example7.eventbus.LoggedInEvent;
import com.techyourchance.mockitofundamentals.example7.networking.LoginHttpEndpointSync;
import com.techyourchance.mockitofundamentals.example7.networking.NetworkErrorException;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class LoginUseCaseSyncTest {

    public static String USERNAME="username";
    public static String PASSWORD="password";
    public static String AUTH_TOKEN="auth_token";

    LoginUseCaseSync loginUseCaseSync;

    LoginHttpEndpointsSyncTd loginHttpEndpointsSyncTd;
    AuthTokenCacheTd authTokenCacheTd;
    EventBusPosterTd eventBusPosterTd;


    @Before
    public void setUp() throws Exception {
        loginHttpEndpointsSyncTd=new LoginHttpEndpointsSyncTd();
        authTokenCacheTd=new AuthTokenCacheTd();
        eventBusPosterTd=new EventBusPosterTd();
        loginUseCaseSync=new LoginUseCaseSync(loginHttpEndpointsSyncTd,authTokenCacheTd,eventBusPosterTd);
    }

    //username and password passed to endpoint
    @Test
    public void loginSync_loginPasswordPassedToEndpoint_loginSuccess() {
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(loginHttpEndpointsSyncTd.mUsername,is(USERNAME));
        MatcherAssert.assertThat(loginHttpEndpointsSyncTd.mPassword,is(PASSWORD));
    }

    //if login succeeds - auth token must be cached
    @Test
    public void loginSync_success_authTokenCached() throws Exception {
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(authTokenCacheTd.getAuthToken(),is(AUTH_TOKEN));
    }

    // if login fails- old auth token shouldn't be changed
    @Test
    public void loginSync_generalError_authTokenNotChanged() {
        loginHttpEndpointsSyncTd.isGeneralError=true;
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(authTokenCacheTd.getAuthToken(),is(""));
    }

    @Test
    public void loginSync_authError_authTokenNotChanged() {
        loginHttpEndpointsSyncTd.isAuthError=true;
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(authTokenCacheTd.getAuthToken(),is(""));
    }

    @Test
    public void loginSync_serverError_authTokenNotChanged() {
        loginHttpEndpointsSyncTd.isServerError=true;
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(authTokenCacheTd.getAuthToken(),is(""));
    }


    // if login succeeds - event bus is posted
    @Test
    public void loginSync_success_eventBusPosted() {
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(eventBusPosterTd.mEventPosted,is(instanceOf(LoggedInEvent.class)));
    }


    // if login fails - event bus is not posted
    @Test
    public void loginSync_generalError_eventBusNotPosted() {
        loginHttpEndpointsSyncTd.isGeneralError=true;
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(eventBusPosterTd.mInteractions,is(0));
    }

    @Test
    public void loginSync_serverError_eventBusNotPosted() {
        loginHttpEndpointsSyncTd.isServerError=true;
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(eventBusPosterTd.mInteractions,is(0));
    }

    @Test
    public void loginSync_authError_eventBusNotPosted() {
        loginHttpEndpointsSyncTd.isAuthError=true;
        loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(eventBusPosterTd.mInteractions,is(0));
    }


    // if login succeeds - SUCCESS returned
    @Test
    public void loginSync_success_successReturned() {
        LoginUseCaseSync.UseCaseResult result=loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(result,is(LoginUseCaseSync.UseCaseResult.SUCCESS));
    }


    //if login failed - FAIL returned
    @Test
    public void loginSync_generalError_failureReturned() {
        loginHttpEndpointsSyncTd.isGeneralError=true;
        LoginUseCaseSync.UseCaseResult result=loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(result,is(LoginUseCaseSync.UseCaseResult.FAILURE));
    }
    @Test
    public void loginSync_server_failureReturned() {
        loginHttpEndpointsSyncTd.isServerError=true;
        LoginUseCaseSync.UseCaseResult result=loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(result,is(LoginUseCaseSync.UseCaseResult.FAILURE));
    }
    @Test
    public void loginSync_authError_failureReturned() {
        loginHttpEndpointsSyncTd.isAuthError=true;
        LoginUseCaseSync.UseCaseResult result=loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(result,is(LoginUseCaseSync.UseCaseResult.FAILURE));
    }


    //if network error- network error returned
    @Test
    public void loginSync_networkError_networkErrorReturned() {
        loginHttpEndpointsSyncTd.isNetworkError=true;
        LoginUseCaseSync.UseCaseResult result=loginUseCaseSync.loginSync(USERNAME,PASSWORD);
        MatcherAssert.assertThat(result,is(LoginUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

//----------------------------------------------------------------------------------------------------------//
    //Helper classes
    private static class LoginHttpEndpointsSyncTd implements LoginHttpEndpointSync{
        public String mUsername;
        public String mPassword;
        public boolean isGeneralError;
        public boolean isAuthError;
        public boolean isServerError;
        public boolean isNetworkError;

        @Override
        public EndpointResult loginSync(String username, String password) throws NetworkErrorException {
            mUsername=username;
            mPassword=password;
            if(isGeneralError){
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR,"");
            }else if(isAuthError){
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR,"");
            }else if(isServerError){
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR,"");
            }else if(isNetworkError){
                throw new NetworkErrorException();
            }else{
                return new EndpointResult(EndpointResultStatus.SUCCESS,AUTH_TOKEN);
            }
        }
    }


    private static class AuthTokenCacheTd implements AuthTokenCache{
        String mAuthToken="";
        @Override
        public void cacheAuthToken(String authToken) {
            mAuthToken=authToken;
        }

        @Override
        public String getAuthToken() {
            return mAuthToken;
        }
    }


    private static class EventBusPosterTd implements EventBusPoster{
        public Object mEventPosted;
        public int mInteractions;

        @Override
        public void postEvent(Object event) {
            mEventPosted=event;
            mInteractions++;
        }
    }
}
//
//    public static final String USERNAME = "username";
//    public static final String PASSWORD = "password";
//    public static final String AUTH_TOKEN = "authToken";
//
//    LoginHttpEndpointSync mLoginHttpEndpointSyncMock;
//    AuthTokenCache mAuthTokenCacheMock;
//    EventBusPoster mEventBusPosterMock;
//
//    LoginUseCaseSync SUT;
//
//    @Before
//    public void setup() throws Exception {
//        mLoginHttpEndpointSyncMock = mock(LoginHttpEndpointSync.class);
//        mAuthTokenCacheMock = mock(AuthTokenCache.class);
//        mEventBusPosterMock = mock(EventBusPoster.class);
//        SUT = new LoginUseCaseSync(mLoginHttpEndpointSyncMock, mAuthTokenCacheMock, mEventBusPosterMock);
//        success();
//    }
//
//    @Test
//    public void loginSync_success_usernameAndPasswordPassedToEndpoint() throws Exception {
//        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
//        SUT.loginSync(USERNAME, PASSWORD);
//        verify(mLoginHttpEndpointSyncMock, times(1)).loginSync(ac.capture(), ac.capture());
//        List<String> captures = ac.getAllValues();
//        assertThat(captures.get(0), is(USERNAME));
//        assertThat(captures.get(1), is(PASSWORD));
//    }
//
//    @Test
//    public void loginSync_success_authTokenCached() throws Exception {
//        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
//        SUT.loginSync(USERNAME, PASSWORD);
//        verify(mAuthTokenCacheMock).cacheAuthToken(ac.capture());
//        assertThat(ac.getValue(), is(AUTH_TOKEN));
//    }
//
//    @Test
//    public void loginSync_generalError_authTokenNotCached() throws Exception {
//        generalError();
//        SUT.loginSync(USERNAME, PASSWORD);
//        verifyNoMoreInteractions(mAuthTokenCacheMock);
//    }
//
//    @Test
//    public void loginSync_authError_authTokenNotCached() throws Exception {
//        authError();
//        SUT.loginSync(USERNAME, PASSWORD);
//        verifyNoMoreInteractions(mAuthTokenCacheMock);
//    }
//
//    @Test
//    public void loginSync_serverError_authTokenNotCached() throws Exception {
//        serverError();
//        SUT.loginSync(USERNAME, PASSWORD);
//        verifyNoMoreInteractions(mAuthTokenCacheMock);
//    }
//
//    @Test
//    public void loginSync_success_loggedInEventPosted() throws Exception {
//        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
//        SUT.loginSync(USERNAME, PASSWORD);
//        verify(mEventBusPosterMock).postEvent(ac.capture());
//        assertThat(ac.getValue(), is(instanceOf(LoggedInEvent.class)));
//    }
//
//    @Test
//    public void loginSync_generalError_noInteractionWithEventBusPoster() throws Exception {
//        generalError();
//        SUT.loginSync(USERNAME, PASSWORD);
//        verifyNoMoreInteractions(mEventBusPosterMock);
//    }
//
//    @Test
//    public void loginSync_authError_noInteractionWithEventBusPoster() throws Exception {
//        authError();
//        SUT.loginSync(USERNAME, PASSWORD);
//        verifyNoMoreInteractions(mEventBusPosterMock);
//    }
//
//    @Test
//    public void loginSync_serverError_noInteractionWithEventBusPoster() throws Exception {
//        serverError();
//        SUT.loginSync(USERNAME, PASSWORD);
//        verifyNoMoreInteractions(mEventBusPosterMock);
//    }
//
//    @Test
//    public void loginSync_success_successReturned() throws Exception {
//        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
//        assertThat(result, is(LoginUseCaseSync.UseCaseResult.SUCCESS));
//    }
//
//    @Test
//    public void loginSync_serverError_failureReturned() throws Exception {
//        serverError();
//        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
//        assertThat(result, is(LoginUseCaseSync.UseCaseResult.FAILURE));
//    }
//
//    @Test
//    public void loginSync_authError_failureReturned() throws Exception {
//        authError();
//        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
//        assertThat(result, is(LoginUseCaseSync.UseCaseResult.FAILURE));
//    }
//
//    @Test
//    public void loginSync_generalError_failureReturned() throws Exception {
//        generalError();
//        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
//        assertThat(result, is(LoginUseCaseSync.UseCaseResult.FAILURE));
//    }
//
//    @Test
//    public void loginSync_networkError_networkErrorReturned() throws Exception {
//        networkError();
//        LoginUseCaseSync.UseCaseResult result = SUT.loginSync(USERNAME, PASSWORD);
//        assertThat(result, is(LoginUseCaseSync.UseCaseResult.NETWORK_ERROR));
//    }
//
//    private void networkError() throws Exception {
//        doThrow(new NetworkErrorException())
//                .when(mLoginHttpEndpointSyncMock).loginSync(any(String.class), any(String.class));
//    }
//
//    private void success() throws NetworkErrorException {
//        when(mLoginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
//                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.SUCCESS, AUTH_TOKEN));
//    }
//
//    private void generalError() throws Exception {
//        when(mLoginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
//                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, ""));
//    }
//
//    private void authError() throws Exception {
//        when(mLoginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
//                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, ""));
//    }
//
//    private void serverError() throws Exception {
//        when(mLoginHttpEndpointSyncMock.loginSync(any(String.class), any(String.class)))
//                .thenReturn(new LoginHttpEndpointSync.EndpointResult(LoginHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, ""));
//    }