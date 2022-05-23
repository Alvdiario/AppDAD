package verticles;

import io.netty.handler.codec.http.HttpContentEncoder.Result;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import types.Dispositivo;
import types.Gps;
import types.SensorValue;
import types.Usuario;

public class DatabaseVerticle extends AbstractVerticle {
	private MySQLPool mySQLPool;

	@Override
	public void start(Promise<Void> startPromise) {
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dadproyecto").setUser("root").setPassword("root");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(50);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);

		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.route("/api/usuario*").handler(BodyHandler.create());
		router.route("/api/dispositivo*").handler(BodyHandler.create());
		router.route("/api/sensor/values/gps*").handler(BodyHandler.create());
		router.route("/api/mpu6050").handler(BodyHandler.create());
		router.route("/api*").handler(BodyHandler.create());
		



		vertx.createHttpServer().requestHandler(router::handle).listen(8081, result -> {
			if (result.succeeded()) {
				startPromise.complete();
			} else {
				startPromise.fail(result.cause());
			}

		});
		
		//--------------------------------------------------------------------------------------------------------------------------------------------SENSORES
		router.get("/api/mpu6050/:idsensor_valor_mpu6050").handler(this::getValueBySensor);
		router.put("/api/mpu6050").handler(this::putValueBySensor);
		router.post("/api/mpu6050").handler(this::postValueBySensor);
		//------------------------------------------------------------------------------------------------------------------------------------------------USUARIOS
		router.get("/api/usuario/:idusuario").handler(this::getUsuario);
		router.put("/api/usuario").handler(this::putUsuario);
		router.post("/api/usuario").handler(this::postUsuarioActualiza);  
		router.delete("/api/usuario/:idusuario").handler(this::deleteUsuario);
		//-----------------------------------------------------------------------------------------------------------DISPOSITIVO
		router.get("/api/dispositivo/:iddispositivo").handler(this::getDispositivo);
		router.put("/api/dispositivo").handler(this::putDispositivo);
		router.post("/api/dispositivo/actualiza").handler(this::postDispositivaActualiza);
		router.delete("/api/dispositivo/delete/:iddispositivo").handler(this::deleteDispositivo);
		//-----------------------------------------------------------------------------------------------------------GPS
		router.get("/api/sensor/values/gps/:idsensor_valor_gps").handler(this::getGps);
		router.put("/api/sensor/values/gps").handler(this::putGps);
		router.post("/api/sensor/values/gps").handler(this::postGps);


	}


