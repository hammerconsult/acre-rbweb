package br.com.webpublico.negocios.padrao;



import br.com.webpublico.entidades.SuperEntidade;

import java.util.List;

public interface WebPublicoRepository<T extends SuperEntidade>  {
    /**
     * Grava a entidade no banco para um novo registro.
     *
     * @param entity A entidade preenchida a ser persistida no banco
     */
    void salvarNovo(T entity);

    /**
     * Grava a entidade no banco, seja um novo registro ou um registro alterado.
     * Caso o ID esteja preenchido, os atributos da entidade substituirão os dados do registro gravado no banco com o mesmo ID
     *
     * @param entity A entidade preenchida a ser persistida no banco
     */
    T salvar(T entity);

    /**
     * Remove o registro do banco com o ID do objeto informado. Não faz nada caso não encontre um registro com o ID informado
     * ou o ID seja null
     *
     * @param entity A entidade cujo registro do banco será removido
     */
    void remover(T entity);

    /**
     * Recupera do banco o registro com o ID informado
     *
     * @param id o ID do registro a ser recuperado
     * @return uma instância preenchida conforme os dados do banco de dados, ou null quando não encontrar
     */
    T recuperar(Long id);

    /**
     * Lista todos os registros do banco que satisfaça todos os parâmetros informados
     *
     * @param campo   o nome do atributo do banco a ser filtrado
     * @param valores valores possíveis para o atributo passado
     * @return uma instância de list com todos os registros encontrados, ou uma list vazia quando nenhum registro for encontrado
     */
    List<T> listar(String campo, String... valores);

    /**
     * Conta a quantidade de registros no banco
     *
     * @return o total de registros, sem qualquer filtro, que existem no banco
     */
    long contar();

    /**
     * Recupera o nome da dependência no caso de uma mensagem de erro envolvendo uma associação
     *
     * @param mensagem a Mensagem que contém o erro de associação
     * @return o nome da Entidade associada, ou uma String vazia caso a mensagem seja inválida
     */
    String getNomeDependencia(String mensagem) throws ClassNotFoundException;

    /**
     * Inicializa com a classe correta um objeto encapsulado por um proxy
     *
     * @param proxiedEntity O objeto encapsulado
     * @return Uma instância sem proxy do objeto passado, ou null quando não for possível desencapsular o objeto
     */
    T initializeAndUnproxy(T proxiedEntity);

    /**
     * Recupera o histórico de modificações de um determinado registro gravados na auditoria
     *
     * @param entity O registro cujos dados de auditoria serão recuperados
     * @return uma Lista de Object[] com os dados de auditoria, ou uma lista vazia caso nenhuma informação seja encontrada
     */
    List<Object[]> recuperarHistoricoAlteracoes(T entity);

    /**
     * Cria um registro de auditoria quando o mesmo foi inserido por migração ou alguma outra forma que não grava auditoria
     *
     * @param entity O registro sem dados de auditoria
     */
    void forcarRegistroInicialNaAuditoria(T entity);

}
