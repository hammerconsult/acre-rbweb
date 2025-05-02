package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroGeracaoCredencialRBTrans;
import br.com.webpublico.entidadesauxiliares.VoCredencialTrafegoRBTrans;
import br.com.webpublico.entidadesauxiliares.VoCredencialTransporteRBTrans;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CredencialRBTransFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: user
 * Date: 11/11/13
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "emissaoCredencialRBTransControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaEmissaoCredencial",
        pattern = "/emissao-credencial/novo/",
        viewId = "/faces/tributario/rbtrans/emissaodecredenciais/emissao.xhtml")
}
)
public class EmissaoCredencialRBTransControlador extends PrettyControlador<CredencialRBTrans> {

    private static final Logger logger = LoggerFactory.getLogger(EmissaoCredencialRBTransControlador.class);
    @EJB
    private CredencialRBTransFacade credencialRBTransFacade;
    private ParametrosTransitoTransporte parametro;
    private BigDecimal valorSegundaVia;
    private CredencialRBTrans selecionado;

    private FiltroGeracaoCredencialRBTrans filtro;
    private List<CredencialRBTrans> listaCredenciais;
    private CredencialRBTrans[] credenciaisSelecionadas;
    private List<VoCredencialTransporteRBTrans> listaCredenciaisTransporte;
    private VoCredencialTransporteRBTrans voCredencialTransporteRBTrans;
    private List<VoCredencialTrafegoRBTrans> listaCredenciaisTrafego;
    private VoCredencialTrafegoRBTrans voCredencialTrafegoRBTrans;


