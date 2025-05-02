package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConsultaDeViagensOtt;
import br.com.webpublico.entidades.OperadoraTecnologiaTransporte;
import br.com.webpublico.entidades.ViagemOperadoraTecnologiaTransporte;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaDeViagensOttFacade;
import br.com.webpublico.negocios.OperadoraTecnologiaTransporteFacade;
import br.com.webpublico.negocios.ViagemOperadoraTecnologiaTransporteFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author octavio
 */
@ManagedBean(name = "consultaDeViagensOttControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consultaDeViagensOtt", pattern = "/consultaViagem-operadora-tecnologia-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/consultaviagensott/edita.xhtml")
})
public class ConsultaDeViagensOttControlador {

    @EJB
    private ConsultaDeViagensOttFacade consultaDeViagensOttFacade;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraTecnologiaTransporteFacade;
    @EJB
    private ViagemOperadoraTecnologiaTransporteFacade viagemFacade;
    private ConsultaDeViagensOtt selecionado;
    private ViagemOperadoraTecnologiaTransporte viagemOperadoraTecnologiaTransporte;
    private List<ViagemOperadoraTecnologiaTransporte> listaViagensOtt;
    private Set<Date> datasQueNaoTem;
    private UploadedFile file;
    private String arquivoSelecionado;

    public ConsultaDeViagensOttControlador() {
    }

