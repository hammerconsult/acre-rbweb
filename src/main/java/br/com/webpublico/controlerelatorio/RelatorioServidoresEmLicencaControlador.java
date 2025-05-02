/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.TipoAfastamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.TipoAfastamentoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "Relatorio-Servidores-em-Licenca", pattern = "/relatorio/Servidores-em-Licenca/", viewId = "/faces/rh/relatorios/relatorioservidoresemlicenca.xhtml")
})
public class RelatorioServidoresEmLicencaControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    private PessoaFisica servidor;
    private Converter converterServidor;
    private Date dtInicial;
    private Date dtFinal;
    private String filtro;
    private TipoAfastamento tipoAfastamento;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioServidoresEmLicencaControlador.class);

    @URLAction(mappingId = "Relatorio-Servidores-em-Licenca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.servidor = null;
        this.dtInicial = DataUtil.montaData(1, 0, sistemaControlador.getExercicioCorrente().getAno()).getTime();
        this.dtFinal = new Date();
        this.tipoAfastamento = null;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-de-servidores-em-licenca/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("CONCATENASQL", gerarSql());
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO SERVIDORES EM LICENÇA");
        dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO SERVIDORES EM LICENÇA");
        dto.adicionarParametro("FILTROS", filtro);
        dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);

        return dto;
    }

    private String gerarSql() {
        filtro = "";
        StringBuilder stb = new StringBuilder();
        String juncao = " AND ";

        if (this.servidor != null) {
            stb.append(juncao).append(" pf.NOME = \'").append(this.servidor.getNome()).append("\'");
            filtro += "Servidor: " + servidor.getNome() + "; ";
        }

        if (this.tipoAfastamento != null) {
            stb.append(juncao).append(" TIPO.CODIGO = \'").append(this.tipoAfastamento.getCodigo()).append("\'");
            filtro += "Tipo Afastamento: " + tipoAfastamento.getDescricao() + "; ";
        }
        stb.append(juncao).append(" AFAST.INICIO BETWEEN to_date('").append(formataData(dtInicial)).append("', 'DD/MM/YYYY') ").append(juncao).append(" TO_DATE('").append(formataData(dtFinal)).append("', 'DD/MM/YYYY') ");
        stb.append(juncao).append(" AFAST.TERMINO BETWEEN to_date('").append(formataData(dtInicial)).append("', 'DD/MM/YYYY') ").append(juncao).append(" TO_DATE('").append(formataData(dtFinal)).append("', 'DD/MM/YYYY') ");
        filtro += "Período: " + formataData(dtInicial) + " à " + formataData(dtFinal) + "; ";

        filtro = filtro.substring(0, filtro.length() - 1);
        return stb.toString();
    }

    private String formataData(Date data) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        return formato.format(data);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dtInicial == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida( "O campo Data Inicial deve ser informado.");
        }
        if (dtFinal == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida( "O campo Data Final deve ser informado.");
        }
        if (dtFinal.before(dtInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
    }

    public Converter getConverterServidor() {
        if (converterServidor == null) {
            converterServidor = new ConverterAutoComplete(PessoaFisica.class, pessoaFacade);
        }
        return converterServidor;
    }

    public List<SelectItem> getTiposDeAfastamento() {
        return Util.getListSelectItem(tipoAfastamentoFacade.lista(), false);
    }

    public List<PessoaFisica> completaServidor(String parte) {
        return contratoFPFacade.listaPessoasComContratos(parte.trim());
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public PessoaFisica getServidor() {
        return servidor;
    }

    public void setServidor(PessoaFisica servidor) {
        this.servidor = servidor;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public TipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(TipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }
}
