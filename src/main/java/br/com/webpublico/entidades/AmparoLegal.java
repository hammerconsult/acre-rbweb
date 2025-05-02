package br.com.webpublico.entidades;

import br.com.webpublico.enums.LeiLicitacao;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Amparo Legal")
public class AmparoLegal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Código")
    @Obrigatorio
    private Long codigo;

    @Etiqueta("Nome")
    @Obrigatorio
    private String nome;

    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;

    @Etiqueta("Ato Legal")
    @Tabelavel
    @ManyToOne
    private AtoLegal atoLegal;

    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    @Obrigatorio
    @Tabelavel
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    @Tabelavel
    private Date fimVigencia;

    @Etiqueta("Modalidade de Licitação")
    @Enumerated(EnumType.STRING)
    private ModalidadeLicitacao modalidadeLicitacao;

    @Etiqueta("Lei de Licitação")
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private LeiLicitacao leiLicitacao;

    public AmparoLegal() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public LeiLicitacao getLeiLicitacao() {
        return leiLicitacao;
    }

    public void setLeiLicitacao(LeiLicitacao leiLicitacao) {
        this.leiLicitacao = leiLicitacao;
    }

    @Override
    public String toString() {
        return nome;
    }
}
