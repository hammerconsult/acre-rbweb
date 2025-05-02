package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOEndereco;
import br.com.webpublico.negocios.AlteracaoCmcFacade;
import br.com.webpublico.negocios.CalculoAlvaraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "verAlteracoes", pattern = "/alvara/alteracoes-cmc/", viewId = "/faces/tributario/alvara/alteracoes/edita.xhtml")
})
public class AlteracaoCmcControlador implements Serializable {
    private final static Logger logger = LoggerFactory.getLogger(AlteracaoCmcControlador.class);
    private final static int INPUT_SIZE = 70;

    @EJB
    private AlteracaoCmcFacade alteracaoFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;

    private CadastroEconomico cadastroEconomico;
    private List<Map<ChaveAlteracaoCMC, Object>> atributosCnae;
    private List<Map<ChaveAlteracaoCMC, Object>> atributosCnaeAnterior;
    private List<Map<ChaveAlteracaoCMC, Object>> atributosEndereco;
    private List<Map<ChaveAlteracaoCMC, Object>> atributosEnderecoAnterior;

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<Map<ChaveAlteracaoCMC, Object>> getAtributosCnae() {
        return atributosCnae;
    }

    public void setAtributosCnae(List<Map<ChaveAlteracaoCMC, Object>> atributosCnae) {
        this.atributosCnae = atributosCnae;
    }

    public List<Map<ChaveAlteracaoCMC, Object>> getAtributosCnaeAnterior() {
        return atributosCnaeAnterior;
    }

    public void setAtributosCnaeAnterior(List<Map<ChaveAlteracaoCMC, Object>> atributosCnaeAnterior) {
        this.atributosCnaeAnterior = atributosCnaeAnterior;
    }

    public List<Map<ChaveAlteracaoCMC, Object>> getAtributosEndereco() {
        return atributosEndereco;
    }

    public void setAtributosEndereco(List<Map<ChaveAlteracaoCMC, Object>> atributosEndereco) {
        this.atributosEndereco = atributosEndereco;
    }

    public List<Map<ChaveAlteracaoCMC, Object>> getAtributosEnderecoAnterior() {
        return atributosEnderecoAnterior;
    }

    public void setAtributosEnderecoAnterior(List<Map<ChaveAlteracaoCMC, Object>> atributosEnderecoAnterior) {
        this.atributosEnderecoAnterior = atributosEnderecoAnterior;
    }

    @URLAction(mappingId = "verAlteracoes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void verAlteracoes() {
        inicializarAtributos();
    }

    public List<CadastroEconomico> buscarCMCs(String parte) {
        return calculoAlvaraFacade.buscarCmcPorRazaoSocialAndCnpj(parte);
    }

    private void inicializarAtributos() {
        atributosCnae = Lists.newLinkedList();
        atributosCnaeAnterior = Lists.newLinkedList();
        atributosEndereco = Lists.newArrayList();
        atributosEnderecoAnterior = Lists.newArrayList();
    }

    public void buscarAlteracao() {
        if (cadastroEconomico != null) {
            boolean hasAlteracao = alteracaoFacade.hasAlteracaoCadastro(cadastroEconomico.getId());
            if (hasAlteracao) {
                Alvara alvara = alteracaoFacade.recuperarUltimoAlvaraDoCmc(cadastroEconomico.getId());
                List<CNAE> cnaesAlvara = alvara != null ? alteracaoFacade.montarListaCnaes(alvara.getCnaeAlvaras()) : Lists.<CNAE>newArrayList();
                List<CNAE> cnaesCmc = alteracaoFacade.montarListaCnaes(calculoAlvaraFacade.buscarCnaesVigentesCMC(cadastroEconomico.getId()));
                ordenarListaCnaes(cnaesAlvara, cnaesCmc);

                EnderecoAlvara enderecoAlvara = alvara != null ? alteracaoFacade.buscarEnderecoAlvara(alvara) : null;
                EnderecoCadastroEconomico enderecoCmc = alteracaoFacade.buscarEnderecoCmc(cadastroEconomico.getId());

                atribuirAlteracaoCmc(cnaesAlvara, cnaesCmc, enderecoAlvara, enderecoCmc);
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foi encontrado alteração para o CMC: " + cadastroEconomico);
            }
        }
    }

