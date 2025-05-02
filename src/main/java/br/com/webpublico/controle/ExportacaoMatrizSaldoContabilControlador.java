package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.ActionConsultaEntidade;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.MatrizSaldoContabil;
import br.com.webpublico.entidadesauxiliares.contabil.ValidadorMatrizSaldoContabil;
import br.com.webpublico.entidadesauxiliares.contabil.ValidadorMatrizSaldoContabilBlc;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExportacaoMatrizSaldoContabilFacade;
import br.com.webpublico.negocios.SaldoContaContabilFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "exportacao-msc", pattern = "/exportacao-msc/", viewId = "/faces/financeiro/msc/exportacao.xhtml"),
    @URLMapping(id = "lista-exportacao-msc", pattern = "/exportacao-msc/listar/", viewId = "/faces/financeiro/msc/lista.xhtml")
})
public class ExportacaoMatrizSaldoContabilControlador extends PrettyControlador<ExportacaoMatrizSaldoContabil> implements Serializable, CRUD {

    @EJB
    private ExportacaoMatrizSaldoContabilFacade exportacaoMatrizSaldoContabilFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    private UnidadeGestora unidadeGestora;
    private List<ValidadorMatrizSaldoContabil> msc;
    private List<ValidadorMatrizSaldoContabilBlc> blc;
    private List<ValidadorMatrizSaldoContabilBlc> blcContabil;
    private List<ValidadorMatrizSaldoContabilBlc> blcDiferenca;

    public ExportacaoMatrizSaldoContabilControlador() {
        super(ExportacaoMatrizSaldoContabil.class);
    }

    @URLAction(mappingId = "exportacao-msc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        super.novo();
        selecionado.setMes(Mes.JANEIRO);
        selecionado.setTipoBalanceteExportacao(TipoBalanceteExportacao.AGREGADA);
        unidadeGestora = null;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItemSemCampoVazio(Mes.values(), false);
    }

    public List<SelectItem> getTiposDeBalancete() {
        return Util.getListSelectItemSemCampoVazio(TipoBalanceteExportacao.values(), false);
    }

    public List<UnidadeGestora> completarUnidadesGestoras(String parte) {
        return exportacaoMatrizSaldoContabilFacade.getUnidadeGestoraFacade().completaUnidadeGestoraDasUnidadeDoUsuarioLogadoVigentes(parte.trim(),
            exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getUsuarioCorrente(),
            exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getDataOperacao(),
            exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getExercicioCorrente(),
            TipoUnidadeGestora.MSC);
    }

    public void buscarRegistros() {
        msc = Lists.newArrayList();
        blc = Lists.newArrayList();
        blcContabil = Lists.newArrayList();
        blcDiferenca = Lists.newArrayList();
        List<MatrizSaldoContabil> saldos = exportacaoMatrizSaldoContabilFacade.buscarSaldos(selecionado.getMes(), unidadeGestora, selecionado.getTipoBalanceteExportacao(), buscarTiposIniciais(), buscarTiposFinais());
        for (MatrizSaldoContabil matrizSaldoContabil : saldos) {
            String conta = matrizSaldoContabil.getContaContabilSiconfi().toString().substring(0, 1);
            String naturezaValor = matrizSaldoContabil.getNaturezaValor();
            BigDecimal valor = (matrizSaldoContabil.getValor().compareTo(BigDecimal.ZERO) < 0 ? matrizSaldoContabil.getValor().multiply(new BigDecimal("-1")) : matrizSaldoContabil.getValor());
            ValidadorMatrizSaldoContabil matriz = new ValidadorMatrizSaldoContabil();
            matriz.setCodigo(conta);
            if (naturezaValor.equals("D")) {
                matriz.setDebito(valor);
            }
            if (naturezaValor.equals("C")) {
                matriz.setCredito(valor);
            }
            matriz.setTipoValor(matrizSaldoContabil.getTipoValor());

            Boolean encontrouAlguem = Boolean.FALSE;
            for (ValidadorMatrizSaldoContabil v : msc) {
                if (v.getCodigo().equals(matriz.getCodigo())
                    && v.getTipoValor().equals(matriz.getTipoValor())) {
                    encontrouAlguem = Boolean.TRUE;
                    v.setCredito(v.getCredito().add(matriz.getCredito()));
                    v.setDebito(v.getDebito().add(matriz.getDebito()));
                }
            }

            if (!encontrouAlguem) {
                msc.add(matriz);
            }
        }
        for (ValidadorMatrizSaldoContabil validadorMatrizSaldoContabil : msc) {

            ValidadorMatrizSaldoContabilBlc bal = new ValidadorMatrizSaldoContabilBlc();
            bal.setCodigo(validadorMatrizSaldoContabil.getCodigo());
            if (TipoMatrizSaldoContabil.BEGINNING_BALANCE.equals(validadorMatrizSaldoContabil.getTipoValor())) {
                bal.setSaldoAnterior(validadorMatrizSaldoContabil.getCredito().subtract(validadorMatrizSaldoContabil.getDebito()));
            }
            if (TipoMatrizSaldoContabil.PERIOD_CHANGE.equals(validadorMatrizSaldoContabil.getTipoValor())) {
                bal.setCredito(validadorMatrizSaldoContabil.getCredito());
                bal.setDebito(validadorMatrizSaldoContabil.getDebito());
            }
            if (TipoMatrizSaldoContabil.ENDING_BALANCE.equals(validadorMatrizSaldoContabil.getTipoValor())) {
                bal.setAtual(validadorMatrizSaldoContabil.getCredito().subtract(validadorMatrizSaldoContabil.getDebito()));
            }

            Boolean encontrouAlguem = Boolean.FALSE;
            for (ValidadorMatrizSaldoContabilBlc v : blc) {
                if (v.getCodigo().equals(bal.getCodigo())) {
                    encontrouAlguem = Boolean.TRUE;
                    v.setSaldoAnterior(v.getSaldoAnterior().add(bal.getSaldoAnterior()));
                    v.setCredito(v.getCredito().add(bal.getCredito()));
                    v.setDebito(v.getDebito().add(bal.getDebito()));
                    v.setAtual(v.getAtual().add(bal.getAtual()));
                }
            }

            if (!encontrouAlguem) {
                blc.add(bal);
            }
        }
        buscarBalanceteContabil();
        verificarDiferenca();
    }

