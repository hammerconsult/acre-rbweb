package br.com.webpublico.nfse.enums;

public enum ModuloDesif {
    MODULO_1(1, "1 - Demonstrativo Contábil"),
    MODULO_2(2, "2 - Apuração Mensal do ISSQN"),
    MODULO_3(3, "3 - Informações Comuns aos Municípios"),
    MODULO_4(4, "4 - Partidas dos Lançamentos Contábeis");

    private Integer codigo;
    private String descricao;

    ModuloDesif(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public ModuloDesif findByCodigo(Integer codigo) {
        for (ModuloDesif modulo : ModuloDesif.values()) {
            if (modulo.getCodigo().equals(codigo)) {
                return modulo;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
