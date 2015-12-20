package donatr;

import donatr.event.AccountCreatedEvent;
import io.netty.util.CharsetUtil;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.Json;

public class EventMessageCodec implements MessageCodec<AccountCreatedEvent, String> {
	public void encodeToWire(Buffer buffer, AccountCreatedEvent distributedEvent) {
		String strJson = Json.encode(distributedEvent);
		byte[] encoded = strJson.getBytes(CharsetUtil.UTF_8);
		buffer.appendInt(encoded.length);
		Buffer buff = Buffer.buffer(encoded);
		buffer.appendBuffer(buff);
	}

	public String decodeFromWire(int pos, Buffer buffer) {
		int length = buffer.getInt(pos);
		pos += 4;
		byte[] encoded = buffer.getBytes(pos, pos + length);
		return new String(encoded, CharsetUtil.UTF_8);
	}

	public String transform(AccountCreatedEvent distributedEvent) {
		return Json.encode(distributedEvent);
	}

	public String name() {
		return "AccountCreatedEvent";
	}

	public byte systemCodecID() {
		return (byte)-1;
	}
}
