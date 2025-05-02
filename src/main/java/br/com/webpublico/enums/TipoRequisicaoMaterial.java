package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoRequisicaoMaterialDTO;

/**
 * Created by mga on 29/12/2017.
 */
public enum TipoRequisicaoMaterial {
    CONSUMO("Consumo", TipoRequisicaoMaterialDTO.CONSUMO),
    TRANSFERENCIA("Transferência", TipoRequisicaoMaterialDTO.TRANSFERENCIA),
    /*Apesar do tipo de uma requisição poder ser 'Empréstimo', a instância da classe é RequisicaoTransferenciaMaterial e seu comportamento é o mesmo.*/
    EMPRESTIMO("Empréstimo", TipoRequisicaoMaterialDTO.EMPRESTIMO);
    private String descricao;
    private TipoRequisicaoMaterialDTO toDto;

    TipoRequisicaoMaterial(String descricao, TipoRequisicaoMaterialDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoRequisicaoMaterialDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
