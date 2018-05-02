package pedro.com.br.indigo.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pedro.com.br.indigo.R;

public class FragmentoConversasActivity extends android.support.v4.app.Fragment {

    private View convertView;

    public FragmentoConversasActivity() {
    }


    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_fragmento_conversas, container, false);
        }
        return convertView;
    }
}
