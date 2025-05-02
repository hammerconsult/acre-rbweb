package br.com.webpublico.singletons;


import br.com.webpublico.entidades.UsuarioSistema;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
//@AccessTimeout(value = 5000)
public class SingletonFolhaDePagamento implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private boolean calculandoFolha;
    private UsuarioSistema usuarioSistema;
    private Date dataInicioCalculo;
    private Integer contador = 0;

    @Lock(value = LockType.READ)
    public boolean isCalculandoFolha() {
        return calculandoFolha;
    }

    @Lock(value = LockType.WRITE)
    public void setCalculandoFolha(boolean calculandoFolha) {
        this.calculandoFolha = calculandoFolha;
    }

    @Lock(value = LockType.READ)
    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    @Lock(value = LockType.WRITE)
    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Lock(value = LockType.READ)
    public Date getDataInicioCalculo() {
        return dataInicioCalculo;
    }

    @Lock(value = LockType.WRITE)
    public void setDataInicioCalculo(Date dataInicioCalculo) {
        this.dataInicioCalculo = dataInicioCalculo;
    }


    @Lock(value = LockType.READ)
    public Integer getContador() {
        return contador;
    }

    @Lock(value = LockType.READ)
    public synchronized void contarDuplicidade() {
        this.contador++;
    }

    @Lock(value = LockType.WRITE)
    public void setContador(Integer contador) {
        this.contador = contador;
    }
}