    public EmissaoCredencialRBTransControlador() {
        super(CredencialRBTrans.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return credencialRBTransFacade;
    }

    @URLAction(mappingId = "novaEmissaoCredencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaEmissao() {
        filtro = new FiltroGeracaoCredencialRBTrans();
        listaCredenciais = new ArrayList<>();
        listaCredenciaisTransporte = new ArrayList<>();
        listaCredenciaisTrafego = new ArrayList<>();
    }

    public String getValorSegundaVia() {
        DecimalFormat df = new DecimalFormat("R$ #,##0.00");
        return df.format(valorSegundaVia);
    }

    public void recuperaValorSegundaViaDoParametro(CredencialRBTrans credencial) {
        selecionado = credencial;
        if (credencial.getPermissaoTransporte() != null) {
            parametro = credencialRBTransFacade.getPermissaoTransporteFacade().getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(credencial.getPermissaoTransporte().getTipoPermissaoRBTrans());
            List<TaxaTransito> taxa = credencialRBTransFacade.getPermissaoTransporteFacade().getParametrosTransitoTransporteFacade().recuperarTaxaTransitoPeloTipo(parametro, TipoCalculoRBTRans.SEGUNDA_VIA_CREDENCIAL_TRANSPORTE);
            valorSegundaVia = taxa.get(0).getValor().multiply(credencialRBTransFacade.getPermissaoTransporteFacade().getMoedaFacade().recuperaValorUFMParaData(new Date()));
            FacesUtil.executaJavaScript("segundaVia.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Essa credencial não possui Permissão de Transporte.");
        }

    }

    public void efetivarEmissaoCredencialSegundaVia() throws Exception {
        CalculoCredencialRBTrans calculoCredencialRBTrans = new CalculoCredencialRBTrans();

        CalculoRBTrans calculo = getCalculoRBTrans();
        calculoCredencialRBTrans.setCredencialRBTrans(selecionado);
        calculoCredencialRBTrans.setCalculoRBTrans(calculo);
        selecionado = credencialRBTransFacade.recuperar(selecionado.getId());
        selecionado.getCalculosCredencial().add(calculoCredencialRBTrans);
//        selecionado.setDataEmissao(credencialRBTransFacade.getSistemaFacade().getDataOperacao());
        credencialRBTransFacade.getPermissaoTransporteFacade().getCalculoRBTransFacade().gerarDebito(calculo);
        emitir2ViaCredencial(selecionado);
    }

    private CalculoRBTrans getCalculoRBTrans() {
        try {
            CalculoRBTrans calculo;
            if (getFiltro().getTipoCredencialRBTrans() == TipoCredencialRBTrans.TRANSPORTE) {
                calculo = credencialRBTransFacade.getPermissaoTransporteFacade().getCalculoRBTransFacade().calculaSegundaViaCredencialTransporte(selecionado.getCadastroEconomico(), selecionado.getPermissaoTransporte().getTipoPermissaoRBTrans());
            } else {
                calculo = credencialRBTransFacade.getPermissaoTransporteFacade().getCalculoRBTransFacade().calculaSegundaViaCredencialTrafego(selecionado.getCadastroEconomico(), selecionado.getPermissaoTransporte().getTipoPermissaoRBTrans());
            }
            return calculo;
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
            return null;
        }
    }

    public List<SelectItem> getTiposTransporte() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPermissaoRBTrans obj : TipoPermissaoRBTrans.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposRequerente() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Ambos"));
        for (TipoRequerenteCredencialRBTrans obj : TipoRequerenteCredencialRBTrans.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposCredencial() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Ambos"));
        for (TipoCredencialRBTrans obj : TipoCredencialRBTrans.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposVigencia() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Ambos"));
        for (TipoVigencia obj : TipoVigencia.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public boolean validaPesquisa() {
        boolean valida = true;
        if (getFiltro().getNumeroPermissaoInicial() != null) {
            if (getFiltro().getNumeroPermissaoFinal() != null) {
                if (getFiltro().getNumeroPermissaoInicial() > getFiltro().getNumeroPermissaoFinal()) {
                    FacesUtil.addWarn("Atenção!", "O número inicial do intervalo de permissões deve ser menor que o número final do intervalo.");
                    valida = false;
                }
            } else {
                FacesUtil.addWarn("Atenção!", "É necessário informar o final do intervalo do número da permissão");
                valida = false;
            }
        }
        if (getFiltro().getFinalPermissaoInicial() != null) {
            if (getFiltro().getFinalPermissaoFinal() != null) {
                if (getFiltro().getFinalPermissaoInicial() > getFiltro().getFinalPermissaoFinal()) {
                    FacesUtil.addWarn("Atenção!", "O número inicial do intervalo deve ser menor que o número final do intervalo.");
                    valida = false;
                }
            } else {
                FacesUtil.addWarn("Atenção!", "É necessário informar o fim do intervalo de final de permissão.");
                valida = false;
            }
        }
        return valida;
    }


    public void gerarRelatorio(String arquivoJasper, HashMap parametros, Connection con)
        throws JRException, IOException {
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        //recupera informação do faces
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
        //gera relatorio com as classes do jasper
        JasperPrint jasperPrint = JasperFillManager.fillReport(scontext.getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, con);
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        byte[] bytes = dadosByte.toByteArray();
        if (bytes != null && bytes.length > 0) {
            int recorte = arquivoJasper.indexOf(".");
            String nomePDF = arquivoJasper.substring(0, recorte);
            HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline; filename=\"" + nomePDF + ".pdf\"");
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
        }
    }

    public JasperPrint gerarRelatorioComDadosEmCollection(String arquivoJasper, HashMap parametros, JRBeanCollectionDataSource jrbc) throws JRException, IOException {
        parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
        JasperPrint jasperPrint = JasperFillManager.fillReport(scontext.getRealPath("/WEB-INF/report/" + arquivoJasper), parametros, jrbc);
        ByteArrayOutputStream dadosByte = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
        exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, dadosByte);
        exporter.exportReport();
        return jasperPrint;
    }

    public void gerarRelatorio(String arquivoJasper, HashMap parametros) throws JRException, IOException {
        final Connection conn = AbstractReport.getAbstractReport().recuperaConexaoJDBC();
        try {
            parametros.put(JRParameter.REPORT_LOCALE, new Locale("pt", "BR"));
            gerarRelatorio(arquivoJasper, parametros, conn);
        } catch (Exception e) {
            logger.error("Erro: ", e);
        } finally {
            AbstractReport.fecharConnection(conn);
        }
    }

    public String getCaminhoImagem() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String caminho = ((ServletContext) facesContext.getExternalContext().getContext()).getRealPath("/img");
        caminho += "/";
        return caminho;
    }

    public boolean validaEmissaoCredenciais() {
        boolean valida = true;
        if (getCredenciaisSelecionadas().length < 1 || getCredenciaisSelecionadas() == null) {
            FacesUtil.addWarn("Atenção!", "Selecione ao menos uma credencial a ser emitida.");
            valida = false;
        }

        return valida;
    }

    public String montaCondicao() {
        StringBuilder condicao = new StringBuilder("WHERE c.id IN (");
        for (CredencialRBTrans obj : getCredenciaisSelecionadas()) {
            condicao.append("'").append(obj.getId()).append("',");
        }
        condicao = new StringBuilder(condicao.toString().substring(0, condicao.toString().length() - 1));
        condicao.append(")");
        return condicao.toString();
    }

    public void pesquisar() {
        if (validaPesquisa()) {
            setListaCredenciais(credencialRBTransFacade.buscarCredenciaisPorFiltroDeGeracao(getFiltro()));
            Collections.sort(getListaCredenciais());
        }
    }

    public void emitir2ViaCredencial(CredencialRBTrans credencial) throws IOException, JRException {
        if (credencial != null) {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";
            String arquivoJasper = "";
            HashMap parameters = new HashMap();
            parameters.put("IMG", getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", subReport);

            List<JasperPrint> jaspers = Lists.newArrayList();
            if (credencial.getTipoCredencialRBTrans().equals(TipoCredencialRBTrans.TRANSPORTE)) {
                arquivoJasper = "CredencialTransporteRBTrans_OBJ_root.jasper";
                getListaCredenciaisTransporte().clear();
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(carregaCredencialTransporteParaEmissao(credencial));
                jaspers.add(gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, jr));
            } else {
                arquivoJasper = "CredencialTransporteRBTrans_OBJ_trafego_root.jasper";
                getListaCredenciaisTrafego().clear();
                JRBeanCollectionDataSource jr = new JRBeanCollectionDataSource(carregaCredencialTrafegoParaEmissao(credencial));
                jaspers.add(gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, jr));
            }
            imprimirJaspers(jaspers);
        }
    }

