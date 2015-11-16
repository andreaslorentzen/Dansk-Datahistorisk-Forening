package app.ddf.danskdatahistoriskforening;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar registerToolbar;

    /**
     * http://developer.android.com/training/animation/screen-slide.html
     */

    private ViewPager viewPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerToolbar = (Toolbar) findViewById(R.id.register_toolbar);
        registerToolbar.setTitle("Registrer genstand");
        registerToolbar.setTitleTextColor(-1); // #FFF
        registerToolbar.setNavigationIcon(R.drawable.ic_close);
        setSupportActionBar(registerToolbar);

        // Instantiate a ViewPager and a PagerAdapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mPagerAdapter);

        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tab_strip);
        pagerSlidingTabStrip.setViewPager(viewPager);

   //     viewPager.setPageTransformer(false, new ZoomOutPageTransformer());
   //    ((LinearLayout.LayoutParams) viewPager.getLayoutParams()).weight = 1;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }



    @Override
    public void onBackPressed() {
        prompt();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_register_done:
                save();
                return true;
            case android.R.id.home:
                prompt();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void save() {
        itemFragment.getTitle();
        itemFragment.getImages();
        itemFragment.getRecoording();
    }

    private void prompt(){
        finish();
    }

    private RegisterItemFragment itemFragment = new RegisterItemFragment();
    private RegisterDetailsFragment detailsFragment = new RegisterDetailsFragment();
    private RegisterDescriptionFragment descriptionFragment = new RegisterDescriptionFragment();

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment[] fragments = new Fragment[3];

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
            fragments[0] = itemFragment;
            fragments[1] = detailsFragment;
            fragments[2] = descriptionFragment;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];

        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Genstand";
                case 1:
                    return "Oplysninger";
                case 2:
                    return "Beskrivelse";
            }

            return null;
        }
    }

}
