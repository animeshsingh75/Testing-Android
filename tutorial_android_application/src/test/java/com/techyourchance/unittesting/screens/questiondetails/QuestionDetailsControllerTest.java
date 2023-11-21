package com.techyourchance.unittesting.screens.questiondetails;

import static com.techyourchance.unittesting.testdata.QuestionDetailsTestData.getQuestionDetails1;

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

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {
    private static final QuestionDetails QUESTION_DETAILS = getQuestionDetails1();
    private static final String QUESTION_ID = QUESTION_DETAILS.getId();

    //region constants

    //end region constants

    //region helper fields
    QuestionDetailsController SUT;
    private UseCaseTd useCaseTd;
    @Mock
    ScreensNavigator screensNavigator;
    @Mock
    ToastsHelper toastsHelper;
    @Mock
    QuestionDetailsViewMvc questionDetailsViewMvc;

    //end region helper fields


    @Before
    public void setup() throws Exception {
        useCaseTd = new UseCaseTd();
        SUT = new QuestionDetailsController(useCaseTd, screensNavigator, toastsHelper);
        SUT.bindView(questionDetailsViewMvc);
        SUT.bindQuestionId(QUESTION_ID);
    }

    @Test
    public void onStart_listenersRegistered() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvc).registerListener(SUT);
        useCaseTd.verifyListenerRegistered(SUT);
    }

    @Test
    public void onStop_listenersUnregistered() {
        // Arrange
        // Act
        SUT.onStart();
        SUT.onStop();
        // Assert
        verify(questionDetailsViewMvc).unregisterListener(SUT);
        useCaseTd.verifyListenerUnregistered(SUT);
    }

    @Test
    public void onStart_success_questionDetailsBoundToView() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvc).bindQuestion(QUESTION_DETAILS);
    }


    @Test
    public void onStart_failure_errorToastShown() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(toastsHelper).showUseCaseError();
    }

    @Test
    public void onStart_progressIndicatorShown() {
        // Arrange
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvc).showProgressIndication();
    }

    @Test
    public void onStart_success_progressIndicatorHidden() {
        // Arrange
        success();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvc).hideProgressIndication();
    }

    @Test
    public void onStart_failure_progressIndicatorHidden() {
        // Arrange
        failure();
        // Act
        SUT.onStart();
        // Assert
        verify(questionDetailsViewMvc).hideProgressIndication();
    }

    @Test
    public void onNavigateUp_navigateUp() {
        // Arrange
        // Act
        SUT.onNavigateUpClicked();
        // Assert
        verify(screensNavigator).navigateUp();
    }

    //region helper methods
    private void success() {
        // currently no-op
    }

    private void failure() {
        useCaseTd.mFailure = true;
    }
    //end region helper methods

    //region helper classes
    public class UseCaseTd extends FetchQuestionDetailsUseCase {
        private boolean mFailure;

        public UseCaseTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(String questionId) {
            if (!questionId.equals(QUESTION_ID)) {
                throw new RuntimeException("invalid question ID: " + questionId);
            }
            for (Listener listener : getListeners()) {
                if (mFailure) {
                    listener.onQuestionDetailsFetchFailed();
                } else {
                    listener.onQuestionDetailsFetched(QUESTION_DETAILS);
                }
            }
        }

        public void verifyListenerRegistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }

        public void verifyListenerUnregistered(QuestionDetailsController candidate) {
            for (Listener listener : getListeners()) {
                if (listener == candidate) {
                    throw new RuntimeException("listener not unregistered");
                }
            }
        }
    }
    //end region helper classes
}