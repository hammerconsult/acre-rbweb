package br.com.webpublico.singletons;

import br.com.webpublico.entidades.ItemLoteBaixa;
import br.com.webpublico.entidades.LoteBaixa;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.VOUsuarioArrecadacaoTributaria;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Map;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 50000)
public class SingletonArrecadacaoTributaria implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Map<Long, UsuarioSistema> cacheItemUsuario;
    private boolean executandoIntegracao;

    @PostConstruct
    public void init() {
        inicializarCache();
        executandoIntegracao = false;
    }

    public void inicializarCache() {
        cacheItemUsuario = Maps.newHashMap();
    }

    @Lock(LockType.WRITE)
    public void bloquearItens(LoteBaixa loteBaixa, UsuarioSistema usuario) {
        loteBaixa = recuperarItensDoLote(loteBaixa);
        for (ItemLoteBaixa ilb : loteBaixa.getItemLoteBaixa()) {
            if (!cacheItemUsuario.containsKey(ilb.getId())) {
                cacheItemUsuario.put(ilb.getId(), usuario);
            }
        }
    }

    @Lock(LockType.WRITE)
    public void desbloquearItens(LoteBaixa loteBaixa) {
        loteBaixa = recuperarItensDoLote(loteBaixa);
        for (ItemLoteBaixa ilb : loteBaixa.getItemLoteBaixa()) {
            cacheItemUsuario.remove(ilb.getId());
        }
    }

    @Lock(LockType.WRITE)
    public VOUsuarioArrecadacaoTributaria verificarDisponibilidadeDosItens(LoteBaixa loteBaixa) {
        loteBaixa = recuperarItensDoLote(loteBaixa);
        for (ItemLoteBaixa ilb : loteBaixa.getItemLoteBaixa()) {
            if (cacheItemUsuario.containsKey(ilb.getId())) {
                return new VOUsuarioArrecadacaoTributaria(cacheItemUsuario.get(ilb.getId()), Boolean.FALSE);
            }
        }
        return new VOUsuarioArrecadacaoTributaria(null, Boolean.TRUE);
    }

    @Lock(LockType.WRITE)
    public LoteBaixa recuperarItensDoLote(LoteBaixa loteBaixa) {
        loteBaixa = em.find(LoteBaixa.class, loteBaixa.getId());
        Hibernate.initialize(loteBaixa.getItemLoteBaixa());
        return loteBaixa;
    }

    public boolean isExecutandoIntegracao() {
        return executandoIntegracao;
    }

    public void setExecutandoIntegracao(boolean executandoIntegracao) {
        this.executandoIntegracao = executandoIntegracao;
    }

    public String getMensagemJaExecutando() {
        return "Uma integração ja está em execução, vá para a tela de processos assíncronos para visualizar o progresso.";
    }
}
