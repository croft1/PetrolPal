package m.petrolpal.Tools;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import m.petrolpal.TabFragments.MapFragment;
import m.petrolpal.TabFragments.StatsFragment;
import m.petrolpal.TabFragments.SummaryFragment;


/**
 * Created by Michaels on 3/5/2016.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentStatePagerAdapter {





    public static final int FUEL_TAB_SUMMARY_POSITION = 0;
    public static final int FUEL_TAB_LIST_POSITION = 1;
    public static final int FUEL_TAB_MAP_POSITION = 2;
    public static final int FUEL_TAB_STATS_POSITION = 3;

    //tab visual identifiers - text or icon
    private String[] tabTitles = new String[] {"Summary", "List", "Map", "Stats"};
    final int PAGE_COUNT = tabTitles.length;
    private int[] tabImgResId = new int[] {
            //android.R.drawable.ic_menu_myplaces,
            PAGE_COUNT + 1, PAGE_COUNT + 1 , PAGE_COUNT + 1, PAGE_COUNT + 1};       //pagecount + 1

    private Context c;

    public FragmentPagerAdapter(FragmentManager fm, Context c){
        super(fm);
        this.c = c;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position)  {


        switch(position){
            case FUEL_TAB_SUMMARY_POSITION:
                return m.petrolpal.TabFragments.SummaryFragment.newInstance(position);
            case FUEL_TAB_LIST_POSITION:
                return m.petrolpal.TabFragments.ListFragment.newInstance(position);
            case FUEL_TAB_MAP_POSITION:
                return m.petrolpal.TabFragments.MapFragment.newInstance(position);
            case FUEL_TAB_STATS_POSITION:
                return m.petrolpal.TabFragments.StatsFragment.newInstance(position);
            default:
                return m.petrolpal.TabFragments.SummaryFragment.newInstance(position);
        }


    }

    @Override
    public int getItemPosition(Object object) {
//        MapFragment mf = (MapFragment) object;
//        StatsFragment sf = (StatsFragment) object;
//        SummaryFragment summF = (SummaryFragment) object;
//        ListFragment lf = (ListFragment) object;
//        try{
//
//        }catch(Exception e){
//            //not a map frag
//        }



        return super.getItemPosition(object);
    }

    //for tab id - icon or text
    @Override
    public CharSequence getPageTitle(int position) {

        //for icon tab lables
        if(tabImgResId[position] == PAGE_COUNT + 1){

            return tabTitles[position].toUpperCase();

        }else{

            Drawable image = ContextCompat.getDrawable(c, tabImgResId[position]);
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
            SpannableString imgStr = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
            imgStr.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return imgStr;
        }


    }




}
