package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoBancoArquivoObn;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Edi on 05/04/2016.
 */
@Entity
@Audited
@Etiqueta("Banco")
public class BancoObn extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Configuração Arquivo Obn")
    private ConfiguracaoArquivoObn configuracaoArquivoObn;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Banco")
    private String numeroDoBanco;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Banco")
    private TipoBancoArquivoObn tipoBancoArquivoObn;
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    @Etiqueta("Conta Única")
    @Obrigatorio
    private ContaBancariaEntidade contaBancariaEntidade;
    @OneToMany(mappedBy = "bancoObn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContratoObn> contratos;

    public BancoObn() {
        super();
        this.contratos = Lists.newArrayList();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoArquivoObn getConfiguracaoArquivoObn() {
        return configuracaoArquivoObn;
    }

    public void setConfiguracaoArquivoObn(ConfiguracaoArquivoObn configuracaoArquivoObn) {
        this.configuracaoArquivoObn = configuracaoArquivoObn;
    }

    public String getNumeroDoBanco() {
        return numeroDoBanco;
    }

    public void setNumeroDoBanco(String numeroDoBanco) {
        this.numeroDoBanco = numeroDoBanco;
    }

    public TipoBancoArquivoObn getTipoBancoArquivoObn() {
        return tipoBancoArquivoObn;
    }

    public void setTipoBancoArquivoObn(TipoBancoArquivoObn tipoBancoArquivoObn) {
        this.tipoBancoArquivoObn = tipoBancoArquivoObn;
    }

    public boolean isArquivoCaixaEconomica() {
        return TipoBancoArquivoObn.CAIXA_ECONOMICA.equals(this.getTipoBancoArquivoObn());
    }

    public boolean isArquivoBancoDoBrasil() {
        return TipoBancoArquivoObn.BANCO_DO_BRASIL.equals(this.getTipoBancoArquivoObn());
    }

    public List<ContratoObn> getContratos() {
        return contratos;
    }

    public void setContratos(List<ContratoObn> contratos) {
        this.contratos = contratos;
    }

    public ContaBancariaEntidade getContaBancariaEntidade() {
        return contaBancariaEntidade;
    }

    public void setContaBancariaEntidade(ContaBancariaEntidade contaBancariaEntidade) {
        this.contaBancariaEntidade = contaBancariaEntidade;
    }

    @Override
    public String toString() {
        return "Banco: <b>" + numeroDoBanco + "</b> - Tipo: <b>" + tipoBancoArquivoObn.getDescricao() + "</b> ";
    }
}
