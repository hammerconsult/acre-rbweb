package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 28/11/13
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Cacheable
@Audited
public class ItemNotificacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Numero")
    private BigDecimal numero;
    @OneToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Cadastro")
    private Cadastro cadastro;
    @OneToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Contribuinte")
    private Pessoa contribuinte;
    @Invisivel
    @Transient
    private Long criadoEm;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Pesquisavel
    @Etiqueta("Aceite")
    private Aceite aceite;
    @OneToOne( mappedBy = "itemNotificacao" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private DocumentoNotificacao documentoNotificacao;
    @ManyToOne
    @Pesquisavel
    private NotificacaoCobrancaAdmin notificacaoADM;
    @OneToMany(mappedBy = "itemNotificacao", cascade = CascadeType.MERGE)
    private List<ItemProgramacaoCobranca> itemProgramacaoCobrancaLista;

    public ItemNotificacao() {
        this.criadoEm = System.currentTimeMillis();
        this.itemProgramacaoCobrancaLista = new ArrayList<>();
        this.documentoNotificacao = new DocumentoNotificacao();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getNumero() {
        return numero;
    }

    public void setNumero(BigDecimal numero) {
        this.numero = numero;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public Aceite getAceite() {
        return aceite;
    }

    public void setAceite(Aceite aceite) {
        this.aceite = aceite;
    }

    public DocumentoNotificacao getDocumentoNotificacao() {
        return documentoNotificacao;
    }

    public void setDocumentoNotificacao(DocumentoNotificacao documentoNotificacao) {
        this.documentoNotificacao = documentoNotificacao;
    }

    public NotificacaoCobrancaAdmin getNotificacaoADM() {
        return notificacaoADM;
    }

    public void setNotificacaoADM(NotificacaoCobrancaAdmin notificacaoADM) {
        this.notificacaoADM = notificacaoADM;
    }

    public List<ItemProgramacaoCobranca> getItemProgramacaoCobrancaLista() {
        return itemProgramacaoCobrancaLista;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public void setItemProgramacaoCobrancaLista(List<ItemProgramacaoCobranca> itemProgramacaoCobrancaLista) {
        this.itemProgramacaoCobrancaLista = itemProgramacaoCobrancaLista;
    }

    public List<ParcelaValorDivida> parcelas(){
        List<ParcelaValorDivida> retorno = new ArrayList<>();
        for(ItemProgramacaoCobranca item: itemProgramacaoCobrancaLista){
            retorno.add(item.getParcelaValorDivida());
        }
        return retorno;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return numero != null ? numero.toString() : "";
    }
}
