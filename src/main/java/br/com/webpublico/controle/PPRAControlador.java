package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PPRAFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 11/08/15.
 */
@ManagedBean(name = "ppraControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarPPRA", pattern = "/ppra/listar/", viewId = "/faces/rh/administracaodepagamento/ppra/lista.xhtml"),
                @URLMapping(id = "criarPPRA", pattern = "/ppra/novo/", viewId = "/faces/rh/administracaodepagamento/ppra/edita.xhtml"),
                @URLMapping(id = "editarPPRA", pattern = "/ppra/editar/#{ppraControlador.id}/", viewId = "/faces/rh/administracaodepagamento/ppra/edita.xhtml"),
                @URLMapping(id = "verPPRA", pattern = "/ppra/ver/#{ppraControlador.id}/", viewId = "/faces/rh/administracaodepagamento/ppra/visualizar.xhtml")
        }
)
public class PPRAControlador extends PrettyControlador<PPRA> implements CRUD {
    @EJB
    private PPRAFacade ppraFacade;
    private Integer indiceAba;
    private IdentificacaoRisco identificacaoRisco;
    private ReconhecimentoRisco reconhecimentoRisco;
    private MedidaDeControlePPRA medidaDeControlePPRA;
    private ConverterAutoComplete converterFatorIdentificacao;
    private ConverterAutoComplete converterFatorReconhecimento;
    private ConverterAutoComplete converterFatorMedida;
    private AvaliacaoQuantitativaPPRA avaliacaoQuantitativaPPRA;
    private ProgramaPPRA programaPPRA;

    public PPRAControlador() {
        super(PPRA.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ppra/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return ppraFacade;
    }

    @Override
    @URLAction(mappingId = "criarPPRA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        indiceAba = 0;
        identificacaoRisco = new IdentificacaoRisco();
        reconhecimentoRisco = new ReconhecimentoRisco();
        avaliacaoQuantitativaPPRA = new AvaliacaoQuantitativaPPRA();
        medidaDeControlePPRA = new MedidaDeControlePPRA();
        programaPPRA = new ProgramaPPRA();
        super.novo();
    }

    @Override
    @URLAction(mappingId = "editarPPRA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        indiceAba = 0;
        identificacaoRisco = new IdentificacaoRisco();
        reconhecimentoRisco = new ReconhecimentoRisco();
        avaliacaoQuantitativaPPRA = new AvaliacaoQuantitativaPPRA();
        medidaDeControlePPRA = new MedidaDeControlePPRA();
        programaPPRA = new ProgramaPPRA();
        super.editar();
    }

    @Override
    @URLAction(mappingId = "verPPRA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (isValidaPPRA()) {
            super.salvar();
        }
    }

//    ##################################### SELECT ITENS #############################################################

    public List<SelectItem> tipoRiscoIdentificacao() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (Risco risco : ppraFacade.getRiscoFacade().listaRiscos()) {
            tipo.add(new SelectItem(risco, risco.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoRiscoReconhecimento() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (Risco risco : ppraFacade.getRiscoFacade().listaRiscos()) {
            tipo.add(new SelectItem(risco, risco.toString()));
        }
        return tipo;
    }

    public List<SelectItem> tipoRiscoMedida() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (Risco risco : ppraFacade.getRiscoFacade().listaRiscos()) {
            tipo.add(new SelectItem(risco, risco.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoItensidade() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (IntensidadeAvaliacaoQuantitativaPPRA intensidadeAvaliacaoQuantitativaPPRA : IntensidadeAvaliacaoQuantitativaPPRA.values()) {
            tipo.add(new SelectItem(intensidadeAvaliacaoQuantitativaPPRA, intensidadeAvaliacaoQuantitativaPPRA.getDescricao()));
        }
        return tipo;
    }

    public List<SelectItem> tipoTecnica() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (TecnicaAvaliacaoQuantitativaPPRA tecnicaAvaliacaoQuantitativaPPRA : TecnicaAvaliacaoQuantitativaPPRA.values()) {
            tipo.add(new SelectItem(tecnicaAvaliacaoQuantitativaPPRA, tecnicaAvaliacaoQuantitativaPPRA.getDescricao()));
        }
        return tipo;
    }

    public List<SelectItem> tipoMedidaEficaz() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (MedidaDeControleEficaz medidaDeControleEficaz : MedidaDeControleEficaz.values()) {
            tipo.add(new SelectItem(medidaDeControleEficaz, medidaDeControleEficaz.getDescricao()));
        }
        return tipo;
    }


    public List<SelectItem> parteCorpo() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (PartesCorpoHumano corpoHumano : PartesCorpoHumano.values()) {
            tipo.add(new SelectItem(corpoHumano, corpoHumano.getDescricao()));
        }

        return tipo;
    }

//    ####################################### METODO AUTOCOMPLETE ####################################################

    public List<UnidadeOrganizacional> completaUnidade(String filtro) {
        return ppraFacade.getUnidadeOrganizacionalFacade().buscarUnidadePorTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional.ADMINISTRATIVA, filtro.toLowerCase());
    }

