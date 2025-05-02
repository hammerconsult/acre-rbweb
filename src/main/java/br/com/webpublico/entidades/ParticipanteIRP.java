package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoIRP;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 12/01/2016.
 */
@Entity
@Audited
@Etiqueta("Participante de Intenção de Registro de Preço")
public class ParticipanteIRP extends SuperEntidade implements Comparable<ParticipanteIRP> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Interesse")
    private Date dataInteresse;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Intenção de Registro Preço")
    private IntencaoRegistroPreco intencaoRegistroDePreco;

    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Etiqueta("Participante")
    private UsuarioSistema participante;

    @Obrigatorio
    @Length(maximo = 255)
    @Etiqueta("Interesse")
    @Pesquisavel
    private String interesse;

    @Transient
    @Etiqueta("Unidade Organizacional")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoIRP situacao;

    private Boolean gerenciador;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participanteIRP", orphanRemoval = true)
    private List<UnidadeParticipanteIRP> unidades;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "participanteIRP", orphanRemoval = true)
    private List<ItemParticipanteIRP> itens;

    public ParticipanteIRP() {
        unidades = Lists.newArrayList();
        itens = Lists.newArrayList();
        gerenciador = Boolean.FALSE;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public List<ItemParticipanteIRP> getItens() {
        return itens;
    }

    public void setItens(List<ItemParticipanteIRP> itens) {
        this.itens = itens;
    }

    public SituacaoIRP getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoIRP situacao) {
        this.situacao = situacao;
    }

    public List<UnidadeParticipanteIRP> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeParticipanteIRP> unidades) {
        this.unidades = unidades;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntencaoRegistroPreco getIntencaoRegistroDePreco() {
        return intencaoRegistroDePreco;
    }

    public void setIntencaoRegistroDePreco(IntencaoRegistroPreco intencaoRegistroDePreco) {
        this.intencaoRegistroDePreco = intencaoRegistroDePreco;
    }

    public UsuarioSistema getParticipante() {
        return participante;
    }

    public void setParticipante(UsuarioSistema participante) {
        this.participante = participante;
    }

    public String getInteresse() {
        return interesse;
    }

    public void setInteresse(String interesse) {
        this.interesse = interesse;
    }

    public Date getDataInteresse() {
        return dataInteresse;
    }

    public void setDataInteresse(Date dataInteresse) {
        this.dataInteresse = dataInteresse;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean getGerenciador() {
        return gerenciador;
    }

    public void setGerenciador(Boolean gerenciador) {
        this.gerenciador = gerenciador;
    }

    public boolean isParticipanteAprovado() {
        return SituacaoIRP.APROVADA.equals(situacao);
    }

    public boolean isParticipanteEmElaboracao() {
        return SituacaoIRP.EM_ELABORACAO.equals(situacao);
    }

    public String toString() {
        try {
            return "Part: " + numero + " - " + hierarquiaOrganizacional
                + " (Irp: " + intencaoRegistroDePreco.getNumero() + "/" + DataUtil.getAno(intencaoRegistroDePreco.getDataLancamento()) + ")";
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public int compareTo(ParticipanteIRP o) {
        if (o.getNumero() != null && getNumero() != null) {
            return ComparisonChain.start().compare(o.getGerenciador(), getGerenciador())
                .compare(getNumero(), o.getNumero()).result();
        }

        return 0;
    }
}