    private void verificarDiferenca() {
        if (blcContabil != null && blc != null) {
            blcDiferenca = Lists.newArrayList();
            for (ValidadorMatrizSaldoContabilBlc bal : blcContabil) {
                for (ValidadorMatrizSaldoContabilBlc msc : blc) {
                    if (bal.getCodigo().equals(msc.getCodigo())) {

                        if (bal.getAtual().setScale(2, RoundingMode.HALF_UP).compareTo(msc.getAtual().setScale(2, RoundingMode.HALF_UP)) != 0 ||
                            bal.getSaldoAnterior().setScale(2, RoundingMode.HALF_UP).compareTo(msc.getSaldoAnterior().setScale(2, RoundingMode.HALF_UP)) != 0) {
                            ValidadorMatrizSaldoContabilBlc dif = new ValidadorMatrizSaldoContabilBlc();
                            dif.setCodigo(bal.getCodigo());
                            dif.setSaldoAnterior(bal.getSaldoAnterior().subtract(msc.getSaldoAnterior()));
                            dif.setCredito(bal.getCredito().add(msc.getCredito()));
                            dif.setDebito(bal.getDebito().add(msc.getDebito()));
                            dif.setAtual(bal.getAtual().subtract(msc.getAtual()));
                            blcDiferenca.add(dif);
                        }
                    }
                }
            }
            if (blcDiferenca.isEmpty()) {
                FacesUtil.addInfo("Informação", "Nenhuma diferença foi encontrada.");
            }
        }
    }

    private void buscarBalanceteContabil() {
        Exercicio exercicio = exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getExercicioCorrente();


        String dataInicial = "01/" + selecionado.getMes().getNumeroMesString() + "/" + exercicio.getAno();
        String dataFinal = Util.getDiasMes(selecionado.getMes().getNumeroMes(), exercicio.getAno()) + "/" + selecionado.getMes().getNumeroMesString() + "/" + exercicio.getAno();

        blcContabil = saldoContaContabilFacade.buscarSaldoAtualBalanceteContabil(DataUtil.getDateParse(dataInicial), DataUtil.getDateParse(dataFinal), exercicio);

    }

