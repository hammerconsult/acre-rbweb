package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "acompanhamento-meta-fisica-financeira", pattern = "/ppa/acompanhamento-meta-fisica-financeira/", viewId = "/faces/financeiro/ppa/acompanhamentometafisicafinanceira/edita.xhtml"),
})
public class AcompanhamentoMetaFisicaEFinanceiraControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private ProdutoPPAFacade produtoPPAFacade;
    @EJB
    private ProvisaoPPAFacade provisaoPPAFacade;
    @EJB
    private MedicaoProvisaoPPAFacade medicaoProvisaoPPAFacade;
    private PPA ppa;
    private ProgramaPPA programaPPA;
    private AcaoPrincipal acaoPrincipal;
    private ProdutoPPA produtoPPA;
    private ProvisaoPPA provisaoPPA;
    private MedicaoProvisaoPPA medicaoProvisaoPPA;
    private ConverterAutoComplete converterProdutoPPA;
    private ConverterAutoComplete converterProvisaoPPA;
    private List<MedicaoProvisaoPPA> medicoes;
    private List<MedicaoProvisaoPPA> medicoesRemovidas;

    public AcompanhamentoMetaFisicaEFinanceiraControlador() {
    }

    @URLAction(mappingId = "acompanhamento-meta-fisica-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        alterouPpa();
        ppa = null;
        medicoes = Lists.newArrayList();
        medicoesRemovidas = Lists.newArrayList();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public ProdutoPPA getProdutoPPA() {
        return produtoPPA;
    }

    public void setProdutoPPA(ProdutoPPA produtoPPA) {
        this.produtoPPA = produtoPPA;
    }

    public ProvisaoPPA getProvisaoPPA() {
        return provisaoPPA;
    }

    public void setProvisaoPPA(ProvisaoPPA provisaoPPA) {
        this.provisaoPPA = provisaoPPA;
    }

    public MedicaoProvisaoPPA getMedicaoProvisaoPPA() {
        return medicaoProvisaoPPA;
    }

    public void setMedicaoProvisaoPPA(MedicaoProvisaoPPA medicaoProvisaoPPA) {
        this.medicaoProvisaoPPA = medicaoProvisaoPPA;
    }

    public List<PPA> completarPpas(String parte) {
        return ppaFacade.buscarPpas(parte);
    }

    public List<ProgramaPPA> completarProgramasPPA(String parte) {
        return programaPPAFacade.buscarProgramasPorPPA(parte, ppa);
    }

    public List<AcaoPrincipal> completarAcoesPrincipais(String parte) {
        return acaoPrincipalFacade.listaAcaoPPAPorPrograma(programaPPA, parte);
    }

    public List<ProdutoPPA> completarProdutosPPA(String parte) {
        return produtoPPAFacade.buscarProdutosPPAPorAcoesPrincipais(parte, acaoPrincipal);
    }

    public List<ProvisaoPPA> completarProvisaoPPA(String parte) {
        return provisaoPPAFacade.buscarProvisoesPorAno(parte, produtoPPA);
    }

    public ConverterAutoComplete getConverterProdutosPPA() {
        if (converterProdutoPPA == null) {
            converterProdutoPPA = new ConverterAutoComplete(ProdutoPPA.class, produtoPPAFacade);
        }

        return converterProdutoPPA;
    }

    public ConverterAutoComplete getConverterProvisaoPPA() {
        if (converterProvisaoPPA == null) {
            converterProvisaoPPA = new ConverterAutoComplete(ProvisaoPPA.class, provisaoPPAFacade);
        }

        return converterProvisaoPPA;
    }

    public void removerMedicaoProvisaoPPA(MedicaoProvisaoPPA medicao) {
        medicoes.remove(medicao);
        Util.adicionarObjetoEmLista(medicoesRemovidas, medicao);
    }

    public void adicionarMedicaoProvisaoPPA() {
        try {
            validarMedicacaoProvisaoPPA();
            medicaoProvisaoPPA.setProvisaoPPA(provisaoPPA);
            Util.adicionarObjetoEmLista(medicoes, medicaoProvisaoPPA);
            limparMedicaoProvisaoPPA();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void limparMedicaoProvisaoPPA() {
        medicaoProvisaoPPA = new MedicaoProvisaoPPA();
        medicaoProvisaoPPA.setDataRegistro(new Date());
        medicaoProvisaoPPA.setUsuarioSistema(sistemaControlador.getUsuarioCorrente());
    }

    public void inicializarMetaFisicaAcumulada() {
        limparMedicaoProvisaoPPA();
        medicoes = Lists.newArrayList();
        provisaoPPA = provisaoPPAFacade.recuperar(provisaoPPA.getId());
        for (MedicaoProvisaoPPA medicao : provisaoPPA.getMedicaoProvisaoPPAS()) {
            Util.adicionarObjetoEmLista(medicoes, (MedicaoProvisaoPPA) Util.clonarObjeto(medicao));
        }
    }

    private void validarMedicacaoProvisaoPPA() {
        ValidacaoException ve = new ValidacaoException();
        if (medicaoProvisaoPPA.getAcumulada() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Meta Acumulada deve ser informado.");
        }
        if (Strings.isNullOrEmpty(medicaoProvisaoPPA.getHistorico())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Histórico da Avaliação deve ser informado.");
        }
        if (medicaoProvisaoPPA.getHistorico().length() < 300) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Histórico da Avaliação precisa ter no mínimo 300 caracteres.");
        }
        ve.lancarException();
    }

    public String emExecucao(MedicaoProvisaoPPA med) {
        if (med.getAcumulada().compareTo(BigDecimal.ZERO) == 0 || provisaoPPA.getMetaFisica().compareTo(BigDecimal.ZERO) == 0) {
            return "0%";
        } else {
            return (med.getAcumulada().multiply(new BigDecimal(100))).divide(provisaoPPA.getMetaFisica(), 0, RoundingMode.HALF_UP).toString() + "%";
        }
    }

    public void alterouPpa() {
        programaPPA = null;
        alterouProgramaPPA();
    }

    public void alterouProgramaPPA() {
        acaoPrincipal = null;
        alterouAcaoPrincipal();
    }

    public void alterouAcaoPrincipal() {
        produtoPPA = null;
        alterouProdutoPPA();
    }

    public void alterouProdutoPPA() {
        provisaoPPA = null;
        medicaoProvisaoPPA = null;
        medicoes = Lists.newArrayList();
        medicoesRemovidas = Lists.newArrayList();
    }

    public List<MedicaoProvisaoPPA> getMedicoesOrdenadasPorData() {
        if (!medicoes.isEmpty()) {
            Collections.sort(medicoes, new Comparator<MedicaoProvisaoPPA>() {
                @Override
                public int compare(MedicaoProvisaoPPA o1, MedicaoProvisaoPPA o2) {
                    Date i1 = o1.getDataRegistro();
                    Date i2 = o2.getDataRegistro();
                    return i2.compareTo(i1);
                }
            });
            return medicoes;
        }
        return new ArrayList<>();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ppa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo PPA deve ser informado.");
        }
        if (programaPPA == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Programa deve ser informado.");
        }
        if (acaoPrincipal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ação deve ser informado.");
        }
        if (produtoPPA == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Produto deve ser informado.");
        }
        if (provisaoPPA == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo plano de Aplicação deve ser informado.");
        }
        ve.lancarException();
        if (medicoes.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi(ram) encontrada(s) medição(ões) para salvar.");
        }
        ve.lancarException();
    }

    public void salvar() {
        try {
            validarCampos();
            for (MedicaoProvisaoPPA med : medicoes) {
                medicaoProvisaoPPAFacade.salvar(med);
            }
            for (MedicaoProvisaoPPA medicaoRemovida : medicoesRemovidas) {
                if (medicaoRemovida.getId() != null) {
                    medicaoProvisaoPPAFacade.remover(medicaoRemovida);
                }
            }
            FacesUtil.addOperacaoRealizada("Medições salvas com sucesso");
            Web.navegacao("/ppa/acompanhamento-meta-fisica-financeira/", "/ppa/acompanhamento-meta-fisica-financeira/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<MedicaoProvisaoPPA> getMedicoes() {
        return medicoes;
    }

    public void setMedicoes(List<MedicaoProvisaoPPA> medicoes) {
        this.medicoes = medicoes;
    }

    public List<MedicaoProvisaoPPA> getMedicoesRemovidas() {
        return medicoesRemovidas;
    }

    public void setMedicoesRemovidas(List<MedicaoProvisaoPPA> medicoesRemovidas) {
        this.medicoesRemovidas = medicoesRemovidas;
    }
}
