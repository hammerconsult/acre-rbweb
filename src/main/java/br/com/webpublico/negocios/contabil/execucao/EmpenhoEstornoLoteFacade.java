package br.com.webpublico.negocios.contabil.execucao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoItemVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoLoteVo;
import br.com.webpublico.entidadesauxiliares.SolicitacaoEmpenhoEstornoVo;
import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.enums.TipoEmpenhoEstorno;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoReconhecimentoObrigacaoPagar;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EmpenhoEstornoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.administrativo.ExecucaoProcessoEstornoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class EmpenhoEstornoLoteFacade extends AbstractFacade<EmpenhoEstornoLote> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private ExecucaoProcessoEstornoFacade execucaoProcessoEstornoFacade;

    public EmpenhoEstornoLoteFacade() {
        super(EmpenhoEstornoLote.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EmpenhoEstornoLote recuperar(Object id) {
        EmpenhoEstornoLote entity = super.recuperar(id);
        Hibernate.initialize(entity.getEmpenhosEstornos());
        return entity;
    }

    @Override
    public void salvarNovo(EmpenhoEstornoLote entity) {
        for (EmpenhoEstornoLoteEmpenhoEstorno esl : entity.getEmpenhosEstornos()) {
            if (esl.getExecucaoContratoEstorno() != null) {
                esl.setExecucaoContratoEstorno(empenhoEstornoFacade.getExecucaoContratoEstornoFacade().salvarRetornando(esl.getExecucaoContratoEstorno()));
                esl.setSolicitacaoEmpenhoEstorno(esl.getExecucaoContratoEstorno().getEstornosEmpenho().get(0).getSolicitacaoEmpenhoEstorno());
            } else if (esl.getExecucaoProcessoEstorno() != null) {
                esl.setExecucaoProcessoEstorno(execucaoProcessoEstornoFacade.salvarRetornando(esl.getExecucaoProcessoEstorno()));
                esl.setSolicitacaoEmpenhoEstorno(esl.getExecucaoProcessoEstorno().getEstornosEmpenho().get(0).getSolicitacaoEmpenhoEstorno());
            }
            esl.setEmpenhoEstorno(empenhoEstornoFacade.salvarNovoEstorno(esl.getEmpenhoEstorno(), esl.getSolicitacaoEmpenhoEstorno()));
        }
        entity.setDataLote(new Date());
        super.salvarNovo(entity);
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public HierarquiaOrganizacional getHierarquiaDaUnidade(EmpenhoEstornoLote selecionado) {
        return empenhoEstornoFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataLote(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrcamentarias(String parte) {
        return empenhoEstornoFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), sistemaFacade.getDataOperacao());
    }

    public List<Empenho> buscarEmpenhoPorNumeroPessoaAndCategoriaResto(UnidadeOrganizacional uo) {
        return empenhoEstornoFacade.getEmpenhoFacade().buscarEmpenhoPorNumeroPessoaAndCategoriaResto("", sistemaFacade.getExercicioCorrente().getAno(), uo, null);
    }

    public void buscarEMontarEmpenhosEstornos(EmpenhoEstornoLote selecionado) {
        StringBuilder errosEncontrados = new StringBuilder();
        List<Empenho> empenhos = buscarEmpenhoPorNumeroPessoaAndCategoriaResto(selecionado.getUnidadeOrganizacional());
        for (Empenho empenho : empenhos) {
            try {
                selecionado.getEmpenhosEstornos().add(novoEmpenhoEstorno(empenho, selecionado));
            } catch (ValidacaoException ve) {
                for (FacesMessage mensage : ve.getMensagens()) {
                    errosEncontrados.append("</br>").append(mensage.getDetail()).append("</br>");
                }
            } catch (Exception ex) {
                errosEncontrados.append(empenho.toString()).append("</br>").append(ex.getMessage()).append("</br>");
            }
        }
        selecionado.setErros(errosEncontrados.toString());
    }

    private EmpenhoEstornoLoteEmpenhoEstorno novoEmpenhoEstorno(Empenho empenho, EmpenhoEstornoLote selecionado) {
        EmpenhoEstorno empenhoEstorno = new EmpenhoEstorno();
        empenhoEstorno.setExercicio(sistemaFacade.getExercicioCorrente());
        empenhoEstorno.setEmpenho(empenhoEstornoFacade.getEmpenhoFacade().recuperar(empenho.getId()));
        empenhoEstorno.setUnidadeOrganizacional(empenho.getUnidadeOrganizacional());
        empenhoEstorno.setUnidadeOrganizacionalAdm(empenho.getUnidadeOrganizacionalAdm());
        empenhoEstorno.setCategoriaOrcamentaria(empenho.getCategoriaOrcamentaria());
        empenhoEstorno.setDataEstorno(sistemaFacade.getDataOperacao());
        empenhoEstorno.setTipoEmpenhoEstorno(TipoEmpenhoEstorno.CANCELAMENTO);
        SolicitacaoEmpenhoEstornoLoteVo solicitacaoVO = buscarOuMontarSolicitacaoEmpenhoEstorno(empenhoEstorno);
        empenhoEstorno.setComplementoHistorico((solicitacaoVO != null && solicitacaoVO.getSolicitacaoEmpenhoEstorno() != null) ? solicitacaoVO.getSolicitacaoEmpenhoEstorno().getHistorico() : empenho.getComplementoHistorico());
        empenhoEstorno.setValor((solicitacaoVO != null && solicitacaoVO.getSolicitacaoEmpenhoEstorno() != null) ? solicitacaoVO.getSolicitacaoEmpenhoEstorno().getValor() : empenhoEstorno.getEmpenho().getSaldoDisponivelEmpenhoComObrigacao());
        empenhoEstorno.getEmpenho().setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.SEM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        if (empenhoEstorno.getEmpenho().isEmpenhoDeObrigacaoPagar()) {
            empenhoEstorno.getEmpenho().setTipoReconhecimento(TipoReconhecimentoObrigacaoPagar.COM_RECONHECIMENTO_OBRIGACAO_ANTES_EMPENHO_DESPESA);
        }
        empenhoEstorno.setEventoContabil(empenhoEstornoFacade.buscarEventoContabil(empenhoEstorno));
        empenhoEstorno.setDesdobramentos(criarDesdobramentoEmpenhoEstorno(empenhoEstorno));
        empenhoEstornoFacade.verificarSeEmpenhoPossuiObrigacaoPagarDepoisEmpenho(empenhoEstorno);
        return criarEmpenhoEstornoLoteEmpenhoEstorno(selecionado, empenhoEstorno, solicitacaoVO);
    }

    private EmpenhoEstornoLoteEmpenhoEstorno criarEmpenhoEstornoLoteEmpenhoEstorno(EmpenhoEstornoLote selecionado, EmpenhoEstorno empenhoEstorno, SolicitacaoEmpenhoEstornoLoteVo solicitacaoVO) {
        EmpenhoEstornoLoteEmpenhoEstorno retorno = new EmpenhoEstornoLoteEmpenhoEstorno();
        retorno.setEmpenhoEstornoLote(selecionado);
        retorno.setEmpenhoEstorno(empenhoEstorno);
        retorno.setSolicitacaoEmpenhoEstorno(solicitacaoVO != null ? solicitacaoVO.getSolicitacaoEmpenhoEstorno() : null);
        retorno.setExecucaoContratoEstorno(solicitacaoVO != null ? solicitacaoVO.getExecucaoContratoEstorno() : null);
        retorno.setExecucaoProcessoEstorno(solicitacaoVO != null ? solicitacaoVO.getExecucaoProcessoEstorno() : null);
        return retorno;
    }

    private List<DesdobramentoEmpenhoEstorno> criarDesdobramentoEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        List<DesdobramentoEmpenhoEstorno> retorno = Lists.newArrayList();
        for (DesdobramentoEmpenho desdobramentoEmpenho : empenhoEstorno.getEmpenho().getDesdobramentos()) {
            DesdobramentoEmpenhoEstorno desdobramentoEstorno = new DesdobramentoEmpenhoEstorno();
            desdobramentoEstorno.setEmpenhoEstorno(empenhoEstorno);
            desdobramentoEstorno.setValor(desdobramentoEmpenho.getSaldo());
            if (empenhoEstorno.getEmpenho().isEmpenhoDeObrigacaoPagar()) {
                desdobramentoEstorno.setDesdobramentoObrigacaoPagar(desdobramentoEmpenho.getDesdobramentoObrigacaoPagar());
                desdobramentoEstorno.setConta(desdobramentoEmpenho.getDesdobramentoObrigacaoPagar().getConta());
            } else {
                desdobramentoEstorno.setDesdobramentoEmpenho(desdobramentoEmpenho);
                desdobramentoEstorno.setConta(desdobramentoEmpenho.getConta());
            }
            retorno.add(desdobramentoEstorno);
        }
        return retorno;
    }

    public SolicitacaoEmpenhoEstornoLoteVo buscarOuMontarSolicitacaoEmpenhoEstorno(EmpenhoEstorno selecionado) {
        List<SolicitacaoEmpenhoEstorno> solicitacoes = empenhoEstornoFacade.getSolicitacaoEmpenhoEstornoFacade().buscarSolicitacaoEmpenhoEstorno(selecionado.getUnidadeOrganizacional(), selecionado.getCategoriaOrcamentaria(), selecionado.getEmpenho(), selecionado.getExercicio());
        if (solicitacoes != null && !solicitacoes.isEmpty()) {
            return new SolicitacaoEmpenhoEstornoLoteVo(solicitacoes.get(0), null, null);
        }

        Empenho empenhoOriginal = selecionado.getEmpenho().getEmpenho();
        ExecucaoContratoEmpenho exEmp = empenhoEstornoFacade.getExecucaoContratoFacade().buscarExecucaoContratoEmpenhoPorEmpenho(empenhoOriginal);
        if (exEmp != null) {
            return montarSolicitacaoVoPorExecucaoContrato(selecionado, exEmp);
        }

        ExecucaoProcessoEmpenho exProcEmp = empenhoEstornoFacade.getExecucaoProcessoFacade().buscarExecucaoProcessoEmpenhoPorEmpenho(empenhoOriginal.getId());
        if (exProcEmp != null) {
            return montarSolicitacaoVoPorExecucaoProcesso(selecionado, exProcEmp);
        }
        return null;
    }

    private SolicitacaoEmpenhoEstornoLoteVo montarSolicitacaoVoPorExecucaoContrato(EmpenhoEstorno selecionado, ExecucaoContratoEmpenho exEmp) {
        ExecucaoContratoEstorno execucaoContratoEstorno = criarExecucaoContratoEstorno(selecionado, exEmp);
        if (execucaoContratoEstorno.getValorTotal().compareTo(selecionado.getEmpenho().getSaldo()) <= 0) {
            return new SolicitacaoEmpenhoEstornoLoteVo(execucaoContratoEstorno.getEstornosEmpenho().get(0).getSolicitacaoEmpenhoEstorno(), execucaoContratoEstorno, null);
        } else {
            String urlExecucao = FacesUtil.getRequestContextPath() + "/execucao-contrato-adm/ver/" + exEmp.getExecucaoContrato().getId() + "/";
            String urlExecucaoEstorno = FacesUtil.getRequestContextPath() + "/execucao-contrato-estorno/novo/";
            String mensagem = "A soma de todos os itens disponíveis para estorno <b>(" + Util.formatarValor(execucaoContratoEstorno.getValorTotal()) + ")</b> da execução <b><a href='" +
                urlExecucao + "'target='_blank' style='color: blue;'>" + exEmp.getExecucaoContrato() +
                "</a></b> é superior ao valor <b>(" + Util.formatarValor(selecionado.getEmpenho().getSaldo()) + ")</b> do saldo do empenho " + selecionado.getEmpenho() +
                ", e deverá ser feita a solicitação de estorno manualmente na tela de <b><a href='" +
                urlExecucaoEstorno + "'target='_blank' style='color: blue;'>Estorno de Execução de Contrato</a></b>.";
            throw new ValidacaoException(mensagem);
        }
    }

    private SolicitacaoEmpenhoEstornoLoteVo montarSolicitacaoVoPorExecucaoProcesso(EmpenhoEstorno selecionado, ExecucaoProcessoEmpenho exProcEmp) {
        ExecucaoProcessoEstorno execucaoProcessoEstorno = criarExecucaoProcessoEstorno(selecionado, exProcEmp);
        if (execucaoProcessoEstorno.getValor().compareTo(selecionado.getEmpenho().getSaldo()) <= 0) {
            return new SolicitacaoEmpenhoEstornoLoteVo(execucaoProcessoEstorno.getEstornosEmpenho().get(0).getSolicitacaoEmpenhoEstorno(), null, execucaoProcessoEstorno);
        } else {
            String urlExecucao = FacesUtil.getRequestContextPath() + "/execucao-sem-contrato/ver/" + exProcEmp.getExecucaoProcesso().getId() + "/";
            String urlExecucaoEstorno = FacesUtil.getRequestContextPath() + "/execucao-sem-contrato-estorno/novo/";
            String mensagem = "A soma de todos os itens disponíveis para estorno <b>(" + Util.formatarValor(execucaoProcessoEstorno.getValor()) + ")</b> da execução <b><a href='" +
                urlExecucao + "'target='_blank' style='color: blue;'>" + exProcEmp.getExecucaoProcesso() +
                "</a></b> é superior ao valor <b>(" + Util.formatarValor(selecionado.getEmpenho().getSaldo()) + ")</b> do saldo do empenho " + selecionado.getEmpenho() +
                ", e deverá ser feita a solicitação de estorno manualmente na tela de <b><a href='" +
                urlExecucaoEstorno + "'target='_blank' style='color: blue;'>Estorno de Execução Sem Contrato</a></b>.";
            throw new ValidacaoException(mensagem);
        }
    }

    private ExecucaoContratoEstorno criarExecucaoContratoEstorno(EmpenhoEstorno selecionado, ExecucaoContratoEmpenho exEmp) {
        ExecucaoContratoEstorno execucaoContratoEstorno = new ExecucaoContratoEstorno();
        execucaoContratoEstorno.setExecucaoContrato(exEmp.getExecucaoContrato());
        execucaoContratoEstorno.setDataLancamento(sistemaFacade.getDataOperacao());

        SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo = empenhoEstornoFacade.getExecucaoContratoEstornoFacade().criarSolicitacaoEmpenhoEstornoVoPorEmpenho(
            execucaoContratoEstorno, selecionado.getEmpenho(), exEmp.getSolicitacaoEmpenho()
        );
        solicitacaoEmpenhoEstornoVo.setSolicitacaoEmpenho(exEmp.getSolicitacaoEmpenho());
        solicitacaoEmpenhoEstornoVo.setEmpenho(selecionado.getEmpenho());
        solicitacaoEmpenhoEstornoVo.setItens(empenhoEstornoFacade.getExecucaoContratoEstornoFacade().criarItensEstorno(execucaoContratoEstorno, solicitacaoEmpenhoEstornoVo, exEmp.getExecucaoContrato().getItens()));

        ExecucaoContratoEmpenhoEstorno execucaoContEmpEst = criarExecucaoContratoEmpenhoEstorno(selecionado.getEmpenho(), exEmp, execucaoContratoEstorno, solicitacaoEmpenhoEstornoVo);
        Util.adicionarObjetoEmLista(execucaoContratoEstorno.getEstornosEmpenho(), execucaoContEmpEst);
        execucaoContratoEstorno.calcularValorTotal();
        return execucaoContratoEstorno;
    }

    private ExecucaoContratoEmpenhoEstorno criarExecucaoContratoEmpenhoEstorno(Empenho empenho, ExecucaoContratoEmpenho exEmp, ExecucaoContratoEstorno execucaoContratoEstorno, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        ExecucaoContratoEmpenhoEstorno execucaoContEmpEst = new ExecucaoContratoEmpenhoEstorno();
        execucaoContEmpEst.setExecucaoContratoEstorno(execucaoContratoEstorno);
        criarExecucaoContratoItens(execucaoContEmpEst, solicitacaoEmpenhoEstornoVo);
        execucaoContEmpEst.setSolicitacaoEmpenhoEstorno(criarSolicitacaoEmpenhoEstorno(empenho, exEmp, execucaoContEmpEst.getValorTotalItens()));
        execucaoContEmpEst.setValor(execucaoContEmpEst.getValorTotalItens());
        return execucaoContEmpEst;
    }

    private void criarExecucaoContratoItens(ExecucaoContratoEmpenhoEstorno execucaoContratoEmpenhoEstorno, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        for (SolicitacaoEmpenhoEstornoItemVo itemVo : solicitacaoEmpenhoEstornoVo.getItens()) {
            ExecucaoContratoEmpenhoEstornoItem novoItem = new ExecucaoContratoEmpenhoEstornoItem();
            novoItem.setExecucaoContratoEmpenhoEst(execucaoContratoEmpenhoEstorno);
            novoItem.setExecucaoContratoItem(itemVo.getExecucaoContratoItem());
            novoItem.setQuantidade(itemVo.getQuantidade());
            novoItem.setValorUnitario(itemVo.getValorUnitario());
            novoItem.setValorTotal(itemVo.getValorTotal());
            Util.adicionarObjetoEmLista(execucaoContratoEmpenhoEstorno.getItens(), novoItem);
        }
    }

    public SolicitacaoEmpenhoEstorno criarSolicitacaoEmpenhoEstorno(Empenho empenho, ExecucaoContratoEmpenho exEmp, BigDecimal valor) {
        SolicitacaoEmpenhoEstorno novaSolEst = new SolicitacaoEmpenhoEstorno(exEmp.getSolicitacaoEmpenho(), empenho, OrigemSolicitacaoEmpenho.CONTRATO);
        novaSolEst.setDataSolicitacao(sistemaFacade.getDataOperacao());
        novaSolEst.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        novaSolEst.setValor(valor);
        novaSolEst.setHistorico(empenhoEstornoFacade.getSolicitacaoEmpenhoEstornoFacade().getHistoricoSolicitacaoEmpenho(novaSolEst.getClasseCredor(), novaSolEst.getFonteDespesaORC(), exEmp.getExecucaoContrato()));
        return novaSolEst;
    }

    private ExecucaoProcessoEstorno criarExecucaoProcessoEstorno(EmpenhoEstorno selecionado, ExecucaoProcessoEmpenho exProcEmp) {
        ExecucaoProcessoEstorno execucaoProcessoEstorno = new ExecucaoProcessoEstorno();
        execucaoProcessoEstorno.setExecucaoProcesso(exProcEmp.getExecucaoProcesso());
        execucaoProcessoEstorno.setDataLancamento(sistemaFacade.getDataOperacao());

        SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo = execucaoProcessoEstornoFacade.criarSolicitacaoEmpenhoEstornoVo(
            execucaoProcessoEstorno, selecionado.getEmpenho(), exProcEmp.getSolicitacaoEmpenho()
        );
        solicitacaoEmpenhoEstornoVo.setSolicitacaoEmpenho(exProcEmp.getSolicitacaoEmpenho());
        solicitacaoEmpenhoEstornoVo.setEmpenho(selecionado.getEmpenho());
        execucaoProcessoEstornoFacade.criarItensEstorno(execucaoProcessoEstorno, solicitacaoEmpenhoEstornoVo, exProcEmp.getExecucaoProcesso().getItens());

        ExecucaoProcessoEmpenhoEstorno execucaoContEmpEst = criarExecucaoProcessoEmpenhoEstorno(selecionado.getEmpenho(), exProcEmp, execucaoProcessoEstorno, solicitacaoEmpenhoEstornoVo);
        Util.adicionarObjetoEmLista(execucaoProcessoEstorno.getEstornosEmpenho(), execucaoContEmpEst);
        execucaoProcessoEstorno.calcularValorTotal();
        return execucaoProcessoEstorno;
    }

    private ExecucaoProcessoEmpenhoEstorno criarExecucaoProcessoEmpenhoEstorno(Empenho empenho, ExecucaoProcessoEmpenho exProcEmp, ExecucaoProcessoEstorno execucaoProcessoEstorno, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        ExecucaoProcessoEmpenhoEstorno execucaoContEmpEst = new ExecucaoProcessoEmpenhoEstorno();
        execucaoContEmpEst.setExecucaoProcessoEstorno(execucaoProcessoEstorno);
        criarExecucaoProcessoItens(execucaoContEmpEst, solicitacaoEmpenhoEstornoVo);
        execucaoContEmpEst.setSolicitacaoEmpenhoEstorno(criarSolicitacaoEmpenhoEstornoPorProcesso(empenho, exProcEmp, execucaoContEmpEst.getValorTotalItens()));
        execucaoContEmpEst.setValor(execucaoContEmpEst.getValorTotalItens());
        return execucaoContEmpEst;
    }

    private void criarExecucaoProcessoItens(ExecucaoProcessoEmpenhoEstorno execProcEmpEst, SolicitacaoEmpenhoEstornoVo solicitacaoEmpenhoEstornoVo) {
        for (SolicitacaoEmpenhoEstornoItemVo itemVo : solicitacaoEmpenhoEstornoVo.getItens()) {
            ExecucaoProcessoEmpenhoEstornoItem novoItem = new ExecucaoProcessoEmpenhoEstornoItem();
            novoItem.setExecucaoProcessoEmpenhoEstorno(execProcEmpEst);
            novoItem.setExecucaoProcessoItem(itemVo.getExecucaoProcessoItem());
            novoItem.setQuantidade(itemVo.getQuantidade());
            novoItem.setValorUnitario(itemVo.getValorUnitario());
            novoItem.setValorTotal(itemVo.getValorTotal());
            Util.adicionarObjetoEmLista(execProcEmpEst.getItens(), novoItem);
        }
    }

    public SolicitacaoEmpenhoEstorno criarSolicitacaoEmpenhoEstornoPorProcesso(Empenho empenho, ExecucaoProcessoEmpenho exProcEmp, BigDecimal valor) {
        OrigemSolicitacaoEmpenho origemSolEmp = exProcEmp.getExecucaoProcesso().isExecucaoAta() ? OrigemSolicitacaoEmpenho.ATA_REGISTRO_PRECO : OrigemSolicitacaoEmpenho.DISPENSA_LICITACAO_INEXIGIBILIDADE;
        SolicitacaoEmpenhoEstorno novaSolEst = new SolicitacaoEmpenhoEstorno(exProcEmp.getSolicitacaoEmpenho(), empenho, origemSolEmp);
        novaSolEst.setDataSolicitacao(sistemaFacade.getDataOperacao());
        novaSolEst.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        novaSolEst.setValor(valor);
        novaSolEst.setHistorico(empenhoEstornoFacade.getSolicitacaoEmpenhoEstornoFacade().getHistoricoSolicitacaoEmpenhoExecucaoProcessoEstorno(exProcEmp.getSolicitacaoEmpenho(), exProcEmp.getExecucaoProcesso()));
        return novaSolEst;
    }
}
