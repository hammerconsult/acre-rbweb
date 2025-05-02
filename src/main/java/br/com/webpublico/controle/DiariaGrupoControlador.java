/*
 * Codigo gerado automaticamente em Mon Mar 19 14:45:35 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PropostaConcessaoDiariaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "diariaGrupoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novodiariagrupo", pattern = "/diaria-grupo/novo/", viewId = "/faces/financeiro/concessaodediarias/diariagrupo/edita.xhtml")
})
public class DiariaGrupoControlador extends PrettyControlador<PropostaConcessaoDiaria> implements Serializable, CRUD {

    @EJB
    private PropostaConcessaoDiariaFacade propostaConcessaoDiariaFacade;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterFonteDespesaORC;
    private ConverterAutoComplete converterClasseCredor;
    private ConverterAutoComplete converterContaCorrente;
    private ConverterAutoComplete converterGrupoDiaria;
    private ConverterGenerico converterClasseDiaria;
    private DiariaPrestacaoConta diariaPrestacaoContaselecionada;
    private MoneyConverter moneyConverter;
    private FonteDespesaORC fonteDespesaORC;
    private List<PessoaFisica> listaPessoasDiariaCampo;
    private PessoaFisica pessoaSelecionada;
    private ClasseCredor classeCredorSelecionada;
    private ContaCorrenteBancPessoa contaCorrenteBancPessoaSelecionada;
    private GrupoDiaria grupoDiaria;
    private Boolean grupoOuPessoa;

    public DiariaGrupoControlador() {
        super(PropostaConcessaoDiaria.class);
    }

    public PropostaConcessaoDiariaFacade getFacade() {
        return propostaConcessaoDiariaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return propostaConcessaoDiariaFacade;
    }

    public List<SelectItem> getSituacoesProposta() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (SituacaoPropostaConcessaoDiaria situacaoPropostaConcessaoDiaria : SituacaoPropostaConcessaoDiaria.values()) {
            retorno.add(new SelectItem(situacaoPropostaConcessaoDiaria, situacaoPropostaConcessaoDiaria.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (SituacaoCadastralContabil situacao : SituacaoCadastralContabil.values()) {
            retorno.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return retorno;
    }

    @URLAction(mappingId = "novodiariagrupo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        selecionado.setUnidadeOrganizacionalAdm(sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        selecionado.setTipoProposta(TipoProposta.CONCESSAO_DIARIACAMPO);
        selecionado.setTipoViagem(TipoViagem.ESTADUAL);
        fonteDespesaORC = new FonteDespesaORC();
        pessoaSelecionada = new PessoaFisica();
        pessoaSelecionada = null;
        classeCredorSelecionada = new ClasseCredor();
        contaCorrenteBancPessoaSelecionada = new ContaCorrenteBancPessoa();
        contaCorrenteBancPessoaSelecionada = null;
        listaPessoasDiariaCampo = new ArrayList<>();
        grupoOuPessoa = true;
    }

    public void limpaCamposPainelPessoa() {
        grupoDiaria = null;
        pessoaSelecionada = null;
        classeCredorSelecionada = null;
        contaCorrenteBancPessoaSelecionada = null;
    }

    public Boolean validaCamposDiaria() {
        Boolean controle = Boolean.TRUE;
        if (selecionado.getDataLancamento() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Data é obrigatório."));
            controle = Boolean.FALSE;
        }
        if (selecionado.getDespesaORC() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Código da Reduzido é obrigatório."));
            controle = Boolean.FALSE;
        }
        if (selecionado.getFonteDespesaORC() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Fonte de Recurso é obrigatório."));
            controle = Boolean.FALSE;
        }
        if (selecionado.getClasseDiaria() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Classe da Diária é obrigatório."));
            controle = Boolean.FALSE;
        }
        if (selecionado.getTipoViagem() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Tipo de Viagem é obrigatório."));
            controle = Boolean.FALSE;
        }
        if (selecionado.getObjetivo() == "") {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo Objetivo é obrigatório."));
            controle = Boolean.FALSE;
        }
        if (getTotalIntegral() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " Teste"));
            controle = Boolean.FALSE;
        }

        return controle;
    }

    public Boolean getVerificaEdicao() {
        if (selecionado == null) {
            return false;
        }
        if (selecionado.getId() != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void salvar() {
        selecionado.setDespesaORC(componenteTreeDespesaORC.getDespesaORCSelecionada());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        if (validaCamposDiaria()) {
            if (listaPessoasDiariaCampo.isEmpty()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", " Para salvar é necessário adicionar um grupo de pessoa."));
            } else {
                propostaConcessaoDiariaFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(selecionado.getUnidadeOrganizacionalAdm(), selecionado.getUnidadeOrganizacional(), selecionado.getDataLancamento());
                if (operacao.equals(Operacoes.NOVO)) {
                    for (PessoaFisica pf : listaPessoasDiariaCampo) {
                        PropostaConcessaoDiaria pcc = new PropostaConcessaoDiaria();
                        pcc = selecionado;
                        pcc.setCodigo(propostaConcessaoDiariaFacade.getCodigoSequencialCampo(sistemaControlador.getExercicioCorrente()));
                        pcc.setPessoaFisica(pf);
                        pcc.setClasseCredor(pf.getClasseCredorPessoas().get(0).getClasseCredor());
                        pcc.setContaCorrenteBanc(pf.getContaCorrenteBancPessoas().get(0));
                        this.getFacede().salvar(pcc);
                    }
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                }
                FacesUtil.redirecionamentoInterno("/diaria/listar/");
            }
        }
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/diaria/listar/");
    }

    public List<GrupoDiaria> completaGrupoPessoas(String parte) {
        return propostaConcessaoDiariaFacade.getGrupoDiariaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public ConverterAutoComplete getConverterGrupoDiaria() {
        if (converterGrupoDiaria == null) {
            converterGrupoDiaria = new ConverterAutoComplete(GrupoDiaria.class, propostaConcessaoDiariaFacade.getGrupoDiariaFacade());
        }
        return converterGrupoDiaria;
    }

    public List<FonteDespesaORC> completaFonteDespesaORC(String parte) {
        if (componenteTreeDespesaORC.getDespesaORCSelecionada().getId() != null) {
            return propostaConcessaoDiariaFacade.getFonteDespesaORCFacade().completaFonteDespesaORC(parte.trim(), componenteTreeDespesaORC.getDespesaORCSelecionada());
        } else {
            return new ArrayList<FonteDespesaORC>();
        }
    }

    public boolean validaAdicionaDiariaPrestacao() {
        boolean controle = true;
        if (diariaPrestacaoContaselecionada.getNumeroNota().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", "O campo Nº do Documento é obrigatório para adicionar"));
            controle = false;
        }
        if (diariaPrestacaoContaselecionada.getDataValor() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", "O campo data é obrigatório para adicionar"));
            controle = false;
        }
        if (diariaPrestacaoContaselecionada.getRazaosocial().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", "O campo Razão Social é obrigatório para adicionar"));
            controle = false;
        }
        if (diariaPrestacaoContaselecionada.getCpfCnpj().equals("")) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", "O campo CNPJ é obrigatório para adicionar"));
            controle = false;
        }
        if (diariaPrestacaoContaselecionada.getValor().compareTo(BigDecimal.ZERO) == 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção! ", "O campo Valor deve ser maior que 0"));
            controle = false;
        }

        return controle;
    }

    public void adicionaDiariaPrestacao() {
        if (validaAdicionaDiariaPrestacao()) {
            diariaPrestacaoContaselecionada.setPropostaConcessaoDiaria(selecionado);
            selecionado.getDiariaPrestacaoContas().add(diariaPrestacaoContaselecionada);
            diariaPrestacaoContaselecionada = new DiariaPrestacaoConta();
        }
    }

    public void removeDiariaPrestacao() {
        PropostaConcessaoDiaria p = (PropostaConcessaoDiaria) selecionado;
        p.getDiariaPrestacaoContas().remove(diariaPrestacaoContaselecionada);
    }

    public void removerDocumento(ActionEvent evento) {
        DiariaPrestacaoConta diaria = (DiariaPrestacaoConta) evento.getComponent().getAttributes().get("objeto");
        PropostaConcessaoDiaria p = (PropostaConcessaoDiaria) selecionado;
        p.getDiariaPrestacaoContas().remove(diaria);
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return propostaConcessaoDiariaFacade.getPessoaFacade().listaFiltrandoPessoaComVinculoVigenteEPorTipoClasse(parte.trim(), TipoClasseCredor.DIARIA_CAMPO);
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, propostaConcessaoDiariaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<ContaCorrenteBancPessoa> completaContaCorrente(String parte) {
        return propostaConcessaoDiariaFacade.getContaCorrenteBancPessoaFacade().listaContasBancariasPorPessoa(pessoaSelecionada, parte.trim());
    }

    public ConverterAutoComplete getConverterContaCorrente() {
        if (converterContaCorrente == null) {
            converterContaCorrente = new ConverterAutoComplete(ContaCorrenteBancPessoa.class, propostaConcessaoDiariaFacade.getContaCorrenteBancPessoaFacade());
        }
        return converterContaCorrente;
    }

    public List<SelectItem> getMeioTransporte() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (MeioDeTransporte m : MeioDeTransporte.values()) {
            toReturn.add(new SelectItem(m, m.getDescricao()));
        }
        return toReturn;
    }

    public void validaSaldoFonteDespesaOrc(FacesContext context, UIComponent component, Object value) {
        FonteDespesaORC fd = (FonteDespesaORC) value;
        PropostaConcessaoDiaria pcd = ((PropostaConcessaoDiaria) selecionado);
        FacesMessage message = new FacesMessage();
        if (fd.getId() != null) {
            BigDecimal saldo = propostaConcessaoDiariaFacade.getFonteDespesaORCFacade().saldoRealPorFonte(fd, sistemaControlador.getDataOperacao(), sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
            if (pcd.getValor().compareTo(saldo) > 0) {
                message.setDetail("Saldo indisponível na fonte. A solicitação de empenho será gerada sem reserva de dotação.");
                message.setSummary("Saldo indisponível na fonte. A solicitação de empenho será gerada sem reserva de dotação.");
                message.setSeverity(FacesMessage.SEVERITY_WARN);
                FacesContext.getCurrentInstance().addMessage("Formulario:completaFonte", message);
            }
        }
    }

    public DiariaPrestacaoConta getDiariaPrestacaoContaselecionada() {
        return diariaPrestacaoContaselecionada;
    }

    public void setDiariaPrestacaoContaselecionada(DiariaPrestacaoConta diariaPrestacaoContaselecionada) {
        this.diariaPrestacaoContaselecionada = diariaPrestacaoContaselecionada;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return propostaConcessaoDiariaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte.trim(), pessoaSelecionada, TipoClasseCredor.DIARIA_CAMPO);
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, propostaConcessaoDiariaFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    public Date concatenaDiasHorasMinutos(Date data, String hora, String minuto) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String teste = sdf.format(data);
        teste = teste + " " + hora + ":" + minuto;
        Date sd = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(teste);
        return sd;
    }

    public int getDias() {
        return 0;
    }

    public String getMeiaDiaria() {
        return "";
    }

    public String getTotalDiasFormatado() {
        if (!getMeiaDiaria().equals("0")) {
            return getDias() + " " + getMeiaDiaria();
        } else {
            return getDias() + " ";
        }
    }

    public List<SelectItem> getClassesDiarias() {
        PropostaConcessaoDiaria pcd = ((PropostaConcessaoDiaria) selecionado);
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (ClasseDiaria c : propostaConcessaoDiariaFacade.getConfiguracaoDiariaFacade().listaClassDiariaConfiguracaoAtiva(pcd)) {
            toReturn.add(new SelectItem(c, c.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDeViagem() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        PropostaConcessaoDiaria pcd = ((PropostaConcessaoDiaria) selecionado);
        for (TipoViagem t : TipoViagem.values()) {
            if (pcd.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIACAMPO)) {
                if (t.equals(TipoViagem.ESTADUAL)) {
                    toReturn.add(new SelectItem(t, t.getDescricao()));
                }
            } else {
                toReturn.add(new SelectItem(t, t.getDescricao()));
            }
        }
        return toReturn;
    }

    public ConverterGenerico getConverterClasseDiaria() {
        if (converterClasseDiaria == null) {
            converterClasseDiaria = new ConverterGenerico(ClasseDiaria.class, propostaConcessaoDiariaFacade.getConfiguracaoDiariaFacade());
        }
        return converterClasseDiaria;
    }

    public BigDecimal getValorDaDiaria() {
        PropostaConcessaoDiaria p = (PropostaConcessaoDiaria) selecionado;
        BigDecimal valorDiaria = BigDecimal.ZERO;
        BigDecimal valorCotacao = BigDecimal.ZERO;
        if ((p.getClasseDiaria() != null) && (p.getTipoViagem() != null)) {
            if (p.getTipoViagem().equals(TipoViagem.ESTADUAL)) {
                valorDiaria = p.getClasseDiaria().getValorEstadual();
            } else if (p.getTipoViagem().equals(TipoViagem.INTERNACIONAL)) {
                valorDiaria = p.getClasseDiaria().getValorInternacional();
                valorCotacao = propostaConcessaoDiariaFacade.retornaCotacaoDolarDia(p.getDataLancamento());
                valorDiaria = valorDiaria.multiply(valorCotacao);
            } else {
                valorDiaria = p.getClasseDiaria().getValorNacional();
            }
        }
        return valorDiaria;
    }

    public ConverterAutoComplete getConverterFonteDespesaORC() {
        if (converterFonteDespesaORC == null) {
            converterFonteDespesaORC = new ConverterAutoComplete(FonteDespesaORC.class, propostaConcessaoDiariaFacade.getFonteDespesaORCFacade());
        }
        return converterFonteDespesaORC;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(TipoProposta.CONCESSAO_DIARIACAMPO, TipoProposta.CONCESSAO_DIARIACAMPO.getDescricao()));
        toReturn.add(new SelectItem(TipoProposta.CONCESSAO_DIARIA, TipoProposta.CONCESSAO_DIARIA.getDescricao()));
        return toReturn;
    }


    public boolean validaAdicionarPessoa() {
        boolean controle = true;
        if (grupoDiaria == null && grupoOuPessoa.equals(true)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar! ", " O campo Grupo de Pessoas é obrigatório ao adicionar."));
            return false;
        }
        if (pessoaSelecionada == null && grupoOuPessoa.equals(false)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar! ", " O campo Pessoa é obrigatório ao adicionar."));
            return false;
        }
        if (classeCredorSelecionada == null && grupoOuPessoa.equals(false)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar! ", " O campo Classe é obrigatório ao adicionar."));
            return false;
        }
        if (contaCorrenteBancPessoaSelecionada == null && grupoOuPessoa.equals(false)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar! ", " O campo Conta Corrente é obrigatório ao adicionar."));
            return false;
        } else {
            for (PessoaFisica g : listaPessoasDiariaCampo) {
                if (pessoaSelecionada != null) {
                    if (g.getCpf_Cnpj().equals(pessoaSelecionada.getCpf_Cnpj())) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar! ", "A Pessoa " + pessoaSelecionada + " já foi adicionada na lista"));
                        controle = false;
                    }
                } else if (grupoDiaria != null) {
                    grupoDiaria = propostaConcessaoDiariaFacade.getGrupoDiariaFacade().recuperar(grupoDiaria.getId());
                    for (GrupoPessoasDiarias gpd : grupoDiaria.getGrupoPessoasDiarias()) {
                        if (gpd.getPessoa().getCpf_Cnpj().equals(g.getCpf_Cnpj())) {
                            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar! ", "A Pessoa " + gpd.getPessoa() + " já foi adicionada na lista, favor corrigir a lista para adicionar este grupo"));
                            controle = false;
                        }
                    }
                }
            }
        }
        return controle;
    }


    public void adicionarPessoa() {
        if (validaAdicionarPessoa()) {
            if (grupoDiaria != null) {
                grupoDiaria = propostaConcessaoDiariaFacade.getGrupoDiariaFacade().recuperar(grupoDiaria.getId());
                for (GrupoPessoasDiarias dp : grupoDiaria.getGrupoPessoasDiarias()) {
                    PessoaFisica pfff = (PessoaFisica) dp.getPessoa();
                    pfff = (PessoaFisica) propostaConcessaoDiariaFacade.getPessoaFacade().recuperarPF(pfff.getId());
                    listaPessoasDiariaCampo.add(pfff);
                }
            } else if (pessoaSelecionada != null) {
                ClasseCredorPessoa ccp = new ClasseCredorPessoa();
                ccp.setClasseCredor(classeCredorSelecionada);
                ccp.setPessoa(pessoaSelecionada);
                pessoaSelecionada = (PessoaFisica) propostaConcessaoDiariaFacade.getPessoaFacade().recuperarPF(pessoaSelecionada.getId());
                pessoaSelecionada.getClasseCredorPessoas().add(ccp);
                pessoaSelecionada.getContaCorrenteBancPessoas().add(contaCorrenteBancPessoaSelecionada);
                listaPessoasDiariaCampo.add(pessoaSelecionada);
                classeCredorSelecionada = null;
                pessoaSelecionada = null;
                contaCorrenteBancPessoaSelecionada = null;
            }
            grupoDiaria = null;
        }

    }

    public void removePessoa(ActionEvent evt) {
        pessoaSelecionada = (PessoaFisica) evt.getComponent().getAttributes().get("objeto");
        listaPessoasDiariaCampo.remove(pessoaSelecionada);
        pessoaSelecionada = new PessoaFisica();
    }

    public void limpaTabelaPessoa() {
        listaPessoasDiariaCampo.clear();
    }

    public BigDecimal getTotalIntegral() {
        BigDecimal total;
        total = getValorDaDiaria();
        total = total.multiply(new BigDecimal(getDias()));

        return total;
    }

    public BigDecimal getTotalValorMeia() {
        if (!getMeiaDiaria().contains("0")) {
            return getValorDaDiaria().divide(new BigDecimal(2));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getValorTotaGeralDiarias() {
        BigDecimal total = getTotalIntegral().add(getTotalValorMeia());
        PropostaConcessaoDiaria p = (PropostaConcessaoDiaria) selecionado;
        p.setValor(total);
        return total;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public List<PessoaFisica> getListaPessoasDiariaCampo() {
        return listaPessoasDiariaCampo;
    }

    public void setListaPessoasDiariaCampo(List<PessoaFisica> listaPessoasDiariaCampo) {
        this.listaPessoasDiariaCampo = listaPessoasDiariaCampo;
    }

    public PessoaFisica getPessoaSelecionada() {
        return pessoaSelecionada;
    }

    public void setPessoaSelecionada(PessoaFisica pessoaSelecionada) {
        this.pessoaSelecionada = pessoaSelecionada;
    }

    public ClasseCredor getClasseCredorSelecionada() {
        return classeCredorSelecionada;
    }

    public void setClasseCredorSelecionada(ClasseCredor classeCredorSelecionada) {
        this.classeCredorSelecionada = classeCredorSelecionada;
    }

    public ContaCorrenteBancPessoa getContaCorrenteBancPessoaSelecionada() {
        return contaCorrenteBancPessoaSelecionada;
    }

    public void setContaCorrenteBancPessoaSelecionada(ContaCorrenteBancPessoa contaCorrenteBancPessoaSelecionada) {
        this.contaCorrenteBancPessoaSelecionada = contaCorrenteBancPessoaSelecionada;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/diaria-grupo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public GrupoDiaria getGrupoDiaria() {
        return grupoDiaria;
    }

    public void setGrupoDiaria(GrupoDiaria grupoDiaria) {
        this.grupoDiaria = grupoDiaria;
    }

    public Boolean getGrupoOuPessoa() {
        return grupoOuPessoa;
    }

    public void setGrupoOuPessoa(Boolean grupoOuPessoa) {
        this.grupoOuPessoa = grupoOuPessoa;
    }
}
