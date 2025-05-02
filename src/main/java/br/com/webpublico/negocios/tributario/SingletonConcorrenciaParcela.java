package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.interfaces.IParcela;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;
import java.util.Set;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonConcorrenciaParcela implements Serializable {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Set<Long> parcelas;
    @EJB
    private ConsultaDebitoFacade debitoFacade;

    @PostConstruct
    private void init() {
        parcelas = Sets.newHashSet();
    }

    @Lock(LockType.WRITE)
    public void lock(Long idParcela) {
        parcelas.add(idParcela);
    }

    @Lock(LockType.WRITE)
    public void lock(IParcela parcela) {
        parcelas.add(parcela.getId());
    }


    @Lock(LockType.WRITE)
    public void lock(List<IParcela> parcelas) {
        for (IParcela parcela : parcelas) {
            this.parcelas.add(parcela.getId());
        }
    }

    @Lock(LockType.WRITE)
    public void unLock(Long idParcela) {
        parcelas.remove(idParcela);
    }

    @Lock(LockType.WRITE)
    public void unLock(IParcela parcela) {
        parcelas.remove(parcela.getId());
    }

    @Lock(LockType.WRITE)
    public void unLock(List<IParcela> parcelas) {
        for (IParcela parcela : parcelas) {
            this.parcelas.remove(parcela.getId());
        }
    }

    @Lock(LockType.WRITE)
    public boolean isLocked(Long idParcela) {
        return parcelas.contains(idParcela);
    }

    private ParcelaValorDivida recuperaParcela(Long idParcela) {
        return em.find(ParcelaValorDivida.class, idParcela);
    }

    @Lock(LockType.WRITE)
    public boolean isLocked(IParcela parcela) {
        return parcelas.contains(parcela.getId());
    }

    @Lock(LockType.WRITE)
    public boolean isDisponivel(Long idParcela, SituacaoParcela situacao) {
        ParcelaValorDivida parcela = recuperaParcela(idParcela);
        return (debitoFacade.getUltimaSituacao(parcela).getSituacaoParcela().equals(situacao)
            && !parcelas.contains(parcela.getDividaId()));
    }

    @Lock(LockType.WRITE)
    public boolean isDisponivel(IParcela idParcela, SituacaoParcela situacao) {
        ParcelaValorDivida parcela = recuperaParcela(idParcela.getId());
        return (debitoFacade.getUltimaSituacao(parcela).getSituacaoParcela().equals(situacao)
            && !parcelas.contains(parcela.getDividaId()));
    }

    public List<Long> getParcelas() {
        return Lists.newArrayList(parcelas);
    }
}
