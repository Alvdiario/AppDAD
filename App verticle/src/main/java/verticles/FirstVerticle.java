package verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;

public class FirstVerticle extends AbstractVerticle {
	@SuppressWarnings("deprecation")
	@Override

	public void start(Future<Void> startFuture) {
//		
	
		vertx.deployVerticle(DatabaseVerticle.class.getName());
		vertx.deployVerticle(TelegramBaseVerticle.class.getName());

	}
}