    public void emitirCredenciais() throws IOException, JRException {
        if (validaEmissaoCredenciais()) {
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport += "/report/";

            HashMap parameters = new HashMap();
            parameters.put("IMG", getCaminhoImagem());
            parameters.put("SUBREPORT_DIR", subReport);

            List<JasperPrint> jaspers = Lists.newArrayList();

            List<VoCredencialTransporteRBTrans> voCredencialTransporteRBTranses = carregarListaCredenciaisTransporte();
            if (voCredencialTransporteRBTranses != null && !voCredencialTransporteRBTranses.isEmpty()) {
                JRBeanCollectionDataSource jrTransporte = new JRBeanCollectionDataSource(voCredencialTransporteRBTranses);
                jaspers.add(gerarRelatorioComDadosEmCollection("CredencialTransporteRBTrans_OBJ_root.jasper", parameters, jrTransporte));
            }

            List<VoCredencialTrafegoRBTrans> voCredencialTrafegoRBTranses = carregaListaCredencialTrafego();
            if (voCredencialTrafegoRBTranses != null && !voCredencialTrafegoRBTranses.isEmpty()) {
                JRBeanCollectionDataSource jrTrafego = new JRBeanCollectionDataSource(voCredencialTrafegoRBTranses);
                jaspers.add(gerarRelatorioComDadosEmCollection("CredencialTransporteRBTrans_OBJ_trafego_root.jasper", parameters, jrTrafego));
            }
            if (!jaspers.isEmpty()) {
                imprimirJaspers(jaspers);
            }
        }
    }


    private void imprimirJaspers(List<JasperPrint> jaspers) throws JRException, IOException {
        byte[] bytes = AbstractReport.getAbstractReport().exportarJaspersParaPDF(jaspers).toByteArray();
        if (bytes != null && bytes.length > 0) {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/pdf");
            response.setHeader("Content-disposition", "inline; filename=\"Credencial.pdf\"");
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
        }
    }

