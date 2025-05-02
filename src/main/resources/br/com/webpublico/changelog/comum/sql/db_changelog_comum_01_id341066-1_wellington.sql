create or replace procedure proc_delete_pessoa(p_id numeric)
as
begin
delete from representantelegalpessoa where pessoa_id = p_id;
delete from representantelegalpessoa where representante_id = p_id;
delete from tramite where processo_id in (select id from processo where pessoa_id = p_id);
delete from processo where pessoa_id = p_id;
update pessoa set contacorrenteprincipal_id = null where id = p_id;
delete from contacorrentebancpessoa where pessoa_id = p_id;
update pessoa set telefoneprincipal_id = null where id = p_id;
delete from telefone where pessoa_id = p_id;
delete from historicosituacaopessoa where pessoa_id = p_id;
delete from pessoa_enderecocorreio where pessoa_id = p_id;
delete from tituloeleitor where id in (select id from documentopessoal where pessoafisica_id = p_id);
delete from rg where id in (select id from documentopessoal where pessoafisica_id = p_id);
delete from habilitacao where id in (select id from documentopessoal where pessoafisica_id = p_id);
delete from situacaomilitar where id in (select id from documentopessoal where pessoafisica_id = p_id);
delete from documentopessoal where pessoafisica_id = p_id;
delete from impressaodoctooficial where solicitacaodoctooficial_id in (select id from solicitacaodoctooficial where pessoafisica_id = p_id);
delete from impressaodoctooficial where solicitacaodoctooficial_id in (select id from solicitacaodoctooficial where pessoajuridica_id = p_id);
delete from solicitacaodoctooficial where pessoafisica_id = p_id;
delete from solicitacaodoctooficial where pessoajuridica_id = p_id;
delete from documentooficial where pessoa_id = p_id;
update dadospessoaisnfse set pessoa_id = null where pessoa_id = p_id;
delete from pessoacnae where pessoa_id = p_id;
delete from sociedadepessoajuridica where socio_id = p_id;
delete from sociedadepessoajuridica where pessoajuridica_id = p_id;
delete from itemarquivosimplesnacional where pessoa_id = p_id;
delete from mensagemcontribusuario where usuarioweb_id in (select id from usuarioweb where pessoa_id = p_id);
update usuarioweb set usernfsecadastroeconomico_id = null where pessoa_id = p_id;
delete from usernfsecadastroeconomico where usuarionfse_id in (select id from usuarioweb where pessoa_id = p_id);
delete from usuarioweb where pessoa_id = p_id;
delete from telefonedadosretpessoacda where dado_id in (select id from dadosretificacaopessoacda where pessoa_id = p_id);
delete from dadosretificacaopessoacda where pessoa_id = p_id;
delete from classecredorpessoa where pessoa_id = p_id;
delete from pessoafisicacid where pessoafisica_id = p_id;
delete from pessoafisica where id = p_id;
delete from pessoajuridica where id = p_id;
delete from pessoa where id = p_id;

exception
        when others then
            dbms_output.put_line('ID: '||p_id||' - Erro: '||substr(sqlerrm, 1, 100) || ' - ' || sqlcode);
rollback;
commit;
end;
