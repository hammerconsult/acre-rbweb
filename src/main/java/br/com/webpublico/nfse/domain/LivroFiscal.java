package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.nfse.domain.dtos.LivroFiscalNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Livro Fiscal")
public class LivroFiscal extends SuperEntidade implements Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Integer numero;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês Inicial")
    @Enumerated(EnumType.STRING)
    private Mes mesInicial;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês Final")
    @Enumerated(EnumType.STRING)
    private Mes mesFinal;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo do Movimento")
    private TipoMovimentoMensal tipoMovimento;

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Prestador")
    private CadastroEconomico prestador;

    @OneToMany(mappedBy = "livroFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLivroFiscal> itens;

    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Abertura")
    private Date abertura;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Encerramento")
    private Date encerramento;


    public LivroFiscal() {
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoMovimentoMensal getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimentoMensal tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public List<ItemLivroFiscal> getItens() {
        return itens;
    }

    public void setItens(List<ItemLivroFiscal> itens) {
        this.itens = itens;
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public Date getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(Date encerramento) {
        this.encerramento = encerramento;
    }

    @Override
    public LivroFiscalNfseDTO toNfseDto() {
        LivroFiscalNfseDTO dto = new LivroFiscalNfseDTO();
        dto.setAbertura(this.getAbertura());
        dto.setEncerramento(this.getEncerramento());
        dto.setExercicio(this.getExercicio().getAno());
        dto.setMesInicial(this.getMesInicial().getNumeroMes());
        dto.setMesFinal(this.getMesFinal().getNumeroMes());
        dto.setTipoMovimento(this.getTipoMovimento().toDto());
        dto.setPrestador(this.getPrestador().toSimpleNfseDto());
        dto.setNumero(this.getNumero());
        return dto;
    }
}
