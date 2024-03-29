package codigo.labplc.mx.traxi.buscarplaca.tips;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import codigo.labplc.mx.traxi.R;
import codigo.labplc.mx.traxi.fonts.fonts;

/**
 * Actividd que muestra tips de uso a los usuarios 
 * 
 * @author mikesaurio
 *
 */
public class Tips_activity extends Activity {
	 public final String TAG = this.getClass().getSimpleName();
	
	private  int[] stips= {R.string.tip_1,R.string.tip_2,R.string.tip_3,R.string.tip_4,R.string.tip_5,
			R.string.tip_6,R.string.tip_7,R.string.tip_8,R.string.tip_9,R.string.tip_10};

	private int num_String = 0;
	private TextView tv_contenido;
	private TextView tips_activity_tv_cuantos;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_tips_activity);
		

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels/2;
		LinearLayout tips_activity_ll_tips =(LinearLayout)findViewById(R.id.tips_activity_ll_tips);
		RelativeLayout.LayoutParams lp= new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,height);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lp.addRule(RelativeLayout.CENTER_VERTICAL);
		tips_activity_ll_tips.setLayoutParams(lp);

		
	  /*  ((TextView) findViewById(R.id.tips_activity_tv_titulo)).setTypeface(new fonts(Tips_activity.this).getTypeFace(fonts.FLAG_MAMEY));	
		((TextView) findViewById(R.id.tips_activity_tv_titulo)).setTextColor(getResources().getColor(R.color.color_vivos));
		*/
		tips_activity_tv_cuantos =(TextView)findViewById(R.id.tips_activity_tv_cuantos);
		
		tv_contenido = (TextView) findViewById(R.id.tips_activity_tv_contenido);
		tv_contenido.setTypeface(new fonts(Tips_activity.this).getTypeFace(fonts.FLAG_ROJO));	
		tv_contenido.setTextColor(getResources().getColor(R.color.color_vivos));
		tv_contenido.setText(getResources().getString(stips[randInt(0,stips.length-1)]));
		
		ImageView	tips_activity_iv_cerrar	=(ImageView)findViewById(R.id.tips_activity_iv_cerrar);
		tips_activity_iv_cerrar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		ImageView	tips_activity_iv_back	=(ImageView)findViewById(R.id.tips_activity_iv_back);
		tips_activity_iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_contenido.setText(getResources().getString(stips[recorreArregloAtras()]));
				
			}
		});
		ImageView	tips_activity_iv_next	=(ImageView)findViewById(R.id.tips_activity_iv_next);
		tips_activity_iv_next.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_contenido.setText(getResources().getString(stips[recorreArregloAdelante()]));
			}
		});
	
		
	}

	/**
	 * metodo que regresa un numero random entre 2
	 * @param min
	 * @param max
	 * @return
	 */
	public  int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
		Log.d(TAG, randomNum+"");
	    num_String= randomNum;
		tips_activity_tv_cuantos.setText((num_String+1)+"/"+(stips.length));
	    return randomNum;
	}
	
	/**
	 * metodo que recorre los tips a partir de uno 
	 * @return regresa el entero siguiente del arreglo de tips
	 */
	public int recorreArregloAdelante(){
		if(num_String>=9){
			num_String=0;
			tips_activity_tv_cuantos.setText((num_String+1)+"/"+(stips.length));
			return 0;
		}else{	
			num_String+=1;
			tips_activity_tv_cuantos.setText((num_String+1)+"/"+(stips.length));
		return num_String;
		}
		
	}
	/**
	 * metodo que recorre el areglo para atras
	 * @return regresa el numero anterior del arreglo tips
	 */
	public int recorreArregloAtras(){
		if(num_String<=0){
			num_String=9;
			tips_activity_tv_cuantos.setText((num_String+1)+"/"+(stips.length));
			return 9;
		}else{
			num_String-=1;
			tips_activity_tv_cuantos.setText((num_String+1)+"/"+(stips.length));
			return num_String;
		}
		
	}
	
}
