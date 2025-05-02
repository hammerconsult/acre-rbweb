package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 17/07/14
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
public enum GrupoExportacaoRAIS {
    DISSIDIO_COLETIVO(15, "DC", "Dissídio Coletivo"),
    GRATIFICACOES(14, "GTF", "Gratificações"),
    BASE_CONTRIBUICAO_SINDICAL(13, "BCS", "Base da Contribuição Sindica"),
    CONTRIBUICAO_CONFEDERATIVA(12, "CC", "Contribuição Confederativa"),
    CONTRUBUICAO_ASSISTENCIAL(11, "C.ASSIST", "Contribuição Assistencial"),
    CONTRIBUICAO_SINDICAL(10, "CS", "Contribuição Sindical"),
    CONTRIBUICAO_ASSOCIATIVA(9, "C.ASSOC", "Contribuição Associativa"),
    FERIAS_INDENIZADAS(8, "FI", "Férias Indenizadas"),
    MULTA_RESCISORIA(7, "MR", "Multa Rescisória"),
    AVISO_PREVIO(6, "AP", "Aviso prévio"),
    SALARIO_13_PARCELA_2(5, "13º - 2P", "13º Salário 2ª Parcela"),
    SALARIO_13_PARCELA_1(4, "13º - 1P", "13º Salário 1ª Parcela"),
    HORAS_EXTRAS(3, "HE", "Horas Extras"),
    REMUNERACAO_MENSAL(2, "RM", "Remuneração Mensal"),
    SALARIO(1, "Salário", "Salário");

    private GrupoExportacaoRAIS(Integer codigo, String nomeReduzido, String descricao) {
        this.codigo = codigo;
        this.nomeReduzido = nomeReduzido;
        this.descricao = descricao;
    }

    private Integer codigo;
    private String nomeReduzido;
    private String descricao;

    public String getNomeReduzido() {
        return nomeReduzido;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
