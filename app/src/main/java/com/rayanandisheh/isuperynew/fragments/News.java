package com.rayanandisheh.isuperynew.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;

import com.rayanandisheh.isuperynew.activities.MainActivity;
import com.rayanandisheh.isuperynew.R;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.rayanandisheh.isuperynew.adapters.ViewPagerSimpleAdapter;
import com.rayanandisheh.isuperynew.constant.ConstantValues;
import com.rayanandisheh.isuperynew.models.post_model.PostDetails;
import com.rayanandisheh.isuperynew.models.api_response_model.ErrorResponse;
import com.rayanandisheh.isuperynew.models.post_model.PostMedia;
import com.rayanandisheh.isuperynew.network.APIClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;


public class News extends Fragment implements BaseSliderView.OnSliderClickListener {
    
    View rootView;
    
    ViewPager viewPager;
    TabLayout tabLayout;
    FrameLayout banner_adView;

    SliderLayout sliderLayout;
    PagerIndicator pagerIndicator;

    List<PostMedia> postMediaList;
    List<PostDetails> postsList;

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.currentFragment = ConstantValues.NEWS_PAGE;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.currentFragment = ConstantValues.NEWS_PAGE;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.news, container, false);

        // Enable Drawer Indicator with static variable actionBarDrawerToggle of MainActivity
        MainActivity.actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.actionNews));


        // Binding Layout Views
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.myViewPager);
        banner_adView = (FrameLayout) rootView.findViewById(R.id.banner_adView);
        sliderLayout = (SliderLayout) rootView.findViewById(R.id.banner_slider);
        pagerIndicator = (PagerIndicator) rootView.findViewById(R.id.banner_slider_indicator);
    
        
        postMediaList = new ArrayList<>();
        postsList = new ArrayList<>();
        
        // Request Sticky Posts
        RequestStickyPosts();


        // Setup ViewPagers
        setupViewPager(viewPager);

        // Add corresponding ViewPagers to TabLayouts
        tabLayout.setupWithViewPager(viewPager);


        return rootView;

    }



    //*********** Setup the given ViewPager ********//

    private void setupViewPager(ViewPager viewPager) {

        // Initialize ViewPagerAdapter with ChildFragmentManager for ViewPager
        ViewPagerSimpleAdapter viewPagerAdapter = new ViewPagerSimpleAdapter(getChildFragmentManager());

        // Add the Fragments to the ViewPagerAdapter with TabHeader
        viewPagerAdapter.addFragment(new NewsCategories(), getString(R.string.news_categories));
        viewPagerAdapter.addFragment(new News_All(), getString(R.string.news_all));

        // Attach the ViewPagerAdapter to given ViewPager
        viewPager.setAdapter(viewPagerAdapter);
    }



    //*********** Setup the BannerSlider with the given List of BannerImages ********//

    private void setupBannerSlider() {

        // Initialize new LinkedHashMap<ImageName, ImagePath>
        final LinkedHashMap<String, String> slider_covers = new LinkedHashMap<>();


        for (int i=0;  i< postsList.size();  i++) {
            
            // Put Image's Name and URL to the HashMap slider_covers
            slider_covers.put
                    (
                            String.valueOf(postsList.get(i).getId()),
                            postsList.get(i).getEmbedded().getWpFeaturedmedia().get(0).getSourceUrl()
                    );
        }


        for(String name : slider_covers.keySet()) {
            // Initialize DefaultSliderView
            DefaultSliderView defaultSliderView = new DefaultSliderView(getContext());

            // Set Attributes(Name, Image, Type etc) to DefaultSliderView
            defaultSliderView
                    .description(name)
                    .image(slider_covers.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            // Add DefaultSliderView to the SliderLayout
            sliderLayout.addSlider(defaultSliderView);
        }

        // Set PresetTransformer type of the SliderLayout
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);


        // Check if the size of Images in the Slider is less than 2
        if (slider_covers.size() < 2) {
            // Disable PagerTransformer
            sliderLayout.setPagerTransformer(false, new BaseTransformer() {
                @Override
                protected void onTransform(View view, float v) {
                }
            });

            // Hide Slider PagerIndicator
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        } else {
            // Set custom PagerIndicator to the SliderLayout
            sliderLayout.setCustomIndicator(pagerIndicator);
            // Make PagerIndicator Visible
            sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);
        }
    }
    
    
    
    //*********** Handle the Click Listener on BannerImages of Slider ********//
    
    @Override
    public void onSliderClick(BaseSliderView slider) {
        int position = sliderLayout.getCurrentPosition();
        
        PostDetails postDetails = postsList.get(position);
    
        // Get Product Info
        Bundle bundle = new Bundle();
        bundle.putInt("postID", postDetails.getId());
    
        // Navigate to NewsDescription of selected News
        Fragment fragment = new NewsDescription();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_fragment, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(getString(R.string.actionHome)).commit();
        
    }
    
    
    
    //*********** Request Featured News from the Server based on PageNo. ********//
    
    public void RequestStickyPosts() {
    
        Map<String, String> params = new LinkedHashMap<>();
        params.put("page", "1");
        params.put("per_page", "100");
        params.put("sticky", "true");
        params.put("_embed", "true");
        
        Call<List<PostDetails>> call = APIClient.getInstance()
                .getAllPosts
                        (
                                params
                        );
        
        call.enqueue(new Callback<List<PostDetails>>() {
            @Override
            public void onResponse(Call<List<PostDetails>> call, retrofit2.Response<List<PostDetails>> response) {
    
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        
                        postsList.addAll(response.body());
                        
                        setupBannerSlider();
                    }
                    
                }
                else {
                    Converter<ResponseBody, ErrorResponse> converter = APIClient.retrofit.responseBodyConverter(ErrorResponse.class, new Annotation[0]);
                    ErrorResponse error;
                    try {
                        error = converter.convert(response.errorBody());
                    } catch (IOException e) {
                        error = new ErrorResponse();
                    }
        
                    Toast.makeText(getContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<PostDetails>> call, Throwable t) {
                Toast.makeText(getContext(), "خطایی رخ داد!", Toast.LENGTH_LONG).show();
            }
        });
    }
    
}

