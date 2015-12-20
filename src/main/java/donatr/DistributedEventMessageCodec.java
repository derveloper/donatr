package donatr;

import io.netty.util.CharsetUtil;
import io.resx.core.event.DistributedEvent;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class DistributedEventMessageCodec<T extends DistributedEvent> implements MessageCodec<T, String> {
	private final Class<T> clazz;

	public DistributedEventMessageCodec(final Class<T> clazz) {
		this.clazz = clazz;
	}

	public void encodeToWire(final Buffer buffer, final T distributedEvent) {
		final String strJson = Json.encode(distributedEvent);
		final byte[] encoded = strJson.getBytes(CharsetUtil.UTF_8);
		buffer.appendInt(encoded.length);
		final Buffer buff = Buffer.buffer(encoded);
		buffer.appendBuffer(buff);
	}

	public String decodeFromWire(int pos, final Buffer buffer) {
		final int length = buffer.getInt(pos);
		pos += 4;
		final byte[] encoded = buffer.getBytes(pos, pos + length);
		return new String(encoded, CharsetUtil.UTF_8);
	}

	public String transform(final T distributedEvent) {
		return Json.encode(distributedEvent);
	}

	public String name() {
		return clazz.getSimpleName();
	}

	public byte systemCodecID() {
		return (byte) -1;
	}
}
