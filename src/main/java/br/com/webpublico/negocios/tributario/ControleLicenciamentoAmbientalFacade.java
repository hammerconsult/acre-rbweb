package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidades.AnexoControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.tributario.ControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.RevisaoControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.TecnicoLicenciamentoAmbiental;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ControleLicenciamentoAmbientalFacade extends AbstractFacade<ControleLicenciamentoAmbiental> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TecnicoLicenciamentoAmbientalFacade tecnicoLicenciamentoAmbientalFacade;
    @EJB
    private ProcessoLicenciamentoAmbientalFacade processoLicenciamentoAmbientalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;

    public ControleLicenciamentoAmbientalFacade() {
        super(ControleLicenciamentoAmbiental.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TecnicoLicenciamentoAmbientalFacade getTecnicoLicenciamentoAmbientalFacade() {
        return tecnicoLicenciamentoAmbientalFacade;
    }

    public ProcessoLicenciamentoAmbientalFacade getProcessoLicenciamentoAmbientalFacade() {
        return processoLicenciamentoAmbientalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Override
    public ControleLicenciamentoAmbiental recuperar(Object id) {
        ControleLicenciamentoAmbiental controle = super.recuperar(id);
        if (controle == null) return null;
        if (controle.getRevisoes() != null) {
            Hibernate.initialize(controle.getRevisoes());
            for (RevisaoControleLicenciamentoAmbiental revisao : controle.getRevisoes()) {
                Hibernate.initialize(revisao.getAnexos());
                if (revisao.getAnexos() != null) {
                    for (AnexoControleLicenciamentoAmbiental anexo : revisao.getAnexos()) {
                        Hibernate.initialize(anexo.getArquivo().getPartes());
                    }
                }
            }
        }
        if (controle.getAnexos() != null) {
            Hibernate.initialize(controle.getAnexos());
            for (AnexoControleLicenciamentoAmbiental anexo : controle.getAnexos()) {
                Hibernate.initialize(anexo.getArquivo().getPartes());
            }
        }
        return controle;
    }

    @Override
    public void preSave(ControleLicenciamentoAmbiental entidade) {
        entidade.realizarValidacoes();
        ProcessoLicenciamentoAmbiental processo = processoLicenciamentoAmbientalFacade.recuperar(entidade.getProcesso().getId());
        if (StatusLicenciamentoAmbiental.ARQUIVADO.equals(processo.getStatus()) ||
            StatusLicenciamentoAmbiental.ENCERRADO.equals(processo.getStatus())) {
            throw new ValidacaoException("O Processo selecionado já encontra-se " + processo.getStatus().getDescricao() + ", não é permitido novos pareceres.");
        }
    }

    @Override
    public void salvarNovo(ControleLicenciamentoAmbiental entity) {
        super.salvarNovo(entity);
        processoLicenciamentoAmbientalFacade.definirStatusAtual(entity.getProcesso(), entity.getStatus());
    }

    @Override
    public void salvar(ControleLicenciamentoAmbiental entity) {
        super.salvar(entity);
        processoLicenciamentoAmbientalFacade.definirStatusAtual(entity.getProcesso(), entity.getStatus());
    }

    public void salvarSemMudarSituacaoProcesso(ControleLicenciamentoAmbiental entity) {
        super.salvar(entity);
    }

    @Override
    public void remover(ControleLicenciamentoAmbiental entity) {
        ProcessoLicenciamentoAmbiental processo = entity.getProcesso();
        super.remover(entity);
        processoLicenciamentoAmbientalFacade.definirStatusAtual(processo, entity.getStatus());
    }

    public List<ControleLicenciamentoAmbiental> buscarControlesPorProcesso(ProcessoLicenciamentoAmbiental processo) {
        List<BigDecimal> ids = em.createNativeQuery(" select c.id from controlelicenciamentoambiental c " +
                " where c.processo_id = :processo_id " +
                " order by c.datahora desc ")
            .setParameter("processo_id", processo.getId())
            .getResultList();
        List<ControleLicenciamentoAmbiental> controles = Lists.newArrayList();
        for (BigDecimal id : ids) {
            controles.add(recuperar(id.longValue()));
        }
        return controles;
    }

    public List<ProcessoLicenciamentoAmbiental> buscarProcessosDoTecnico(TecnicoLicenciamentoAmbiental tecnico) {
        String sql = "select p.* from ProcessoLicenciamentoAmbiental p " +
            " where p.status in (:situacoes) " +
            " and exists (select t.id from TecnicoProcessoLicenciamentoAmbiental t " +
            "              where t.processoLicenciamentoAmbiental_id = p.id" +
            "               and t.tecnicoLicenciamentoAmbiental_id = :idTecnico)";
        Query q = em.createNativeQuery(sql, ProcessoLicenciamentoAmbiental.class);
        q.setParameter("situacoes", StatusLicenciamentoAmbiental.getSituacoesAtivasName());
        q.setParameter("idTecnico", tecnico.getId());
        return q.getResultList();
    }
}
