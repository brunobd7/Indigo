package pedro.com.br.indigo.Fragmentos;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pedro.com.br.indigo.R;

public class FragmentoAmigosActivity extends android.support.v4.app.Fragment {

    private View convertView;

    public FragmentoAmigosActivity() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_fragmento_amigos, container, false);
        }
        return convertView;
    }
}
