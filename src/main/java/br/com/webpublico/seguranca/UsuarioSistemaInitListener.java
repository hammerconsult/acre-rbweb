package br.com.webpublico.seguranca;

import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.seguranca.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UsuarioSistemaInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Transactional
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // executa quando sobe o sistema!!
    }

}
