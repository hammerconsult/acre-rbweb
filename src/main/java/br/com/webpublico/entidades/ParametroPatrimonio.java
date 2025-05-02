package br.com.webpublico.entidades;

import br.com.webpublico.entidades.administrativo.patrimonio.SecretariaControleSetorial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 23/05/14
 * Time: 11:57
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Parâmetros do Patrimônio")
public class ParametroPatrimonio extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Data de Criação")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataCriacao;

    @Etiqueta("Data de Corte")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataDeCorte;

    @Etiqueta("Data de Referência")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataDeReferencia;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Configurado por")
    private UsuarioSistema usuarioSistema;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Faixa inicial para bens inservíveis")
    private Long faixaInicialParaInsevivel;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Faixa final para bens inservíveis")
    private Long faixaFinalParaInsevivel;

    @OneToMany(mappedBy = "parametroPatrimonio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponsavelPatrimonio> listaResponsavelPatrimonio;

    @OneToMany(mappedBy = "parametroPatrimonio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntidadeGeradoraIdentificacaoPatrimonio> entidadeGeradoras;

    @OneToMany(mappedBy = "parametroPatrimonio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SecretariaControleSetorial> secretarias;

    public ParametroPatrimonio() {
        super();
        this.listaResponsavelPatrimonio = new ArrayList<>();
        this.entidadeGeradoras = new ArrayList<>();
        this.secretarias = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ResponsavelPatrimonio> getListaResponsavelPatrimonio() {
        Collections.sort(listaResponsavelPatrimonio);
        return listaResponsavelPatrimonio;
    }

    public void setListaResponsavelPatrimonio(List<ResponsavelPatrimonio> listaResponsavelPatrimonio) {
        this.listaResponsavelPatrimonio = listaResponsavelPatrimonio;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public List<EntidadeGeradoraIdentificacaoPatrimonio> getEntidadeGeradoras() {
        if (entidadeGeradoras == null) {
            return Lists.newArrayList();
        }
        return entidadeGeradoras;
    }

    public List<EntidadeGeradoraIdentificacaoPatrimonio> getEntidadeGeradorasMoveis() {
        List<EntidadeGeradoraIdentificacaoPatrimonio> retorno = Lists.newArrayList();
        if (entidadeGeradoras != null) {
            for (EntidadeGeradoraIdentificacaoPatrimonio ent : entidadeGeradoras) {
                if (ent.isTipoGeracaoBemMovel()) {
                    Util.adicionarObjetoEmLista(retorno, ent);
                }
            }
        }
        return retorno;
    }

    public List<EntidadeGeradoraIdentificacaoPatrimonio> getEntidadeGeradorasImoveis() {
        List<EntidadeGeradoraIdentificacaoPatrimonio> retorno = Lists.newArrayList();
        if (entidadeGeradoras != null) {
            for (EntidadeGeradoraIdentificacaoPatrimonio ent : entidadeGeradoras) {
                if (ent.isTipoGeracaoBemImovel()) {
                    Util.adicionarObjetoEmLista(retorno, ent);
                }
            }
        }
        return retorno;
    }

    public void setEntidadeGeradoras(List<EntidadeGeradoraIdentificacaoPatrimonio> entidadeGeradoras) {
        this.entidadeGeradoras = entidadeGeradoras;
    }

    public Date getDataDeCorte() {
        return dataDeCorte;
    }

    public void setDataDeCorte(Date dataDeCorte) {
        this.dataDeCorte = dataDeCorte;
    }

    public Date getDataDeReferencia() {
        return dataDeReferencia;
    }

    public void setDataDeReferencia(Date dataDeReferencia) {
        this.dataDeReferencia = dataDeReferencia;
    }

    public Long getFaixaInicialParaInsevivel() {
        return faixaInicialParaInsevivel;
    }

    public void setFaixaInicialParaInsevivel(Long faixaInicialParaInsevivel) {
        this.faixaInicialParaInsevivel = faixaInicialParaInsevivel;
    }

    public Long getFaixaFinalParaInsevivel() {
        return faixaFinalParaInsevivel;
    }

    public void setFaixaFinalParaInsevivel(Long faixaFinalParaInsevivel) {
        this.faixaFinalParaInsevivel = faixaFinalParaInsevivel;
    }

    public List<SecretariaControleSetorial> getSecretarias() {
        return secretarias;
    }

    public void setSecretarias(List<SecretariaControleSetorial> secretarias) {
        this.secretarias = secretarias;
    }

    public void adicionarEntidadeGeradora(EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradora) {
        ValidacaoException ve = new ValidacaoException();
        entidadeGeradora.setParametroPatrimonio(this);

        try {
            entidadeGeradora.validarCamposObrigatorios();
        } catch (ValidacaoException vex) {
            ve.adicionarMensagensError(vex.getMensagens());
        }

        if (ve.validou) {
            if (entidadeGeradora instanceof EntidadeSequenciaPropria) {
                try {
                    Long i = Long.parseLong(entidadeGeradora.getFaixaInicial());
                    Long f = Long.parseLong(entidadeGeradora.getFaixaFinal());

                    if (i > f) {
                        ve.adicionarMensagemDeOperacaoNaoRealizada("A faixa inicial deve ser maior que a faixa final.");
                    }
                } catch (NumberFormatException ex) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("As faixas inicial e final devem ter somente numeros.");
                }

                if (entidadeGeradora.getFaixaFinal().length() < entidadeGeradora.getFaixaInicial().length()) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("A faixa final deve ter mais caracteres que a faixa inicial.");
                }
            }

            List<EntidadeGeradoraIdentificacaoPatrimonio> entidades = entidadeGeradora.isTipoGeracaoBemMovel() ? getEntidadeGeradorasMoveis() : getEntidadeGeradorasImoveis();
            for (EntidadeGeradoraIdentificacaoPatrimonio e : entidades) {
                if (!entidadeGeradora.equals(e) &&
                    entidadeGeradora.getEntidade().equals(e.getEntidade())) {
                    ve.adicionarMensagemDeOperacaoNaoRealizada("A entidade " + e.getEntidade().getNome() + " já foi adicionada.");
                }
            }
        }

        if (!ve.temMensagens()) {
            Util.adicionarObjetoEmLista(entidadeGeradoras, entidadeGeradora);
            if (!entidadeGeradora.ehSequenciaPropria()) {
                Util.adicionarObjetoEmLista(entidadeGeradora.getEntidadeSequenciaPropria().getAgregadas(), (EntidadeSequenciaAgregada) entidadeGeradora);
            } else {
                for (EntidadeSequenciaAgregada entidadeSequenciaAgregada : entidadeGeradora.getEntidadeSequenciaPropria().getAgregadas()) {
                    entidadeSequenciaAgregada.setEntidadeSequenciaPropria((EntidadeSequenciaPropria) entidadeGeradora);
                }
            }
        }
        ve.lancarException();
    }

    public void excluirEntidadeGeradora(EntidadeGeradoraIdentificacaoPatrimonio entidadeGeradora) {
        entidadeGeradoras.remove(entidadeGeradora);

        if (!entidadeGeradora.ehSequenciaPropria()) {
            entidadeGeradora.getEntidadeSequenciaPropria().getAgregadas().remove(entidadeGeradora);
        }
    }
}
