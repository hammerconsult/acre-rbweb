package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.enums.TipoDeclaracaoMensalServico;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.enums.TipoParametroNfse;
import br.com.webpublico.nfse.facades.ConfiguracaoNfseFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "configuracaoNfse", pattern = "/nfse/configuracao/",
        viewId = "/faces/tributario/nfse/configuracao/configurar.xhtml")
})
public class ConfiguracaoNfseControlador extends PrettyControlador<ConfiguracaoNfse> implements CRUD {

    @EJB
    private ConfiguracaoNfseFacade facade;
    @EJB
    private CidadeFacade cidadeFacade;
    private ConfiguracaoNfseDivida configuracaoNfseDivida;
    private PossuidorArquivo possuidorArquivo;

    public ConfiguracaoNfseControlador() {
        super(ConfiguracaoNfse.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public PossuidorArquivo getPossuidorArquivo() {
        return possuidorArquivo;
    }

    public void setPossuidorArquivo(PossuidorArquivo possuidorArquivo) {
        this.possuidorArquivo = possuidorArquivo;
    }

    @URLAction(mappingId = "configuracaoNfse", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void configurar() {
        selecionado = facade.recuperarConfiguracao();
        if (selecionado == null) {
            operacao = Operacoes.NOVO;
            selecionado = new ConfiguracaoNfse();
        } else {
            operacao = Operacoes.EDITAR;
            if (selecionado.getConfiguracaoNfseRelatorio() == null)
                selecionado.setConfiguracaoNfseRelatorio(new ConfiguracaoNfseRelatorio());

            if (selecionado.getConfiguracaoNotaPremiada() == null)
                selecionado.setConfiguracaoNotaPremiada(new ConfiguracaoNotaPremiada());

            if (selecionado.getConfiguracaoIssForaMun() == null)
                selecionado.setConfiguracaoIssForaMun(new ConfiguracaoIssForaMunicipio());

            if (selecionado.getConfiguracaoDesif() == null)
                selecionado.setConfiguracaoDesif(new ConfiguracaoDesif());

            if (selecionado.getConfiguracaoNfseNacional() == null)
                selecionado.setConfiguracaoNfseNacional(new ConfiguracaoNfseNacional());
        }
        for (TipoParametroNfse tipo : TipoParametroNfse.values()) {
            boolean adicionado = false;
            for (ConfiguracaoNfseParametros parametro : selecionado.getParametros()) {
                if (tipo.equals(parametro.getTipoParametro())) {
                    adicionado = true;
                    break;
                }
            }
            if (!adicionado) {
                ConfiguracaoNfseParametros configParam = new ConfiguracaoNfseParametros();
                configParam.setConfiguracao(selecionado);
                configParam.setTipoParametro(tipo);
                selecionado.getParametros().add(configParam);

            }
        }
        carregarDividas();
    }

    private void carregarDividas() {
        if (selecionado.getDividasNfse() == null) {
            selecionado.setDividasNfse(Lists.<ConfiguracaoNfseDivida>newArrayList());
        }
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno("/nfse/configuracao/");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/configuracao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public List<Cidade> completarCidades(String parte) {
        return cidadeFacade.listaFiltrandoCidade(parte.trim());
    }

    public void handleFileUploadGeral(FileUploadEvent event) {
        handleFileUpload(selecionado, event);
    }

    public void handleFileUploadRelatorio(FileUploadEvent event) {
        handleFileUpload(selecionado.getConfiguracaoNfseRelatorio(), event);
    }

    public void handleFileUploadNotaNacional(FileUploadEvent event) {
        handleFileUpload(selecionado.getConfiguracaoNfseNacional(), event);
    }

    public void handleFileUpload(PossuidorArquivo possuidorArquivo, FileUploadEvent event) {
        try {
            if (possuidorArquivo.getDetentorArquivoComposicao() == null) {
                possuidorArquivo.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
            }
            adicionarUnicoArquivo(possuidorArquivo.getDetentorArquivoComposicao(), event.getFile());
        } catch (IOException e) {
            logger.error("Erro ao adicionar arquivo.", e);
        }
    }


    private void adicionarUnicoArquivo(DetentorArquivoComposicao detentorArquivoComposicao, UploadedFile file) throws IOException {
        detentorArquivoComposicao.setArquivoComposicao(criarArquivoComposicao(criarArquivo(file), file));
    }

    private ArquivoComposicao criarArquivoComposicao(Arquivo arquivo, UploadedFile file) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDataUpload(new Date());
        arquivoComposicao.setDetentorArquivoComposicao(selecionado.getConfiguracaoNfseRelatorio().getDetentorArquivoComposicao());

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

    public List<SelectItem> getTiposMovimento() {
        return Util.getListSelectItem(TipoMovimentoMensal.values());
    }

    public List<SelectItem> getTiposDeclaracao() {
        return Util.getListSelectItem(TipoDeclaracaoMensalServico.values());
    }

    public void removerUnicoArquivo(PossuidorArquivo possuidorArquivo) {
        possuidorArquivo.setDetentorArquivoComposicao(null);
    }

    public ConfiguracaoNfseDivida getConfiguracaoNfseDivida() {
        return configuracaoNfseDivida;
    }

    public void setConfiguracaoNfseDivida(ConfiguracaoNfseDivida configuracaoNfseDivida) {
        this.configuracaoNfseDivida = configuracaoNfseDivida;
    }

    public void novaConfiguracaoNfseDivida() {
        configuracaoNfseDivida = new ConfiguracaoNfseDivida();
        configuracaoNfseDivida.setConfiguracaoNfse(selecionado);
    }

    public void cancelarPainelDivida() {
        this.configuracaoNfseDivida = null;
    }

    public void confirmarConfiguracaNfseDivida() {
        try {
            validarCamposDivida(this.configuracaoNfseDivida);
            adicionarDivida();
            cancelarPainelDivida();
        } catch (ValidacaoException e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void adicionarDivida() {
        Util.adicionarObjetoEmLista(selecionado.getDividasNfse(), configuracaoNfseDivida);
    }

    public void removerConfiguracaoNfseDivida(ConfiguracaoNfseDivida divida) {
        if (selecionado.getDividasNfse().contains(divida)) {
            selecionado.getDividasNfse().remove(divida);
        }
    }

    public void selecionarConfiguracaoNfseDivida(ConfiguracaoNfseDivida divida) {
        configuracaoNfseDivida = (ConfiguracaoNfseDivida) Util.clonarObjeto(divida);
        removerConfiguracaoNfseDivida(divida);
    }

    public void validarCamposDivida(ConfiguracaoNfseDivida divida) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();

        if (divida.getDividaNfse() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida deve ser informado.");
        }
        if (divida.getTipoMovimentoMensal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Movimento Mensal deve ser informado.");
        }
        if (divida.getTipoDeclaracaoMensalServico() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Declaração Mensal de Serviço deve ser informado.");
        }
        for (ConfiguracaoNfseDivida div : selecionado.getDividasNfse()) {
            if (divida.getTipoMovimentoMensal().equals(div.getTipoMovimentoMensal()) &&
                    divida.getTipoDeclaracaoMensalServico().equals(div.getTipoDeclaracaoMensalServico())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já possui Dívida cadastrada para os tipos selecionados.");
            }
        }
        ve.lancarException();
    }

    public void iniciarUploadArquivo(PossuidorArquivo possuidorArquivo) {
        this.possuidorArquivo = possuidorArquivo;
    }
}
