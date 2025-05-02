/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.ServidorInativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leonardo
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRelatorioServidoresInativos", pattern = "/relatorio-servidores-inativos/novo/", viewId = "/faces/rh/relatorios/relatorioservidoresinativos.xhtml")
})
@ManagedBean
public class RelatorioServidoresInativos implements Serializable {

    private ConverterGenerico converterUnidade;
    private Integer mes;
    private Integer ano;
    private ServidorInativo servidorInativo;
    private TipoOrdenacao tipoOrdenacao;
    private UnidadeOrganizacional unidadeOrganizacional;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public RelatorioServidoresInativos() {
    }

    public void gerarRelatorio() {
        try {
            validaCampos();
            RelatorioDTO dto = new RelatorioDTO();
            montarDatas();
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("RELATÓRIO-SERVIDORES-INATIVOS");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÂO");
            dto.adicionarParametro("NOMERELATORIO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("MES", mes);
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("TIPOSERVIDOR", servidorInativo.name());
            dto.adicionarParametro("UNIDADEQUERY", unidadeOrganizacional != null ? " and un.id = " + unidadeOrganizacional.getId() : " ");
            montaCondicoes(dto);
            dto.setApi("rh/servidores-inativos/");
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

    private void montarDatas() {
        DateTime dateTime = new DateTime(new Date());
        DateTime dateMes = new DateTime(sistemaFacade.getDataOperacao());
        dateTime = dateTime.withMonthOfYear(mes);
        dateTime = dateTime.withYear(ano);
        if (dateMes.getMonthOfYear() == dateTime.getMonthOfYear()) {
            dateTime.withDayOfMonth(dateMes.getDayOfMonth());
        } else {
            dateTime = dateTime.withDayOfMonth(dateTime.dayOfMonth().getMaximumValue());
        }
    }

    private void validaCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O mês é campo obrigatório.");
        }
        if (ano == null || (ano != null && ano == 0)) {
            ve.adicionarMensagemDeCampoObrigatorio("O ano é campo obrigatório.");
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um mês entre 01 (Janeiro) e 12 (Dezembro).");
        }
        ve.lancarException();
    }

    private void montaCondicoes(RelatorioDTO dto) {
        dto.adicionarParametro("DATAADMISSAO", ", v.iniciovigencia as DATAADMISSAO ");
        if (servidorInativo.equals(ServidorInativo.APOSENTADO)) {
            dto.adicionarParametro("CARGO", ", cargo.descricao as cargo");
            dto.adicionarParametro("INNERJOIN", " join aposentadoria apo on apo.id = v.id " +
                "     join contratofp contratoantigo on contratoantigo.id = apo.contratofp_id " +
                "     join vinculofp vinculoantigo on vinculoantigo.id = contratoantigo.id " +
                "     left join cargo cargo on cargo.id = contratoantigo.cargo_id ");
        } else if (servidorInativo.equals(ServidorInativo.PENSIONISTA)) {
            dto.adicionarParametro("INNERJOIN", " join pensionista p on p.id = v.id ");
        } else {
            dto.adicionarParametro("INNERJOIN", " left join pensionista p on p.id = v.id left join aposentadoria apo on apo.id = v.id  ");
            dto.adicionarParametro("WHERETODOS", " and (apo.id = v.id or p.id = v.id) ");
        }
        defineOrdenacao(dto);
    }

    private void defineOrdenacao(RelatorioDTO dto) {
        if (tipoOrdenacao.equals(TipoOrdenacao.ALFABETICA)) {
            dto.adicionarParametro("ORDENACAO", " order by  un.descricao, pf.nome");
            dto.adicionarParametro("TIPOORDENACAO", "ORDEM ALFABÉTICA");
        } else if (tipoOrdenacao.equals(TipoOrdenacao.MATRICULA)) {
            dto.adicionarParametro("ORDENACAO", " order by un.descricao, to_number(m.matricula), v.numero");
            dto.adicionarParametro("TIPOORDENACAO", "ORDEM MATRÍCULA");
        } else if (tipoOrdenacao.equals(TipoOrdenacao.DATA_CONCESSAO)) {
            dto.adicionarParametro("ORDENACAO", " order by un.descricao, v.inicioVigencia asc");
            dto.adicionarParametro("TIPOORDENACAO", "Data Concessão");
        } else if (tipoOrdenacao.equals(TipoOrdenacao.DATA_NASCIMENTO)) {
            dto.adicionarParametro("ORDENACAO", " order by un.descricao, pf.dataNascimento");
            dto.adicionarParametro("TIPOORDENACAO", "Data De Nascimento");
        } else if (tipoOrdenacao.equals(TipoOrdenacao.FINAL_VIGENCIA)) {
            dto.adicionarParametro("ORDENACAO", " order by un.descricao, v.finalVigencia");
            dto.adicionarParametro("TIPOORDENACAO", "Data De Nascimento");
        }
    }

    public List<SelectItem> getUnidadesPensAndApos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (UnidadeOrganizacional un : unidadeOrganizacionalFacade.unidadesOrganizacionaisPensionista()) {
            toReturn.add(new SelectItem(un, un.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterUnidade() {
        if (converterUnidade == null) {
            converterUnidade = new ConverterGenerico(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidade;
    }

    @URLAction(mappingId = "novoRelatorioServidoresInativos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = null;
        ano = null;
        servidorInativo = ServidorInativo.TODOS;
    }


    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public List<SelectItem> getServidoresInativos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (ServidorInativo object : ServidorInativo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposOrdenacaos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoOrdenacao object : TipoOrdenacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public void setServidorInativo(ServidorInativo servidorInativo) {
        this.servidorInativo = servidorInativo;
    }

    public ServidorInativo getServidorInativo() {
        return servidorInativo;
    }

    public void setTipoOrdenacao(TipoOrdenacao tipoOrdenacao) {
        this.tipoOrdenacao = tipoOrdenacao;
    }

    public TipoOrdenacao getTipoOrdenacao() {
        return tipoOrdenacao;
    }

    public enum TipoOrdenacao {

        ALFABETICA("Alfabética"),
        MATRICULA("Matrícula"),
        DATA_NASCIMENTO("Data de Nascimento"),
        DATA_CONCESSAO("Data da Concessão"),
        FINAL_VIGENCIA("Final da Vigência");

        private String descricao;

        private TipoOrdenacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public void fixaUnidade() {
        if (servidorInativo != null && servidorInativo.equals(ServidorInativo.TODOS)) {
            unidadeOrganizacional = null;
        }
    }

    public boolean isServidorInativoTipoTodos() {
        return ServidorInativo.TODOS.equals(servidorInativo);
    }

}
