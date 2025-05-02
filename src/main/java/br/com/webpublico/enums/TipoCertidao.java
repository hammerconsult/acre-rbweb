package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: FABIO
 * Date: 08/09/15
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public enum TipoCertidao {
    POSITIVA("Certidão Positiva", TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA),
    POSITIVA_EFEITO_NEGATIVA("Certidão Positiva com efeito Negativo", TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA_EFEITONEGATIVA),
    NEGATIVA("Certidão Negativa", TipoValidacaoDoctoOficial.CERTIDAONEGATIVA),
    POSITIVA_BENS_IMOVEIS("Certidão Positiva de Bens Imóveis", TipoValidacaoDoctoOficial.CERTIDAO_POSITIVA_BENS_IMOVEIS),
    NEGATIVA_BENS_IMOVEIS("Certidão Negativa de Bens Imóveis", TipoValidacaoDoctoOficial.CERTIDAO_NEGATIVA_BENS_IMOVEIS),
    CADASTRO_IMOBILIARIO("Certidão de Cadastro Imobiliário", TipoValidacaoDoctoOficial.SEMVALIDACAO),
    BAIXA_ATIVIDADE("Certidão de Baixa de Atividade", TipoValidacaoDoctoOficial.SEMVALIDACAO);
    private String descricao;
    private TipoValidacaoDoctoOficial tipoValidacao;

    private TipoCertidao(String descricao, TipoValidacaoDoctoOficial tipoValidacao) {
        this.descricao = descricao;
        this.tipoValidacao = tipoValidacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoValidacaoDoctoOficial getTipoValidacao() {
        return tipoValidacao;
    }
}
