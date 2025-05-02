/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.IndicadorRetificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Arquivo RAIS")
public class ArquivoRAIS extends SuperEntidade implements Serializable, ValidadorEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Entidade")
    private Entidade entidade;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Exercício")
    @Pesquisavel
    @Obrigatorio
    private Exercicio exercicio;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Responsável")
    @Pesquisavel
    @Obrigatorio
    private PessoaFisica pessoaFisica;
    @Etiqueta("Indicador de Retificação")
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private IndicadorRetificacao indicadorRetificacao;
    @Etiqueta("Data")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date geradoEm;
    @Invisivel
    @Etiqueta("Conteúdo")
    private String conteudo;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;
    @Transient
    @Invisivel
    private List<ContratoFP> servidores;
    @Transient
    @Invisivel
    private ContratoFP servidorSelecionado;

    public ArquivoRAIS() {
        super();
        this.servidores = new ArrayList<>();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public IndicadorRetificacao getIndicadorRetificacao() {
        return indicadorRetificacao;
    }

    public void setIndicadorRetificacao(IndicadorRetificacao indicadorRetificacao) {
        this.indicadorRetificacao = indicadorRetificacao;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<ContratoFP> getServidores() {
        return servidores;
    }

    public void setServidores(List<ContratoFP> servidores) {
        this.servidores = servidores;
    }

    public ContratoFP getServidorSelecionado() {
        return servidorSelecionado;
    }

    public void setServidorSelecionado(ContratoFP servidorSelecionado) {
        this.servidorSelecionado = servidorSelecionado;
    }

    @Override
    public String toString() {
        return entidade + " - " + exercicio.getAno();
    }

    public Date getPrimeiroDia() {
        Calendar pDia = Calendar.getInstance();
        pDia.set(Calendar.DAY_OF_MONTH, 1);
        pDia.set(Calendar.MONTH, 0);
        try {
            pDia.set(Calendar.YEAR, getExercicio().getAno());
        } catch (NullPointerException npe) {
            return null;
        }

        pDia.setTime(Util.getDataHoraMinutoSegundoZerado(pDia.getTime()));
        return pDia.getTime();
    }

    public Date getUltimoDia() {
        Calendar uDia = Calendar.getInstance();
        uDia.set(Calendar.DAY_OF_MONTH, 31);
        uDia.set(Calendar.MONTH, 11);
        try {
            uDia.set(Calendar.YEAR, getExercicio().getAno());
        } catch (NullPointerException npe) {
            return null;
        }

        uDia.setTime(Util.getDataHoraMinutoSegundoZerado(uDia.getTime()));
        return uDia.getTime();
    }

    public Boolean isIndicadorRetifica() {
        return IndicadorRetificacao.RETIFICA.equals(this.indicadorRetificacao);
    }

    public void adicionarServidorNaLista() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (servidorSelecionado != null) {
            if (!servidores.contains(servidorSelecionado)) {
                servidores.add(servidorSelecionado);
                servidorSelecionado = null;
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) já foi adicionado na lista.");
            }
        } else {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um(a) servidor(a) para adicioná-lo à lista.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void removerServidorNaLista(ContratoFP contratoFP) {
        servidores.remove(contratoFP);
    }

    public List<Long> idsServidores() {
        List<Long> ids = new ArrayList<>();
        for (ContratoFP s : servidores) {
            ids.add(s.getId());
        }
        return ids;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (isIndicadorRetifica() && servidores.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Informe ao menos um(a) servidor(a) para realizar a operação.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }
}
