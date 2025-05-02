package br.com.webpublico.negocios.contabil.execucao;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.ConciliacaoArquivo;
import br.com.webpublico.entidades.contabil.ConciliacaoArquivoMovimentoArquivo;
import br.com.webpublico.entidades.contabil.ConciliacaoArquivoMovimentoSistema;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.FiltroPendenciaConciliacao;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoConciliacaoBancaria;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.enums.contabil.TipoFiltroConciliacaoArquivo;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.FechamentoMensalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.primefaces.model.UploadedFile;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ConciliacaoArquivoFacade extends AbstractFacade<ConciliacaoArquivo> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LancamentoConciliacaoBancariaFacade lancamentoConciliacaoBancariaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConciliacaoBancariaFacade conciliacaoBancariaFacade;
    @EJB
    private FechamentoMensalFacade fechamentoMensalFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private TipoConciliacaoFacade tipoConciliacaoFacade;

    public ConciliacaoArquivoFacade() {
        super(ConciliacaoArquivo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ConciliacaoArquivo recuperar(Object id) {
        if (id != null) {
            ConciliacaoArquivo ca = em.find(ConciliacaoArquivo.class, id);
            Hibernate.initialize(ca.getMovimentos());
            for (ConciliacaoArquivoMovimentoArquivo movimento : ca.getMovimentos()) {
                Hibernate.initialize(movimento.getMovimentosSistemas());
            }
            return ca;
        }
        return null;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void salvarNovo(ConciliacaoArquivo entity) {
        entity.setDataCadastro(new Date());
        super.salvarNovo(entity);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipoData(parte, sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(ConciliacaoArquivo selecionado, UnidadeOrganizacional uo) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
            selecionado.getDataCadastro() != null ? selecionado.getDataCadastro() : sistemaFacade.getDataOperacao(), uo, TipoHierarquiaOrganizacional.ORCAMENTARIA
        );
    }

    public List<SubConta> buscarContasFinanceiras(String parte) {
        return subContaFacade.listaPorExercicio(parte, sistemaFacade.getExercicioCorrente());
    }

    public List<TipoConciliacao> buscarTiposConciliacoes(String parte) {
        return tipoConciliacaoFacade.listaFiltrando(parte.trim(), "numero", "descricao");
    }

    public void validarMesContabil(Date dataSaldo) {
        fechamentoMensalFacade.validarMesContabil(dataSaldo);
    }

    public Arquivo criarArquivo(UploadedFile file) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setNome(file.getFileName());
        arquivo.setDescricao(file.getFileName());
        arquivo.setTamanho(file.getSize());
        arquivo.setInputStream(file.getInputstream());
        arquivo.setMimeType(arquivoFacade.getMimeType(file.getInputstream()));
        return arquivoFacade.novoArquivoMemoria(arquivo);
    }

    public ContaBancariaEntidade buscarContaBancaria(String conta, String digitoVerificador) {
        List<ContaBancariaEntidade> contaBancariaEntidades = contaBancariaEntidadeFacade.buscarContaBancaria(conta, digitoVerificador);
        if (contaBancariaEntidades != null && !contaBancariaEntidades.isEmpty()) {
            return contaBancariaEntidades.get(0);
        } else {
            FacesUtil.addWarn("Atenção", "Não foi encontrado nenhuma conta bancária com o número " + conta);
            return null;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void buscarMovimentos(ConciliacaoArquivo selecionado, List<String> tiposFiltrosSelecionados) {
        FiltroPendenciaConciliacao filtroPendencia = new FiltroPendenciaConciliacao();
        filtroPendencia.setConciliado(null);
        for (ConciliacaoArquivoMovimentoArquivo movimento : selecionado.getMovimentos()) {
            montarFiltros(filtroPendencia, movimento, tiposFiltrosSelecionados);
        }

        List<MovimentoConciliacaoBancaria> movimentosEncontradosNoSistema = buscarMovimentosPorContaBancariaConciliacaoSemIdentificador(filtroPendencia);
        for (ConciliacaoArquivoMovimentoArquivo movimento : selecionado.getMovimentos()) {
            for (MovimentoConciliacaoBancaria movimentoConciliacaoBancaria : movimentosEncontradosNoSistema) {
                if (isMovimentoCorrespondente(tiposFiltrosSelecionados, movimento, movimentoConciliacaoBancaria)) {
                    ConciliacaoArquivoMovimentoSistema movimentoSistema = new ConciliacaoArquivoMovimentoSistema(movimento, movimentoConciliacaoBancaria);
                    movimento.getMovimentosSistemas().add(movimentoSistema);
                }
            }
        }

        selecionado.getMovimentos().forEach(mov -> mov.getMovimentosSistemas()
            .forEach(movSistema -> movSistema.setCorrespondente(mov.getMovimentosSistemas().size() == 1)));
    }

    private boolean isMovimentoCorrespondente(List<String> tiposFiltrosSelecionados, ConciliacaoArquivoMovimentoArquivo movimentoArquivo, MovimentoConciliacaoBancaria movimentoSistema) {
        if (tiposFiltrosSelecionados.contains(TipoFiltroConciliacaoArquivo.DATA.name()) &&
            movimentoArquivo.getDataMovimento().after(movimentoSistema.getDataMovimento())) {
            return false;
        }
        if (tiposFiltrosSelecionados.contains(TipoFiltroConciliacaoArquivo.VALOR.name()) &&
            movimentoArquivo.getValor().compareTo(movimentoSistema.getCredito()) != 0) {
            return false;
        }
        if (tiposFiltrosSelecionados.contains(TipoFiltroConciliacaoArquivo.NUMERO_DOCUMENTO.name()) &&
            !movimentoArquivo.getNumeroDocumento().equals(movimentoSistema.getNumero()) &&
            !movimentoArquivo.getNumeroDocumento().equals(movimentoSistema.getNumeroOBN600())) {
            return false;
        }
        return true;
    }

    private void montarFiltros(FiltroPendenciaConciliacao filtroPendencia, ConciliacaoArquivoMovimentoArquivo movimento, List<String> tiposFiltrosSelecionados) {
        if (tiposFiltrosSelecionados.contains(TipoFiltroConciliacaoArquivo.VALOR.name())) {
            filtroPendencia.getValores().add(movimento.getValor());
        }
        if (tiposFiltrosSelecionados.contains(TipoFiltroConciliacaoArquivo.NUMERO_DOCUMENTO.name())) {
            filtroPendencia.getNumeros().add(movimento.getNumeroDocumento());
        }
        filtroPendencia.setContaBancariaEntidade(movimento.getConciliacaoArquivo().getContaBancariaEntidade());
        filtroPendencia.setVisualizarPendenciasBaixadas(Boolean.FALSE);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<BarraProgressoItens> conciliar(BarraProgressoItens barraProgresso, ConciliacaoArquivo selecionado) {
        barraProgresso.inicializa();
        barraProgresso.setTotal(selecionado.getMovimentos().size());
        try {
            conciliarMovimentos(selecionado, barraProgresso);
            salvar(selecionado);
            barraProgresso.finaliza();
        } catch (Exception ex) {
            barraProgresso.finaliza();
            throw ex;
        }
        return new AsyncResult<>(barraProgresso);
    }

    public void conciliarMovimentos(ConciliacaoArquivo selecionado, BarraProgressoItens barraProgresso) {
        List<ConciliacaoArquivoMovimentoArquivo> movimentosParaGerarPendencias = Lists.newArrayList();
        for (ConciliacaoArquivoMovimentoArquivo movimentoArquivo : selecionado.getMovimentos()) {
            barraProgresso.setMensagens("Conciliando movimentos correspondentes selecionados....");
            if (!movimentoArquivo.getMovimentosSistemas().isEmpty()) {
                for (ConciliacaoArquivoMovimentoSistema movimentoSistema : movimentoArquivo.getMovimentosSistemas()) {
                    if (movimentoSistema.getDataConciliacao() == null && movimentoSistema.getCorrespondente()) {
                        lancamentoConciliacaoBancariaFacade.conciliarMovimento(new MovimentoConciliacaoBancaria(movimentoSistema), selecionado.getDataConciliacao());
                        barraProgresso.setProcessados(barraProgresso.getProcessados() + 1);
                    }
                }
            } else if (movimentoArquivo.getUnidadeOrganizacional() != null &&
                movimentoArquivo.getSubConta() != null &&
                movimentoArquivo.getTipoOperacaoConcilicaoBancaria() != null &&
                movimentoArquivo.getTipoConciliacao() != null) {
                movimentosParaGerarPendencias.add(movimentoArquivo);
            }
        }
        salvarPendencias(movimentosParaGerarPendencias, barraProgresso);
    }

    private void salvarPendencias(List<ConciliacaoArquivoMovimentoArquivo> movimentosParaGerarPendencias, BarraProgressoItens barraProgresso) {
        barraProgresso.setMensagens("Gerando pendências dos movimentos sem correspondentes....");
        for (ConciliacaoArquivoMovimentoArquivo movimentoPendencia : movimentosParaGerarPendencias) {
            LancamentoConciliacaoBancaria pendencia = new LancamentoConciliacaoBancaria();
            pendencia.setValor(movimentoPendencia.getValor());
            pendencia.setData(movimentoPendencia.getConciliacaoArquivo().getDataConciliacao());
            pendencia.setHistorico(movimentoPendencia.getHistorico());
            pendencia.setUnidadeOrganizacional(movimentoPendencia.getUnidadeOrganizacional());
            pendencia.setSubConta(movimentoPendencia.getSubConta());
            pendencia.setTipoOperacaoConciliacao(movimentoPendencia.getTipoOperacaoConcilicaoBancaria());
            pendencia.setTipoConciliacao(movimentoPendencia.getTipoConciliacao());
            lancamentoConciliacaoBancariaFacade.salvarNovo(pendencia);
            barraProgresso.setProcessados(barraProgresso.getProcessados() + 1);
        }
    }

    public List<MovimentoConciliacaoBancaria> buscarMovimentosPorContaBancariaConciliacaoSemIdentificador(FiltroPendenciaConciliacao filtro) {
        String sql = " select vwmov.data_movimento, " +
            "       vwmov.data_conciliacao, " +
            "       vwmov.historico, " +
            "       vwmov.valor, " +
            "       vwmov.numero_movimento, " +
            "       vwmov.tipo_movimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subconta, " +
            "       vwmov.movimento_id, " +
            "       vwmov.situacao, " +
            "       coalesce(vwmov.tipooperacaoconciliacao, eve.tipooperacaoconciliacao) as tipooperacaoconciliacao," +
            "       vwmov.numeroobn600 " +
            " from (select distinct cast(rr.datalancamento as date)  as data_movimento, " +
            "                      cast(rr.dataconciliacao as date) as data_conciliacao, " +
            "                      rr.complemento                   as historico, " +
            "                      coalesce(fonte.valor, 0)         as valor, " +
            "                      rr.numero                        as numero_movimento, " +
            "                      'RECEITA_REALIZADA'              as tipo_movimento, " +
            "                      rr.subconta_id                   as subconta_id, " +
            "                      rr.id                            as movimento_id, " +
            "                      rr.identificador_id, " +
            "                      case when rr.saldo > 0 then 'ABERTO' else 'PAGO' end as situacao, " +
            "                      rr.eventocontabil_id             as eventocontabil_id, " +
            "                      null                             as tipooperacaoconciliacao, " +
            "                      null                             as numeroobn600 " +
            "      from lancamentoreceitaorc rr " +
            "               inner join lancreceitafonte fonte on rr.id = fonte.lancreceitaorc_id " +
            "      union all " +
            "      select cast(rre.dataestorno as date)     as data_movimento, " +
            "             cast(rre.dataconciliacao as date) as data_conciliacao, " +
            "             rre.complementohistorico          as historico, " +
            "             coalesce(rre.valor, 0)            as valor, " +
            "             rre.numero                        as numero_movimento, " +
            "             'ESTORNO_RECEITA_REALIZADA'       as tipo_movimento, " +
            "             rre.contafinanceira_id            as subconta_id, " +
            "             rre.id                            as movimento_id, " +
            "             rre.identificador_id, " +
            "             'NAO_APLICAVEL'                   as situacao, " +
            "             rre.eventocontabil_id             as eventocontabil_id, " +
            "             null                              as tipooperacaoconciliacao, " +
            "             null                              as numeroobn600 " +
            "      from receitaorcestorno rre " +
            "               inner join receitaorcfonteestorno rfe on rre.id = rfe.receitaorcestorno_id " +
            "      union all " +
            "      select cast(pag.datapagamento as date)   as data_movimento, " +
            "             cast(pag.dataconciliacao as date) as data_conciliacao, " +
            "             pag.historicorazao                as historico, " +
            "             coalesce(pag.valorfinal, 0)       as valor, " +
            "             pag.numeropagamento               as numero_movimento, " +
            "             'PAGAMENTO'                       as tipo_movimento, " +
            "             pag.subconta_id                   as subconta_id, " +
            "             pag.id                            as movimento_id, " +
            "             pag.identificador_id, " +
            "             case when pag.status = 'PAGO' then 'PAGO' else 'ABERTO' end as situacao, " +
            "             pag.eventocontabil_id             as eventocontabil_id, " +
            "             null                              as tipooperacaoconciliacao, " +
            "             arb.numero                        as numeroobn600 " +
            "      from pagamento pag " +
            "               left join borderopagamento bp on bp.pagamento_id = pag.id and bp.situacaoitembordero <> 'INDEFIRIDO' " +
            "               left join arquivorembancobordero arbb on arbb.bordero_id = bp.bordero_id " +
            "               left join arquivoremessabanco arb on arb.id = arbb.arquivoremessabanco_id " +
            "      where pag.categoriaorcamentaria = 'NORMAL' " +
            "        and pag.status <> 'ABERTO' " +
            "      union all " +
            "      select cast(pagest.dataestorno as date)     as data_movimento, " +
            "             cast(pagest.dataconciliacao as date) as data_conciliacao, " +
            "             pagest.historicorazao                as historico, " +
            "             coalesce(pagest.valorfinal, 0)       as valor, " +
            "             pagest.numero                        as numero_movimento, " +
            "             'ESTORNO_PAGAMENTO'                  as tipo_movimento, " +
            "             pag.subconta_id                      as subconta_id, " +
            "             pagest.id                            as movimento_id, " +
            "             pagest.identificador_id, " +
            "             'NAO_APLICAVEL'                      as situacao, " +
            "             pagest.eventocontabil_id             as eventocontabil_id, " +
            "             null                                 as tipooperacaoconciliacao, " +
            "             null                                 as numeroobn600 " +
            "      from pagamentoestorno pagest " +
            "               inner join pagamento pag on pagest.pagamento_id = pag.id " +
            "               inner join liquidacao liq on pag.liquidacao_id = liq.id " +
            "               inner join empenho emp on liq.empenho_id = emp.id " +
            "               inner join fontedespesaorc fdo on emp.fontedespesaorc_id = fdo.id " +
            "               inner join provisaoppafonte provisao on fdo.provisaoppafonte_id = provisao.id " +
            "               inner join contadedestinacao cdt on provisao.destinacaoderecursos_id = cdt.id " +
            "      where pag.categoriaorcamentaria = 'NORMAL' " +
            "        and pag.status <> 'ABERTO' " +
            "      union all " +
            "      select cast(pag.datapagamento as date)   as data_movimento, " +
            "             cast(pag.dataconciliacao as date) as data_conciliacao, " +
            "             pag.historicorazao                as historico, " +
            "             coalesce(pag.valorfinal, 0)       as valor, " +
            "             pag.numeropagamento               as numero_movimento, " +
            "             'PAGAMENTO_RESTO'                 as tipo_movimento, " +
            "             pag.subconta_id                   as subconta_id, " +
            "             pag.id                            as movimento_id, " +
            "             pag.identificador_id, " +
            "             case when pag.status = 'PAGO' then 'PAGO' else 'ABERTO' end as situacao, " +
            "             pag.eventocontabil_id             as eventocontabil_id, " +
            "             null                              as tipooperacaoconciliacao, " +
            "             arb.numero                        as numeroobn600 " +
            "      from pagamento pag " +
            "               inner join liquidacao liq on pag.liquidacao_id = liq.id " +
            "               inner join empenho emp on liq.empenho_id = emp.id " +
            "               inner join fontedespesaorc fdo on emp.fontedespesaorc_id = fdo.id " +
            "               inner join provisaoppafonte provisao on fdo.provisaoppafonte_id = provisao.id " +
            "               inner join contadedestinacao cdt on provisao.destinacaoderecursos_id = cdt.id " +
            "               left join borderopagamento bp on bp.pagamento_id = pag.id and bp.situacaoitembordero <> 'INDEFIRIDO'" +
            "               left join arquivorembancobordero arbb on arbb.bordero_id = bp.bordero_id " +
            "               left join arquivoremessabanco arb on arb.id = arbb.arquivoremessabanco_id " +
            "      where pag.categoriaorcamentaria = 'RESTO' " +
            "        and pag.status <> 'ABERTO' " +
            "      union all " +
            "      select cast(pagest.dataestorno as date)     as data_movimento, " +
            "             cast(pagest.dataconciliacao as date) as data_conciliacao, " +
            "             pagest.historicorazao                as historico, " +
            "             coalesce(pagest.valorfinal, 0)       as valor, " +
            "             pagest.numero                        as numero_movimento, " +
            "             'ESTORNO_PAGAMENTO_RESTO'            as tipo_movimento, " +
            "             pag.subconta_id                      as subconta_id, " +
            "             pagest.id                            as movimento_id, " +
            "             pagest.identificador_id, " +
            "             'NAO_APLICAVEL'                      as situacao, " +
            "             pagest.eventocontabil_id             as eventocontabil_id, " +
            "             null                                 as tipooperacaoconciliacao, " +
            "             null                                 as numeroobn600 " +
            "      from pagamentoestorno pagest " +
            "               inner join pagamento pag on pagest.pagamento_id = pag.id " +
            "               inner join liquidacao liq on pag.liquidacao_id = liq.id " +
            "               inner join empenho emp on liq.empenho_id = emp.id " +
            "               inner join fontedespesaorc fdo on emp.fontedespesaorc_id = fdo.id " +
            "               inner join provisaoppafonte provisao on fdo.provisaoppafonte_id = provisao.id " +
            "               inner join contadedestinacao cdt on provisao.destinacaoderecursos_id = cdt.id " +
            "      where pag.categoriaorcamentaria = 'RESTO' " +
            "        and pag.status <> 'ABERTO' " +
            "      union all " +
            "      select cast(transf.datatransferencia as date) as data_movimento, " +
            "             cast(transf.dataconciliacao as date)   as data_conciliacao, " +
            "             transf.historico                       as historico, " +
            "             coalesce(transf.valor, 0)              as valor, " +
            "             transf.numero                          as numero_movimento, " +
            "             'TRANSFERENCIA_FINANCEIRA_RETIRADA'    as tipo_movimento, " +
            "             transf.subcontaretirada_id             as subconta_id, " +
            "             transf.id                              as movimento_id, " +
            "             transf.identificador_id, " +
            "             case when transf.statuspagamento = 'PAGO' then 'PAGO' else 'ABERTO' end as situacao, " +
            "             transf.eventocontabilretirada_id       as eventocontabil_id, " +
            "             null                                   as tipooperacaoconciliacao, " +
            "             null                                   as numeroobn600 " +
            "      from transferenciacontafinanc transf " +
            "      union all " +
            "      select cast(est.dataestorno as date)               as data_movimento, " +
            "             cast(est.dataconciliacao as date)           as data_conciliacao, " +
            "             est.historico                               as historico, " +
            "             coalesce(est.valor, 0)                      as valor, " +
            "             est.numero                                  as numero_movimento, " +
            "             'ESTORNO_TRANSFERENCIA_FINANCEIRA_RETIRADA' as tipo_movimento, " +
            "             transf.subcontaretirada_id                  as subconta_id, " +
            "             est.id                                      as movimento_id, " +
            "             est.identificador_id, " +
            "             'NAO_APLICAVEL'                             as situacao, " +
            "             est.eventocontabilretirada_id               as eventocontabil_id, " +
            "             null                                        as tipooperacaoconciliacao, " +
            "             null                                        as numeroobn600 " +
            "      from estornotransferencia est " +
            "               inner join transferenciacontafinanc transf on est.transferencia_id = transf.id " +
            "      union all " +
            "      select cast(transf.datatransferencia as date)   as data_movimento, " +
            "             cast(transf.dataconciliacao as date)     as data_conciliacao, " +
            "             transf.historico                         as historico, " +
            "             coalesce(transf.valor, 0)                as valor, " +
            "             transf.numero                            as numero_movimento, " +
            "             'TRANSFERENCIA_FINANCEIRA_MESMA_UNIDADE' as tipo_movimento, " +
            "             transf.subcontaretirada_id               as subconta_id, " +
            "             transf.id                                as movimento_id, " +
            "             transf.identificador_id, " +
            "             case when transf.statuspagamento = 'PAGO' then 'PAGO' else 'ABERTO' end as situacao, " +
            "             transf.eventocontabilretirada_id         as eventocontabil_id, " +
            "             null                                     as tipooperacaoconciliacao, " +
            "             null                                     as numeroobn600 " +
            "      from transferenciamesmaunidade transf " +
            "      union all " +
            "      select cast(est.dataestorno as date)                    as data_movimento, " +
            "             cast(est.dataconciliacao as date)                as data_conciliacao, " +
            "             est.historico                                    as historico, " +
            "             coalesce(est.valor, 0)                           as valor, " +
            "             est.numero                                       as numero_movimento, " +
            "             'ESTORNO_TRANSFERENCIA_FINANCEIRA_MESMA_UNIDADE' as tipo_movimento, " +
            "             transf.subcontaretirada_id                       as subconta_id, " +
            "             est.id                                           as movimento_id, " +
            "             est.identificador_id, " +
            "             'NAO_APLICAVEL'                                  as situacao, " +
            "             est.eventocontabilretirada_id                    as eventocontabil_id, " +
            "             null                                             as tipooperacaoconciliacao, " +
            "             null                                             as numeroobn600 " +
            "      from estornotransfmesmaunidade est " +
            "               inner join transferenciamesmaunidade transf on est.transferenciamesmaunidade_id = transf.id " +
            "      union all " +
            "      select cast(lib.dataliberacao as date) as data_movimento, " +
            "             cast(lib.recebida as date)      as data_conciliacao, " +
            "             lib.observacoes                 as historico, " +
            "             coalesce(lib.valor, 0)          as valor, " +
            "             lib.numero                      as numero_movimento, " +
            "             'LIBERACAO_FINANCEIRA'          as tipo_movimento, " +
            "             so.contafinanceira_id           as subconta_id, " +
            "             lib.id                          as movimento_id, " +
            "             lib.identificador_id, " +
            "             case when lib.statuspagamento = 'PAGO' then 'PAGO' else 'ABERTO' end as situacao, " +
            "             lib.eventocontabildeposito_id   as eventocontabil_id, " +
            "             null                            as tipooperacaoconciliacao, " +
            "             null                            as numeroobn600 " +
            "      from liberacaocotafinanceira lib " +
            "               inner join solicitacaocotafinanceira so on so.id = lib.solicitacaocotafinanceira_id " +
            "      union all " +
            "      select cast(est.dataestorno as date)  as data_movimento, " +
            "             cast(est.recebida as date)     as data_conciliacao, " +
            "             est.historico                  as historico, " +
            "             coalesce(est.valor, 0)         as valor, " +
            "             est.numero                     as numero_movimento, " +
            "             'ESTORNO_LIBERACAO_FINANCEIRA' as tipo_movimento, " +
            "             so.contafinanceira_id          as subconta_id, " +
            "             est.id                         as movimento_id, " +
            "             est.identificador_id, " +
            "             'NAO_APLICAVEL'                as situacao, " +
            "             est.eventocontabildeposito_id  as eventocontabil_id, " +
            "             null                           as tipooperacaoconciliacao, " +
            "             null                           as numeroobn600 " +
            "      from estornolibcotafinanceira est " +
            "               inner join liberacaocotafinanceira lib on est.liberacao_id = lib.id " +
            "               inner join solicitacaocotafinanceira so on so.id = lib.solicitacaocotafinanceira_id " +
            "      union all " +
            "      select cast(pag.datapagto as date)       as data_movimento, " +
            "             cast(pag.dataconciliacao as date) as data_conciliacao, " +
            "             pag.complementohistorico          as historico, " +
            "             coalesce(pag.valor, 0)            as valor, " +
            "             pag.numero                        as numero_movimento, " +
            "             'DESPESA_EXTRA'                   as tipo_movimento, " +
            "             pag.subconta_id                   as subconta_id, " +
            "             pag.id                            as movimento_id, " +
            "             pag.identificador_id, " +
            "             case when pag.status = 'PAGO' then 'PAGO' else 'ABERTO' end as situacao, " +
            "             pag.eventocontabil_id             as eventocontabil_id, " +
            "             null                              as tipooperacaoconciliacao, " +
            "             arb.numero                        as numeroobn600 " +
            "      from pagamentoextra pag " +
            "               left join borderopagamentoextra bpe on bpe.pagamentoextra_id = pag.id and bpe.situacaoitembordero <> 'INDEFIRIDO' " +
            "               left join arquivorembancobordero arbb on arbb.bordero_id = bpe.bordero_id " +
            "               left join arquivoremessabanco arb on arb.id = arbb.arquivoremessabanco_id " +
            "      union all " +
            "      select cast(est.dataestorno as date)     as data_movimento, " +
            "             cast(est.dataconciliacao as date) as data_conciliacao, " +
            "             est.historico                     as historico, " +
            "             coalesce(est.valor, 0)            as valor, " +
            "             est.numero                        as numero_movimento, " +
            "             'ESTORNO_DESPESA_EXTRA'           as tipo_movimento, " +
            "             pag.subconta_id                   as subconta_id, " +
            "             est.id                            as movimento_id, " +
            "             est.identificador_id, " +
            "             'NAO_APLICAVEL'                   as situacao, " +
            "             est.eventocontabil_id             as eventocontabil_id, " +
            "             null                              as tipooperacaoconciliacao, " +
            "             null                              as numeroobn600 " +
            "      from pagamentoextraestorno est " +
            "               inner join pagamentoextra pag on est.pagamentoextra_id = pag.id " +
            "      union all " +
            "      select cast(ajuste.dataajuste as date)       as data_movimento, " +
            "             cast(ajuste.dataconciliacao as date)  as data_conciliacao, " +
            "             ajuste.historico                      as historico, " +
            "             coalesce(ajuste.valor, 0)             as valor, " +
            "             ajuste.numero                         as numero_movimento, " +
            "             'AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO' as tipo_movimento, " +
            "             ajuste.contafinanceira_id             as subconta_id, " +
            "             ajuste.id                             as movimento_id, " +
            "             ajuste.identificador_id, " +
            "             'NAO_APLICAVEL'                       as situacao, " +
            "             ajuste.eventocontabil_id              as eventocontabil_id, " +
            "             null                                  as tipooperacaoconciliacao, " +
            "             null                                  as numeroobn600 " +
            "      from ajusteativodisponivel ajuste " +
            "      where ajuste.tipoajustedisponivel in " +
            "            ('AUMENTATIVO_CONTROLE_EXTERNO', 'AUMENTATIVO_CONTROLE_INTERNO', 'AUMENTATIVO_RPPS') " +
            "        and ajuste.tipolancamento = 'NORMAL' " +
            "      union all " +
            "      select cast(ajuste.dataajuste as date)               as data_movimento, " +
            "             cast(ajuste.dataconciliacao as date)          as data_conciliacao, " +
            "             ajuste.historico                              as historico, " +
            "             coalesce(ajuste.valor, 0)                     as valor, " +
            "             ajuste.numero                                 as numero_movimento, " +
            "             'ESTORNO_AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO' as tipo_movimento, " +
            "             ajuste.contafinanceira_id                     as subconta_id, " +
            "             ajuste.id                                     as movimento_id, " +
            "             ajuste.identificador_id, " +
            "             'NAO_APLICAVEL'                               as situacao, " +
            "             ajuste.eventocontabil_id                      as eventocontabil_id, " +
            "             null                                          as tipooperacaoconciliacao, " +
            "             null                                          as numeroobn600 " +
            "      from ajusteativodisponivel ajuste " +
            "      where ajuste.tipoajustedisponivel in " +
            "            ('AUMENTATIVO_CONTROLE_EXTERNO', 'AUMENTATIVO_CONTROLE_INTERNO', 'AUMENTATIVO_RPPS') " +
            "        and ajuste.tipolancamento = 'ESTORNO' " +
            "      union all " +
            "      select cast(ajuste.dataajuste as date)      as data_movimento, " +
            "             cast(ajuste.dataconciliacao as date) as data_conciliacao, " +
            "             ajuste.historico                     as historico, " +
            "             coalesce(ajuste.valor, 0)            as valor, " +
            "             ajuste.numero                        as numero_movimento, " +
            "             'AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO' as tipo_movimento, " +
            "             ajuste.contafinanceira_id            as subconta_id, " +
            "             ajuste.id                            as movimento_id, " +
            "             ajuste.identificador_id, " +
            "             'NAO_APLICAVEL'                      as situacao, " +
            "             ajuste.eventocontabil_id             as eventocontabil_id, " +
            "             null                                 as tipooperacaoconciliacao, " +
            "             null                                 as numeroobn600 " +
            "      from ajusteativodisponivel ajuste " +
            "      where ajuste.tipoajustedisponivel in " +
            "            ('DIMINUTIVO_CONTROLE_EXTERNO', 'DIMINUTIVO_CONTROLE_INTERNO', 'DIMINUTIVO_RPPS') " +
            "        and ajuste.tipolancamento = 'NORMAL' " +
            "      union all " +
            "      select cast(ajuste.dataajuste as date)              as data_movimento, " +
            "             cast(ajuste.dataconciliacao as date)         as data_conciliacao, " +
            "             ajuste.historico                             as historico, " +
            "             coalesce(ajuste.valor, 0)                    as valor, " +
            "             ajuste.numero                                as numero_movimento, " +
            "             'ESTORNO_AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO' as tipo_movimento, " +
            "             ajuste.contafinanceira_id                    as subconta_id, " +
            "             ajuste.id                                    as movimento_id, " +
            "             ajuste.identificador_id, " +
            "             'NAO_APLICAVEL'                              as situacao, " +
            "             ajuste.eventocontabil_id                     as eventocontabil_id, " +
            "             null                                         as tipooperacaoconciliacao, " +
            "             null                                         as numeroobn600 " +
            "      from ajusteativodisponivel ajuste " +
            "      where ajuste.tipoajustedisponivel in " +
            "            ('DIMINUTIVO_CONTROLE_EXTERNO', 'DIMINUTIVO_CONTROLE_INTERNO', 'DIMINUTIVO_RPPS') " +
            "        and ajuste.tipolancamento = 'ESTORNO' " +
            "      union all " +
            "      select cast(recextra.datareceita as date)     as data_movimento, " +
            "             cast(recextra.dataconciliacao as date) as data_conciliacao, " +
            "             recextra.complementohistorico          as historico, " +
            "             coalesce(recextra.valor, 0)            as valor, " +
            "             recextra.numero                        as numero_movimento, " +
            "             'RECEITA_EXTRA'                        as tipo_movimento, " +
            "             recextra.subconta_id                   as subconta_id, " +
            "             recextra.id                            as movimento_id, " +
            "             recextra.identificador_id, " +
            "             case when recextra.SituacaoReceitaExtra = 'EFETUADO' then 'PAGO' else 'ABERTO' end as situacao, " +
            "             recextra.eventocontabil_id             as eventocontabil_id, " +
            "             null                                   as tipooperacaoconciliacao, " +
            "             null                                   as numeroobn600 " +
            "      from receitaextra recextra " +
            "               inner join contaextraorcamentaria cextra on recextra.contaextraorcamentaria_id = cextra.id " +
            "      where cextra.tipocontaextraorcamentaria <> 'DEPOSITOS_CONSIGNACOES' " +
            "      union all " +
            "      select cast(est.dataestorno as date)     as data_movimento, " +
            "             cast(est.dataconciliacao as date) as data_conciliacao, " +
            "             est.complementohistorico          as historico, " +
            "             coalesce(est.valor, 0)            as valor, " +
            "             est.numero                        as numero_movimento, " +
            "             'ESTORNO_RECEITA_EXTRA'           as tipo_movimento, " +
            "             recextra.subconta_id              as subconta_id, " +
            "             est.id                            as movimento_id, " +
            "             est.identificador_id, " +
            "             'NAO_APLICAVEL'                   as situacao, " +
            "             est.eventocontabil_id             as eventocontabil_id, " +
            "             null                              as tipooperacaoconciliacao, " +
            "             null                              as numeroobn600 " +
            "      from receitaextraestorno est " +
            "               inner join receitaextra recextra on est.receitaextra_id = recextra.id " +
            "               inner join contaextraorcamentaria cextra on recextra.contaextraorcamentaria_id = cextra.id " +
            "      where cextra.tipocontaextraorcamentaria <> 'DEPOSITOS_CONSIGNACOES' " +
            "      union all " +
            "      select cast(lanc.data as date)            as data_movimento, " +
            "             cast(lanc.dataconciliacao as date) as data_conciliacao, " +
            "             lanc.historico                     as historico, " +
            "             coalesce(lanc.valor, 0)            as valor, " +
            "             cast(lanc.numero as varchar(20))   as numero_movimento, " +
            "             'LANCCONCILIACAOBANCARIA'          as tipo_movimento, " +
            "             lanc.subconta_id                   as subconta_id, " +
            "             lanc.id                            as movimento_id, " +
            "             lanc.identificador_id, " +
            "             'NAO_APLICAVEL'                    as situacao, " +
            "             null                               as eventocontabil_id, " +
            "             lanc.tipooperacaoconciliacao       as tipooperacaoconciliacao, " +
            "             null                               as numeroobn600 " +
            "      from LANCCONCILIACAOBANCARIA lanc) vwmov " +
            "         inner join subconta sub on vwmov.subconta_id = sub.id " +
            "         left join eventocontabil eve on eve.id = vwmov.eventocontabil_id " +
            " where vwmov.identificador_id is null " +
            "   and sub.contabancariaentidade_id = :contaBanc ";
        if (filtro.getNumeros() != null && !filtro.getNumeros().isEmpty()) {
            sql += " and (vwmov.numero_movimento in :numeros or vwmov.numeroobn600 in :numeros) ";
        }
        if (filtro.getValores() != null && !filtro.getValores().isEmpty()) {
            sql += " and vwmov.valor in :valores ";
        }
        sql += " order by vwmov.data_movimento desc, to_number(vwmov.numero_movimento) desc ";

        Query consulta = em.createNativeQuery(sql);
        consulta.setParameter("contaBanc", filtro.getContaBancariaEntidade().getId());
        if (filtro.getNumeros() != null && !filtro.getNumeros().isEmpty()) {
            consulta.setParameter("numeros", filtro.getNumeros());
        }
        if (filtro.getValores() != null && !filtro.getValores().isEmpty()) {
            consulta.setParameter("valores", filtro.getValores());
        }
        List<MovimentoConciliacaoBancaria> retorno = Lists.newArrayList();
        List<Object[]> resultado = consulta.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                MovimentoConciliacaoBancaria movimentoConciliacaoBancaria = popularMovimentoConciliacaoBancaria(obj);
                Util.adicionarObjetoEmLista(retorno, movimentoConciliacaoBancaria);
            }
        }
        return retorno;
    }

    private MovimentoConciliacaoBancaria popularMovimentoConciliacaoBancaria(Object[] obj) {
        MovimentoConciliacaoBancaria movimentoConciliacaoBancaria = new MovimentoConciliacaoBancaria();
        movimentoConciliacaoBancaria.setDataMovimento((Date) obj[0]);
        movimentoConciliacaoBancaria.setDataConciliacao((Date) obj[1]);
        movimentoConciliacaoBancaria.setHistorico((String) obj[2]);
        movimentoConciliacaoBancaria.setCredito((BigDecimal) obj[3]);
        movimentoConciliacaoBancaria.setNumero((String) obj[4]);
        movimentoConciliacaoBancaria.setTipoMovimento(obj[5] != null ? MovimentoConciliacaoBancaria.TipoMovimento.valueOf((String) obj[5]) : null);
        movimentoConciliacaoBancaria.setSubConta((String) obj[6]);
        movimentoConciliacaoBancaria.setMovimentoId(((BigDecimal) obj[7]).longValue());
        movimentoConciliacaoBancaria.setSituacao(obj[8] != null ? MovimentoConciliacaoBancaria.Situacao.valueOf((String) obj[8]) : null);
        movimentoConciliacaoBancaria.setTipoOperacaoConciliacao(obj[9] != null ? TipoOperacaoConcilicaoBancaria.valueOf((String) obj[9]) : null);
        movimentoConciliacaoBancaria.setNumeroOBN600((String) obj[10]);
        return movimentoConciliacaoBancaria;
    }
}
