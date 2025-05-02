package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.BancoContaConfTributario;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "relatorioAcompanhamentoArrecadacao")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
            id = "novoRelatorioAcompanhamentoArrecadacao",
            pattern = "/tributario/arrecadacao/relatorioacompanhamentoarrecadacao/",
            viewId = "/faces/tributario/arrecadacao/relatorios/relatorioacompanhamentoarrecadacao.xhtml"),
})
public class RelatorioAcompanhamentoArrecadao implements Serializable {

    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private LoteBaixaFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    private transient Converter converterConta;
    private BancoContaConfTributario contaBancariaSelecionada;
    private Filtros filtros;
    private StringBuilder where;
    private StringBuilder filtro;

    public RelatorioAcompanhamentoArrecadao() {
    }

    @URLAction(mappingId = "novoRelatorioAcompanhamentoArrecadacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        filtros = new Filtros();
        contaBancariaSelecionada = null;
        where = new StringBuilder();
        filtro = new StringBuilder();
    }

    public Filtros getFiltros() {
        return filtros;
    }

    public void setFiltros(Filtros filtros) {
        this.filtros = filtros;
    }

    public List<SelectItem> getOpcoesPorDataDe() {
        return Util.getListSelectItem(Filtros.PorDataDe.values());
    }

    public List<SelectItem> getOpcoesAgrupamento() {
        return Util.getListSelectItem(Filtros.AgruparPor.values());
    }

