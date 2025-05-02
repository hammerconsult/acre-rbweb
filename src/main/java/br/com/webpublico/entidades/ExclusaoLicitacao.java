package br.com.webpublico.entidades;


import br.com.webpublico.entidadesauxiliares.RelacionamentoLicitacao;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Etiqueta("Exclusão de Licitação")
public class ExclusaoLicitacao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Id Licitação")
    private Long idLicitacao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número")
    private Long numero;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Exclusão")
    private Date dataExclusao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Modalidade da Licitação")
    private ModalidadeLicitacao modalidadeLicitacao;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;

    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @Transient
    @Obrigatorio
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @Transient
    private List<RelacionamentoLicitacao> relacionamentos;

    public ExclusaoLicitacao() {
        relacionamentos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdLicitacao() {
        return idLicitacao;
    }

    public void setIdLicitacao(Long idLicitacao) {
        this.idLicitacao = idLicitacao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(Date dataExclusao) {
        this.dataExclusao = dataExclusao;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public void gerarHistorico() {
        StringBuilder historico = new StringBuilder("<b>LICITAÇÃO: </b> " + getLicitacao().toString());
        for (RelacionamentoLicitacao rel : getRelacionamentos()) {
            historico.append("<br/>").append("<b>").append(rel.getTipo().getDescricao().toUpperCase()).append(":</b> ").append(rel.getMovimento()).append(", <b>ID: </b> ").append(rel.getId());
        }
        setHistorico(historico.toString());
    }

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public List<RelacionamentoLicitacao> getRelacionamentos() {
        return relacionamentos;
    }

    public void setRelacionamentos(List<RelacionamentoLicitacao> relacionamentos) {
        this.relacionamentos = relacionamentos;
    }
}