    public List<EquipamentoEPC> completaEPC(String parte) {
        return ppraFacade.getEquipamentoEPCFacade().listaEquipamentoEPCFiltrando(parte);

    }

    public List<FatorDeRisco> completaFatorRiscoIdentificacao(String parte) {
        return ppraFacade.getFatorDeRiscoFacade().completaFatorRisco(parte, identificacaoRisco.getRisco());
    }

    public List<FatorDeRisco> completaFatorRiscoReconhecimento(String parte) {
        return ppraFacade.getFatorDeRiscoFacade().completaFatorRisco(parte, reconhecimentoRisco.getRisco());
    }

    public List<FatorDeRisco> completaFatorRiscoMedida(String parte) {
        return ppraFacade.getFatorDeRiscoFacade().completaFatorRisco(parte, medidaDeControlePPRA.getRisco());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return ppraFacade.getPessoaFisicaFacade().listaPessoaFisica(parte);
    }

//    ##################################### BOTAO ADICIONAR ##########################################################

    public void adicionaIdentificacao() {
        identificacaoRisco.setPpra(selecionado);
        if (isValidaIdentificacao()) {
            selecionado.getIdentificacaoRiscos().add(identificacaoRisco);
            identificacaoRisco = new IdentificacaoRisco();
        }
    }

    public void adicionaReconhecimento() {
        reconhecimentoRisco.setPpra(selecionado);
        if (isValidaReconhecimento()) {
            selecionado.getReconhecimentoRiscos().add(reconhecimentoRisco);
            reconhecimentoRisco = new ReconhecimentoRisco();
        }
    }

    public void adicionaAvaliacao() {
        avaliacaoQuantitativaPPRA.setPpra(selecionado);
        if (isValidaAvaliacao()) {
            selecionado.getAvaliacaoQuantitativaPPRAs().add(avaliacaoQuantitativaPPRA);
            avaliacaoQuantitativaPPRA = new AvaliacaoQuantitativaPPRA();
        }
    }

    public void adicionaMedida() {
        medidaDeControlePPRA.setPpra(selecionado);
        if (isValidaMedida()) {
            selecionado.getMedidaDeControlePPRAs().add(medidaDeControlePPRA);
            medidaDeControlePPRA = new MedidaDeControlePPRA();
        }
    }

    public void adicionaProgramaPPRA() {
        programaPPRA.setPpra(selecionado);
        if (isValidaProgramaPPRA()) {
            selecionado.getProgramaPPRAs().add(programaPPRA);
            programaPPRA = new ProgramaPPRA();
        }
    }

//    ################################## BOTAO REMOVER ###############################################################

    public void removerIdentificacao(IdentificacaoRisco identificacaoRisco) {
        selecionado.getIdentificacaoRiscos().remove(identificacaoRisco);
    }

    public void removerReconhecimento(ReconhecimentoRisco reconhecimentoRisco) {
        selecionado.getReconhecimentoRiscos().remove(reconhecimentoRisco);
    }

    public void removerAvaliacao(AvaliacaoQuantitativaPPRA avaliacaoQuantitativaPPRA) {
        selecionado.getAvaliacaoQuantitativaPPRAs().remove(avaliacaoQuantitativaPPRA);
    }

