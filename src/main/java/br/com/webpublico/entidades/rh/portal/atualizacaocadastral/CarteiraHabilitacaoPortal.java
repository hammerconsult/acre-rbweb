package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.enums.CategoriaHabilitacao;
import br.com.webpublico.pessoa.dto.CarteiraHabilitacaoDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class CarteiraHabilitacaoPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PessoaFisicaPortal pessoaFisicaPortal;
    private String numero;
    @Enumerated(EnumType.STRING)
    private CategoriaHabilitacao categoria;
    private Date validade;
    private Date dataEmissao;
    private String orgaoExpedidor;
    private Date dataRegistro;

    public CarteiraHabilitacaoPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisicaPortal getPessoaFisicaPortal() {
        return pessoaFisicaPortal;
    }

    public void setPessoaFisicaPortal(PessoaFisicaPortal pessoaFisicaPortal) {
        this.pessoaFisicaPortal = pessoaFisicaPortal;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public CategoriaHabilitacao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaHabilitacao categoria) {
        this.categoria = categoria;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getOrgaoExpedidor() {
        return orgaoExpedidor;
    }

    public void setOrgaoExpedidor(String orgaoExpedidor) {
        this.orgaoExpedidor = orgaoExpedidor;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public static List<CarteiraHabilitacaoPortal> dtoToHabilitacoes(List<CarteiraHabilitacaoDTO> habilitacao, PessoaFisicaPortal pessoa) {
        List<CarteiraHabilitacaoPortal> habilitacoes = Lists.newLinkedList();
        for (CarteiraHabilitacaoDTO dto : habilitacao) {
            CarteiraHabilitacaoPortal f = dtoToCarteiraHabilitacaoPortal(dto, pessoa);
            if (f != null) {
                habilitacoes.add(f);
            }
        }
        return habilitacoes;
    }

    private static CarteiraHabilitacaoPortal dtoToCarteiraHabilitacaoPortal(CarteiraHabilitacaoDTO dto, PessoaFisicaPortal pessoa) {
        CarteiraHabilitacaoPortal ch = new CarteiraHabilitacaoPortal();
        ch.setCategoria(dto.getCategoriaCNH() != null ? CategoriaHabilitacao.valueOf(dto.getCategoriaCNH().name()) : null);
        ch.setDataEmissao(dto.getDataEmissaoCNH());
        ch.setDataRegistro(dto.getDataRegistro());
        ch.setNumero(dto.getNumeroCNH());
        ch.setOrgaoExpedidor(dto.getOrgaoExpedidorCNH());
        ch.setValidade(dto.getValidade());
        ch.setPessoaFisicaPortal(pessoa);
        return ch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarteiraHabilitacaoPortal that = (CarteiraHabilitacaoPortal) o;
        return Objects.equals(numero != null ? numero.trim() : null, that.numero != null ? that.numero.trim() : null)
            && categoria == that.categoria
            && Objects.equals(DataUtil.getDataFormatada(validade), DataUtil.getDataFormatada(that.validade))
            && Objects.equals(DataUtil.getDataFormatada(dataEmissao), DataUtil.getDataFormatada(that.dataEmissao))
            && Objects.equals(orgaoExpedidor != null ? orgaoExpedidor.toUpperCase().trim() : null, that.orgaoExpedidor != null ? that.orgaoExpedidor.toUpperCase().trim() : null);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, categoria, validade, dataEmissao, orgaoExpedidor);
    }
}
