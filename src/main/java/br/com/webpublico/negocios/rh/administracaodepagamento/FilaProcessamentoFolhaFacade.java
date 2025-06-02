/*
 * Codigo gerado automaticamente em Sat Jul 02 11:00:55 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.DetalhesCalculoRH;
import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.administracaodepagamento.FilaProcessamentoFolha;
import br.com.webpublico.entidades.rh.administracaodepagamento.SituacaoCalculoFP;
import br.com.webpublico.negocios.AbstractFacade;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Stateless
public class FilaProcessamentoFolhaFacade extends AbstractFacade<FilaProcessamentoFolha> {

    private static final Logger logger = LoggerFactory.getLogger(FilaProcessamentoFolhaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FilaProcessamentoFolhaFacade() {
        super(FilaProcessamentoFolha.class);
    }

    public Map<VinculoFP, FilaProcessamentoFolha> salvarFilaProcessamento(Set<VinculoFP> vinculos, UsuarioSistema usuarioCorrente, FolhaDePagamento folha) {
        Map<VinculoFP, FilaProcessamentoFolha> filas = new HashMap<>();
        for (VinculoFP vinculo : vinculos) {
            FilaProcessamentoFolha fila = new FilaProcessamentoFolha();
            fila.setVinculoFP(vinculo);
            fila.setUsuario(usuarioCorrente);
            fila.setSituacao(SituacaoCalculoFP.ABERTO);
            fila.setFolhaDePagamento(folha);
            FilaProcessamentoFolha merge = em.merge(fila);
            filas.put(vinculo, merge);
        }
        return filas;
    }

    public void updateSituacao(FilaProcessamentoFolha fila, SituacaoCalculoFP situacao) {
        fila.setFinalizadoEm(new Date());
        fila.setSituacao(situacao);
        em.merge(fila);
    }

    public void updateDetalhe(FilaProcessamentoFolha fila, DetalhesCalculoRH detalhe) {
        detalhe = em.merge(detalhe);
        fila.setDetalhesCalculoRH(detalhe);
        em.merge(fila);
    }

    private void updateFila(String tabela, FilaProcessamentoFolha fila, SituacaoCalculoFP situacao) {
        String sql = " update %s f set f.situacao = :situacao, f.finalizadoEm = :finalizadoEm " +
            "   where f.id = :id ";
        Query q = em.createNativeQuery(String.format(sql, tabela));
        q.setParameter("id", fila.getId());
        q.setParameter("situacao", situacao.name());
        q.setParameter("finalizadoEm", new Date());
        q.executeUpdate();
    }

    public List<FilaProcessamentoFolha> buscarFilasProcessamentoFolhaPorDetalheCalculo(DetalhesCalculoRH detalhe) {
        Query q = em.createQuery(" from FilaProcessamentoFolha fila " +
            " where fila.detalhesCalculoRH = :detalhe ");
        q.setParameter("detalhe", detalhe);
        List<FilaProcessamentoFolha> retorno = q.getResultList();
        if (retorno != null && !retorno.isEmpty()) {
            return retorno;
        }
        return Lists.newArrayList();
    }

    public List<FilaProcessamentoFolha> buscarFilasProcessamentosFolhaPorVinculoFP(VinculoFP vinculoFP) {
        String sql = " select fila.* from FILAPROCESSAMENTOFOLHA fila " +
            " where fila.VINCULOFP_ID = :vinculo ";
        Query q = em.createNativeQuery(sql, FilaProcessamentoFolha.class);
        q.setParameter("vinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

}
