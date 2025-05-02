package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Desenvolvimento on 23/08/2016.
 */
@Entity
@Audited
@Table(name = "ITEMARQPERMISSCONCESSO")
public class ItemPermissionarioConcessionario extends SuperEntidade implements Comparable<ItemPermissionarioConcessionario> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;

    @Obrigatorio
    @Column(length = 1)
    private Integer registro;

    @Obrigatorio
    @Column(length = 1)
    private String tipoPessoa;

    @Obrigatorio
    @Column(length = 14)
    private String cpfCnpj;

    @Obrigatorio
    @Column(length = 100)
    private String nomePermissionario;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataPermissao;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Column(length = 100)
    private TipoArquivoPermissionarioConcessionario tipoPermissionario;

    private Boolean valido;

    @ManyToOne
    private ArquivoPermissionarioConcessionario arquivo;

    public ItemPermissionarioConcessionario() {
        super();
    }

    public ItemPermissionarioConcessionario(ArquivoPermissionarioConcessionario arquivo) {
        super();
        this.arquivo = arquivo;
        this.valido = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArquivoPermissionarioConcessionario getArquivo() {
        return arquivo;
    }

    public void setArquivo(ArquivoPermissionarioConcessionario arquivo) {
        this.arquivo = arquivo;
    }

    public Integer getRegistro() {
        return registro;
    }

    public void setRegistro(Integer registro) {
        this.registro = registro;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomePermissionario() {
        return nomePermissionario;
    }

    public void setNomePermissionario(String nomePermissionario) {
        this.nomePermissionario = nomePermissionario;
    }

    public Date getDataPermissao() {
        return dataPermissao;
    }

    public void setDataPermissao(Date dataPermissao) {
        this.dataPermissao = dataPermissao;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public TipoArquivoPermissionarioConcessionario getTipoPermissionario() {
        return tipoPermissionario;
    }

    public void setTipoPermissionario(TipoArquivoPermissionarioConcessionario tipoPermissionario) {
        this.tipoPermissionario = tipoPermissionario;
    }

    public Boolean getValido() {
        return valido != null ? valido : false;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public String getConcatenacaoDosAtributos() {
        String retorno = "";
        try {
            retorno = registro + tipoPessoa;
            if (cpfCnpj != null && !cpfCnpj.isEmpty() && !cpfCnpj.equals("null")) {
                retorno += StringUtil.cortaOuCompletaEsquerda(cpfCnpj, 14, "0") ;
            } else {
                retorno += "00000000000000";
            }
            if (nomePermissionario != null && !nomePermissionario.isEmpty()) {
                retorno += String.format("%-100s", StringUtil.removeAcentuacao(nomePermissionario));
            } else {
                retorno += String.format("%-100s", " ");
            }
            if (dataPermissao != null) {
                retorno += DataUtil.getDataFormatada(dataPermissao).replace("/", "");
            } else {
                retorno += "00000000";
            }
            if (dataVencimento != null) {
                retorno += DataUtil.getDataFormatada(dataVencimento).replace("/", "");
            } else {
                retorno += "00000000";
            }
            if (tipoPermissionario != null) {
                retorno += String.format("%-100s", tipoPermissionario.descricao);
            } else {
                retorno += " ";
            }
            return retorno;
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @Override
    public int compareTo(ItemPermissionarioConcessionario o) {
        int i = getValido().compareTo(o.getValido());
        if (i == 0) {
            i = getCpfCnpj().compareTo(o.getCpfCnpj());
        }
        if (i == 0) {
            i = getNomePermissionario().compareTo(o.getNomePermissionario());
        }
        return i;
    }

    public enum TipoArquivoPermissionarioConcessionario {
        MOTO_TAXISTAS(1, "Moto Taxistas", PermissionarioConcessionario.PERMISSIONARIO),
        TAXISTAS(2, "Taxistas", PermissionarioConcessionario.PERMISSIONARIO),
        FRETEIROS(3, "Freteiros", PermissionarioConcessionario.PERMISSIONARIO),
        CONTRATOS_DE_RENDAS_PATRIMONIAIS(4, "Contratos de Rendas Patrimoniais", PermissionarioConcessionario.CONCESSIONARIO),
        CONTRATOS_DE_CEASA(5, "Contratos de CEASA", PermissionarioConcessionario.CONCESSIONARIO);

        private Integer numero;
        private String descricao;
        private PermissionarioConcessionario permissionarioConcessionario;

        TipoArquivoPermissionarioConcessionario(Integer numero, String descricao, PermissionarioConcessionario permissionarioConcessionario) {
            this.numero = numero;
            this.descricao = descricao;
            this.permissionarioConcessionario = permissionarioConcessionario;
        }

        public Integer getNumero() {
            return numero;
        }

        public String getDescricao() {
            return descricao;
        }

        public PermissionarioConcessionario getPermissionarioConcessionario() {
            return permissionarioConcessionario;
        }

        @Override
        public String toString() {
            return numero + " - " + descricao;
        }

        public enum PermissionarioConcessionario {
            PERMISSIONARIO("Permissionário"),
            CONCESSIONARIO("Concessionário");

            private String descricao;

            PermissionarioConcessionario(String descricao) {
                this.descricao = descricao;
            }

            public String getDescricao() {
                return descricao;
            }
        }
    }

}
