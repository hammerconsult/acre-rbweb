package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContaBancariaEntidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.HoleriteBB;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HoleriteBBFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.transaction.TransactionTimedOutException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ManagedBean(name = "holeriteBBControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoHoleriteBB", pattern = "/holeriteBB/novo/", viewId = "/faces/rh/administracaodepagamento/holeritebb/edita.xhtml"),
    @URLMapping(id = "editarHoleriteBB", pattern = "/holeriteBB/editar/#{holeriteBBControlador.id}/", viewId = "/faces/rh/administracaodepagamento/holeritebb/edita.xhtml"),
    @URLMapping(id = "listarHoleriteBB", pattern = "/holeriteBB/listar/", viewId = "/faces/rh/administracaodepagamento/holeritebb/lista.xhtml"),
    @URLMapping(id = "verHoleriteBB", pattern = "/holeriteBB/ver/#{holeriteBBControlador.id}/", viewId = "/faces/rh/administracaodepagamento/holeritebb/visualizar.xhtml")
})
public class HoleriteBBControlador extends PrettyControlador<HoleriteBB> implements Serializable, CRUD {


    @EJB
    private HoleriteBBFacade holeriteBBFacade;
    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    private String conteudo;
    private ContaBancariaEntidade contaBancariaEntidade;
    private Date dataReferencia;
    private ConverterGenerico converterContaBancariaEntidade;
    private File arquivo;
    private DefaultStreamedContent fileDownload;
    private String mensagem;
    private HierarquiaOrganizacional hierarquiaOrganizacional;


