package com.techyourchance.testdrivendevelopment.exercise7;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {
    FetchReputationUseCaseSync SUT;
    //region constants
    int mReputationSuccess = 5;
    int mReputationFailure = 0;
    //end region constants

    //region helper fields
    @Mock
    GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    //end region helper fields
    @Before
    public void setup() throws Exception {
        SUT = new FetchReputationUseCaseSync(getReputationHttpEndpointSync);
        success();
    }

    @Test
    public void fetchReputation_success_successReturned() {
        // Arrange
        // Act
        FetchReputationUseCaseSync.UseCaseResult result=SUT.getReputationSync();
        // Assert
        MatcherAssert.assertThat(result, is(FetchReputationUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchReputation_generalError_failureReturned() {
        // Arrange
        generalError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result=SUT.getReputationSync();
        // Assert
        MatcherAssert.assertThat(result, is(FetchReputationUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchReputation_NetworkError_failureReturned() {
        // Arrange
        networkError();
        // Act
        FetchReputationUseCaseSync.UseCaseResult result=SUT.getReputationSync();
        // Assert
        MatcherAssert.assertThat(result, is(FetchReputationUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchReputation_success_reputationReturned() {
        // Arrange
        // Act
        int result=SUT.fetchReputation();
        // Assert
        MatcherAssert.assertThat(result, is(mReputationSuccess));
    }
    @Test
    public void fetchReputation_generalError_reputationReturned() {
        // Arrange
        generalError();
        // Act
        int result=SUT.fetchReputation();
        // Assert
        MatcherAssert.assertThat(result, is(mReputationFailure));
    }

    @Test
    public void fetchReputation_NetworkError_reputationReturned() {
        // Arrange
        networkError();
        // Act
        int result=SUT.fetchReputation();
        // Assert
        MatcherAssert.assertThat(result, is(mReputationFailure));
    }

    //region helper methods
    public void success() {
        GetReputationHttpEndpointSync.EndpointResult endpointResult = new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.SUCCESS, mReputationSuccess);
        when(getReputationHttpEndpointSync.getReputationSync()).thenReturn(endpointResult);
    }
    public void generalError() {
        GetReputationHttpEndpointSync.EndpointResult endpointResult = new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR, mReputationFailure);
        when(getReputationHttpEndpointSync.getReputationSync()).thenReturn(endpointResult);
    }

    public void networkError() {
        GetReputationHttpEndpointSync.EndpointResult endpointResult = new GetReputationHttpEndpointSync.EndpointResult(GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR, mReputationFailure);
        when(getReputationHttpEndpointSync.getReputationSync()).thenReturn(endpointResult);
    }
    //end region helper methods

    //region helper classes

    //end region helper classes
}