    public void removerMedida(MedidaDeControlePPRA medidaDeControlePPRA) {
        selecionado.getMedidaDeControlePPRAs().remove(medidaDeControlePPRA);
    }

    public void removerProgramaPPRA(ProgramaPPRA programaPPRA) {
        selecionado.getProgramaPPRAs().remove(programaPPRA);
    }

//    ############################################## VALIDACOES #######################################################

    public boolean isValidaPPRA() {
        boolean valida = true;
        if (!Util.validaCampos(selecionado)) {
            valida = false;
        }
        if (selecionado.getIdentificacaoRiscos().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar ao menos uma Identificação de Risco.");
            valida = false;
        }
        if (selecionado.getReconhecimentoRiscos().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar ao menos um Reconhecimento de Risco.");
            valida = false;
        }
        if (selecionado.getAvaliacaoQuantitativaPPRAs().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar ao menos uma Avaliação Quantitativa.");
            valida = false;
        }
        if (selecionado.getMedidaDeControlePPRAs().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar ao menos uma Medida de Controle.");
            valida = false;
        }
        if (selecionado.getProgramaPPRAs().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar ao menos um PPRA.");
            valida = false;
        }
        return valida;
    }

    public boolean isValidaIdentificacao() {
        boolean valida = true;
        if (!Util.validaCampos(identificacaoRisco)) {
            valida = false;
        }
        for (IdentificacaoRisco risco : selecionado.getIdentificacaoRiscos()) {
            if (risco.getDataVistoria().equals(identificacaoRisco.getDataVistoria())
                    && risco.getRisco().getCodigo().equals(identificacaoRisco.getRisco().getCodigo())
                    && risco.getFatorDeRisco().getCodigo().equals(identificacaoRisco.getFatorDeRisco().getCodigo())
                    && risco.getLocalVistoriado().equals(identificacaoRisco.getLocalVistoriado())) {
                FacesUtil.addOperacaoNaoPermitida("Os parâmetros informados já constam na lista.");
                return false;
            }
        }
        return valida;
    }

    public boolean isValidaAvaliacao() {
        boolean valida = true;
        if (!Util.validaCampos(avaliacaoQuantitativaPPRA)) {
            valida = false;
        }
        for (AvaliacaoQuantitativaPPRA quantitativaPPRA : selecionado.getAvaliacaoQuantitativaPPRAs()) {
            if (quantitativaPPRA.getIntensidade().equals(avaliacaoQuantitativaPPRA.getIntensidade())
                    && quantitativaPPRA.getTecnica().equals(avaliacaoQuantitativaPPRA.getTecnica())
                    && quantitativaPPRA.getTolerancia().equals(avaliacaoQuantitativaPPRA.getTolerancia())
                    && quantitativaPPRA.getExposicao().equals(avaliacaoQuantitativaPPRA.getExposicao())
                    && quantitativaPPRA.getQuantidade().equals(avaliacaoQuantitativaPPRA.getQuantidade())){
                FacesUtil.addOperacaoNaoPermitida("Os parâmetros informados já constam na lista.");
                valida = false;
            }
        }
        return valida;
    }

    public boolean isValidaMedida() {
        boolean valida = true;
        if (!Util.validaCampos(medidaDeControlePPRA)) {
            valida = false;
        }
        for (MedidaDeControlePPRA medidaControle : selecionado.getMedidaDeControlePPRAs()) {
            if (medidaControle.getRisco().equals(medidaDeControlePPRA.getRisco())
                    && medidaControle.getFatorDeRisco().equals(medidaDeControlePPRA.getFatorDeRisco())
                    && medidaControle.getEquipamentoEPC().equals(medidaDeControlePPRA.getEquipamentoEPC())) {
                FacesUtil.addOperacaoNaoPermitida("Os parâmetros informados já constam na lista.");
                valida = false;
            }
        }

        return valida;
    }

