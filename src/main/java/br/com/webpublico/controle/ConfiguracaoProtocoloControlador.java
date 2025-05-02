/*
 * Codigo gerado automaticamente em Wed Aug 24 10:34:19 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.ConfiguracaoProtocoloFacade;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ConfiguracaoProtocolo;
import br.com.webpublico.entidades.Texto;
import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.TipoDoctoOficialFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@SessionScoped
public class ConfiguracaoProtocoloControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConfiguracaoProtocoloControlador.class);

    @EJB
    private ConfiguracaoProtocoloFacade configuracaoProtocoloFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private ConfiguracaoProtocolo selecionado;
    private Texto texto;
    private Date desde;
    private ConverterAutoComplete converterTipoDoctoOficial;
    private String caminho;

    public ConfiguracaoProtocoloControlador() {
        desde = new Date();
    }

    public ConfiguracaoProtocolo getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConfiguracaoProtocolo selecionado) {
        this.selecionado = selecionado;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String caminho() {
        return this.caminho;
    }

    public List<ConfiguracaoProtocolo> getLista() {
        if (desde == null) {
            desde = new Date();
        }
        List<ConfiguracaoProtocolo> l = new ArrayList<ConfiguracaoProtocolo>();
        l.add(configuracaoProtocoloFacade.listaConfiguracaoProtocoloFiltrandoVigencia(desde));
        return l;
    }

    public Date getDesde() {
        return desde;
    }

    public void setDesde(Date desde) {
        this.desde = desde;
    }

    public ConfiguracaoProtocoloFacade getFacade() {
        return configuracaoProtocoloFacade;
    }

    public Texto getTexto() {
        return texto;
    }

    public void setTexto(Texto texto) {
        this.texto = texto;
    }

    public void novo() {
        this.selecionado = new ConfiguracaoProtocolo();
        texto = new Texto();
    }

    public String salvar() {
        if (validaCampos()) {
            try {
                selecionado.setInformacoes(texto);
                if (this.selecionado.getId() == null) {
                    selecionado.setDesde(SistemaFacade.getDataCorrente());
                    configuracaoProtocoloFacade.salvarNovo(selecionado);
                } else {
                    configuracaoProtocoloFacade.salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Salvo com sucesso!"));
                return "visualizar";
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Não foi possível continuar!", "Erro ao salvar " + e.getMessage()));
            }
        }
        return "edita";
    }

    public void selecionar(ActionEvent evento) {
        this.selecionado = (ConfiguracaoProtocolo) evento.getComponent().getAttributes().get("objeto");
        texto = selecionado.getInformacoes();
    }

    public void setVigencia(DateSelectEvent event) {
        desde = event.getDate();
    }

    public Converter getConverterTipoDoctoOficial() {
        if (converterTipoDoctoOficial == null) {
            converterTipoDoctoOficial = new ConverterAutoComplete(TipoDoctoOficial.class, tipoDoctoOficialFacade);
        }
        return converterTipoDoctoOficial;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        return tipoDoctoOficialFacade.tipoDoctoPorModulo(parte.trim(), ModuloTipoDoctoOficial.PROTOCOLO);
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getTipoDoctoCapaProcesso() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:tipoDoctoCapa", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "Favor informar o tipo de documento para a Capa do Processo."));
            retorno = false;
        }
        if (selecionado.getTipoDoctoTramiteProcesso() == null) {
            FacesContext.getCurrentInstance().addMessage("Formulario:tipoDoctoTramite", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção!", "Favor informar o tipo de documento para o Trâmite do Processo."));
            retorno = false;
        }
        return retorno;
    }

    public void excluirSelecionado() {
        try {
            configuracaoProtocoloFacade.remover(selecionado);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Este registro não pode ser excluído porque possui dependências."));
        }
    }

    public void uploadArquivo(FileUploadEvent file) {
        try {
            Arquivo arq = new Arquivo();
            arq.setNome(file.getFile().getFileName());
            arq.setMimeType(arquivoFacade.getMimeType(file.getFile().getFileName()));
            arq.setDescricao(new Date().toString());
            arq.setTamanho(file.getFile().getSize());

            selecionado.setArquivoManual(arquivoFacade.novoArquivoMemoria(arq, file.getFile().getInputstream()));
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public String getUrlDownloadManualProtocolo() {
        ConfiguracaoProtocolo config = configuracaoProtocoloFacade.retornaUltima();
        String url = "#";
        if (config != null && config.getArquivoManual() != null) {
            url = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/arquivos/" + config.getArquivoManual().getNome() + "?id=" + config.getArquivoManual().getId();
        }
        return url;
    }

    public void downloadManualProtocolo() {
//        try {
//            FacesContext.getCurrentInstance().getExternalContext().redirect("window.open('"+urlDownloadManualProtocolo()+"', '_blank');");
//        } catch (IOException ex) {
//            //System.out.println("Erro ou fazer o download do manual do Protocolo. " + ex.getMessage());
//        }
    }
}