	private void getValueBySensor(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dadproyecto.sensor_valor_mpu6050 WHERE idsensor_valor_mpu6050 = "
				+ routingContext.request().getParam("idsensor_valor_mpu6050"), res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es " + resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new SensorValue(
									row.getInteger("idsensor_valor_mpu6050"),
									row.getInteger("idsensor"), row.getInteger("a_value_x"),
									row.getInteger("a_value_y"), row.getInteger("a_value_z"),
									row.getInteger("g_value_x"), row.getInteger("g_value_y"),
									row.getInteger("g_value_z"), row.getLong("timesstamp"))));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(result.encodePrettily());
					} else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}

				});
	}
	private void putValueBySensor(RoutingContext routingContext) {

		SensorValue SensorValue = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		mySQLPool.preparedQuery("INSERT INTO dadproyecto.sensor_valor_mpu6050 (idsensor_valor_mpu6050,idsensor,a_value_x,a_value_y,a_value_z,"
				+ "g_value_x,g_value_y,g_value_z,timesstamp) VALUES (?,?,?,?,?,?,?,?,?)",
				Tuple.of(SensorValue.getIdsensor_valor_mpu6050(),SensorValue.getIdsensor(),
						SensorValue.getA_value_x(),SensorValue.getA_value_y(),SensorValue.getA_value_z(),
						SensorValue.getG_value_x(),SensorValue.getG_value_y(),SensorValue.getG_value_z(),
						SensorValue.getTimestamp()),
				handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result().rowCount());

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						SensorValue.setIdsensor_valor_mpu6050( (int)id);

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(SensorValue).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	private void postValueBySensor(RoutingContext routingContext) {
		SensorValue SensorValue = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		mySQLPool.query("UPDATE dadproyecto.sensor_valor_mpu6050 SET idsensor_valor_mpu6050="+SensorValue.getIdsensor_valor_mpu6050()+
				",idsensor="+SensorValue.getIdsensor()+
				",a_value_x="+SensorValue.getA_value_x()+",a_value_y="+SensorValue.getA_value_y()+",a_value_z="+SensorValue.getA_value_z()+
				",g_value_x="+SensorValue.getG_value_x()+",g_value_y="+SensorValue.getG_value_y()+",g_value_z="+SensorValue.getG_value_z()+
				",timesstamp="+SensorValue.getTimestamp()+" WHERE idsensor_valor_mpu6050="+SensorValue.getIdsensor_valor_mpu6050(),
				handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result().rowCount());

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						SensorValue.setIdsensor_valor_mpu6050((int) id);

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(SensorValue).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});

	}
	//--------------------------------------------------------------------GPS

	private void getGps(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dadproyecto.sensor_valor_gps WHERE idsensor_valor_gps = "+ routingContext.request().getParam("idsensor_valor_gps"),
				res->{
					if(res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Gps(
									row.getInteger("idsensor_valor_gps"),
									row.getInteger("idsensor"),
									row.getString("value")
									)));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}

				});
	}
	
	private void putGps(RoutingContext routingContext) {

		Gps Gps = Json.decodeValue(routingContext.getBodyAsString(), Gps.class);
		mySQLPool.preparedQuery("INSERT INTO dadproyecto.sensor_valor_gps (idsensor_valor_gps,idsensor,value) VALUES (?,?,?)",
				Tuple.of(Gps.getIdsensor_valor_mpu6050(),Gps.getIdsensor(),
						Gps.getLocalizacion()),
				handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result().rowCount());

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						Gps.setIdsensor_valor_mpu6050( (int)id);

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(Gps).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	private void postGps(RoutingContext routingContext) {
		Gps Gps = Json.decodeValue(routingContext.getBodyAsString(), Gps.class);
		mySQLPool.query("UPDATE dadproyecto.sensor_valor_gps SET idsensor_valor_gps="+Gps.getIdsensor_valor_mpu6050()+
				",idsensor="+Gps.getIdsensor()+
				",value= '"+Gps.getLocalizacion()+"'"+" WHERE idsensor_valor_gps="+Gps.getIdsensor_valor_mpu6050(),
				handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result().rowCount());

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						Gps.setIdsensor_valor_mpu6050((int) id);

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(Gps).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});

	}
	//--------------------------------------------------------------------DISPOSITIVO
	private void getDispositivo(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dadproyecto.dispositivo WHERE iddispositivo = "+ routingContext.request().getParam("iddispositivo"),
				res->{
					if(res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es "+ resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new Dispositivo(
									row.getInteger("iddispositivo"),
									row.getString("ip"),
									row.getString("nombre"),
									row.getInteger("idusuario"),
									row.getLong("initialtimestamp")
									)));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json").end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}

				});
	}
	private void putDispositivo(RoutingContext routingContext) {

		Dispositivo dispositivo = Json.decodeValue(routingContext.getBodyAsString(), Dispositivo.class);
		mySQLPool.preparedQuery("INSERT INTO dadproyecto.dispositivo (iddispositivo,ip,nombre,idusuario,initialtimestamp) VALUES (?,?,?,?,?)",
				Tuple.of(dispositivo.getIddispositivo(), dispositivo.getIp(), dispositivo.getNombre(),dispositivo.getIdusuario(),
						dispositivo.getInitialtimestamp()),
				handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result().rowCount());

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						dispositivo.setIdusuario( (int)id);

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(dispositivo).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	private void postDispositivaActualiza(RoutingContext routingContext) {
		Dispositivo Dispositivo = Json.decodeValue(routingContext.getBodyAsString(), Dispositivo.class);
		mySQLPool.query("UPDATE dadproyecto.dispositivo SET ip='"+Dispositivo.getIp()+"'"+
				",nombre='"+Dispositivo.getNombre()+"'"+
				",idusuario="+Dispositivo.getIdusuario()+
				",initialtimestamp="+Dispositivo.getInitialtimestamp()+" WHERE iddispositivo="+Dispositivo.getIddispositivo(),
				handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result().rowCount());

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						Dispositivo.setIddispositivo((int) id);

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(Dispositivo).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});

	}
	private void deleteDispositivo(RoutingContext routingContext) {
		mySQLPool.query(
				"DELETE  FROM dadproyecto.dispositivo WHERE iddispositivo = "+ routingContext.request().getParam("iddispositivo"),
				res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El numero de elementos eliminados es " + res.result().rowCount());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(
									JsonObject.mapFrom(new Dispositivo( row.getInteger("iddispositivo"),
											row.getString("ip"),
											row.getString("nombre"),
											row.getInteger("idusuario"),
											row.getLong("initialtimestamp")
											)));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(result.encodePrettily());
					} else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}

				});
	}

	//---------------------------------------------------------------------USUARIOS

	private void getUsuario(RoutingContext routingContext) {
		mySQLPool.query(
				"SELECT * FROM dadproyecto.usuario WHERE idusuario = " + routingContext.request().getParam("idusuario"),
				res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El numero de elementos obtenidos es " + resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(
									JsonObject.mapFrom(new Usuario(row.getInteger("idusuario"), row.getString("nombre"),
											row.getLong("tlf_usuario"), row.getString("correo_usuario"),
											row.getLong("tlf_emergencia"), row.getString("modelo_moto"))));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(result.encodePrettily());
					} else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}

				});
	}
	private void putUsuario(RoutingContext routingContext) {
		Usuario Usuario = Json.decodeValue(routingContext.getBodyAsString(), Usuario.class);
		mySQLPool.preparedQuery("INSERT INTO dadproyecto.usuario (idusuario,nombre,tlf_usuario,correo_usuario,tlf_emergencia,modelo_moto) VALUES (?,?,?,?,?,?)",
				Tuple.of(Usuario.getIdusuario(), Usuario.getNombre(),Usuario.getTlf_usuario(), Usuario.getCorreo_usuario(),
						Usuario.getTlf_emergencia(),Usuario.getModelo_moto()),
				handler -> {
					if (handler.succeeded()) {
						System.out.println("El numero de elementos insertado es:"+handler.result().rowCount());

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						Usuario.setIdusuario((int) id);

						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(Usuario).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	private void postUsuarioActualiza(RoutingContext routingContext) {
		Usuario Usuario = Json.decodeValue(routingContext.getBodyAsString(), Usuario.class);
		mySQLPool.query("UPDATE dadproyecto.usuario SET nombre='"+Usuario.getNombre()+"'"+
				",tlf_usuario="+Usuario.getTlf_usuario()+
				",correo_usuario='"+Usuario.getCorreo_usuario()+"'"+
				",tlf_emergencia="+Usuario.getTlf_emergencia()
				+",modelo_moto='"+Usuario.getModelo_moto()+"'"+"WHERE idusuario="+Usuario.getIdusuario(),
				handler -> {
					if (handler.succeeded()) {
						System.out.println("Ha sido actualizado "+handler.result().rowCount()+" linea/s");

						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						Usuario.setIdusuario((int) id);
						System.out.println(Usuario);
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(Usuario).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	private void deleteUsuario(RoutingContext routingContext) {
		mySQLPool.query(
				"DELETE  FROM dadproyecto.usuario WHERE idusuario = " + routingContext.request().getParam("idusuario"),
				res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El numero de elementos eliminados es " + resultSet.rowCount());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(
									JsonObject.mapFrom(new Usuario(row.getInteger("idusuario"), row.getString("nombre"),
											row.getLong("tlf_usuario"), row.getString("correo_usuario"),
											row.getLong("tlf_emergencia"), row.getString("modelo_moto"))));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(result.encodePrettily());
					} else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
						.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}

				});
	}
}