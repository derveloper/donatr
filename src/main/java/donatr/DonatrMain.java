package donatr;

import io.vertx.rxjava.core.Vertx;

public class DonatrMain {
	public static void main(final String[] args) {
		new DonatrMain().run(Vertx.vertx());
	}

	public void run(final Vertx vertx) {
		vertx.deployVerticle("donatr.DonatrRouter");
	}
}
