package br.com.webpublico.negocios.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoConta;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

@Service
public class ServiceNotificacaoInconsistenciaRH {

    @PersistenceContext
    protected transient EntityManager em;

    public void recuperarInconsistencias() {
        for (VinculoFP vinculo : recuperaVinculosSemLotacaoVigente()) {
            criarNotificacao(getUrl(vinculo), "O vínculo " + vinculo.getMatriculaFP().getPessoa().getNome() + " não possui Lotação Funcional Vigente.");
        }
        for (VinculoFP vinculo : recuperaVinculosSemRecursoFPVigente()) {
            criarNotificacao(getUrl(vinculo), "Ausência de Recurso FP vigente para o vínculo " + vinculo.getMatriculaFP().getPessoa().getNome() + ".");
        }
        for (VinculoFP vinculo : recuperaVinculosSemContaCorrenteBancariaAtiva()) {
            criarNotificacao(getUrl(vinculo), "Ausência de Conta Corrente Bancária ativa para o vínculo " + vinculo.getMatriculaFP().getPessoa().getNome() + ".");
        }
    }

    private String getUrl(VinculoFP vinculo) {
        if (vinculo instanceof Aposentadoria) {
            return "/aposentadoria/editar/" + vinculo.getId() + "/";
        } else if (vinculo instanceof Pensionista) {
            Pensionista p = ((Pensionista) vinculo);
            return "/pensao/editar/" + p.getPensao().getId() + "/";
        } else if (vinculo instanceof Estagiario) {
            return "/estagiario/editar/" + vinculo.getId() + "/";
        } else if (vinculo instanceof Beneficiario) {
            return "/beneficiario/editar/" + vinculo.getId() + "/";
        } else {
            return "/contratofp/editar/" + vinculo.getId() + "/";
        }
    }

    private void criarNotificacao(String url, String descricao) {
//        List<Notificacao> notificacoesSalvas = NotificacaoService.getService().buscarNotificacoesPorLinkAndTipoNaoVisualizado(url, TipoNotificacao.INCONSISTENCIA_CADASTRO_CALCULO_RH);
//        if (notificacoesSalvas.isEmpty()) {
//            NotificacaoService.getService().notificar(TipoNotificacao.INCONSISTENCIA_CADASTRO_CALCULO_RH.getDescricao(),
//                descricao, url, Notificacao.Gravidade.ATENCAO,
//                TipoNotificacao.INCONSISTENCIA_CADASTRO_CALCULO_RH);
//        }
    }

    public List<VinculoFP> recuperaVinculosSemLotacaoVigente() {

        Query q = em.createQuery("from VinculoFP v " +
            " where :data between v.inicioVigencia and coalesce(v.finalVigencia, :data) " +
            " and v not in (select lotacao.vinculoFP from LotacaoFuncional lotacao" +
            "                       where :data between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia, :data))");
        q.setParameter("data", SistemaFacade.getDataCorrente());

        return q.getResultList();
    }

    public List<VinculoFP> recuperaVinculosSemRecursoFPVigente() {

        Query q = em.createQuery("from VinculoFP v " +
            " where :data between v.inicioVigencia and coalesce(v.finalVigencia, :data) " +
            " and v not in (select recurso.vinculoFP from RecursoDoVinculoFP recurso" +
            "                       where :data between recurso.inicioVigencia and coalesce(recurso.finalVigencia, :data))");
        q.setParameter("data", SistemaFacade.getDataCorrente());

        return q.getResultList();
    }

    public List<VinculoFP> recuperaVinculosSemContaCorrenteBancariaAtiva() {

        Query q = em.createQuery(" from VinculoFP v" +
            " where :data between v.inicioVigencia and coalesce(v.finalVigencia, :data) " +
            "   and v not in (select vinculo from VinculoFP vinculo " +
            "                   inner join vinculo.contaCorrente conta" +
            "                       where conta.situacao = :situacao)" +
            "   and v not in (select vinculo from VinculoFP vinculo " +
            "                   inner join vinculo.matriculaFP mat " +
            "                   inner join  mat.pessoa pessoa " +
            "                   inner join pessoa.contaCorrentePrincipal contaPessoa " +
            "                   inner join contaPessoa.contaCorrenteBancaria conta " +
            "                       where conta.situacao = :situacao)");
        q.setParameter("data", SistemaFacade.getDataCorrente());
        q.setParameter("situacao", SituacaoConta.ATIVO);

        return q.getResultList();
    }
}
