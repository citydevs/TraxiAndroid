package codigo.labplc.mx.traxi.buscarplaca.paginador;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import codigo.labplc.mx.traxi.R;
import codigo.labplc.mx.traxi.buscarplaca.BuscaPlacaTexto;
import codigo.labplc.mx.traxi.buscarplaca.bean.AutoBean;
import codigo.labplc.mx.traxi.buscarplaca.bean.ComentarioBean;
import codigo.labplc.mx.traxi.configuracion.UserSettingActivity;
import codigo.labplc.mx.traxi.dialogos.Dialogos;
import codigo.labplc.mx.traxi.facebook.FacebookLogin;
import codigo.labplc.mx.traxi.fonts.fonts;
import codigo.labplc.mx.traxi.historico.Historico_viajes;
import codigo.labplc.mx.traxi.log.DatosLogBean;
import codigo.labplc.mx.traxi.services.ServicioGeolocalizacion;
import codigo.labplc.mx.traxi.tracking.map.Mapa_tracking;
import codigo.labplc.mx.traxi.utils.Utils;

/**
 * FragmentActivity que funge como paginador
 *
 * @author mikesaurio
 */
@SuppressWarnings("deprecation")
@SuppressLint("SimpleDateFormat")
public class DatosAuto extends FragmentActivity implements OnClickListener {

    public final String TAG = this.getClass().getSimpleName();

    private int PUNTOS_USUARIO = 0;
    private AutoBean autoBean;
    private String placa;
    private int imagen_verde = 1;
    private int imagen_rojo = 2;
    private boolean hasRevista = true;
    private float sumaCalificacion = 0.0f;
    private static final int RESULT_SETTINGS = 1;
    private LocationManager mLocationManager;
    private CirclePageIndicator titleIndicator;
    private ViewPager pager = null;
    private FacebookLogin facebookLogin;
    public static TextView abs_layout_tv_titulo_datosAutos;
    private boolean hasVerificacion = false;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.setContentView(R.layout.dialogo_datos_correctos);

        DatosLogBean.setTagLog(TAG);