    public StreamedContent exportar() {
        try {
            List<String> cabecalho = Lists.newArrayList();
            cabecalho.add(exportacaoMatrizSaldoContabilFacade.getConfiguracaoContabilFacade().configuracaoContabilVigente().getCodigoCabecalhoArquivoMSC());
            cabecalho.add(exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getExercicioCorrente().getAno() + "-" + (TipoBalanceteExportacao.ENCERRAMENTO.equals(selecionado.getTipoBalanceteExportacao()) ? "13" : selecionado.getMes().getNumeroMesString()));

            List<Object[]> objetos = Lists.newArrayList();
            Object[] titulos = new Object[18];
            titulos[0] = "CONTA";
            titulos[1] = "IC1";
            titulos[2] = "TIPO1";
            titulos[3] = "IC2";
            titulos[4] = "TIPO2";
            titulos[5] = "IC3";
            titulos[6] = "TIPO3";
            titulos[7] = "IC4";
            titulos[8] = "TIPO4";
            titulos[9] = "IC5";
            titulos[10] = "TIPO5";
            titulos[11] = "IC6";
            titulos[12] = "TIPO6";
            titulos[13] = "IC7";
            titulos[14] = "TIPO7";
            titulos[15] = "Valor";
            titulos[16] = "Tipo_valor";
            titulos[17] = "Natureza_valor";
            objetos.add(titulos);
            List<MatrizSaldoContabil> saldos = exportacaoMatrizSaldoContabilFacade.buscarSaldos(selecionado.getMes(), unidadeGestora, selecionado.getTipoBalanceteExportacao(), buscarTiposIniciais(), buscarTiposFinais());
            for (MatrizSaldoContabil matrizSaldoContabil : saldos) {
                Object[] obj = new Object[18];
                obj[0] = matrizSaldoContabil.getContaContabilSiconfi();
                obj[1] = matrizSaldoContabil.getIc1();
                obj[2] = matrizSaldoContabil.getTipo1();
                obj[3] = matrizSaldoContabil.getIc2();
                obj[4] = matrizSaldoContabil.getTipo2();
                obj[5] = matrizSaldoContabil.getIc3();
                obj[6] = matrizSaldoContabil.getTipo3();
                obj[7] = matrizSaldoContabil.getIc4();
                obj[8] = matrizSaldoContabil.getTipo4();
                obj[9] = matrizSaldoContabil.getIc5();
                obj[10] = matrizSaldoContabil.getTipo5();
                obj[11] = matrizSaldoContabil.getIc6();
                obj[12] = matrizSaldoContabil.getTipo6();
                obj[13] = matrizSaldoContabil.getIc7();
                obj[14] = matrizSaldoContabil.getTipo7();
                obj[15] = (matrizSaldoContabil.getValor().compareTo(BigDecimal.ZERO) < 0 ? matrizSaldoContabil.getValor().multiply(new BigDecimal("-1")) : matrizSaldoContabil.getValor());
                obj[16] = matrizSaldoContabil.getTipoValor().getTipo();
                obj[17] = matrizSaldoContabil.getNaturezaValor();
                objetos.add(obj);
            }
            ExcelUtil excel = new ExcelUtil();
            excel.gerarCSV("Matriz de Saldos Contábeis", getNomeArquivo(), cabecalho, objetos, false, false, false);
            salvar(excel.fileDownload().getStream());
            return excel.fileDownload();
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
        return null;
    }

    private String getNomeArquivo() {
        return "MSC-" + exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getExercicioCorrente().getAno() + "-"
            + selecionado.getMes().getNumeroMesString()
            + (unidadeGestora != null ? "-" + unidadeGestora.getCodigo() : "") + "-"
            + (TipoBalanceteExportacao.ENCERRAMENTO.equals(selecionado.getTipoBalanceteExportacao()) ? "E" : "A"); //‘A’ = Agregada (jan a dez) e ‘E’ = Encerramento (dez)
    }

    private List<String> buscarTiposIniciais() {
        List<String> retorno = Lists.newArrayList();
        switch (selecionado.getTipoBalanceteExportacao()) {
            case AGREGADA:
                retorno.add(TipoBalancete.TRANSPORTE.name());
                if (!Mes.JANEIRO.equals(selecionado.getMes())) {
                    retorno.add(TipoBalancete.ABERTURA.name());
                    retorno.add(TipoBalancete.MENSAL.name());
                    retorno.add(TipoBalancete.APURACAO.name());
                }
                break;
            case ENCERRAMENTO:
                retorno.add(TipoBalancete.TRANSPORTE.name());
                retorno.add(TipoBalancete.ABERTURA.name());
                retorno.add(TipoBalancete.MENSAL.name());
                retorno.add(TipoBalancete.APURACAO.name());
                break;
        }
        return retorno;
    }

    private List<String> buscarTiposFinais() {
        List<String> retorno = Lists.newArrayList();
        switch (selecionado.getTipoBalanceteExportacao()) {
            case AGREGADA:
                if (!Mes.JANEIRO.equals(selecionado.getMes())) {
                    retorno.add(TipoBalancete.TRANSPORTE.name());
                }
                retorno.add(TipoBalancete.ABERTURA.name());
                retorno.add(TipoBalancete.MENSAL.name());
                retorno.add(TipoBalancete.APURACAO.name());
                break;
            case ENCERRAMENTO:
                retorno.add(TipoBalancete.ENCERRAMENTO.name());
                break;
        }
        return retorno;
    }

    //Utilizado para calcular corretamente os movimentos de crédito e débito filtrando os tipos na última data do saldo.
    private List<String> buscarTipoBalanceteFinalParaSaldoAtual(TipoBalancete tipoInicial, TipoBalancete tipoFinal) {
        List<String> toReturn = Lists.newArrayList();
        switch (tipoFinal) {
            case TRANSPORTE:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case ABERTURA:
                toReturn.add(TipoBalancete.ABERTURA.name());
                break;
            case MENSAL:
                if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.ABERTURA.name());
                }
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case APURACAO:
                toReturn.add(TipoBalancete.APURACAO.name());
                if (TipoBalancete.MENSAL.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                } else if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.ABERTURA.name());
                }
                break;
            case ENCERRAMENTO:
                toReturn.add(TipoBalancete.ENCERRAMENTO.name());
                if (TipoBalancete.APURACAO.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.APURACAO.name());
                } else if (TipoBalancete.MENSAL.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                } else if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.ABERTURA.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                }
                break;
        }
        return toReturn;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    @Override
    public AbstractFacade getFacede() {
        return exportacaoMatrizSaldoContabilFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exportacao-msc/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void salvar(InputStream inputStream) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setInputStream(inputStream);
            arquivo.setNome(getNomeArquivo() + ".csv");
            arquivo.setDescricao("Matriz de Saldos Contábeis");
            arquivo.setMimeType("application/vnd.ms-excel");
            arquivo = exportacaoMatrizSaldoContabilFacade.getArquivoFacade().novoArquivoMemoria(arquivo);
            selecionado.setArquivo(arquivo);
            selecionado.setDataExportacao(exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getDataOperacao());
            selecionado.setUsuarioSistema(exportacaoMatrizSaldoContabilFacade.getSistemaFacade().getUsuarioCorrente());
            validaRegrasParaSalvar();
            exportacaoMatrizSaldoContabilFacade.salvar(selecionado);
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<ActionConsultaEntidade> getActionsPesquisa() {
        ActionConsultaEntidade action = new ActionExportacaoMatrizSaldoContabil();
        action.setIcone("ui-icon-download");
        action.setTitle("Clique para fazer o download do arquivo.");
        action.setDownload(true);
        return Lists.newArrayList(action);
    }

    public class ActionExportacaoMatrizSaldoContabil extends ActionConsultaEntidade {
        @Override
        public void action(Map<String, Object> registro) throws IOException {
            Long idEmsc = ((Number) registro.get("identificador")).longValue();
            ExportacaoMatrizSaldoContabil emsc = exportacaoMatrizSaldoContabilFacade.recuperar(idEmsc);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : emsc.getArquivo().getPartes()) {
                try {
                    buffer.write(a.getDados());
                } catch (IOException ex) {
                    logger.error("Erro: ", ex);
                }
            }
            InputStream is = new ByteArrayInputStream(buffer.toByteArray());
            StreamedContent streamedContent = new DefaultStreamedContent(is, emsc.getArquivo().getMimeType(), emsc.getArquivo().getNome());
            escreverNoResponse(IOUtils.toByteArray(streamedContent.getStream()), emsc.getArquivo().getNome(), streamedContent.getContentType());
        }
    }

    public List<ValidadorMatrizSaldoContabil> getMsc() {
        return msc;
    }

    public void setMsc(List<ValidadorMatrizSaldoContabil> msc) {
        this.msc = msc;
    }

    public List<ValidadorMatrizSaldoContabilBlc> getBlc() {
        return blc;
    }

    public void setBlc(List<ValidadorMatrizSaldoContabilBlc> blc) {
        this.blc = blc;
    }

    public List<ValidadorMatrizSaldoContabilBlc> getBlcContabil() {
        return blcContabil;
    }

    public void setBlcContabil(List<ValidadorMatrizSaldoContabilBlc> blcContabil) {
        this.blcContabil = blcContabil;
    }

    public List<ValidadorMatrizSaldoContabilBlc> getBlcDiferenca() {
        return blcDiferenca;
    }

    public void setBlcDiferenca(List<ValidadorMatrizSaldoContabilBlc> blcDiferenca) {
        this.blcDiferenca = blcDiferenca;
    }
}
