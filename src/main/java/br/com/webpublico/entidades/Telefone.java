/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.TelefonePortal;
import br.com.webpublico.enums.TipoTelefone;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.TelefoneDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
public class Telefone extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @Tabelavel
    @ManyToOne
    // @Fetch(FetchMode.JOIN)
    private Pessoa pessoa;
    @Tabelavel
    @Column(length = 15)
    @Obrigatorio
    @Etiqueta("Telefone")
    private String telefone;
    @Obrigatorio
    @Etiqueta("Tipo do Telefone")
    @Enumerated(EnumType.STRING)
    private TipoTelefone tipoFone;
    private Boolean principal;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private String pessoaContato;
    @Transient
    @Invisivel
    private Long criadoEm;

    public Telefone() {
        this.dataRegistro = new Date();
        this.criadoEm = System.nanoTime();
        this.principal = Boolean.FALSE;
    }

    public Telefone(Pessoa pessoa, String telefone, Boolean principal, TipoTelefone tipoFone) {
        this.pessoa = pessoa;
        this.telefone = telefone;
        this.principal = principal;
        this.tipoFone = tipoFone;
    }

    public Telefone(Long id, Pessoa pessoa, String telefone, TipoTelefone tipoFone) {
        this.id = id;
        this.pessoa = pessoa;
        this.telefone = telefone;
        this.tipoFone = tipoFone;
        this.dataRegistro = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public TipoTelefone getTipoFone() {
        return tipoFone;
    }

    public void setTipoFone(TipoTelefone tipoFone) {
        this.tipoFone = tipoFone;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Boolean getPrincipal() {
        return principal != null ? principal : Boolean.FALSE;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public String getPessoaContato() {
        return pessoaContato;
    }

    public void setPessoaContato(String pessoaContato) {
        this.pessoaContato = pessoaContato;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return telefone;
    }

    public String getDDD() {
        try {
            String tel = this.telefone.replaceAll("-", "").replaceAll("\\.", "").replaceAll(" ", "");
            String ddd = "";
            if (tel.length() > 10 && tel.contains("(")) {
                ddd = tel.substring(tel.indexOf("(") + 1, tel.indexOf(")"));
                if (ddd.length() > 2) {
                    ddd = ddd.substring(ddd.indexOf("0") + 1, ddd.length());
                }
            } else if (tel.length() > 10 && tel.startsWith("0")) {
                ddd = tel.substring(tel.indexOf("0") + 1, 3);
            } else if (tel.length() == 10) {
                ddd = tel.substring(0, 2);
            }
            return ddd;
        } catch (NullPointerException npe) {
            return "";
        }
    }

    // Método utilizado para recuperar somente o telefone (Sem DDD e sem caracteres especiais) levando em consideração o nono dígito de São Paulo
    // 9988-7766       -> 99887766
    // (44) 9988-7766  -> 99887766
    // 99988-7766      -> 99988-7766
    // (44) 99988-7766 -> 99988-7766
    public String getSomenteTelefone() {
        if (telefone == null || telefone.isEmpty()) {
            return "";
        }

        String tel = telefone.replaceAll("\\D+", ""); // Somente números

        if (tel.length() == 8) { // Quando somente telefone sem DDD (migração)
            return tel;
        }

        if (tel.length() == 9) { // Quando somente telefone sem DDD (migração) (9º dígito - São Paulo)
            return tel;
        }

        if (tel.length() > 2) {
            return tel.substring(2, tel.length());
        }
        return tel;
    }

    public boolean isFax() {
        return this.tipoFone.equals(TipoTelefone.FAX);
    }

    public static List<TelefoneDTO> toTelefones(List<Telefone> telefones) {
        if (telefones != null && !telefones.isEmpty()) {
            List<TelefoneDTO> dtos = Lists.newLinkedList();
            for (Telefone telefone : telefones) {
                TelefoneDTO dto = toTelefone(telefone);
                if (dto != null) {
                    dtos.add(dto);
                }
            }
            return dtos;
        }
        return null;
    }

    public static TelefoneDTO toTelefone(Telefone telefone) {
        if (telefone == null) {
            return null;
        }
        TelefoneDTO dto = new TelefoneDTO();
        dto.setId(telefone.getId());
        dto.setDataRegistro(telefone.getDataRegistro());
        dto.setPessoaContato(telefone.getPessoaContato());
        dto.setPrincipalTelefone(telefone.getPrincipal());
        dto.setTipoFone(telefone.getTipoFone() != null ? br.com.webpublico.pessoa.enumeration.TipoTelefone.valueOf(telefone.getTipoFone().name()) : null);
        dto.setTelefone(telefone.getTelefone());
        return dto;
    }

    public static List<TelefoneDTO> toTelefonesPortalDTO(List<TelefonePortal> telefones) {
        if (telefones != null && !telefones.isEmpty()) {
            List<TelefoneDTO> dtos = Lists.newLinkedList();
            for (TelefonePortal telefone : telefones) {
                TelefoneDTO dto = toTelefonePortalDTO(telefone);
                if (dto != null) {
                    dtos.add(dto);
                }
            }
            return dtos;
        }
        return null;
    }

    public static TelefoneDTO toTelefonePortalDTO(TelefonePortal telefone) {
        if (telefone == null) {
            return null;
        }
        TelefoneDTO dto = new TelefoneDTO();
        dto.setId(telefone.getId());
        dto.setDataRegistro(telefone.getDataRegistro());
        dto.setPessoaContato(telefone.getPessoaContato());
        dto.setPrincipalTelefone(telefone.getPrincipal());
        dto.setTipoFone(telefone.getTipoFone() != null ? br.com.webpublico.pessoa.enumeration.TipoTelefone.valueOf(telefone.getTipoFone().name()) : null);
        dto.setTelefone(telefone.getTelefone());
        return dto;
    }

    public static List<Telefone> toTelefones(PessoaFisica pessoaFisica, List<TelefonePortal> teles) {
        if (teles != null && !teles.isEmpty()) {
            List<Telefone> telefones = Lists.newLinkedList();
            for (TelefonePortal telefone : teles) {
                Telefone dto = toTelefone(pessoaFisica, telefone);
                if (dto != null) {
                    telefones.add(dto);
                }
            }
            return telefones;
        }
        return Lists.newArrayList();
    }

    public static Telefone toTelefone(PessoaFisica pessoa, TelefonePortal telefone) {
        if (telefone == null) {
            return null;
        }
        Telefone tel = new Telefone();
        tel.setPessoa(pessoa);
        tel.setDataRegistro(telefone.getDataRegistro());
        tel.setPessoaContato(telefone.getPessoaContato());
        tel.setPrincipal(telefone.getPrincipal());
        tel.setTipoFone(telefone.getTipoFone());
        tel.setTelefone(telefone.getTelefone());
        return tel;
    }
}
