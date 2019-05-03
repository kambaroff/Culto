package kz.iitu.culto.MenuFragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kz.iitu.culto.Question;
import kz.iitu.culto.R;

public class QuizFragment extends Fragment {

    private static String TAG = "QuizFragment";
    private static final String KEY_INDEX = "index";


    public List<Integer> storedIndex = new ArrayList<>();

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private int correctAnswers = 6;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView(LayoutInflater,ViewGroup,Bundle) called");
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);




        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("AccountValue").child(currentUserId);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        mQuestionTextView = view.findViewById(R.id.question_text_view);

        mTrueButton = view.findViewById(R.id.true_button);
        mFalseButton = view.findViewById(R.id.false_button);



        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(true);
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);

                storedIndex.add(mCurrentIndex);
                yourGrade(correctAnswers);
            }
        });

        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkAnswer(false);
                mTrueButton.setEnabled(false);
                mFalseButton.setEnabled(false);

                storedIndex.add(mCurrentIndex);
                yourGrade(correctAnswers);
            }
        });

        mPrevButton = (ImageButton) view.findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentIndex == 0) {
                    mCurrentIndex = mQuestionBank.length - 1;
                } else {
                    mCurrentIndex = mCurrentIndex - 1;
                }
                checkRepeat(mCurrentIndex);
                updateQuestion();


            }
        });


        mNextButton = (ImageButton) view.findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                checkRepeat(mCurrentIndex);
                updateQuestion();


            }
        });

        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                checkRepeat(mCurrentIndex);
                updateQuestion();
            }
        });
        updateQuestion();
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }


    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
        } else {
            messageResId = R.string.incorrect_toast;
            correctAnswers--;

        }
        Toast.makeText(getActivity(), messageResId, Toast.LENGTH_SHORT).show();
    }

    private void yourGrade(double answers)
    {
        double percentage = ((answers/6)*100);
        if(storedIndex.size() == 6){
            Toast.makeText(getActivity(),"Your grade: " +  new DecimalFormat("##.##").format(percentage), Toast.LENGTH_SHORT).show();
            double levelpoints = answers/6;

            SendAccountLevelInformation(levelpoints);
        }
    }



    private void SendAccountLevelInformation(double levelPoints)
    {

        String level = new DecimalFormat("##.##").format(levelPoints);

        HashMap accountMap = new HashMap();
        accountMap.put("Level", level);

        profileUserRef.updateChildren(accountMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
            if(task.isSuccessful())
            {
                Toast.makeText(getActivity(),"Your points added to your level!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String message = task.getException().getMessage();
                Toast.makeText(getActivity(), "Error:", Toast.LENGTH_SHORT).show();
            }
            }
        });
    }

    private void checkRepeat( int index){

        if (!storedIndex.contains(index))
        {
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
        else
        {
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        }
    }
}




