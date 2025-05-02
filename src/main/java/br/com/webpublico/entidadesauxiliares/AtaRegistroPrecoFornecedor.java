package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.webreportdto.dto.administrativo.AtaRegistroPrecoFornecedorDTO;
import br.com.webpublico.webreportdto.dto.administrativo.RelatorioAtaRegistroPrecoDTO;
import com.google.common.collect.Lists;

import java.util.List;

public class AtaRegistroPrecoFornecedor {

    private String fornecedor;
    private List<AtaRegistroPrecoItens> itensPorQuantidade;
    private List<AtaRegistroPrecoItens> itensPorValor;

    public AtaRegistroPrecoFornecedor() {
        itensPorQuantidade = Lists.newArrayList();
        itensPorValor = Lists.newArrayList();
    }

    public static List<AtaRegistroPrecoFornecedorDTO> fornecedoresToDto(List<AtaRegistroPrecoFornecedor> fornecedores) {
        List<AtaRegistroPrecoFornecedorDTO> retorno = Lists.newArrayList();
        if (fornecedores != null && !fornecedores.isEmpty()) {
            for (AtaRegistroPrecoFornecedor fornecedor : fornecedores) {
                retorno.add(fornecedorToDto(fornecedor));
            }
        }
        return retorno;
    }

    public static AtaRegistroPrecoFornecedorDTO fornecedorToDto(AtaRegistroPrecoFornecedor fornecedor) {
        AtaRegistroPrecoFornecedorDTO retorno = new AtaRegistroPrecoFornecedorDTO();
        retorno.setFornecedor(fornecedor.getFornecedor());
        retorno.setItensPorQuantidade(AtaRegistroPrecoItens.itensToDto(fornecedor.getItensPorQuantidade()));
        retorno.setItensPorValor(AtaRegistroPrecoItens.itensToDto(fornecedor.getItensPorValor()));
        return retorno;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public List<AtaRegistroPrecoItens> getItensPorQuantidade() {
        return itensPorQuantidade;
    }

    public void setItensPorQuantidade(List<AtaRegistroPrecoItens> itensPorQuantidade) {
        this.itensPorQuantidade = itensPorQuantidade;
    }

    public List<AtaRegistroPrecoItens> getItensPorValor() {
        return itensPorValor;
    }

    public void setItensPorValor(List<AtaRegistroPrecoItens> itensPorValor) {
        this.itensPorValor = itensPorValor;
    }
}