        final ActionBar ab = getActionBar();
        ab.setDisplayShowHomeEnabled(false);
        ab.setDisplayShowTitleEnabled(false);
        final LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
        View view = inflater.inflate(R.layout.abs_layout_back, null);
        abs_layout_tv_titulo_datosAutos = (TextView) view.findViewById(R.id.abs_layout_tv_titulo);
        abs_layout_tv_titulo_datosAutos.setTypeface(new fonts(DatosAuto.this).getTypeFace(fonts.FLAG_MAMEY));
        abs_layout_tv_titulo_datosAutos.setText(getResources().getString(R.string.datos_del_taxi));
        ab.setDisplayShowCustomEnabled(true);
        ImageView abs_layout_iv_menu = (ImageView) view.findViewById(R.id.abs_layout_iv_menu);
        abs_layout_iv_menu.setOnClickListener(this);
        ImageView abs_layout_iv_logo = (ImageView) view.findViewById(R.id.abs_layout_iv_logo);
        abs_layout_iv_logo.setOnClickListener(this);
        ab.setCustomView(view, new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ab.setCustomView(view);


        facebookLogin = new FacebookLogin(DatosAuto.this);


        Bundle bundle = getIntent().getExtras();
        placa = bundle.getString("placa");


        SharedPreferences prefs = getSharedPreferences("MisPreferenciasTrackxi", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("placa", placa);
        editor.commit();

        Upload nuevaTarea = new Upload();
        nuevaTarea.execute();


        Button dialogo_datos_correctos_btn_noViajo = (Button) findViewById(R.id.dialogo_datos_correctos_btn_noViajo);
        dialogo_datos_correctos_btn_noViajo.setTypeface(new fonts(this).getTypeFace(fonts.FLAG_ROJO));
        dialogo_datos_correctos_btn_noViajo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("MisPreferenciasTrackxi", Context.MODE_PRIVATE);
                String face = prefs.getString("facebook", "0");
                String UUID_local = prefs.getString("uuid", null);
                try {
                    String url = "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=pasajero&type=addnoviaje"
                            + "&id_usuario=" + UUID_local
                            + "&id_face=" + face
                            + "&calificacion_final=" + autoBean.getCalificacion_final()
                            + "&placa=" + placa
                            + "&revista=" + autoBean.isHasrevista_()
                            + "&infraccion=" + autoBean.isHasinfracciones_()
                            + "&anio=" + autoBean.isHasanio_()
                            + "&verificacion=" + autoBean.isHasverificacion_()
                            + "&tenencia=" + autoBean.isHastenencia_();

                    Utils.doHttpConnection(url);
                } catch (Exception e) {
                    DatosLogBean.setDescripcion(Utils.getStackTrace(e));
                }
                back();
            }
        });

        Button dialogo_datos_correctos_btn_iniciar = (Button) findViewById(R.id.dialogo_datos_correctos_btn_iniciar);
        dialogo_datos_correctos_btn_iniciar.setTypeface(new fonts(this).getTypeFace(fonts.FLAG_ROJO));
        dialogo_datos_correctos_btn_iniciar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Utils.hasInternet(DatosAuto.this)) {
                    if (Utils.getPreferencia("prefBusquedaFina", DatosAuto.this.getBaseContext(), true)) {
                        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            new Dialogos().showDialogGPS(DatosAuto.this).show();
                        } else {
                            ServicioGeolocalizacion.taxiActivity = DatosAuto.this;
                            startService(new Intent(DatosAuto.this, ServicioGeolocalizacion.class));

                            Intent intent_mapa = new Intent(DatosAuto.this, Mapa_tracking.class);
                            intent_mapa.putExtra("latitud_inicial", 19.0);
                            intent_mapa.putExtra("longitud_inicial", -99.0);
                            startActivity(intent_mapa);

                            DatosAuto.this.finish();
                        }
                    } else {
                        ServicioGeolocalizacion.taxiActivity = DatosAuto.this;
                        startService(new Intent(DatosAuto.this, ServicioGeolocalizacion.class));

                        Intent intent_mapa = new Intent(DatosAuto.this, Mapa_tracking.class);
                        intent_mapa.putExtra("latitud_inicial", 19.0);
                        intent_mapa.putExtra("longitud_inicial", -99.0);
                        startActivity(intent_mapa);

                        DatosAuto.this.finish();
                    }
                } else {
                    Dialogos.Toast(DatosAuto.this, getResources().getString(R.string.no_internet_connection), Toast.LENGTH_LONG);
                }

            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_SETTINGS:
                showUserSettings();
                break;
            default:
                facebookLogin.getFacebook().authorizeCallback(requestCode, resultCode, data);
                break;

        }

    }


    /**
     * carga todos los comentarios de una placa
     */
    private void cargaComentarios(String calificaciones) {
        try {
            calificaciones =  calificaciones.replace("[","");
            calificaciones =  calificaciones.replace("]","");

            String[] coment_separated = calificaciones.split(",");
            ArrayList<ComentarioBean> arrayComenario = new ArrayList<ComentarioBean>();
            for (int i = 0; i < coment_separated.length; i++) {
                    String result_comment[] = coment_separated[i].split(": ");
                    ComentarioBean comentarioBean = new ComentarioBean();
                    comentarioBean.setComentario(result_comment[1].replace("\"", ""));
                    comentarioBean.setId_facebook(result_comment[0].replace("\"", ""));
                    arrayComenario.add(comentarioBean);
            }
            autoBean.setArrayComentarioBean(arrayComenario);
        } catch (Exception e) {
            DatosLogBean.setDescripcion(Utils.getStackTrace(e));
        }
    }

    /**
     * metodo que verifica los datos de adeudos de un taxi
     */

    private void datosVehiculo(String vehiculo, String tiene_tenencia, String tiene_infracciones,String tiene_verificacion,String modelo_optimo, String calificacion_usuario) {
        try {

            autoBean.setMarca(vehiculo);
            if (Boolean.parseBoolean(tiene_tenencia)) {
                autoBean.setDescripcion_tenencia(getResources().getString(R.string.sin_adeudo_tenencia));
                autoBean.setImagen_teencia(imagen_verde);
            } else {
                autoBean.setDescripcion_tenencia(getResources().getString(R.string.con_adeudo_tenencia));
                autoBean.setImagen_teencia(imagen_rojo);
            }

            if (Boolean.parseBoolean(tiene_infracciones)) {
                autoBean.setDescripcion_infracciones(getResources().getString(R.string.tiene_infraccion));
                autoBean.setImagen_infraccones(imagen_rojo);
            } else {
                autoBean.setDescripcion_infracciones(getResources().getString(R.string.no_tiene_infraccion));
                autoBean.setImagen_infraccones(imagen_verde);
            }

            if (!Boolean.parseBoolean(tiene_verificacion)) {
                autoBean.setDescripcion_verificacion(getResources().getString(R.string.no_tiene_verificaciones));
                autoBean.setImagen_verificacion(imagen_rojo);
            }else{
                autoBean.setDescripcion_verificacion(getResources().getString(R.string.tiene_infraccion));
                autoBean.setImagen_verificacion(imagen_verde);
            }
            if(!Boolean.parseBoolean(modelo_optimo)){
                autoBean.setDescripcion_vehiculo(getResources().getString(R.string.carro_viejo));
                autoBean.setImagen_vehiculo(imagen_rojo);
            }else{
                autoBean.setDescripcion_vehiculo(getResources().getString(R.string.carro_nuevo));
                autoBean.setImagen_vehiculo(imagen_verde);
            }
            autoBean.setCalificacion_usuarios(Float.parseFloat(calificacion_usuario));
        } catch (Exception e) {
            DatosLogBean.setDescripcion(Utils.getStackTrace(e));
        }

    }




    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent().setClass(DatosAuto.this, BuscaPlacaTexto.class);
        startActivity(mainIntent);
        pager = null;
        DatosAuto.this.finish();
        super.onBackPressed();

    }

    /**
     * al dar atras en la actividad
     */
    public void back() {
        Intent mainIntent = new Intent().setClass(DatosAuto.this, BuscaPlacaTexto.class);
        startActivity(mainIntent);
        pager = null;
        DatosAuto.this.finish();
        Dialogos.Toast(DatosAuto.this, getResources().getString(R.string.mapa_inicio_de_viaje_no_tomado), Toast.LENGTH_LONG);
        super.onBackPressed();
    }

    /**
     * clase que envia por post los datos del registro
     *
     * @author mikesaurio
     */
    class Upload extends AsyncTask<String, Void, Void> {

        private ProgressDialog pDialog;
        ;
        public static final int HTTP_TIMEOUT = 40 * 1000;

        @Override
        protected Void doInBackground(String... params) {
            try

            {

                mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                titleIndicator = (CirclePageIndicator) findViewById(R.id.indicator_dialg);

                DatosAuto.this.pager = (ViewPager) DatosAuto.this.findViewById(R.id.pager_dialog);
                DatosAuto.this.pager.setOffscreenPageLimit(4);
                autoBean = new AutoBean();
                String getInfoTraxi = Utils.doHttpConnection("https://traxi.herokuapp.com/cdmx/api/taxis/" + placa);
                JSONObject jObj = new JSONObject(getInfoTraxi);
                String vehiculo = jObj.getString("vehiculo");
                String tiene_tenencia = jObj.getString("tiene_tenencia");
                String tiene_infracciones = jObj.getString("tiene_infracciones");
                String tiene_verificacion = jObj.getString("tiene_verificacion");
                String modelo_optimo = jObj.getString("modelo_optimo");
                String tiene_revista = jObj.getString("tiene_revista");
                String calificacion_escudo = jObj.getString("calificacion_escudo");
                String array_comentario = jObj.getString("array_comentario");
                String calificacion_usuario = jObj.getString("calificacion_usuarios");
                autoBean.setPlaca(placa);

                if (!Boolean.parseBoolean(tiene_revista)) {
                    autoBean.setDescripcion_revista(getResources().getString(R.string.sin_revista));
                    autoBean.setImagen_revista(imagen_rojo);
                    hasRevista = false;
                }else{
                    autoBean.setDescripcion_revista(getResources().getString(R.string.con_revista));
                    autoBean.setImagen_revista(imagen_verde);
                    hasRevista = true;
                }

                datosVehiculo(vehiculo, tiene_tenencia,tiene_infracciones,tiene_verificacion, modelo_optimo, calificacion_usuario);
                cargaComentarios(array_comentario);

                int PUNTOS = (Integer.parseInt(calificacion_escudo) + PUNTOS_USUARIO);
                if (PUNTOS <= 25) {
                    autoBean.setDescripcion_calificacion_app(getResources().getString(R.string.texto_calificacion_25));
                } else if (PUNTOS <= 49 && PUNTOS > 25) {
                    autoBean.setDescripcion_calificacion_app(getResources().getString(R.string.texto_calificacion_49));
                } else if (PUNTOS <= 60 && PUNTOS > 49) {
                    autoBean.setDescripcion_calificacion_app(getResources().getString(R.string.texto_calificacion_60));
                } else if (PUNTOS <= 80 && PUNTOS > 60) {
                    autoBean.setDescripcion_calificacion_app(getResources().getString(R.string.texto_calificacion_80));
                } else if (PUNTOS <= 90 && PUNTOS > 80) {
                    autoBean.setDescripcion_calificacion_app(getResources().getString(R.string.texto_calificacion_90));
                } else if (PUNTOS > 90) {
                    autoBean.setDescripcion_calificacion_app(getResources().getString(R.string.texto_calificacion_100));
                }
                autoBean.setCalificacion_final(PUNTOS);
                autoBean.setCalificaion_app(Integer.parseInt(calificacion_escudo));

            } catch (Exception e) {
                DatosLogBean.setDescripcion(Utils.getStackTrace(e));
            }
            return null;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DatosAuto.this);
            pDialog.setMessage(getResources().getString(R.string.cargando_info));
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            // Create an adapter with the fragments we show on the ViewPager
            FragmentPagerAdapterDialog adapter = new FragmentPagerAdapterDialog(getSupportFragmentManager());
            adapter.addFragment(ScreenSlidePageFragmentDialog.newInstance(getResources().getColor(R.color.android_blue), 1, DatosAuto.this, autoBean));
            adapter.addFragment(ScreenSlidePageFragmentDialog.newInstance(getResources().getColor(R.color.android_red), 2, DatosAuto.this, autoBean));
            adapter.addFragment(ScreenSlidePageFragmentDialog.newInstance(getResources().getColor(R.color.android_darkpink), 3, DatosAuto.this, autoBean, facebookLogin));


            DatosAuto.this.pager.setAdapter(adapter);

            titleIndicator.setViewPager(pager);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.abs_layout_iv_menu) {
            showPopup(v);
        } else if (v.getId() == R.id.abs_layout_iv_logo) {
            Intent mainIntent = new Intent().setClass(DatosAuto.this, BuscaPlacaTexto.class);
            startActivity(mainIntent);
            pager = null;
            DatosAuto.this.finish();
            super.onBackPressed();
        }


    }

    @Override
    protected void onDestroy() {
        pager = null;
        super.onDestroy();
    }


    /**
     * muestra popup en forma de menu
     *
     * @param v
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(DatosAuto.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup, popup.getMenu());
        int positionOfMenuItem = 0;
        MenuItem item = popup.getMenu().getItem(positionOfMenuItem);
        SpannableString s = new SpannableString(getResources().getString(R.string.action_historial));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_vivos)), 0, s.length(), 0);
        item.setTitle(s);
        positionOfMenuItem = 1;
        item = popup.getMenu().getItem(positionOfMenuItem);
        s = new SpannableString(getResources().getString(R.string.action_settings));
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_vivos)), 0, s.length(), 0);
        item.setTitle(s);


        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.configuracion_historial:
                        startActivity(new Intent(DatosAuto.this, Historico_viajes.class));
                        return true;


                    case R.id.configuracion_pref:

                        Intent i = new Intent(DatosAuto.this, UserSettingActivity.class);
                        startActivityForResult(i, RESULT_SETTINGS);
                        return true;

                    case R.id.configuracion_acerca_de:
                        new Dialogos().mostrarAercaDe(DatosAuto.this).show();
                        return true;

                }
                return false;
            }
        });

        popup.show();
    }


    /**
     * mustra las preferencias guardadas
     */
    private void showUserSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        StringBuilder builder = new StringBuilder();
        builder.append("\n Send report:" + sharedPrefs.getBoolean("prefSendReport", true));
    }


    /**
     * ponderacion para las calificaciones de los usuarios
     *
     * @param parcial
     * @return
     */
    public int usuarioCalifica(float parcial) {
        if (parcial < 0.5)
            return -15;
        if (parcial < 1.0 && parcial >= 0.5)
            return -13;
        if (parcial < 1.5 && parcial >= 1.0)
            return -10;
        if (parcial < 2.0 && parcial >= 1.5)
            return -8;
        if (parcial < 2.5 && parcial >= 2.0)
            return -5;
        if (parcial < 3.0 && parcial >= 2.5)
            return -3;
        if (parcial < 3.5 && parcial >= 3.0)
            return 1;
        if (parcial < 4.0 && parcial >= 3.5)
            return 2;
        if (parcial < 4.5 && parcial >= 4.0)
            return 3;
        if (parcial < 5.0 && parcial >= 4.5)
            return 4;
        if (parcial >= 5)
            return 5;

        return 0;
    }


}
