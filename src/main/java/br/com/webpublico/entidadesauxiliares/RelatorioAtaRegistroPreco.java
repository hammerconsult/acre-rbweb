package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.webreportdto.dto.administrativo.RelatorioAtaRegistroPrecoDTO;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class RelatorioAtaRegistroPreco {

    private Long numeroAta;
    private Date dataInicio;
    private Date dataVencimento;
    private String licitacao;
    private String numeroProcesso;
    private String modalidade;
    private String secretaria;
    private String objeto;
    private List<AtaRegistroPrecoFornecedor> fornecedores;
    private List<AtaRegistroPrecoContrato> contratos;
    private Boolean hasItemPorQuantidade;
    private Boolean hasItemPorValor;

    public RelatorioAtaRegistroPreco(){
        fornecedores = Lists.newArrayList();
        contratos = Lists.newArrayList();
    }

    public static RelatorioAtaRegistroPrecoDTO ataToDto(RelatorioAtaRegistroPreco ata) {
        RelatorioAtaRegistroPrecoDTO retorno = new RelatorioAtaRegistroPrecoDTO();
        retorno.setNumeroAta(ata.getNumeroAta());
        retorno.setDataInicio(ata.getDataInicio());
        retorno.setDataVencimento(ata.getDataVencimento());
        retorno.setLicitacao(ata.getLicitacao());
        retorno.setNumeroProcesso(ata.getNumeroProcesso());
        retorno.setModalidade(ata.getModalidade());
        retorno.setSecretaria(ata.getSecretaria());
        retorno.setObjeto(ata.getObjeto());
        retorno.setHasItemPorQuantidade(ata.getHasItemPorQuantidade());
        retorno.setHasItemPorValor(ata.getHasItemPorValor());
        retorno.setFornecedores(AtaRegistroPrecoFornecedor.fornecedoresToDto(ata.getFornecedores()));
        return retorno;
    }

    public Long getNumeroAta() {
        return numeroAta;
    }

    public void setNumeroAta(Long numeroAta) {
        this.numeroAta = numeroAta;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(String licitacao) {
        this.licitacao = licitacao;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(String secretaria) {
        this.secretaria = secretaria;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public List<AtaRegistroPrecoFornecedor> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<AtaRegistroPrecoFornecedor> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public List<AtaRegistroPrecoContrato> getContratos() {
        return contratos;
    }

    public void setContratos(List<AtaRegistroPrecoContrato> contratos) {
        this.contratos = contratos;
    }

    public Boolean getHasItemPorQuantidade() {
        if (hasItemPorQuantidade == null) {
            for (AtaRegistroPrecoFornecedor f : fornecedores) {
                hasItemPorQuantidade = !f.getItensPorQuantidade().isEmpty();
            }
        }
        return hasItemPorQuantidade;
    }

    public void setHasItemPorQuantidade(Boolean hasItemPorQuantidade) {
        this.hasItemPorQuantidade = hasItemPorQuantidade;
    }

    public Boolean getHasItemPorValor() {
        if (hasItemPorValor == null) {
            for (AtaRegistroPrecoFornecedor f : fornecedores) {
                hasItemPorValor = !f.getItensPorValor().isEmpty();
            }
        }
        return hasItemPorValor;
    }

    public void setHasItemPorValor(Boolean hasItemPorValor) {
        this.hasItemPorValor = hasItemPorValor;
    }
}
