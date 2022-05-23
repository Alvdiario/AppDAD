package verticles;

import java.text.DecimalFormat;

import org.schors.vertx.telegram.bot.LongPollingReceiver;
import org.schors.vertx.telegram.bot.TelegramBot;
import org.schors.vertx.telegram.bot.TelegramOptions;
import org.schors.vertx.telegram.bot.api.methods.SendLocation;
import org.schors.vertx.telegram.bot.api.methods.SendMessage;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import kt.schors.vertx.telegram.bot.api.types.Update;

public class TelegramBaseVerticle extends AbstractVerticle{

	private TelegramBot bot;
	private Integer ejex;
	private Integer ejey;
	private Integer ejez;
	private String localizacion;
	@Override
	public void start(Promise<Void> future) {
		TelegramOptions telegramOptions = new TelegramOptions()
				.setBotName("Findtomotor_bot")
				.setBotToken("908697753:AAHqjdFUsLFZY5GOzcrz9IaSoZmkzV2gAHM");
		bot = TelegramBot.create(vertx, telegramOptions)
				.receiver(new LongPollingReceiver().onUpdate(handler -> {
					if (handler.getMessage().getText().toLowerCase().contains("hola")) {
						bot.sendMessage(new SendMessage()
								.setText("Hola " + handler.getMessage().getFrom().getFirstName() + " ¿en qué puedo ayudarte?")
								.setChatId(handler.getMessage().getChatId()));
					} else if (handler.getMessage().getText().toLowerCase().contains("inclinacion")) {
						WebClient client = WebClient.create(vertx);
						client.get(8081, "localhost", 
								"/api/mpu6050/2")
						.send(ar -> {
							if (ar.succeeded()) {

								HttpResponse<Buffer> response = ar.result();
								System.out.println(response);
								JsonArray list= response.bodyAsJsonArray();
								JsonObject sensor=list.getJsonObject(list.size()-1);
								System.out.println(sensor);
								System.out.println(sensor);
								ejex= sensor.getInteger("a_value_x");
								ejey= sensor.getInteger("a_value_y");
								ejez= sensor.getInteger("a_value_z");
								double accel_ang_x = Math.atan(ejex / Math.sqrt(Math.pow(ejey, 2) + Math.pow(ejez, 2)))*(180.0 / 3.14);

								bot.sendMessage(new SendMessage()
										.setText("La inclinacion actual en la moto es de " + Math.round(accel_ang_x) + " grados")
										.setChatId(handler.getMessage().getChatId()));
							}else{
								bot.sendMessage(new SendMessage()
										.setText("Vaya, algo ha salido mal")
										.setChatId(handler.getMessage().getChatId()));
							}
						});

					}else if (handler.getMessage().getText().toLowerCase().contains("localizacion")) {
						WebClient client = WebClient.create(vertx);
						client.get(8081, "localhost", 
								"/api/sensor/values/gps/2")
						.send(ar -> {
							if (ar.succeeded()) {

								HttpResponse<Buffer> response = ar.result();
								System.out.println(response);
								JsonArray list= response.bodyAsJsonArray();
								JsonObject sensor1=list.getJsonObject(list.size()-1);

								System.out.println(sensor1);
								localizacion = (String) sensor1.getValue("localizacion");

								bot.sendMessage(new SendMessage()
										.setText("La localizacion actual de la moto es " +localizacion)
										.setChatId(handler.getMessage().getChatId()));
							}else{
								bot.sendMessage(new SendMessage()
										.setText("Vaya, algo ha salido mal")
										.setChatId(handler.getMessage().getChatId()));
							}
						});
					}
				}));

		bot.start();
	}



}
