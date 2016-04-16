package donatr;

import io.vertx.rxjava.core.Vertx;

public class DonatrMain {
	public static void main(final String[] args) {
		System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
		new DonatrMain().run(Vertx.vertx());
	}

	public void run(final Vertx vertx) {
		vertx.deployVerticle("donatr.DonatrRouter");
	}
}