    public List<SelectItem> getContas() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        List<BancoContaConfTributario> lista = facade.recuperaContasConfiguracao();
        Collections.sort(lista, new Comparator<BancoContaConfTributario>() {

            @Override
            public int compare(BancoContaConfTributario o1, BancoContaConfTributario o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        for (BancoContaConfTributario object : lista) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public BancoContaConfTributario getContaBancariaSelecionada() {
        return contaBancariaSelecionada;
    }

    public void setContaBancariaSelecionada(BancoContaConfTributario contaBancariaSelecionada) {
        this.contaBancariaSelecionada = contaBancariaSelecionada;
    }

    public StringBuilder getWhere() {
        return where;
    }

    public void setWhere(StringBuilder where) {
        this.where = where;
    }

    public StringBuilder getFiltro() {
        return filtro;
    }

    public void setFiltro(StringBuilder filtro) {
        this.filtro = filtro;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void adicionarContaBancaria() {
        try {
            validarAdicionarConta();
            filtros.getContasBancarias().add(contaBancariaSelecionada);
            contaBancariaSelecionada = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarConta() {
        ValidacaoException ve = new ValidacaoException();
        if (contaBancariaSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Banco e Conta Bancária deve ser informado.");
        }
        ve.lancarException();
    }

    public void removerContaBancaria(BancoContaConfTributario contaBancaria) {
        filtros.getContasBancarias().remove(contaBancaria);
    }

    public Converter getConverterConta() {
        if (converterConta == null) {
            converterConta = new Converter() {

                @Override
                public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
                    if (string == null || string.isEmpty()) {
                        return null;
                    }
                    return facade.recuperar(BancoContaConfTributario.class, Long.parseLong(string));
                }

                @Override
                public String getAsString(FacesContext fc, UIComponent uic, Object o) {
                    if (o == null) {
                        return null;
                    } else {
                        return String.valueOf(((BancoContaConfTributario) o).getId());
                    }
                }
            };
        }
        return converterConta;
    }

    public List<Tributo> getTributos() {
        return tributoFacade.listaDecrescente();
    }

    public String retornaNomeCampoData() {
        return filtros.getPorDataDe().equals(Filtros.PorDataDe.DATAARRECADACAO) ? "lb.dataPagamento" : "lb.dataFinanciamento";
    }

    public String retornaDescricaoCampoData() {
        return filtros.getPorDataDe().equals(Filtros.PorDataDe.DATAARRECADACAO) ? "Data de Pagamento" : "Data de Movimento";
    }

    public void montaFiltro() {
        where = new StringBuilder();
        filtro = new StringBuilder();
        String juncao = " where ";
        String juncaoIn = "";
        String campoData = retornaNomeCampoData();
        String descricaoCampoData = retornaDescricaoCampoData();
        if (filtros.getDataInicial() != null) {
            where.append(juncao);
            where.append(" ").append(campoData).append(" >= '").append(DataUtil.getDataFormatada(filtros.getDataInicial())).append("' ");
            juncao = " and ";

            filtro.append(descricaoCampoData).append(" Inicial: ").append(DataUtil.getDataFormatada(filtros.getDataInicial())).append("; ");
        }
        if (filtros.getDataFinal() != null) {
            where.append(juncao);
            where.append(" ").append(campoData).append(" <= '").append(DataUtil.getDataFormatada(filtros.getDataFinal())).append("' ");
            juncao = " and ";

            filtro.append(descricaoCampoData).append(" Final: ").append(DataUtil.getDataFormatada(filtros.getDataFinal())).append("; ");
        }

        if(filtros.getTributosSelecionados() != null && filtros.getTributosSelecionados().length > 0){
            where.append(juncao);
            where.append(" t.id in (");
            juncaoIn = "";
            filtro.append("Tributos: ");
            for(Tributo tributo : filtros.getTributosSelecionados()){
                where.append(juncaoIn);
                where.append(tributo.getId());
                juncaoIn = ",";
                filtro.append(tributo.getDescricao()).append(";");
            }
            where.append(")");
            juncao = " and ";
        }

        if( filtros.getContasBancarias() != null && filtros.getContasBancarias().size() > 0) {
            where.append(juncao);
            where.append(" sc.id in (");
            juncaoIn = "";
            for(BancoContaConfTributario conta : filtros.getContasBancarias()){
                where.append(juncaoIn);
                where.append(conta.getSubConta().getId());
                juncaoIn = ",";
                filtro.append(conta).append(";");
            }
            where.append(")");
        }
    }

    private void validaInformacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (filtros.getPorDataDe() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Por Data de deve ser informado.");
        }
        if (filtros.getAgruparPor() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Agrupar por deve ser informado.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validaInformacoes();
            RelatorioDTO dto = new RelatorioDTO();
            montaFiltro();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("condicao", where.toString());
            dto.adicionarParametro("FILTROS", filtro.toString());
            if (Filtros.AgruparPor.BANCOCONTABANCARIADATA.equals(filtros.getAgruparPor())) {
                dto.adicionarParametro("isDataArrecadacao", Filtros.PorDataDe.DATAARRECADACAO.equals(filtros.getPorDataDe()));
            }
            dto.setNomeRelatorio("Relatório de Acompanhamento da Arrecadação");
            dto.setApi("tributario/acompanhamento-arrecadacao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public static class Filtros {
        private Date dataInicial;
        private Date dataFinal;
        private List<BancoContaConfTributario> contasBancarias;
        private Tributo tributosSelecionados[];
        private PorDataDe porDataDe;
        private AgruparPor agruparPor;

        public Filtros() {
            contasBancarias = new ArrayList();
        }

        public Date getDataInicial() {
            return dataInicial;
        }

        public void setDataInicial(Date dataInicial) {
            this.dataInicial = dataInicial;
        }

        public Date getDataFinal() {
            return dataFinal;
        }

        public void setDataFinal(Date dataFinal) {
            this.dataFinal = dataFinal;
        }

        public List<BancoContaConfTributario> getContasBancarias() {
            return contasBancarias;
        }

        public void setContasBancarias(List<BancoContaConfTributario> contasBancarias) {
            this.contasBancarias = contasBancarias;
        }

        public Tributo[] getTributosSelecionados() {
            return tributosSelecionados;
        }

        public void setTributosSelecionados(Tributo[] tributosSelecionados) {
            this.tributosSelecionados = tributosSelecionados;
        }

        public PorDataDe getPorDataDe() {
            return porDataDe;
        }

        public void setPorDataDe(PorDataDe porDataDe) {
            this.porDataDe = porDataDe;
        }

        public AgruparPor getAgruparPor() {
            return agruparPor;
        }

        public void setAgruparPor(AgruparPor agruparPor) {
            this.agruparPor = agruparPor;
        }

        public enum PorDataDe implements EnumComDescricao {
            DATAARRECADACAO("Data de Pagamento"),
            DATAMOVIMENTO("Data de Movimento");
            private String descricao;

            PorDataDe(String descricao) {
                this.descricao = descricao;
            }

            @Override
            public String getDescricao() {
                return descricao;
            }

            @Override
            public String toString() {
                return descricao;
            }
        }

        public enum AgruparPor implements EnumComDescricao {
            BANCOCONTABANCARIA("Banco e Conta Bancária"),
            BANCOCONTABANCARIADATA("Banco, Conta Bancária e Data");
            private String descricao;

            AgruparPor(String descricao) {
                this.descricao = descricao;
            }

            @Override
            public String getDescricao() {
                return descricao;
            }

            @Override
            public String toString() {
                return descricao;
            }
        }
    }

}
