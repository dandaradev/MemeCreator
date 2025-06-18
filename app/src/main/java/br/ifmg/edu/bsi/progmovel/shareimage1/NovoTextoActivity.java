package br.ifmg.edu.bsi.progmovel.shareimage1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NovoTextoActivity extends AppCompatActivity {

    public static String EXTRA_TEXTO_SUPERIOR_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_superior_atual";
    public static String EXTRA_TEXTO_INFERIOR_ATUAL = "br.ifmg.edu.bsi.progmovel.shareimage1.texto_inferior_atual";
    public static String EXTRA_COR_ATUAL_SUPERIOR = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual_superior";
    public static String EXTRA_COR_ATUAL_INFERIOR = "br.ifmg.edu.bsi.progmovel.shareimage1.cor_atual_inferior";

    public static String EXTRA_NOVO_TEXTO_SUPERIOR = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto_superior";
    public static String EXTRA_NOVO_TEXTO_INFERIOR = "br.ifmg.edu.bsi.progmovel.shareimage1.novo_texto_superior_inferior";

    public static String EXTRA_NOVA_COR_SUPERIOR = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor_superior";
    public static String EXTRA_NOVA_COR_INFERIOR = "br.ifmg.edu.bsi.progmovel.shareimage1.nova_cor_inferior";


    public static String EXTRA_TAMANHO_ATUAL_SUPERIOR= "br.ifmg.edu.bsi.progmovel.shareimage1.tamanho_atual_superior";
    public static String EXTRA_TAMANHO_ATUAL_INFERIOR= "br.ifmg.edu.bsi.progmovel.shareimage1.tamanho_atual_inferior";

    public static String EXTRA_NOVO_TAMANHO_SUPERIOR= "br.ifmg.edu.bsi.progmovel.shareimage1.novo_tamanho_superior";
    public static String EXTRA_NOVO_TAMANHO_INFERIOR= "br.ifmg.edu.bsi.progmovel.shareimage1.novo_tamanho_inferior";


    private EditText etTextoSuperior;
    private EditText etTextoInferior;
    private EditText etCorSuperior;
    private EditText etCorInferior;

    private EditText etTamanhoSuperior;
    private EditText etTamanhoInferior;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_texto);

        etTextoSuperior = findViewById(R.id.etTextoSuperior);
        etTextoInferior = findViewById(R.id.etTextoInferior);
        etCorSuperior = findViewById(R.id.etCorSuperior);
        etCorInferior = findViewById(R.id.etCorInferior);
        etTamanhoSuperior = findViewById(R.id.etTamanhoSuperior);
        etTamanhoInferior = findViewById(R.id.etTamanhoInferior);
        Intent intent = getIntent();
        String textoSuperiorAtual = intent.getStringExtra(EXTRA_TEXTO_SUPERIOR_ATUAL);
        String textoInferiorAtual = intent.getStringExtra(EXTRA_TEXTO_INFERIOR_ATUAL);
        String corAtualSuperior = intent.getStringExtra(EXTRA_COR_ATUAL_SUPERIOR);
        String corAtualInferior = intent.getStringExtra(EXTRA_COR_ATUAL_INFERIOR);

        String tamanhoAtualSuperior = intent.getStringExtra(EXTRA_TAMANHO_ATUAL_SUPERIOR);
        String tamanhoAtualInferior = intent.getStringExtra(EXTRA_TAMANHO_ATUAL_INFERIOR);

        etTextoSuperior.setText(textoSuperiorAtual);
        etTextoInferior.setText(textoInferiorAtual);
        etCorSuperior.setText(corAtualSuperior);
        etCorInferior.setText(corAtualInferior);
        etTamanhoSuperior.setText(tamanhoAtualSuperior);
        etTamanhoInferior.setText(tamanhoAtualInferior);

    }

    public void enviarNovoTexto(View v) {
        String novoTextoSuperior = etTextoSuperior.getText().toString();
        String novoTextoInferior = etTextoInferior.getText().toString();
        String novaCorSuperior = etCorSuperior.getText().toString();
        String novaCorInfrior = etCorInferior.getText().toString();
        String novoTamanhoSuperior = etTamanhoSuperior.getText().toString();
        String novoTamanhoInferior = etTamanhoInferior.getText().toString();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_NOVO_TEXTO_SUPERIOR, novoTextoSuperior);
        intent.putExtra(EXTRA_NOVO_TEXTO_INFERIOR, novoTextoInferior);
        intent.putExtra(EXTRA_NOVA_COR_SUPERIOR, novaCorSuperior);
        intent.putExtra(EXTRA_NOVA_COR_INFERIOR, novaCorInfrior);
        intent.putExtra(EXTRA_NOVO_TAMANHO_SUPERIOR, novoTamanhoSuperior);
        intent.putExtra(EXTRA_NOVO_TAMANHO_INFERIOR, novoTamanhoInferior);

        setResult(RESULT_OK, intent);
        finish();
    }
}