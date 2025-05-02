/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.dto.SituacaoCadastralRFB;

/**
 * @author Pedro
 */
@GrupoDiagrama(nome = "CadastroEconomico")
public enum SituacaoCadastralCadastroEconomico {

    ATIVA_REGULAR("Ativo Regular", "Ativação", SituacaoCadastralPessoa.ATIVO, 1),
    ATIVA_NAO_REGULAR("Ativo Não Regular", "Ativação Não Regular", SituacaoCadastralPessoa.ATIVO, 2),
    BAIXADA("Baixado", "Baixa", SituacaoCadastralPessoa.BAIXADO, 3),
    SUSPENSA("Suspenso", "Suspensão", SituacaoCadastralPessoa.SUSPENSO, 4),
    AGUARDANDO_AVALIACAO("Aguardando Avaliação", "Aguardando Avaliação", SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO, 5),
    NULA("Nulo", "Nula", SituacaoCadastralPessoa.INATIVO, 6),
    INAPTA("Inapto", "Inapta", SituacaoCadastralPessoa.INATIVO, 7),
    INATIVO("Inativo", "Inativação", SituacaoCadastralPessoa.INATIVO, 8);
    private final String descricao;
    private final String descricaoMovimento;
    private final SituacaoCadastralPessoa situacaoCadastralPessoa;
    private final Integer codigo;

    SituacaoCadastralCadastroEconomico(String descricao, String descricaoMovimento, SituacaoCadastralPessoa situacaoCadastralPessoa, Integer codigo) {
        this.descricao = descricao;
        this.descricaoMovimento = descricaoMovimento;
        this.situacaoCadastralPessoa = situacaoCadastralPessoa;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoMovimento() {
        return descricaoMovimento;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public static SituacaoCadastralCadastroEconomico getSituacaoCadastralBySituacaoCadastralRFB(SituacaoCadastralRFB sitRFB) {
        if (sitRFB == null) return null;
        for (SituacaoCadastralCadastroEconomico sitCmc : values()) {
            if (sitCmc.name().equals(sitRFB.name())) {
                return sitCmc;
            }
        }
        return ATIVA_REGULAR;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
