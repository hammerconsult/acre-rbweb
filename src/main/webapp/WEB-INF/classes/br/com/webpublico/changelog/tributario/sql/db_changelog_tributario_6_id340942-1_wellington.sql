declare
    id_divida number;
    descricao_divida_copia varchar(255);
    descricao_divida varchar(255);

begin

    descricao_divida_copia := 'ISSQN MENSAL';
    descricao_divida := 'ISSQN (FORA DO MUNICÍPIO)';

    select hibernate_sequence.nextval into id_divida from dual;

    insert into divida (id, descricao, finalvigencia, iniciovigencia, entidade_id, dataregistro,
                        multaacessoria, divida_id, isdividaativa, nrlivrodividaativa, tipocadastro,
                        configuracaodam_id, codigo, isparcelamento, ordemapresentacao, permissaoemissaodam,
                        permiterevisao)
    select id_divida, descricao_divida, finalvigencia, iniciovigencia, entidade_id, dataregistro,
           multaacessoria, divida_id, isdividaativa, nrlivrodividaativa, tipocadastro,
           configuracaodam_id, (select max(codigo) + 1 from divida where codigo != '999'), isparcelamento,
           ordemapresentacao, 'HABILITA_ATE_VENCIMENTO_PARCELA',
           permiterevisao
        from divida
    where descricao = descricao_divida_copia
      and not exists (select id from divida where descricao = descricao_divida);

    insert into dividaacrescimo (id, divida_id, acrescimo_id, iniciovigencia, finalvigencia)
    select hibernate_sequence.nextval, id_divida, da.acrescimo_id, da.iniciovigencia, da.finalvigencia
        from divida d
       inner join dividaacrescimo da on da.divida_id = d.id
    where descricao = descricao_divida_copia
      and not exists (select 1
                         from divida s_d
                        inner join dividaacrescimo s_da on s_da.divida_id = s_d.id
                      where s_d.descricao = descricao_divida
                        and s_da.acrescimo_id = da.acrescimo_id
                        and s_da.iniciovigencia = da.iniciovigencia);


    insert into opcaopagamentodivida (id, finalvigencia, iniciovigencia, divida_id, opcaopagamento_id)
    select hibernate_sequence.nextval, opd.finalvigencia, opd.iniciovigencia, id_divida, opd.opcaopagamento_id
        from divida d
       inner join opcaopagamentodivida opd on opd.divida_id = d.id
    where descricao = descricao_divida_copia
       and not exists (select 1
                          from divida s_d
                         inner join opcaopagamentodivida s_opd on s_opd.divida_id = s_d.id
                       where s_d.descricao = descricao_divida
                         and s_opd.opcaopagamento_id = opd.opcaopagamento_id
                         and s_opd.iniciovigencia = opd.iniciovigencia);

    merge into valordivida
    using (
        select vd.id, nd.id id_nova_divida
           from calculoiss iss
          inner join valordivida vd on vd.calculo_id = iss.id
          inner join divida d on d.id = vd.divida_id
          inner join divida nd on nd.descricao = descricao_divida
        where cadastroeconomico_id is null) dados
    on ( dados.id = valordivida.id )
    when matched then
    update set divida_id = dados.id_nova_divida;
end;
