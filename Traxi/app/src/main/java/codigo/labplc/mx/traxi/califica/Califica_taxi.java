package codigo.labplc.mx.traxi.califica;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import codigo.labplc.mx.traxi.R;
import codigo.labplc.mx.traxi.TraxiMainActivity;
import codigo.labplc.mx.traxi.bd.DBHelper;
import codigo.labplc.mx.traxi.dialogos.Dialogos;
import codigo.labplc.mx.traxi.fonts.fonts;
import codigo.labplc.mx.traxi.log.DatosLogBean;
import codigo.labplc.mx.traxi.services.ServicioGeolocalizacion;
import codigo.labplc.mx.traxi.tracking.map.Mapa_tracking;
import codigo.labplc.mx.traxi.utils.Utils;

/**
 * Actividad que muestra un dialogo el cual sirve para calificar el servicio
 * 
 * @author mikesaurio
 *
 */
@SuppressLint("SimpleDateFormat")
public class Califica_taxi extends Activity {

	

	public final String TAG = this.getClass().getSimpleName();
	
	Button calificar_aceptar;
	EditText comentario;
	RatingBar rank;
	private String Scalificacion = "0";
	private String Scomentario;
	private TextView dialogo_califica_tv_caracteres;

	private TextView califica_taxi_tv_titulo_calif;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_califica_taxi);

		DatosLogBean.setTagLog(TAG);
		
			try{
			 Mapa_tracking.fa.finish();
			}catch(Exception e){
				DatosLogBean.setDescripcion(Utils.getStackTrace(e));
			}
		
		//en caso de que se active si ya no existe el servicio
		if(ServicioGeolocalizacion.serviceIsIniciado!=true||ServicioGeolocalizacion.taxiActivity==null){
			Mapa_tracking.isButtonExit= false;
			ServicioGeolocalizacion.CancelNotification(Califica_taxi.this, 0);
			Intent svc = new Intent(Califica_taxi.this, ServicioGeolocalizacion.class);
			stopService(svc);
			ServicioGeolocalizacion.serviceIsIniciado=false;
	  		Intent mainIntent = new Intent().setClass(Califica_taxi.this, TraxiMainActivity.class);
	  		startActivity(mainIntent);
	  		finish();
		}
		
		setFinishOnTouchOutside(false);
		
		
		
	    ((TextView) findViewById(R.id.califica_taxi_tv_titulo)).setTypeface(new fonts(Califica_taxi.this).getTypeFace(fonts.FLAG_MAMEY));	
		((TextView) findViewById(R.id.califica_taxi_tv_titulo)).setTextColor(getResources().getColor(R.color.color_vivos));
		
		 dialogo_califica_tv_caracteres =(TextView)findViewById(R.id.dialogo_califica_tv_caracteres);
		dialogo_califica_tv_caracteres.setTypeface(new fonts(Califica_taxi.this).getTypeFace(fonts.FLAG_ROJO));
		dialogo_califica_tv_caracteres.setTextColor(getResources().getColor(R.color.color_vivos));
		
	     
		 califica_taxi_tv_titulo_calif= (TextView)findViewById(R.id.califica_taxi_tv_titulo_calif);
		califica_taxi_tv_titulo_calif.setTypeface(new fonts(Califica_taxi.this).getTypeFace(fonts.FLAG_ROJO));	
		califica_taxi_tv_titulo_calif.setTextColor(getResources().getColor(R.color.color_vivos));
					
		ImageView califica_taxi_iv_no_calif=(ImageView)findViewById(R.id.califica_taxi_iv_no_calif);
		califica_taxi_iv_no_calif.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Dialogos.Toast(Califica_taxi.this, getResources().getString(R.string.dialogo_califica_servicio_NO_enviar_comentario), Toast.LENGTH_LONG);
				cerrarDialog();
			}
		});
		
		
		comentario = (EditText)findViewById(R.id.dialogo_califica_servicio_et_comentario);
		comentario.setTypeface(new fonts(Califica_taxi.this).getTypeFace(fonts.FLAG_ROJO));
		comentario.setTextColor(getResources().getColor(R.color.color_vivos));
		
		comentario.addTextChangedListener(mTextEditorWatcher);
		
		rank = (RatingBar)findViewById(R.id.dialogo_califica_servicio_ratingBarServicio);
		rank.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			
			public void onRatingChanged(RatingBar ratingBar, float rating,boolean fromUser) {
				Scalificacion=(String.valueOf(rating));
				if(rating==0.0){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_0));
				}
				if(rating==0.5){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_05));
				}
				if(rating==1.0){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_10));
				}
				if(rating==1.5){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_15));
				}
				if(rating==2.0){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_20));
				}
				if(rating==2.5){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_25));
				}
				if(rating==3.0){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_30));
				}
				if(rating==3.5){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_35));
				}
				if(rating==4.0){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_40));
				}
				if(rating==4.5){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_45));
				}
				if(rating==5.0){
					califica_taxi_tv_titulo_calif.setText(getResources().getString(R.string.Califica_taxi_50));
				}
			}

			
		});
		
		calificar_aceptar =(Button)findViewById(R.id.dialogo_califica_servicio_btnAceptar);
		calificar_aceptar.setTypeface(new fonts(this).getTypeFace(fonts.FLAG_ROJO));
		calificar_aceptar.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				Dialogos.Toast(Califica_taxi.this, getResources().getString(R.string.dialogo_califica_servicio_enviar_comentario), Toast.LENGTH_LONG);
				SharedPreferences prefs = getSharedPreferences("MisPreferenciasTrackxi",Context.MODE_PRIVATE);
				String placa = prefs.getString("placa", null);
				String face = prefs.getString("facebook","0");
				String id_usuario = prefs.getString("uuid", null);
				Scomentario=comentario.getText().toString().replaceAll(" ", "+");
				if(!Scomentario.equals("")){     
					Calendar c = Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd+HH:mm:ss");
					 String finViaje = sdf.format(c.getTime());
					 
					 
					try {
						DBHelper	BD = new DBHelper(Califica_taxi.this);
						SQLiteDatabase bd = BD.loadDataBase(Califica_taxi.this, BD);
						BD.setViaje(bd, placa, ServicioGeolocalizacion.horaInicio, finViaje, Scalificacion, comentario.getText().toString(), 
								ServicioGeolocalizacion.latitud_inicial+","+ServicioGeolocalizacion.longitud_inicial, 
								ServicioGeolocalizacion.latitud+","+ServicioGeolocalizacion.longitud);
						BD.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

                    String url="https://traxi.herokuapp.com/cdmx/api/taxis/new?" +
                            "id_usuario="+face+
                            "&calificacion="+Scalificacion+
                            "&comentario="+Scomentario+
                            "&placa="+placa+
                            "&latitud_inicial="+ServicioGeolocalizacion.latitud_inicial+
                            "&longitud_inicial="+ServicioGeolocalizacion.longitud_inicial+
                            "&latitud="+ServicioGeolocalizacion.latitud+
                            "&longitud="+ServicioGeolocalizacion.longitud+
                            "&horaInicio="+ServicioGeolocalizacion.horaInicio+
                            "&horafin="+finViaje;
                    Log.d("**************",url+"");
                   /* String url= "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=pasajero&type=addcomentario"
							+"&id_usuario="+id_usuario
							+"&calificacion="+Scalificacion
							+"&comentario="+Scomentario
							+"&placa="+placa
							+"&id_face="+face
							+"&pointinilat="+ServicioGeolocalizacion.latitud_inicial
							+"&pointinilon="+ServicioGeolocalizacion.longitud_inicial
							+"&pointfinlat="+ServicioGeolocalizacion.latitud
							+"&pointfinlon="+ServicioGeolocalizacion.longitud
							+"&horainicio="+ServicioGeolocalizacion.horaInicio
							+"&horafin="+finViaje;*/

					Utils.doHttpConnection(url);
					
					
					
				}
				
				cerrarDialog();
			}
		});
		
		
	}

	
	/**
	 * termina la actividad y muestra un mensaje
	 * @param cadena (int) String al cerrar la actividad
	 */
	public void cerrarDialog( ){
	
	Mapa_tracking.isButtonExit= false;
	
	Intent svc = new Intent(Califica_taxi.this, ServicioGeolocalizacion.class);
	stopService(svc);
	

	try{
		 Mapa_tracking.fa.finish();
		}catch(Exception e){
			DatosLogBean.setDescripcion(Utils.getStackTrace(e));
		}

	
	Califica_taxi.this.finish();
}
	


	@Override
	public void onBackPressed() {
	}
	
	@Override
	protected void onStart() {
		ServicioGeolocalizacion.stopNotification();
		super.onStart();
	}
	
	
	/**
	 * escucha para saber cuantos caracteres quedan al dejar un comentario
	 */
	private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        	dialogo_califica_tv_caracteres.setText(String.valueOf(s.length()+"/50"));
        }

        public void afterTextChanged(Editable s) {
        }
};
	
}
