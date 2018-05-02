package pedro.com.br.indigo.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    private ArrayList<Fragment> arrayFragmentos;
    private ArrayList<String> arrayTitulos;

    public ViewPagerAdapter(FragmentManager fm, ArrayList<Fragment> arrayFragmentos, ArrayList<String> arrayTitulos) {
        super(fm);

        this.arrayFragmentos = arrayFragmentos;
        this.arrayTitulos = arrayTitulos;
    }

    @Override
    public Fragment getItem(int position) {
        return arrayFragmentos.get(position);
    }

    @Override
    public int getCount() {
        return arrayFragmentos.size();
    }

    public CharSequence getPageTitle(int position){
        return arrayTitulos.get(position);
    }
}
