package pedro.com.br.indigo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class IndigoHomeActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indigo_home);

        toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        //HABILITANDO O MENU A ESQUERDA
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_indigo_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case R.id.configuracoes_id:
                Toast.makeText(this, "Configuracoes Selecionada", Toast.LENGTH_SHORT).show();
                break;
            case R.id.perfil_id:
                 Toast.makeText(this, "Perfil Selecionado", Toast.LENGTH_SHORT).show();
                 break;
            case R.id.sobre_id:
                Toast.makeText(this, "Sobre o Indigo Selecionado", Toast.LENGTH_SHORT).show();
                break;
            case R.id.pesquisa_id:
                Toast.makeText(this, "Pesquisa Selecionada", Toast.LENGTH_SHORT).show();
            case android.R.id.home:

                finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
