package br.com.webpublico.controle.rh.arquivos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.CBO;
import br.com.webpublico.entidades.rh.ImportacaoArquivoCBO;
import br.com.webpublico.entidadesauxiliares.rh.cbo.ConteudoCBO;
import br.com.webpublico.enums.rh.cbo.TipoCBO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.CBOFacade;
import br.com.webpublico.negocios.rh.cbo.ImportacaoArquivoCBOExecutor;
import br.com.webpublico.negocios.rh.cbo.ImportacaoArquivoCBOFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-importacao-cbo", pattern = "/importacao-cbo/novo/", viewId = "/faces/rh/cbo/importacaocbo/edita.xhtml"),
    @URLMapping(id = "listar-importacao-cbo", pattern = "/importacao-cbo/listar/", viewId = "/faces/rh/cbo/importacaocbo/lista.xhtml"),
    @URLMapping(id = "ver-importacao-cbo", pattern = "/importacao-cbo/ver/#{importacaoArquivoCBOControlador.id}/", viewId = "/faces/rh/cbo/importacaocbo/visualizar.xhtml"),
})
public class ImportacaoArquivoCBOControlador extends PrettyControlador<ImportacaoArquivoCBO> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoArquivoCBOControlador.class);

    @EJB
    private ImportacaoArquivoCBOFacade importacaoArquivoCBOFacade;
    @EJB
    private CBOFacade cboFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    private List<ConteudoCBO> cbosImportacao;
    private List<CBO> cbosIguais;
    private UploadedFile file;
    private Map<CBO, ConteudoCBO> mapCboDescricaoDiferente;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<AssistenteBarraProgresso> future;

    public ImportacaoArquivoCBOControlador() {
        super(ImportacaoArquivoCBO.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return importacaoArquivoCBOFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/importacao-cbo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-importacao-cbo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        cbosImportacao = Lists.newArrayList();
        cbosIguais = Lists.newArrayList();
        file = null;
    }

    @URLAction(mappingId = "ver-importacao-cbo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarCampoTipoCbo();
            Arquivo a = new Arquivo();
            a.setMimeType(file.getContentType());
            a.setNome(file.getFileName());
            a.setDescricao(file.getFileName());
            a.setTamanho(file.getSize());
            a.setInputStream(file.getInputstream());
            a = arquivoFacade.novoArquivoMemoria(a);
            selecionado.setArquivo(a);

            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Atualizando CBOS...");
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setTotal(cbosImportacao.size());

            future = new ImportacaoArquivoCBOExecutor(cboFacade).execute(selecionado, cbosImportacao, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("atualizarCbos()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    public void verificarTermino() {
        FacesUtil.executaJavaScript("aguarde.hide()");
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("termina()");
            future = null;

            importacaoArquivoCBOFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (file == null || cbosImportacao.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário importar um arquivo.");
        }
        ve.lancarException();
    }

    private void validarCampoTipoCbo() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCBO() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de CBO deve ser informado.");
        }
        ve.lancarException();
    }

    public Boolean isTipoCboDiferenteArquivo() {
        return selecionado.getTipoCBO() != null && file != null && !StringUtil.removeCaracteresEspeciais(file.getFileName().toUpperCase(Locale.ROOT)).contains(selecionado.getTipoCBO().name());
    }


    public void importarArquivo(FileUploadEvent arquivo) throws FileNotFoundException, IOException {
        try {
            validarCampoTipoCbo();
            file = arquivo.getFile();
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy(arquivo.getFile().getInputstream(), stringWriter);
            processaCsv();
            if (!cbosIguais.isEmpty()) {
                FacesUtil.executaJavaScript("visualizarCBOIguais.show()");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Erro ao processar arquivo.", "Erro: " + e);
        }
    }

    public void processaCsv() {

        cbosImportacao = Lists.newArrayList();
        cbosIguais = Lists.newArrayList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputstream(), "ISO-8859-1"))) {
            logger.info("Nome do arquivo" + file.getFileName());
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {

                if (line.isEmpty()) {
                    continue;
                }
                String[] conteudoCsv = line.split(";");
                ConteudoCBO conteudoCBO = new ConteudoCBO(Long.valueOf(conteudoCsv[0]), conteudoCsv[1], selecionado.getTipoCBO());
                cbosImportacao.add(conteudoCBO);
            }
            if (TipoCBO.OCUPACAO.equals(selecionado.getTipoCBO())) {
                cboCodigoIgualDescricaoDirefente();
            }
        } catch (Exception ex) {
            logger.error("Erro ao ler o arquivo CSV: {}", ex);
        }
    }

    public void cboCodigoIgualDescricaoDirefente() {
        mapCboDescricaoDiferente = new HashMap<>();
        List<CBO> cbosExistentes = cboFacade.buscarCbosPorTipo(selecionado.getTipoCBO());
        Map<Long, CBO> mapCbo = Maps.newHashMap();
        for (CBO cboBase : cbosExistentes) {
            mapCbo.put(cboBase.getCodigo(), cboBase);
        }
        for (ConteudoCBO cboImportacao : cbosImportacao) {
            CBO cbo = mapCbo.get(cboImportacao.getCodigo());
            if (cbo != null && !StringUtil.removeCaracteresEspeciais(cboImportacao.getDescricao().trim()).equalsIgnoreCase(StringUtil.removeCaracteresEspeciais(cbo.getDescricao().trim()))) {
                cbosIguais.add(cbo);
                mapCboDescricaoDiferente.put(cbo, cboImportacao);
            }
        }
    }

    public List<SelectItem> getTiposCbo() {
        return Util.getListSelectItem(TipoCBO.values(), false);
    }

    public List<ConteudoCBO> getCbosImportacao() {
        return cbosImportacao;
    }

    public void setCbosImportacao(List<ConteudoCBO> cbosImportacao) {
        this.cbosImportacao = cbosImportacao;
    }

    public List<CBO> getCbosIguais() {
        return cbosIguais;
    }

    public void setCbosIguais(List<CBO> cbosIguais) {
        this.cbosIguais = cbosIguais;
    }

    public Map<CBO, ConteudoCBO> getMapCboDescricaoDiferente() {
        return mapCboDescricaoDiferente;
    }

    public void setMapCboDescricaoDiferente(Map<CBO, ConteudoCBO> mapCboDescricaoDiferente) {
        this.mapCboDescricaoDiferente = mapCboDescricaoDiferente;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
