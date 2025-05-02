package br.com.webpublico.dte.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.dte.entidades.ConfiguracaoDte;
import br.com.webpublico.dte.entidades.ConfiguracaoDteParametros;
import br.com.webpublico.dte.entidades.ConfiguracaoDteRelatorio;
import br.com.webpublico.dte.enums.TipoParametroDte;
import br.com.webpublico.dte.facades.ConfiguracaoDteFacade;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoComposicao;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "configuracaoDte", pattern = "/dte/configuracao/",
        viewId = "/faces/tributario/dte/configuracao/configurar.xhtml")
})
public class ConfiguracaoDteControlador extends PrettyControlador<ConfiguracaoDte> implements CRUD {

    @EJB
    private ConfiguracaoDteFacade facade;

    public ConfiguracaoDteControlador() {
        super(ConfiguracaoDte.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }


    @URLAction(mappingId = "configuracaoDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void configurar() {
        try {
            selecionado = facade.recuperarConfiguracao();
        } catch (ExcecaoNegocioGenerica e) {
        }
        if (selecionado == null) {
            operacao = Operacoes.NOVO;
            selecionado = new ConfiguracaoDte();
        } else {
            operacao = Operacoes.EDITAR;
            if (selecionado.getConfiguracaoDteRelatorio() == null)
                selecionado.setConfiguracaoDteRelatorio(new ConfiguracaoDteRelatorio());
        }
        for (TipoParametroDte tipo : TipoParametroDte.values()) {
            boolean adicionado = false;
            for (ConfiguracaoDteParametros parametro : selecionado.getParametros()) {
                if (tipo.equals(parametro.getTipoParametro())) {
                    adicionado = true;
                    break;
                }
            }
            if (!adicionado) {
                ConfiguracaoDteParametros configParam = new ConfiguracaoDteParametros();
                configParam.setConfiguracao(selecionado);
                configParam.setTipoParametro(tipo);
                selecionado.getParametros().add(configParam);

            }
        }
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno("/dte/configuracao/");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/dte/configuracao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public void handleFileUploadUnicoArquivo(FileUploadEvent event) {
        if (selecionado.getConfiguracaoDteRelatorio().getDetentorArquivoComposicao() == null) {
            selecionado.getConfiguracaoDteRelatorio().setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }
        try {
            adicionarUnicoArquivo(event.getFile());
        } catch (IOException e) {
            logger.debug("Assistente Arquivo Controlador: " + e.getMessage());
        }
    }

    private void adicionarUnicoArquivo(UploadedFile file) throws IOException {
        selecionado.getConfiguracaoDteRelatorio().getDetentorArquivoComposicao().setArquivoComposicao(new ArquivoComposicao());
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(criarArquivo(file), file);
        selecionado.getConfiguracaoDteRelatorio().getDetentorArquivoComposicao().setArquivoComposicao(arquivoComposicao);
    }

    private ArquivoComposicao criarArquivoComposicao(Arquivo arquivo, UploadedFile file) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setDetentorArquivoComposicao(selecionado.getConfiguracaoDteRelatorio().getDetentorArquivoComposicao());

        return arquivoComposicao;
    }

    private Arquivo criarArquivo(UploadedFile file) throws IOException {
        Arquivo arquivo = new Arquivo();

        arquivo.setDescricao(file.getFileName());
        arquivo.setNome(file.getFileName());
        arquivo.setTamanho(file.getSize());
        arquivo.setInputStream(file.getInputstream());
        arquivo.setMimeType(getMimeType(file.getInputstream()));
        arquivo = criarPartesDoArquivo(arquivo);

        return arquivo;
    }

    public String getMimeType(InputStream is) {
        TikaConfig tika = null;
        try {
            tika = new TikaConfig();
            MediaType mimetype = tika.getDetector().detect(
                TikaInputStream.get(is), new Metadata());
            return mimetype.getType() + "/" + mimetype.getSubtype();
        } catch (TikaException e) {
            logger.error("Não foi possível gerar o mimeType: " + e);
        } catch (IOException e) {
            logger.error("Não foi possível gerar o mimeType: " + e);
        }
        return "application/octet-stream";
    }

    private Arquivo criarPartesDoArquivo(Arquivo arquivo) throws IOException {
        int bytesLidos = 0;

        while (true) {
            int restante = arquivo.getInputStream().available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = arquivo.getInputStream().read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
        }

        return arquivo;
    }

    public void removerUnicoArquivo() {
        selecionado.getConfiguracaoDteRelatorio().getDetentorArquivoComposicao().setArquivoComposicao(null);
        selecionado.getConfiguracaoDteRelatorio().setDetentorArquivoComposicao(null);
    }
}
