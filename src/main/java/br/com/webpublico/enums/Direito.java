package br.com.webpublico.enums;

import static com.google.common.base.Preconditions.checkNotNull;

public enum Direito {

    LEITURA(4, "Leitura") {
        @Override
        public boolean matches(String url) {
            checkNotNull(url);
            return url.matches(".*/(lista|visualizar)\\.(xhtml|jsf)");
        }
    },
    ESCRITA(2, "Escrita") {
        @Override
        public boolean matches(String url) {
            checkNotNull(url);
            return url.matches(".*/edita\\.(xhtml|jsf)");
        }
    },
    EXCLUSAO(1, "Exclusão") {
        @Override
        public boolean matches(String url) {
            checkNotNull(url);
            return false;
        }
    };
    private final int codigo;
    private String descricao;

    private Direito(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public abstract boolean matches(String url);
}
