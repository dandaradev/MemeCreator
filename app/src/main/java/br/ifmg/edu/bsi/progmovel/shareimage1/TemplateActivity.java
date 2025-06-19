package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class TemplateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);
    }
    public void selecionarTemplate(View view) {
        String nomeImagem = (String) view.getTag();
        Intent intent = new Intent();
        intent.putExtra("templateSelecionado", nomeImagem);
        setResult(Activity.RESULT_OK, intent);
        finish(); // fecha a tela de templates dps de selecionar template
    }

    public void fecharTela(View view) {
        finish(); // botão fechar
    }

}


