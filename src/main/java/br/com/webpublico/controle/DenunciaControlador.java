package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteDetentorArquivoComposicao;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DenunciaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonFiscalizacaoSecretaria;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "denunciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaDenuncia", pattern = "/denuncia/novo/", viewId = "/faces/tributario/denuncia/denuncia/edita.xhtml"),
    @URLMapping(id = "editarDenuncia", pattern = "/denuncia/editar/#{denunciaControlador.id}/", viewId = "/faces/tributario/denuncia/denuncia/edita.xhtml"),
    @URLMapping(id = "listarDenuncia", pattern = "/denuncia/listar/", viewId = "/faces/tributario/denuncia/denuncia/lista.xhtml"),
    @URLMapping(id = "verDenuncia", pattern = "/denuncia/ver/#{denunciaControlador.id}/", viewId = "/faces/tributario/denuncia/denuncia/visualizar.xhtml")
})
public class DenunciaControlador extends PrettyControlador<Denuncia> implements Serializable, CRUD {

    @EJB
    private DenunciaFacade denunciaFacade;
    @EJB
    private SingletonFiscalizacaoSecretaria singletonFiscalizacaoSecretaria;
    private ConverterAutoComplete converterTipoDenuncia;
    private ConverterAutoComplete converterFiscal;
    private ConverterAutoComplete converterBairro;
    private ConverterAutoComplete converterSecretaria;
    private UsuarioSistema fiscal;
    private DenunciaOcorrencias denunciaOcorrencias;
    private AssistenteDetentorArquivoComposicao assistenteDetentorArquivoComposicao;


