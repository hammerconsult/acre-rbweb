package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.ConfiguracaoMetrica;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpush.push.Push;
import com.google.common.collect.Lists;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class VerificadorSessoesAtivasUsuario {

    private static String AVISO_FIM_SESSAO = "avisoFimSessao";
    public static String MATAR_SESSAO = "logout";
    public static int TEMPO_MAXIMO_INATIVO_MINUTOS = 15;
    public static int TEMPO_ANTES_AVISO_ENCERRAR_SESSAO_MINUTOS = 5;

    @Autowired
    private SingletonRecursosSistema singletonRecursosSistema;

    public void notificarTodosUsuariosLogados(String pushId) {
        Map<UsuarioSistema, LocalDateTime> ultimoAcessoUsuario = singletonRecursosSistema.getUltimoAcessoUsuario();
        for (UsuarioSistema usuarioSistema : ultimoAcessoUsuario.keySet()) {
            Push.sendTo(usuarioSistema, pushId);
        }
    }

    @Scheduled(cron = "0/15 * * * * ?")
    public void verificarUsuarios() {
        if (getConfiguracaoMetrica().getVerificarTimeout()) {
            Map<UsuarioSistema, LocalDateTime> ultimoAcessoUsuario = singletonRecursosSistema.getUltimoAcessoUsuario();
            List<UsuarioSistema> remover = Lists.newArrayList();

            if (ultimoAcessoUsuario != null) {
                for (UsuarioSistema usuarioSistema : ultimoAcessoUsuario.keySet()) {
                    if (ultimoAcessoUsuario.get(usuarioSistema).isBefore(LocalDateTime.now().minusMinutes(usuarioSistema.getTempoMaximoInativoMinutos()))) {
                        Push.sendTo( usuarioSistema, MATAR_SESSAO);
                        remover.add(usuarioSistema);
                        continue;
                    }
                    if (ultimoAcessoUsuario.get(usuarioSistema).isBefore(LocalDateTime.now().minusMinutes(getTempoAntesDoAvisoEncerrarSessaoMinutos(usuarioSistema)))) {
                        Push.sendTo( usuarioSistema, AVISO_FIM_SESSAO);
                    }
                }
            }
            for (UsuarioSistema usuarioSistema : remover) {
                singletonRecursosSistema.getUltimoAcessoUsuario().remove(usuarioSistema);
            }
        }
    }

    public static int getTempoAntesDoAvisoEncerrarSessaoMinutos(UsuarioSistema usuarioSistema) {
        double tempoMaximoInativoMinutos = usuarioSistema.getTempoMaximoInativoMinutos();
        double tempoAntesAvisoEncerrarSessaoMinutos = tempoMaximoInativoMinutos - TEMPO_ANTES_AVISO_ENCERRAR_SESSAO_MINUTOS;
        if (TEMPO_ANTES_AVISO_ENCERRAR_SESSAO_MINUTOS > tempoMaximoInativoMinutos) {
            tempoAntesAvisoEncerrarSessaoMinutos = tempoMaximoInativoMinutos - 1;
        }
        return (int) tempoAntesAvisoEncerrarSessaoMinutos;
    }

    public ConfiguracaoMetrica getConfiguracaoMetrica() {
        return singletonRecursosSistema.getSingletonMetricas().getConfiguracaoMetrica();
    }
}
