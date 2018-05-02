package pedro.com.br.indigo;

import android.app.Fragment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import pedro.com.br.indigo.Adapters.ViewPagerAdapter;
import pedro.com.br.indigo.Fragmentos.FragmentoAmigosActivity;
import pedro.com.br.indigo.Fragmentos.FragmentoConversasActivity;
import pedro.com.br.indigo.Fragmentos.FragmentoSolicitacoesActivity;

public class IndigoHomeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private TabLayout tabLayout;
    private ViewPager vpConteudo;
    private ViewPagerAdapter vpAdapter;
    private ArrayList<android.support.v4.app.Fragment> arrayFragmentos;
    private ArrayList<String> arrayTitulos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indigo_home);

        toolbar = findViewById(R.id.toolbarHome);
        setSupportActionBar(toolbar);
        //HABILITANDO O MENU ou SETA A ESQUERDA
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tlHome);
        vpConteudo = (ViewPager) findViewById(R.id.vpConteudo);


        carregarFragmentos();
        carregarTitulos();
        viewPagerTabLayout();
    }

    private void carregarTitulos() {

        arrayTitulos = new ArrayList<>();
        arrayTitulos.add("Solicitacoes");
        arrayTitulos.add("Conversas");
        arrayTitulos.add("Amigos");

    }

    private void carregarFragmentos(){

        arrayFragmentos= new ArrayList<>();
        arrayFragmentos.add(new FragmentoSolicitacoesActivity());
        arrayFragmentos.add(new FragmentoConversasActivity());
        arrayFragmentos.add(new FragmentoAmigosActivity());

    }

    private void viewPagerTabLayout(){

        vpAdapter = new ViewPagerAdapter(getSupportFragmentManager(), arrayFragmentos, arrayTitulos);
        vpConteudo.setAdapter(vpAdapter);

        tabLayout.setupWithViewPager(vpConteudo);
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
                break;
            case android.R.id.home:

                finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
