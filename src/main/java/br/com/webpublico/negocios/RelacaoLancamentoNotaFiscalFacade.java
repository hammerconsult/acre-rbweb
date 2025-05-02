package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.AbstractFiltroRelacaoLancamentoDebito;
import br.com.webpublico.entidadesauxiliares.AbstractVOConsultaLancamento;
import br.com.webpublico.entidadesauxiliares.FiltroRelacaoLancamentoNotaFiscal;
import br.com.webpublico.entidadesauxiliares.VOConsultaNotaFiscal;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 03/08/15
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class RelacaoLancamentoNotaFiscalFacade extends AbstractRelacaoLancamentoTributarioFacade {

    private static Logger logger = LoggerFactory.getLogger(RelacaoLancamentoNotaFiscalFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    @Override
    public Future<? extends List<? extends AbstractVOConsultaLancamento>> consultarLancamentos(AbstractFiltroRelacaoLancamentoDebito filtroRelacaoLancamentoDebito) {
        List<VOConsultaNotaFiscal> resultado;
        FiltroRelacaoLancamentoNotaFiscal filtroRelacaoLancamentoNotaFiscal = (FiltroRelacaoLancamentoNotaFiscal) montarWhere(filtroRelacaoLancamentoDebito);
        resultado = consultarLancamentoNotaFiscalAvulsa(filtroRelacaoLancamentoNotaFiscal.getWhere().toString());
        return new AsyncResult<>(resultado);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 12)
    public List<VOConsultaNotaFiscal> consultarLancamentoNotaFiscalAvulsa(String where) {
        String sql = " select vw.*, ued.login usuario_emissao_dam " +
            "    from vwconsultanotafiscalavulsa vw " +
            "  inner join historicoimpressaodam h_dam on h_dam.id = vw.id_historico " +
            "  inner join usuariosistema ued on ued.id = h_dam.usuariosistema_id ";
        sql += where;
        Query q = em.createNativeQuery(sql);
        List<VOConsultaNotaFiscal> toReturn = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        if (resultado != null && resultado.size() > 0) {
            for (List<Object[]> parte : Lists.partition(resultado, 100000)) {
                toReturn.addAll(preencherLancamentoNotaFiscalAvulsa(parte));
            }
        }
        return toReturn;
    }

    public List<VOConsultaNotaFiscal> preencherLancamentoNotaFiscalAvulsa(List<Object[]> objetos) {
        List<VOConsultaNotaFiscal> lista = Lists.newArrayList();
        VOConsultaNotaFiscal voConsultaNotaFiscal;
        if (objetos != null) {
            for (Object[] obj : objetos) {
                voConsultaNotaFiscal = new VOConsultaNotaFiscal(obj);
                lista.add(voConsultaNotaFiscal);
            }
        }
        return lista;
    }

    @Override
    protected AbstractFiltroRelacaoLancamentoDebito montarWhere(AbstractFiltroRelacaoLancamentoDebito abstractFiltroRelacaoLancamentoDebito) {
        FiltroRelacaoLancamentoNotaFiscal filtro = (FiltroRelacaoLancamentoNotaFiscal) super.montarWhere(abstractFiltroRelacaoLancamentoDebito);

        filtro.addFiltro("vw.nota_numero", ">=", "Número Nota Inicial", filtro.getNumeroNotaFiscalInicial());
        filtro.addFiltro("vw.nota_numero", "<=", "Número Nota Final", filtro.getNumeroNotaFiscalFinal());

        filtro.addFiltro("vw.nota_emissao", ">=", "Data de Emissão da Nota Inicial", filtro.getDataEmissaoNotaInicial());
        filtro.addFiltro("vw.nota_emissao", "<=", "Data de Emissão da Nota Final", filtro.getDataEmissaoNotaFinal());

        if (filtro.getTipoCadastroTributarioTomador() != null) {
            if (filtro.getTipoCadastroTributarioTomador().equals(TipoCadastroTributario.ECONOMICO)) {
                filtro.addFiltro("vw.cmc_tomador", ">=", "CMC Tomador Inicial", filtro.getCmcInicialTomador());
                filtro.addFiltro("vw.cmc_tomador", "<=", "CMC Tomador Final", filtro.getCmcFinalPrestador());
            } else if (filtro.getTipoCadastroTributarioTomador().equals(TipoCadastroTributario.PESSOA)) {
                if (filtro.getTomadores() != null && filtro.getTomadores().size() > 0) {
                    String juncao = "";
                    String ids = "";
                    String contribuintes = "";
                    for (Pessoa pessoa : filtro.getTomadores()) {
                        ids += juncao + pessoa.getId();
                        contribuintes += juncao + pessoa;
                        juncao = ", ";
                    }
                    filtro.addFiltroIn("vw.id_tomador", "Tomadores", ids, contribuintes);
                }
            }
        }

        if (filtro.getTipoCadastroTributarioPrestador() != null) {
            if (filtro.getTipoCadastroTributarioPrestador().equals(TipoCadastroTributario.ECONOMICO)) {
                filtro.addFiltro("vw.cmc_prestador", ">=", "CMC Prestador Inicial", filtro.getCmcInicialTomador());
                filtro.addFiltro("vw.cmc_prestador", "<=", "CMC Prestador Final", filtro.getCmcFinalPrestador());
            } else if (filtro.getTipoCadastroTributarioPrestador().equals(TipoCadastroTributario.PESSOA)) {
                if (filtro.getPrestadores() != null && filtro.getPrestadores().size() > 0) {
                    String juncao = "";
                    String ids = "";
                    String contribuintes = "";
                    for (Pessoa pessoa : filtro.getPrestadores()) {
                        ids += juncao + pessoa.getId();
                        contribuintes += juncao + pessoa;
                        juncao = ", ";
                    }
                    filtro.addFiltroIn("vw.id_prestador", "Prestadores", ids, contribuintes);
                }
            }
        }

        filtro.addFiltro("vw.nota_situacao", "=", "Situação da Nota", filtro.getSituacaoNota());

        if (filtro.getUsuarioNota() != null) {
            filtro.addFiltro("vw.nota_id_usuario", "=", "Usuário da Nota", filtro.getUsuarioNota().getId(), filtro.getUsuarioNota().getLogin());
        }

        filtro.addFiltro(" vw.nota_total_servicos ", ">=", "Total Nota Inicial", filtro.getTotalNotaInicial());
        filtro.addFiltro(" vw.nota_total_servicos ", "<=", "Total Nota Final", filtro.getTotalNotaFinal());

        filtro.addFiltro(" vw.nota_total_iss ", ">=", "Total Iss Inicial", filtro.getTotalIssInicial());
        filtro.addFiltro(" vw.nota_total_iss ", "<=", "Total Iss Final", filtro.getTotalIssFinal());

        return filtro;
    }
}
