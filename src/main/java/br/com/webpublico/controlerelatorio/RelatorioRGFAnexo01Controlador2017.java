/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.RGFAnexo01Pessoal2017;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.enums.TipoAdministracao;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.ItemDemonstrativoCalculator;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo01Calculator2017;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo1-2017", pattern = "/relatorio/rgf/anexo1/2017", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo01-2017.xhtml")})
public class RelatorioRGFAnexo01Controlador2017 extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private ItemDemonstrativoCalculator itemDemonstrativoCalculator;
    @EJB
    private RelatorioRGFAnexo01Calculator2017 relatorioRGFAnexo01Calculator2017;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private Esferas esferas;
    private BigDecimal percentual;
    private BigDecimal percentualPrudencial;
    private BigDecimal percentualLimite;
    private BigDecimal rcl;
    private BigDecimal percentualDespesa;
    private BigDecimal limiteMaximo;
    private BigDecimal limitePrudencial;
    private BigDecimal limiteAlerta;
    private BigDecimal despesaTotalComPessoal;
    private String mesInicial;
    private String mesFinal;
    @Enumerated(EnumType.STRING)
    private TipoAdministracao tipoAdministracao;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;

    public RelatorioRGFAnexo01Controlador2017() {
        itens = new ArrayList<ItemDemonstrativoComponente>();
    }

    public List<SelectItem> getListaEsferas() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Esferas es : Esferas.values()) {
            toReturn.add(new SelectItem(es, es.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "relatorio-rgf-anexo1-2017", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        this.esferas = Esferas.CONSOLIDADO;
        this.percentual = new BigDecimal(0);
        this.percentualPrudencial = new BigDecimal(0);
        this.rcl = new BigDecimal(0);
        this.percentualDespesa = new BigDecimal(0);
        this.limiteMaximo = new BigDecimal(0);
        this.limitePrudencial = new BigDecimal(0);
        this.despesaTotalComPessoal = new BigDecimal(0);
        this.mesFinal = "";
        this.mesInicial = "";
    }

    private List<RGFAnexo01Pessoal2017> geraDadosGrupoUm() {
        String data = mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno();
        for (int i = 1; i <= 11; i++) {
            data = alterarMes(data);
        }
        return relatorioRGFAnexo01Calculator2017.geraDadosGrupoUm(itens, relatoriosItemDemonst, data.substring(0, 2), mesFinal, sistemaControlador.getExercicioCorrente(), despesaTotalComPessoal, tipoAdministracao != null ? tipoAdministracao.name().toString() : null, esferas.name().toString());
    }

    public List<RGFAnexo01Pessoal2017> buscaDadosGrupoUm() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(sistemaControlador.getExercicioCorrente(), "", "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        itens = new ArrayList<ItemDemonstrativoComponente>();
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            ItemDemonstrativoComponente itemDemonstrativoComponente = new ItemDemonstrativoComponente();
            itemDemonstrativoComponente.setNome(itemDemonstrativo.getNome());
            itemDemonstrativoComponente.setDescricao(itemDemonstrativo.getDescricao());
            itemDemonstrativoComponente.setOrdem(itemDemonstrativo.getOrdem());
            itemDemonstrativoComponente.setColuna(itemDemonstrativo.getColuna());
            itemDemonstrativoComponente.setGrupo(itemDemonstrativo.getGrupo());
            itemDemonstrativoComponente.setEspaco(itemDemonstrativo.getEspaco());
            itemDemonstrativoComponente.setItemDemonstrativo(itemDemonstrativo);
            itens.add(itemDemonstrativoComponente);
        }
        despesaTotalComPessoal = BigDecimal.ZERO;
        String data = mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno();
        for (int i = 1; i <= 11; i++) {
            data = alterarMes(data);
        }
        return relatorioRGFAnexo01Calculator2017.geraDadosGrupoUm(itens, relatoriosItemDemonst, data.substring(0, 2), mesFinal, sistemaControlador.getExercicioCorrente(), despesaTotalComPessoal, tipoAdministracao != null ? tipoAdministracao.name().toString() : null, esferas.name().toString());
    }

    private String alterarMes(String data) {
        Integer mes = Integer.parseInt(data.substring(0, 2));
        Integer ano = Integer.parseInt(data.substring(3, 7));
        if (mes != 1) {
            mes += -1;
        } else {
            mes = 12;
            ano += -1;
        }
        String toReturn;
        if (mes < 10) {
            toReturn = "0" + mes + "/" + ano;
        } else {
            toReturn = mes + "/" + ano;
        }
        return toReturn;
    }

    private String formataDataMesAno(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("MM/yyyy");
        return formato.format(data);
    }

    private BigDecimal calcularRcl() {
        try {
            BigDecimal toReturn = BigDecimal.ZERO;
            RelatoriosItemDemonst relatorio = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
            ItemDemonstrativo itemDemonstrativo = itemDemonstrativoCalculator.recuperarItemDemonstrativoPeloNomeERelatorio("RECEITA CORRENTE LÍQUIDA", sistemaControlador.getExercicioCorrente(), relatorio);
            GregorianCalendar dataCalendar = new GregorianCalendar(sistemaControlador.getExercicioCorrente().getAno(), new Integer(mesFinal) - 1, new Integer(1));
            String data = formataDataMesAno(dataCalendar.getTime());
            for (int i = 1; i <= 12; i++) {
                toReturn = toReturn.add(relatorioRGFAnexo01Calculator2017.getRelatorioRREOAnexo03Calculator().calcularAlterado(itemDemonstrativo, data, relatorio));
                data = alterarMes(data);
            }
            return toReturn;
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    public List<SelectItem> getListaTipoAdministracao() {
        return Util.getListSelectItem(TipoAdministracao.values());
    }

    public void gerarRelatorio() {
        try {
            List<RGFAnexo01Pessoal2017> lista = recuperarRelatorios();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(lista);
            HashMap parameters = montarParametros();
            gerarRelatorioComDadosEmCollection(getNomeRelatorio(), parameters, ds);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void salvarRelatorio() {
        try {
            List<RGFAnexo01Pessoal2017> lista = recuperarRelatorios();
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(lista);
            HashMap parameters = montarParametros();
            PortalTransparencia anexo = new PortalTransparencia();
            anexo.setExercicio(sistemaControlador.getExercicioCorrente());
            anexo.setNome("Anexo 1 - Demonstrativo da Despesa com Pessoal");
            anexo.setMes(Mes.getMesToInt(Integer.valueOf(mesFinal)));
            anexo.setTipo(PortalTransparenciaTipo.RGF);
            salvarArquivoPortalTransparencia(getNomeRelatorio(), parameters, ds, anexo);
            FacesUtil.addOperacaoRealizada(" O Relatório " + anexo.toString() + " foi salvo com sucesso.");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RelatorioRGFAnexo01_2017.jasper";
    }

    private List<RGFAnexo01Pessoal2017> recuperarRelatorios() {
        relatoriosItemDemonst = itemDemonstrativoFacade.getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", sistemaControlador.getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        List<RGFAnexo01Pessoal2017> lista = geraDadosGrupoUm();
        for (RGFAnexo01Pessoal2017 item : lista) {
            if (item.getDescricao().equals("DESPESA LÍQUIDA COM PESSOAL (III) = (I - II)")) {
                despesaTotalComPessoal = item.getLiquidacao().add(item.getInscritasRestos());
                break;
            }
        }
        return lista;
    }

    private HashMap montarParametros() {
        HashMap parameters = new HashMap();
        if (esferas.name().equals("EXECUTIVO")) {
            percentual = new BigDecimal(54);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO DA DESPESA COM PESSOAL");
        } else if (esferas.name().equals("LEGISLATIVO")) {
            percentual = new BigDecimal(6);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO DA DESPESA COM PESSOAL");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
        } else {
            percentual = new BigDecimal(60);
            percentualPrudencial = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.05)));
            percentualLimite = percentual.subtract(percentual.multiply(BigDecimal.valueOf(0.1)));
            parameters.put("NOME_RELATORIO", "DEMONSTRATIVO CONSOLIDADO DA DESPESA COM PESSOAL");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        }
        parameters.put("PERCENTUAL", percentual);
        parameters.put("PERCENTUALPRUD", percentualPrudencial);
        parameters.put("PERCENTUALALERTA", percentualLimite);
        rcl = calcularRcl();
        if (rcl != BigDecimal.ZERO) {
            percentualDespesa = despesaTotalComPessoal.divide(rcl, 6, RoundingMode.HALF_EVEN).multiply(BigDecimal.valueOf(100));
            limiteMaximo = rcl.multiply(percentual).divide(BigDecimal.valueOf(100), 2);
            limitePrudencial = rcl.multiply(percentualPrudencial).divide(BigDecimal.valueOf(100), 2);
            limiteAlerta = rcl.multiply(percentualLimite).divide(BigDecimal.valueOf(100), 2);
        }
        parameters.put("RCL", rcl);
        parameters.put("PERCENTUALDESP", percentualDespesa);
        parameters.put("LIMITEMAXIMO", limiteMaximo);
        parameters.put("LIMITEPRUDENCIAL", limitePrudencial);
        parameters.put("LIMITEALERTA", limiteAlerta);
        parameters.put("DESPESA", despesaTotalComPessoal);

        parameters.put("IMAGEM", getCaminhoImagem());
        if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
        } else {
            parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
        }
        parameters.put("DATAINICIAL", retornaDescricaoMes(mesInicial));
        parameters.put("DATAFINAL", retornaDescricaoMes(mesFinal) + " DE " + sistemaControlador.getExercicioCorrente().getAno());
        return parameters;
    }

    private String retornaDescricaoMes(String mes) {
        String toReturn = "";
        switch (mes) {
            case "01":
                toReturn = "JANEIRO";
                break;
            case "02":
                toReturn = "FEVEREIRO";
                break;
            case "03":
                toReturn = "MARÇO";
                break;
            case "04":
                toReturn = "ABRIL";
                break;
            case "05":
                toReturn = "MAIO";
                break;
            case "06":
                toReturn = "JUNHO";
                break;
            case "07":
                toReturn = "JULHO";
                break;
            case "08":
                toReturn = "AGOSTO";
                break;
            case "09":
                toReturn = "SETEMBRO";
                break;
            case "10":
                toReturn = "OUTUBRO";
                break;
            case "11":
                toReturn = "NOVEMBRO";
                break;
            case "12":
                toReturn = "DEZEMBRO";
                break;
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    private BigDecimal calculaLiquidacao(ItemDemonstrativo it, RelatoriosItemDemonst relatoriosItemDemonst) {
        return relatorioRGFAnexo01Calculator2017.calcularLiquidacao(it, sistemaControlador.getExercicioCorrente(), "01/01/" + sistemaControlador.getExercicioCorrente().getAno(), Util.getDiasMes(new Integer(mesFinal), sistemaControlador.getExercicioCorrente().getAno()) + "/" + mesFinal + "/" + sistemaControlador.getExercicioCorrente().getAno(), this.tipoAdministracao == null ? "" : " AND VW.TIPOADMINISTRACAO = '" + this.tipoAdministracao.name() + "'", this.esferas.name().equals("CONSOLIDADO") ? "" : " AND VW.esferadopoder = '" + this.esferas.name() + "'", relatoriosItemDemonst);
    }

    private BigDecimal calculaRestos(ItemDemonstrativo it, String dataInicial, String dataFinal) {
        return relatorioRGFAnexo01Calculator2017.calcularRestos(it, sistemaControlador.getExercicioCorrente(), dataInicial, dataFinal, tipoAdministracao == null ? "" : " AND VW.TIPOADMINISTRACAO = '" + this.tipoAdministracao.name() + "'", this.esferas.name().equals("CONSOLIDADO") ? "" : " AND VW.esferadopoder = '" + this.esferas.name() + "'", relatoriosItemDemonst);
    }

    public ItemDemonstrativoCalculator getItemDemonstrativoCalculator() {
        return itemDemonstrativoCalculator;
    }

    public void setItemDemonstrativoCalculator(ItemDemonstrativoCalculator itemDemonstrativoCalculator) {
        this.itemDemonstrativoCalculator = itemDemonstrativoCalculator;
    }

    public RelatorioRGFAnexo01Calculator2017 getRelatorioRGFAnexo01Calculator2017() {
        return relatorioRGFAnexo01Calculator2017;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    public BigDecimal getPercentualPrudencial() {
        return percentualPrudencial;
    }

    public void setPercentualPrudencial(BigDecimal percentualPrudencial) {
        this.percentualPrudencial = percentualPrudencial;
    }

    public BigDecimal getLimiteMaximo() {
        return limiteMaximo;
    }

    public void setLimiteMaximo(BigDecimal limiteMaximo) {
        this.limiteMaximo = limiteMaximo;
    }

    public BigDecimal getLimitePrudencial() {
        return limitePrudencial;
    }

    public void setLimitePrudencial(BigDecimal limitePrudencial) {
        this.limitePrudencial = limitePrudencial;
    }

    public BigDecimal getPercentualDespesa() {
        return percentualDespesa;
    }

    public void setPercentualDespesa(BigDecimal percentualDespesa) {
        this.percentualDespesa = percentualDespesa;
    }

    public BigDecimal getRcl() {
        return rcl;
    }

    public void setRcl(BigDecimal rcl) {
        this.rcl = rcl;
    }

    public BigDecimal getDespesaTotalComPessoal() {
        return despesaTotalComPessoal;
    }

    public void setDespesaTotalComPessoal(BigDecimal despesaTotalComPessoal) {
        this.despesaTotalComPessoal = despesaTotalComPessoal;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
        Integer mes = Integer.parseInt(this.mesFinal);
        if (mes == 4) {
            this.mesInicial = "01";
        } else {
            this.mesInicial = "0" + (mes - 3);
        }
    }

    public String getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(String mesInicial) {
        this.mesInicial = mesInicial;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public BigDecimal getPercentualLimite() {
        return percentualLimite;
    }

    public void setPercentualLimite(BigDecimal percentualLimite) {
        this.percentualLimite = percentualLimite;
    }

    public BigDecimal getLimiteAlerta() {
        return limiteAlerta;
    }

    public void setLimiteAlerta(BigDecimal limiteAlerta) {
        this.limiteAlerta = limiteAlerta;
    }

    public Esferas getEsferas() {
        return esferas;
    }

    public void setEsferas(Esferas esferas) {
        this.esferas = esferas;
    }

    public enum Esferas {

        CONSOLIDADO("Consolidado"),
        EXECUTIVO("Executivo"),
        LEGISLATIVO("Legislativo");
        private String descricao;

        private Esferas(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public void setRelatorioRGFAnexo01Calculator2017(RelatorioRGFAnexo01Calculator2017 relatorioRGFAnexo01Calculator2017) {
        this.relatorioRGFAnexo01Calculator2017 = relatorioRGFAnexo01Calculator2017;
    }

    public ItemDemonstrativoFacade getItemDemonstrativoFacade() {
        return itemDemonstrativoFacade;
    }

    public void setItemDemonstrativoFacade(ItemDemonstrativoFacade itemDemonstrativoFacade) {
        this.itemDemonstrativoFacade = itemDemonstrativoFacade;
    }
}