    private void ordenarListaCnaes(List<CNAE> cnaesAlvara, List<CNAE> cnaesCmc) {
        List<CNAE> cnaesRepetidos = Lists.newArrayList(cnaesAlvara);
        cnaesRepetidos.retainAll(cnaesCmc);
        ordenarCnaes(cnaesRepetidos);
        ordenarCnaesPorLista(cnaesCmc, cnaesRepetidos);
        ordenarCnaesPorLista(cnaesAlvara, cnaesRepetidos);
    }

    public void atribuirAlteracaoCmc(List<CNAE> cnaesAlvara, List<CNAE> cnaesCmc, EnderecoAlvara enderecoAlvara, EnderecoCadastroEconomico enderecoCmc) {
        try {
            inicializarAtributos();
            adicionarInformacoesCnae(cnaesAlvara, atributosCnaeAnterior);
            adicionarInformacoesCnae(cnaesCmc, atributosCnae);
            if (enderecoAlvara != null) {
                adicionarInformacoesEndereco(criarVoEndereco(enderecoAlvara), atributosEnderecoAnterior);
            }
            if (enderecoCmc != null) {
                adicionarInformacoesEndereco(criarVoEndereco(enderecoCmc), atributosEndereco);
            }
            igualarListas();
            atualizarComponentesView();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao buscar informações da alteração selecionada. Detalhes: " + e.getMessage());
            logger.error("Erro ao buscar informações da alteração selecionada. ", e);
        }
    }

    private VOEndereco criarVoEndereco(Object endereco) {
        VOEndereco voEnderecoCmc = new VOEndereco();
        voEnderecoCmc.setNumero(endereco instanceof EnderecoAlvara ? ((EnderecoAlvara) endereco).getNumero() : ((EnderecoCadastroEconomico) endereco).getNumero());
        voEnderecoCmc.setComplemento(endereco instanceof EnderecoAlvara ? ((EnderecoAlvara) endereco).getComplemento() : ((EnderecoCadastroEconomico) endereco).getComplemento());

        String logradouro = "";
        String bairro = "";
        if (endereco instanceof EnderecoAlvara) {
            EnderecoAlvara enderecoAlvara = (EnderecoAlvara) Util.clonarObjeto(endereco);
            logradouro = enderecoAlvara != null ? enderecoAlvara.getLogradouro() : "";

            bairro = enderecoAlvara != null ? enderecoAlvara.getBairro() : "";
        }

        voEnderecoCmc.setLogradouro(endereco instanceof EnderecoCadastroEconomico ? ((EnderecoCadastroEconomico) endereco).getLogradouro() : logradouro);
        voEnderecoCmc.setBairro(endereco instanceof EnderecoCadastroEconomico ? ((EnderecoCadastroEconomico) endereco).getBairro() : bairro);
        voEnderecoCmc.setCep(endereco instanceof EnderecoAlvara ? ((EnderecoAlvara) endereco).getCep() : ((EnderecoCadastroEconomico) endereco).getCep());

        return voEnderecoCmc;
    }

    private void atualizarComponentesView() {
        FacesUtil.executaJavaScript("alteracoes.show()");
        FacesUtil.atualizarComponente("FormularioAlteracao");
    }

    private void igualarListas() {
        igualarListas(atributosCnae, atributosCnaeAnterior, true);
        igualarListas(atributosEndereco, atributosEnderecoAnterior, false);
    }

