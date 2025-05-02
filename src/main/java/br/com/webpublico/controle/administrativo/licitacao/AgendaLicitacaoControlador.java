package br.com.webpublico.controle.administrativo.licitacao;

import br.com.webpublico.entidadesauxiliares.administrativo.AgendaLicitacaoVO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.administrativo.licitacao.AgendaLicitacaoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "agenda-licitacao", pattern = "/administrativo/agenda-licitacao/",
        viewId = "/faces/administrativo/licitacao/agenda-licitacao/lista.xhtml"),
})
public class AgendaLicitacaoControlador {

    @EJB
    private AgendaLicitacaoFacade agendaLicitacaoFacade;
    private List<AgendaLicitacaoVO> agendasLicitacoes;
    private List<AgendaLicitacaoVO> semana1;
    private List<AgendaLicitacaoVO> semana2;
    private List<AgendaLicitacaoVO> semana3;
    private List<AgendaLicitacaoVO> semana4;
    private List<AgendaLicitacaoVO> semana5;
    private List<AgendaLicitacaoVO> semana6;
    private Date dataInicial;
    private Date dataFinal;

    @URLAction(mappingId = "agenda-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        dataInicial = agendaLicitacaoFacade.getSistemaFacade().getDataOperacao();
        dataFinal = DataUtil.ultimoDiaMes(agendaLicitacaoFacade.getSistemaFacade().getDataOperacao()).getTime();
        agendasLicitacoes = agendaLicitacaoFacade.buscarDadosAgenda(dataInicial, dataFinal);
        atualizarSemanas();
    }

    public void pesquisar() {
        try {
            validarCampos();
            agendasLicitacoes = agendaLicitacaoFacade.buscarDadosAgenda(dataInicial, dataFinal);
            atualizarSemanas();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve ser superior a Data Inicial");
        }
        if (DataUtil.getMes(dataInicial) != DataUtil.getMes(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas devem estar no mesmo mês.");
        }
        ve.lancarException();
    }

    private void atualizarSemanas() {
        semana1 = Lists.newArrayList();
        semana2 = Lists.newArrayList();
        semana3 = Lists.newArrayList();
        semana4 = Lists.newArrayList();
        semana5 = Lists.newArrayList();
        semana6 = Lists.newArrayList();
        if (agendasLicitacoes.get(0).getPosicao() != 0) {
            int count = 0;
            while (count < agendasLicitacoes.get(0).getPosicao()) {
                semana1.add(new AgendaLicitacaoVO());
                count++;
            }
        }
        for (AgendaLicitacaoVO agenda : agendasLicitacoes) {
            if (agenda != null){
                if (agenda.getPosicao() >= 0 && agenda.getPosicao() <= 6) {
                    semana1.add(agenda.getPosicao(), agenda);
                } else if (agenda.getPosicao() >= 7 && agenda.getPosicao() <= 13) {
                    semana2.add(agenda);
                } else if (agenda.getPosicao() >= 14 && agenda.getPosicao() <= 20) {
                    semana3.add(agenda);
                } else if (agenda.getPosicao() >= 21 && agenda.getPosicao() <= 27) {
                    semana4.add(agenda);
                } else if (agenda.getPosicao() >= 28 && agenda.getPosicao() <= 34) {
                    semana5.add(agenda);
                } else if (agenda.getPosicao() >= 35 && agenda.getPosicao() <= 41) {
                    semana6.add(agenda);
                }
            }
        }
        while (semana5.size() < 7) {
            semana5.add(new AgendaLicitacaoVO());
        }
        if (!semana6.isEmpty()) {
            while (semana6.size() < 7) {
                semana6.add(new AgendaLicitacaoVO());
            }
        }
    }

    public void redirecionarParaLicitacao(Long idLicitacao) {
        FacesUtil.redirecionamentoInterno("/licitacao/ver/" + idLicitacao + "/");
    }

    public String getDescricaoMes() {
        return dataFinal != null ? DataUtil.getDescricaoMes(DataUtil.getMes(dataFinal)) : "";
    }

    public List<AgendaLicitacaoVO> getAgendasLicitacoes() {
        return agendasLicitacoes;
    }

    public void setAgendasLicitacoes(List<AgendaLicitacaoVO> agendasLicitacoes) {
        this.agendasLicitacoes = agendasLicitacoes;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<AgendaLicitacaoVO> getSemana1() {
        return semana1;
    }

    public void setSemana1(List<AgendaLicitacaoVO> semana1) {
        this.semana1 = semana1;
    }

    public List<AgendaLicitacaoVO> getSemana2() {
        return semana2;
    }

    public void setSemana2(List<AgendaLicitacaoVO> semana2) {
        this.semana2 = semana2;
    }

    public List<AgendaLicitacaoVO> getSemana3() {
        return semana3;
    }

    public void setSemana3(List<AgendaLicitacaoVO> semana3) {
        this.semana3 = semana3;
    }

    public List<AgendaLicitacaoVO> getSemana4() {
        return semana4;
    }

    public void setSemana4(List<AgendaLicitacaoVO> semana4) {
        this.semana4 = semana4;
    }

    public List<AgendaLicitacaoVO> getSemana5() {
        return semana5;
    }

    public void setSemana5(List<AgendaLicitacaoVO> semana5) {
        this.semana5 = semana5;
    }

    public List<AgendaLicitacaoVO> getSemana6() {
        return semana6;
    }

    public void setSemana6(List<AgendaLicitacaoVO> semana6) {
        this.semana6 = semana6;
    }
}
