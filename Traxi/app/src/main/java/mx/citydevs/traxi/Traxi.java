package mx.citydevs.traxi;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import mx.citydevs.traxi.log.HockeySender;
import mx.citydevs.traxi.utils.Utils;

/**
 * clase que extiende de Aplicacion y contola los Log 
 * @author mikesaurio
 *
 */
@ReportsCrashes(formKey = "traxi", formUri ="http://codigo.labplc.mx/~mikesaurio/taxi.php?act=pasajero&type=addlog" )
public class Traxi extends Application{
	@Override
	public void onCreate() {
		
		if(Utils.getPreferencia("prefSendReport",Traxi.this.getBaseContext(),true)){
		 	String  envio= "http://codigo.labplc.mx/~mikesaurio/taxi.php?act=pasajero&type=addlog";
	    	ACRA.init(this);
	    	HockeySender MySender = new HockeySender(Traxi.this,envio);
	        ACRA.getErrorReporter().setReportSender(MySender);
		}
		super.onCreate();
	}

	
	
	
	
}