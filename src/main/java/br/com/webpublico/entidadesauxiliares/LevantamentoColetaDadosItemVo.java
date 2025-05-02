package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.LevantamentoBemImovel;
import com.google.common.base.Strings;

import java.math.BigDecimal;

public class LevantamentoColetaDadosItemVo {

    private Long idBci;
    private String inscricaoCadastral;
    private BigDecimal areaTotal;
    private BigDecimal valorImovel;
    private String proprietario;
    private Long idProprietario;
    private String lote;
    private String bairro;
    private String cep;
    private String logradouro;
    private String numeroLogradouro;
    private LevantamentoBemImovel levantamentoBemImovel;

    public Long getIdBci() {
        return idBci;
    }

    public void setIdBci(Long idBci) {
        this.idBci = idBci;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Long getIdProprietario() {
        return idProprietario;
    }

    public void setIdProprietario(Long idProprietario) {
        this.idProprietario = idProprietario;
    }

    public String getNumeroLogradouro() {
        return numeroLogradouro;
    }

    public void setNumeroLogradouro(String numeroLogradouro) {
        this.numeroLogradouro = numeroLogradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public BigDecimal getValorImovel() {
        return valorImovel;
    }

    public void setValorImovel(BigDecimal valorImovel) {
        this.valorImovel = valorImovel;
    }

    public LevantamentoBemImovel getLevantamentoBemImovel() {
        return levantamentoBemImovel;
    }

    public void setLevantamentoBemImovel(LevantamentoBemImovel levantamentoBemImovel) {
        this.levantamentoBemImovel = levantamentoBemImovel;
    }

    public enum CampoImportacao {
        UNIDADE_ADM("Unidade Administrativa"),
        UNIDADE_ORC("Unidade Orçamentária"),
        GRUPO_PATRIMONIAL("Grupo Patrimonial"),
        REGISTRO_PATRIMONIAL("Registro Patrimonial"),
        LOCALIZACAO("Localização"),
        DESCRICAO_IMOVEL("Descrição o Imóvel"),
        CONDICAO_OCUPACAO("Condição de Ocupação"),
        ESTADO_CONSERVACAO("Estado de Conservação"),
        SITUACAO_CONSERVACAO("Situação de Conservação"),
        DATA_AQUISICAO("Data da Aquisição"),
        VALOR_ATUAL_BEM("Valor Atual do Bem"),
        TIPO_AQUISICAO("Tipo de Aquisição");
        private String descricao;

        CampoImportacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder("");
        if (!Strings.isNullOrEmpty(logradouro)) {
            sb.append(logradouro).append(", ");
        }
        if (!Strings.isNullOrEmpty(numeroLogradouro)) {
            sb.append(numeroLogradouro).append(", ");
        }
        if (!Strings.isNullOrEmpty(bairro)) {
            sb.append(bairro).append(", ");
        }
        if (!Strings.isNullOrEmpty(cep)) {
            sb.append(cep).append(", ");
        }
        if (sb.toString().endsWith(", ")) {
            sb.replace(sb.toString().length() - 2, sb.toString().length(), "");
        }
        return sb.toString();
    }
}
