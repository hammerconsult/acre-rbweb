/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.MapClp;
import br.com.webpublico.entidadesauxiliares.MapTagOCC;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.ItemParametroEventoDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.MovimentoContabilDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.ObjetoParametroDTO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.ParametroEventoDTO;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.ApiServiceContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcLancamentoContabilDAO;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author major
 */
@Stateless
public class ContabilizacaoFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(ContabilizacaoFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemMovelFacade;
    @EJB
    private SaldoGrupoBemImovelFacade saldoGrupoBemImovelFacade;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private SaldoGrupoBemIntangivelFacade saldoGrupoBemIntangivelFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private HashMap<MapClp, CLP> listaCLPs = new HashMap<>();
    private HashMap<MapTagOCC, OrigemContaContabil> listaTagsOCC = new HashMap<>();


    public void limparEM() {
        em.clear();
        listaCLPs = new HashMap<>();
        listaTagsOCC = new HashMap<>();
        saldoContaContabilFacade.limparConfiguracaoContabil();
        saldoGrupoBemMovelFacade.limparConfiguracaoContabil();
        saldoGrupoBemImovelFacade.limparConfiguracaoContabil();
        saldoGrupoMaterialFacade.limparConfiguracaoContabil();
        saldoGrupoBemIntangivelFacade.limparConfiguracaoContabil();
    }

    public SaldoContaContabilFacade getSaldoContaContabilFacade() {
        return saldoContaContabilFacade;
    }

    public void contabilizar(ParametroEvento parametroEvento) throws ExcecaoNegocioGenerica {
        contabilizar(parametroEvento, false);
    }

    public void contabilizar(ParametroEvento parametroEvento, boolean simulacao) throws ExcecaoNegocioGenerica {
        if (configuracaoContabilFacade.isGerarSaldoUtilizandoMicroService("CONTABILIZARMICROSERVICE")) {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                ParametroEventoDTO parametroEventoDTO = criarParametroEventoDTO(parametroEvento, simulacao);
                reprocessamentoLancamentoContabilSingleton.adicionarMovimentoContabilizar(parametroEventoDTO);

                if (reprocessamentoLancamentoContabilSingleton.getMovimentoContabilDTO().getContabilizar().size() >=
                    reprocessamentoLancamentoContabilSingleton.getReprocessamentoContabil().getConfiguracaoContabil().getTamanhoLoteReprocessamentoContabil()) {
                    MovimentoContabilDTO dto = new MovimentoContabilDTO();
                    dto.getContabilizar().addAll(reprocessamentoLancamentoContabilSingleton.getMovimentoContabilDTO().getContabilizar());
                    reprocessamentoLancamentoContabilSingleton.atribuirNullMovimentoContabil();
                    dto = ApiServiceContabil.getService().movimentarContabil(dto);
                    if (dto != null) {
                        for (String msg : dto.getMensagensErro()) {
                            reprocessamentoLancamentoContabilSingleton.adicionarLogErro(msg);
                            reprocessamentoLancamentoContabilSingleton.contaComErro();
                        }
                    }
                }
            } else {
                ParametroEventoDTO parametroEventoDTO = criarParametroEventoDTO(parametroEvento, simulacao);
                ApiServiceContabil.getService().contabilizar(parametroEventoDTO);
            }
        } else {
            UnidadeOrganizacional unidadeOrganizacional = parametroEvento.getUnidadeOrganizacional();
            EventoContabil eventoContabil = parametroEvento.getEventoContabil();
            try {
                contabilizarParametroEvento(parametroEvento, simulacao);
            } catch (ExcecaoNegocioGenerica ex) {
                tipoContaAuxiliarFacade.limparContasDetalhadas();
                logger.error("Erro ao contabilizar: ", ex);
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            } catch (Exception ex) {
                tipoContaAuxiliarFacade.limparContasDetalhadas();
                logger.error("Erro ao contabilizar: ", ex);
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }
        }
    }

    @NotNull
    private ParametroEventoDTO criarParametroEventoDTO(ParametroEvento parametroEvento, boolean simulacao) {
        ParametroEventoDTO parametroEventoDTO = new ParametroEventoDTO();
        parametroEventoDTO.setDataEvento(DataUtil.dateToLocalDate(parametroEvento.getDataEvento()));
        parametroEventoDTO.setComplementoHistorico(parametroEvento.getComplementoHistorico());
        parametroEventoDTO.setIdEventoContabil(parametroEvento.getEventoContabil().getId());
        parametroEventoDTO.setIdUnidadeOrganizacional(parametroEvento.getUnidadeOrganizacional().getId());
        parametroEventoDTO.setClasseOrigem(parametroEvento.getClasseOrigem());
        parametroEventoDTO.setIdOrigem(parametroEvento.getIdOrigem());
        parametroEventoDTO.setComplementoId(parametroEvento.getComplementoId());
        parametroEventoDTO.setIdExercicio(parametroEvento.getExercicio().getId());
        parametroEventoDTO.setTipoBalancete(parametroEvento.getTipoBalancete());
        parametroEventoDTO.setSimulacao(simulacao);

        parametroEventoDTO.setItensParametrosEvento(Lists.<ItemParametroEventoDTO>newArrayList());
        for (ItemParametroEvento itemParametroEvento : parametroEvento.getItensParametrosEvento()) {
            ItemParametroEventoDTO item = new ItemParametroEventoDTO();
            item.setValor(itemParametroEvento.getValor());
            item.setTagValor(itemParametroEvento.getTagValor());
            item.setOperacaoClasseCredor(itemParametroEvento.getOperacaoClasseCredor());

            item.setObjetoParametros(Lists.<ObjetoParametroDTO>newArrayList());
            for (ObjetoParametro objetoParametro : itemParametroEvento.getObjetoParametros()) {
                ObjetoParametroDTO obj = new ObjetoParametroDTO();
                obj.setClasseObjeto(objetoParametro.getClasseObjeto());
                obj.setIdObjeto(objetoParametro.getIdObjeto());
                obj.setTipoObjetoParametro(objetoParametro.getTipoObjetoParametro() != null ? objetoParametro.getTipoObjetoParametro() : ObjetoParametro.TipoObjetoParametro.AMBOS);

                try {
                    //Object entidade = tipoContaAuxiliarFacade.localizarEntidade(objetoParametro.getIdObjeto(), objetoParametro.getClasseObjeto());
                    /*Class c = Class.forName("br.com.webpublico.entidades."+objetoParametro.getClasseObjeto());
                    Object entidade = c.getDeclaredConstructor().newInstance();*/
                    Object entidade = objetoParametro.getEntidade();


                    if (entidade != null &&
                        entidade instanceof SuperEntidadeContabilGerarContaAuxiliar) {
                        if (entidade instanceof TransferenciaContaFinanceira ||
                            entidade instanceof EstornoTransferencia ||
                            entidade instanceof LiberacaoCotaFinanceira ||
                            entidade instanceof EstornoLibCotaFinanceira ||
                            entidade instanceof TransferenciaMesmaUnidade ||
                            entidade instanceof EstornoTransfMesmaUnidade ||
                            entidade instanceof TransfBensMoveis ||
                            entidade instanceof TransfBensImoveis ||
                            entidade instanceof TransferenciaBensEstoque ||
                            entidade instanceof TransfBensIntangiveis) {
                            obj.setMovimentarContabilidadeRecebido(Boolean.TRUE);
                        } else {
                            obj.setMovimentarContabilidadeRecebido(Boolean.FALSE);
                        }
                        obj.setGerarContaAuxiliar(Boolean.TRUE);
                        if (entidade instanceof SuperEntidadeContabilGerarContaAuxiliar) {
                            //entidade = tipoContaAuxiliarFacade.localizarEntidade(objetoParametro.getIdObjeto(), objetoParametro.getClasseObjeto());
                            SuperEntidadeContabilGerarContaAuxiliar superEntidadeContabilGerarContaAuxiliar = (SuperEntidadeContabilGerarContaAuxiliar) entidade;
                            parametroEventoDTO.setGeradorContaAuxiliar(superEntidadeContabilGerarContaAuxiliar.gerarContaAuxiliarDTO(parametroEvento.getComplementoId()));
                        }
                    } else {
                        obj.setMovimentarContabilidadeRecebido(Boolean.FALSE);
                        obj.setGerarContaAuxiliar(Boolean.FALSE);
                    }
                } catch (Exception e) {
                    obj.setMovimentarContabilidadeRecebido(Boolean.FALSE);
                    obj.setGerarContaAuxiliar(Boolean.FALSE);
                }
                item.getObjetoParametros().add(obj);
            }

            parametroEventoDTO.getItensParametrosEvento().add(item);
        }
        return parametroEventoDTO;
    }

    private void contabilizarParametroEvento(ParametroEvento parametroEvento, boolean simulacao) {
        try {
            EventoContabil eventoContabil = parametroEvento.getEventoContabil();
            CLP clp = null;
            TagValor tagValorTemp = null;
            List<LancamentoContabil> listaLancamentosCont = new ArrayList<>();
            boolean canLacamentoContaDebitoCredito = canLacamentoContaDebitoCredito(parametroEvento);
            for (ItemParametroEvento itemParametro : parametroEvento.getItensParametrosEvento()) {
                if (itemParametro.getTagValor() != tagValorTemp) {
                    tagValorTemp = itemParametro.getTagValor();
                    clp = recuperaCLPPorEventoETag(parametroEvento.getEventoContabil(), itemParametro.getTagValor(), parametroEvento.getDataEvento());
                }

                BigDecimal valor = itemParametro.getValor();
                if (valor.scale() > 2) {
                    throw new ExcecaoNegocioGenerica("Erro na contabilização do evento " + parametroEvento.getEventoContabil().toString() + "." +
                        " Erro: Valor com " + valor.scale() + " casas decimais. Só é permitido contabilizar valores com 2 (DUAS) casas decimais.");
                }
                if (clp.getLcps().isEmpty()) {
                    throw new ExcecaoNegocioGenerica("Não foi encontrada nenhuma LCP para a CLP " + clp.toString() + ".");
                }
                for (LCP lcp : clp.getLcps()) {
                    ContaContabil contaCredito = null;
                    ContaContabil contaDebito = null;

                    contaCredito = recuperarContaCredito(parametroEvento, itemParametro, lcp, contaCredito, ObjetoParametro.TipoObjetoParametro.CREDITO);
                    contaDebito = recuperarContaDebito(parametroEvento, itemParametro, lcp, contaDebito, ObjetoParametro.TipoObjetoParametro.DEBITO);

                    ContaAuxiliar contaAuxiliarCredito = null;
                    ContaAuxiliar contaAuxiliarDebito = null;

                    ContaAuxiliar contaAuxiliarCredSiconfi = null;
                    ContaAuxiliar contaAuxiliarDebSiconfi = null;

                    ContaAuxiliarDetalhada contaAuxiliarDetalhadaCredSiconfi = null;
                    ContaAuxiliarDetalhada contaAuxiliarDetalhadaDebSiconfi = null;
                    if (!simulacao && saldoContaContabilFacade.getConfiguracaoContabil().getGerarContaAuxiliarSiconfi()) {
                        if (lcp.getTipoContaAuxCredSiconfi() != null) {
                            contaAuxiliarCredSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliar(lcp.getTipoContaAuxCredSiconfi(), itemParametro.getObjetoParametros(), contaCredito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.CREDITO);
                            contaAuxiliarDetalhadaCredSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliarDetalhada(lcp.getTipoContaAuxCredSiconfi(), itemParametro.getObjetoParametros(), contaCredito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.CREDITO);
                        }
                        if (contaAuxiliarCredSiconfi == null) {
                            TipoContaAuxiliar tipoContaAuxiliarCredito = buscarTipoContaAuxiliarContaPorTagEObjeto(lcp.getTagOCCCredito(),
                                itemParametro.getObjetoParametrosCredito(),
                                itemParametro.getOperacaoClasseCredor(),
                                parametroEvento.getDataEvento(),
                                ObjetoParametro.TipoObjetoParametro.CREDITO);
                            if (tipoContaAuxiliarCredito != null) {
                                contaAuxiliarCredSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliar(tipoContaAuxiliarCredito, itemParametro.getObjetoParametros(), contaCredito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.CREDITO);
                                contaAuxiliarDetalhadaCredSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliarDetalhada(tipoContaAuxiliarCredito, itemParametro.getObjetoParametros(), contaCredito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.CREDITO);
                            }
                        }
                        if (lcp.getTipoContaAuxDebSiconfi() != null) {
                            contaAuxiliarDebSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliar(lcp.getTipoContaAuxDebSiconfi(), itemParametro.getObjetoParametros(), contaDebito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.DEBITO);
                            contaAuxiliarDetalhadaDebSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliarDetalhada(lcp.getTipoContaAuxDebSiconfi(), itemParametro.getObjetoParametros(), contaDebito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.DEBITO);
                        }
                        if (contaAuxiliarDebSiconfi == null) {
                            TipoContaAuxiliar tipoContaAuxiliarDebito = buscarTipoContaAuxiliarContaPorTagEObjeto(lcp.getTagOCCDebito(),
                                itemParametro.getObjetoParametrosDebito(),
                                itemParametro.getOperacaoClasseCredor(),
                                parametroEvento.getDataEvento(),
                                ObjetoParametro.TipoObjetoParametro.DEBITO);
                            if (tipoContaAuxiliarDebito != null) {
                                contaAuxiliarDebSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliar(tipoContaAuxiliarDebito, itemParametro.getObjetoParametros(), contaDebito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.DEBITO);
                                contaAuxiliarDetalhadaDebSiconfi = tipoContaAuxiliarFacade.buscarContaAuxiliarDetalhada(tipoContaAuxiliarDebito, itemParametro.getObjetoParametros(), contaDebito, false, parametroEvento, ObjetoParametro.TipoObjetoParametro.DEBITO);
                            }
                        }
                    }
                    listaLancamentosCont.add(
                        new LancamentoContabil(
                            parametroEvento.getDataEvento(),
                            itemParametro.getValor(),
                            (canLacamentoContaDebitoCredito ? contaCredito : null),
                            (canLacamentoContaDebitoCredito ? contaDebito : null),
                            eventoContabil.getClpHistoricoContabil(),
                            parametroEvento.getComplementoHistorico(),
                            parametroEvento.getUnidadeOrganizacional(),
                            lcp,
                            itemParametro,
                            contaAuxiliarCredito,
                            contaAuxiliarDebito,
                            contaAuxiliarCredSiconfi,
                            contaAuxiliarDebSiconfi,
                            contaAuxiliarDetalhadaCredSiconfi,
                            contaAuxiliarDetalhadaDebSiconfi));
                }
            }
            if (!simulacao) {
                em.persist(parametroEvento);
                for (LancamentoContabil lcont : listaLancamentosCont) {
                    gerarSaldosContaContabilDoLancamentoContabil(lcont);
                    em.persist(lcont);
                }
            }
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage());

        } catch (Exception e) {
            logger.error("Erro: ", e);
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void esperarUmPouco() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void gerarSaldosContaContabilDoLancamentoContabil(LancamentoContabil lcont) {
        try {
            if (lcont.getContaCredito() != null) {
                saldoContaContabilFacade.geraSaldoContaContabil(lcont.getDataLancamento(), lcont.getContaCredito(), lcont.getUnidadeOrganizacional(), TipoLancamentoContabil.CREDITO, lcont.getValor(), lcont.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SOMA);
            }
            if (lcont.getContaDebito() != null) {
                saldoContaContabilFacade.geraSaldoContaContabil(lcont.getDataLancamento(), lcont.getContaDebito(), lcont.getUnidadeOrganizacional(), TipoLancamentoContabil.DEBITO, lcont.getValor(), lcont.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SOMA);
            }
            if (lcont.getContaAuxiliarCredSiconfi() != null) {
                saldoContaContabilFacade.geraSaldoContaContabil(lcont.getDataLancamento(), lcont.getContaAuxiliarCredSiconfi(), lcont.getUnidadeOrganizacional(), TipoLancamentoContabil.CREDITO, lcont.getValor(), lcont.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SOMA);
            }
            if (lcont.getContaAuxCreDetalhadaSiconfi() != null) {
                saldoContaContabilFacade.geraSaldoContaContabil(lcont.getDataLancamento(), lcont.getContaAuxCreDetalhadaSiconfi(), lcont.getUnidadeOrganizacional(), TipoLancamentoContabil.CREDITO, lcont.getValor(), lcont.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SOMA);
            }
            if (lcont.getContaAuxiliarDebSiconfi() != null) {
                saldoContaContabilFacade.geraSaldoContaContabil(lcont.getDataLancamento(), lcont.getContaAuxiliarDebSiconfi(), lcont.getUnidadeOrganizacional(), TipoLancamentoContabil.DEBITO, lcont.getValor(), lcont.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SOMA);
            }
            if (lcont.getContaAuxDebDetalhadaSiconfi() != null) {
                saldoContaContabilFacade.geraSaldoContaContabil(lcont.getDataLancamento(), lcont.getContaAuxDebDetalhadaSiconfi(), lcont.getUnidadeOrganizacional(), TipoLancamentoContabil.DEBITO, lcont.getValor(), lcont.getTipoBalancete(), SomaSubtraiSaldoContaContabil.SOMA);
            }
        } catch (ExcecaoNegocioGenerica e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private ContaContabil recuperarContaDebito(ParametroEvento parametroEvento, ItemParametroEvento itemParametro, LCP lcp, ContaContabil contaDebito, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) {
        if ((itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.EXTRA
            || itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.NENHUM
            || itemParametro.getOperacaoClasseCredor() == null)
            && lcp.getContaDebito() != null) {
            contaDebito = (ContaContabil) lcp.getContaDebito();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER && lcp.getContaDebitoInterMunicipal() != null) {
            contaDebito = (ContaContabil) lcp.getContaDebitoInterMunicipal();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_ESTADO && lcp.getContaDebitoInterEstado() != null) {
            contaDebito = (ContaContabil) lcp.getContaDebitoInterEstado();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_UNIAO && lcp.getContaDebitoInterUniao() != null) {
            contaDebito = (ContaContabil) lcp.getContaDebitoInterUniao();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTRA && lcp.getContaDebitoIntra() != null) {
            contaDebito = (ContaContabil) lcp.getContaDebitoIntra();
        } else if (lcp.getTagOCCDebito() != null) {
            contaDebito = buscarContaPorTagEObjeto(lcp.getTagOCCDebito(), itemParametro.getObjetoParametrosDebito(), itemParametro.getOperacaoClasseCredor(), parametroEvento.getDataEvento(), tipoObjetoParametro);
        } else {
            if (itemParametro.getOperacaoClasseCredor() != null) {
                //se a conta estiver null, retornar a conta configurada na extra por padrão,  se extra estiver null exception.
                if (contaDebito == null) {
                    contaDebito = (ContaContabil) lcp.getContaDebito();
                }
                Preconditions.checkNotNull(contaDebito, " Não foi encontrada nenhuma conta contábil de débito configurada para a operação classe credor " + itemParametro.getOperacaoClasseCredor().getDescricao());
            } else {
                if (contaDebito == null) {
                    contaDebito = (ContaContabil) lcp.getContaDebito();
                }
                Preconditions.checkNotNull(contaDebito, " Não foi encontrada nenhuma conta contábil de débito configurada para a operação classe credor ");

            }
        }
        return buscarContaCorretaQuandoSintetica(parametroEvento, lcp, contaDebito, tipoObjetoParametro);
    }

    private ContaContabil recuperarContaCredito(ParametroEvento parametroEvento, ItemParametroEvento itemParametro, LCP lcp, ContaContabil contaCredito, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) {
        if ((itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.EXTRA
            || itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.NENHUM
            || itemParametro.getOperacaoClasseCredor() == null)
            && lcp.getContaCredito() != null) {
            contaCredito = (ContaContabil) lcp.getContaCredito();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER && lcp.getContaCreditoInterMunicipal() != null) {
            contaCredito = (ContaContabil) lcp.getContaCreditoInterMunicipal();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_ESTADO && lcp.getContaCreditoInterEstado() != null) {
            contaCredito = (ContaContabil) lcp.getContaCreditoInterEstado();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTER_UNIAO && lcp.getContaCreditoInterUniao() != null) {
            contaCredito = (ContaContabil) lcp.getContaCreditoInterUniao();
        } else if (itemParametro.getOperacaoClasseCredor() == OperacaoClasseCredor.INTRA && lcp.getContaCreditoIntra() != null) {
            contaCredito = (ContaContabil) lcp.getContaCreditoIntra();
        } else if (lcp.getTagOCCCredito() != null) {
            contaCredito = buscarContaPorTagEObjeto(lcp.getTagOCCCredito(), itemParametro.getObjetoParametrosCredito(), itemParametro.getOperacaoClasseCredor(), parametroEvento.getDataEvento(), tipoObjetoParametro);
        } else {
            if (itemParametro.getOperacaoClasseCredor() != null) {
                //se a conta estiver null, retornar a conta configurada na extra por padrão,  se extra estiver null exception.
                if (contaCredito == null) {
                    contaCredito = (ContaContabil) lcp.getContaCredito();
                }
                Preconditions.checkNotNull(contaCredito, " Não foi encontrada nenhuma conta contábil de crédito configurada para a operação classe credor " + itemParametro.getOperacaoClasseCredor().getDescricao());
            } else {
                if (contaCredito == null) {
                    contaCredito = (ContaContabil) lcp.getContaCredito();
                }
                Preconditions.checkNotNull(contaCredito, " Não foi encontrada nenhuma conta contábil de crédito configurada para a operação classe credor ");
            }
        }
        return buscarContaCorretaQuandoSintetica(parametroEvento, lcp, contaCredito, tipoObjetoParametro);
    }

    private boolean canLacamentoContaDebitoCredito(ParametroEvento parametroEvento) {
        if (TipoEventoContabil.AJUSTE_CONTABIL_MANUAL.equals(parametroEvento.getEventoContabil().getTipoEventoContabil())) {
            return isLancamentoManualContabilizarSistema(Long.parseLong(parametroEvento.getIdOrigem()));
        }
        return true;
    }

    private ContaContabil buscarContaCorretaQuandoSintetica(ParametroEvento parametroEvento, LCP lcp, ContaContabil contaContabil, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) {
        if (CategoriaConta.SINTETICA.equals(contaContabil.getCategoria()) &&
            !ObjetoParametro.TipoObjetoParametro.AMBOS.equals(tipoObjetoParametro) &&
            LancamentoContabilManual.class.getSimpleName().equals(parametroEvento.getClasseOrigem())) {
            return buscarContasLancamentoManualPorLancamentoLcpETipo(Long.parseLong(parametroEvento.getIdOrigem().trim()), lcp.getId(),
                ObjetoParametro.TipoObjetoParametro.CREDITO.equals(tipoObjetoParametro) ? TipoLancamentoContabil.CREDITO : TipoLancamentoContabil.DEBITO);
        }
        return contaContabil;
    }

    private ContaContabil buscarContasLancamentoManualPorLancamentoLcpETipo(Long idLancamento, Long idLcp, TipoLancamentoContabil tipoLancamento) {
        String sql = " select c.*, cc.migracao, cc.naturezaconta, cc.naturezainformacao, cc.naturezasaldo, cc.subsistema " +
            " from contacontabil cc " +
            "    inner join conta c on c.id = cc.id " +
            "    inner join contalancamentomanual clm on clm.contacontabil_id = cc.id " +
            " where clm.lancamento_id = :idLancamento and clm.lcp_id = :idLcp and clm.tipo = :tipoLancamento ";
        Query q = em.createNativeQuery(sql, ContaContabil.class);
        q.setParameter("idLancamento", idLancamento);
        q.setParameter("idLcp", idLcp);
        q.setParameter("tipoLancamento", tipoLancamento.name());
        List<ContaContabil> resultado = q.getResultList();
        return resultado.isEmpty() ? null : resultado.get(0);
    }

    private boolean isLancamentoManualContabilizarSistema(Long idLancamento) {
        String sql = " select lcm.id " +
            " from lancamentocontabilmanual lcm " +
            "   inner join tipolancamentomanual tlm on tlm.lancamento_id = lcm.id " +
            " where lcm.id = :idLancamento " +
            "   and tlm.tipoprestacaodecontas = :sistema ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLancamento", idLancamento);
        q.setParameter("sistema", TipoPrestacaoDeContas.SISTEMA.name());
        return !q.getResultList().isEmpty();
    }

    public CLP recuperaCLPPorEventoETag(EventoContabil evento, TagValor tag, Date data) throws ExcecaoNegocioGenerica {
        try {
            MapClp mapClp = new MapClp(evento, tag);
            if (listaCLPs.containsKey(mapClp)) {
                return listaCLPs.get(mapClp);
            }

            String sql = "SELECT clp.* FROM ITEMEVENTOCLP ITEM "
                + "INNER JOIN CLP CLP ON ITEM.CLP_ID = CLP.ID "
                + "WHERE ITEM.TAGVALOR = :tag AND ITEM.EVENTOCONTABIL_ID = :evento "
                + "AND to_date(:data,'dd/mm/yyyy') between trunc(clp.inicioVigencia) and coalesce(trunc(clp.fimVigencia), to_date(:data,'dd/mm/yyyy'))";
            Query q = em.createNativeQuery(sql, CLP.class);
            q.setParameter("evento", evento.getId());
            q.setParameter("tag", tag.name());
            q.setParameter("data", DataUtil.getDataFormatada(data));
            List<CLP> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                CLP c = resultado.get(0);
                listaCLPs.put(mapClp, c);
                return c;
            } else {
                throw new ExcecaoNegocioGenerica(" CLP não encontrada para o evento " + evento + " e tag " + tag + " na Data de:" + DataUtil.getDataFormatada(data));
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private ContaContabil buscarContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("ContaDespesa".equals(obj.getClasseObjeto()) ||
                    "ContaDeDestinacao".equals(obj.getClasseObjeto()) ||
                    "ContaReceita".equals(obj.getClasseObjeto()) ||
                    "ContaContabil".equals(obj.getClasseObjeto()) ||
                    "ContaExtraorcamentaria".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccConta();
        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                //se encontrar no MAP ja finaliza retornando a conta contábil deste método.
                return retornaContaOCCporOperacaoClasseCredor(op, listaTagsOCC.get(mapTagOCC));
            }

            Query q = em.createNativeQuery(sql, OCCConta.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            q.setParameter("tag", tagOCC.getId());
            List<OCCConta> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + " e Conta " + recuperarToStringIDObjeto(idObjeto, Conta.class));
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("ContaDespesa".equals(obj.getClasseObjeto()) ||
                    "ContaDeDestinacao".equals(obj.getClasseObjeto()) ||
                    "ContaReceita".equals(obj.getClasseObjeto()) ||
                    "ContaContabil".equals(obj.getClasseObjeto()) ||
                    "ContaExtraorcamentaria".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }
        String sql = getSqlOccConta();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                return listaTagsOCC.get(mapTagOCC).getTipoContaAuxiliarSiconfi();
            }

            Query q = em.createNativeQuery(sql, OCCConta.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            q.setParameter("tag", tagOCC.getId());
            List<OCCConta> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccConta() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN OCCCONTA OCC ON ORI.ID = OCC.ID"
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND OCC.CONTAORIGEM_ID = :idObjeto "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private boolean isTipoObjetoParametro(ObjetoParametro.TipoObjetoParametro tipoObjetoParametro, ObjetoParametro obj) {
        return obj.getTipoObjetoParametro().equals(ObjetoParametro.TipoObjetoParametro.AMBOS) || obj.getTipoObjetoParametro().equals(tipoObjetoParametro);
    }

    private ContaContabil buscarContaPorTagEObjetoContaFinanceira(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {

        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String sql = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if (obj.getClasseObjeto().equals("SubConta")) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        sql = getSqlOccBanco();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                //se encontrar no MAP ja finaliza retornando a conta contábil deste método.
                return retornaContaOCCporOperacaoClasseCredor(op, listaTagsOCC.get(mapTagOCC));
            }

            Query q = em.createNativeQuery(sql, OCCBanco.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCBanco> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + " e Conta Financeira " + recuperarToStringIDObjeto(idObjeto, SubConta.class));
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarPorTagEObjetoContaFinanceira(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {

        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("SubConta".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccBanco();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                return listaTagsOCC.get(mapTagOCC).getTipoContaAuxiliarSiconfi();
            }
            Query q = em.createNativeQuery(sql, OCCBanco.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCBanco> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccBanco() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN OCCBANCO OCC ON ORI.ID = OCC.ID"
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND OCC.SUBCONTA_ID = :idObjeto "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private ContaContabil buscarContaPorTagEObjetoTipoClassePessoa(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String sql = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if (obj.getClasseObjeto().equals("ClasseCredor")) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }


        sql = getSqlOccClassePessoa();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto.toString());
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                //se encontrar no MAP ja finaliza retornando a conta contábil deste método.
                return retornaContaOCCporOperacaoClasseCredor(op, listaTagsOCC.get(mapTagOCC));
            }

            Query q = em.createNativeQuery(sql, OccClassePessoa.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OccClassePessoa> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + " e Classe " + recuperarToStringIDObjeto(idObjeto, ClasseCredor.class));
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarPorTagEObjetoTipoClassePessoa(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("ClasseCredor".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }


        String sql = getSqlOccClassePessoa();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                return listaTagsOCC.get(mapTagOCC).getTipoContaAuxiliarSiconfi();
            }

            Query q = em.createNativeQuery(sql, OccClassePessoa.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OccClassePessoa> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccClassePessoa() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN OCCCLASSEPESSOA OCC ON ORI.ID = OCC.ID"
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND OCC.CLASSEPESSOA_ID = :idObjeto "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private ContaContabil buscarContaPorTagEObjetoNaturezaDividaPublica(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String sql = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if (obj.getClasseObjeto().equals("CategoriaDividaPublica")) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        sql = getSqlOccNaturezaDividaPublica();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                //se encontrar no MAP ja finaliza retornando a conta contábil deste método.
                return retornaContaOCCporOperacaoClasseCredor(op, listaTagsOCC.get(mapTagOCC));
            }

            Query q = em.createNativeQuery(sql, OCCNaturezaDividaPublica.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCNaturezaDividaPublica> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + " e Categoria da Dívida Pública " + recuperarToStringIDObjeto(idObjeto, CategoriaDividaPublica.class));
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarPorTagEObjetoNaturezaDividaPublica(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("CategoriaDividaPublica".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccNaturezaDividaPublica();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                return listaTagsOCC.get(mapTagOCC).getTipoContaAuxiliarSiconfi();
            }

            Query q = em.createNativeQuery(sql, OCCNaturezaDividaPublica.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCNaturezaDividaPublica> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccNaturezaDividaPublica() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN OCCNATUREZADIVIDAPUBLICA OCC ON ORI.ID = OCC.ID"
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND OCC.CATEGORIADIVIDAPUBLICA_ID = :idObjeto "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private ContaContabil buscarContaPorTagEObjetoTipoPassivoAtuarial(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String sql = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if (obj.getClasseObjeto().equals("TipoPassivoAtuarial")) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        sql = getSqlOccTipoPassivoAtuarial();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                //se encontrar no MAP ja finaliza retornando a conta contábil deste método.
                return retornaContaOCCporOperacaoClasseCredor(op, listaTagsOCC.get(mapTagOCC));
            }

            Query q = em.createNativeQuery(sql, OCCTipoPassivoAtuarial.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCTipoPassivoAtuarial> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + " e Tipo Passivo Atuarial " + recuperarToStringIDObjeto(idObjeto, TipoPassivoAtuarial.class));
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarPorTagEObjetoTipoPassivoAtuarial(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("TipoPassivoAtuarial".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccTipoPassivoAtuarial();

        if (idObjeto != null) {
            MapTagOCC mapTagOCC = new MapTagOCC(tagOCC, idObjeto);
            if (listaTagsOCC.containsKey(mapTagOCC)) {
                return listaTagsOCC.get(mapTagOCC).getTipoContaAuxiliarSiconfi();
            }

            Query q = em.createNativeQuery(sql, OCCTipoPassivoAtuarial.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCTipoPassivoAtuarial> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
                listaTagsOCC.put(mapTagOCC, origemContaContabil);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccTipoPassivoAtuarial() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN occtipopassivoatuarial OCC ON ORI.ID = OCC.ID"
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND OCC.tipopassivoatuarial_id = :idObjeto "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private ContaContabil buscarContaPorTagEObjetoOrigemRecurso(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String sql = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if (obj.getClasseObjeto().equals(OrigemSuplementacaoORC.class.getSimpleName())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        sql = getSqlOccOrigemRecurso();

        if (idObjeto != null) {

            Query q = em.createNativeQuery(sql, OCCOrigemRecurso.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCOrigemRecurso> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + " e Origem de Recuro " + idObjeto);
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarPorTagEObjetoOrigemRecurso(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if (OrigemSuplementacaoORC.class.getSimpleName().equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccOrigemRecurso();

        if (idObjeto != null) {
            Query q = em.createNativeQuery(sql, OCCOrigemRecurso.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCOrigemRecurso> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccOrigemRecurso() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN occorigemrecurso OCC ON ORI.ID = OCC.ID "
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND occ.origemsuplementacaoorc = :idObjeto "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private ContaContabil buscarContaPorTagEObjetoGrupoBem(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String tipoGrupo = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("GrupoBem".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                } else if ("TipoGrupo".equals(obj.getClasseObjeto())) {
                    tipoGrupo = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccGrupoBem();

        if (idObjeto != null && tipoGrupo != null) {

            Query q = em.createNativeQuery(sql, OCCGrupoBem.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tipoGrupo", tipoGrupo);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCGrupoBem> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + ", Tipo de Grupo " + TipoGrupo.valueOf(tipoGrupo).toString() + " e Grupo Patrimonial " + recuperarToStringIDObjeto(Long.parseLong(idObjeto), GrupoBem.class));
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarPorTagEObjetoGrupoBem(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String tipoGrupo = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("GrupoBem".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                } else if ("TipoGrupo".equals(obj.getClasseObjeto())) {
                    tipoGrupo = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccGrupoBem();

        if (idObjeto != null && tipoGrupo != null) {

            Query q = em.createNativeQuery(sql, OCCGrupoBem.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tipoGrupo", tipoGrupo);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCGrupoBem> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccGrupoBem() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN OCCGRUPOBEM OCC ON ORI.ID = OCC.ID "
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND OCC.GRUPOBEM_ID = :idObjeto "
            + " AND OCC.tipoGrupo = :tipoGrupo "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private ContaContabil buscarContaPorTagEObjetoGrupoMaterial(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        String sql = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if (obj.getClasseObjeto().equals("GrupoMaterial")) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        sql = getSqlOccGrupoMaterial();

        if (idObjeto != null) {

            Query q = em.createNativeQuery(sql, OCCGrupoMaterial.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCGrupoMaterial> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
            } else {
                throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma configuração para a tag " + tagOCC + " e Grupo Material " + recuperarToStringIDObjeto(Long.parseLong(idObjeto), GrupoMaterial.class));
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return retornaContaOCCporOperacaoClasseCredor(op, origemContaContabil);
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarPorTagEObjetoGrupoMaterial(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        OrigemContaContabil origemContaContabil = null;
        String idObjeto = null;
        for (ObjetoParametro obj : objetoParametros) {
            if (isTipoObjetoParametro(tipoObjetoParametro, obj)) {
                if ("GrupoMaterial".equals(obj.getClasseObjeto())) {
                    idObjeto = obj.getIdObjeto();
                }
            }
        }

        String sql = getSqlOccGrupoMaterial();

        if (idObjeto != null) {

            Query q = em.createNativeQuery(sql, OCCGrupoMaterial.class);
            q.setParameter("idObjeto", idObjeto);
            q.setParameter("tag", tagOCC.getId());
            q.setParameter("data", DataUtil.getDataFormatada(Util.getDataHoraMinutoSegundoZerado(data)));
            List<OCCGrupoMaterial> resultado = q.getResultList();
            if (!resultado.isEmpty()) {
                origemContaContabil = resultado.get(0);
            } else {
                return null;
            }
        } else {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada o parametro de busca para a tag " + tagOCC);
        }
        return origemContaContabil.getTipoContaAuxiliarSiconfi();
    }

    private String getSqlOccGrupoMaterial() {
        return "SELECT ori.*, occ.* FROM ORIGEMCONTACONTABIL ORI "
            + " INNER JOIN OCCGRUPOMATERIAL OCC ON ORI.ID = OCC.ID "
            + " WHERE ORI.TAGOCC_ID = :tag "
            + " AND OCC.GRUPOMATERIAL_ID = :idObjeto "
            + " AND to_date(:data,'dd/mm/yyyy') between trunc(ORI.INICIOVIGENCIA) AND coalesce(trunc(ORI.FIMVIGENCIA), to_date(:data,'dd/mm/yyyy'))";
    }

    private ContaContabil buscarContaPorTagEObjeto(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        try {
            switch (tagOCC.getEntidadeOCC()) {
                case CONTADESPESA:
                case CONTARECEITA:
                case CONTAEXTRAORCAMENTARIA:
                case DESTINACAO:
                case CONTACONTABIL:
                    return buscarContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case CONTAFINANCEIRA:
                    return buscarContaPorTagEObjetoContaFinanceira(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case TIPOCLASSEPESSOA:
                    return buscarContaPorTagEObjetoTipoClassePessoa(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case NATUREZADIVIDAPUBLICA:
                    return buscarContaPorTagEObjetoNaturezaDividaPublica(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case TIPOPASSIVOATUARIAL:
                    return buscarContaPorTagEObjetoTipoPassivoAtuarial(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case ORIGEM_RECURSO:
                    return buscarContaPorTagEObjetoOrigemRecurso(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case GRUPOBEM:
                    return buscarContaPorTagEObjetoGrupoBem(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case GRUPOMATERIAL:
                    return buscarContaPorTagEObjetoGrupoMaterial(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                default:
                    throw new ExcecaoNegocioGenerica(" Entidade da origem conta contábil não encontrada!");
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private TipoContaAuxiliar buscarTipoContaAuxiliarContaPorTagEObjeto(TagOCC tagOCC, List<ObjetoParametro> objetoParametros, OperacaoClasseCredor op, Date data, ObjetoParametro.TipoObjetoParametro tipoObjetoParametro) throws ExcecaoNegocioGenerica {
        try {
            if (tagOCC == null || tagOCC.getEntidadeOCC() == null) {
                return null;
            }
            switch (tagOCC.getEntidadeOCC()) {
                case CONTADESPESA:
                case CONTARECEITA:
                case CONTAEXTRAORCAMENTARIA:
                case DESTINACAO:
                case CONTACONTABIL:
                    return buscarTipoContaAuxiliarContaPorTagEObjetoContaDespesaReceitaExtraDestinacao(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case CONTAFINANCEIRA:
                    return buscarTipoContaAuxiliarPorTagEObjetoContaFinanceira(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case TIPOCLASSEPESSOA:
                    return buscarTipoContaAuxiliarPorTagEObjetoTipoClassePessoa(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case NATUREZADIVIDAPUBLICA:
                    return buscarTipoContaAuxiliarPorTagEObjetoNaturezaDividaPublica(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case TIPOPASSIVOATUARIAL:
                    return buscarTipoContaAuxiliarPorTagEObjetoTipoPassivoAtuarial(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case ORIGEM_RECURSO:
                    return buscarTipoContaAuxiliarPorTagEObjetoOrigemRecurso(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case GRUPOBEM:
                    return buscarTipoContaAuxiliarPorTagEObjetoGrupoBem(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                case GRUPOMATERIAL:
                    return buscarTipoContaAuxiliarPorTagEObjetoGrupoMaterial(tagOCC, objetoParametros, op, data, tipoObjetoParametro);
                default:
                    throw new ExcecaoNegocioGenerica(" Entidade da origem conta contábil não encontrada!");
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private String recuperarToStringIDObjeto(Long idObjeto, Class contaClass) {
        try {
            Query consulta = em.createQuery("select obj from " + contaClass.getSimpleName() + " obj where obj.id = :id");
            consulta.setParameter("id", idObjeto);
            consulta.setMaxResults(1);
            Object o = (Object) consulta.getSingleResult();
            return o.toString();
        } catch (Exception e) {
            return idObjeto.toString();
        }
    }

    public EventoContabil recuperaEvento(String chave) throws ExcecaoNegocioGenerica {
        try {
            String sql = "select e from EventoContabil e where e.chave = :chave";
            Query q = em.createQuery(sql, EventoContabil.class);
            q.setParameter("chave", chave);
            if (!q.getResultList().isEmpty()) {
                return (EventoContabil) q.getSingleResult();
            } else {
                throw new ExcecaoNegocioGenerica(" Nenhum Evento Contábil encontrado");
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private String recuperarToStringIDObjeto(String idObjeto, Class contaClass) {
        try {
            Query consulta = em.createQuery("select obj from " + contaClass.getSimpleName() + " obj where obj.id = :id");
            consulta.setParameter("id", Long.parseLong(idObjeto));
            consulta.setMaxResults(1);
            Object o = (Object) consulta.getSingleResult();
            return o.toString();
        } catch (Exception e) {
            return idObjeto.toString();
        }
    }

    private ContaContabil retornaContaOCCporOperacaoClasseCredor(OperacaoClasseCredor op, OrigemContaContabil origemContaContabil) throws ExcecaoNegocioGenerica {
        ContaContabil c = null;
        if (op == null
            || OperacaoClasseCredor.EXTRA.equals(op)
            || OperacaoClasseCredor.NENHUM.equals(op)) {
            if (origemContaContabil.getContaContabil() != null) {
                return origemContaContabil.getContaContabil();
            } else {
                throw new ExcecaoNegocioGenerica(" A Conta Contábil Extra da Origem Conta Contábil " + origemContaContabil + " não foi preenchida.");
            }
        } else {
            switch (op) {
                case INTER:
                    c = origemContaContabil.getContaInterMunicipal() != null ? origemContaContabil.getContaInterMunicipal() : origemContaContabil.getContaContabil();
                    break;
                case INTER_ESTADO:
                    c = origemContaContabil.getContaInterEstado() != null ? origemContaContabil.getContaInterEstado() : origemContaContabil.getContaContabil();
                    break;
                case INTER_UNIAO:
                    c = origemContaContabil.getContaInterUniao() != null ? origemContaContabil.getContaInterUniao() : origemContaContabil.getContaContabil();
                    break;
                case INTRA:
                    c = origemContaContabil.getContaIntra() != null ? origemContaContabil.getContaIntra() : origemContaContabil.getContaContabil();
                    break;
            }
        }
        if (c == null) {
            throw new ExcecaoNegocioGenerica(" Não foi encontrada nenhuma conta contábil configurada para a operação classe credor " + op.getDescricao() + " na Origem Conta Contabil " + origemContaContabil + ".");
        }
        return c;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public List<LancamentoContabil> recuperarLancamentosContabeis(EntidadeContabil entidadeContabil) throws ExcecaoNegocioGenerica {
        String sql = "SELECT l.* FROM LANCAMENTOCONTABIL L " +
            " INNER JOIN ITEMPARAMETROEVENTO IT ON L.ITEMPARAMETROEVENTO_ID = IT.ID " +
            " INNER JOIN PARAMETROEVENTO P ON IT.PARAMETROEVENTO_ID = P.ID " +
            " WHERE P.IDORIGEM = :id";
        Query q = em.createNativeQuery(sql, LancamentoContabil.class);
        q.setParameter("id", Persistencia.getId(entidadeContabil));
        List<LancamentoContabil> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado;
        } else {
            return Lists.newArrayList();
        }

    }
}
