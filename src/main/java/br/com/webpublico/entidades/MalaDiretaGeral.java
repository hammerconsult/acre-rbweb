package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoMalaDireta;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 07/06/2016.
 */
@Entity
@Audited
@Etiqueta("Mala Direta de IPTU")
public class MalaDiretaGeral extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Exercício")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Exercicio exercicio;
    @OneToMany(mappedBy = "malaDiretaGeral", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemMalaDiretaGeral> itensMalaDireta;
    @Etiqueta("Texto da Mala Direta")
    private String texto;
    @Etiqueta("Cabecalho da Mala Direta")
    private String cabecalho;
    @Etiqueta("Tipo de Mala Direta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoMalaDireta tipoMalaDireta;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Cadastro")
    @Tabelavel
    @Pesquisavel
    private TipoCadastroTributario tipoCadastro;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Gerado em")
    @Tabelavel
    @Pesquisavel
    private Date geradoEm;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Vencimento para")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private Date vencimento;
    private Boolean enviaEmail;
    private Boolean consideraDebitos;
    @ManyToOne
    private ParametroMalaDireta parametroMalaDireta;
    @Transient
    private ConfiguracaoDAM configuracaoDAM;

    public MalaDiretaGeral() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ItemMalaDiretaGeral> getItensMalaDireta() {
        return itensMalaDireta;
    }

    public void setItensMalaDireta(List<ItemMalaDiretaGeral> itensMalaDireta) {
        this.itensMalaDireta = itensMalaDireta;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public TipoMalaDireta getTipoMalaDireta() {
        return tipoMalaDireta;
    }

    public void setTipoMalaDireta(TipoMalaDireta tipoMalaDireta) {
        this.tipoMalaDireta = tipoMalaDireta;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public Boolean getEnviaEmail() {
        return enviaEmail;
    }

    public void setEnviaEmail(Boolean enviaEmail) {
        this.enviaEmail = enviaEmail;
    }

    public Boolean getConsideraDebitos() {
        return consideraDebitos;
    }

    public void setConsideraDebitos(Boolean consideraDebitos) {
        this.consideraDebitos = consideraDebitos;
    }

    public ParametroMalaDireta getParametroMalaDireta() {
        return parametroMalaDireta;
    }

    public void setParametroMalaDireta(ParametroMalaDireta parametroMalaDireta) {
        this.parametroMalaDireta = parametroMalaDireta;
    }

    public ConfiguracaoDAM getConfiguracaoDAM() {
        return configuracaoDAM;
    }

    public void setConfiguracaoDAM(ConfiguracaoDAM configuracaoDAM) {
        this.configuracaoDAM = configuracaoDAM;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public String getCabecalho() {
        return cabecalho;
    }

    public void setCabecalho(String cabecalho) {
        this.cabecalho = cabecalho;
    }

    @Override
    public String toString() {
        String decricao = "";
        if (tipoMalaDireta != null) {
            decricao = tipoMalaDireta.getDescricao();
        }
        if (tipoCadastro != null) {
            decricao += " - " + tipoCadastro.getDescricaoLonga();
        }
        return decricao;
    }
}
