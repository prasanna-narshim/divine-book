package ram.king.com.divinebook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ram.king.com.divinebook.R;
import ram.king.com.divinebook.activity.MainActivity;
import ram.king.com.divinebook.activity.UserAllPostActivity;
import ram.king.com.divinebook.models.User;
import ram.king.com.divinebook.util.AppConstants;
import ram.king.com.divinebook.util.AppUtil;
import ram.king.com.divinebook.util.MessageEvent;
import ram.king.com.divinebook.viewholder.UserViewHolder;

public abstract class UserListFragment extends BaseFragment {

    private static final String TAG = "UserListFragment";
    SharedPreferences sharedPref;
    // [END define_database_reference]
    String preferredLanguage;
    Intent userAllPostIntent;
    Uri photoUrl;
    // [START define_database_reference]
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<User, UserViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    //private InterstitialAd mInterstitialAd;
    private ProgressBar mProgressBar;

    public UserListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View rootView = inflater.inflate(R.layout.fragment_all_users, container, false);

        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]

        mRecycler = (RecyclerView) rootView.findViewById(R.id.users_list);
        mRecycler.setHasFixedSize(true);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(activity);
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        setupAdapterWithQuery();

        //Ad mob
        /*mInterstitialAd = new InterstitialAd(activity);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        // [END instantiate_interstitial_ad]

        // [START create_interstitial_ad_listener]
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                if (userAllPostIntent != null)
                    startActivity(userAllPostIntent);
            }

            @Override
            public void onAdLoaded() {
                // Ad received, ready to display
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }

            @Override
            public void onAdFailedToLoad(int i) {
                // See https://goo.gl/sCZj0H for possible error codes.
                Log.w(TAG, "onAdFailedToLoad:" + i);
            }
        });*/
    }

    /**
     * Load a new interstitial ad asynchronously.
     */
    // [START request_new_interstitial]
   /* private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd.loadAd(adRequest);
    }*/
    // [END request_new_interstitial]
    public void setupAdapterWithQuery() {
        Query usersQuery = getQuery(mDatabase);

        mAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(User.class, R.layout.item_user,
                UserViewHolder.class, usersQuery) {
            @Override
            protected void populateViewHolder(final UserViewHolder viewHolder, final User model, final int position) {
                final DatabaseReference usersRef = getRef(position);

                // Set click listener for the whole post view
                final String usersKey = usersRef.getKey();

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                viewHolder.bindToPost(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View topUserLayoutView) {
                        if (activity instanceof MainActivity) {
                            AppUtil.putString(activity, AppConstants.PREF_USER_POST_QUERY, usersKey);
                            userAllPostIntent = new Intent(activity, UserAllPostActivity.class);
                            userAllPostIntent.putExtra(AppConstants.EXTRA_DISPLAY_NAME, model.displayName);
                            startActivityUserAllPost();
                            /*if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else
                                //startActivityUserAllPost();*/
                        } else
                            return;
                    }
                });

                //Custom email and icon
                //vishnu
                if (model.email.equals(AppConstants.VISHNU_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.VISHNU_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.VISHNU_MANTRAS_NAME);
                }
                //vishnu tamil
                else if (model.email.equals(AppConstants.VISHNU_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.VISHNU_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.VISHNU_MANTRAS_NAME_TAMIL);
                }
                //shiva
                else if (model.email.equals(AppConstants.SHIVA_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.SHIVA_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.SHIVA_MANTRAS_NAME);
                }
                //shiva tamil
                else if (model.email.equals(AppConstants.SHIVA_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.SHIVA_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.SHIVA_MANTRAS_NAME_TAMIL);
                }
                //sai baba
                else if (model.email.equals(AppConstants.SAI_BABA_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.SAIBABA_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.SAI_BABA_MANTRAS_NAME);
                }
                //sai baba tamil
                else if (model.email.equals(AppConstants.SAI_BABA_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.SAIBABA_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.SAI_BABA_MANTRAS_NAME_TAMIL);
                }
                //ganapathy
                else if (model.email.equals(AppConstants.GANAPATHY_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.GANAPATHY_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.GANAPATHY_MANTRAS_NAME);
                }
                //ganapathy tamil
                else if (model.email.equals(AppConstants.GANAPATHY_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.GANAPATHY_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.GANAPATHY_MANTRAS_NAME_TAMIL);
                }
                //krishna
                else if (model.email.equals(AppConstants.KRISHNA_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.KRISHNA_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.KRISHNA_MANTRAS_NAME);
                }
                //krishna tamil
                else if (model.email.equals(AppConstants.KRISHNA_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.KRISHNA_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.KRISHNA_MANTRAS_NAME_TAMIL);
                }
                //lakshmi
                else if (model.email.equals(AppConstants.LAKSHMI_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.LAKSHMI_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.LAKSHMI_MANTRAS_NAME);
                }
                //Purana english
                else if (model.email.equals(AppConstants.PURANAS_STORY_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.PURANAS_STORY_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.PURANAS_STORY_NAME);
                }
                //hanuman english
                else if (model.email.equals(AppConstants.HANUMAN_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.HANUMAN_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.HANUMAN_MANTRAS_NAME);
                }
                //lakshmi tamil
                else if (model.email.equals(AppConstants.LAKSHMI_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.LAKSHMI_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.LAKSHMI_MANTRAS_NAME_TAMIL);
                }
                //murugan tamil
                else if (model.email.equals(AppConstants.MURUGAN_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.MURUGAN_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.MURUGAN_MANTRAS_NAME_TAMIL);

                }
                //Ramanujar
                else if (model.email.equals(AppConstants.RAMANUJAR_MANTRAS_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.RAMANUJAR_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.RAMANUJAR_MANTRAS_NAME);

                }
                //Ramanujar tamil
                else if (model.email.equals(AppConstants.RAMANUJAR_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.RAMANUJAR_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.RAMANUJAR_MANTRAS_NAME_TAMIL);

                }
                //Upanishad
                else if (model.email.equals(AppConstants.UPANISHAD_EMAIL)) {
                    photoUrl = Uri.parse(AppConstants.UPANISHAD_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.UPANISHAD_NAME);

                }
                //Upanishads tamil
                else if (model.email.equals(AppConstants.UPANISHAD_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.UPANISHAD_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.UPANISHAD_NAME_TAMIL);

                }
                //Purana tamil
                else if (model.email.equals(AppConstants.PURANAS_STORY_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.PURANAS_STORY_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.PURANAS_STORY_NAME_TAMIL);
                }//hanuman tamil
                else if (model.email.equals(AppConstants.HANUMAN_MANTRAS_EMAIL_TAMIL)) {
                    photoUrl = Uri.parse(AppConstants.HANUMAN_MANTRAS_IMAGE);
                    Glide
                            .with(activity)
                            .load(photoUrl)
                            .into(viewHolder.authorPhoto);

                    viewHolder.author.setText(AppConstants.HANUMAN_MANTRAS_NAME_TAMIL);
                } else {
                    Glide
                            .with(activity)
                            .load(model.photoUrl)
                            .into(viewHolder.authorPhoto);
                }


            }

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                mProgressBar.setVisibility(View.GONE);
                mRecycler.setVisibility(View.VISIBLE);
                //AppUtil.getDynamicLink(activity);
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    private void startActivityUserAllPost() {
        startActivity(userAllPostIntent);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent event) {
        if (event.getMessage().equals("changed")) {
            setupAdapterWithQuery();
            mAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.VISIBLE);
            sharedPref = activity.getSharedPreferences(
                    getString(R.string.preference_file), Context.MODE_PRIVATE);
            preferredLanguage = sharedPref.getString(AppConstants.PREFERRED_LANGUAGE, AppConstants.DEFAULT_LANGUAGE);

            if (preferredLanguage.equals(AppConstants.TAMIL_LANGUAGE))
                ((AppCompatActivity) activity).getSupportActionBar().setTitle(getResources().getString(R.string.app_name_tamil));
            else
                ((AppCompatActivity) activity).getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        } else if (event.getMessage().equals("refresh")) {
            mManager.scrollToPositionWithOffset(mAdapter.getItemCount(), 0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (!mInterstitialAd.isLoaded()) {
            requestNewInterstitial();
        }*/
    }

    // [END post_stars_transaction]

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);


}