    public boolean isValidaReconhecimento() {
        boolean valida = true;
        if (!Util.validaCampos(reconhecimentoRisco)) {
            valida = false;
        }
        for (ReconhecimentoRisco risco : selecionado.getReconhecimentoRiscos()) {
            if (risco.getRisco().getCodigo().equals(reconhecimentoRisco.getRisco().getCodigo())
                    && risco.getFatorDeRisco().getCodigo().equals(reconhecimentoRisco.getFatorDeRisco().getCodigo())
                    && risco.getFonteGeradora().equals(reconhecimentoRisco.getFonteGeradora())
                    && risco.getPartesCorpoHumano().equals(reconhecimentoRisco.getPartesCorpoHumano())
                    && risco.getMeioDePropagacao().equals(reconhecimentoRisco.getMeioDePropagacao())
                    && risco.getDanoSaude().equals(reconhecimentoRisco.getDanoSaude())) {
                FacesUtil.addOperacaoNaoPermitida("Os parâmetros informados já constam na lista.");
                valida = false;
            }
        }
        return valida;
    }

    public Boolean isValidaProgramaPPRA() {
        Boolean valida = true;
        if (!Util.validaCampos(programaPPRA)) {
            valida = false;
        }
        for (ProgramaPPRA ppra : selecionado.getProgramaPPRAs()) {
            if (ppra.getNomePrograma().equals(programaPPRA.getNomePrograma())
                    && ppra.getDescricao().equals(programaPPRA.getDescricao())
                    && ppra.getObjetivo().equals(programaPPRA.getObjetivo())
                    && ppra.getPrazo().equals(programaPPRA.getPrazo())
                    && ppra.getPessoaFisica().equals(programaPPRA.getPessoaFisica())
                    && ppra.getConclusao().equals(programaPPRA.getConclusao())) {
                FacesUtil.addCampoObrigatorio("Os parâmetros informados já constam na lista.");
                valida = false;
            }
        }
        return valida;
    }

//    ###################################### CONVERTER ################################################################

    public ConverterAutoComplete getConverterFatorIdentificacao() {
        if (converterFatorIdentificacao == null) {
            converterFatorIdentificacao = new ConverterAutoComplete(FatorDeRisco.class, ppraFacade.getFatorDeRiscoFacade());
        }
        return converterFatorIdentificacao;
    }

    public ConverterAutoComplete getConverterFatorReconhecimento() {
        if (converterFatorReconhecimento == null) {
            converterFatorReconhecimento = new ConverterAutoComplete(FatorDeRisco.class, ppraFacade.getFatorDeRiscoFacade());
        }
        return converterFatorReconhecimento;
    }

    public ConverterAutoComplete getConverterFatorMedida() {
        if (converterFatorMedida == null) {
            converterFatorMedida= new ConverterAutoComplete(FatorDeRisco.class, ppraFacade.getFatorDeRiscoFacade());
        }
        return converterFatorReconhecimento;
    }

//    ######################################### GETTER E SETTER ######################################################

    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public IdentificacaoRisco getIdentificacaoRisco() {
        return identificacaoRisco;
    }

    public void setIdentificacaoRisco(IdentificacaoRisco identificacaoRisco) {
        this.identificacaoRisco = identificacaoRisco;
    }

    public ReconhecimentoRisco getReconhecimentoRisco() {
        return reconhecimentoRisco;
    }

    public void setReconhecimentoRisco(ReconhecimentoRisco reconhecimentoRisco) {
        this.reconhecimentoRisco = reconhecimentoRisco;
    }

    public AvaliacaoQuantitativaPPRA getAvaliacaoQuantitativaPPRA() {
        return avaliacaoQuantitativaPPRA;
    }

    public void setAvaliacaoQuantitativaPPRA(AvaliacaoQuantitativaPPRA avaliacaoQuantitativaPPRA) {
        this.avaliacaoQuantitativaPPRA = avaliacaoQuantitativaPPRA;
    }

    public MedidaDeControlePPRA getMedidaDeControlePPRA() {
        return medidaDeControlePPRA;
    }

    public void setMedidaDeControlePPRA(MedidaDeControlePPRA medidaDeControlePPRA) {
        this.medidaDeControlePPRA = medidaDeControlePPRA;
    }

    public ProgramaPPRA getProgramaPPRA() {
        return programaPPRA;
    }

    public void setProgramaPPRA(ProgramaPPRA programaPPRA) {
        this.programaPPRA = programaPPRA;
    }
}
