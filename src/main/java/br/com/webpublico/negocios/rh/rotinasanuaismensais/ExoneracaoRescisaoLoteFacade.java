package br.com.webpublico.negocios.rh.rotinasanuaismensais;

import br.com.webpublico.entidades.ExoneracaoRescisao;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.exoneracao.ExoneracaoRescisaoContrato;
import br.com.webpublico.entidades.rh.exoneracao.ExoneracaoRescisaoLote;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExoneracaoRescisaoFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class ExoneracaoRescisaoLoteFacade extends AbstractFacade<ExoneracaoRescisaoLote> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;

    public ExoneracaoRescisaoLoteFacade() {
        super(ExoneracaoRescisaoLote.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ExoneracaoRescisaoLote recuperar(Object id) {
        ExoneracaoRescisaoLote exoneracao = super.recuperar(id);
        Hibernate.initialize(exoneracao.getItemExoneracaoContrato());
        return exoneracao;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public ExoneracaoRescisaoLote salvar(ExoneracaoRescisaoLote entity, AssistenteBarraProgresso assistenteBarraProgresso) throws IOException {
        for (ExoneracaoRescisaoContrato itemExoneracaoContrato : entity.getItemExoneracaoContrato()) {
            criarExoneracao(itemExoneracaoContrato.getVinculoFP(), entity);
            assistenteBarraProgresso.conta();
        }
        for (FichaFinanceiraFP ficha : buscarFichasFinanceirasParaExclusao(entity)) {
            exoneracaoRescisaoFacade.excluirFichaFinanceira(ficha);
        }
        assistenteBarraProgresso.conta();
        return super.salvarRetornando(entity);
    }

    public List<FichaFinanceiraFP> buscarFichasFinanceirasParaExclusao(ExoneracaoRescisaoLote exoneracaoLote) {
        List<FichaFinanceiraFP> fichas = Lists.newArrayList();
        for (ExoneracaoRescisaoContrato item : exoneracaoLote.getItemExoneracaoContrato()) {
            FichaFinanceiraFP fichaFinanceiraFP = exoneracaoRescisaoFacade.buscarFichaFinanceiraParaExclusao(item.getVinculoFP(), exoneracaoLote.getDataExoneracao());
            if (fichaFinanceiraFP != null) {
                fichas.add(fichaFinanceiraFP);
            }
        }
        return fichas;
    }

    private void criarExoneracao(VinculoFP vinculoFP, ExoneracaoRescisaoLote exoneracaoRescisaoLote) {
        ExoneracaoRescisao exoneracao = new ExoneracaoRescisao();
        exoneracao.setAtoLegal(exoneracaoRescisaoLote.getAtoLegal());
        exoneracao.setMotivoExoneracaoRescisao(exoneracaoRescisaoLote.getMotivoExoneracaoRescisao());
        exoneracao.setDataRescisao(exoneracaoRescisaoLote.getDataExoneracao());
        exoneracao.setVinculoFP(vinculoFP);
        exoneracaoRescisaoFacade.realizarRecisaoContratoPF(exoneracao, exoneracaoRescisaoLote.getDataExoneracao());
    }

    public List<VinculoFP> buscarVinculoFPPorModalidadeContrato(List<ModalidadeContratoFP> modalidades) {
        String sql = "select vinculo.id " +
            " from CONTRATOFP contrato" +
            "         inner join vinculofp vinculo on contrato.ID = vinculo.ID" +
            "         inner join MODALIDADECONTRATOFP modalidade on contrato.MODALIDADECONTRATOFP_ID = modalidade.ID" +
            " where modalidade.id in :modalidades" +
            "  and sysdate between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, sysdate)" +
            "  and not exists (select * from EXONERACAORESCISAO exoneracao where exoneracao.VINCULOFP_ID = vinculo.id ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("modalidades", getIDModalidades(modalidades));
        List resultList = q.getResultList();
        List<VinculoFP> vinculos = Lists.newArrayList();
        if (!resultList.isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                vinculos.add(em.find(VinculoFP.class, id.longValue()));
            }
            return vinculos;
        }
        return null;
    }

    private List<Long> getIDModalidades(List<ModalidadeContratoFP> modalidades) {
        List<Long> ids = Lists.newArrayList();
        for (ModalidadeContratoFP modalidade : modalidades) {
            ids.add(modalidade.getId());
        }
        return ids;
    }
}

