/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoAquisicaoContratoDTO;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoAquisicaoContrato implements EnumComDescricao {

    LICITACAO("Licitação", TipoAquisicaoContratoDTO.LICITACAO),
    DISPENSA_DE_LICITACAO("Dispensa de Licitação/Inexigibilidade", TipoAquisicaoContratoDTO.DISPENSA_DE_LICITACAO),
    PROCEDIMENTO_AUXILIARES("Procedimentos Auxiliares", TipoAquisicaoContratoDTO.PROCEDIMENTO_AUXILIARES),
    REGISTRO_DE_PRECO_EXTERNO("Registro de Preço Externo", TipoAquisicaoContratoDTO.REGISTRO_DE_PRECO_EXTERNO),
    COMPRA_DIRETA("Compra Direta", TipoAquisicaoContratoDTO.COMPRA_DIRETA),
    CONTRATOS_VIGENTE("Contratos Vigentes antes de 01/03/2017", TipoAquisicaoContratoDTO.CONTRATOS_VIGENTE),
    INTENCAO_REGISTRO_PRECO("Intenção Registro de Preço", TipoAquisicaoContratoDTO.INTENCAO_REGISTRO_PRECO),
    ADESAO_ATA_REGISTRO_PRECO_INTERNA("Adesão a Ata de Registro de Preço Interna", TipoAquisicaoContratoDTO.ADESAO_ATA_REGISTRO_PRECO_INTERNA);

    private String descricao;
    private TipoAquisicaoContratoDTO toDto;

    TipoAquisicaoContrato(String descricao, TipoAquisicaoContratoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isLicitacao() {
        return LICITACAO.equals(this);
    }

    public boolean isDispensa() {
        return DISPENSA_DE_LICITACAO.equals(this);
    }

    public boolean isContratoVigente() {
        return CONTRATOS_VIGENTE.equals(this);
    }

    public boolean isRegistroPrecoExterno() {
        return REGISTRO_DE_PRECO_EXTERNO.equals(this);
    }

    public boolean isAdesaoAtaInterna() {
        return ADESAO_ATA_REGISTRO_PRECO_INTERNA.equals(this);
    }

    public boolean isIrp() {
        return INTENCAO_REGISTRO_PRECO.equals(this);
    }

    public boolean isProcedimentosAuxiliares() {
        return PROCEDIMENTO_AUXILIARES.equals(this);
    }

    public static List<TipoAquisicaoContrato> getTiposLicitacao() {
        List<TipoAquisicaoContrato> toReturn = Lists.newArrayList();
        toReturn.add(LICITACAO);
        toReturn.add(ADESAO_ATA_REGISTRO_PRECO_INTERNA);
        toReturn.add(INTENCAO_REGISTRO_PRECO);
        return toReturn;
    }

    public static List<TipoAquisicaoContrato> getTiposAquisicaoPorModalidade(ModalidadeLicitacaoEmpenho modalidade) {
        List<TipoAquisicaoContrato> toReturn = Lists.newArrayList();
        if (ModalidadeLicitacaoEmpenho.getModalidadesParaProcessoDeLicitacao().contains(modalidade)) {
            toReturn.add(LICITACAO);
            toReturn.add(ADESAO_ATA_REGISTRO_PRECO_INTERNA);
            toReturn.add(INTENCAO_REGISTRO_PRECO);
            toReturn.add(CONTRATOS_VIGENTE);
        } else if (ModalidadeLicitacaoEmpenho.getModalidadesParaProcessoDeDispensa().contains(modalidade)) {
            toReturn.add(DISPENSA_DE_LICITACAO);
        } else {
            toReturn.add(REGISTRO_DE_PRECO_EXTERNO);
        }
        return toReturn;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
