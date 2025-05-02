package br.com.webpublico.controle;

import br.com.webpublico.entidades.AlvaraConstrucao;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Habitese;
import br.com.webpublico.entidades.ProcRegularizaConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.negocios.ProcRegularizaConstrucaoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consultarProcessoRegularizaConstrucao",
        pattern = "/processo-regularizacao-construcao/consultar/",
        viewId = "/faces/tributario/processoregularizaconstrucao/consulta.xhtml")
})
public class ConsultaProcRegularizaConstrucaoControlador implements Serializable {

    @EJB
    private ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private CadastroImobiliario cadastroImobiliario;
    private List<ProcRegularizaConstrucao> processos;
    private List<Habitese> habitesesDisponiveis;

    public AlvaraConstrucao buscarUltimoAlvara(ProcRegularizaConstrucao proc) {
        return procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().buscarUltimoAlvaraParaProcesso(false, proc, AlvaraConstrucao.Situacao.EFETIVADO, AlvaraConstrucao.Situacao.EM_ABERTO, AlvaraConstrucao.Situacao.FINALIZADO);
    }

    public boolean canEmitirDAMTaxaVistoria(ProcRegularizaConstrucao proc) {
        return ProcRegularizaConstrucao.Situacao.ALVARA_CONSTRUCAO.equals(proc.getSituacao()) ||
            ProcRegularizaConstrucao.Situacao.AGUARDANDO_TAXA_VISTORIA.equals(proc.getSituacao());
    }

    public void imprimirAlvara(ProcRegularizaConstrucao proc) {
        try {
            AlvaraConstrucao alvaraConstrucao = procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().buscarUltimoAlvaraParaProcesso(false, proc, AlvaraConstrucao.Situacao.FINALIZADO);
            procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().emitirAlvara(alvaraConstrucao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void imprimirHabitese(Habitese hab) {
        try {
            procRegularizaConstrucaoFacade.getHabiteseConstrucaoFacade().emitirTermo(hab);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void imprimirHabitese(ProcRegularizaConstrucao proc) {
        List<Habitese> habiteses = proc.getUltimoAlvara().getHabiteses();
        habitesesDisponiveis = Lists.newArrayList();
        for (Habitese habitese : habiteses) {
            if (Habitese.Situacao.FINALIZADO.equals(habitese.getSituacao())) {
                habitesesDisponiveis.add(habitese);
            }
        }
        if (habitesesDisponiveis.size() > 1) {
            FacesUtil.atualizarComponente("dialogEscolherHabitese");
            FacesUtil.executaJavaScript("dlgEscolherHabitese.show();");
        } else {
            imprimirHabitese(habitesesDisponiveis.get(0));
        }
    }

    public boolean canImprimirAlvara(ProcRegularizaConstrucao proc) {
        AlvaraConstrucao alvaraConstrucao = procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().buscarUltimoAlvaraParaProcesso(false, proc, AlvaraConstrucao.Situacao.FINALIZADO);
        return alvaraConstrucao != null;
    }

    public boolean canImprimirHabitese(ProcRegularizaConstrucao proc) {
        if (proc.getUltimoAlvara() != null && (ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE.equals(proc.getSituacao()) || ProcRegularizaConstrucao.Situacao.HABITESE.equals(proc.getSituacao()))) {
            List<Habitese> habiteses = proc.getUltimoAlvara().getHabiteses();
            for (Habitese habitese : habiteses) {
                if (Habitese.Situacao.FINALIZADO.equals(habitese.getSituacao())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void emitirDAMAlvara(ProcRegularizaConstrucao proc) {
        try {
            procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().emitirDAM(proc.getUltimoAlvara());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void emitirDAMHabitese(Habitese hab) {
        try {
            procRegularizaConstrucaoFacade.getHabiteseConstrucaoFacade().emitirDAM(hab);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void emitirDAMHabitese(ProcRegularizaConstrucao proc) {
        List<Habitese> habiteses = proc.getUltimoAlvara().getHabiteses();
        habitesesDisponiveis = Lists.newArrayList();
        for (Habitese habitese : habiteses) {
            if (Habitese.Situacao.EFETIVADO.equals(habitese.getSituacao())) {
                habitesesDisponiveis.add(habitese);
            }
        }
        if (habitesesDisponiveis.size() > 1) {
            FacesUtil.atualizarComponente(":dialogEscolherHabitese");
            FacesUtil.executaJavaScript("dlgEscolherHabitese.show();");
        } else {
            emitirDAMHabitese(habitesesDisponiveis.get(0));
        }
    }

    public boolean canEmitirDAMAlvara(ProcRegularizaConstrucao proc) {
        AlvaraConstrucao alvaraConstrucao = procRegularizaConstrucaoFacade.getAlvaraConstrucaoFacade().buscarUltimoAlvaraParaProcesso(false, proc, AlvaraConstrucao.Situacao.EFETIVADO, AlvaraConstrucao.Situacao.EM_ABERTO);
        return alvaraConstrucao != null && !AlvaraConstrucao.Situacao.EM_ABERTO.equals(alvaraConstrucao.getSituacao()) && !AlvaraConstrucao.Situacao.FINALIZADO.equals(alvaraConstrucao.getSituacao());
    }

    public boolean canEmitirDAMHabitse(ProcRegularizaConstrucao proc) {
        if (proc.getUltimoAlvara() != null && (ProcRegularizaConstrucao.Situacao.AGUARDANDO_HABITESE.equals(proc.getSituacao()) || ProcRegularizaConstrucao.Situacao.HABITESE.equals(proc.getSituacao()))) {
            List<Habitese> habiteses = proc.getUltimoAlvara().getHabiteses();
            for (Habitese habitese : habiteses) {
                if (Habitese.Situacao.EFETIVADO.equals(habitese.getSituacao())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void consultar() {
        if (cadastroImobiliario != null) {
            processos = procRegularizaConstrucaoFacade.buscarProcessosPorCadastroImobiliario(cadastroImobiliario);
            if (processos == null || processos.isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("Nenhum Processo de Regularização da Construção encontrado para esse Cadastro Imobiliário!");
            }
        } else {
            FacesUtil.addCampoObrigatorio("Informe o Cadastro Imobiliário!");
        }
    }

    public void selecionarCadastroImobiliario() {
        cadastroImobiliario = cadastroImobiliarioFacade.recuperarSemCalcular(cadastroImobiliario.getId());
    }

    public List<ProcRegularizaConstrucao> getProcessos() {
        return processos;
    }

    public void setProcessos(List<ProcRegularizaConstrucao> processos) {
        this.processos = processos;
    }

    public ProcRegularizaConstrucaoFacade getProcRegularizaConstrucaoFacade() {
        return procRegularizaConstrucaoFacade;
    }

    public void setProcRegularizaConstrucaoFacade(ProcRegularizaConstrucaoFacade procRegularizaConstrucaoFacade) {
        this.procRegularizaConstrucaoFacade = procRegularizaConstrucaoFacade;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public List<Habitese> getHabitesesDisponiveis() {
        return habitesesDisponiveis;
    }

    public void setHabitesesDisponiveis(List<Habitese> habitesesDisponiveis) {
        this.habitesesDisponiveis = habitesesDisponiveis;
    }
}
