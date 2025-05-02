/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.CarteiraHabilitacaoPortal;
import br.com.webpublico.enums.CategoriaHabilitacao;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.CarteiraHabilitacaoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
public class Habilitacao extends DocumentoPessoal {

    @Etiqueta("Numero")
    @Obrigatorio
    private String numero;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Categoria")
    @Obrigatorio
    private CategoriaHabilitacao categoria;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Validade")
    @Obrigatorio
    private Date validade;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Emissão")
    private Date dataEmissao;
    @Etiqueta("Órgâo Expedidor")
    private String orgaoExpedidor;

    public Habilitacao() {
    }

    public Habilitacao(PessoaFisica pessoaFisica, String numero, CategoriaHabilitacao categoria, Date validade, Date dataEmissao) {
        this.setPessoaFisica(pessoaFisica);
        this.numero = numero;
        this.categoria = categoria;
        this.validade = validade;
        this.dataEmissao = dataEmissao;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getOrgaoExpedidor() {
        if (orgaoExpedidor != null) {
            return orgaoExpedidor.toUpperCase();
        }
        return orgaoExpedidor;
    }

    public void setOrgaoExpedidor(String orgaoExpedidor) {
        this.orgaoExpedidor = orgaoExpedidor != null ? orgaoExpedidor.toUpperCase() : null;
    }

    public String getNumero() {
        return numero;
    }

    public CategoriaHabilitacao getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaHabilitacao categoria) {
        this.categoria = categoria;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    @Override
    public String toString() {
        try {
            return "Habilitação Número: " + this.numero + " - Categoria: " + this.categoria + " - Valida até.: " + new SimpleDateFormat("dd/MM/yyyy").format(this.validade);
        } catch (NullPointerException e) {
            return "Habilitação Número: " + this.numero + " - Categoria: " + this.categoria;
        }
    }

    public static List<CarteiraHabilitacaoDTO> toHabilitacoesDTO(List<Habilitacao> habilitacao) {
        List<CarteiraHabilitacaoDTO> carteiras = Lists.newLinkedList();
        for (Habilitacao hab : habilitacao) {
            CarteiraHabilitacaoDTO carteiraHabilitacaoDTO = toHabilitacaoDTO(hab);
            if (carteiraHabilitacaoDTO != null) {
                carteiras.add(carteiraHabilitacaoDTO);
            }
        }
        return carteiras;
    }

    public static CarteiraHabilitacaoDTO toHabilitacaoDTO(Habilitacao habilitacao) {
        if (habilitacao == null) {
            return null;
        }
        CarteiraHabilitacaoDTO dto = new CarteiraHabilitacaoDTO();
        dto.setId(habilitacao.getId());
        dto.setCategoriaCNH(habilitacao.getCategoria() != null ? br.com.webpublico.pessoa.enumeration.CategoriaHabilitacao.valueOf(habilitacao.getCategoria().name()) : null);
        dto.setDataEmissaoCNH(habilitacao.getDataEmissao());
        dto.setNumeroCNH(habilitacao.getNumero());
        dto.setOrgaoExpedidorCNH(habilitacao.getOrgaoExpedidor());
        dto.setValidade(habilitacao.getValidade());
        return dto;
    }

    public static List<Habilitacao> toHabilitacoes(PessoaFisica pessoaFisica, List<CarteiraHabilitacaoPortal> habilitacoes) {
        List<Habilitacao> habilitacaos = Lists.newLinkedList();
        for (CarteiraHabilitacaoPortal habilitacao : habilitacoes) {
            if (habilitacao == null) {
                return null;
            }
            Habilitacao dto = new Habilitacao();
            dto.setPessoaFisica(pessoaFisica);
            dto.setCategoria(habilitacao.getCategoria());
            dto.setDataEmissao(habilitacao.getDataEmissao());
            dto.setNumero(habilitacao.getNumero());
            dto.setOrgaoExpedidor(habilitacao.getOrgaoExpedidor());
            dto.setValidade(habilitacao.getValidade());
            habilitacaos.add(dto);
        }
        return habilitacaos;
    }

    public static List<CarteiraHabilitacaoDTO> toHabilitacoesPortalDTO(List<CarteiraHabilitacaoPortal> habilitacao) {
        List<CarteiraHabilitacaoDTO> carteiras = Lists.newLinkedList();
        for (CarteiraHabilitacaoPortal hab : habilitacao) {
            CarteiraHabilitacaoDTO carteiraHabilitacaoDTO = toHabilitacaoPortal(hab);
            if (carteiraHabilitacaoDTO != null) {
                carteiras.add(carteiraHabilitacaoDTO);
            }
        }
        return carteiras;
    }

    public static CarteiraHabilitacaoDTO toHabilitacaoPortal(CarteiraHabilitacaoPortal habilitacao) {
        if (habilitacao == null) {
            return null;
        }
        CarteiraHabilitacaoDTO dto = new CarteiraHabilitacaoDTO();
        dto.setId(habilitacao.getId());
        dto.setCategoriaCNH(habilitacao.getCategoria() != null ? br.com.webpublico.pessoa.enumeration.CategoriaHabilitacao.valueOf(habilitacao.getCategoria().name()) : null);
        dto.setDataEmissaoCNH(habilitacao.getDataEmissao());
        dto.setNumeroCNH(habilitacao.getNumero());
        dto.setOrgaoExpedidorCNH(habilitacao.getOrgaoExpedidor());
        dto.setValidade(habilitacao.getValidade());
        return dto;
    }
}
