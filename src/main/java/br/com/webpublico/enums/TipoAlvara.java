package br.com.webpublico.enums;

import br.com.webpublico.entidades.CalculoAlvaraFuncionamento;
import br.com.webpublico.entidades.CalculoAlvaraLocalizacao;
import br.com.webpublico.entidades.CalculoAlvaraSanitario;
import br.com.webpublico.entidades.ProcessoCalculoAlvara;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

@GrupoDiagrama(nome = "Alvara")
public enum TipoAlvara implements EnumComDescricao {

    LOCALIZACAO("L - Localização", "Localização", "AlvaraLocalizacao", CalculoAlvaraLocalizacao.class, "CALCULOALVARALOCALIZACAO", 1),
    FUNCIONAMENTO("F - Funcionamento", "Funcionamento", "AlvaraFuncionamento", CalculoAlvaraFuncionamento.class, "CALCULOALVARAFUNC", 2),
    SANITARIO("S - Vigilância Sanitária", "Vigilância Sanitária", "AlvaraSanitario", CalculoAlvaraSanitario.class, "CALCULOALVARASANITARIO", 3),
    AMBIENTAL("A - Ambiental", "Ambiental", "AlvaraAmbiental", CalculoAlvaraSanitario.class, "CALCULOALVARASANITARIO", 4),
    LOCALIZACAO_FUNCIONAMENTO("LF - Localização e Funcionamento", "Localização e Funcionamento", "AlvaraLocalizacaoFuncionamento", ProcessoCalculoAlvara.class, "PROCESSOCALCULOALVARA", 5);

    private final String descricao;
    private final String descricaoSimples;
    private final String tabelaSql;
    private final String jasper;
    private final Class entidade;
    private final Integer ordenacao;

    TipoAlvara(String descricao, String descricaoSimples, String jasper, Class entidade, String tabelaSql, Integer ordenacao) {
        this.descricao = descricao;
        this.descricaoSimples = descricaoSimples;
        this.jasper = jasper;
        this.entidade = entidade;
        this.tabelaSql = tabelaSql;
        this.ordenacao = ordenacao;
    }

    public static TipoAlvara tipoAlvaraPorDescricao(String descricao) {
        for (TipoAlvara tipo : values()) {
            if (tipo.getDescricao().equals(descricao)) {
                return tipo;
            }
        }
        return LOCALIZACAO;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoSimples() {
        return descricaoSimples;
    }

    public boolean isLocalizacao() {
        return this.equals(LOCALIZACAO);
    }

    public boolean isSanitario() {
        return this.equals(SANITARIO);
    }

    public boolean isFuncionamento() {
        return this.equals(FUNCIONAMENTO);
    }

    public boolean isAmbiental() {
        return this.equals(AMBIENTAL);
    }

    public boolean isFuncionamentoOrAmbiental() {
        return isFuncionamento() || isAmbiental();
    }

    public boolean isFuncionamentoOrSanitaria() {
        return isFuncionamento() || isSanitario();
    }

    public String getJasper() {
        return jasper;
    }

    public Class getEntidade() {
        return entidade;
    }

    public String getTabelaSql() {
        return tabelaSql;
    }

    public Integer getOrdenacao() {
        return ordenacao;
    }
}
