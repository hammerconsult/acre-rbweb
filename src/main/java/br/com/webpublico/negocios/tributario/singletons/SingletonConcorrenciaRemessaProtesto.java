package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.entidades.UsuarioSistema;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.io.Serializable;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonConcorrenciaRemessaProtesto implements Serializable {

    private boolean enviando;
    private UsuarioSistema usuarioSistema;

    @PostConstruct
    private void init() {
        this.enviando = false;
    }

    @Lock(LockType.WRITE)
    public void lock(UsuarioSistema usuarioSistema) {
        this.enviando = true;
        this.usuarioSistema = usuarioSistema;
    }

    @Lock(LockType.WRITE)
    public void unLock() {
        this.enviando = false;
        this.usuarioSistema = null;
    }

    @Lock(LockType.WRITE)
    public boolean isLocked() {
        return enviando;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }
}
