insert into historicosituacaodam (id, dam_id, usuariosituacao_id, situacaodam, datasituacao)
select hibernate_sequence.nextval, iddam, idusuario, situacaodam, datasituacao
       from (select aud.id as iddam, (select id from usuariosistema where login = revisao.usuario) as idusuario,
                    aud.situacao as situacaodam, revisao.datahora as datasituacao,
                    row_number() over (partition by aud.situacao, aud.id, to_char(revisao.datahora, 'dd/MM/yyyy HH24:mi')
                        order by revisao.datahora) as linhasituacao
             from dam_aud aud
             inner join dam on dam.id = aud.id
             inner join revisaoauditoria revisao on revisao.id = aud.rev
            order by datasituacao)
where linhasituacao = 1;