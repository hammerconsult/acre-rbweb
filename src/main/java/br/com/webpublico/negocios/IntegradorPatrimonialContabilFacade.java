package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EventoBemIncorporavelComContabil;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class IntegradorPatrimonialContabilFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BensMoveisFacade bensMoveisFacade;
    @EJB
    private BensImoveisFacade bensImoveisFacade;
    @EJB
    private BensIntangiveisFacade bensIntangiveisFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private TransfBensMoveisFacade transfBensMoveisFacade;
    @EJB
    private TransfBensImoveisFacade transfBensImoveisFacade;
    @EJB
    private TransfBensIntangiveisFacade transfBensIntangiveisFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public static List<EventosAgrupados> agruparListEventoBem(List<? extends EventoBem> eventos, Date dataOperacao) {
        List<EventosAgrupados> retorno = new ArrayList<>();
        for (EventoBem evento : eventos) {
            EventosAgrupados eventoAgrupado = new EventosAgrupados(evento, dataOperacao);
            if (retorno.contains(eventoAgrupado)) {
                retorno.get(retorno.indexOf(eventoAgrupado)).adicionarValor(evento);
            } else {
                eventoAgrupado.adicionarValor(evento);
                retorno.add(eventoAgrupado);
            }
        }
        return retorno;
    }

    public static List<EventosAgrupados> agruparListEventoBem(List<? extends EventoBem> eventos, Date dataOperacao, GrupoBem grupoBem) {
        List<EventosAgrupados> retorno = new ArrayList<>();
        for (EventoBem evento : eventos) {
            EventosAgrupados eventoAgrupado = new EventosAgrupados(evento, dataOperacao, grupoBem);
            if (retorno.contains(eventoAgrupado)) {
                retorno.get(retorno.indexOf(eventoAgrupado)).adicionarValor(evento);
            } else {
                eventoAgrupado.adicionarValor(evento);
                retorno.add(eventoAgrupado);
            }
        }
        return retorno;
    }

    public static List<AgrupadorBem> agruparBens(List<BemVo> bens, Date dataOperacao) {
        List<AgrupadorBem> retorno = new ArrayList<>();
        for (BemVo evento : bens) {
            AgrupadorBem eventoAgrupado = new AgrupadorBem(evento, dataOperacao);
            if (retorno.contains(eventoAgrupado)) {
                retorno.get(retorno.indexOf(eventoAgrupado)).adicionarValor(evento);
            } else {
                eventoAgrupado.adicionarValor(evento);
                retorno.add(eventoAgrupado);
            }
        }
        return retorno;
    }

    public void incorporarBem(EventoBemIncorporavelComContabil evento, String historico) {
        if (evento.getTipoBemResultante() != null) {
            switch (evento.getTipoBemResultante()) {
                case MOVEIS:
                    bensMoveisFacade.salvarNovo(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.INCORPORACAO_BENS_MOVEIS, historico));
                    break;
                case IMOVEIS:
                    bensImoveisFacade.salvarNovo(criarNovaInstanciaDeBensImoveis(evento, TipoOperacaoBensImoveis.INCORPORACAO_BENS_IMOVEIS, historico));
                    break;
                case INTANGIVEIS:
                    bensIntangiveisFacade.salvarNovo(criarNovaInstanciaDeBensIntangiveis(evento, TipoOperacaoBensIntangiveis.INCORPORACAO_BENS_INTANGIVEIS, historico));
                    break;
                default:
                    throw new ExcecaoNegocioGenerica("Não é possível incorporar um bem do tipo " + evento.getTipoBemResultante() + ".");
            }
        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void desincorporarBem(EventoBemIncorporavelComContabil evento, String historico, Boolean simularContabilizacao, Exercicio exercicio) {
        if (evento.getTipoBemResultante() != null) {
            switch (evento.getTipoBemResultante()) {
                case MOVEIS:
                    BensMoveis bensMoveis = criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.DESINCORPORACAO_BENS_MOVEIS, historico, exercicio);
                    simularAndContabilizarBemMovel(simularContabilizacao, bensMoveis);
                    break;
                case IMOVEIS:
                    BensImoveis bensImoveis = criarNovaInstanciaDeBensImoveis(evento, TipoOperacaoBensImoveis.DESINCORPORACAO_BENS_IMOVEIS, historico, exercicio);
                    bensImoveisFacade.salvarNovo(bensImoveis, simularContabilizacao);
                    break;
                case INTANGIVEIS:
                    BensIntangiveis bensIntangiveis = criarNovaInstanciaDeBensIntangiveis(evento, TipoOperacaoBensIntangiveis.DESINCORPORACAO_BENS_INTANGIVEIS, historico, exercicio);
                    bensIntangiveisFacade.salvarNovo(bensIntangiveis);
                    break;
                default:
                    throw new ExcecaoNegocioGenerica("Não é possível desincorporar um bem do tipo " + evento.getTipoBemResultante() + ".");
            }
        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void simularAndContabilizarBemMovel(Boolean simularContabilizacao, BensMoveis bensMoveis) {
        if (simularContabilizacao) {
            bensMoveisFacade.simularContabilizacaoIntegracao(bensMoveis);
        } else {
            bensMoveisFacade.salvarAndContabilizarIntegracaoNaoValidandoSaldoGrupo(bensMoveis, simularContabilizacao);
        }
    }

    private void simularAndContabilizarBemImovel(Boolean simularContabilizacao, BensImoveis bensMoveis) {
        if (simularContabilizacao) {
            bensImoveisFacade.simularContabilizacao(bensMoveis);
        } else {
            bensImoveisFacade.contabilizarBemImovel(bensMoveis, simularContabilizacao);
        }
    }

    private void transferirBemOriginal(EventoBemIncorporavelComContabil evento, String historico) throws ExcecaoNegocioGenerica {
        if (evento.getTipoBemResultante() != null) {
            try {
                switch (evento.getTipoBemResultante()) {
                    case MOVEIS:
                        TransfBensMoveis transfBensMoveis = criarNovaInstanciaDeTransfBensMoveis(evento, historico);
                        if (BigDecimal.ZERO.compareTo(transfBensMoveis.getValor()) < 0) {
                            transfBensMoveisFacade.salvarNovo(transfBensMoveis);
                        } else {
                            throw new ExcecaoNegocioGenerica("Valor da transferência deve ser maior que zero");
                        }
                        break;
                    case IMOVEIS:
                        transfBensImoveisFacade.salvarNovo(criarNovaInstanciaDeTransfBensImoveis(evento, historico));
                        break;
                    case INTANGIVEIS:
                        transfBensIntangiveisFacade.salvarNovo(criarNovaInstanciaDeTransfBensIntangiveis(evento, historico));
                        break;
                    default:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir um bem do tipo " + evento.getTipoBemResultante() + ".");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }

        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void transferirDepreciacao(EventoBemIncorporavelComContabil evento, String historico) throws ExcecaoNegocioGenerica {
        if (evento.getTipoBemResultante() != null) {
            try {
                switch (evento.getTipoBemResultante()) {
                    case MOVEIS:
                        TransfBensMoveis transfBensMoveis = criarNovaInstanciaDeTransfBensMoveisDepreciacao(evento, historico);
                        if (BigDecimal.ZERO.compareTo(transfBensMoveis.getValor()) < 0) {
                            transfBensMoveisFacade.salvarNovo(transfBensMoveis);
                        }
                        break;
                    case IMOVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    case INTANGIVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    default:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }

        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void transferirAmortizacao(EventoBemIncorporavelComContabil evento, String historico) throws ExcecaoNegocioGenerica {
        if (evento.getTipoBemResultante() != null) {
            try {
                switch (evento.getTipoBemResultante()) {
                    case MOVEIS:
                        TransfBensMoveis transfBensMoveis = criarNovaInstanciaDeTransfBensMoveisAmortizacao(evento, historico);
                        if (BigDecimal.ZERO.compareTo(transfBensMoveis.getValor()) < 0) {
                            transfBensMoveisFacade.salvarNovo(transfBensMoveis);
                        }
                        break;
                    case IMOVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    case INTANGIVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    default:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }

        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void transferirExaustao(EventoBemIncorporavelComContabil evento, String historico) throws ExcecaoNegocioGenerica {
        if (evento.getTipoBemResultante() != null) {
            try {
                switch (evento.getTipoBemResultante()) {
                    case MOVEIS:
                        TransfBensMoveis transfBensMoveis = criarNovaInstanciaDeTransfBensMoveisExaustao(evento, historico);
                        if (BigDecimal.ZERO.compareTo(transfBensMoveis.getValor()) < 0) {
                            transfBensMoveisFacade.salvarNovo(transfBensMoveis);
                        }
                        break;
                    case IMOVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    case INTANGIVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    default:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }

        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void transferirReducao(EventoBemIncorporavelComContabil evento, String historico) throws ExcecaoNegocioGenerica {
        if (evento.getTipoBemResultante() != null) {
            try {
                switch (evento.getTipoBemResultante()) {
                    case MOVEIS:
                        TransfBensMoveis transfBensMoveis = criarNovaInstanciaDeTransfBensMoveisReducao(evento, historico);
                        if (BigDecimal.ZERO.compareTo(transfBensMoveis.getValor()) < 0) {
                            transfBensMoveisFacade.salvarNovo(transfBensMoveis);
                        }
                        break;
                    case IMOVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    case INTANGIVEIS:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                    default:
                        throw new ExcecaoNegocioGenerica("Não é possível transferir depreciação de um bem do tipo " + evento.getTipoBemResultante() + ".");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }

        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }


    private void reavaliarBemAumentativa(EventoBemIncorporavelComContabil evento, String historico) throws ExcecaoNegocioGenerica {
        if (evento.getTipoBemResultante() != null) {
            try {
                switch (evento.getTipoBemResultante()) {
                    case MOVEIS:
                        bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.REAVALIACAO_BENS_MOVEIS_AUMENTATIVA, historico), false);
                        break;
                    case IMOVEIS:
                        bensImoveisFacade.contabilizarBemImovel(criarNovaInstanciaDeBensImoveis(evento, TipoOperacaoBensImoveis.REAVALIACAO_BENS_IMOVEIS_AUMENTATIVA, historico));
                        break;
                    case INTANGIVEIS:
                        break;
                    default:
                        throw new ExcecaoNegocioGenerica("Não é possível realizar uma reavaliação aumentativa de um bem do tipo " + evento.getTipoBemResultante() + ".");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }

        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void reavaliarBemDiminutiva(EventoBemIncorporavelComContabil evento, String historico) throws ExcecaoNegocioGenerica {
        if (evento.getTipoBemResultante() != null) {
            try {
                switch (evento.getTipoBemResultante()) {
                    case MOVEIS:
                        bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.REAVALIACAO_BENS_MOVEIS_DIMINUTIVA, historico), false);
                        break;
                    case IMOVEIS:
                        bensImoveisFacade.contabilizarBemImovel(criarNovaInstanciaDeBensImoveis(evento, TipoOperacaoBensImoveis.REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA, historico));
                        break;
                    case INTANGIVEIS:
                        break;
                    default:
                        throw new ExcecaoNegocioGenerica("Não é possível realizar uma reavaliação diminutiva de um bem do tipo " + evento.getTipoBemResultante() + ".");
                }
            } catch (ExcecaoNegocioGenerica ex) {
                throw new ExcecaoNegocioGenerica(ex.getMessage());
            }

        } else {
            throw new ExcecaoNegocioGenerica("O tipo do bem não está definido no grupo bem '" + evento.getGrupoBemResultante() + "'.");
        }
    }

    private void alienacaoDeBem(EventoBemIncorporavelComContabil evento, String historico, Boolean simularContabilizacao, Exercicio exercicio) {
        if (evento.getTipoBemResultante() == null) {
            throw new ExcecaoNegocioGenerica("Não é possível alienar o valor de um bem com seu tipo nulo.");
        }
        switch (evento.getTipoBemResultante()) {
            case MOVEIS:
                BensMoveis bensMoveis = criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.ALIENACAO_BENS_MOVEIS, historico, exercicio);
                simularAndContabilizarBemMovel(simularContabilizacao, bensMoveis);
                break;
            case IMOVEIS:
                BensImoveis bensImoveis = criarNovaInstanciaDeBensImoveis(evento, TipoOperacaoBensImoveis.ALIENACAO_BENS_IMOVEIS, historico, exercicio);
                bensImoveisFacade.contabilizarBemImovel(bensImoveis, simularContabilizacao);
                break;
            case INTANGIVEIS:
                BensIntangiveis bensIntangiveis = criarNovaInstanciaDeBensIntangiveis(evento, TipoOperacaoBensIntangiveis.ALIENACAO_BENS_INTANGIVEIS, historico, exercicio);
                bensIntangiveisFacade.contabilizarBensIntangiveis(bensIntangiveis);
                break;
            default:
                throw new ExcecaoNegocioGenerica("Não é possível alienar um bem do tipo " + evento.getTipoBemResultante() + ".");
        }
    }

    public void contabilizarAjusteBemMovel(String historico, Boolean aumentativo, TipoAjusteBemMovel tipoAjusteBemMovel,
                                           Boolean simulacao, AssistenteMovimentacaoBens assistente,
                                           OperacaoAjusteBemMovel operacaoAjusteBemMovel) throws ExcecaoNegocioGenerica {
        try {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso(simulacao ? "Simulando Contabilização " : "Contabilizando" + " Ajuste de Bens - " + tipoAjusteBemMovel.getDescricao() + "...");

            List<AgrupadorBem> bensAgrupadors = agruparBens(assistente.getBensMovimentadosVo(), assistente.getDataLancamento());
            assistente.setTotal(bensAgrupadors.size());

            for (AgrupadorBem bemAgrupado : bensAgrupadors) {
                switch (tipoAjusteBemMovel) {
                    case ORIGINAL:
                        contabilziarAjusteBemOriginal(bemAgrupado, historico, aumentativo, simulacao, operacaoAjusteBemMovel);
                        break;
                    case DEPRECIACAO:
                        contabilizarAjusteBemDepreciacao(bemAgrupado, historico, aumentativo, simulacao);
                        break;
                    case AMORTIZACAO:
                        contabilizarAjusteBemAmortizacao(bemAgrupado, historico, aumentativo, simulacao);
                    default:
                }
                assistente.conta();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void contabilizarAjusteBemAmortizacao(EventoBemIncorporavelComContabil evento, String historico, Boolean aumentativo, Boolean simulacao) {
        if (aumentativo) {
            bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AMORTIZACAO_AUMENTATIVO, historico), simulacao);
        } else {
            bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AMORTIZACAO_DIMINUTIVO, historico), simulacao);
        }
    }

    private void contabilizarAjusteBemDepreciacao(EventoBemIncorporavelComContabil evento, String historico, Boolean aumentativo, Boolean simulacao) {
        if (aumentativo) {
            bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DEPRECIACAO_AUMENTATIVO, historico), simulacao);
        } else {
            bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DEPRECIACAO_DIMINUTIVO, historico), simulacao);
        }
    }

    private void contabilziarAjusteBemOriginal(EventoBemIncorporavelComContabil evento, String historico, Boolean aumentativo, Boolean simulacao, OperacaoAjusteBemMovel operacaoAjusteBemMovel) {
        if (aumentativo) {
            if (OperacaoAjusteBemMovel.AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO.equals(operacaoAjusteBemMovel)) {
                bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AUMENTATIVO, historico), simulacao);

            } else if (OperacaoAjusteBemMovel.AJUSTE_BEM_MOVEL_ORIGINAL_AUMENTATIVO_EMPRESA_PUBLICA.equals(operacaoAjusteBemMovel)) {
                bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_AUMENTATIVO_EMPRESA_PUBLICA, historico), simulacao);
            }
        } else {
            if (OperacaoAjusteBemMovel.AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO.equals(operacaoAjusteBemMovel)) {
                bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DIMINUTIVO, historico), simulacao);

            } else if (OperacaoAjusteBemMovel.AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO_EMPRESA_PUBLICA.equals(operacaoAjusteBemMovel)) {
                bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AJUSTE_BENS_MOVEIS_DIMINUTIVO_EMPRESA_PUBLICA, historico), simulacao);
            }
        }
    }

    public void transferirBens(List<? extends EventoBem> eventos, String historico, AssistenteMovimentacaoBens assistente) throws ExcecaoNegocioGenerica {
        try {
            Date dataAtual;
            if (assistente != null) {
                assistente.zerarContadoresProcesso();
                assistente.setDescricaoProcesso("Contabilizando Operação...");
                dataAtual = assistente.getDataLancamento();
            } else {
                dataAtual = sistemaFacade.getDataOperacao();
            }
            List<EventosAgrupados> eventosAgrupados = agruparListEventoBem(eventos, dataAtual);
            if (assistente != null) {
                assistente.setTotal(eventosAgrupados.size());
            }
            for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
                transferirBemOriginal(eventosAgrupado, historico);
                transferirDepreciacao(eventosAgrupado, historico);
                transferirAmortizacao(eventosAgrupado, historico);
                transferirExaustao(eventosAgrupado, historico);
                transferirReducao(eventosAgrupado, historico);
                if (assistente != null) {
                    assistente.conta();
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            if (assistente != null) {
                assistente.setMensagens(Lists.newArrayList(ex.getMessage()));
            }
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void transferirGrupoBens(List<? extends EventoBem> eventos, AssistenteMovimentacaoBens assistente) throws ExcecaoNegocioGenerica {
        try {
            assistente.configurarInicializacao("Contabilizando Transferência Grupo Patrimonial...", 0);
            EfetivacaoTransferenciaGrupoBem efetivacao = (EfetivacaoTransferenciaGrupoBem) assistente.getSelecionado();
            String historico = efetivacao.getHistoricoRazao();
            GrupoBem grupoBemOrigem = efetivacao.getSolicitacao().getGrupoBemOrigem();

            List<EventosAgrupados> eventosAgrupados = agruparListEventoBem(eventos, assistente.getDataLancamento(), grupoBemOrigem);
            assistente.setTotal(eventosAgrupados.size());

            for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
                transferirBemOriginal(eventosAgrupado, historico);
                transferirDepreciacao(eventosAgrupado, historico);
                transferirAmortizacao(eventosAgrupado, historico);
                transferirReducao(eventosAgrupado, historico);
                assistente.conta();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            assistente.setMensagens(Lists.newArrayList(ex.getMessage()));
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void reavaliarBens(List<? extends EventoBem> eventos, String historico, Boolean aumentativa) throws ExcecaoNegocioGenerica {
        try {
            List<EventosAgrupados> eventosAgrupados = agruparListEventoBem(eventos, sistemaFacade.getDataOperacao());
            for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
                if (aumentativa) {
                    reavaliarBemAumentativa(eventosAgrupado, historico);
                } else {
                    reavaliarBemDiminutiva(eventosAgrupado, historico);
                }
            }
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void desincorporarBens(List<? extends EventoBem> eventos, String historico, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        EfetivacaoBaixaPatrimonial efetivacaoBaixa = (EfetivacaoBaixaPatrimonial) assistente.getSelecionado();
        assistente.setDescricaoProcesso(assistente.getSimularContabilizacao() ? " Simulando Desincorporação da Solicitação de Baixa..." : " Contabilizando Desincorporação da Solicitação de Baixa...");
        List<EventosAgrupados> eventosAgrupados = agruparListEventoBem(eventos, assistente.getDataLancamento());

        assistente.setTotal(eventosAgrupados.size());
        for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
            try {
                desincorporarBem(eventosAgrupado, historico, assistente.getSimularContabilizacao(), assistente.getExercicio());
            } catch (Exception ex) {
                String erro = "Desincorporar Bens: " + ex.getMessage();
                LogErroEfetivacaoBaixaBem logErro = new LogErroEfetivacaoBaixaBem(efetivacaoBaixa, eventosAgrupado.getBem(), erro);
                assistente.getLogErrosEfetivacaoBaixa().add(logErro);
                continue;
            }
            assistente.conta();
        }
    }

    public void incorporarBens(List<? extends EventoBem> eventos, String historico) throws ExcecaoNegocioGenerica {
        List<EventosAgrupados> eventosAgrupados = agruparListEventoBem(eventos, sistemaFacade.getDataOperacao());
        for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
            incorporarBem(eventosAgrupado, historico);
        }
    }

    public void alienarBens(List<LeilaoAlienacaoLoteBem> eventos, AssistenteMovimentacaoBens assistente) {

        EfetivacaoBaixaPatrimonial selecionado = (EfetivacaoBaixaPatrimonial) assistente.getSelecionado();
        String historico = "";
        List<EventosAgrupados> eventosAgrupados = new ArrayList<>();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Agrupando Eventos Alienação...");

        for (LeilaoAlienacaoLoteBem itemLeilao : eventos) {
            LeilaoAlienacaoLote leilaoAlienacaoLote = itemLeilao.getLeilaoAlienacaoLote();
            historico = selecionado.getCodigo() != null ? leilaoAlienacaoLote.getHistoricoRazaoAlienacao(selecionado.getCodigo().toString(), leilaoAlienacaoLote.getLeilaoAlienacao().getCodigo().toString()) : "";
            itemLeilao.setValorDoLancamento(itemLeilao.getValorProporcionalArrematado());

            EventosAgrupados eventoAgrupado = new EventosAgrupados(itemLeilao, assistente.getDataLancamento());
            if (eventosAgrupados.contains(eventoAgrupado)) {
                eventosAgrupados.get(eventosAgrupados.indexOf(eventoAgrupado)).adicionarValor(itemLeilao);
            } else {
                eventoAgrupado.adicionarValor(itemLeilao);
                eventosAgrupados.add(eventoAgrupado);
            }
        }
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso(assistente.getSimularContabilizacao() ? " Simulando Contabilização Baixa por Alienação de Bens..." : " Contabilizando Baixa por Alienação de Bens...");
        assistente.setTotal(eventosAgrupados.size());
        for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
            try {
                alienacaoDeBem(eventosAgrupado, historico, assistente.getSimularContabilizacao(), assistente.getExercicio());
            } catch (Exception ex) {
                String erro = "Alienar Bens: " + ex.getMessage();
                LogErroEfetivacaoBaixaBem logErro = new LogErroEfetivacaoBaixaBem(selecionado, eventosAgrupado.getBem(), erro);
                assistente.getLogErrosEfetivacaoBaixa().add(logErro);
                continue;
            }
            assistente.conta();
        }

    }

    public void registrarGanhosDosBens(List<? extends EventoBem> eventos, String historico, AssistenteMovimentacaoBens assistente) throws ExcecaoNegocioGenerica {
        EfetivacaoBaixaPatrimonial selecionado = (EfetivacaoBaixaPatrimonial) assistente.getSelecionado();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso(assistente.getSimularContabilizacao() ? "Simulando Registro de Ganhos do Bem..." : " Contabilizando Registro de Ganhos do Bem...");
        List<EventosAgrupados> eventosAgrupados = agruparListEventoBem(eventos, assistente.getDataLancamento());
        assistente.setTotal(eventosAgrupados.size());
        for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
            try {
                registrarGanho(eventosAgrupado, historico, assistente.getSimularContabilizacao(), assistente.getExercicio());
            } catch (Exception ex) {
                if (assistente.getSimularContabilizacao()) {
                    String erro = "Registro Ganho Patrimonial: " + ex.getMessage();
                    LogErroEfetivacaoBaixaBem logErro = new LogErroEfetivacaoBaixaBem(selecionado, eventosAgrupado.getBem(), erro);
                    assistente.getLogErrosEfetivacaoBaixa().add(logErro);
                    continue;
                }
            }
            assistente.conta();
        }
    }

    public void registrarPerdasDosBens(List<? extends EventoBem> eventos, String historico, AssistenteMovimentacaoBens assistente) throws ExcecaoNegocioGenerica {
        EfetivacaoBaixaPatrimonial selecionado = (EfetivacaoBaixaPatrimonial) assistente.getSelecionado();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso(assistente.getSimularContabilizacao() ? "Simulando Registro de Perdas do Bem..." : " Contabilizando Registro de Perdas do Bem...");
        List<EventosAgrupados> eventosAgrupados = agruparListEventoBem(eventos, assistente.getDataLancamento());
        assistente.setTotal(eventosAgrupados.size());
        for (EventosAgrupados eventosAgrupado : eventosAgrupados) {
            try {
                registrarPerda(eventosAgrupado, historico, assistente.getSimularContabilizacao(), assistente.getExercicio());
            } catch (Exception ex) {
                if (assistente.getSimularContabilizacao()) {
                    String erro = "Registro Perda Patrimonial: " + ex.getMessage();
                    LogErroEfetivacaoBaixaBem logErro = new LogErroEfetivacaoBaixaBem(selecionado, eventosAgrupado.getBem(), erro);
                    assistente.getLogErrosEfetivacaoBaixa().add(logErro);
                    continue;
                }
            }
            assistente.conta();
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reduzirValorDoBemEstorno(List<ReducaoValorBemEstornoContabil> associacoes, EventoBemIncorporavelComContabil evento, TipoReducaoValorBem tipoReducaoValorBem, TipoBem tipoBem, String historico, boolean simulacao) {
        try {
            if (tipoBem == null) {
                throw new ExcecaoNegocioGenerica("Não é possível reduzir o valor do bem , pois o tipo do bem do grupo vinculado ao mesmo encontra-se nulo.");
            }
            switch (tipoBem) {
                case MOVEIS:
                    reduzirValorDoBemMovelEstorno(associacoes, evento, tipoReducaoValorBem, historico, simulacao);
                    break;
                case IMOVEIS:
                    reduzirValorDoBemImovel(evento, tipoReducaoValorBem, historico, simulacao);
                    break;
                default:
                    throw new ExcecaoNegocioGenerica("Não é possível reduzir o valor de um bem do tipo " + evento.getTipoBemResultante() + ".");
            }
        } catch (ExcecaoNegocioGenerica eng) {
            throw new ExcecaoNegocioGenerica(eng.getMessage());
        }
    }

    private void reduzirValorDoBemMovelEstorno(List<ReducaoValorBemEstornoContabil> associacoes, EventoBemIncorporavelComContabil evento,
                                               TipoReducaoValorBem tipoReducaoValorBem, String historico, boolean simulacao) {
        switch (tipoReducaoValorBem) {
            case AMORTIZACAO:
                bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AMORTIZACAO_BENS_MOVEIS, historico), simulacao);
                break;
            case DEPRECIACAO:
                BensMoveis bensMoveis = criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.DEPRECIACAO_BENS_MOVEIS, historico);
                bensMoveisFacade.salvarContabilizandoBemMovelIntegracaoReducaoValorEstorno(associacoes, bensMoveis, simulacao);
                break;
            case EXAUSTAO:
                bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.EXAUSTAO_BENS_MOVEIS, historico), simulacao);
                break;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void reduzirValorDoBem(List<ReducaoValorBemContabil> associacoes, EventoBemIncorporavelComContabil evento, TipoReducaoValorBem tipoReducaoValorBem, TipoBem tipoBem, String historico, boolean simulacao) {
        try {
            if (tipoBem == null) {
                throw new ExcecaoNegocioGenerica("Não é possível reduzir o valor do bem , pois o tipo do bem do grupo vinculado ao mesmo encontra-se nulo.");
            }
            switch (tipoBem) {
                case MOVEIS:
                    reduzirValorDoBemMovel(associacoes, evento, tipoReducaoValorBem, historico, simulacao);
                    break;
                case IMOVEIS:
                    reduzirValorDoBemImovel(evento, tipoReducaoValorBem, historico, simulacao);
                    break;
                case INTANGIVEIS:
                    //reduzirValorDoBemIntangivel(evento, historico);
                    break;
                default:
                    throw new ExcecaoNegocioGenerica("Não é possível reduzir o valor de um bem do tipo " + evento.getTipoBemResultante() + ".");
            }
        } catch (ExcecaoNegocioGenerica eng) {
            throw new ExcecaoNegocioGenerica(eng.getMessage());
        }
    }

    private void reduzirValorDoBemMovel(List<ReducaoValorBemContabil> associacoes, EventoBemIncorporavelComContabil evento,
                                        TipoReducaoValorBem tipoReducaoValorBem, String historico, boolean simulacao) {
        try {
            switch (tipoReducaoValorBem) {
                case AMORTIZACAO:
                    bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.AMORTIZACAO_BENS_MOVEIS, historico), simulacao);
                    break;
                case DEPRECIACAO:
                    BensMoveis bensMoveis = criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.DEPRECIACAO_BENS_MOVEIS, historico);
                    bensMoveisFacade.salvarContabilizandoBemMovelIntegracaoReducaoValorBemNormal(associacoes, bensMoveis, simulacao);
                    break;
                case EXAUSTAO:
                    bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.EXAUSTAO_BENS_MOVEIS, historico), simulacao);
                    break;
            }
        } catch (ExcecaoNegocioGenerica eng) {
            throw new ExcecaoNegocioGenerica(eng.getMessage());
        }
    }

    private void reduzirValorDoBemImovel(EventoBemIncorporavelComContabil redutor, TipoReducaoValorBem tipoReducaoValorBem, String historico, boolean simulacao) {
        try {
            switch (tipoReducaoValorBem) {
                case AMORTIZACAO:
                    bensImoveisFacade.contabilizarBemImovel(criarNovaInstanciaDeBensImoveis(redutor, TipoOperacaoBensImoveis.AMORTIZACAO_BENS_IMOVEIS, historico), simulacao);
                    break;
                case DEPRECIACAO:
                    bensImoveisFacade.contabilizarBemImovel(criarNovaInstanciaDeBensImoveis(redutor, TipoOperacaoBensImoveis.DEPRECIACAO_BENS_IMOVEIS, historico), simulacao);
                    break;
                case EXAUSTAO:
                    bensImoveisFacade.contabilizarBemImovel(criarNovaInstanciaDeBensImoveis(redutor, TipoOperacaoBensImoveis.EXAUSTAO_BENS_IMOVEIS, historico), simulacao);
                    break;
            }
        } catch (ExcecaoNegocioGenerica eng) {
            throw new ExcecaoNegocioGenerica(eng.getMessage());

        }
    }

    public void registrarGanho(EventoBemIncorporavelComContabil evento, String historico, Boolean simularContabilizacao, Exercicio exercicio) {
        if (evento.getTipoBemResultante() == null) {
            throw new ExcecaoNegocioGenerica("Não é possível registrar o ganho/perda de um bem com seu tipo nulo.");
        }

        switch (evento.getTipoBemResultante()) {
            case MOVEIS:
                BensMoveis bensMoveis = criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.GANHOS_ALIENACAO_BENS_MOVEIS, historico, exercicio);
                simularAndContabilizarBemMovel(simularContabilizacao, bensMoveis);
                break;
            case IMOVEIS:
                BensImoveis bensImoveis = criarNovaInstanciaDeBensImoveis(evento, TipoOperacaoBensImoveis.GANHOS_ALIENACAO_BENS_IMOVEIS, historico, exercicio);
                bensImoveisFacade.contabilizarBemImovel(bensImoveis, simularContabilizacao);
                break;
            case INTANGIVEIS:
                BensIntangiveis bensIntangiveis = criarNovaInstanciaDeBensIntangiveis(evento, TipoOperacaoBensIntangiveis.GANHOS_ALIENACAO_BENS_INTANGIVEIS, historico, exercicio);
                bensIntangiveisFacade.contabilizarBensIntangiveis(bensIntangiveis);
                break;
            default:
                throw new ExcecaoNegocioGenerica("Não é possível registrar o ganho/perda de um bem do tipo " + evento.getTipoBemResultante() + ".");
        }
    }

    public void registrarPerda(EventoBemIncorporavelComContabil evento, String historico, Boolean simularContabilizacao, Exercicio exercicio) {
        if (evento.getTipoBemResultante() == null) {
            throw new ExcecaoNegocioGenerica("Não é possível registrar o ganho/perda de um bem com seu tipo nulo.");
        }

        switch (evento.getTipoBemResultante()) {
            case MOVEIS:
                BensMoveis bensMoveis = criarNovaInstanciaDeBensMoveis(evento, TipoOperacaoBensMoveis.PERDAS_ALIENACAO_BENS_MOVEIS, historico, exercicio);
                simularAndContabilizarBemMovel(simularContabilizacao, bensMoveis);
                break;
            case IMOVEIS:
                BensImoveis bensImoveis = criarNovaInstanciaDeBensImoveis(evento, TipoOperacaoBensImoveis.PERDAS_ALIENACAO_BENS_IMOVEIS, historico, exercicio);
                bensImoveisFacade.contabilizarBemImovel(bensImoveis, simularContabilizacao);
                break;
            case INTANGIVEIS:
                BensIntangiveis bensIntangiveis = criarNovaInstanciaDeBensIntangiveis(evento, TipoOperacaoBensIntangiveis.PERDAS_ALIENACAO_BENS_INTANGIVEIS, historico, exercicio);
                bensIntangiveisFacade.contabilizarBensIntangiveis(bensIntangiveis);
                break;
            default:
                throw new ExcecaoNegocioGenerica("Não é possível registrar o ganho/perda de um bem do tipo " + evento.getTipoBemResultante() + ".");
        }
    }

    public void ajustarPerda(EventoBem eventoBem, String historico) {
        if (eventoBem.getTipoBemResultante() == null) {
            throw new ExcecaoNegocioGenerica("Não é possível reduzir o valor de um bem com seu tipo nulo.");
        }
        switch (eventoBem.getTipoBemResultante()) {
            case MOVEIS:
                bensMoveisFacade.salvarAndContabilizarBemMovelIntegracao(criarNovaInstanciaDeBensMoveis(eventoBem, TipoOperacaoBensMoveis.REDUCAO_VALOR_RECUPERAVEL_BENS_MOVEIS, historico), false);
                break;
            case IMOVEIS:
                bensImoveisFacade.contabilizarBemImovel(criarNovaInstanciaDeBensImoveis(eventoBem, TipoOperacaoBensImoveis.REDUCAO_VALOR_RECUPERAVEL_BENS_IMOVEIS, historico));
                break;
            case INTANGIVEIS:
                bensIntangiveisFacade.contabilizarBensIntangiveis(criarNovaInstanciaDeBensIntangiveis(eventoBem, TipoOperacaoBensIntangiveis.REDUCAO_VALOR_RECUPERAVEL_BENS_INTANGIVEIS, historico));
                break;
            default:
                throw new ExcecaoNegocioGenerica("Não é possível reduzir o valor de um bem do tipo " + eventoBem.getTipoBemResultante() + ".");
        }
    }


    public void apurarValorLiquidoContabil(EfetivacaoBaixaPatrimonial selecionado, BensMoveis bemMovel, AssistenteMovimentacaoBens assistente) {
        try {
            simularAndContabilizarBemMovel(assistente.getSimularContabilizacao(), bemMovel);
        } catch (Exception ex) {
            String erro = "Apuração do Valor Líquido Contábil: " + ex.getMessage();
            LogErroEfetivacaoBaixaBem logErro = new LogErroEfetivacaoBaixaBem(selecionado, bemMovel.getBem(), erro);
            assistente.getLogErrosEfetivacaoBaixa().add(logErro);
        }
    }


    public void contabilizarApuracaoValorLiquidoContabilBensImoveis(EfetivacaoBaixaPatrimonial selecionado, BensImoveis bensImoveis, AssistenteMovimentacaoBens assistente) {
        try {
            simularAndContabilizarBemImovel(assistente.getSimularContabilizacao(), bensImoveis);
        } catch (Exception ex) {
            String erro = "Apuração do Valor Líquido Contábil: " + ex.getMessage();
            LogErroEfetivacaoBaixaBem logErro = new LogErroEfetivacaoBaixaBem(selecionado, bensImoveis.getBem(), erro);
            assistente.getLogErrosEfetivacaoBaixa().add(logErro);
        }
    }

    public BensMoveis criarNovaInstanciaDeBensMoveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensMoveis tipoOperacaoBensMoveis, String historico, Exercicio exercicio) {
        return criarObjetoBemMovel(evento, tipoOperacaoBensMoveis, historico, exercicio);
    }

    private BensMoveis criarNovaInstanciaDeBensMoveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensMoveis tipoOperacaoBensMoveis, String historico) {
        return criarObjetoBemMovel(evento, tipoOperacaoBensMoveis, historico, null);
    }

    private BensMoveis criarObjetoBemMovel(EventoBemIncorporavelComContabil evento, TipoOperacaoBensMoveis tipoOperacaoBensMoveis, String historico, Exercicio exercicio) {
        BensMoveis bensMoveis = new BensMoveis();
        bensMoveis.setDataBensMoveis(evento.getDataDoLancamento());
        bensMoveis.setExercicio(exercicio != null ? exercicio : exercicioFacade.getExercicioPorAno(DataUtil.getAno(evento.getDataDoLancamento())));
        bensMoveis.setTipoLancamento(evento.ehEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        bensMoveis.setUnidadeOrganizacional(evento.getUnidadeOrcamentariaResultante());
        bensMoveis.setTipoOperacaoBemEstoque(tipoOperacaoBensMoveis);
        bensMoveis.setTipoGrupo(evento.getTipoGrupoResultante());
        bensMoveis.setGrupoBem(evento.getGrupoBemResultante());
        bensMoveis.setHistorico(historico);
        bensMoveis.setValor(evento.getValorDoLancamento());
        return bensMoveis;
    }

    public BensImoveis criarNovaInstanciaDeBensImoveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensImoveis tipoOperacaoBensImoveis, String historico, Exercicio exercicio) {
        return criarObjetoBemImovel(evento, tipoOperacaoBensImoveis, historico, exercicio);
    }

    private BensImoveis criarNovaInstanciaDeBensImoveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensImoveis tipoOperacaoBensImoveis, String historico) {
        return criarObjetoBemImovel(evento, tipoOperacaoBensImoveis, historico, null);
    }

    private BensImoveis criarObjetoBemImovel(EventoBemIncorporavelComContabil evento, TipoOperacaoBensImoveis tipoOperacaoBensImoveis, String historico, Exercicio exercicio) {
        BensImoveis bensImoveis = new BensImoveis();
        bensImoveis.setDataBem(evento.getDataDoLancamento());
        bensImoveis.setExercicio(exercicio != null ? exercicio : exercicioFacade.getExercicioPorAno(DataUtil.getAno(evento.getDataDoLancamento())));
        bensImoveis.setTipoLancamento(evento.ehEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        bensImoveis.setUnidadeOrganizacional(evento.getUnidadeOrcamentariaResultante());
        bensImoveis.setTipoOperacaoBemEstoque(tipoOperacaoBensImoveis);
        bensImoveis.setTipoGrupo(evento.getTipoGrupoResultante());
        bensImoveis.setGrupoBem(evento.getGrupoBemResultante());
        bensImoveis.setHistorico(historico);
        bensImoveis.setValor(evento.getValorDoLancamento());
        return bensImoveis;
    }

    private BensIntangiveis criarNovaInstanciaDeBensIntangiveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis, String historico) {
        return criarObjetoBemIntangivel(evento, tipoOperacaoBensIntangiveis, historico, null);
    }

    private BensIntangiveis criarNovaInstanciaDeBensIntangiveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis, String historico, Exercicio exercicio) {
        return criarObjetoBemIntangivel(evento, tipoOperacaoBensIntangiveis, historico, exercicio);
    }

    private BensIntangiveis criarObjetoBemIntangivel(EventoBemIncorporavelComContabil evento, TipoOperacaoBensIntangiveis tipoOperacaoBensIntangiveis, String historico, Exercicio exercicio) {
        BensIntangiveis bensIntangiveis = new BensIntangiveis();
        bensIntangiveis.setDataBem(evento.getDataDoLancamento());
        bensIntangiveis.setExercicio(exercicio != null ? exercicio : exercicioFacade.getExercicioPorAno(DataUtil.getAno(evento.getDataDoLancamento())));
        bensIntangiveis.setTipoLancamento(evento.ehEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        bensIntangiveis.setUnidadeOrganizacional(evento.getUnidadeOrcamentariaResultante());
        bensIntangiveis.setTipoOperacaoBemEstoque(tipoOperacaoBensIntangiveis);
        bensIntangiveis.setTipoGrupo(evento.getTipoGrupoResultante());
        bensIntangiveis.setGrupoBem(evento.getGrupoBemResultante());
        bensIntangiveis.setHistorico(historico);
        bensIntangiveis.setValor(evento.getValorDoLancamento());
        return bensIntangiveis;
    }

    private TransfBensMoveis criarNovaInstanciaDeTransfBensMoveis(EventoBemIncorporavelComContabil evento, String historico) {
        return criarNovaInstanciaDeTransfBensMoveis(evento,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_CONCEDIDA,
            TipoOperacaoBensMoveis.TRANFERENCIA_BENS_MOVEIS_RECEBIDA, evento.getValorOriginal(), historico);
    }

    private TransfBensMoveis criarNovaInstanciaDeTransfBensMoveisDepreciacao(EventoBemIncorporavelComContabil evento, String historico) {
        return criarNovaInstanciaDeTransfBensMoveis(evento,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA, evento.getValorDepreciacao(), historico);
    }

    private TransfBensMoveis criarNovaInstanciaDeTransfBensMoveisAmortizacao(EventoBemIncorporavelComContabil evento, String historico) {
        return criarNovaInstanciaDeTransfBensMoveis(evento,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA, evento.getValorAmortizacao(), historico);
    }

    private TransfBensMoveis criarNovaInstanciaDeTransfBensMoveisExaustao(EventoBemIncorporavelComContabil evento, String historico) {
        return criarNovaInstanciaDeTransfBensMoveis(evento,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA, evento.getValorExaustao(), historico);
    }

    private TransfBensMoveis criarNovaInstanciaDeTransfBensMoveisReducao(EventoBemIncorporavelComContabil evento, String historico) {
        return criarNovaInstanciaDeTransfBensMoveis(evento,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA,
            TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA, evento.getValorReducao(), historico);
    }

    private TransfBensMoveis criarNovaInstanciaDeTransfBensMoveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensMoveis concedida, TipoOperacaoBensMoveis recebida, BigDecimal valor, String historico) {
        UnidadeOrganizacional origem = null;
        UnidadeOrganizacional destino = null;
        if (evento.ehEstorno()) {
            origem = evento.getUnidadeOrcamentariaResultante();
            destino = evento.getUnidadeOrcamentariaInicial();
        } else {
            origem = evento.getUnidadeOrcamentariaInicial();
            destino = evento.getUnidadeOrcamentariaResultante();
        }
        TransfBensMoveis transfBensMoveis = new TransfBensMoveis();
        transfBensMoveis.setTipoLancamento(evento.ehEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        transfBensMoveis.setUnidadeOrigem(origem);
        transfBensMoveis.setUnidadeDestino(destino);
        transfBensMoveis.setTipoGrupoOrigem(evento.getTipoGrupoInicial());
        transfBensMoveis.setTipoGrupoDestino(evento.getTipoGrupoResultante());
        transfBensMoveis.setGrupoBemOrigem(evento.getGrupoBemInicial());
        transfBensMoveis.setGrupoBemDestino(evento.getGrupoBemResultante());
        transfBensMoveis.setHistorico(historico);
        transfBensMoveis.setDataTransferencia(evento.getDataDoLancamento());
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(evento.getDataDoLancamento()));
        transfBensMoveis.setExercicio(exercicio);
        transfBensMoveis.setOperacaoOrigem(concedida);
        transfBensMoveis.setOperacaoDestino(recebida);
        transfBensMoveis.setValor(valor);
        return transfBensMoveis;
    }

    private TransfBensImoveis criarNovaInstanciaDeTransfBensImoveis(EventoBemIncorporavelComContabil evento, String historico) {
        return criarNovaInstanciaDeTransfBensImoveis(evento, TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA, TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA, historico);
    }

    private TransfBensImoveis criarNovaInstanciaDeTransfBensImoveis(EventoBemIncorporavelComContabil evento, TipoOperacaoBensImoveis concedida, TipoOperacaoBensImoveis recebida, String historico) {
        TransfBensImoveis transfBensImoveis = new TransfBensImoveis();

        transfBensImoveis.setTipoLancamento(evento.ehEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        transfBensImoveis.setUnidadeOrigem(evento.getUnidadeOrcamentariaInicial());
        transfBensImoveis.setUnidadeDestino(evento.getUnidadeOrcamentariaResultante());
        transfBensImoveis.setTipoGrupoOrigem(evento.getTipoGrupoInicial());
        transfBensImoveis.setTipoGrupoDestino(evento.getTipoGrupoResultante());
        transfBensImoveis.setGrupoBemOrigem(evento.getGrupoBemInicial());
        transfBensImoveis.setGrupoBemDestino(evento.getGrupoBemResultante());
        transfBensImoveis.setHistorico(historico);
        transfBensImoveis.setValor(evento.getValorDoLancamento());
        transfBensImoveis.setDataTransferencia(evento.getDataDoLancamento());
        transfBensImoveis.setExercicio(exercicioFacade.getExercicioCorrente());
        transfBensImoveis.setOperacaoOrigem(concedida);
        transfBensImoveis.setOperacaoDestino(recebida);

        return transfBensImoveis;
    }

    private TransfBensIntangiveis criarNovaInstanciaDeTransfBensIntangiveis(EventoBemIncorporavelComContabil evento, String historico) {
        TransfBensIntangiveis transfBensIntangiveis = new TransfBensIntangiveis();

        transfBensIntangiveis.setTipoLancamento(evento.ehEstorno() ? TipoLancamento.ESTORNO : TipoLancamento.NORMAL);
        transfBensIntangiveis.setUnidadeOrigem(evento.getUnidadeOrcamentariaInicial());
        transfBensIntangiveis.setUnidadeDestino(evento.getUnidadeOrcamentariaResultante());
        transfBensIntangiveis.setTipoGrupoOrigem(evento.getTipoGrupoInicial());
        transfBensIntangiveis.setTipoGrupoDestino(evento.getTipoGrupoResultante());
        transfBensIntangiveis.setGrupoBemOrigem(evento.getGrupoBemInicial());
        transfBensIntangiveis.setGrupoBemDestino(evento.getGrupoBemResultante());
        transfBensIntangiveis.setHistorico(historico);
        transfBensIntangiveis.setValor(evento.getValorDoLancamento());
        transfBensIntangiveis.setDataTransferencia(evento.getDataDoLancamento());
        transfBensIntangiveis.setExercicio(exercicioFacade.getExercicioCorrente());
        transfBensIntangiveis.setOperacaoOrigem(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_CONCEDIDA);
        transfBensIntangiveis.setOperacaoDestino(TipoOperacaoBensIntangiveis.TRANSFERENCIA_BENS_INTANGIVEIS_RECEBIDA);

        return transfBensIntangiveis;
    }

    public static class EventosAgrupados<T extends EventoBem> implements EventoBemIncorporavelComContabil {

        private BigDecimal valorOperacao = BigDecimal.ZERO;
        private BigDecimal valorDepreciacao = BigDecimal.ZERO;
        private List<Bem> bens;
        private List<T> eventos;
        private BigDecimal valorAmortizacao = BigDecimal.ZERO;
        private BigDecimal valorExaustao = BigDecimal.ZERO;
        private BigDecimal valorReducao = BigDecimal.ZERO;
        private BigDecimal valorOriginal = BigDecimal.ZERO;
        private boolean ehEstorno;
        private Date dataOperacao;
        private EventoBem eventoBem;
        private GrupoBem grupoBem;

        public EventosAgrupados(EventoBem eventoBem, Date dataOperacao) {
            this.bens = Lists.newArrayList();
            this.eventos = Lists.newArrayList();
            this.dataOperacao = dataOperacao;
            this.eventoBem = eventoBem;
        }

        public EventosAgrupados(EventoBem eventoBem, Date dataOperacao, GrupoBem grupoBem) {
            this.bens = Lists.newArrayList();
            this.eventos = Lists.newArrayList();
            this.dataOperacao = dataOperacao;
            this.eventoBem = eventoBem;
            this.grupoBem = grupoBem;
        }

        public void adicionarValor(T eventoBem) {
            this.valorOperacao = this.valorOperacao.add(eventoBem.getValorDoLancamento() != null ? eventoBem.getValorDoLancamento() : BigDecimal.ZERO);
            this.bens.add(eventoBem.getBem());
            this.eventos.add(eventoBem);
            this.ehEstorno = eventoBem.ehEstorno();
            this.valorDepreciacao = this.valorDepreciacao.add(eventoBem.getValorDepreciacao());
            this.valorAmortizacao = this.valorAmortizacao.add(eventoBem.getValorAmortizacao());
            this.valorExaustao = this.valorExaustao.add(eventoBem.getValorExaustao());
            this.valorReducao = this.valorReducao.add(eventoBem.getValorReducao());
            this.valorOriginal = this.valorOriginal.add(eventoBem.getValorOriginal());
        }

        public void setEhEstorno(boolean ehEstorno) {
            this.ehEstorno = ehEstorno;
        }

        @Override
        public Boolean ehEstorno() {
            return this.ehEstorno;
        }

        @Override
        public UnidadeOrganizacional getUnidadeOrcamentariaInicial() {
            return this.eventoBem.getUnidadeOrcamentariaInicial();
        }

        @Override
        public UnidadeOrganizacional getUnidadeOrcamentariaResultante() {
            return this.eventoBem.getUnidadeOrcamentariaResultante();
        }

        @Override
        public UnidadeOrganizacional getUnidadeAdministrativaInicial() {
            return this.eventoBem.getUnidadeAdministrativaInicial();
        }

        @Override
        public UnidadeOrganizacional getUnidadeAdministrativaResultante() {
            return this.eventoBem.getUnidadeAdministrativaResultante();
        }

        @Override
        public TipoGrupo getTipoGrupoInicial() {
            return this.eventoBem.getTipoGrupoInicial();
        }

        @Override
        public TipoGrupo getTipoGrupoResultante() {
            return this.eventoBem.getTipoGrupoResultante();
        }

        @Override
        public GrupoBem getGrupoBemInicial() {
            return grupoBem != null ? grupoBem : this.eventoBem.getGrupoBemInicial();
        }

        @Override
        public GrupoBem getGrupoBemResultante() {
            return this.eventoBem.getGrupoBemResultante();
        }

        @Override
        public TipoBem getTipoBemResultante() {
            return this.eventoBem.getTipoBemResultante();
        }

        @Override
        public BigDecimal getValorDoLancamento() {
            return this.valorOperacao;
        }

        public void setValorDoLancamento(BigDecimal valorOperacao) {
            this.valorOperacao = valorOperacao;
        }

        @Override
        public Date getDataDoLancamento() {
            return this.dataOperacao;
        }

        @Override
        public BigDecimal getValorDepreciacao() {
            return this.valorDepreciacao;
        }

        @Override
        public BigDecimal getValorAmortizacao() {
            return valorAmortizacao;
        }

        @Override
        public BigDecimal getValorExaustao() {
            return valorExaustao;
        }

        @Override
        public BigDecimal getValorReducao() {
            return valorReducao;
        }

        @Override
        public BigDecimal getValorOriginal() {
            return valorOriginal;
        }

        public List<Bem> getBens() {
            return bens;
        }

        public void setBens(List<Bem> bens) {
            this.bens = bens;
        }

        @Override
        public Bem getBem() {
            return eventoBem.getBem();
        }

        public List<T> getEventos() {
            return eventos;
        }

        public void setEventos(List<T> eventos) {
            this.eventos = eventos;
        }

        public GrupoBem getGrupoBem() {
            return grupoBem;
        }

        public void setGrupoBem(GrupoBem grupoBem) {
            this.grupoBem = grupoBem;
        }

        @Override
        public boolean equals(Object obj) {
            boolean igual = false;
            EventoBemIncorporavelComContabil evento = (EventoBemIncorporavelComContabil) obj;
            if (evento == null) {
                return false;
            } else {
                if (TipoEventoBem.retornaTipoEventoTransferencia().contains(eventoBem.getTipoEventoBem())) {
                    if (this.getTipoBemResultante().equals(evento.getTipoBemResultante())
                        && this.getGrupoBemResultante().equals(evento.getGrupoBemResultante())
                        && this.getTipoGrupoResultante().equals(evento.getTipoGrupoResultante())
                        && this.getUnidadeAdministrativaInicial().getId().equals(evento.getUnidadeAdministrativaInicial().getId())
                        && this.getUnidadeAdministrativaResultante().getId().equals(evento.getUnidadeAdministrativaResultante().getId())
                        && this.getUnidadeOrcamentariaInicial().getId().equals(evento.getUnidadeOrcamentariaInicial().getId())
                        && this.getUnidadeOrcamentariaResultante().getId().equals(evento.getUnidadeOrcamentariaResultante().getId())) {
                        igual = true;
                    }
                } else if (TipoEventoBem.retornaTipoEventoReducaoValorBem().contains(eventoBem.getTipoEventoBem())) {
                    if (this.getTipoBemResultante().equals(evento.getTipoBemResultante())
                        && this.getGrupoBemResultante().equals(evento.getGrupoBemResultante())
                        && this.getTipoGrupoResultante().equals(evento.getTipoGrupoResultante())
                        && this.getUnidadeOrcamentariaResultante().equals(evento.getUnidadeOrcamentariaResultante())) {
                        igual = true;
                    }
                } else {
                    if (this.getTipoBemResultante().equals(evento.getTipoBemResultante())
                        && this.getGrupoBemResultante().equals(evento.getGrupoBemResultante())
                        && this.getTipoGrupoResultante().equals(evento.getTipoGrupoResultante())
                        && this.getUnidadeAdministrativaResultante().equals(evento.getUnidadeAdministrativaResultante())
                        && this.getUnidadeOrcamentariaResultante().equals(evento.getUnidadeOrcamentariaResultante())) {
                        igual = true;
                    }
                }
            }
            return igual;
        }

        @Override
        public String toString() {
            return "Tipo Bem: " + getTipoBemResultante() + " Grupo Bem: " + getGrupoBemResultante() +
                " Tipo Grupo: " + getTipoGrupoResultante() + " Unidade Adm: " + getUnidadeAdministrativaResultante() +
                " Unidade Orç: " + getUnidadeOrcamentariaResultante();
        }

    }

    public static class AgrupadorBem implements EventoBemIncorporavelComContabil {

        private List<Bem> bens;
        private BemVo bemVo;
        private Date dataOperacao;
        private BigDecimal valorLancamento;
        private BigDecimal valorOriginal;
        private BigDecimal valorDepreciacao;
        private BigDecimal valorAmortizacao;
        private BigDecimal valorExaustao;
        private BigDecimal valorAjuste;

        public AgrupadorBem(BemVo bemVo, Date dataOperacao) {
            this.bens = Lists.newArrayList();
            this.dataOperacao = dataOperacao;
            this.bemVo = bemVo;
            this.valorLancamento = BigDecimal.ZERO;
            this.valorOriginal = BigDecimal.ZERO;
            this.valorDepreciacao = BigDecimal.ZERO;
            this.valorAmortizacao = BigDecimal.ZERO;
            this.valorExaustao = BigDecimal.ZERO;
            this.valorAjuste = BigDecimal.ZERO;
        }

        public void adicionarValor(BemVo bemVo) {
            this.valorLancamento = this.valorLancamento.add(bemVo.getValorLancamento() != null ? bemVo.getValorLancamento() : BigDecimal.ZERO);
            this.valorDepreciacao = this.valorDepreciacao.add(bemVo.getEstadoResultante().getValorAcumuladoDaDepreciacao());
            this.valorOriginal = this.valorOriginal.add(bemVo.getEstadoResultante().getValorOriginal());
            this.valorAmortizacao = this.valorAmortizacao.add(bemVo.getEstadoResultante().getValorAcumuladoDaAmortizacao());
            this.valorExaustao = this.valorExaustao.add(bemVo.getEstadoResultante().getValorAcumuladoDaExaustao());
            this.valorAjuste = this.valorAjuste.add(bemVo.getEstadoResultante().getValorAcumuladoDeAjuste());
            this.bens.add(bemVo.getBem());
        }

        @Override
        public Boolean ehEstorno() {
            return bemVo.getTipoLancamento().isEstorno();
        }

        @Override
        public UnidadeOrganizacional getUnidadeOrcamentariaInicial() {
            return this.bemVo.getEstadoInicial().getDetentoraOrcamentaria();
        }

        @Override
        public UnidadeOrganizacional getUnidadeOrcamentariaResultante() {
            return this.bemVo.getEstadoResultante().getDetentoraOrcamentaria();
        }

        @Override
        public UnidadeOrganizacional getUnidadeAdministrativaInicial() {
            return this.bemVo.getEstadoInicial().getDetentoraAdministrativa();
        }

        @Override
        public UnidadeOrganizacional getUnidadeAdministrativaResultante() {
            return this.bemVo.getEstadoResultante().getDetentoraAdministrativa();
        }

        @Override
        public TipoGrupo getTipoGrupoInicial() {
            return this.bemVo.getEstadoInicial().getTipoGrupo();
        }

        @Override
        public TipoGrupo getTipoGrupoResultante() {
            return this.bemVo.getEstadoResultante().getTipoGrupo();
        }

        @Override
        public GrupoBem getGrupoBemInicial() {
            return this.bemVo.getEstadoInicial().getGrupoBem();
        }

        @Override
        public GrupoBem getGrupoBemResultante() {
            return this.bemVo.getEstadoResultante().getGrupoBem();
        }

        @Override
        public TipoBem getTipoBemResultante() {
            return this.bemVo.getEstadoResultante().getGrupoBem().getTipoBem();
        }

        @Override
        public BigDecimal getValorDoLancamento() {
            return this.valorLancamento;
        }

        public void setValorDoLancamento(BigDecimal valorLancamento) {
            this.valorLancamento = valorLancamento;
        }

        @Override
        public Date getDataDoLancamento() {
            return this.dataOperacao;
        }

        @Override
        public BigDecimal getValorDepreciacao() {
            return this.valorDepreciacao;
        }

        @Override
        public BigDecimal getValorAmortizacao() {
            return valorAmortizacao;
        }

        @Override
        public BigDecimal getValorExaustao() {
            return valorExaustao;
        }

        @Override
        public BigDecimal getValorReducao() {
            return valorAjuste;
        }

        @Override
        public BigDecimal getValorOriginal() {
            return valorOriginal;
        }

        public List<Bem> getBens() {
            return bens;
        }

        public void setBens(List<Bem> bens) {
            this.bens = bens;
        }

        @Override
        public Bem getBem() {
            return bemVo.getBem();
        }


        @Override
        public boolean equals(Object obj) {
            boolean igual = false;
            EventoBemIncorporavelComContabil evento = (EventoBemIncorporavelComContabil) obj;
            if (evento == null) {
                return false;
            } else {
                if (TipoEventoBem.retornaTipoEventoTransferencia().contains(bemVo.getTipoEventoBem())) {
                    if (this.getTipoBemResultante().equals(evento.getTipoBemResultante())
                        && this.getGrupoBemResultante().equals(evento.getGrupoBemResultante())
                        && this.getTipoGrupoResultante().equals(evento.getTipoGrupoResultante())
                        && this.getUnidadeAdministrativaInicial().getId().equals(evento.getUnidadeAdministrativaInicial().getId())
                        && this.getUnidadeAdministrativaResultante().getId().equals(evento.getUnidadeAdministrativaResultante().getId())
                        && this.getUnidadeOrcamentariaInicial().getId().equals(evento.getUnidadeOrcamentariaInicial().getId())
                        && this.getUnidadeOrcamentariaResultante().getId().equals(evento.getUnidadeOrcamentariaResultante().getId())) {
                        igual = true;
                    }
                } else if (TipoEventoBem.retornaTipoEventoReducaoValorBem().contains(bemVo.getTipoEventoBem())) {
                    if (this.getTipoBemResultante().equals(evento.getTipoBemResultante())
                        && this.getGrupoBemResultante().equals(evento.getGrupoBemResultante())
                        && this.getTipoGrupoResultante().equals(evento.getTipoGrupoResultante())
                        && this.getUnidadeOrcamentariaResultante().equals(evento.getUnidadeOrcamentariaResultante())) {
                        igual = true;
                    }
                } else {
                    if (this.getTipoBemResultante().equals(evento.getTipoBemResultante())
                        && this.getGrupoBemResultante().equals(evento.getGrupoBemResultante())
                        && this.getTipoGrupoResultante().equals(evento.getTipoGrupoResultante())
                        && this.getUnidadeAdministrativaResultante().equals(evento.getUnidadeAdministrativaResultante())
                        && this.getUnidadeOrcamentariaResultante().equals(evento.getUnidadeOrcamentariaResultante())) {
                        igual = true;
                    }
                }
            }
            return igual;
        }

        @Override
        public String toString() {
            return "Tipo Bem: " + getTipoBemResultante() + " Grupo Bem: " + getGrupoBemResultante() +
                " Tipo Grupo: " + getTipoGrupoResultante() + " Unidade Adm: " + getUnidadeAdministrativaResultante() +
                " Unidade Orç: " + getUnidadeOrcamentariaResultante();
        }

    }

    public BensMoveisFacade getBensMoveisFacade() {
        return bensMoveisFacade;
    }
}
