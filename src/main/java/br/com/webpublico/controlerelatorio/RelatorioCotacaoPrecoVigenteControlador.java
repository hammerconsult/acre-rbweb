package br.com.webpublico.controlerelatorio;


import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.enums.TipoSolicitacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by israeleriston on 12/05/15.
 */
@ManagedBean(name = "relatorioCotacaoPrecoVigenteControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioCotacaoPrecoVigente", pattern = "/licitacao/relatorio-cotacao-preco-vigente/",
                viewId = "/faces/administrativo/relatorios/relatoriocotacaoprecovigente.xhtml")
})
public class RelatorioCotacaoPrecoVigenteControlador extends AbstractReport implements Serializable {


    private String filtros;
    @Enumerated(EnumType.STRING)
    private TipoSolicitacao tipoSolicitacao;
    private Long numero;
    private String descricao;
    private Date validade;


    public static final String ARQUIVO_JASPER = "RelatorioCotacaoPrecoVigente.jasper";

    public RelatorioCotacaoPrecoVigenteControlador() {
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public TipoSolicitacao getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(TipoSolicitacao tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public String isCriterio() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";
        if (numero != null) {
            sb.append(juncao).append("co.numero = ").append(" " + numero + " ");
            filtros += " Nº Cotação: " + numero + " ";
        }
        if (tipoSolicitacao != null) {
            if (TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA.equals(this.tipoSolicitacao)) {
                sb.append(juncao).append(" fc.tiposolicitacao = ").append(" 'OBRA_SERVICO_DE_ENGENHARIA' ").append(" ");
            }
            if (TipoSolicitacao.COMPRA_SERVICO.equals(this.tipoSolicitacao)) {
                sb.append(juncao).append(" fc.tiposolicitacao = ").append(" 'COMPRA_SERVICO' ").append(" ");
            }
            filtros += " Tipo Solicitacao: " + this.tipoSolicitacao.getDescricao() + " ";
        }

        if (validade != null) {
            sb.append(juncao).append(" co.validadecotacao <= to_date( '" + DataUtil.getDataFormatada(validade) + "','dd/MM/yyyy') ");
            filtros += " Validade: " + DataUtil.getDataFormatada(validade) + " ";
        }

        return sb.toString();
    }

    public void gerarRelatorio() throws JRException, IOException {

        HashMap parameter = new HashMap();

        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");

        parameter.put("BRASAO", super.getCaminhoImagem());
        parameter.put("SECRETARIA", sistemaControlador.getUnidadeOrganizacionalAdministrativaCorrente().getDescricao());
        parameter.put("NOMERELATORIO", "Relatorio Cotação de Preço Vigente");
        parameter.put("MODULO", "Administrativo");
        parameter.put("USUARIO", sistemaControlador.getUsuarioCorrente().toString());
        parameter.put("FILTROS", filtros);
        parameter.put("WHERE", isCriterio());

        gerarRelatorio(ARQUIVO_JASPER, parameter);


    }

    @URLAction(mappingId = "relatorioCotacaoPrecoVigente", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        tipoSolicitacao = null;
        numero = null;
        validade = null;
        descricao = null;

    }
}

