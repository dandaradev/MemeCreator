package br.ifmg.edu.bsi.progmovel.shareimage1;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.content.Context;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import android.view.MotionEvent;


import java.io.BufferedOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Activity que cria uma imagem com um texto e imagem de fundo.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private MemeCreator memeCreator;
    private boolean textoSuperiorSelecionado = false;
    private boolean textoInferiorSelecionado = false;
    private float startX;

    private float offsetYSuperior = 0.15f;
    private float offsetYInferior = 0.9f;
    private float offsetXSuperior = 0.5f;
    private float offsetXInferior = 0.5f;


    private final ActivityResultLauncher<Intent> startNovoTexto = registerForActivityResult(new StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            String novoTextoSuperior = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO_SUPERIOR);
                            String novoTextoInferior = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TEXTO_INFERIOR);
                            String novaCorSuperior = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVA_COR_SUPERIOR);
                            String novaCorInferior = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVA_COR_INFERIOR);
                            String novoTamanhoSuperior = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TAMANHO_SUPERIOR);
                            String novoTamanhoInferior = intent.getStringExtra(NovoTextoActivity.EXTRA_NOVO_TAMANHO_INFERIOR);
                            if (novaCorSuperior == null && novaCorInferior == null) {
                                Toast.makeText(MainActivity.this, "Cor desconhecida. Usando preto no lugar.", Toast.LENGTH_SHORT).show();
                                novaCorSuperior = "BLACK";
                                novaCorInferior = "BLACK";
                            }
                            memeCreator.setTextoSuperior(novoTextoSuperior);
                            memeCreator.setTextoInferior(novoTextoInferior);
                            memeCreator.setCorTextoSuperior(Color.parseColor(novaCorSuperior.toUpperCase()));
                            memeCreator.setCorTextoInferior(Color.parseColor(novaCorInferior.toUpperCase()));
                            memeCreator.setTamanhoTextoSuperior(Integer.parseInt(novoTamanhoSuperior));
                            memeCreator.setTamanhoTextoInferior(Integer.parseInt(novoTamanhoInferior));
                            mostrarImagem();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<PickVisualMediaRequest> startImagemFundo = registerForActivityResult(new PickVisualMedia(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result == null) {
                        return;
                    }
                    try (ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(result, "r")) {
                        Bitmap imagemFundo = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), result);
                        memeCreator.setFundo(imagemFundo);

                        // descobrir se é preciso rotacionar a imagem
                        FileDescriptor fd = pfd.getFileDescriptor();
                        ExifInterface exif = new ExifInterface(fd);
                        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                            memeCreator.rotacionarFundo(90);
                        }

                        mostrarImagem();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            });

    private ActivityResultLauncher<String> startWriteStoragePermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean result) {
                    if (!result) {
                        Toast.makeText(MainActivity.this, "Sem permissão de acesso a armazenamento do celular.", Toast.LENGTH_SHORT).show();
                    } else {
                        compartilhar(null);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        // Configuração do toque longo e do movimento dos textos
        imageView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        float touchY = event.getY() / imageView.getHeight();
                        float touchX = event.getX() / imageView.getWidth();

                        if (Math.abs(touchY - offsetYSuperior) < 0.1f) {
                            textoSuperiorSelecionado = true;
                            vibrar();
                        } else if (Math.abs(touchY - offsetYInferior) < 0.1f) {
                            textoInferiorSelecionado = true;
                            vibrar();
                        }

                        startY = event.getY();
                        startX = event.getX();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        if (textoSuperiorSelecionado || textoInferiorSelecionado) {
                            float deltaY = (event.getY() - startY) / imageView.getHeight();
                            float deltaX = (event.getX() - startX) / imageView.getWidth();

                            if (textoSuperiorSelecionado) {
                                offsetYSuperior += deltaY;
                                offsetXSuperior += deltaX;
                                offsetYSuperior = Math.max(0.05f, Math.min(0.95f, offsetYSuperior));
                                offsetXSuperior = Math.max(0.05f, Math.min(0.95f, offsetXSuperior));
                                memeCreator.setOffsetYSuperior(offsetYSuperior);
                                memeCreator.setOffsetXSuperior(offsetXSuperior);
                            } else {
                                offsetYInferior += deltaY;
                                offsetXInferior += deltaX;
                                offsetYInferior = Math.max(0.05f, Math.min(0.95f, offsetYInferior));
                                offsetXInferior = Math.max(0.05f, Math.min(0.95f, offsetXInferior));
                                memeCreator.setOffsetYInferior(offsetYInferior);
                                memeCreator.setOffsetXInferior(offsetXInferior);
                            }

                            mostrarImagem();
                            startY = event.getY();
                            startX = event.getX();
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                        textoSuperiorSelecionado = false;
                        textoInferiorSelecionado = false;
                        return true;
                }
                return false;
            }
        });


        Bitmap imagemFundo = BitmapFactory.decodeResource(getResources(), R.drawable.fry_meme);
        memeCreator = new MemeCreator("Olá", "Dandara!", Color.WHITE, Color.WHITE,
                64, 64, imagemFundo, getResources().getDisplayMetrics());
        mostrarImagem();
    }

    private void vibrar() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    public void iniciarMudarTexto(View v) {
        Intent intent = new Intent(this, NovoTextoActivity.class);
        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_SUPERIOR_ATUAL, memeCreator.getTextoSuperior());
        intent.putExtra(NovoTextoActivity.EXTRA_TEXTO_INFERIOR_ATUAL, memeCreator.getTextoInferior());
        intent.putExtra(NovoTextoActivity.EXTRA_COR_ATUAL_SUPERIOR, converterCor(memeCreator.getCorTextoSuperior()));
        intent.putExtra(NovoTextoActivity.EXTRA_COR_ATUAL_INFERIOR, converterCor(memeCreator.getCorTextoInferior()));
        intent.putExtra(NovoTextoActivity.EXTRA_TAMANHO_ATUAL_SUPERIOR, String.valueOf(memeCreator.getTamanhoTextoSuperior()));
        intent.putExtra(NovoTextoActivity.EXTRA_TAMANHO_ATUAL_INFERIOR, String.valueOf(memeCreator.getTamanhoTextoInferior()));






        startNovoTexto.launch(intent);
    }


    private final ActivityResultLauncher<Intent> iniciarTemplates = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        String nomeTemplate = data.getStringExtra("templateSelecionado");
                        int idImagem = getResources().getIdentifier(nomeTemplate, "drawable", getPackageName());
                        Bitmap imagemFundo = BitmapFactory.decodeResource(getResources(), idImagem);
                        memeCreator.setFundo(imagemFundo);
                        mostrarImagem();
                    }
                }
            });


    public void abrirTemplates(View view) {
        Intent intent = new Intent(this, TemplateActivity.class);
        iniciarTemplates.launch(intent);
    }




    public String converterCor(int cor) {
        switch (cor) {
            case Color.BLACK: return "BLACK";
            case Color.WHITE: return "WHITE";
            case Color.BLUE: return "BLUE";
            case Color.GREEN: return "GREEN";
            case Color.RED: return "RED";
            case Color.YELLOW: return "YELLOW";
        }
        return null;
    }

    public void iniciarMudarFundo(View v) {
        startImagemFundo.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ImageOnly.INSTANCE)
                .build());
    }

    public void compartilhar(View v) {
        compartilharImagem(memeCreator.getImagem());
    }

    public void mostrarImagem() {
        imageView.setImageBitmap(memeCreator.getImagem());
    }

    public void compartilharImagem(Bitmap bitmap) {

        // pegar a uri da mediastore
        // pego o volume externo pq normalmente ele é maior que o volume interno.
        Uri contentUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            /*
            Em versões <= 28, é preciso solicitar a permissão WRITE_EXTERNAL_STORAGE.
            Mais detalhes em https://developer.android.com/training/data-storage/shared/media#java.
             */
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (PackageManager.PERMISSION_GRANTED != write) {
                startWriteStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return;
            }
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // montar a nova imagem a ser inserida na mediastore
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, "shareimage1file");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        Uri imageUri = getContentResolver().insert(contentUri, values);

        // criar a nova imagem na pasta da mediastore
        try (
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(imageUri, "w");
                FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor())
            ) {
            BufferedOutputStream bytes = new BufferedOutputStream(fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gravar imagem:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // compartilhar a imagem com intent implícito
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_TITLE, "Seu meme fabuloso");
        share.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(share, "Compartilhar Imagem"));
    }
}