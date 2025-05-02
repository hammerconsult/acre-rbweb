package br.com.webpublico.enums;

/**
 * Created by carlos on 01/09/15.
 */
public enum PartesCorpoHumano {
    CABECA_PESCOCO("Cabeça e Pescoço"),
    MEMBRO_SUPERIOR("Membro Superior"),
    TORAX("Tórax"),
    ABDOMEN("Abdômen"),
    COSTAS("Costas"),
    PELVE_PERINEO("Pelve e Períneo"),
    MEMBRO_INFERIOR("Membro Inferior");

    private String descricao;

    PartesCorpoHumano(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
