/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.EnderecoCorreioPortal;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.enums.TipoLogradouroEnderecoCorreio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.pessoa.dto.EnderecoCorreioDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author reidocrime
 */
@Entity

@Audited
public class EnderecoCorreio extends SuperEntidade implements ValidadorEntidade {

    public static final int QUARENTA = 40;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String cep;
    @Obrigatorio
    @Etiqueta("Logradouro")
    private String logradouro;
    @Etiqueta("Complemento")
    private String complemento;
    @Obrigatorio
    @Etiqueta("Bairro")
    private String bairro;
    @Tabelavel
    @Etiqueta("Localidade")
    private String localidade;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Estado (UF)")
    private String uf;
    @Etiqueta("Número")
    @Obrigatorio
    private String numero;
    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;
    private Boolean principal;
    @Transient
    private Long criadoEm;
    private String migracaoChave;
    @Enumerated(EnumType.STRING)
    private TipoLogradouroEnderecoCorreio tipoLogradouro;
    @Transient
    private PessoaFisica pessoaFisica;

    public EnderecoCorreio() {
        principal = Boolean.FALSE;
        criadoEm = System.nanoTime();

    }

    public EnderecoCorreio(String cep, String logradouro, String complemento, String bairro, String localidade, String uf, String numero, TipoEndereco tipoEndereco, Boolean principal) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.complemento = complemento;
        this.bairro = bairro;
        this.localidade = localidade;
        this.uf = uf;
        this.numero = numero;
        this.tipoEndereco = tipoEndereco;
        this.principal = principal;
    }


    public EnderecoCorreio(String cep, String logradouro, String complemento, String bairro, String localidade, String uf, String numero, TipoEndereco tipoEndereco, String migracaoChave) {
        this.cep = cep;
        this.logradouro = logradouro;
        this.complemento = complemento;
        this.bairro = bairro;
        this.localidade = localidade;
        this.uf = uf;
        this.numero = numero;
        this.tipoEndereco = tipoEndereco;
        this.migracaoChave = migracaoChave;
    }

    public static List<EnderecoCorreioDTO> toEnderecoCorreioDTOs(List<EnderecoCorreio> enderecoCorreios) {
        if (enderecoCorreios != null && !enderecoCorreios.isEmpty()) {
            List<EnderecoCorreioDTO> dtos = Lists.newLinkedList();
            for (EnderecoCorreio enderecoCorreio : enderecoCorreios) {
                EnderecoCorreioDTO dto = toEnderecoCorreioDTO(enderecoCorreio);
                if (dto != null) {
                    dtos.add(dto);
                }
            }
            return dtos;
        }
        return null;
    }

    public static List<br.com.webpublico.tributario.dto.EnderecoCorreioDTO> toEnderecoCorreioDTOsTributario(List<EnderecoCorreio> enderecoCorreios) {
        if (enderecoCorreios != null && !enderecoCorreios.isEmpty()) {
            List<br.com.webpublico.tributario.dto.EnderecoCorreioDTO> dtos = Lists.newLinkedList();
            for (EnderecoCorreio enderecoCorreio : enderecoCorreios) {
                br.com.webpublico.tributario.dto.EnderecoCorreioDTO dto = toEnderecoCorreioDTOTributario(enderecoCorreio);
                if (dto != null) {
                    dtos.add(dto);
                }
            }
            return dtos;
        }
        return null;
    }

    public static EnderecoCorreioDTO toEnderecoCorreioDTO(EnderecoCorreio endereco) {
        if (endereco == null) {
            return null;
        }
        EnderecoCorreioDTO dto = new EnderecoCorreioDTO();
        dto.setId(endereco.getId());
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setLocalidade(endereco.getLocalidade());
        dto.setUf(endereco.getUf());
        dto.setNumero(endereco.getNumero());
        dto.setTipoEndereco(endereco.getTipoEndereco() != null ? br.com.webpublico.pessoa.enumeration.TipoEndereco.valueOf(endereco.getTipoEndereco().name()) : null);
        dto.setPrincipal(endereco.getPrincipal());
        dto.setMigracaoChave(endereco.getMigracaoChave());
        return dto;
    }

    public static br.com.webpublico.tributario.dto.EnderecoCorreioDTO toEnderecoCorreioDTOTributario(EnderecoCorreio endereco) {
        if (endereco == null) {
            return null;
        }
        br.com.webpublico.tributario.dto.EnderecoCorreioDTO dto = new br.com.webpublico.tributario.dto.EnderecoCorreioDTO();
        dto.setId(endereco.getId());
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setLocalidade(endereco.getLocalidade());
        dto.setUf(endereco.getUf());
        dto.setNumero(endereco.getNumero());
        dto.setTipoEndereco(endereco.getTipoEndereco() != null ? br.com.webpublico.tributario.enumeration.TipoEndereco.valueOf(endereco.getTipoEndereco().name()): null);
        dto.setPrincipal(endereco.getPrincipal());
        dto.setMigracaoChave(endereco.getMigracaoChave());
        return dto;
    }

    public static List<EnderecoCorreioDTO> toEnderecoCorreioPortalDTOs(List<EnderecoCorreioPortal> enderecoCorreios) {
        if (enderecoCorreios != null && !enderecoCorreios.isEmpty()) {
            List<EnderecoCorreioDTO> dtos = Lists.newLinkedList();
            for (EnderecoCorreioPortal enderecoCorreio : enderecoCorreios) {
                EnderecoCorreioDTO dto = toEnderecoCorreioPortalDTO(enderecoCorreio);
                if (dto != null) {
                    dtos.add(dto);
                }
            }
            return dtos;
        }
        return null;
    }

    public static EnderecoCorreioDTO toEnderecoCorreioPortalDTO(EnderecoCorreioPortal endereco) {
        if (endereco == null) {
            return null;
        }
        EnderecoCorreioDTO dto = new EnderecoCorreioDTO();
        dto.setId(endereco.getId());
        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setLocalidade(endereco.getLocalidade());
        dto.setUf(endereco.getUf());
        dto.setNumero(endereco.getNumero());
        dto.setTipoEndereco(endereco.getTipoEndereco() != null ? br.com.webpublico.pessoa.enumeration.TipoEndereco.valueOf(endereco.getTipoEndereco().name()) : null);
        dto.setPrincipal(endereco.getPrincipal());
        return dto;
    }

    public static List<EnderecoCorreio> toEnderecos(PessoaFisica pessoaFisica, List<EnderecoCorreioPortal> enderecos) {
        if (enderecos != null && !enderecos.isEmpty()) {
            List<EnderecoCorreio> dtos = Lists.newLinkedList();
            for (EnderecoCorreioPortal enderecoCorreio : enderecos) {
                EnderecoCorreio dto = toEndereco(enderecoCorreio);
                if (dto != null) {
                    dtos.add(dto);
                }
            }
            return dtos;
        }
        return Lists.newArrayList();
    }

    public static EnderecoCorreio toEndereco(EnderecoCorreioPortal endereco) {
        if (endereco == null) {
            return null;
        }
        EnderecoCorreio dto = new EnderecoCorreio();

        dto.setCep(endereco.getCep());
        dto.setLogradouro(endereco.getLogradouro());
        dto.setComplemento(endereco.getComplemento());
        dto.setBairro(endereco.getBairro());
        dto.setLocalidade(endereco.getLocalidade());
        dto.setUf(endereco.getUf());
        dto.setNumero(endereco.getNumero());
        dto.setTipoEndereco(endereco.getTipoEndereco());
        dto.setPrincipal(endereco.getPrincipal());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public TipoLogradouroEnderecoCorreio getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(TipoLogradouroEnderecoCorreio tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getLogradouroReduzido() {
        if (logradouro.length() < QUARENTA) {
            return logradouro;
        }
        return logradouro.substring(0, QUARENTA);
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Boolean getPrincipal() {
        if (principal != null) {
            return principal;
        }
        return false;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public String getEnderecoCompleto() {
        StringBuilder sb = new StringBuilder();
        if (tipoLogradouro != null) {
            sb.append(tipoLogradouro.getDescricao().trim()).append(" ");
        }
        if (logradouro != null) {
            sb.append(logradouro.trim()).append(" ");
        }
        if (numero != null) {
            sb.append(numero.trim()).append(", ");
        }
        if (complemento != null) {
            sb.append(complemento.trim()).append(", ");
        }
        if (bairro != null) {
            sb.append(bairro.trim()).append(", ");
        }
        if (cep != null) {
            sb.append(cep.trim()).append(" - ");
        }
        if (localidade != null) {
            sb.append(localidade.trim()).append(" - ");
        }
        if (uf != null) {
            sb.append(uf.trim()).append(". ");
        }
        if (tipoEndereco != null) {
            sb.append(" - ").append(tipoEndereco.getDescricao().trim()).append(" ");
        }
        if (principal != null && principal) {
            sb.append("(Endereço Principal)");
        }
        return sb.toString();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        String endereco = logradouro + ", " + "" + numero + ", " + complemento + ", " + bairro + ", " + localidade + ", " + uf + ", " + cep;
        endereco = endereco.replace(", null", " ");
        endereco = endereco.replace("null", " ");
        //Util.imprime("endereco -> " + endereco);
        return endereco;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public String getCepSemFormatacao() {
        if (cep != null) {
            return StringUtil.removeCaracteresEspeciaisSemEspaco(cep);
        }
        return cep;
    }
}
