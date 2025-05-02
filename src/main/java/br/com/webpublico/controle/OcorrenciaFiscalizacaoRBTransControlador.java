/**
 *
 * @author Andre
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MedidaAdministrativaFacade;
import br.com.webpublico.negocios.OcorrenciaFiscalizacaoRBTransFacade;
import br.com.webpublico.negocios.TributoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@ManagedBean(name = "ocorrenciaFiscalizacaoRBTransControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoOcorrenciaFiscalizacao", pattern = "/infracoes-e-penalidades/novo/", viewId = "/faces/tributario/rbtrans/fiscalizacao/ocorrencia/edita.xhtml"),
        @URLMapping(id = "editarOcorrenciaFiscalizacao", pattern = "/infracoes-e-penalidades/editar/#{ocorrenciaFiscalizacaoRBTransControlador.id}/", viewId = "/faces/tributario/rbtrans/fiscalizacao/ocorrencia/edita.xhtml"),
        @URLMapping(id = "listarOcorrenciaFiscalizacao", pattern = "/infracoes-e-penalidades/listar/", viewId = "/faces/tributario/rbtrans/fiscalizacao/ocorrencia/lista.xhtml"),
        @URLMapping(id = "verOcorrenciaFiscalizacao", pattern = "/infracoes-e-penalidades/ver/#{ocorrenciaFiscalizacaoRBTransControlador.id}/", viewId = "/faces/tributario/rbtrans/fiscalizacao/ocorrencia/visualizar.xhtml"),
})
public class OcorrenciaFiscalizacaoRBTransControlador extends PrettyControlador<OcorrenciaFiscalizacaoRBTrans> implements Serializable, CRUD {

    @EJB
    private OcorrenciaFiscalizacaoRBTransFacade ocorrenciaFiscalizacaoRBTransFacade;
    @EJB
    private TributoFacade tributoFacade;
    private ConverterAutoComplete converterTributo;
    private List<MedidasOcorrencia> listaOcorrenciaPermissaoTaxi;
    private Boolean retencaoRegularizacao;
    private Boolean retencaoVeiculo;
    private Boolean apreensaoVeiculo;
    private Boolean remocaoVeiculo;
    private List<TipoPermissaoTaxi> penalidadeTaxiSelecionadas;
    private List<TipoPermissaoMotoTaxi> penalidadeMotoTaxiSelecionado;
    private String tipoInfrator;
    @EJB
    private MedidaAdministrativaFacade medidaAdministrativaFacade;
    private ConverterAutoComplete converterMedidasAdministrativas;
    private MedidasOcorrencia medidaOcorrencia;
    private MedidaAdministrativa medidaAdministrativa;
    private MedidasOcorrencia antigaMedidaOcorrencia;
    private OcorrenciaFiscalizacaoRBTrans antigaOcorrencia;
    protected List lista;

    public MedidasOcorrencia getMedidaOcorrencia() {
        return medidaOcorrencia;
    }

    public void setMedidaOcorrencia(MedidasOcorrencia medidaOcorrencia) {
        this.medidaOcorrencia = medidaOcorrencia;
    }

    public MedidaAdministrativa getMedidaAdministrativa() {
        return medidaAdministrativa;
    }

    public void setMedidaAdministrativa(MedidaAdministrativa medidaAdministrativa) {
        this.medidaAdministrativa = medidaAdministrativa;
    }

    public String getTipoInfrator() {
        return tipoInfrator;
    }

    public void setTipoInfrator(String tipoInfrator) {
        this.tipoInfrator = tipoInfrator;
    }

    public List<MedidasOcorrencia> getListaOcorrenciaPermissaoTaxi() {
        return listaOcorrenciaPermissaoTaxi;
    }

    public void setListaOcorrenciaPermissaoTaxi(List<MedidasOcorrencia> listaOcorrenciaPermissaoTaxi) {
        this.listaOcorrenciaPermissaoTaxi = listaOcorrenciaPermissaoTaxi;
    }

    public Boolean getApreensaoVeiculo() {
        return apreensaoVeiculo;
    }

    public void setApreensaoVeiculo(Boolean apreensaoVeiculo) {
        this.apreensaoVeiculo = apreensaoVeiculo;
    }

    public Boolean getRemocaoVeiculo() {
        return remocaoVeiculo;
    }

    public void setRemocaoVeiculo(Boolean remocaoVeiculo) {
        this.remocaoVeiculo = remocaoVeiculo;
    }

    public Boolean getRetencaoRegularizacao() {
        return retencaoRegularizacao;
    }

    public void setRetencaoRegularizacao(Boolean retencaoRegularizacao) {
        this.retencaoRegularizacao = retencaoRegularizacao;
    }

    public Boolean getRetencaoVeiculo() {
        return retencaoVeiculo;
    }

    public void setRetencaoVeiculo(Boolean retencaoVeiculo) {
        this.retencaoVeiculo = retencaoVeiculo;
    }

    public OcorrenciaFiscalizacaoRBTransControlador() {
        super(OcorrenciaFiscalizacaoRBTrans.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/infracoes-e-penalidades/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoOcorrenciaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(ocorrenciaFiscalizacaoRBTransFacade.sugereCodigo());
        penalidadeTaxiSelecionadas = new ArrayList<>();
        penalidadeMotoTaxiSelecionado = new ArrayList<>();
        medidaOcorrencia = new MedidasOcorrencia();
        medidaAdministrativa = new MedidaAdministrativa();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCodigo() == null || (selecionado.getCodigo().intValue() <= 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o código!");
        }
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Valor da infração em UFM!");
        }
        if ("".equals(selecionado.getDescricao().trim())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Descrição!");
        }
        if (selecionado.getConduta() == null || selecionado.getConduta().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Conduta!");
        }
        if (selecionado.getTributo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Receita referente a esta infração!");
        }
        if (!selecionado.getMotorista() && !selecionado.getPermissionario() && !isInfracaoClandestina()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Infrator!");
        }
        if (selecionado.getEspecieTransporte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Espécie de Transporte!");
        } else if (EspecieTransporte.MOTO_TAXI.equals(selecionado.getEspecieTransporte())) {
            if (selecionado.getGravidade() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe a Gravidade da infração para Moto-Táxi!");
            }
            if (EspecieTransporte.MOTO_TAXI.equals(selecionado.getEspecieTransporte())) {
                if (selecionado.getPontuacao() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("Informe a Pontuação da infração para Moto-Táxi!");
                }
            }

        } else if (EspecieTransporte.TAXI.equals(selecionado.getEspecieTransporte())) {
            if (selecionado.getGrupo() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("Informe o Grupo!");
            }

            if (EspecieTransporte.TAXI.equals(selecionado.getEspecieTransporte())) {
                selecionado.setPontuacao(null);
                selecionado.setGravidade(null);
            }
        }
        ve.lancarException();
    }


    @Override
    public void excluir() {
        try {
            if (ocorrenciaFiscalizacaoRBTransFacade.existeOcorrenciaNoProcessoDeAutuacao(antigaOcorrencia)) {
                ocorrenciaFiscalizacaoRBTransFacade.remover(selecionado);
                FacesUtil.addInfo("Excluído com sucesso!", "");
                redireciona();
            } else {
                FacesUtil.addError("Não foi possível excluir!", "Este registro está sendo utilizado em algum Processo de Autuação.");
            }
        } catch (Exception e) {
            FacesUtil.addFatal("Não foi possível excluir!", " Este registro está sendo utilizado em algum Processo de Autuação.");
        }
    }

    public OcorrenciaFiscalizacaoRBTransFacade getFacade() {
        return ocorrenciaFiscalizacaoRBTransFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return ocorrenciaFiscalizacaoRBTransFacade;
    }

    @URLAction(mappingId = "editarOcorrenciaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        copiarOcorrencia();
    }

    @URLAction(mappingId = "verOcorrenciaFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void copiarOcorrencia() {
        if (ocorrenciaFiscalizacaoRBTransFacade.existeOcorrenciaNoProcessoDeAutuacao(selecionado)) {
            antigaOcorrencia = selecionado;
        } else {
            selecionado = copiarOcorrenciaEAlterarVigencias();
        }
    }

    private OcorrenciaFiscalizacaoRBTrans copiarOcorrenciaEAlterarVigencias() {
        antigaOcorrencia = selecionado;
        antigaOcorrencia.setVigenciaFinal(new Date());
        OcorrenciaFiscalizacaoRBTrans novaOcorrencia = copiarOcorrenciaESuasListas();
        novaOcorrencia.setId(null);
        novaOcorrencia.setVigenciaInicial(new Date());
        novaOcorrencia.setVigenciaFinal(null);

        return novaOcorrencia;
    }

    private OcorrenciaFiscalizacaoRBTrans copiarOcorrenciaESuasListas() {
        OcorrenciaFiscalizacaoRBTrans novaOcorrencia = (OcorrenciaFiscalizacaoRBTrans) Util.clonarObjeto(antigaOcorrencia);
        novaOcorrencia.setOcorrenciaFiscalizacao(new ArrayList<MedidasOcorrencia>());
        for (MedidasOcorrencia mo : antigaOcorrencia.getOcorrenciaFiscalizacao()) {
            MedidasOcorrencia medidasOcorrencia = new MedidasOcorrencia();
            medidasOcorrencia.setMedidaAdministrativa(mo.getMedidaAdministrativa());
            medidasOcorrencia.setOcorrenciaFiscalizaRBTrans(novaOcorrencia);
            novaOcorrencia.getOcorrenciaFiscalizacao().add(medidasOcorrencia);
        }
        novaOcorrencia.setOcorrenciasAutuacaoRBTrans(new ArrayList<OcorrenciaAutuacaoRBTrans>());
        for (OcorrenciaAutuacaoRBTrans oco : antigaOcorrencia.getOcorrenciasAutuacaoRBTrans()) {
            OcorrenciaAutuacaoRBTrans ocorrencia = new OcorrenciaAutuacaoRBTrans();
            ocorrencia.setAutuacaoFiscalizacao(oco.getAutuacaoFiscalizacao());
            ocorrencia.setOcorrenciaFiscalizacao(novaOcorrencia);
            novaOcorrencia.getOcorrenciasAutuacaoRBTrans().add(ocorrencia);
        }

        return novaOcorrencia;
    }

    public String cancelarOcorrencia() {
        if (Operacoes.EDITAR.equals(operacao)) {
            return "visualizar";
        } else {
            return "lista";
        }
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (!Operacoes.NOVO.equals(operacao)) {
                if (ocorrenciaFiscalizacaoRBTransFacade.existeOcorrenciaNoProcessoDeAutuacao(antigaOcorrencia)) {
                    ocorrenciaFiscalizacaoRBTransFacade.salvar(antigaOcorrencia);
                } else {
                    ocorrenciaFiscalizacaoRBTransFacade.salvar(antigaOcorrencia);
                    ocorrenciaFiscalizacaoRBTransFacade.salvar(selecionado);
                }
            } else {
                ocorrenciaFiscalizacaoRBTransFacade.salvar(selecionado);
            }
            FacesUtil.addInfo("Salvo com sucesso!","");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getEspeciesTransporte() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EspecieTransporte object : EspecieTransporte.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getGruposOcorrencias() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (GrupoOcorrenciaFiscalizacaoRBTrans object : GrupoOcorrenciaFiscalizacaoRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<Tributo> completaTributo(String parte) {
        return tributoFacade.listaFiltrando(parte.trim().toLowerCase(), "descricao");
    }

    public Converter getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, tributoFacade);
        }
        return converterTributo;
    }

    public boolean getEhMotoTaxi() {
        return EspecieTransporte.MOTO_TAXI.equals(selecionado.getEspecieTransporte());
    }

    public List<MedidaAdministrativa> completaMedidasAdministrativa(String parte) {
        return medidaAdministrativaFacade.listaFiltrando(parte.trim().toLowerCase(), "descricao");
    }

    public Converter getConverterMedidadasAdmnistrativas() {
        if (converterMedidasAdministrativas == null) {
            converterMedidasAdministrativas = new ConverterAutoComplete(MedidaAdministrativa.class, medidaAdministrativaFacade);
        }
        return converterMedidasAdministrativas;
    }

    public void removerMedidaAdm(ActionEvent e) {
        medidaOcorrencia = (MedidasOcorrencia) e.getComponent().getAttributes().get("objeto");
        selecionado.getOcorrenciaFiscalizacao().remove(medidaOcorrencia);
        medidaOcorrencia = new MedidasOcorrencia();
    }

    public void addMedidaAdmnistrativa() {
        medidaOcorrencia = new MedidasOcorrencia();
        if (medidaAdministrativa == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe a a(s) Medida(s) Administrativa(s)!");
        } else {
            medidaOcorrencia.setMedidaAdministrativa(medidaAdministrativa);
            medidaOcorrencia.setOcorrenciaFiscalizaRBTrans(selecionado);
            for (MedidasOcorrencia medida : selecionado.getOcorrenciaFiscalizacao()) {
                if (medida.getMedidaAdministrativa().getCodigo() == medidaAdministrativa.getCodigo()) {
                    FacesUtil.addOperacaoNaoPermitida("Esta Medida Administrativa já foi adicionada!");
                    return;
                }
            }
            selecionado.getOcorrenciaFiscalizacao().add(medidaOcorrencia);
            medidaOcorrencia = new MedidasOcorrencia();
            medidaAdministrativa = new MedidaAdministrativa();
        }
    }

    public boolean getExibeTaxi() {
        return EspecieTransporte.TAXI.equals(selecionado.getEspecieTransporte());
    }

    public List<SelectItem> getGravidades() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        retorno.add(new SelectItem(null, ""));
        for (TipoGravidade tipo : TipoGravidade.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<TipoPermissaoTaxi> getTipoPermissaoTaxi() {
        return Arrays.asList(TipoPermissaoTaxi.values());
    }

    public List<TipoPermissaoMotoTaxi> getTipoPermissaoMotoTaxi() {
        return Arrays.asList(TipoPermissaoMotoTaxi.values());
    }

    public List<TipoPermissaoTaxi> getPenalidadeTaxiSelecionadas() {
        return penalidadeTaxiSelecionadas;
    }

    public void setPenalidadeTaxiSelecionadas(List<TipoPermissaoTaxi> penalidadeTaxiSelecionadas) {
        this.penalidadeTaxiSelecionadas = penalidadeTaxiSelecionadas;
    }

    public List<TipoPermissaoMotoTaxi> getPenalidadeMotoTaxiSelecionado() {
        return penalidadeMotoTaxiSelecionado;
    }

    public void setPenalidadeMotoTaxiSelecionado(List<TipoPermissaoMotoTaxi> penalidadeMotoTaxiSelecionado) {
        this.penalidadeMotoTaxiSelecionado = penalidadeMotoTaxiSelecionado;
    }

    public void limpaOpcoes() {
        penalidadeTaxiSelecionadas = new ArrayList<>();
        penalidadeMotoTaxiSelecionado = new ArrayList<>();
    }

    public boolean isInfracaoClandestina() {
        return EspecieTransporte.CLANDESTINO.equals(selecionado.getEspecieTransporte());
    }
}