    public HoleriteBBControlador() {
        super(HoleriteBB.class);
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return holeriteBBFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public StreamedContent getFileDownload() throws FileNotFoundException, IOException {

        if (selecionado.getConteudoArquivo() != null) {
            FileOutputStream fos = null;
            arquivo = File.createTempFile("Holerite", "txt");
            fos = new FileOutputStream(arquivo);
            fos.write(selecionado.getConteudoArquivo().toString().getBytes());
            fos.close();
            InputStream stream = new FileInputStream(arquivo);
            fileDownload = new DefaultStreamedContent(stream, "text/plain", "HOLERITEBB " + Mes.getMesToInt(Integer.parseInt(selecionado.getMes())).name() + selecionado.getAno() + ".TXT");
        }
        return fileDownload;
    }

    public List<SelectItem> getContasBancariasDaEntidade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        HierarquiaOrganizacional hierarquiaRoot = holeriteBBFacade.getHierarquiaOrganizacionalFacade().getRaizHierarquia(UtilRH.getDataOperacao());
        if (hierarquiaRoot != null && hierarquiaRoot.getId() != null) {
            List<ContaBancariaEntidade> listaContas = holeriteBBFacade.getContaBancariaEntidadeFacade().listaContasPorEntidade(hierarquiaRoot.getSubordinada().getEntidade());
            if (listaContas != null && !listaContas.isEmpty()) {
                contaBancariaEntidade = listaContas.get(0);
                for (ContaBancariaEntidade conta : listaContas) {
                    toReturn.add(new SelectItem(conta, conta.toString()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione o Tipo de Folha..."));
        for (TipoFolhaDePagamento tipoFolha : TipoFolhaDePagamento.values()) {
            if (isTipoNormalOrComplementarOrRescisao(tipoFolha)) {
                toReturn.add(new SelectItem(tipoFolha, tipoFolha.getDescricao()));
            }
        }
        return toReturn;
    }

    public boolean isTipoNormalOrComplementarOrRescisao(TipoFolhaDePagamento tipoFolha) {
        return isTipoNormal(tipoFolha) || isTipoComplementar(tipoFolha) || isTipoRescisao(tipoFolha);
    }

    public boolean isTipoRescisao(TipoFolhaDePagamento tipoFolha) {
        return TipoFolhaDePagamento.RESCISAO.equals(tipoFolha);
    }

    public boolean isTipoComplementar(TipoFolhaDePagamento tipoFolha) {
        return TipoFolhaDePagamento.COMPLEMENTAR.equals(tipoFolha);
    }

    public boolean isTipoNormal(TipoFolhaDePagamento tipoFolha) {
        return TipoFolhaDePagamento.NORMAL.equals(tipoFolha);
    }

    public Converter getConverterContaBancariaEntidade() {
        if (converterContaBancariaEntidade == null) {
            converterContaBancariaEntidade = new ConverterGenerico(ContaBancariaEntidade.class, holeriteBBFacade.getContaBancariaEntidadeFacade());
        }
        return converterContaBancariaEntidade;
    }

    @Override
    public AbstractFacade getFacede() {
        return holeriteBBFacade;
    }

    @URLAction(mappingId = "novoHoleriteBB", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        arquivo = null;
        dataReferencia = UtilRH.getDataOperacao();
        selecionado.setDataGeracao(UtilRH.getDataOperacao());
        selecionado.setNumeroRemessa(holeriteBBFacade.recuperarNumeroRemessa());
        selecionado.setVersao(holeriteBBFacade.recuperarVersao(selecionado.getAno(), selecionado.getMes()));
        selecionado.setTodosOrgaos(true);
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        selecionado.setQuantidadeContratos(0);
        selecionado.setDecorrido(0l);
        selecionado.setTempo(0l);
        selecionado.setProcessados(0);
        selecionado.setTotal(0);
        selecionado.setCalculando(false);
    }

    @URLAction(mappingId = "verHoleriteBB", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarHoleriteBB", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        this.selecionado.setConteudoArquivo(conteudo);
        this.selecionado.setDataGeracao(holeriteBBFacade.getSistemaFacade().getDataOperacao());
        holeriteBBFacade.salvarNovo(selecionado);
    }

    public void gerarArquivo() {
        try {
            if (validarCampos()) {
                holeriteBBFacade.gerarArquivo(selecionado, dataReferencia, contaBancariaEntidade, hierarquiaOrganizacional);
                FacesUtil.executaJavaScript("poll.start()");
            }
        } catch (TransactionTimedOutException tte) {
            FacesUtil.addOperacaoNaoPermitida("Tempo limite expirado!");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public Double getPorcentagemDoCalculo() {
        if (selecionado.getProcessados() == null || selecionado.getTotal() == null) {
            return 0d;
        }
        return (selecionado.getProcessados().doubleValue() / selecionado.getTotal().doubleValue()) * 100;
    }

    public String getTempoDecorrido() {
        long HOUR = TimeUnit.HOURS.toMillis(1);

        selecionado.setDecorrido(System.currentTimeMillis() - selecionado.getTempo());

        return String.format(formatoDataHora, selecionado.getDecorrido() / HOUR, selecionado.getDecorrido() % HOUR);
    }

    public String getTempoEstimado() {
        try {
            long HOUR = TimeUnit.HOURS.toMillis(1);
            long unitario = (System.currentTimeMillis() - selecionado.getTempo()) / (selecionado.getProcessados() + 1);
            Double qntoFalta = (unitario * (selecionado.getTotal() - selecionado.getProcessados().doubleValue()));

            return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        } catch (Exception e) {
            return "0";
        }
    }

    public void removerBarraStatus() {
        if (!selecionado.getCalculando()) {
            FacesUtil.atualizarComponente("Formulario");
            try {
                Thread.sleep(3000);
            } catch (Exception e) {

            }
            if (selecionado.getMensagemExcecao() != null && !selecionado.getMensagemExcecao().isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada(selecionado.getMensagemExcecao());
            }
        }
    }

    public Boolean validarCampos() {
        boolean retorno = Boolean.TRUE;
        if (!Util.validaCampos(selecionado)) {
            retorno = Boolean.FALSE;
        }
        if (dataReferencia == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addCampoObrigatorio("O campo Data de Referência é obrigatório.");
        }
        if (!selecionado.getTodosOrgaos() && hierarquiaOrganizacional == null) {
            retorno = Boolean.FALSE;
            FacesUtil.addCampoObrigatorio("O campo Órgão é obrigatório.");
        }
        return retorno;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/holeriteBB/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }


    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public void setConverterContaBancariaEntidade(ConverterGenerico converterContaBancariaEntidade) {
        this.converterContaBancariaEntidade = converterContaBancariaEntidade;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public File getArquivo() {
        return arquivo;
    }

    public void setArquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Converter getConverterHierarquiaOrganizacional() {
        return new ConverterAutoComplete(HierarquiaOrganizacional.class, holeriteBBFacade.getHierarquiaOrganizacionalFacade());
    }
}
