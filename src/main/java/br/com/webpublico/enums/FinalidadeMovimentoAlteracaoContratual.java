/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.FinalidadeMovimentoAlteracaoContratualDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Felipe Marinzeck
 */
public enum FinalidadeMovimentoAlteracaoContratual implements EnumComDescricao {
    SUPRESSAO("Supressão",  FinalidadeMovimentoAlteracaoContratualDTO.SUPRESSAO),
    ACRESCIMO("Acréscimo",  FinalidadeMovimentoAlteracaoContratualDTO.ACRESCIMO),
    NAO_SE_APLICA("Não se Aplica", FinalidadeMovimentoAlteracaoContratualDTO.NAO_SE_APLICA);
    private String descricao;
    private FinalidadeMovimentoAlteracaoContratualDTO dto;

    FinalidadeMovimentoAlteracaoContratual(String descricao, FinalidadeMovimentoAlteracaoContratualDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }


    public static List<FinalidadeMovimentoAlteracaoContratual> retornaFinalidadesPorOperacao(OperacaoMovimentoAlteracaoContratual operacao, TipoAlteracaoContratual tipoAlteracao) {
        List<FinalidadeMovimentoAlteracaoContratual> toReturn = Lists.newArrayList();
        if (operacao != null) {
            switch (operacao) {
                case REDUCAO_PRAZO:
                    toReturn.add(SUPRESSAO);
                    break;
                case TRANSFERENCIA_FORNECEDOR:
                case TRANSFERENCIA_UNIDADE:
                    toReturn.add(NAO_SE_APLICA);
                    break;
                case OUTRO:
                case REDIMENSIONAMENTO_OBJETO:
                    if (tipoAlteracao.isApostilamento()) {
                        toReturn.add(NAO_SE_APLICA);
                        break;
                    }
                    toReturn.add(SUPRESSAO);
                    toReturn.add(ACRESCIMO);
                    break;
                default:
                    toReturn.add(ACRESCIMO);
                    break;
            }
        }
        return toReturn;
    }

    public boolean isSupressao() {
        return FinalidadeMovimentoAlteracaoContratual.SUPRESSAO.equals(this);
    }

    public boolean isAcrescimo() {
        return FinalidadeMovimentoAlteracaoContratual.ACRESCIMO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