    private void igualarListas(List<Map<ChaveAlteracaoCMC, Object>> alteracoes, List<Map<ChaveAlteracaoCMC, Object>> alteracoesAnteriores, boolean isCnae) {
        if (alteracoes.size() != alteracoesAnteriores.size()) {
            int size = Math.abs(alteracoes.size() - alteracoesAnteriores.size());
            if (alteracoes.size() < alteracoesAnteriores.size()) {
                adicionarAlteracoes(size, alteracoes, isCnae);
            } else {
                adicionarAlteracoes(size, alteracoesAnteriores, isCnae);
            }
        }
    }

    private void adicionarAlteracoes(int size, List<Map<ChaveAlteracaoCMC, Object>> alteracoes, boolean isCnae) {
        for (int i = 0; i < size; i++) {
            Map<ChaveAlteracaoCMC, Object> map = Maps.newLinkedHashMap();
            map.put(new ChaveAlteracaoCMC(isCnae ? "CNAE" : "", (i + alteracoes.size())), null);
            alteracoes.add(map);
        }
    }

    private void adicionarInformacoesCnae(List<CNAE> cnaes, List<Map<ChaveAlteracaoCMC, Object>> atributos) {
        int i = 0;
        for (CNAE cnae : cnaes) {
            Map<ChaveAlteracaoCMC, Object> mapaCnae = Maps.newLinkedHashMap();
            preencherMapa(new ChaveAlteracaoCMC("CNAE", i++), "codigoDescricao", cnae, mapaCnae);
            atributos.add(mapaCnae);
        }
    }

    private void ordenarCnaes(List<CNAE> cnaes) {
        Collections.sort(cnaes, new Comparator<CNAE>() {
            @Override
            public int compare(CNAE o1, CNAE o2) {
                return ComparisonChain.start().compare(o1.getCodigoCnae(), o2.getCodigoCnae()).result();
            }
        });
    }

    private void ordenarCnaesPorLista(List<CNAE> cnaesA, final List<CNAE> cnaesB) {
        Collections.sort(cnaesA, new Comparator<CNAE>() {
            @Override
            public int compare(CNAE o1, CNAE o2) {
                if (cnaesB.contains(o1) && cnaesB.contains(o2))
                    return cnaesB.indexOf(o1) - cnaesB.indexOf(o2);
                else if (cnaesB.contains(o1))
                    return -1;
                else if (cnaesB.contains(o2))
                    return 1;
                return 0;
            }
        });
    }

    private void adicionarInformacoesEndereco(VOEndereco endereco, List<Map<ChaveAlteracaoCMC, Object>> atributos) {
        Map<ChaveAlteracaoCMC, Object> mapaEndereco = Maps.newLinkedHashMap();
        adicionarEnderecos(mapaEndereco, endereco);
        atributos.add(mapaEndereco);
    }

    private void adicionarEnderecos(Map<ChaveAlteracaoCMC, Object> mapaEndereco, VOEndereco endereco) {
        preencherMapa(new ChaveAlteracaoCMC("Logradouro"), "logradouro", endereco, mapaEndereco);
        preencherMapa(new ChaveAlteracaoCMC("Bairro"), "bairro", endereco, mapaEndereco);
        preencherMapa(new ChaveAlteracaoCMC("Número"), "numero", endereco, mapaEndereco);
        preencherMapa(new ChaveAlteracaoCMC("Complemento"), "complemento", endereco, mapaEndereco);
        preencherMapa(new ChaveAlteracaoCMC("CEP"), "cep", endereco, mapaEndereco);
    }

    private void preencherMapa(ChaveAlteracaoCMC chave, String nomeCampo, Object valor, Map<ChaveAlteracaoCMC, Object> mapa) {
        mapa.put(chave, buscarValorAtributo(valor, nomeCampo));
    }