    public DenunciaControlador() {
        super(Denuncia.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return denunciaFacade;
    }

    public Denuncia getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(Denuncia selecionado) {
        this.selecionado = selecionado;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/denuncia/";
    }

    public AssistenteDetentorArquivoComposicao getAssistenteDetentorArquivoComposicao() {
        return assistenteDetentorArquivoComposicao;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void inicializarAtributos() {
        fiscal = new UsuarioSistema();
        denunciaOcorrencias = new DenunciaOcorrencias();
        assistenteDetentorArquivoComposicao = new AssistenteDetentorArquivoComposicao(selecionado, denunciaFacade.getUsuarioSistemaFacade().getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "novaDenuncia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        this.selecionado = new Denuncia();
        this.selecionado.setDataDenuncia(new Date());
        this.selecionado.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
        this.selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        this.selecionado.setSituacao(SituacaoDenuncia.ABERTO);
        this.selecionado.setSituacaoFinalDenuncia(SituacaoFinalDenuncia.NAO_FINALIZADO);
        this.selecionado.setDenunciado(new PessoaDenuncia());
        this.selecionado.setDenunciante(new PessoaDenuncia());
        inicializarAtributos();


    }

    @URLAction(mappingId = "editarDenuncia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        recuperar();
        inicializarAtributos();
        if (!selecionado.getSituacaoFinalDenuncia().equals(SituacaoFinalDenuncia.NAO_FINALIZADO)) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + getId() + "/");
        }
    }

    @URLAction(mappingId = "verDenuncia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        recuperar();
        inicializarAtributos();
    }

    public void recuperar() {
        setSessao(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sessao"));
        operacao = Operacoes.EDITAR;
        selecionado = (Denuncia) Web.pegaDaSessao(metadata.getEntidade());
        if (selecionado == null) {
            selecionado = (Denuncia) getFacede().recuperar(getId());
        }
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            if (selecionado.getId() == null || selecionado.getNumero() == null) {
                this.selecionado.setNumero(singletonFiscalizacaoSecretaria.getProximoCodigoDenuncia().longValue());
            }
            super.salvar();
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getSecretariaFiscalizacao() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Secretaria");
            retorno = false;
        }
        if (selecionado.getTipoDenuncia() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Tipo de Denuncia");
            retorno = false;
        }
        if (selecionado.getTipoOrigemDenuncia() == null) {
            FacesUtil.addCampoObrigatorio("Informe a Origem");
            retorno = false;
        }
        if (selecionado.getDenunciado() != null && (selecionado.getDenunciado().getEndereco() == null
            || selecionado.getDenunciado().getEndereco().isEmpty())) {
            FacesUtil.addCampoObrigatorio("Informe o Endereço do Denunciado");
            retorno = false;
        }
        if (selecionado.getDenunciado() != null && selecionado.getDenunciado().getBairro() == null) {
            FacesUtil.addCampoObrigatorio("Informe o Bairro do Denunciado");
            retorno = false;
        }
        if (!selecionado.getSituacaoFinalDenuncia().equals(SituacaoFinalDenuncia.NAO_FINALIZADO)) {
            if ("".equals(selecionado.getParecerFinal())) {
                FacesUtil.addCampoObrigatorio("Para a Situação informada, o Parecer Final é obrigatório");
                retorno = false;
            } else if (selecionado.getDataFinal() == null) {
                FacesUtil.addCampoObrigatorio("Para a Situação informada, a Data do Parecer Final é obrigatório");
                retorno = false;
            }
        }
        return retorno;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = new ArrayList<>();
        for (SituacaoDenuncia sit : SituacaoDenuncia.values()) {
            retorno.add(new SelectItem(sit, sit.getDescricao()));
        }
        return retorno;
    }

    public List<SecretariaFiscalizacao> completaSecretaria(String parte) {
        return denunciaFacade.getSecretariaFiscalizacaoFacade().completarSecretariaFiscalizacao(parte.trim());
    }

    public List<TipoDenuncia> completaTipoDenuncia(String parte) {
        return denunciaFacade.getTipoDenunciaFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<UsuarioSistema> completaFiscal(String parte) {
        return denunciaFacade.getUsuarioSistemaFacade().listaFiltrandoUsuarioSistemaPorTipo(parte.trim(), TipoUsuarioTributario.FISCAL_OBRAS, TipoUsuarioTributario.FISCAL_MEIO_AMBIENTE, TipoUsuarioTributario.FISCAL_SANITARIO);
    }

    public List<Bairro> completaBairro(String parte) {
        return denunciaFacade.getBairroFacade().completaBairro(parte.trim());
    }

    public ConverterAutoComplete getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, denunciaFacade.getBairroFacade());
        }
        return converterBairro;
    }

    public ConverterAutoComplete getConverterTipoDenuncia() {
        if (converterTipoDenuncia == null) {
            converterTipoDenuncia = new ConverterAutoComplete(TipoDenuncia.class, denunciaFacade.getTipoDenunciaFacade());
        }
        return converterTipoDenuncia;
    }

    public ConverterAutoComplete getConverterFiscal() {
        if (converterFiscal == null) {
            converterFiscal = new ConverterAutoComplete(UsuarioSistema.class, denunciaFacade.getUsuarioSistemaFacade());
        }
        return converterFiscal;
    }

    public ConverterAutoComplete getConverterSecretaria() {
        if (converterSecretaria == null) {
            converterSecretaria = new ConverterAutoComplete(SecretariaFiscalizacao.class, denunciaFacade.getSecretariaFiscalizacaoFacade());
        }
        return converterSecretaria;
    }

    public void removeFiscal(DenunciaFiscaisDesignados fiscal) {
        selecionado.getDenunciaFisciasDesignados().remove(fiscal);
        if (selecionado.getDenunciaFisciasDesignados().isEmpty()) {
            if (selecionado.getSituacao().equals(SituacaoDenuncia.DESIGNADO)) {
                selecionado.setSituacao(SituacaoDenuncia.ABERTO);
            }
        }
    }

    public void removeOcorrencia(DenunciaOcorrencias ocorrencia) {
        selecionado.getDenunciaOcorrencias().remove(ocorrencia);
    }

    public void addFiscal() {
        if (fiscal != null) {
            DenunciaFiscaisDesignados denunciaFiscaisDesignados = new DenunciaFiscaisDesignados();
            denunciaFiscaisDesignados.setDenuncia(selecionado);
            denunciaFiscaisDesignados.setFiscalUsuarioSistema(fiscal);
            selecionado.getDenunciaFisciasDesignados().add(denunciaFiscaisDesignados);
            if (selecionado.getSituacao().equals(SituacaoDenuncia.ABERTO)) {
                selecionado.setSituacao(SituacaoDenuncia.DESIGNADO);
            }

            fiscal = new UsuarioSistema();
        }
    }

    public void addOcorrencia() {
        if (denunciaOcorrencias != null) {
            if (denunciaOcorrencias.getDataOcorrencia() == null) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar!", "Informe a Data da Ocorrência."));
            } else if ("".equals(denunciaOcorrencias.getOcorrencia())) {
                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar!", "Informe a Ocorrência."));
            } else {
                denunciaOcorrencias.setSequencia(selecionado.getDenunciaOcorrencias().size() + 1l);
                denunciaOcorrencias.setDenuncia(selecionado);
                selecionado.getDenunciaOcorrencias().add(denunciaOcorrencias);
                denunciaOcorrencias = new DenunciaOcorrencias();
            }
        }
    }

    public UsuarioSistema getFiscal() {
        return fiscal;
    }

    public void setFiscal(UsuarioSistema fiscal) {
        this.fiscal = fiscal;
    }

    public DenunciaOcorrencias getDenunciaOcorrencias() {
        return denunciaOcorrencias;
    }

    public void setDenunciaOcorrencias(DenunciaOcorrencias denunciaOcorrencias) {
        this.denunciaOcorrencias = denunciaOcorrencias;
    }

    public List<SituacaoFinalDenuncia> getSituacoesFinaisDenuncia() {
        List<SituacaoFinalDenuncia> lista = new ArrayList<SituacaoFinalDenuncia>();
        for (SituacaoFinalDenuncia sit : SituacaoFinalDenuncia.values()) {
            lista.add(sit);
        }
        return lista;
    }

    public void alteraSituacao() {
        if (selecionado.getSituacaoFinalDenuncia().equals(SituacaoFinalDenuncia.CONCLUIDO)) {
            selecionado.setSituacao(SituacaoDenuncia.CONCLUIDO);
        } else if (selecionado.getSituacaoFinalDenuncia().equals(SituacaoFinalDenuncia.FISCALIZACAO)) {
            selecionado.setSituacao(SituacaoDenuncia.FISCALIZACAO);
        } else if (selecionado.getSituacaoFinalDenuncia().equals(SituacaoFinalDenuncia.CANCELADO)) {
            selecionado.setSituacao(SituacaoDenuncia.CANCELADO);
        } else if (selecionado.getSituacaoFinalDenuncia().equals(SituacaoFinalDenuncia.NAO_FINALIZADO)) {
            if (selecionado.getDenunciaFisciasDesignados().isEmpty()) {
                selecionado.setSituacao(SituacaoDenuncia.ABERTO);
            } else {
                selecionado.setSituacao(SituacaoDenuncia.DESIGNADO);
            }
        }
    }

    public boolean denunciaAberta() {
        return selecionado.getSituacao().equals(SituacaoDenuncia.ABERTO) || selecionado.getSituacao().equals(SituacaoDenuncia.DESIGNADO);
    }

    public String caminhoAtual() {
        if (selecionado.getId() != null) {
            return getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/";
        }
        return getCaminhoPadrao() + "novo/";
    }

    public List<SelectItem> getTipoOrigemDenuncia() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoOrigemDenuncia tipo : TipoOrigemDenuncia.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public void imprimeDenuncia() {
        new ImprimeDenuncia().ImprimeDenuncia(selecionado);
    }

    public void selecionaOcorrencia(DenunciaOcorrencias oco) {
        denunciaOcorrencias = oco;
    }

    public class ImprimeDenuncia extends AbstractReport implements Serializable {

        public ImprimeDenuncia() {
            geraNoDialog = true;
        }

        public void ImprimeDenuncia(Denuncia denuncia) {
            HashMap<String, Object> parametros = Maps.newHashMap();
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("SUBREPORT_DIR", getCaminho());
            parametros.put("DENUNCIA_ID", denuncia.getId());
            try {
                gerarRelatorio("Denuncia.jasper", parametros);
            } catch (JRException e) {
                logger.error(e.getMessage());
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
