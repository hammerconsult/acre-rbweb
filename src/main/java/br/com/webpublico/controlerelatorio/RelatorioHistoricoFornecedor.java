/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Sarruf
 */
@ViewScoped
@ManagedBean

@URLMappings(mappings = {
        @URLMapping(id = "novo-relatorio-historico-fornecedor", pattern = "/licitacao/relatorio-historico-fornecedor/", viewId = "/faces/administrativo/relatorios/relatoriohistoricofornecedor.xhtml")
})
public class RelatorioHistoricoFornecedor extends AbstractReport implements Serializable {

    @Limpavel(Limpavel.NULO)
    private ModalidadeLicitacao modalidadeLicitacao;
    @Limpavel(Limpavel.NULO)
    private TipoApuracaoLicitacao tipoApuracaoLicitacao;
    @Limpavel(Limpavel.NULO)
    private Pessoa pessoa;
    @Limpavel(Limpavel.NULO)
    private String resumoDoObjeto;
    private String filtros;

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public TipoApuracaoLicitacao getTipoApuracaoLicitacao() {
        return tipoApuracaoLicitacao;
    }

    public void setTipoApuracaoLicitacao(TipoApuracaoLicitacao tipoApuracaoLicitacao) {
        this.tipoApuracaoLicitacao = tipoApuracaoLicitacao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getResumoDoObjeto() {
        return resumoDoObjeto;
    }

    public void setResumoDoObjeto(String resumoDoObjeto) {
        this.resumoDoObjeto = resumoDoObjeto;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtros = "";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("CONDICAO", gerarCondicao());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/historico-fornecedor/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "Relatório de Histórico do Fornecedor";
    }

    @URLAction(mappingId = "novo-relatorio-historico-fornecedor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        Util.limparCampos(this);
    }

    private String gerarCondicao() {
        StringBuilder sb = new StringBuilder();
        String complemento = " where ";

        if (pessoa != null) {
            sb.append(complemento).append(" pessoa_id = ").append(pessoa.getId());
            filtros += "Fornecedor: " + getNomeRazaoPessoaInformada() + " ";
            complemento = " and ";
        }
        if (modalidadeLicitacao != null) {
            sb.append(complemento).append(" modalidade = '").append(modalidadeLicitacao.name()).append("'");
            filtros += "Modalidade: " + modalidadeLicitacao.getDescricao() + " ";
            complemento = " and ";
        }
        if (tipoApuracaoLicitacao != null) {
            sb.append(complemento).append(" tipo_apuracao = '").append(tipoApuracaoLicitacao.name()).append("'");
            filtros += "Tipo Apuração: " + tipoApuracaoLicitacao.getDescricao() + " ";
            complemento = " and ";
        }
        if (resumoDoObjeto != null && !resumoDoObjeto.isEmpty()) {
            sb.append(complemento).append(" lower(objeto) like lower('%").append(resumoDoObjeto).append("%') ");
            filtros += "Resumo do Objeto: " + resumoDoObjeto + " ";
        }
        return sb.toString();
    }

    private String getNomeRazaoPessoaInformada() {
        if (pessoa instanceof PessoaFisica) {
            return pessoa.getNome();
        }
        if (pessoa instanceof PessoaJuridica) {
            return ((PessoaJuridica) pessoa).getRazaoSocial();
        }
        return "";
    }

    public List<SelectItem> getTiposApuracao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoApuracaoLicitacao apuracaoLicitacao : TipoApuracaoLicitacao.values()) {
            retorno.add(new SelectItem(apuracaoLicitacao, apuracaoLicitacao.getDescricao()));
        }
        return retorno;
    }
}
