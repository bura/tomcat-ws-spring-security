package org.bura.tomcat.ws;


import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.bura.tomcat.ws.component.TimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;


public class DispatcherWebsocketServlet extends WebSocketServlet {

	private static final long serialVersionUID = 7581359593670014738L;

	private static final Logger log = LoggerFactory.getLogger(DispatcherWebsocketServlet.class);

	private final List<StreamInbound> sessions = new CopyOnWriteArrayList<>();

	private final AnnotationConfigWebApplicationContext context;

	public DispatcherWebsocketServlet(AnnotationConfigWebApplicationContext context) {
		this.context = context;
	}

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("HH:mm:ss");

	@Override
	protected StreamInbound createWebSocketInbound(String subProtocol, HttpServletRequest request) {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		StreamInbound inbound = new StreamInbound() {

			@Override
			protected void onOpen(WsOutbound outbound) {
				sessions.add(this);
				log.debug("WebSocket connection opened");
			}

			@Override
			protected void onClose(int status) {
				sessions.remove(this);
				log.debug("WebSocket connection closed");
			}

			@Override
			protected void onBinaryData(InputStream is) throws IOException {}

			@Override
			protected void onTextData(Reader r) throws IOException {
				CharArrayWriter out = new CharArrayWriter();
				char[] buffer = new char[1024];
				while (true) {
					int count = r.read(buffer);
					if (count > 0) {
						out.write(buffer, 0, count);
						if (count == buffer.length) {

							continue;
						}
					}

					break;
				}

				out.flush();
				String command = out.toString();
				log.info("Recived: " + command);

				String message;
				if ("time".equalsIgnoreCase(command)) {
					try {
						SecurityContextHolder.getContext().setAuthentication(authentication);
						TimeService service = context.getBean(TimeService.class);

						message = "Time is " + FORMAT.format(service.getCurrentTime());
					} catch (Exception e) {
						e.printStackTrace();
						message = "Access denied";
					}
				} else if ("hello".equalsIgnoreCase(command)) {
					message = "Hello " + (authentication != null ? authentication.getName() : "null");
				} else {
					message = "Unknown command: " + command;
				}

				WsOutbound outbound = this.getWsOutbound();
				CharBuffer cb = CharBuffer.wrap(message);
				try {
					outbound.writeTextMessage(cb);
					outbound.flush();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				cb.clear();
			}

		};

		return inbound;
	}
}
