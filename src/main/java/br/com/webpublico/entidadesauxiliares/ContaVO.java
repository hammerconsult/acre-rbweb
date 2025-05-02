package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.*;
import org.apache.commons.lang.StringUtils;

public class ContaVO {
    private Boolean contaContabil;

    private String codigo;
    private String descricao;
    private String naturezaSaldo;
    private String naturezaConta;
    private String categoriaConta;
    private String naturezaInformacao;
    private String subSistema;
    private String codigoSiconfi;
    private String codigoTCE;

    private br.com.webpublico.entidades.Conta contaRecuperada;
    private br.com.webpublico.entidades.Conta contaEquivalente;

    public ContaVO() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public br.com.webpublico.entidades.Conta getContaRecuperada() {
        return contaRecuperada;
    }

    public void setContaRecuperada(br.com.webpublico.entidades.Conta contaRecuperada) {
        this.contaRecuperada = contaRecuperada;
    }

    public br.com.webpublico.entidades.Conta getContaEquivalente() {
        return contaEquivalente;
    }

    public void setContaEquivalente(br.com.webpublico.entidades.Conta contaEquivalente) {
        this.contaEquivalente = contaEquivalente;
    }

    public String getNaturezaSaldo() {
        return naturezaSaldo;
    }

    public void setNaturezaSaldo(String naturezaSaldo) {
        this.naturezaSaldo = naturezaSaldo;
    }


    public String getNaturezaInformacao() {
        return naturezaInformacao;
    }

    public void setNaturezaInformacao(String naturezaInformacao) {
        this.naturezaInformacao = naturezaInformacao;
    }

    public String getSubSistema() {
        return subSistema;
    }

    public void setSubSistema(String subSistema) {
        this.subSistema = subSistema;
    }

    public void setCategoriaConta(String categoriaConta) {
        this.categoriaConta = categoriaConta;
    }

    public NaturezaSaldo getNaturezaDoSaldo() {
        if (naturezaSaldo != null) {
            if ("DEVEDORA".equals(naturezaSaldo.toUpperCase())) {
                return NaturezaSaldo.DEVEDOR;
            }
            if ("CREDORA".equals(naturezaSaldo.toUpperCase())) {
                return NaturezaSaldo.CREDOR;
            }
            if ("CREDORA/DEVEDORA".equals(naturezaSaldo.toUpperCase())) {
                return NaturezaSaldo.QUALQUER;
            }
            return NaturezaSaldo.valueOf(naturezaSaldo.toUpperCase());
        }
        return null;
    }

    public CategoriaConta getCategoriaConta() {
        if (categoriaConta != null) {
            if ("SUPERIOR".equals(categoriaConta.toUpperCase())) {
                return CategoriaConta.ANALITICA;
            }
            if ("ÚLTIMO".equals(categoriaConta.toUpperCase())) {
                return CategoriaConta.SINTETICA;
            }
            return CategoriaConta.valueOf(categoriaConta.toUpperCase());
        }
        return null;
    }

    public SubSistema getSubSistemaEnum() {
        if (subSistema != null) {
            if ("FINANCEIRO".equals(subSistema.toUpperCase())) {
                return SubSistema.FINANCEIRO;
            }
            if ("PERMANENTE".equals(subSistema.toUpperCase())) {
                return SubSistema.PERMANENTE;
            }
            if ("COMPENSADO".equals(subSistema.toUpperCase())) {
                return SubSistema.COMPENSADO;
            }
            if ("ORCAMENTARIO".equals(subSistema.toUpperCase())) {
                return SubSistema.ORCAMENTARIO;
            }
        }
        return null;
    }

    public String getNaturezaConta() {
        return naturezaConta;
    }

    public void setNaturezaConta(String naturezaConta) {
        this.naturezaConta = naturezaConta;
    }

    public NaturezaInformacao getNaturezaInformacaoEnum() {
        if (naturezaInformacao != null) {
            switch (naturezaInformacao.toUpperCase()) {
                case "PATRIMONIAL":
                    return NaturezaInformacao.PATRIMONIAL;
                case "CONTROLE":
                    return NaturezaInformacao.CONTROLE;
                case "ORCAMENTARIO":
                    return NaturezaInformacao.ORCAMENTARIO;
            }
            if (StringUtils.isNotEmpty(naturezaInformacao)) {
                return NaturezaInformacao.valueOf(naturezaInformacao);
            }
        }
        return null;
    }

    public NaturezaConta getNaturezaContaEnum() {
        if (naturezaConta != null) {
            switch (naturezaConta.toUpperCase()) {
                case "ATIVO":
                    return NaturezaConta.ATIVO;
                case "PASSIVO":
                    return NaturezaConta.PASSIVO;
                case "PATRIMÔNIO LÍQUIDO":
                    return NaturezaConta.PATRIMONIO_PUBLICO;
                case "VARIAÇÃO PATRIMONIAL DIMINUTIVA":
                    return NaturezaConta.VARIACAO_PATRIMONIAL_DIMINUTIVA;
                case "VARIAÇÃO PATRIMONIAL AUMENTATIVA":
                    return NaturezaConta.VARIACAO_PATRIMONIAL_AUMENTATIVA;
                case "CONTROLES DA APROVAÇÃO DO PLANEJAMENTO E ORÇAMENTO":
                    return NaturezaConta.CONTROLES_APROVACAO_PLANEJAMENTO_ORCAMENTO;
                case "CONTROLES DA EXECUÇÃO DO PLANEJAMENTO E ORÇAMENTO":
                    return NaturezaConta.CONTROLES_EXECUCAO_PLANEJAMENTO_ORCAMENTO;
                case "CONTROLES DEVEDORES":
                    return NaturezaConta.CONTROLES_DEVEDORES;
                case "CONTROLES CREDORES":
                    return NaturezaConta.CONTROLES_CREDORES;
                case "DEMAIS CONTROLES":
                    return NaturezaConta.DEMAIS_CONTROLES;
            }
            if (StringUtils.isNotEmpty(naturezaConta)) {
                return NaturezaConta.valueOf(naturezaConta);
            }
        }
        return null;
    }

    public Boolean getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(Boolean contaContabil) {
        this.contaContabil = contaContabil;
    }

    public String getCodigoSiconfi() {
        return codigoSiconfi;
    }

    public void setCodigoSiconfi(String codigoSiconfi) {
        this.codigoSiconfi = codigoSiconfi;
    }

    public String getCodigoTCE() {
        return codigoTCE;
    }

    public void setCodigoTCE(String codigoTCE) {
        this.codigoTCE = codigoTCE;
    }
}
