package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Audited
public class ParametroAlvaraImediato extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    private Exercicio exercicio;
    @Obrigatorio
    @Etiqueta("Formulário")
    @ManyToOne
    private Formulario formulario;
    @Obrigatorio
    @Etiqueta("Serviço de Construção")
    @ManyToOne
    private ServicoConstrucao servicoConstrucao;
    @Obrigatorio
    @Etiqueta("Tipo de Imóvel")
    @ManyToOne
    private Atributo tipoImovel;
    @Obrigatorio
    @Etiqueta("Utilização do Imóvel")
    @ManyToOne
    private Atributo utilizacaoImovel;
    @Obrigatorio
    @Etiqueta("E-mail Responsável")
    private String email;
    @OneToMany(mappedBy = "parametro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroAlvaraImediatoCoibicao> coibicoes;
    @OneToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    public ParametroAlvaraImediato() {
        super();
        coibicoes = Lists.newArrayList();
        detentorArquivoComposicao = new DetentorArquivoComposicao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public ServicoConstrucao getServicoConstrucao() {
        return servicoConstrucao;
    }

    public void setServicoConstrucao(ServicoConstrucao servicoConstrucao) {
        this.servicoConstrucao = servicoConstrucao;
    }

    public Atributo getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(Atributo tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public Atributo getUtilizacaoImovel() {
        return utilizacaoImovel;
    }

    public void setUtilizacaoImovel(Atributo utilizacaoImovel) {
        this.utilizacaoImovel = utilizacaoImovel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ParametroAlvaraImediatoCoibicao> getCoibicoes() {
        return coibicoes;
    }

    public void setCoibicoes(List<ParametroAlvaraImediatoCoibicao> coibicoes) {
        this.coibicoes = coibicoes;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }
}
