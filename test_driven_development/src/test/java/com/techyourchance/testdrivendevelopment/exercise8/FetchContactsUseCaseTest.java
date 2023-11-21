package com.techyourchance.testdrivendevelopment.exercise8;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {


    //region constants
    public static final String FILTER_TERM = "Ani";
    public static final String ID = "ID";
    public static final String FULL_NAME = "Animesh Singh";
    public static final String PHONE_NUMBER = "9999999999";
    public static final String IMAGE_URL = "";
    public static final double AGE = 20.0;
    //end region constants

    //region helper fields
    FetchContactsUseCase SUT;
    @Mock GetContactsHttpEndpoint getContactsHttpEndpoint;
    @Mock FetchContactsUseCase.Listener listenerMock1;
    @Mock FetchContactsUseCase.Listener listenerMock2;

    @Captor ArgumentCaptor<List<ContactSchema>> acListContactItem;
    //end region helper fields
    @Before
    public void setup() throws Exception {
        SUT = new FetchContactsUseCase(getContactsHttpEndpoint);
        success();
    }
    //correct limit passed to endpoint

    @Test
    public void fetchContacts_correctLimitPassedToEndpoints() {
        // Arrange
        ArgumentCaptor<String> acString=ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(getContactsHttpEndpoint).getContacts(acString.capture(),any(GetContactsHttpEndpoint.Callback.class));
        assertThat(acString.getValue(),is(FILTER_TERM));
    }

    // success- all observers notified with correct data

    @Test
    public void fetchContacts_success_observersNotifiedWithCorrectData() {
        // Arrange

        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onGetContactsSucceeded(acListContactItem.capture());
        verify(listenerMock2).onGetContactsSucceeded(acListContactItem.capture());

        List<List<ContactSchema>> captures= acListContactItem.getAllValues();
        List<ContactSchema> captures1=captures.get(0);
        List<ContactSchema> captures2=captures.get(1);
        assertThat(captures1,is(getContacts()));
        assertThat(captures2,is(getContacts()));
    }

    // success- unsubscribed observers not notified
    @Test
    public void fetchContacts_success_unsubscribedObserversNotNotified() {
        // Arrange

        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.unregisterListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onGetContactsSucceeded(any(List.class));
        verifyNoMoreInteractions(listenerMock2);
    }
    // general error - observer notified of failure
    @Test
    public void fetchContacts_generalError_observersNotifiedOfFailure() {
        // Arrange
        generalError();
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onGetContactsFailed();
        verify(listenerMock2).onGetContactsFailed();
    }
    // network error - observer notified of failure
    @Test
    public void fetchContacts_networkError_observersNotifiedOfFailure() {
        // Arrange
        networkError();
        // Act
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTER_TERM);
        // Assert
        verify(listenerMock1).onGetContactsFailed();
        verify(listenerMock2).onGetContactsFailed();
    }

    //region helper methods
    public void success(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args=invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback= (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsSucceeded(getContacts());
                return null;
            }

        }).when(getContactsHttpEndpoint).getContacts(anyString(), ArgumentMatchers.<GetContactsHttpEndpoint.Callback>any());
    }

    public void generalError(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args=invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback= (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }

        }).when(getContactsHttpEndpoint).getContacts(anyString(), ArgumentMatchers.<GetContactsHttpEndpoint.Callback>any());
    }

    public void networkError(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args=invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback= (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }

        }).when(getContactsHttpEndpoint).getContacts(anyString(), ArgumentMatchers.<GetContactsHttpEndpoint.Callback>any());
    }

    public List<ContactSchema> getContacts(){
        List<ContactSchema> schemas =new ArrayList<>();
        schemas.add(new ContactSchema (ID, FULL_NAME, PHONE_NUMBER, IMAGE_URL, AGE));
        return schemas;
    }

    //end region helper methods

    //region helper classes

    //end region helper classes
}