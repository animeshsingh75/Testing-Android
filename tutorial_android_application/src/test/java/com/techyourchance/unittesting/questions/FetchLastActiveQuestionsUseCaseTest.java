package com.techyourchance.unittesting.questions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.techyourchance.unittesting.networking.StackoverflowApi;
import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FetchLastActiveQuestionsUseCaseTest {


    //region constants
    public static final String TITLE_1 = "title1";
    public static final String ID_1 = "ID1";
    public static final String BODY_1 = "body1";

    public static final String TITLE_2 = "title2";
    public static final String ID_2 = "ID2";
    public static final String BODY_2 = "body2";
    //end region constants

    //region helper fields
    FetchLastActiveQuestionsUseCase SUT;
    private EndpointTd endpointTd;
    @Mock
    FetchLastActiveQuestionsUseCase.Listener listener1;
    @Mock
    FetchLastActiveQuestionsUseCase.Listener listener2;
    @Captor
    ArgumentCaptor<List<Question>> questionsCaptor;
    //end region helper fields


    @Before
    public void setup() throws Exception {
        endpointTd = new EndpointTd();
        SUT = new FetchLastActiveQuestionsUseCase(endpointTd);

    }

    // success - listeners notified of success with correct data
    @Test
    public void fetchLastActiveQuestionsAndNotify_success_listenersNotifiedWithCorrectData() {
        // Arrange
        success();
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchLastActiveQuestionsAndNotify();
        // Assert
        verify(listener1).onLastActiveQuestionsFetched(questionsCaptor.capture());
        verify(listener2).onLastActiveQuestionsFetched(questionsCaptor.capture());
        List<List<Question>> questionList = questionsCaptor.getAllValues();
        assertThat(questionList.get(0), is(getExpectedQuestions()));
        assertThat(questionList.get(1), is(getExpectedQuestions()));
    }

    //failure - listeners notified of failure
    @Test
    public void fetchLastActiveQuestionsAndNotify_failure_listenersNotifiedOfFailure() {
        // Arrange
        failure();
        // Act
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchLastActiveQuestionsAndNotify();
        // Assert
        verify(listener1).onLastActiveQuestionsFetchFailed();
        verify(listener2).onLastActiveQuestionsFetchFailed();

    }

    //region helper methods
    private void success() {
        //currently no-op
    }

    private void failure() {
        endpointTd.failure = true;
    }

    private List<Question> getExpectedQuestions() {
        List<Question> questions = new LinkedList<>();
        questions.add(new Question(ID_1, TITLE_1));
        questions.add(new Question(ID_2, TITLE_2));
        return questions;
    }
    //end region helper methods

    //region helper classes
    private static class EndpointTd extends FetchLastActiveQuestionsEndpoint {
        public boolean failure;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchLastActiveQuestions(Listener listener) {
            if(failure){
                listener.onQuestionsFetchFailed();
            }else{
                List<QuestionSchema> questionSchemas = new LinkedList<>();
                questionSchemas.add(new QuestionSchema(TITLE_1, ID_1, BODY_1));
                questionSchemas.add(new QuestionSchema(TITLE_2, ID_2, BODY_2));
                listener.onQuestionsFetched(questionSchemas);
            }
        }
    }
    //end region helper classes
}