insert into unidademedida(id, descricao, sigla, mascaraquantidade, mascaravalorunitario, ativo)
values (sequence_generator.nextval, 'UNIDADE', 'UND', 'DUAS_CASAS_DECIMAL', 'TRES_CASAS_DECIMAL', 0);

update itemloteformulariocotacao
set unidademedida_id = (select id
                        from unidademedida
                        where descricao = 'UNIDADE'
                          and mascaravalorunitario = 'TRES_CASAS_DECIMAL' and ativo = 0)
where id = 10718753247;

update itemsolicitacao
set unidademedida_id = (select id
                        from unidademedida
                        where descricao = 'UNIDADE'
                          and mascaravalorunitario = 'TRES_CASAS_DECIMAL' and ativo = 0)
WHERE ID = 10719018190;

update itempregaolancevencedor
set valor = 0.875
where id = 10780970848;