    public CadastroEconomico retornaCadastroEconomicoMotoristaVigente(List<MotoristaAuxiliar> motoristas) {
        for (MotoristaAuxiliar obj : motoristas) {
            if (DataUtil.isVigente(obj.getInicioVigencia(), obj.getFinalVigencia())) {
                return obj.getCadastroEconomico();
            }
        }
        return new CadastroEconomico();
    }

//    public CadastroEconomico retornaCadastroEconomico(PermissaoTransporte permissao, TipoRequerenteCredencialRBTrans tipoRequerente) {
//        if (tipoRequerente.equals(TipoRequerenteCredencialRBTrans.PERMISSIONARIO)) {
//            return permissao.getPermissionarioVigente().getCadastroEconomico();
//        }
//        if (tipoRequerente.equals(TipoRequerenteCredencialRBTrans.MOTORISTA)) {
//            return retornaCadastroEconomicoMotoristaVigente(permissao.getMotoristasAuxiliares());
//        }
//        return new CadastroEconomico();
//    }

    public String retornaNomereduzido(PermissaoTransporte permissao, CadastroEconomico ce, TipoRequerenteCredencialRBTrans tipoRequerente) {
        if (tipoRequerente.equals(TipoRequerenteCredencialRBTrans.PERMISSIONARIO)) {
            return permissao.getPermissionarioVigente().getNomeReduzido();
        }
        for (MotoristaAuxiliar obj : permissao.getMotoristasAuxiliares()) {
            if (ce.equals(obj.getCadastroEconomico())) {
                if (obj.getFinalVigencia() == null) {
                    return obj.getNomeReduzidoMotorista();
                }
            }
        }
        return "";
    }


    public List<VoCredencialTrafegoRBTrans> carregaListaCredencialTrafego() {
        getListaCredenciaisTrafego().clear();
        for (CredencialRBTrans obj : getCredenciaisSelecionadas()) {
            if (TipoCredencialRBTrans.TRAFEGO.equals(obj.getTipoCredencialRBTrans())) {
                carregaCredencialTrafegoParaEmissao(obj);
            }
        }
        return getListaCredenciaisTrafego();
    }

    private List<VoCredencialTrafegoRBTrans> carregaCredencialTrafegoParaEmissao(CredencialRBTrans credencial) {
        setVoCredencialTrafegoRBTrans(new VoCredencialTrafegoRBTrans());
        if (credencial.getDataEmissao() == null) {
            credencial.setDataEmissao(new Date());
        }

        credencialRBTransFacade.salvar(credencial);
        getVoCredencialTrafegoRBTrans().setInputStreamChancela(carregarChancelaVigente());

        getVoCredencialTrafegoRBTrans().setDataEmissao(DataUtil.getDataFormatada(new Date()));
        PermissaoTransporte permissao = credencialRBTransFacade.getPermissaoTransporteFacade().recuperar(credencial.getPermissaoTransporte().getId());
        getVoCredencialTrafegoRBTrans().setValidaAte(DataUtil.getDataFormatada(credencial.getDataValidade()));
        getVoCredencialTrafegoRBTrans().setTipoPermissao(permissao.getTipoPermissaoRBTrans().name());
        getVoCredencialTrafegoRBTrans().setNumeroPermissao(permissao.getNumero().toString());

        CadastroEconomico ce = credencial.getCadastroEconomico();
        getVoCredencialTrafegoRBTrans().setNumeroCMC(ce.getInscricaoCadastral());
        definirPontoTaxiToTaxi(permissao, ce);
        getVoCredencialTrafegoRBTrans().setNomePermissionario(ce.getPessoa().getNome());

        VeiculoPermissionario veiculo = credencialRBTransFacade.getPermissaoTransporteFacade().retornaUltimoVeiculo(true, permissao);
        if (veiculo != null) {
            getVoCredencialTrafegoRBTrans().setPlaca(veiculo.getVeiculoTransporte().getPlaca());
            getVoCredencialTrafegoRBTrans().setAnoFabricacao(veiculo.getVeiculoTransporte().getAnoFabricacao().toString());
            getVoCredencialTrafegoRBTrans().setMarcaModelo(new StringBuilder(veiculo.getVeiculoTransporte().getModelo().getMarca().getDescricao())
                .append("/")
                .append(veiculo.getVeiculoTransporte().getModelo().getDescricao()).toString());
        }
        getListaCredenciaisTrafego().add(getVoCredencialTrafegoRBTrans());
        return getListaCredenciaisTrafego();
    }

