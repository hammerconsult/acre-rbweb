package br.com.webpublico.consultaentidade;

public enum TipoURLConsultaEntidade {
    URL_RETORNO("url-retorno-consulta-entidade");

    private final String key;

    TipoURLConsultaEntidade(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
