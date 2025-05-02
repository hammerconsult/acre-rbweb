package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.webreportdto.dto.rh.ServidorSalarioDTO;
import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;


public class ServidorSalario {

    private VinculoFP vinculoFP;
    private HierarquiaOrganizacional lotacao;
    private RecursoFP recurso;
    private BigDecimal salarioBase;
    private BigDecimal salarioBaseReajustado;

    private String matricula;
    private String contrato;
    private String nome;
    private String unidade;
    private String cargo;

    public ServidorSalario() {
        salarioBase = BigDecimal.ZERO;
        salarioBaseReajustado = BigDecimal.ZERO;
    }

    public static List<ServidorSalarioDTO> servidoresToServidoresDTO(List<ServidorSalario> servidores) {
        List<ServidorSalarioDTO> toReturn = Lists.newArrayList();
        for (ServidorSalario vin : servidores) {
            toReturn.add(servidorToServidorDTO(vin));
        }
        return toReturn;
    }

    private static ServidorSalarioDTO servidorToServidorDTO(ServidorSalario servidor) {
        ServidorSalarioDTO salarioDTO = new ServidorSalarioDTO();
        if(servidor.getVinculoFP() != null){
            salarioDTO.setMatricula(servidor.getMatricula());
            salarioDTO.setContrato(servidor.getContrato());
            salarioDTO.setNome(servidor.getNome());
            salarioDTO.setCargo(servidor.getCargo());
        }
        salarioDTO.setUnidade(servidor.getUnidade());
        salarioDTO.setSalarioBase(servidor.getSalarioBase());
        salarioDTO.setSalarioBaseReajustado(servidor.getSalarioBaseReajustado());
        return salarioDTO;
    }

    public RecursoFP getRecurso() {
        return recurso;
    }

    public void setRecurso(RecursoFP recurso) {
        this.recurso = recurso;
    }

    public BigDecimal getSalarioBase() {
        return salarioBase;
    }

    public void setSalarioBase(BigDecimal salarioBase) {
        this.salarioBase = salarioBase;
    }

    public BigDecimal getSalarioBaseReajustado() {
        return salarioBaseReajustado;
    }

    public void setSalarioBaseReajustado(BigDecimal salarioBaseReajustado) {
        this.salarioBaseReajustado = salarioBaseReajustado;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public HierarquiaOrganizacional getLotacao() {
        return lotacao;
    }

    public void setLotacao(HierarquiaOrganizacional lotacao) {
        this.lotacao = lotacao;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    @Override
    public String toString() {
        return vinculoFP.toString();
    }
}
