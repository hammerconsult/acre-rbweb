declare
    is_declared number;
begin
    select count(1) into is_declared
    from user_mviews
    where lower(mview_name) = 'vwnotasfiscais';

    if (is_declared > 0) then
        dbms_output.put_line('drop materialized view');
        execute immediate 'drop materialized view vwnotasfiscais';
    end if;

    dbms_output.put_line('create materialized view');
    execute immediate 'create materialized view vwnotasfiscais (id, declaracaoprestacaoservico_id, numero, emissao, codigoverificacao, prestador_inscricaocadastral,
                                                                prestador_nome, prestador_cpfcnpj, tomador_nome, tomador_cpfcnpj, discriminacaoservico, totalservicos,
                                                                situaca)
    as
    select nf.id,
           nf.declaracaoprestacaoservico_id,
           nf.numero,
           nf.emissao,
           nf.codigoverificacao,
           ce.inscricaocadastral,
           dpp.nomerazaosocial,
           dpp.cpfcnpj,
           dpt.nomerazaosocial,
           dpt.cpfcnpj,
           nf.descriminacaoservico,
           dec.totalservicos,
           dec.situacao
    from notafiscal nf
             inner join cadastroeconomico ce on ce.id = nf.prestador_id
             inner join declaracaoprestacaoservico dec on dec.id = nf.declaracaoprestacaoservico_id
             left join dadospessoaisnfse dpp on dpp.id = dec.dadospessoaisprestador_id
             left join dadospessoaisnfse dpt on dpt.id = dec.dadospessoaistomador_id';
end;
