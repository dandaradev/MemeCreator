package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;

/**
 * Cria um meme com um texto e uma imagem de fundo.
 *
 * Você pode controlar o texto, a cor do texto e a imagem de fundo.
 */
public class MemeCreator {
    private String textoSuperior;
    private String textoInferior;
    private int corTextoSuperior;
    private int corTextoInferior;

    private int tamanhoTextoSuperior;
    private int tamanhoTextoInferior;


    private Bitmap fundo;
    private DisplayMetrics displayMetrics;


    private Bitmap meme;
    private boolean dirty; // se true, significa que o meme precisa ser recriado.

    public MemeCreator(String textoSuperior, String textoInferior, int corTextoSuperior, int corTextoInferior, int tamanhoTextoSuperior, int tamanhoTextoInferior, Bitmap fundo, DisplayMetrics displayMetrics) {
        this.textoSuperior = textoSuperior;
        this.textoInferior = textoInferior;
        this.corTextoSuperior = corTextoSuperior;
        this.corTextoInferior = corTextoInferior;
        this.tamanhoTextoSuperior = tamanhoTextoSuperior;
        this.tamanhoTextoInferior = tamanhoTextoInferior;
        this.fundo = fundo;
        this.displayMetrics = displayMetrics;
        this.meme = criarImagem();
        this.dirty = false;
    }

    public String getTextoSuperior() {
        return textoSuperior;
    }
    public String getTextoInferior() {
        return textoInferior;
    }

    public void setTextoSuperior(String texto) {
        this.textoSuperior = texto;
        dirty = true;
    }

    public void setTextoInferior(String texto) {
        this.textoInferior = texto;
        dirty = true;
    }


    public int getCorTextoSuperior() {
        return corTextoSuperior;
    }

    public void setCorTextoSuperior(int corTextoSuperior) {
        this.corTextoSuperior = corTextoSuperior;
        dirty = true;
    }

    public int getCorTextoInferior() {
        return corTextoInferior;
    }

    public void setCorTextoInferior(int corTextoInferior) {
        this.corTextoInferior = corTextoInferior;
        dirty = true;
    }

    public int getTamanhoTextoSuperior() {
        return tamanhoTextoSuperior;
    }

    public void setTamanhoTextoSuperior(int tamanhoTextoSuperior) {
        this.tamanhoTextoSuperior = tamanhoTextoSuperior;
        dirty= true;
    }

    public int getTamanhoTextoInferior() {
        return tamanhoTextoInferior;
    }

    public void setTamanhoTextoInferior(int tamanhoTextoInferior) {
        this.tamanhoTextoInferior = tamanhoTextoInferior;
        dirty= true;
    }
    public Bitmap getFundo() {
        return fundo;
    }

    public void setFundo(Bitmap fundo) {
        this.fundo = fundo;
        dirty = true;
    }

    public void rotacionarFundo(float graus) {
        Matrix matrix = new Matrix();
        matrix.postRotate(graus);
        fundo = Bitmap.createBitmap(fundo, 0, 0, fundo.getWidth(), fundo.getHeight(), matrix, true);
        dirty = true;
    }

    public Bitmap getImagem() {
        if (dirty) {
            meme = criarImagem();
            dirty = false;
        }
        return meme;
    }
    protected Bitmap criarImagem() {
        float heightFactor = (float) fundo.getHeight() / fundo.getWidth();
        int width = displayMetrics.widthPixels;
        int height = (int) (width * heightFactor);
        // nao deixa a imagem ocupar mais que 60% da altura da tela.
        if (height > displayMetrics.heightPixels * 0.6) {
            height = (int) (displayMetrics.heightPixels * 0.6);
            width = (int) (height * (1 / heightFactor));
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        Paint paint2 = new Paint();


        Bitmap scaledFundo = Bitmap.createScaledBitmap(fundo, width, height, true);
        canvas.drawBitmap(scaledFundo, 0, 0, new Paint());

        paint.setColor(corTextoSuperior);
        paint2.setColor(corTextoInferior);
        paint.setAntiAlias(true);
        paint2.setAntiAlias(true);
        paint.setTextSize(tamanhoTextoSuperior);
        paint2.setTextSize(tamanhoTextoInferior);
        paint.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paint2.setTypeface(Typeface.create("sans-serif-condensed", Typeface.BOLD));
        paint.setTextAlign(Paint.Align.CENTER);
        paint2.setTextAlign(Paint.Align.CENTER);


        // desenhar texto em cima
        canvas.drawText(textoSuperior, (width / 2.f), (height * 0.15f), paint);

        // desenhar texto embaixo
        canvas.drawText(textoInferior, (width / 2.f), (height * 0.9f), paint2);
        return bitmap;
    }
}
