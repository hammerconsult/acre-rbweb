package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 22/01/14
 * Time: 09:21
 * Classe Utilizada para geração do arquivo do e-consig
 * To change this template use File | Settings | File Templates.
 */
public enum SituacaoVinculo {
    CARREIRA("CARREIRA", 1),
    COMISSAO("COMISSÃO", 2),
    CARREIRA_COMISSAO("CARREIRA/COMISSÃO", 3),
    CONTRATADO("CONTRATADO", 4),
    ELETIVO("ELETIVO", 5),
    CONTRATO_TEMPORARIO("CONTRATO TEMPORÁRIO", 6);
    private String descricao;
    private Integer codigo;

    private SituacaoVinculo(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public static SituacaoVinculo getSitucaoComCodigo(Integer codigo) {
        for (SituacaoVinculo sv : SituacaoVinculo.values()) {
            if (sv.codigo.equals(codigo)) return sv;
        }
        return null;
    }
}
