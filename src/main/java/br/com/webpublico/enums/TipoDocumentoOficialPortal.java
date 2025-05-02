package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoDocumentoOficialPortal implements EnumComDescricao {
    CNBI_PF("Certidão Negativa de Bem Imóvel - Pessoa Física - CNBI-PF", TipoValidacaoDoctoOficial.CERTIDAO_NEGATIVA_BENS_IMOVEIS, TipoCadastroDoctoOficial.PESSOAFISICA),
    CNBI_PJ("Certidão Negativa de Bem Imóvel - Pessoa Jurídica - CNBI-PJ", TipoValidacaoDoctoOficial.CERTIDAO_NEGATIVA_BENS_IMOVEIS, TipoCadastroDoctoOficial.PESSOAJURIDICA),
    CNDI("Certidão Negativa de Débitos - Imobiliário - CNDI", TipoValidacaoDoctoOficial.CERTIDAONEGATIVA, TipoCadastroDoctoOficial.CADASTROIMOBILIARIO),
    CND_PF("Certidão Negativa de Débitos - Pessoa Física - CND-PF", TipoValidacaoDoctoOficial.CERTIDAONEGATIVA, TipoCadastroDoctoOficial.PESSOAFISICA),
    CND_PJ("Certidão Negativa de Débitos - Pessoa Jurídica - CND-PJ", TipoValidacaoDoctoOficial.CERTIDAONEGATIVA, TipoCadastroDoctoOficial.PESSOAJURIDICA),
    CPD("Certidão Positiva de Débitos - CPD", TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA, TipoCadastroDoctoOficial.NENHUM),
    CCI("Certidão de Cadastro Imobiliário - CCI", TipoValidacaoDoctoOficial.SEMVALIDACAO, TipoCadastroDoctoOficial.CADASTROIMOBILIARIO),
    CPBI("Certidão Positiva de Bem Imóvel - CPBI", TipoValidacaoDoctoOficial.CERTIDAO_POSITIVA_BENS_IMOVEIS, TipoCadastroDoctoOficial.NENHUM),
    CPND_PF("Certidão Positiva de Débitos com Efeito de Negativa - Pessoa Física - CPND-PF", TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA_EFEITONEGATIVA, TipoCadastroDoctoOficial.PESSOAFISICA),
    CPND_PJ("Certidão Positiva de Débitos com Efeito de Negativa - Pessoa Jurídica - CPND-PJ", TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA_EFEITONEGATIVA, TipoCadastroDoctoOficial.PESSOAJURIDICA),
    CPND_I("Certidão Positiva de Débitos com Efeito de Negativa - Imobiliário - CPND-I", TipoValidacaoDoctoOficial.CERTIDAOPOSITIVA_EFEITONEGATIVA, TipoCadastroDoctoOficial.CADASTROIMOBILIARIO);

    private final String descricao;
    private final TipoValidacaoDoctoOficial tipoValidacaoDoctoOficial;
    private final TipoCadastroDoctoOficial tipoCadastroDoctoOficial;

    TipoDocumentoOficialPortal(String descricao, TipoValidacaoDoctoOficial tipoValidacaoDoctoOficial, TipoCadastroDoctoOficial tipoCadastroDoctoOficial) {
        this.descricao = descricao;
        this.tipoValidacaoDoctoOficial = tipoValidacaoDoctoOficial;
        this.tipoCadastroDoctoOficial = tipoCadastroDoctoOficial;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoValidacaoDoctoOficial getTipoValidacaoDoctoOficial() {
        return tipoValidacaoDoctoOficial;
    }

    public TipoCadastroDoctoOficial getTipoCadastroDoctoOficial() {
        return tipoCadastroDoctoOficial;
    }

    public boolean isFiltrarModulo() {
        return TipoValidacaoDoctoOficial.SEMVALIDACAO.equals(tipoValidacaoDoctoOficial);
    }
}
