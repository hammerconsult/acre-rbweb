package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacional;
import br.com.webpublico.entidades.rh.saudeservidor.RiscoOcupacionalASO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ASOFacade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.saudeservidor.RiscoOcupacionalFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
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
 * Created by carlos on 22/06/15.
 */
@ManagedBean(name = "asoControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "listarASO", pattern = "/aso/listar/", viewId = "/faces/rh/administracaodepagamento/aso/lista.xhtml"),
        @URLMapping(id = "criarASO", pattern = "/aso/novo/", viewId = "/faces/rh/administracaodepagamento/aso/edita.xhtml"),
        @URLMapping(id = "editarASO", pattern = "/aso/editar/#{asoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/aso/edita.xhtml"),
        @URLMapping(id = "verASO", pattern = "/aso/ver/#{asoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/aso/visualizar.xhtml")
    }
)
public class ASOControlador extends PrettyControlador<ASO> implements CRUD {

    @EJB
    private ASOFacade asoFacade;
    @EJB
    private RiscoOcupacionalFacade riscoOcupacionalFacade;
    private Integer indiceAba;
    private ExameComplementar exameComplementar;
    private RiscoOcupacional riscoOcupacional;
    private EquipamentoPCMSO equipamentoPCMSO;
    private String existeRisco;

