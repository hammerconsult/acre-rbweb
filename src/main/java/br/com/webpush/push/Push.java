package br.com.webpush.push;

import br.com.webpublico.entidades.UsuarioSistema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
@ServerEndpoint("/push")
public class Push {

    private static final Set<Session> SESSIONS = ConcurrentHashMap.newKeySet();
    protected static final Logger logger = LoggerFactory.getLogger(Push.class);

    @OnOpen
    public void onOpen(Session session) {
        SESSIONS.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        SESSIONS.remove(session);
    }

    public static void sendAll(String text) {
        synchronized (SESSIONS) {
            for (Session session : SESSIONS) {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(text);
                }
            }
        }
    }

    public static void sendTo(UsuarioSistema user, String text) {
        synchronized (SESSIONS) {
            List<Session> todasDoUsuariuo = SESSIONS.stream().filter(s -> s.getUserPrincipal().getName().equals(user.getLogin()))
                .collect(Collectors.toList());
            todasDoUsuariuo.forEach(s -> s.getAsyncRemote().sendText(text));
        }
    }
}
