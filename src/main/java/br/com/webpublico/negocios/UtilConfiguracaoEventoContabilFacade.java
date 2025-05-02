package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConfiguracaoEvento;
import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.LancamentoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by mga on 22/06/2017.
 */
@Stateless
public class UtilConfiguracaoEventoContabilFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public void verifcarAlteracoesEvento(ConfiguracaoEvento configuracaoOriginal, ConfiguracaoEvento selecionado, Boolean eventoAlterado) {
        if (!configuracaoOriginal.getTipoLancamento().equals(selecionado.getTipoLancamento())) {
            eventoAlterado = true;
        }
        if (!configuracaoOriginal.getInicioVigencia().equals(selecionado.getInicioVigencia())) {
            eventoAlterado = true;
        }
        if (!configuracaoOriginal.getEventoContabil().equals(selecionado.getEventoContabil())) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
            eventoContabilFacade.geraEventosReprocessar(configuracaoOriginal.getEventoContabil(), configuracaoOriginal.getId(), configuracaoOriginal.getClass().getSimpleName());
        }
        if (eventoAlterado) {
            eventoContabilFacade.geraEventosReprocessar(selecionado.getEventoContabil(), selecionado.getId(), selecionado.getClass().getSimpleName());
        }
    }


    public void validarExercicioDaData(Date dataInicial, Date dataFinal) {
        ValidacaoException ve = new ValidacaoException();
        verificarDataEmExercicioDiferente(ve, dataInicial, sistemaFacade.getExercicioCorrente().getAno().toString(), " O inicio de vigência deve ter o mesmo ano do exercício corrente.");
        verificarDataEmExercicioDiferente(ve, dataFinal, sistemaFacade.getExercicioCorrente().getAno().toString(), " O fim de vigência deve ter o mesmo ano do exercício corrente.");
        verificarDataEmExercicioDiferente(ve, dataInicial, dataFinal, " As datas estão com exercícios diferentes.");
        ve.lancarException();
    }


    public void validarEncerramentoVigencia(Date inicioVigencia, Date fimVigencia, EventoContabil eventoContabil) {
        ValidacaoException ve = new ValidacaoException();
        verificarCampoNull(ve, fimVigencia, "O campo fim de vigência deve ser informado.");
        ve.lancarException();

        if (fimVigencia.before(inicioVigencia)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O fim de vigência deve ser superior ao inicio de vigência.");
        }
        ve.lancarException();
        validarExercicioDaData(inicioVigencia, fimVigencia);
    }

    public void validarVigenciaEncerrada(Date inicioVigencia, Date fimVigencia) {
        ValidacaoException ve = new ValidacaoException();
        if (fimVigencia != null) {
            if (inicioVigencia.after(fimVigencia)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(" Vigência já encerrada na data: " + DataUtil.getDataFormatada(fimVigencia)
                    + ".  Para editar a configuração, o início de vigência deve ser inferior ao fim de vigência.");
            }
        }
        ve.lancarException();
    }

    public List<LancamentoContabil> verificarVigencia(Date data, EventoContabil obj) {
        String sql = " select lanc.* from lancamentocontabil lanc ";
        sql += " inner join itemparametroevento item on item.id=lanc.itemparametroevento_id";
        sql += " inner join parametroevento paramet on paramet.id=item.parametroevento_id and paramet.eventocontabil_id =:evento";
        sql += " where datalancamento >= :data ";
        Query q = em.createNativeQuery(sql, LancamentoContabil.class);
        q.setParameter("data", data);
        q.setParameter("evento", obj.getId());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return q.getResultList();
    }


    public void verificarCampoNull(ValidacaoException ve, Object campo, String mensagemValidacao) {
        if (campo == null) {
            ve.adicionarMensagemDeCampoObrigatorio(mensagemValidacao);
        }
    }

    public boolean isVigenciaEncerrada(Date fimVigencia) {
        return fimVigencia == null || Util.getDataHoraMinutoSegundoZerado(fimVigencia).compareTo(Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao())) >= 0;
    }

    private void verificarDataEmExercicioDiferente(ValidacaoException ve, Date data, Object campoExercicioStringOuData, String mensagemValidacao) {
        SimpleDateFormat formata = new SimpleDateFormat("yyyy");
        if (campoExercicioStringOuData instanceof Date) {
            campoExercicioStringOuData = formata.format(campoExercicioStringOuData);
        }
        if (!formata.format(data).equals(campoExercicioStringOuData)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(mensagemValidacao);
        }
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }
}
