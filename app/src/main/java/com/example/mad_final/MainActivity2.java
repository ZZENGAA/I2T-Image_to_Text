package com.example.mad_final;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    ImageView clear,getImage,copy,search_icons;
    EditText recogText;
    Uri imageUri;
    TextRecognizer textRecognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        clear=findViewById(R.id.imageView5);
        copy=findViewById(R.id.imageView2);
        getImage=findViewById(R.id.imageView4);
        search_icons=findViewById(R.id.imageView6);
        recogText=findViewById(R.id.editTextTextPersonName);
        textRecognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);


        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity2.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text= recogText.getText().toString();
                if(text.isEmpty())
                {
                    Toast.makeText(MainActivity2.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
                }
                else {
                    ClipboardManager clipboardManager=(ClipboardManager) getSystemService(MainActivity2.this.CLIPBOARD_SERVICE);
                    ClipData clipData=ClipData.newPlainText("Data",recogText.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);

                    Toast.makeText(MainActivity2.this, "Text copied to Clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=recogText.getText().toString();
                if(text.isEmpty())
                {
                    Toast.makeText(MainActivity2.this, "there is no text to clear", Toast.LENGTH_SHORT).show();
                }
                else {
                    recogText.setText("");
                }
            }
        });
    }
    public void openChrome(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=text&tbm=isch&source=hp&ei=WzaTZKmLM9SX-AbN_bOoAg&oq=&gs_lcp=ChJtb2JpbGUtZ3dzLXdpei1pbWcQARgAMgIIKVAAWABg9AloAHAAeACAAQCIAQCSAQCYAQCwAQE&sclient=mobile-gws-wiz-img"));
        intent.setPackage("com.android.chrome"); // Specify the package name of Chrome app
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode== ImagePicker.REQUEST_CODE)
        {
            if(resultCode== Activity.RESULT_OK)
            {
                imageUri=data.getData();
                Toast.makeText(this, "image Selected", Toast.LENGTH_SHORT).show();
                try {
                    recognizeText();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void recognizeText() throws IOException {
        if(imageUri!=null)
        {
            try {
                InputImage inputImage = InputImage.fromFilePath(MainActivity2.this, imageUri);
                Task<Text> result = textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String recognizerText = text.getText();
                        recogText.setText(recognizerText);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}