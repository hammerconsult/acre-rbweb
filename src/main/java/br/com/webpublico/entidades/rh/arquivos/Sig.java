package br.com.webpublico.entidades.rh.arquivos;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.sig.EsferaPoderSig;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
public class Sig extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Representante Legal")
    @Pesquisavel
    @Obrigatorio
    private Pessoa representante;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Representante Legal Unidade Gestora")
    @Pesquisavel
    @Obrigatorio
    private Pessoa representanteUnidadeGestora;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Ente Federativo")
    @Pesquisavel
    @Obrigatorio
    private Pessoa enteFederativo;

    @Tabelavel
    @Etiqueta("Código do SIAFIl")
    @Pesquisavel
    @Obrigatorio
    private String codigo;

    @Tabelavel
    @Etiqueta("Mês")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private Mes mes;

    @Tabelavel
    @Etiqueta("Ano")
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    private Exercicio exercicio;

    @Enumerated(EnumType.STRING)
    private EsferaPoderSig esferaPoderSig;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public Sig() {
        detentorArquivoComposicao = new DetentorArquivoComposicao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Pessoa getRepresentante() {
        return representante;
    }

    public void setRepresentante(Pessoa representante) {
        this.representante = representante;
    }

    public Pessoa getRepresentanteUnidadeGestora() {
        return representanteUnidadeGestora;
    }

    public void setRepresentanteUnidadeGestora(Pessoa representanteUnidadeGestora) {
        this.representanteUnidadeGestora = representanteUnidadeGestora;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Pessoa getEnteFederativo() {
        return enteFederativo;
    }

    public void setEnteFederativo(Pessoa enteFederativo) {
        this.enteFederativo = enteFederativo;
    }

    public EsferaPoderSig getEsferaPoderSig() {
        return esferaPoderSig;
    }

    public void setEsferaPoderSig(EsferaPoderSig esferaPoderSig) {
        this.esferaPoderSig = esferaPoderSig;
    }
}