    private void definirPontoTaxiToTaxi(PermissaoTransporte permissao, CadastroEconomico ce) {
        if (getVoCredencialTrafegoRBTrans().getTipoPermissao().equals(TipoPermissaoRBTrans.TAXI.name())) {
            getVoCredencialTrafegoRBTrans().setPonto(Boolean.TRUE);

            if (permissao.getPontoTaxi() != null && permissao.getPontoTaxi().getLocalizacao() != null)
                if (!permissao.getPontoTaxi().getLocalizacao().trim().isEmpty()) {
                    // 17 é o tamanho máximo para não estourar a margem na carteira
                    String ponto = StringUtil.cortaDireita(17, permissao.getPontoTaxi().getLocalizacao());
                    System.out.println(" tamanho do ponto" + ponto.length());
                    getVoCredencialTrafegoRBTrans().setPontoTaxi(ponto);
                }
        } else {
            getVoCredencialTrafegoRBTrans().setCpfPermissionario(ce.getPessoa().getCpf_Cnpj());
        }
    }

    public List<VoCredencialTransporteRBTrans> carregarListaCredenciaisTransporte() {
        getListaCredenciaisTransporte().clear();
        for (CredencialRBTrans obj : getCredenciaisSelecionadas()) {
            if (TipoCredencialRBTrans.TRANSPORTE.equals(obj.getTipoCredencialRBTrans())) {
                carregaCredencialTransporteParaEmissao(obj);
            }
        }
        return getListaCredenciaisTransporte();
    }

    private List<VoCredencialTransporteRBTrans> carregaCredencialTransporteParaEmissao(CredencialRBTrans credencial) {
        setVoCredencialTransporteRBTrans(new VoCredencialTransporteRBTrans());
        getVoCredencialTransporteRBTrans().setDataEmissao(DataUtil.getDataFormatada(new Date()));
        if (credencial.getDataEmissao() == null) {
            credencial.setDataEmissao(new Date());
        }

        credencialRBTransFacade.salvar(credencial);
        getVoCredencialTransporteRBTrans().setInputStreamChancela(carregarChancelaVigente());

        PermissaoTransporte permissao = credencialRBTransFacade.getPermissaoTransporteFacade().recuperar(credencial.getPermissaoTransporte().getId());
        VeiculoTransporte veiculo = credencialRBTransFacade.getPermissaoTransporteFacade().recuperaVeiculosVigente(permissao);
        getVoCredencialTransporteRBTrans().setDataValidade(DataUtil.getDataFormatada(credencial.getDataValidade()));
        getVoCredencialTransporteRBTrans().setTipoPermisssao(permissao.getTipoPermissaoRBTrans().name());
        getVoCredencialTransporteRBTrans().setNumeroPermissao(permissao.getNumero().toString());
        getVoCredencialTransporteRBTrans().setClassificacao(credencial.getTipoRequerente().getDescricao());

        CadastroEconomico ce = credencial.getCadastroEconomico();
        getVoCredencialTransporteRBTrans().setNomeCompletoRequerente(ce.getPessoa().getNome());
        getVoCredencialTransporteRBTrans().setNomeReduzido(retornaNomereduzido(permissao, credencial.getCadastroEconomico(), credencial.getTipoRequerente()));
        getVoCredencialTransporteRBTrans().setNumeroCMCRequerente(ce.getInscricaoCadastral());

        if (veiculo != null) {
            getVoCredencialTransporteRBTrans().setPlacaVeiculo(veiculo.getPlaca());
        }
        getVoCredencialTransporteRBTrans().setInputStreamImagem(carregaFoto(ce));
        getListaCredenciaisTransporte().add(getVoCredencialTransporteRBTrans());

        return getListaCredenciaisTransporte();
    }


