package com.techyourchance.testdrivendevelopment.exercise7;

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;

public class FetchReputationUseCaseSync {

    private final GetReputationHttpEndpointSync getReputationHttpEndpointSync;

    public FetchReputationUseCaseSync(GetReputationHttpEndpointSync getReputationHttpEndpointSync) {
        this.getReputationHttpEndpointSync = getReputationHttpEndpointSync;
    }


    public UseCaseResult getReputationSync() {
        GetReputationHttpEndpointSync.EndpointStatus result= getReputationHttpEndpointSync.getReputationSync().getStatus();
        switch (result) {
            case GENERAL_ERROR:
            case NETWORK_ERROR:
                return UseCaseResult.FAILURE;
            case SUCCESS:
                return UseCaseResult.SUCCESS;
            default:
                throw new RuntimeException("invalid result");
        }
    }

    public int fetchReputation() {
        return getReputationHttpEndpointSync.getReputationSync().getReputation();
    }


    public enum UseCaseResult {
        FAILURE, SUCCESS
    }
}
