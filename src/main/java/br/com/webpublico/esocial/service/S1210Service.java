package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroESocial;
import br.com.webpublico.entidades.rh.esocial.RegistroEventoEsocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.esocial.TipoGrupoCategoriaTrabalhador;
import br.com.webpublico.enums.rh.esocial.TipoRegimePrevidenciario;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.EventoS1210;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.EventosESocialDTO;
import br.com.webpublico.esocial.enums.ClasseWP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.StringUtil.retornaApenasNumeros;

@Service(value = "s1210Service")
public class S1210Service {
    protected static final Logger logger = LoggerFactory.getLogger(S1210Service.class);
    public static final int CATEGORIA_S1202 = 410;

    private PessoaFisicaFacade pessoaFisicaFacade;
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    private FichaRPAFacade fichaRPAFacade;
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    private RegistroESocialFacade registroESocialFacade;
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;

    @Autowired
    private ESocialService eSocialService;

    @PostConstruct
    public void init() {
        try {
            pessoaFisicaFacade = (PessoaFisicaFacade) Util.getFacadeViaLookup("java:module/PessoaFisicaFacade");
            fichaFinanceiraFPFacade = (FichaFinanceiraFPFacade) Util.getFacadeViaLookup("java:module/FichaFinanceiraFPFacade");
            fichaRPAFacade = (FichaRPAFacade) Util.getFacadeViaLookup("java:module/FichaRPAFacade");
            folhaDePagamentoFacade = (FolhaDePagamentoFacade) Util.getFacadeViaLookup("java:module/FolhaDePagamentoFacade");
            registroESocialFacade = (RegistroESocialFacade) new InitialContext().lookup("java:module/RegistroESocialFacade");
            exoneracaoRescisaoFacade = (ExoneracaoRescisaoFacade) new InitialContext().lookup("java:module/ExoneracaoRescisaoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    public void enviarEventoS1210(ConfiguracaoEmpregadorESocial config, EmpregadorESocial empregadorESocial,
                                  RegistroEventoEsocial registroS1210, VinculoFPEventoEsocial vinculo) {
        ValidacaoException ve = new ValidacaoException();
        EventoS1210 eventoS1210 = criarEventoS1210(registroS1210, ve, vinculo, config, empregadorESocial);
        logger.debug("Antes de Enviar: " + eventoS1210.getXml());
        ve.lancarException();
        eSocialService.enviarEventoS1210(eventoS1210);
    }

    private EventoS1210 criarEventoS1210(RegistroEventoEsocial registro, ValidacaoException validacao, VinculoFPEventoEsocial vinculoFPEventoEsocial,
                                         ConfiguracaoEmpregadorESocial config, EmpregadorESocial empregador) {
        EventosESocialDTO.S1210 eventoS1210 = (EventosESocialDTO.S1210) eSocialService.getEventoS1210(empregador);
        eventoS1210.setClasseWP(ClasseWP.FICHAFINANCEIRAFP_OU_RPA);

        PessoaFisica pessoaFisicaServidor = pessoaFisicaFacade.recuperarComDocumentosAndDependentes(vinculoFPEventoEsocial.getIdPessoa());

        adicionarInformacoesBasicas(registro, vinculoFPEventoEsocial, eventoS1210);
        adicionarInformacoesServidor(validacao, eventoS1210, pessoaFisicaServidor);
        adicionarInformacoesPagamento(registro, vinculoFPEventoEsocial, eventoS1210, vinculoFPEventoEsocial.getIdsFichas(), config, validacao);

        return eventoS1210;
    }

    private void adicionarInformacoesPagamento(RegistroEventoEsocial registro, VinculoFPEventoEsocial vinculo, EventosESocialDTO.S1210 eventoS1210,
                                               List<Long> idFichaFinanceira, ConfiguracaoEmpregadorESocial config,
                                               ValidacaoException validacao) {

        Date dataPgto = DataUtil.criarDataUltimoDiaMes(registro.getMes().getNumeroMes(), registro.getExercicio().getAno()).toDate();
        for (Long idFicha : idFichaFinanceira) {
            FichaFinanceiraFP fichaFinanceiraFP = fichaFinanceiraFPFacade.buscarFichaFinanceiraPorId(idFicha);
            FichaRPA fichaRPA = fichaRPAFacade.buscarFichaRPAPorId(idFicha);

            if (Strings.isNullOrEmpty(eventoS1210.getIdentificadorWP())) {
                eventoS1210.setIdentificadorWP(idFicha.toString());
            } else {
                eventoS1210.setIdentificadorWP(eventoS1210.getIdentificadorWP().concat("/").
                    concat(idFicha.toString()));
            }

            if (fichaFinanceiraFP != null) {
                eventoS1210.setDescricao(fichaFinanceiraFP.getVinculoFP().toString());
                adicionarInformacoesServidorComVinculo(registro, vinculo, eventoS1210, fichaFinanceiraFP, config, dataPgto);
            } else {
                eventoS1210.setDescricao(fichaRPA.getPrestadorServicos().getPrestador().toString());
                adicionarInformacoesPrestadorServico(registro, eventoS1210, fichaRPA, dataPgto, validacao);
            }
        }
    }

    private void adicionarInformacoesServidorComVinculo(RegistroEventoEsocial registro, VinculoFPEventoEsocial vinculo, EventosESocialDTO.S1210 eventoS1210,
                                                        FichaFinanceiraFP fichaFinanceiraFP, ConfiguracaoEmpregadorESocial config, Date dataPgto) {
        EventoS1210.InfoPgto infoPgto = null;

        if (TipoFolhaDePagamento.SALARIO_13.equals(fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento())) {
            EventoS1210.InfoPgto infoPgto2 = eventoS1210.addInfoPgto();
            atribuirRetencoesDecimo(registro, fichaFinanceiraFP, infoPgto2, dataPgto);
        } else {
            infoPgto = eventoS1210.addInfoPgto();
            infoPgto.setDtPgto(DataUtil.removerDias(dataPgto, 1));
            atribuirRentencoesNormalOrFerias(registro, fichaFinanceiraFP, infoPgto);
            preencherVerbasNormais(registro, vinculo, config, infoPgto, fichaFinanceiraFP);
        }
    }


    private void preencherVerbasNormais(RegistroEventoEsocial registro, VinculoFPEventoEsocial vinculo,
                                        ConfiguracaoEmpregadorESocial config, EventoS1210.InfoPgto
                                            infoPgto, FichaFinanceiraFP fichaFinanceiraFP) {
        boolean isExoneracao = false;
        if (TipoFolhaDePagamento.RESCISAO.equals(fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento())) {
            ExoneracaoRescisao exoneracaoRescisao = exoneracaoRescisaoFacade.buscarExoneracaoRescisaoPorVinculoFP(vinculo.getVinculoFP());
            if (exoneracaoRescisao != null) {
                infoPgto.setTpPgto(2);
                infoPgto.setPerRef(DataUtil.getAno(exoneracaoRescisao.getDataRescisao()), DataUtil.getMes(exoneracaoRescisao.getDataRescisao()));
                isExoneracao = true;
            } else {
                infoPgto.setPerRef(registro.getExercicio().getAno(), registro.getMes().getNumeroMes());
            }
        }
        infoPgto.setTpPgto(atribuirTipoDePagamento(registro, vinculo, fichaFinanceiraFP, isExoneracao));
    }


    private void atribuirRentencoesNormalOrFerias(RegistroEventoEsocial
                                                      registro, FichaFinanceiraFP fichaFinanceiraFP, EventoS1210.InfoPgto infoPgto) {
        if (!TipoFolhaDePagamento.RESCISAO.equals(fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento())) {
            infoPgto.setPerRef(registro.getExercicio().getAno(), registro.getMes().getNumeroMes());
        }
        List<ItemFichaFinanceiraFP> itensFicha = Lists.newArrayList();
        Long idFicha = null;
        if (TipoFolhaDePagamento.NORMAL.equals(fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento())) {
            idFicha = fichaFinanceiraFP.getId();
        }
        if (!TipoFolhaDePagamento.SALARIO_13.equals(fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento())) {
            if (idFicha == null) {
                idFicha = fichaFinanceiraFP.getId();
            }
            itensFicha.addAll(fichaFinanceiraFP.getItemFichaFinanceiraFP());
        }
        if (idFicha != null) {
            infoPgto.setIdeDmDev(idFicha.toString());
        }
        infoPgto.setVrLiq(atribuirValorLiquido(itensFicha));
    }

    private void atribuirRetencoesDecimo(RegistroEventoEsocial registro, FichaFinanceiraFP ficha, EventoS1210.InfoPgto infoPgto, Date dataPgto) {
        infoPgto.setPerRef(registro.getExercicio().getAno());
        infoPgto.setIdeDmDev(ficha.getId().toString());
        infoPgto.setTpPgto(1);
        infoPgto.setDtPgto(dataPgto);
        List<ItemFichaFinanceiraFP> itensFicha = Lists.newArrayList();
        itensFicha.addAll(ficha.getItemFichaFinanceiraFP());
        infoPgto.setVrLiq(atribuirValorLiquido(itensFicha));
    }

    private Integer atribuirTipoDePagamento(RegistroEventoEsocial registro, VinculoFPEventoEsocial vinculo, FichaFinanceiraFP fichaFinanceiraFP, boolean isExoneracao) {
        List<TipoArquivoESocial> tipos = registroESocialFacade.getRegistrosEsocialPorIdentificadorWP(fichaFinanceiraFP.getId().toString());

        for (TipoArquivoESocial tipo : tipos) {
            switch (tipo) {
                case S1200: {
                    return 1;
                }
                case S2299: {
                    return 2;
                }
                case S2399: {
                    return 3;
                }
                case S1202: {
                    return 4;
                }
                case S1207: {
                    return 5;
                }
            }
        }

        if (isExoneracao) {
            return 2;
        }

        ExoneracaoRescisao exoneracaoRescisao = exoneracaoRescisaoFacade.buscarExoneracaoRescisaoPorVinculoFP(vinculo.getVinculoFP());
        if (exoneracaoRescisao == null || !TipoFolhaDePagamento.RESCISAO.equals(fichaFinanceiraFP.getFolhaDePagamento().getTipoFolhaDePagamento())) {
            return atribuirCodigoDeInformacaoPagamento(registro, vinculo);
        }

        List<RegistroESocial> registroESocial = registroESocialFacade.buscarRegistroEsocialPorTipoAndIdentificador(TipoArquivoESocial.S2299, exoneracaoRescisao.getId().toString(), 1);
        if (registroESocial != null) {
            try {
                String desligamentoXML = registroESocialFacade.getConteudoXML(registroESocial.get(0), "/eSocial/evtDeslig/infoDeslig/dtDeslig");
                Date dataDesligamento = DataUtil.getDateParse(desligamentoXML, "yyyy-MM-dd");
                int mes = DataUtil.getMes(dataDesligamento);
                int ano = DataUtil.getAno(dataDesligamento);
                if (mes == registro.getMes().getNumeroMes() && ano == registro.getExercicio().getAno()) {
                    return 2;
                } else {
                    return atribuirCodigoDeInformacaoPagamento(registro, vinculo);
                }

            } catch (Exception e) {
                logger.error("Não foi possível ler a data de desligamento");
                return 1;
            }
        }

        return 1;
    }

    private Integer atribuirCodigoDeInformacaoPagamento(RegistroEventoEsocial registro, VinculoFPEventoEsocial vinculo) {

        if (folhaDePagamentoFacade.isServidorPrevidenciaPorTipoRegime(vinculo.getVinculoFP().getId(), registro.getMes().getNumeroMesIniciandoEmZero(),
            registro.getExercicio().getAno(), TipoRegimePrevidenciario.RGPS)) {
            return 1;
        }

        if (vinculo.getVinculoFP().getCategoriaTrabalhador() != null &&
            (TipoGrupoCategoriaTrabalhador.AGENTE_PUBLICO.equals(vinculo.getVinculoFP().getCategoriaTrabalhador().getTipo()))
            || vinculo.getVinculoFP().getCategoriaTrabalhador().getCodigo() == CATEGORIA_S1202) {
            return 4;
        }
        if (vinculo.getPrestadorServico() != null) {
            return 1;
        }

        if (vinculo.getIdVinculo() != null) {
            VinculoFP v = folhaDePagamentoFacade.getVinculoFPFacade().recuperarSimples(vinculo.getIdVinculo());
            if (v instanceof Aposentadoria) {
                return 5;
            }
        }
        return 1;
    }

    private BigDecimal atribuirValorLiquido(List<ItemFichaFinanceiraFP> itensFicha) {
        BigDecimal valorLiquido = BigDecimal.ZERO;

        for (ItemFichaFinanceiraFP item : itensFicha) {
            if (TipoEventoFP.VANTAGEM.equals(item.getEventoFP().getTipoEventoFP())) {
                valorLiquido = valorLiquido.add(item.getValor());
            } else if (TipoEventoFP.DESCONTO.equals(item.getTipoEventoFP())) {
                valorLiquido = valorLiquido.subtract(item.getValor());
            }
        }
        return valorLiquido;
    }

    private BigDecimal atribuirValorLiquidoFichaRPA(List<ItemFichaRPA> itensFicha) {
        BigDecimal valorLiquido = BigDecimal.ZERO;

        for (ItemFichaRPA item : itensFicha) {
            if (TipoEventoFP.VANTAGEM.equals(item.getEventoFP().getTipoEventoFP())) {
                valorLiquido = valorLiquido.add(item.getValor());
            } else if (TipoEventoFP.DESCONTO.equals(item.getEventoFP().getTipoEventoFP())) {
                valorLiquido = valorLiquido.subtract(item.getValor());
            }
        }
        return valorLiquido;
    }

    private void adicionarInformacoesBasicas(RegistroEventoEsocial registro, VinculoFPEventoEsocial vinculoFPEventoEsocial,
                                             EventosESocialDTO.S1210 eventoS1210) {
        String mes = registro.getMes().getNumeroMes().toString();
        String ano = registro.getExercicio().getAno().toString();

        eventoS1210.setIdESocial(ano.concat(mes).concat(vinculoFPEventoEsocial.getCpf()));
        eventoS1210.setIndApuracao(1);
        eventoS1210.setPerApur(registro.getExercicio().getAno(), registro.getMes().getNumeroMes());

        if (registro.getTipoOperacaoESocial() != null) {
            eventoS1210.setOperacao(TipoOperacaoESocial.valueOf(registro.getTipoOperacaoESocial().name()));
        }
    }

    private void adicionarInformacoesServidor(ValidacaoException validacao, EventosESocialDTO.S1210
        eventoS1210, PessoaFisica pessoaFisica) {
        eventoS1210.setCpfTrab(retornaApenasNumeros(pessoaFisica.getCpf()));
        eventoS1210.setCpfBenef(retornaApenasNumeros(pessoaFisica.getCpf()));
        if (pessoaFisica.getCarteiraDeTrabalho() == null) {
            validacao.adicionarMensagemDeOperacaoNaoRealizada("A pessoa " + pessoaFisica + " não possui carteira de trabalho.");
        } else {
            eventoS1210.setNisTrab(pessoaFisica.getCarteiraDeTrabalho().getPisPasep());
        }
    }

    private void adicionarInformacoesPrestadorServico(RegistroEventoEsocial registro, EventosESocialDTO.S1210 eventoS1210, FichaRPA fichaRPA, Date dataPgto, ValidacaoException validacao) {
        EventoS1210.InfoPgto infoPgto = eventoS1210.addInfoPgto();
        atribuirRetencoes(infoPgto, registro, validacao, fichaRPA, dataPgto);
    }

    private EventoS1210.InfoPgto atribuirRetencoes(EventoS1210.InfoPgto infoPgto, RegistroEventoEsocial registro,
                                                   ValidacaoException ve,
                                                   FichaRPA fichaRPA, Date dataPgto) {
        infoPgto.setPerRef(registro.getExercicio().getAno(), registro.getMes().getNumeroMes());
        infoPgto.setIdeDmDev(fichaRPA.getId().toString());
        infoPgto.setTpPgto(1);
        infoPgto.setDtPgto(dataPgto);
        List<ItemFichaRPA> itensFicha = Lists.newArrayList();
        itensFicha.addAll(fichaRPA.getItemFichaRPAs());
        BigDecimal vrLiq = atribuirValorLiquidoFichaRPA(itensFicha);
        if (vrLiq.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Valor zerado ou negativo para o prestador "
                + fichaRPA.getPrestadorServicos().getPrestador() + " Valor: " + vrLiq);
            ve.lancarException();
        } else {
            infoPgto.setVrLiq(vrLiq);
        }
        return infoPgto;
    }
}


