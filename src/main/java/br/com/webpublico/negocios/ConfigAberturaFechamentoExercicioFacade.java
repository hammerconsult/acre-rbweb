package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfigAberturaFechamentoExercicio;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.PatrimonioLiquido;
import br.com.webpublico.enums.TipoMovimentoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.Date;

/**
 * Created by mateus on 12/12/17.
 */
@Stateless
public class ConfigAberturaFechamentoExercicioFacade extends AbstractFacade<ConfigAberturaFechamentoExercicio> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private UtilConfiguracaoEventoContabilFacade utilFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public ConfigAberturaFechamentoExercicioFacade() {
        super(ConfigAberturaFechamentoExercicio.class);
    }

    @Override
    public void salvarNovo(ConfigAberturaFechamentoExercicio entity) {
        validarCampos(entity);
        super.salvarNovo(entity);
    }

    public void salvar(ConfigAberturaFechamentoExercicio entity) {
        validarCampos(entity);
        utilFacade.validarVigenciaEncerrada(entity.getInicioVigencia(), entity.getFimVigencia());
        super.salvar(entity);
    }

    public void validarCampos(ConfigAberturaFechamentoExercicio entity) {
        entity.realizarValidacoes();
        validarConfiguracaoExistente(entity);
    }

    private void validarConfiguracaoExistente(ConfigAberturaFechamentoExercicio entity) {
        ValidacaoException ve = new ValidacaoException();
        if (hasConfiguracaoVigente(entity)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Existe uma configuração vigente para os parâmetros: "
                + " tipo de movimento: " + entity.getTipoMovimentoContabil().getDescricao()
                + " e Patrimonio Líquido: " + entity.getPatrimonioLiquido().getDescricao());
        }
        ve.lancarException();
    }

    private boolean hasConfiguracaoVigente(ConfigAberturaFechamentoExercicio entity) {
        String sql = " select 1 from CONFIGABERTURAFECHAMENTOEX conf " +
            " inner join configuracaoevento ce on conf.id = ce.id " +
            " where conf.tipomovimentocontabil = :tipoMovimento " +
            "   and conf.patrimonioLiquido = :patrimonioLiquido " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ce.iniciovigencia) and coalesce(trunc(ce.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        if (entity.getId() != null) {
            sql += " and conf.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoMovimento", entity.getTipoMovimentoContabil().name());
        q.setParameter("patrimonioLiquido", entity.getPatrimonioLiquido().name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (entity.getId() != null) {
            q.setParameter("id", entity.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public void encerrarVigencia(ConfigAberturaFechamentoExercicio entity) {
        utilFacade.validarEncerramentoVigencia(entity.getInicioVigencia(), entity.getFimVigencia(), entity.getEventoContabil());
        super.salvar(entity);
    }

    public EventoContabil buscarEventoAberturaFechamentoExercicio(Date data, PatrimonioLiquido patrimonioLiquido, TipoMovimentoContabil tipoMovimentoContabil) throws ExcecaoNegocioGenerica, NonUniqueResultException {
        String msgErro;
        try {
            Preconditions.checkNotNull(data, " A data do lançamento não foi informada.");
            Preconditions.checkNotNull(patrimonioLiquido, " O tipo Prefeitura/Empresa Pública não foi informado.");
            Preconditions.checkNotNull(tipoMovimentoContabil, " O tipo de movimento contábil não foi informado.");

            return buscarEventoPelaConfiguracao(data, patrimonioLiquido, tipoMovimentoContabil);
        } catch (NoResultException nr) {
            msgErro = "Configuração de Evento não encontrada para o tipo: " + patrimonioLiquido.getDescricao()
                + ", tipo de movimento contábil: " + tipoMovimentoContabil.getDescricao()
                + "  na data: " + DataUtil.getDataFormatada(data) + ".";
            throw new ExcecaoNegocioGenerica(msgErro);
        } catch (Exception e) {
            msgErro = e.getMessage();
            throw new ExcecaoNegocioGenerica(msgErro);
        }
    }

    private EventoContabil buscarEventoPelaConfiguracao(Date data, PatrimonioLiquido patrimonioLiquido, TipoMovimentoContabil tipoMovimentoContabil) throws Exception {
        String sql = " select ev.* from CONFIGABERTURAFECHAMENTOEX conf " +
            " inner join configuracaoevento ce on conf.id = ce.id " +
            " inner join eventocontabil ev on ce.eventocontabil_id = ev.id " +
            " where conf.tipomovimentocontabil = :tipoMovimento " +
            "   and conf.patrimonioLiquido = :patrimonioLiquido " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ce.iniciovigencia) and coalesce(trunc(ce.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ";
        Query q = em.createNativeQuery(sql, EventoContabil.class);
        q.setParameter("patrimonioLiquido", patrimonioLiquido.name());
        q.setParameter("tipoMovimento", tipoMovimentoContabil.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        return (EventoContabil) q.getSingleResult();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