    public InputStream carregaFoto(CadastroEconomico ce) {
        Arquivo arq = credencialRBTransFacade.getPermissaoTransporteFacade().getArquivoFacade().recuperarArquivoDaPessoa(ce.getPessoa());
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : arq.getPartes()) {
                    buffer.write(a.getDados());
                }

                return new ByteArrayInputStream(buffer.toByteArray());
            } catch (Exception ex) {
                logger.debug(ex.getMessage());
            }
        }
        return null;
    }

    public InputStream carregarChancelaVigente() {
        Exercicio exercicioCorrente = credencialRBTransFacade.getSistemaFacade().getExercicioCorrente();
        ParametrosInformacoesRBTrans param = credencialRBTransFacade.getParametroInformacaoRBTransFacade().buscarParametroPeloExercicio(exercicioCorrente);
        if (param == null) return null;
        Arquivo arq = param.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo();
        if (arq != null) {
            arq = credencialRBTransFacade.getArquivoFacade().recuperaDependencias(arq.getId());
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : arq.getPartes()) {
                    buffer.write(a.getDados());
                }

                return new ByteArrayInputStream(buffer.toByteArray());
            } catch (Exception ex) {
                logger.debug(ex.getMessage());
            }
        }
        return null;
    }

    public VoCredencialTrafegoRBTrans getVoCredencialTrafegoRBTrans() {
        return voCredencialTrafegoRBTrans;
    }

    public void setVoCredencialTrafegoRBTrans(VoCredencialTrafegoRBTrans voCredencialTrafegoRBTrans) {
        this.voCredencialTrafegoRBTrans = voCredencialTrafegoRBTrans;
    }

    public List<VoCredencialTrafegoRBTrans> getListaCredenciaisTrafego() {
        return listaCredenciaisTrafego;
    }

    public void setListaCredenciaisTrafego(List<VoCredencialTrafegoRBTrans> listaCredenciaisTrafego) {
        this.listaCredenciaisTrafego = listaCredenciaisTrafego;
    }

    public VoCredencialTransporteRBTrans getVoCredencialTransporteRBTrans() {
        return voCredencialTransporteRBTrans;
    }

    public void setVoCredencialTransporteRBTrans(VoCredencialTransporteRBTrans voCredencialTransporteRBTrans) {
        this.voCredencialTransporteRBTrans = voCredencialTransporteRBTrans;
    }

    public List<VoCredencialTransporteRBTrans> getListaCredenciaisTransporte() {
        return listaCredenciaisTransporte;
    }

    public void setListaCredenciaisTransporte(List<VoCredencialTransporteRBTrans> listaCredenciaisTransporte) {
        this.listaCredenciaisTransporte = listaCredenciaisTransporte;
    }

    public CredencialRBTrans[] getCredenciaisSelecionadas() {
        return credenciaisSelecionadas;
    }

    public void setCredenciaisSelecionadas(CredencialRBTrans[] credenciaisSelecionadas) {
        this.credenciaisSelecionadas = credenciaisSelecionadas;
    }

    public List<CredencialRBTrans> getListaCredenciais() {
        return listaCredenciais;
    }

    public void setListaCredenciais(List<CredencialRBTrans> listaCredenciais) {
        this.listaCredenciais = listaCredenciais;
    }

    public FiltroGeracaoCredencialRBTrans getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroGeracaoCredencialRBTrans filtro) {
        this.filtro = filtro;
    }

    public boolean verificarSeCredencialEstaVencida(CredencialRBTrans credencialRBTrans) {
        return credencialRBTrans != null && credencialRBTrans.getDataValidade().
            before(DataUtil.dataSemHorario(credencialRBTransFacade.getSistemaFacade().getDataOperacao()));
    }
}