    public ASOControlador() {
        super(ASO.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/aso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return asoFacade;
    }

    @Override
    @URLAction(mappingId = "criarASO", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();

        indiceAba = 0;
        existeRisco = null;
        Integer indice = (Integer) Web.pegaDaSessao(Integer.class);
        if (indice != null) {
            indiceAba = indice;
        }

        exameComplementar = (ExameComplementar) Web.pegaDaSessao(ExameComplementar.class);
        if (exameComplementar == null) {
            exameComplementar = new ExameComplementar();
        }

        riscoOcupacional = (RiscoOcupacional) Web.pegaDaSessao(RiscoOcupacional.class);
        if (riscoOcupacional == null) {
            riscoOcupacional = new RiscoOcupacional();
        }

        equipamentoPCMSO = (EquipamentoPCMSO) Web.pegaDaSessao(EquipamentoPCMSO.class);
        if (equipamentoPCMSO == null) {
            equipamentoPCMSO = new EquipamentoPCMSO();
        }
    }

    @Override
    @URLAction(mappingId = "verASO", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarASO", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        exameComplementar = new ExameComplementar();
        riscoOcupacional = new RiscoOcupacional();
        equipamentoPCMSO = new EquipamentoPCMSO();
        indiceAba = 0;
    }

    @Override
    public void salvar() {
        try {
            temExameComplementar();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void definirUnidadeOrganizacional() {
        selecionado.setUnidadeOrganizacional(selecionado.getContratoFP().getUnidadeOrganizacional());
    }

    public List<ContratoFP> completaServidor(String parte) {
        return asoFacade.getContratoFPFacade().recuperaContratoMatricula(parte);
    }

    public List<UnidadeOrganizacional> completaUnidade(String parte) {
        return asoFacade.getUnidadeOrganizacionalFacade().listaTodosPorFiltro(parte);
    }

    public List<Medico> completaMedico(String parte) {
        return asoFacade.getMedicoFacade().listaFiltrandoMedico(parte);
    }

    public void navegaMedico() {
        Web.navegacao(getUrlAtual(), "/medico/novo/", selecionado, indiceAba);
    }

    public List<Exame> completaExame(String parte) {
        return asoFacade.getExameFacade().completaExame(parte);
    }

    public void removerExame(ExameComplementar exameComplementar) {
        selecionado.getExameComplementares().remove(exameComplementar);
    }

    public List<EquipamentoEPI> completaEquipamentoEPI(String parte) {
        if (equipamentoPCMSO.getProtecaoEPI() != null) {
            return asoFacade.getEquipamentoEPIFacade().completaEquipamentoEPI(parte, equipamentoPCMSO.getProtecaoEPI());
        } else {
            return null;
        }
    }

    public void navegaFatorDeRisco() {
        Web.navegacao(getUrlAtual(), "/fator-risco/novo/", selecionado, riscoOcupacional, indiceAba);
    }

    public void navegaEquipamentoEPI() {
        Web.navegacao(getUrlAtual(), "/equipamento-epi/novo/", selecionado, equipamentoPCMSO, indiceAba);
    }

    public void adicionaRisco() {
        try {
            RiscoOcupacionalASO risco = criarRiscoAso();
            selecionado.getRiscos().add(risco);
            riscoOcupacional = new RiscoOcupacional();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private RiscoOcupacionalASO criarRiscoAso() {
        RiscoOcupacionalASO risco = new RiscoOcupacionalASO();
        risco.setAso(selecionado);
        risco.setRiscoOcupacional(riscoOcupacional);
        return risco;
    }


    public void adicionaEquipamento() {
        if (validaEquipamento()) {
            equipamentoPCMSO.setAso(selecionado);
            selecionado.getEquipamentosPCMSO().add(equipamentoPCMSO);
            equipamentoPCMSO = new EquipamentoPCMSO();
        }
    }

    public boolean validaEquipamento() {
        boolean valida = true;

        for (EquipamentoPCMSO equipamentoPCMSO : selecionado.getEquipamentosPCMSO()) {
            if (this.equipamentoPCMSO.getProtecaoEPI().getId().equals(equipamentoPCMSO.getProtecaoEPI().getId())
                && this.equipamentoPCMSO.getEquipamentoEPI().getId().equals(equipamentoPCMSO.getEquipamentoEPI().getId())) {
                FacesUtil.addOperacaoNaoPermitida("O Equipamento já consta na lista.");
                valida = false;
            }
        }

        return valida;
    }

    public void removerRisco(RiscoOcupacionalASO risco) {
        selecionado.getRiscos().remove(risco);
    }

    public void removerEquipamento(EquipamentoPCMSO equipamentoPCMSO) {
        selecionado.getEquipamentosPCMSO().remove(equipamentoPCMSO);
    }

    public void adicionaExame() {
        try {
            validarExame();
            exameComplementar.setAso(selecionado);
            selecionado.getExameComplementares().add(exameComplementar);
            exameComplementar = new ExameComplementar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void temExameComplementar(){
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getExameComplementares().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório adicionar ao menos um exame complementar.");
        }
        ve.lancarException();
    }

    private void validarExame() {

        ValidacaoException ve = new ValidacaoException();
        if (exameComplementar.getDataExame() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data do Exame deve ser informado.");
        }
        for (ExameComplementar complementar : selecionado.getExameComplementares()) {
            if (exameComplementar.getExame() != null && (exameComplementar.getExame().getCodigo().equals(complementar.getExame().getCodigo()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Exame selecionado já foi inserido na lista.");
                break;
            }
        }
        if (exameComplementar.getProcedimentoDiagnostico() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Procedimento Diagnóstico deve ser informado.");
        } else if (exameComplementar.getProcedimentoDiagnostico().getObservacaoObrigatoria() &&
            Strings.isNullOrEmpty(exameComplementar.getObservacaoProcesso())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Obervação do Procedimentodeve ser informado.");

        }
        ve.lancarException();
    }

    public void navegaExame() {
        Web.navegacao(getUrlAtual(), "/exame/novo/", selecionado, exameComplementar, indiceAba);
    }

    public List<SelectItem> tipoSexo() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (Sexo sexo : Sexo.values()) {
            tipo.add(new SelectItem(sexo, sexo.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoJornadaTrabalho() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (JornadaDeTrabalho jornada : asoFacade.getJornadaDeTrabalhoFacade().lista()) {
            tipo.add(new SelectItem(jornada, jornada.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoDescansoSemanal() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (DescansoSemanal descansoSemanal : DescansoSemanal.values()) {
            tipo.add(new SelectItem(descansoSemanal, descansoSemanal.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoSituacao() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (SituacaoASO situacaoASO : SituacaoASO.values()) {
            tipo.add(new SelectItem(situacaoASO, situacaoASO.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoExame() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (TipoExame tipoExame : TipoExame.values()) {
            tipo.add(new SelectItem(tipoExame, tipoExame.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoSanguineo() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (TipoSanguineo tipoSanguineo : TipoSanguineo.values()) {
            tipo.add(new SelectItem(tipoSanguineo, tipoSanguineo.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoLesao() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (Lesoes lesoes : Lesoes.values()) {
            tipo.add(new SelectItem(lesoes, lesoes.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoRisco() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (Risco risco : asoFacade.getRiscoFacade().listaRiscos()) {
            tipo.add(new SelectItem(risco, risco.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoRiscoOcupacional() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (RiscosOcupacionais riscosOcupacionais : RiscosOcupacionais.values()) {
            tipo.add(new SelectItem(riscosOcupacionais, riscosOcupacionais.toString()));
        }

        return tipo;
    }

    public List<SelectItem> tipoProtecao() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));
        for (ProtecaoEPI protecaoEPI : asoFacade.getProtecaoEPIFacade().listaProtecaoEPI()) {
            tipo.add(new SelectItem(protecaoEPI, protecaoEPI.getNome()));
        }

        return tipo;
    }


    public Integer getIndiceAba() {
        return indiceAba;
    }

    public void setIndiceAba(Integer indiceAba) {
        this.indiceAba = indiceAba;
    }

    public ExameComplementar getExameComplementar() {
        return exameComplementar;
    }

    public void setExameComplementar(ExameComplementar exameComplementar) {
        this.exameComplementar = exameComplementar;
    }

    public RiscoOcupacional getRiscoOcupacional() {
        return riscoOcupacional;
    }

    public void setRiscoOcupacional(RiscoOcupacional riscoOcupacional) {
        this.riscoOcupacional = riscoOcupacional;
    }

    public String getExisteRisco() {
        return existeRisco;
    }

    public void setExisteRisco(String existeRisco) {
        this.existeRisco = existeRisco;
    }

    public EquipamentoPCMSO getEquipamentoPCMSO() {
        return equipamentoPCMSO;
    }

    public void setEquipamentoPCMSO(EquipamentoPCMSO equipamentoPCMSO) {
        this.equipamentoPCMSO = equipamentoPCMSO;
    }

    public List<RiscoOcupacional> buscarRiscoOcupacionalPorVInculoFP(String parte) {
        if (selecionado.getContratoFP() != null) {
            return riscoOcupacionalFacade.buscarRiscoOcupacionalPorVinculoFP(selecionado.getContratoFP(), parte.trim());
        }
        return null;
    }
    @Override
    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
            if (selecionado.getRiscos() != null) {
                for (RiscoOcupacionalASO riscoAso : selecionado.getRiscos()) {
                    RevisaoAuditoria revisaoAuditoria = buscarUltimaAuditoria(ItemCalendarioFP.class, riscoAso.getRiscoOcupacional().getId());
                    if (ultimaRevisao == null || (revisaoAuditoria != null && revisaoAuditoria.getDataHora().after(ultimaRevisao.getDataHora()))) {
                        ultimaRevisao = revisaoAuditoria;
                    }
                }
            }
        }
        return ultimaRevisao;
    }
}
