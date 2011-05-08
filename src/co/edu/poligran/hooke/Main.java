package co.edu.poligran.hooke;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class Main extends Activity implements View.OnClickListener {
    private EditText massTxt;
    private EditText constantTxt;
    private EditText fpsTxt;
    private static final int RANDOM = 1;
    private static final int ABOUT = 2;
    private DecimalFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        massTxt = (EditText) findViewById(R.id.mass);
        constantTxt = (EditText) findViewById(R.id.constant);
        fpsTxt = (EditText) findViewById(R.id.fps);
        findViewById(R.id.run).setOnClickListener(this);
        formatter = new DecimalFormat("#.##");
        formatter.setMaximumFractionDigits(2);
    }

    public void onClick(View view) {
        try {
            float mass = Float.parseFloat(massTxt.getText().toString());
            float constant = Float.parseFloat(constantTxt.getText().toString());
            float fps = Float.parseFloat(fpsTxt.getText().toString());
            if (fps > 30 || fps < 5) {
                Toast.makeText(this, "FPS debe estar entre 5 y 30", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, Hooke.class);
            intent.putExtra(Hooke.MASS, mass);
            intent.putExtra(Hooke.CONSTANT, constant);
            intent.putExtra(Hooke.FPS, fps);
            System.out.println("constant "+constant);
            startActivity(intent);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Digitó algo mal o no digitó nada, sonso", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, RANDOM, Menu.FIRST, "Aleatorio").setIcon(R.drawable.random);
        menu.add(0, ABOUT, Menu.FIRST + 1, "Copyright").setIcon(R.drawable.about);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case RANDOM:
                massTxt.setText(formatter.format(Math.random()*100));
                constantTxt.setText(formatter.format(Math.random() * 20));
                fpsTxt.setText("30");
                break;
            case ABOUT:
                showDialog(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        TextView copyright = new TextView(this);
        copyright.setText("DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\n" +
                "Version 2, December 2004\n" +
                "Copyright (C) 2011 Cristian Castiblanco <cristian@elhacker.net>\n" +
                "\n" +
                " Everyone is permitted to copy and distribute verbatim or modified\n" +
                " copies of this license document, and changing it is allowed as long\n" +
                " as the name is changed.\n" +
                "\n" +
                "DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE\n" +
                "TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION:\n" +
                "\n" +
                "  0. You just DO WHAT THE FUCK YOU WANT TO." +
                "\n\nTo download the code go to:\nhttp://github.com/casidiablo/hulk");
        copyright.setTextSize(16);
        Linkify.addLinks(copyright, Linkify.WEB_URLS);
        return new AlertDialog.Builder(this).setTitle("Copyright")
                .setView(copyright).create();
    }
}
