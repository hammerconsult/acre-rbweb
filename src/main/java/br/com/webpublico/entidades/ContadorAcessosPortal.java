package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Contador de Acessos Portal")
public class ContadorAcessosPortal extends SuperEntidade implements Comparable<ContadorAcessosPortal>  {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data do Acesso")
    private Date acessoEm;
    private String usuario;
    @Enumerated(EnumType.STRING)
    private TipoAcesso tipoAcesso;

    public ContadorAcessosPortal(Date acessoEm, String usuario, TipoAcesso tipoAcesso) {
        this.acessoEm = acessoEm;
        this.usuario = usuario;
        this.tipoAcesso = tipoAcesso;
    }

    public ContadorAcessosPortal() {
        this.acessoEm = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAcessoEm() {
        return acessoEm;
    }

    public void setAcessoEm(Date acessoEm) {
        this.acessoEm = acessoEm;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public TipoAcesso getTipoAcesso() {
        return tipoAcesso;
    }

    public void setTipoAcesso(TipoAcesso tipoAcesso) {
        this.tipoAcesso = tipoAcesso;
    }

    @Override
    public int compareTo(ContadorAcessosPortal o) {
        return o.getAcessoEm().compareTo(this.acessoEm);
    }

    public enum TipoAcesso {
        LOGIN ("Login"),
        IMOVEL("Cadastro Imobiliário"),
        EMPRESA("Cadastro Econômico"),
        OTT("Operadora de Tecnologia de Transporte"),
        ALVARA("Alvará"),
        SOLICITACAO_DOCUMENTO("Solicitação de Documemento Oficial"),
        PROTOCOLO("Protocolo"),
        CONSULTA_DEBITO("Consulta de Débitos"),
        ITBI("ITBI"),
        NOTA_FISCAL_AVULSA("Nota Fiscal Avulsa"),
        FALE_CONOSCO("Fale Conosco"),
        AUTENTICIDADE_DOCUMENTOS("Autenticidade de Documentos"),
        CONTRA_CHEQUE("Contracheque"),
        CONSULTA_PROCESSO_REGULARIZACAO_CONSTRUCAO("Consulta de Processo de Regularização de Construção");

        private String descricao;

        TipoAcesso(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
