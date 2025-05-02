package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelatorioEfetivacaoSolicitacaoEstorno;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 25/09/14
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class EfetivacaoEstornoTransferenciaFacade extends AbstractFacade<EfetivacaoEstornoTransferencia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SolicitacaoEstornoTransferenciaFacade solicitacaoEstornoTransferenciaFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    public EfetivacaoEstornoTransferenciaFacade() {
        super(EfetivacaoEstornoTransferencia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EfetivacaoEstornoTransferencia recuperar(Object id) {
        EfetivacaoEstornoTransferencia efetivacao = em.find(EfetivacaoEstornoTransferencia.class, id);
        efetivacao.getListaItemEfetivacaoEstornoTransferencia().size();
        if (efetivacao.getDetentorArquivoComposicao() != null) {
            efetivacao.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return efetivacao;
    }

    public EfetivacaoEstornoTransferencia estornar(EfetivacaoEstornoTransferencia selecionado, Boolean situacao) throws ExcecaoNegocioGenerica {
        List<ItemEstornoTransferencia> lista = solicitacaoEstornoTransferenciaFacade.recuperarItensSolicitacaoEstorno(selecionado.getSolicitacaoEstorno());
        ConfigMovimentacaoBem configuracao = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(sistemaFacade.getDataOperacao(), OperacaoMovimentacaoBem.EFETIVACAO_TRANSFERENCIA_BEM_ESTORNO);
        EfetivacaoEstornoTransferencia salvo = null;
        if (configuracao != null) {
            if (situacao) {
                salvo = estornoAceito(lista, selecionado, configuracao);
            } else {
                salvo = estornoRecusado(lista, selecionado, configuracao);
            }
        }
        List<Number> idsBem = Lists.newArrayList();
        for (ItemEstornoTransferencia item : lista) {
            idsBem.add(item.getBem().getId());
        }
        configMovimentacaoBemFacade.desbloquearBens(configuracao, idsBem);
        return salvo;
    }


    public EfetivacaoEstornoTransferencia estornoAceito(List<ItemEstornoTransferencia> lista, EfetivacaoEstornoTransferencia selecionado, ConfigMovimentacaoBem configuracao) {
        List<EventoBem> eventosIntegrador = new ArrayList<>();
        List<EstornotransferenciaBemConcedida> transferenciaConcedidas = new ArrayList<>();
        selecionado.getListaItemEfetivacaoEstornoTransferencia().clear();
        List<EstornoTransferenciaDepreciacaoConcedida> depreciacoesConcedidas = new ArrayList<>();
        List<EstornoTransferenciaDepreciacaoRecebida> depreciacoesRecebidas = new ArrayList<>();
        List<EstornoTransferenciaAmortizacaoConcedida> amortizacoesConcedidas = new ArrayList<>();
        List<EstornoTransferenciaAmortizacaoRecebida> amortizacoesRecebidas = new ArrayList<>();
        List<EstornoTransferenciaExaustaoConcedida> exaustoesConcedidas = new ArrayList<>();
        List<EstornoTransferenciaExaustaoRecebida> exaustoesRecebidas = new ArrayList<>();
        List<EstornoTransferenciaReducaoConcedida> reducoesConcedidas = new ArrayList<>();
        List<EstornoTransferenciaReducaoRecebida> reducoesRecebidas = new ArrayList<>();

        for (ItemEstornoTransferencia item : lista) {

            EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(item.getBem());
            EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
            novoEstado.setDetentoraAdministrativa(item.getEfetivacaoTransferencia().getEstadoInicial().getDetentoraAdministrativa());
            novoEstado.setDetentoraOrcamentaria(item.getEfetivacaoTransferencia().getEstadoInicial().getDetentoraOrcamentaria());
            novoEstado.setValorAcumuladoDaDepreciacao(BigDecimal.ZERO);
            novoEstado.setValorAcumuladoDaAmortizacao(BigDecimal.ZERO);
            novoEstado.setValorAcumuladoDeAjuste(BigDecimal.ZERO);
            novoEstado.setValorAcumuladoDaExaustao(BigDecimal.ZERO);
            novoEstado = em.merge(novoEstado);

            ItemEfetivacaoEstornoTransferencia estorno = criarItemEstornoTransferencia(selecionado, item, ultimoEstado, configuracao);
            EstadoBem estadoBemRecebido = estorno.getEstadoResultante();
            EstornotransferenciaBemConcedida estornotransferenciaBemConcedida = criarEstornoTransferenciaBemConcedida(ultimoEstado, novoEstado, item.getBem(), configuracao);
            transferenciaConcedidas.add(estornotransferenciaBemConcedida);
            EventoBem ultimoEvento = estornotransferenciaBemConcedida;

            if (ultimoEstado.getValorAcumuladoDaDepreciacao().compareTo(BigDecimal.ZERO) > 0) {
                EstornoTransferenciaDepreciacaoRecebida estornoTransferenciaDepreciacaoRecebida = criarEstornoTransferenciaDepreciacaoRecebida(estadoBemRecebido, item.getBem(), configuracao);
                depreciacoesRecebidas.add(estornoTransferenciaDepreciacaoRecebida);
                estadoBemRecebido = estornoTransferenciaDepreciacaoRecebida.getEstadoResultante();

                EstornoTransferenciaDepreciacaoConcedida estornoTransferenciaDepreciacaoConcedida = criarEstornoTransferenciaDepreciacaoConcedida(ultimoEstado, novoEstado, item.getBem(), configuracao);
                depreciacoesConcedidas.add(estornoTransferenciaDepreciacaoConcedida);
                novoEstado = estornoTransferenciaDepreciacaoConcedida.getEstadoResultante();
                ultimoEvento = estornoTransferenciaDepreciacaoConcedida;
            }
            if (ultimoEstado.getValorAcumuladoDaAmortizacao().compareTo(BigDecimal.ZERO) > 0) {
                EstornoTransferenciaAmortizacaoRecebida estornoTransferenciaAmortizacaoRecebida = criarEstornoTransferenciaAmortizacaoRecebida(estadoBemRecebido, item.getBem(), configuracao);
                amortizacoesRecebidas.add(estornoTransferenciaAmortizacaoRecebida);
                estadoBemRecebido = estornoTransferenciaAmortizacaoRecebida.getEstadoResultante();

                EstornoTransferenciaAmortizacaoConcedida estornoTransferenciaAmortizacaoConcedida = criarEstornoTransferenciaAmortizacaoConcedida(ultimoEstado, novoEstado, item.getBem(), configuracao);
                amortizacoesConcedidas.add(estornoTransferenciaAmortizacaoConcedida);
                novoEstado = estornoTransferenciaAmortizacaoConcedida.getEstadoResultante();
                ultimoEvento = estornoTransferenciaAmortizacaoConcedida;
            }
            if (ultimoEstado.getValorAcumuladoDaExaustao().compareTo(BigDecimal.ZERO) > 0) {
                EstornoTransferenciaExaustaoRecebida estornoTransferenciaExaustaoRecebida = criarEstornoTransferenciaExaustaoRecebida(estadoBemRecebido, item.getBem(), configuracao);
                exaustoesRecebidas.add(estornoTransferenciaExaustaoRecebida);
                estadoBemRecebido = estornoTransferenciaExaustaoRecebida.getEstadoResultante();

                EstornoTransferenciaExaustaoConcedida estornoTransferenciaExaustaoConcedida = criarEstornoTransferenciaExaustaoConcedida(ultimoEstado, novoEstado, item.getBem(), configuracao);
                exaustoesConcedidas.add(estornoTransferenciaExaustaoConcedida);
                novoEstado = estornoTransferenciaExaustaoConcedida.getEstadoResultante();
                ultimoEvento = estornoTransferenciaExaustaoConcedida;
            }
            if (ultimoEstado.getValorAcumuladoDeAjuste().compareTo(BigDecimal.ZERO) > 0) {
                reducoesRecebidas.add(criarEstornoTransferenciaReducaoRecebida(estadoBemRecebido, item.getBem(), configuracao));
                EstornoTransferenciaReducaoConcedida estornoTransferenciaReducaoConcedida = criarEstornoTransferenciaReducaoConcedida(ultimoEstado, novoEstado, item.getBem(), configuracao);
                reducoesConcedidas.add(estornoTransferenciaReducaoConcedida);
                ultimoEvento = estornoTransferenciaReducaoConcedida;
            }
            item.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
            em.merge(item);

            selecionado.getListaItemEfetivacaoEstornoTransferencia().add(estorno);
            eventosIntegrador.add(ultimoEvento);
        }

        for (EstornotransferenciaBemConcedida transferenciaConcedida : transferenciaConcedidas) {
            em.merge(transferenciaConcedida);
        }
        definirSolicitacaoEstornoComoFinalizada(selecionado);
        selecionado = em.merge(selecionado);

        for (EstornoTransferenciaDepreciacaoRecebida depreciacoesRecebida : depreciacoesRecebidas) {
            em.merge(depreciacoesRecebida);
        }
        for (EstornoTransferenciaDepreciacaoConcedida depreciacoesConcedida : depreciacoesConcedidas) {
            em.merge(depreciacoesConcedida);
        }
        for (EstornoTransferenciaAmortizacaoRecebida amortizacoesRecebida : amortizacoesRecebidas) {
            em.merge(amortizacoesRecebida);
        }
        for (EstornoTransferenciaAmortizacaoConcedida amortizacoesConcedida : amortizacoesConcedidas) {
            em.merge(amortizacoesConcedida);
        }
        for (EstornoTransferenciaExaustaoRecebida exaustoesRecebida : exaustoesRecebidas) {
            em.merge(exaustoesRecebida);
        }
        for (EstornoTransferenciaExaustaoConcedida exaustoesConcedida : exaustoesConcedidas) {
            em.merge(exaustoesConcedida);
        }
        for (EstornoTransferenciaReducaoRecebida reducoesRecebida : reducoesRecebidas) {
            em.merge(reducoesRecebida);
        }
        for (EstornoTransferenciaReducaoConcedida reducoesConcedida : reducoesConcedidas) {
            em.merge(reducoesConcedida);
        }
        contabilizarOperacao(selecionado, eventosIntegrador);
        return selecionado;
    }

    private void definirSolicitacaoEstornoComoFinalizada(EfetivacaoEstornoTransferencia selecionado) {
        selecionado.getSolicitacaoEstorno().setSituacao(SituacaoEventoBem.FINALIZADO);
        em.merge(selecionado.getSolicitacaoEstorno());
    }

    private EstornotransferenciaBemConcedida criarEstornoTransferenciaBemConcedida(EstadoBem estadoBemInicial, EstadoBem estadoBemResultante, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstornotransferenciaBemConcedida estornoConcedido = new EstornotransferenciaBemConcedida(estadoBemInicial, estadoBemResultante, bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoConcedido.setDataLancamento(dataLancamento);
        return estornoConcedido;
    }

    private EstornoTransferenciaDepreciacaoConcedida criarEstornoTransferenciaDepreciacaoConcedida(EstadoBem estadoBemInicial, EstadoBem estadoBemResultante, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoBemResultante);
        novoEstado.setValorAcumuladoDaDepreciacao(estadoBemInicial.getValorAcumuladoDaDepreciacao());
        EstornoTransferenciaDepreciacaoConcedida estornoDepreciacaoConcedida = new EstornoTransferenciaDepreciacaoConcedida(estadoBemInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoDepreciacaoConcedida.setDataLancamento(dataLancamento);
        return estornoDepreciacaoConcedida;
    }

    private EstornoTransferenciaDepreciacaoRecebida criarEstornoTransferenciaDepreciacaoRecebida(EstadoBem estadoInicial, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial);
        novoEstado.setValorAcumuladoDaDepreciacao(BigDecimal.ZERO);
        EstornoTransferenciaDepreciacaoRecebida estornoDepreciacaoRecebida = new EstornoTransferenciaDepreciacaoRecebida(estadoInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoDepreciacaoRecebida.setDataLancamento(dataLancamento);
        return estornoDepreciacaoRecebida;
    }

    private EstornoTransferenciaAmortizacaoConcedida criarEstornoTransferenciaAmortizacaoConcedida(EstadoBem estadoInicial, EstadoBem estadoResultante, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoResultante);
        novoEstado.setValorAcumuladoDaAmortizacao(estadoInicial.getValorAcumuladoDaAmortizacao());
        EstornoTransferenciaAmortizacaoConcedida estornoAmortizacaoConcedido = new EstornoTransferenciaAmortizacaoConcedida(estadoInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoAmortizacaoConcedido.setDataLancamento(dataLancamento);
        return estornoAmortizacaoConcedido;
    }

    private EstornoTransferenciaAmortizacaoRecebida criarEstornoTransferenciaAmortizacaoRecebida(EstadoBem estadoInicial, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial);
        novoEstado.setValorAcumuladoDaAmortizacao(BigDecimal.ZERO);
        EstornoTransferenciaAmortizacaoRecebida estornoAmortizacaoRecebido = new EstornoTransferenciaAmortizacaoRecebida(estadoInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoAmortizacaoRecebido.setDataLancamento(dataLancamento);
        return estornoAmortizacaoRecebido;
    }

    private EstornoTransferenciaExaustaoConcedida criarEstornoTransferenciaExaustaoConcedida(EstadoBem estadoInicial, EstadoBem estadoResultante, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoResultante);
        novoEstado.setValorAcumuladoDaExaustao(estadoInicial.getValorAcumuladoDaExaustao());
        EstornoTransferenciaExaustaoConcedida estornoExaustaoConcedido = new EstornoTransferenciaExaustaoConcedida(estadoInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoExaustaoConcedido.setDataLancamento(dataLancamento);
        return estornoExaustaoConcedido;
    }

    private EstornoTransferenciaExaustaoRecebida criarEstornoTransferenciaExaustaoRecebida(EstadoBem estadoInicial, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial);
        novoEstado.setValorAcumuladoDaExaustao(BigDecimal.ZERO);
        EstornoTransferenciaExaustaoRecebida estornoExaustaoRecebido = new EstornoTransferenciaExaustaoRecebida(estadoInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoExaustaoRecebido.setDataLancamento(dataLancamento);
        return estornoExaustaoRecebido;
    }

    private EstornoTransferenciaReducaoConcedida criarEstornoTransferenciaReducaoConcedida(EstadoBem estadoInicial, EstadoBem estadoResultante, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoResultante);
        novoEstado.setValorAcumuladoDeAjuste(estadoInicial.getValorAcumuladoDeAjuste());
        EstornoTransferenciaReducaoConcedida estornoReducaoConcedido = new EstornoTransferenciaReducaoConcedida(estadoInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoReducaoConcedido.setDataLancamento(dataLancamento);
        return estornoReducaoConcedido;
    }

    private EstornoTransferenciaReducaoRecebida criarEstornoTransferenciaReducaoRecebida(EstadoBem estadoInicial, Bem bem, ConfigMovimentacaoBem configuracao) {
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial);
        novoEstado.setValorAcumuladoDeAjuste(BigDecimal.ZERO);
        EstornoTransferenciaReducaoRecebida estornoReducaoRecebido = new EstornoTransferenciaReducaoRecebida(estadoInicial, em.merge(novoEstado), bem);
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estornoReducaoRecebido.setDataLancamento(dataLancamento);
        return estornoReducaoRecebido;
    }

    private ItemEfetivacaoEstornoTransferencia criarItemEstornoTransferencia(EfetivacaoEstornoTransferencia efetivacao, ItemEstornoTransferencia itemSolicitacao, EstadoBem ultimoEstado, ConfigMovimentacaoBem configuracao) {
        ItemEfetivacaoEstornoTransferencia estorno = new ItemEfetivacaoEstornoTransferencia();
        Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
        estorno.setDataLancamento(dataLancamento);
        estorno.setEfetivacaoEstorno(efetivacao);
        estorno.setItemEstornoTransferencia(itemSolicitacao);
        estorno.setBem(itemSolicitacao.getBem());
        estorno.setEstadoInicial(ultimoEstado);
        EstadoBem novoEstado = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        novoEstado.setValorOriginal(BigDecimal.ZERO);
        estorno.setEstadoResultante(em.merge(novoEstado));
        estorno.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        estorno.setValorDoLancamento(ultimoEstado.getValorOriginal());
        return estorno;
    }

    public EfetivacaoEstornoTransferencia estornoRecusado(List<ItemEstornoTransferencia> lista, EfetivacaoEstornoTransferencia selecionado, ConfigMovimentacaoBem configuracao) {
        for (ItemEstornoTransferencia itemSolicitacaoEstorno : lista) {

            ItemEfetivacaoEstornoTransferencia estorno = new ItemEfetivacaoEstornoTransferencia();
            estorno.setEfetivacaoEstorno(selecionado);
            Date dataLancamento = DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao());
            estorno.setDataLancamento(dataLancamento);
            estorno.setItemEstornoTransferencia(itemSolicitacaoEstorno);
            estorno.setBem(itemSolicitacaoEstorno.getBem());
            EstadoBem ultimoEstadoBem = bemFacade.recuperarUltimoEstadoDoBem(itemSolicitacaoEstorno.getBem());
            estorno.setEstadoInicial(ultimoEstadoBem);
            estorno.setEstadoResultante(ultimoEstadoBem);
            estorno.setSituacaoEventoBem(SituacaoEventoBem.RECUSADO);

            selecionado.getListaItemEfetivacaoEstornoTransferencia().add(estorno);
            itemSolicitacaoEstorno.setSituacaoEventoBem(SituacaoEventoBem.RECUSADO);
            em.merge(itemSolicitacaoEstorno);
        }
        selecionado.getSolicitacaoEstorno().setSituacao(SituacaoEventoBem.RECUSADO);
        em.merge(selecionado.getSolicitacaoEstorno());
        return em.merge(selecionado);
    }

    public List<RelatorioEfetivacaoSolicitacaoEstorno> buscarRegistrosTermoEfetivacaoSolicitacaoEstorno(EfetivacaoEstornoTransferencia efetivacao) {

        String sql = "SELECT " +
            "  LT.CODIGO AS CODIGO_LOTE, " +
            "  solicitacaoEstorno.DATADECRIACAO, " +
            "  PF_RESPONSAVEL_ORIGEM.NOME  AS RESPONSAVEL_ORIGEM, " +
            "  PF_RESPONSAVEL_DESTINO.NOME AS RESPONSAVEL_DESTINO, " +
            "  origem.DESCRICAO AS ORIGEM, " +
            "  destino.DESCRICAO AS DESTINO, " +
            "  BEM.IDENTIFICACAO, " +
            "  BEM.DESCRICAO, " +
            "  EST.ESTADOBEM AS ESTADOCONSERVACAOBEM, " +
            "  EST.VALORORIGINAL, " +
            "  EST.VALORACUMULADODAEXAUSTAO + EST.VALORACUMULADODADEPRECIACAO + EST.VALORACUMULADODAAMORTIZACAO + EST.VALORACUMULADODEAJUSTE AS AJUSTES " +
            "FROM EFETIVACAOESTORNOTRANSF LT " +
            "  INNER JOIN SOLICITESTORNOTRANSFER  solicitacaoEstorno ON LT.SOLICITACAOESTORNO_ID = solicitacaoEstorno.ID " +
            "  INNER JOIN ItemEstornoTransferencia item on item.SOLICITACAOESTORNO_ID =  solicitacaoEstorno.ID " +
            "  INNER JOIN EVENTOBEM EV ON EV.ID = item.ID " +
            "  INNER JOIN ESTADOBEM EST ON EST.ID = EV.ESTADORESULTANTE_ID " +
            "  INNER JOIN BEM BEM ON BEM.ID = EV.BEM_ID " +
            "  INNER JOIN EfetivacaoTransferenciaBem efetivacao on item.EFETIVACAOTRANSFERENCIA_ID = efetivacao.ID " +
            "  INNER JOIN TransferenciaBem transferencia on efetivacao.TRANSFERENCIABEM_ID = transferencia.ID " +
            "  INNER JOIN LOTETRANSFERENCIABEM solicitacao on transferencia.LOTETRANSFERENCIABEM_ID = solicitacao.ID " +
            "  INNER JOIN PESSOAFISICA PF_RESPONSAVEL_ORIGEM   ON PF_RESPONSAVEL_ORIGEM.ID = solicitacao.RESPONSAVELORIGEM_ID " +
            "  INNER JOIN PESSOAFISICA PF_RESPONSAVEL_DESTINO  ON PF_RESPONSAVEL_DESTINO.ID = solicitacao.RESPONSAVELDESTINO_ID " +
            "  INNER JOIN vwhierarquiaadministrativa origem ON origem.subordinada_id =  solicitacao.UNIDADEORIGEM_ID " +
            "    and trunc(lt.dataEfetivacao) between trunc(origem.iniciovigencia) and coalesce(trunc(origem.fimvigencia), trunc(lt.dataEfetivacao)) " +
            "  INNER JOIN vwhierarquiaadministrativa destino ON destino.subordinada_id =  solicitacao.UNIDADEDESTINO_ID " +
            "    and trunc(lt.dataEfetivacao) between trunc(destino.iniciovigencia) and coalesce(trunc(destino.fimvigencia), trunc(lt.dataEfetivacao)) " +
            "  WHERE LT.ID = :efetivacaoEstorno_id";

        Query q = em.createNativeQuery(sql);
        q.setParameter("efetivacaoEstorno_id", efetivacao.getId());
        List<RelatorioEfetivacaoSolicitacaoEstorno> retorno = Lists.newArrayList();
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioEfetivacaoSolicitacaoEstorno efetivaoEstono = new RelatorioEfetivacaoSolicitacaoEstorno();
                efetivaoEstono.setCodigoEfetivacao((BigDecimal) obj[0]);
                efetivaoEstono.setDataHoraCriacao((Date) obj[1]);
                efetivaoEstono.setResponsavelOrigem((String) obj[2]);
                efetivaoEstono.setResponsavelDestino((String) obj[3]);
                efetivaoEstono.setUnidadeOrigem((String) obj[4]);
                efetivaoEstono.setUnidadeDestino((String) obj[5]);
                efetivaoEstono.setIdentificacao((String) obj[6]);
                efetivaoEstono.setDescricao((String) obj[7]);
                efetivaoEstono.setEstadoBem(Util.traduzirEnum(EstadoConservacaoBem.class, (String) obj[8]).getDescricao());
                efetivaoEstono.setValorOriginal((BigDecimal) obj[9]);
                efetivaoEstono.setAjustes((BigDecimal) obj[10]);
                retorno.add(efetivaoEstono);
            }
            return retorno;
        }
        return Lists.newArrayList();
    }

    private void contabilizarOperacao(EfetivacaoEstornoTransferencia ef, List<EventoBem> transferenciaConcedidas) throws ExcecaoNegocioGenerica {
        integradorPatrimonialContabilFacade.transferirBens(transferenciaConcedidas, "Estorno da Efetivação de Tranferência N°" + ef.getSolicitacaoEstorno().getLoteEfetivacaoTransferencia().getCodigo(), null);
    }

    public SolicitacaoEstornoTransferenciaFacade getSolicitacaoEstornoTransferenciaFacade() {
        return solicitacaoEstornoTransferenciaFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }
}
