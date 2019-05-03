package kz.iitu.culto.MenuFragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import de.hdodenhof.circleimageview.CircleImageView;
import kz.iitu.culto.R;
import kz.iitu.culto.SideMenuActivity;

public class ProfileFragment extends Fragment {

    private ImageView mProfileImage;
    private TextView mUserName;
    private TextView mLevel;

    private DatabaseReference profileUserRef;
    private DatabaseReference accountUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        accountUserRef = FirebaseDatabase.getInstance().getReference().child("AccountValue").child(currentUserId);
        mProfileImage = view.findViewById(R.id.profile_image);
        mUserName = view.findViewById(R.id.profile_username);
        mLevel = view.findViewById(R.id.account_level);

        profileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                    mUserName.setText(myUserName);
                    Picasso.with(getActivity()).load(myProfileImage).placeholder(R.drawable.profile).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        accountUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String myAccountLevel = dataSnapshot.child("Level").getValue().toString();

                    mLevel.setText(myAccountLevel);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }
}
