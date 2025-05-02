package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 01/09/14
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Proposta Técnica")
public class PropostaTecnica extends SuperEntidade implements Serializable, ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date data;

    @Etiqueta("Licitação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Licitacao licitacao;

    @Etiqueta("Fornecedor")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private Pessoa fornecedor;

    @Etiqueta("Representante")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private RepresentanteLicitacao representante;

    @Etiqueta("Nota Técnica")
    @Pesquisavel
    @Tabelavel
    private BigDecimal notaTecnica;

    @Etiqueta("Representante")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "propostaTecnica")
    private List<ItemPropostaTecnica> itens;

    public PropostaTecnica() {
        this.itens = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public RepresentanteLicitacao getRepresentante() {
        return representante;
    }

    public void setRepresentante(RepresentanteLicitacao representante) {
        this.representante = representante;
    }

    public BigDecimal getNotaTecnica() {
        notaTecnica = BigDecimal.ZERO;
        for (ItemPropostaTecnica itemPropostaTecnica : itens) {
            if (itemPropostaTecnica.getPonto() != null) {
                notaTecnica = notaTecnica.add(itemPropostaTecnica.getPonto());
            }
        }
        return notaTecnica;
    }

    public void setNotaTecnica(BigDecimal notaTecnica) {
        this.notaTecnica = notaTecnica;
    }

    public List<ItemPropostaTecnica> getItens() {
        return itens;
    }

    public void setItens(List<ItemPropostaTecnica> itens) {
        this.itens = itens;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public BigDecimal getTotalPontos() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ItemPropostaTecnica itemPropostaTecnica : itens) {
                if (itemPropostaTecnica.getPonto() == null) {
                    continue;
                }
                total = total.add(itemPropostaTecnica.getPonto());
            }
            return total;
        } catch (NullPointerException npe) {
            return total;
        }
    }

    public void ordenarListas() {
        Collections.sort(itens);
        for (ItemPropostaTecnica item : itens) {
            Collections.sort(item.getItemCriterioTecnica().getPontos());
        }
    }
}
