package com.techyourchance.unittesting.questions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {


    //region constants
    private static final String QUESTION_ID = "ID";
    //end region constants

    //region helper fields
    FetchQuestionDetailsUseCase SUT;
    @Mock FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpoint;

    private ListenerTd listener1;
    private ListenerTd listener2;
    //end region helper fields


    @Before
    public void setup() throws Exception {
        SUT = new FetchQuestionDetailsUseCase(fetchQuestionDetailsEndpoint);
        listener1=new ListenerTd();
        listener2=new ListenerTd();
    }

    @Test
    public void fetchLastActiveQuestionsAndNotify_success_listenersNotifiedWithCorrectData() {
        // Arrange
        success();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        QuestionDetails expected = new QuestionDetails("id", "title", "body");
        listener1.assertOneSuccessfulCall();
        assertThat(listener1.getData(),is(expected));
        listener2.assertOneSuccessfulCall();
        assertThat(listener2.getData(),is(expected));
    }

    @Test
    public void fetchLastActiveQuestionsAndNotify_failure_listenersNotifiedOfFailure() {
        // Arrange
        failure();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        // Act
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        // Assert
        listener1.assertOneFailingCall();
        listener2.assertOneFailingCall();

    }

    //region helper methods
    private void success() {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            FetchQuestionDetailsEndpoint.Listener listener =
                    (FetchQuestionDetailsEndpoint.Listener) args[1];
            listener.onQuestionDetailsFetched(new QuestionSchema("title", "id", "body"));
            return null;
        }).when(fetchQuestionDetailsEndpoint).fetchQuestionDetails(
                eq(QUESTION_ID),
                any(FetchQuestionDetailsEndpoint.Listener.class)
        );
    }

    private void failure() {
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            FetchQuestionDetailsEndpoint.Listener listener =
                    (FetchQuestionDetailsEndpoint.Listener) args[1];
            listener.onQuestionDetailsFetchFailed();
            return null;
        }).when(fetchQuestionDetailsEndpoint).fetchQuestionDetails(
                eq(QUESTION_ID),
                any(FetchQuestionDetailsEndpoint.Listener.class)
        );
    }
    //end region helper methods

    //region helper classes
    private static class ListenerTd implements FetchQuestionDetailsUseCase.Listener {
        private int mCallCount;
        private boolean mSuccess;
        private QuestionDetails mData;

        @Override
        public void onQuestionDetailsFetched(QuestionDetails questionDetails) {
            mCallCount++;
            mSuccess = true;
            mData = questionDetails;
        }

        @Override
        public void onQuestionDetailsFetchFailed() {
            mCallCount++;
            mSuccess = false;
        }

        public void assertOneSuccessfulCall() {
            if (mCallCount != 1 || !mSuccess) {
                throw new RuntimeException("one successful call assertion failed");
            }
        }

        public void assertOneFailingCall() {
            if (mCallCount != 1 || mSuccess) {
                throw new RuntimeException("one failing call assertion failed");
            }
        }

        public QuestionDetails getData() {
            return mData;
        }
    }
    //end region helper classes
}