    public boolean hasCnaeAlterado(Integer indexCnae, ChaveAlteracaoCMC atributo) {
        try {
            return hasValorAlterado(atributosCnae.get(indexCnae).get(atributo),
                atributosCnaeAnterior.get(indexCnae).get(atributo));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasEnderecoAlterado(ChaveAlteracaoCMC atributo) {
        try {
            return hasValorAlterado(atributosEndereco.get(0).get(atributo),
                atributosEnderecoAnterior.get(0).get(atributo));
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasValorAlterado(Object valorAtual, Object valorAnterior) {
        if (valorAtual == null && valorAnterior == null) {
            return true;
        } else if (valorAtual == null || valorAnterior == null) {
            return false;
        }
        return valorAtual.equals(valorAnterior);
    }

    public List<ChaveAlteracaoCMC> buscarAtributos(Map<ChaveAlteracaoCMC, Object> map) {
        if (map != null) {
            return Lists.newArrayList(map.keySet());
        }
        return null;
    }

    public boolean isPessoaJuridica() {
        return (cadastroEconomico != null && cadastroEconomico.getPessoa() != null) && cadastroEconomico.getPessoa() instanceof PessoaJuridica;
    }

    public boolean isPessoaFisica() {
        return (cadastroEconomico != null && cadastroEconomico.getPessoa() != null) && cadastroEconomico.getPessoa() instanceof PessoaFisica;
    }

    public String buscarValorObjetoReduzido(Map<ChaveAlteracaoCMC, Object> map, ChaveAlteracaoCMC atributo) {
        if (map != null && atributo != null && !Strings.isNullOrEmpty(atributo.chave)) {
            Object objeto = map.get(atributo);
            if (objeto != null) {
                return objeto.toString().length() >= INPUT_SIZE ? objeto.toString().substring(0, INPUT_SIZE) : objeto.toString();
            }
        }
        return "";
    }

    public Object buscarValorObjetoInteiro(Map<ChaveAlteracaoCMC, Object> map, ChaveAlteracaoCMC atributo) {
        if (map != null && atributo != null && !Strings.isNullOrEmpty(atributo.chave)) {
            Object objeto = map.get(atributo);
            return objeto != null ? objeto.toString() : "";
        }
        return "";
    }

    public boolean isLenghtMaiorPermitido(Map<ChaveAlteracaoCMC, Object> map, ChaveAlteracaoCMC atributo) {
        if (map != null && atributo != null && !Strings.isNullOrEmpty(atributo.chave)) {
            Object objeto = map.get(atributo);
            return objeto != null && objeto.toString().length() > INPUT_SIZE;
        }
        return false;
    }

    private String buscarValorAtributo(Object entidade, String nomeCampo) {
        try {
            String valorAtributo = montarToString(entidade, "get", nomeCampo);
            valorAtributo = !Strings.isNullOrEmpty(valorAtributo) ? valorAtributo : montarToString(entidade, "is", nomeCampo);
            return valorAtributo;
        } catch (Exception ex) {
            return "";
        }
    }

    private String montarToString(Object entidade, String prefixo, String nomeCampo) {
        try {
            Object objeto = entidade.getClass().getMethod(montarNomeMetodo(prefixo, nomeCampo), new Class[]{}).invoke(entidade);
            return formatarObjeto(objeto);
        } catch (Exception e) {
            return "";
        }
    }

    private String montarNomeMetodo(String prefixo, String nomeCampo) {
        return prefixo + StringUtils.capitalize(nomeCampo);
    }

    private String formatarObjeto(Object valorAtributo) {
        try {
            if (valorAtributo != null) {
                if (valorAtributo instanceof Boolean) {
                    return (Boolean) valorAtributo ? "Sim" : "Não";
                }
                return valorAtributo.toString();
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    public static class ChaveAlteracaoCMC {
        private String chave;
        private Integer index;

        public ChaveAlteracaoCMC(String chave, Integer index) {
            this.chave = chave;
            this.index = index;
        }

        public ChaveAlteracaoCMC(String chave) {
            this.chave = chave;
            this.index = 0;
        }

        public String getChave() {
            return chave;
        }

        public void setChave(String chave) {
            this.chave = chave;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChaveAlteracaoCMC that = (ChaveAlteracaoCMC) o;
            return Objects.equal(chave, that.chave);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(chave);
        }
    }
}