    public ConsultaDeViagensOtt getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ConsultaDeViagensOtt selecionado) {
        this.selecionado = selecionado;
    }

    public List<ViagemOperadoraTecnologiaTransporte> getListaViagensOtt() {
        return listaViagensOtt;
    }

    public void setListaViagensOtt(List<ViagemOperadoraTecnologiaTransporte> listaViagensOtt) {
        this.listaViagensOtt = listaViagensOtt;
    }

    public Set<Date> getDatasQueNaoTem() {
        return datasQueNaoTem;
    }

    public void setDatasQueNaoTem(Set<Date> datasQueNaoTem) {
        this.datasQueNaoTem = datasQueNaoTem;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<OperadoraTecnologiaTransporte> completaOperadora(String parte) {
        return operadoraTecnologiaTransporteFacade.listarOperadoras(parte);
    }

    public String getArquivoSelecionado() {
        return arquivoSelecionado;
    }

    public void setArquivoSelecionado(String arquivoSelecionado) {
        this.arquivoSelecionado = arquivoSelecionado;
    }

    @URLAction(mappingId = "consultaDeViagensOtt", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consulta() {
        selecionado = new ConsultaDeViagensOtt();
        viagemOperadoraTecnologiaTransporte = new ViagemOperadoraTecnologiaTransporte();
        listaViagensOtt = Lists.newArrayList();
        datasQueNaoTem = new LinkedHashSet<>();
    }

    private void listarViagens() {
        if (viagemOperadoraTecnologiaTransporte != null) {
            listaViagensOtt = consultaDeViagensOttFacade.buscarViagensPorOtt(selecionado);
        }
    }

    public void popularLista() {
        try {
            validarCampos();
            listarViagens();
            listarDatasVazias();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getOperadoraTecTransporte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a operadora de transporte!");
        }
        if (selecionado.getDataInicialViagem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data inicial!");
        }
        if (selecionado.getDataFinalViagem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data final!");
        }
        ve.lancarException();
    }

    private void listarDatasVazias() {
        Date inicio = selecionado.getDataInicialViagem();
        Date fim = selecionado.getDataFinalViagem();
        Date dataQueNaoTem = inicio;

        int totalDias = DataUtil.diasEntreDate(inicio, fim);
        if (datasQueNaoTem.isEmpty()) {
            for (int i = 0; i < totalDias; i++) {
                for (ViagemOperadoraTecnologiaTransporte viagens : listaViagensOtt) {
                    if (viagens.getDataChamadas().compareTo(dataQueNaoTem) != 0) {
                        datasQueNaoTem.add(dataQueNaoTem);
                    }
                }
                for (ViagemOperadoraTecnologiaTransporte viagens : listaViagensOtt) {
                    if (viagens.getDataChamadas().compareTo(dataQueNaoTem) == 0) {
                        datasQueNaoTem.remove(dataQueNaoTem);
                    }
                }
                dataQueNaoTem = DataUtil.adicionaDias(dataQueNaoTem, 1);
            }
        } else {
            datasQueNaoTem.clear();
            listarDatasVazias();
        }
    }

    public boolean isRenderizarDatasSemRegistro() {
        return datasQueNaoTem.isEmpty();
    }

    public BigDecimal gerarValorTotalCorridas() {
        BigDecimal total = BigDecimal.ZERO;
        for (ViagemOperadoraTecnologiaTransporte viagem : listaViagensOtt) {
            total = total.add(viagem.getValorTotalCorridas());
        }
        return total;
    }

    public BigDecimal gerarValorPercentualPrecoPublico() {
        BigDecimal precoPublico = BigDecimal.valueOf(2);
        precoPublico = precoPublico.multiply(gerarValorTotalCorridas());
        precoPublico = precoPublico.divide(BigDecimal.valueOf(100));

        return precoPublico;
    }

    public BigDecimal gerarValorISS() {
        return BigDecimal.ZERO;
    }

    public void importarArquivo(FileUploadEvent arq) throws FileNotFoundException, IOException {
        file = arq.getFile();
        arquivoSelecionado = file.getFileName();
    }

    public void processarArquivo() {
        try {
            List<ViagemOperadoraTecnologiaTransporte> viagensLista = Lists.newArrayList();
            InputStreamReader streamReader = new InputStreamReader(file.getInputstream());
            BufferedReader reader = new BufferedReader(streamReader);

            setarViagens(viagensLista, reader);

            reader.close();
            streamReader.close();
            file.getInputstream().close();
            FacesUtil.executaJavaScript("dlgCarregarArq.hide()");
            FacesUtil.addOperacaoRealizada("Viagens cadastradas com sucesso!");
        } catch (IOException | ParseException e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível processar o arquivo!");
        }
    }

    private void setarViagens(List<ViagemOperadoraTecnologiaTransporte> viagensLista, BufferedReader reader) throws IOException, ParseException {
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            String[] todosCampos = line.split(",");
            ViagemOperadoraTecnologiaTransporte viagem = new ViagemOperadoraTecnologiaTransporte();
            viagem.setOperadoraTecTransporte(selecionado.getOperadoraTecTransporte());
            viagem.setQtdChamadas(Long.valueOf(todosCampos[0]));
            Date data = new SimpleDateFormat("ddMMyy").parse(todosCampos[1]);
            viagem.setDataChamadas(data);
            viagem.setQtdCancelamento(Long.valueOf(todosCampos[2]));
            viagem.setQtdCorridas(Long.valueOf(todosCampos[3]));
            viagem.setTempoTotalChamadoSegundos(Long.valueOf(todosCampos[4]));
            viagem.setTempoTotalCorridaSegundos(Long.valueOf(todosCampos[5]));
            viagem.setDistanciaPercorrida(Long.valueOf(todosCampos[6]));
            viagem.setQtdCorridaMasculinas(Long.valueOf(todosCampos[7]));
            viagem.setQtdCorridaFemininas(Long.valueOf(todosCampos[8]));
            viagem.setQtdCorridaVeicPoluentes(Long.valueOf(todosCampos[9]));
            viagem.setQtdCorridaVeicNaoPoluentes(Long.valueOf(todosCampos[10]));
            viagem.setValorTotalCorridas(new BigDecimal(todosCampos[11]));
            viagem.setValorTotalLiquidoCobrado(new BigDecimal(todosCampos[12]));
            viagem.setValorTotalDesconto(new BigDecimal(todosCampos[13]));
            viagem.setAvaliacoes(todosCampos[14]);
            viagem.setQtdCorridaPorHora(todosCampos[15]);
            viagem.setQtdCarrosPorHora(todosCampos[16]);

            viagensLista.add(viagem);

            viagemFacade.salvar(viagem);
        }
    }
}



