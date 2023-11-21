package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    UsersCache usersCache;

    FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync fetchUserHttpEndpointSync, UsersCache usersCache) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        try {
            if (usersCache.getUser(userId) != null) {
                return new UseCaseResult(Status.SUCCESS, usersCache.getUser(userId));
            }
            FetchUserHttpEndpointSync.EndpointResult result;
            result = fetchUserHttpEndpointSync.fetchUserSync(userId);
            if (result.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR) {
                return new UseCaseResult(Status.FAILURE, null);
            }

            if (result.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR) {
                return new UseCaseResult(Status.FAILURE, null);
            }
            User user = new User(result.getUserId(), result.getUsername());
            usersCache.cacheUser(user);
            return new UseCaseResult(Status.SUCCESS, user);
        } catch (NetworkErrorException e) {
            e.printStackTrace();
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }

    }
}
